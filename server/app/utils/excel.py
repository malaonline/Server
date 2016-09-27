import datetime
import logging
import xlwt, xlrd

from django.forms.forms import pretty_name
from django.http import HttpResponse

from .algorithm import str_urlencode

_logger = logging.getLogger('app')
_console = logging.getLogger('console')

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
    response['Content-Disposition'] = 'attachment; filename="{0!s}"'.format(str_urlencode(filename))
    workbook.save(response)
    return response


def read_excel_sheet(file=None, file_content=None, sheet_num=0, sheet_name=None, title_row=0, titles_list=[]):
    '''
    获取Excel表格中指定Sheet的数据

    :param file: Excel文件路径
    :param file_content: Excel文件内容, 适用于内存文件。file or file_content二选一
    :param sheet_num: 表单的索引
    :param sheet_name: 表单的名字(未实现)
    :param title_row: 表头列名所在行的索引(从0开始), 认为下一行为正式数据
    :param titles_list: 用户指定列名, 如果不为空则忽略title_row表头, 直接读取后面的数据
    :return: list(dict) 所有行数据每行为一个dict, 以title_row或titles_list元素为key
    '''
    try:
        wb = xlrd.open_workbook(filename=file, file_contents=file_content)
    except Exception as e:
        raise e

    sheet = wb.sheets()[sheet_num]
    nrows = sheet.nrows  # 行数
    ncols = sheet.ncols  # 列数
    _console.debug('rows: {0!s}, cols: {1!s}'.format(nrows, ncols))
    if titles_list and len(titles_list) > 1:
        titles = titles_list
    else:
        titles = sheet.row_values(title_row)  # 某一行数据
    _console.debug(titles)

    # 遍历读取需要的数据
    list = []
    for row_num in range(title_row + 1, nrows):
        _console.debug('row: {0!s}'.format(row_num))
        row = sheet.row_values(row_num)
        if row:
            obj = {}
            for i in range(len(titles)):
                obj[titles[i]] = row[i]
            list.append(obj)
    return list
