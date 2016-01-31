import re

def parseInt(nums, default='NaN'):
    """
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
    if isinstance(nums, int):
        return nums
    if isinstance(nums, float):
        return int(nums)
    if not nums:
        return default
    d = re.search(r'^-?\d+', nums)
    return int(d.group(0)) if d else default
