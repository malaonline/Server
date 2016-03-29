import xlwt
import datetime

from django.forms.forms import pretty_name
from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponse

from .algorithm import str_urlencode

HEADER_STYLE = xlwt.easyxf('font: bold on')
HEADER_STYLE.font.height = 0x0118
DEFAULT_STYLE = xlwt.easyxf()
CELL_STYLE_MAP = (
    (datetime.datetime, xlwt.easyxf(num_format_str='yyyy-mm-dd hh:mm')),
    (datetime.date, xlwt.easyxf(num_format_str='yyyy-mm-dd')),
    (datetime.time, xlwt.easyxf(num_format_str='hh:mm')),
    (bool, xlwt.easyxf(num_format_str='BOOLEAN')),
)


def multi_getattr(obj, attr, default=None):
    attributes = attr.split(".")
    for i in attributes:
        try:
            obj = getattr(obj, i)
        except AttributeError:
            if default:
                return default
            else:
                raise
    if callable(obj):
        obj = obj()
    return obj


def get_column_head(name):
    name = name.rsplit('.', 1)[-1]
    return pretty_name(name)


def get_column_cell(obj, name):
    try:
        attr = multi_getattr(obj, name)
    except ObjectDoesNotExist:
        return None
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


def queryset_to_workbook(queryset, columns, headers=None, header_style=None,
                         default_style=None, cell_style_map=None):
    workbook = xlwt.Workbook()
    report_date = datetime.date.today()
    sheet_name = 'Export {0}'.format(report_date.strftime('%Y-%m-%d'))
    sheet = workbook.add_sheet(sheet_name)

    if not header_style:
        header_style = HEADER_STYLE
    if not default_style:
        default_style = DEFAULT_STYLE
    if not cell_style_map:
        cell_style_map = CELL_STYLE_MAP

    if headers:
        for y, th in enumerate(headers):
            sheet.write(0, y, th, header_style)
    else:
        for y, th in enumerate(columns):
            value = get_column_head(th)
            sheet.write(0, y, value, header_style)

    for x, obj in enumerate(queryset, start=1):
        for y, column in enumerate(columns):
            value = get_column_cell(obj, column)
            style = default_style
            for value_type, cell_style in cell_style_map:
                if isinstance(value, value_type):
                    style = cell_style
            sheet.write(x, y, value, style)

    return workbook


def excel_response(queryset, columns, headers=None, filename='export.xls'):
    workbook = queryset_to_workbook(queryset, columns, headers)
    response = HttpResponse(content_type='application/vnd.ms-excel')
    response['Content-Disposition'] = 'attachment; filename="%s"' % (str_urlencode(filename),)
    workbook.save(response)
    return response
