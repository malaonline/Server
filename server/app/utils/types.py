
def parseInt(nums):
    """
    (parseInt('') == 0)
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
    (parseInt('asd') == 0)
    (parseInt('-asd') == 0)
    """
    if not nums:
        return 0
    try:
        return int(nums)
    except ValueError:
        pass
    idx = -1
    minus = False
    for i,c in enumerate(nums):
        if i == 0 and c == '-':
            minus = True
            continue
        if '0' <= c <= '9':
            continue
        idx = i
        break
    if idx == -1:
        return int(nums)
    if idx == 0 or (idx == 1 and minus):
        return 0
    return int(nums[:idx])
