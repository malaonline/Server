import re

def parseInt(nums, default='NaN'):
    """
    (parseInt(None) == 'NaN')
    (parseInt('') == 'NaN')
    (parseInt(123) == 123)
    (parseInt(-123) == -123)
    (parseInt('123') == 123)
    (parseInt('-123') == -123)
    (parseInt('123asd') == 123)
    (parseInt('-123asd') == -123)
    (parseInt(234.234) == 234)
    (parseInt(-234.234) == -234)
    (parseInt('234.234') == 234)
    (parseInt('-234.234') == -234)
    (parseInt('asd') == 'NaN')
    (parseInt('-asd') == 'NaN')
    """
    if nums is None or nums is '':
        return default
    if isinstance(nums, int):
        return nums
    if isinstance(nums, float):
        return int(nums)
    d = re.search(r'^-?\d+', nums)
    return int(d.group(0)) if d else default
