

def paginate(query_set, page=1, page_size=20):
    """
    数据库分页
    :param query_set: 数据库QuerySet
    :param page: 目的页码
    :param page_size: 每页的数据量
    :return:
    """
    total_count = query_set.count()
    total_page = (total_count + page_size -1) // page_size
    if not isinstance(page, int):
        if page and isinstance(page, str) and page.isdigit():
            page_to = int(page)
        else:
            page_to = 1
    else:
        page_to = page
    if page_to > total_page:
        page_to = total_page
    if page_to < 1:
        page_to = 1
    query_set = query_set[(page_to-1)*page_size:page_to*page_size]
    return query_set, {'page': page_to, 'total_page': total_page, 'total_count': total_count}

