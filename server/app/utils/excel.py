import xlwt
import datetime

from django.forms.forms import pretty_name
from django.http import HttpResponse

from .algorithm import str_urlencode

HEADER_STYLE = xlwt.easyxf('font: bold on; font: height 0x0118')
DEFAULT_STYLE = xlwt.easyxf('font: height 0x00e0')
CELL_STYLE_MAP = (
    (float, xlwt.easyxf(num_format_str='0.00')),
    (datetime.datetime, xlwt.easyxf(num_format_str='yyyy-mm-dd hh:mm')),  # NOTE: datetime is also date
    (datetime.date, xlwt.easyxf(num_format_str='yyyy-mm-dd')),
    (datetime.time, xlwt.easyxf(num_format_str='hh:mm')),
    (bool, xlwt.easyxf(num_format_str='BOOLEAN')),
)


def multi_getattr(obj, attr, default=None):
    attributes = attr.split(".")
    for i in attributes:
        try:
            obj = getattr(obj, i)
            if callable(obj):
                obj = obj()
        except AttributeError:
            if default:
                return default
            else:
                raise
    return obj


def get_column_head(name):
    if callable(name):
        return 'method()'
    else:
        name = name.rsplit('.', 1)[-1]
        return pretty_name(name)


def get_column_cell(obj, name, default=None):
    try:
        attr = multi_getattr(obj, name)
    except:
        return default
    if hasattr(attr, '_meta'):
        # A Django Model (related object)
        return str(attr)
    elif hasattr(attr, 'all'):
        # A Django queryset (ManyRelatedManager)
        return ', '.join(str(x) for x in attr.all())
    elif isinstance(attr, list) or isinstance(attr, tuple) or isinstance(attr, set) or isinstance(attr, frozenset):
        # A list or set
        return ', '.join(str(e) for e in attr)
    return attr


def get_style_by_value(value, cell_style_map=CELL_STYLE_MAP, default_style=DEFAULT_STYLE):
    style = default_style
    for value_type, cell_style in cell_style_map:
        if isinstance(value, value_type):
            style = cell_style
            break
    return style


def queryset_to_workbook(queryset, columns, headers=None, header_style=HEADER_STYLE,
                         default_style=DEFAULT_STYLE, cell_style_map=CELL_STYLE_MAP):
    '''
    把django QuerySet里的数据写到excel的Workbook
    :param queryset: django QuerySet对象
    :param columns: 如 ('姓名', '手机号',        '账户余额')
    :param headers: 如 ('name', 'profile.phone', lambda x: (x.balance/100),)
    :param header_style: 标题行样式
    :param default_style: 默认样式
    :param cell_style_map: (数据类型,样式)映射集
    :return: xlwt.Workbook
    '''
    workbook = xlwt.Workbook()
    report_date = datetime.date.today()
    sheet_name = 'Export {0}'.format(report_date.strftime('%Y-%m-%d'))
    sheet = workbook.add_sheet(sheet_name)

    if headers:
        for y, th in enumerate(headers):
            sheet.write(0, y, th, header_style)
    else:
        for y, th in enumerate(columns):
            value = get_column_head(th)
            sheet.write(0, y, value, header_style)

    for x, obj in enumerate(queryset, start=1):
        for y, column in enumerate(columns):
            if callable(column):
                value = column(obj)
            else:
                value = get_column_cell(obj, column)
            style = get_style_by_value(value, cell_style_map, default_style)
            sheet.write(x, y, value, style)

    return workbook


def excel_response(queryset, columns, headers=None, filename='export.xls'):
    workbook = queryset_to_workbook(queryset, columns, headers)
    return wb_excel_response(workbook, filename)


def wb_excel_response(workbook, filename='export.xls'):
    response = HttpResponse(content_type='application/vnd.ms-excel')
    response['Content-Disposition'] = 'attachment; filename="%s"' % (str_urlencode(filename),)
    workbook.save(response)
    return response
