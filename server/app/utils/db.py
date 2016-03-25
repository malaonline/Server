

class Pager:
    def __init__(self, cur_page, total_page, total_count, page_size, show_count=15):
        self.page = cur_page
        self.total_page = total_page
        self.total_count = total_count
        self.page_size = page_size
        self.show_count = show_count

    def dict(self):
        return {
            'page': self.page,
            'total_page': self.total_page,
            'total_count': self.total_count,
            'page_size': self.page_size,
            'show_count': self.show_count,
            'show_start': self.show_start,
            'show_end': self.show_end,
        }

    @property
    def number(self):
        return self.page

    @property
    def page_range(self):
        return self.total_page > 0 and range(1,self.total_page+1) or []

    def has_next(self):
        return self.page < self.total_page

    def has_previous(self):
        return self.page > 1

    def has_other_pages(self):
        return self.has_previous() or self.has_next()

    def next_page_number(self):
        return self.has_next() and (self.page + 1) or None

    def previous_page_number(self):
        return self.has_previous() and (self.page - 1) or None

    @property
    def show_start(self):
        to_s = self.page - 1
        to_end = self.total_page - self.page
        half = self.show_count//2
        start = (to_s < to_end) and (self.page - half) or (to_end < half and (self.total_page - self.show_count) or (self.page - half))
        return max(start, 1)

    @property
    def show_end(self):
        return min(self.total_page, self.show_count + self.show_start)

    @property
    def show_page_range(self):
        return self.page_range[(self.show_start-1):self.show_end]


def paginate(query_set, page=1, page_size=20, show_count=15):
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
    return query_set, Pager(page_to, total_page, total_count, page_size, show_count)

