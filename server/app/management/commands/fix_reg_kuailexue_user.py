from django.core.management.base import BaseCommand

from app.models import Order
from app.tasks import registerKuaiLeXueUserByOrder
from app.utils.types import parse_date, parse_date_next


class Command(BaseCommand):
    help = '''
    根据订单注册快乐学师生失败后的修复
    '''

    def add_arguments(self, parser):
        parser.add_argument(
            '--start_date',
            help='开始日期 (YYYY-MM-DD)',
        )
        parser.add_argument(
            '--end_date',
            help='结束日期 (YYYY-MM-DD)',
        )

    def handle(self, *args, **options):
        # print(options)
        start_date_str = options.get('start_date')
        end_date_str = options.get('end_date')
        start_date = start_date_str and parse_date(start_date_str)
        end_date = end_date_str and parse_date_next(end_date_str)
        # print(start_date)
        # print(end_date)

        orders = Order.objects.filter(status=Order.PAID)
        if start_date:
            orders = orders.filter(paid_at__gte=start_date)
        if end_date:
            orders = orders.filter(paid_at__lt=end_date)

        print("count is %d" % orders.count())
        for order in orders:
            print(order.id)
            registerKuaiLeXueUserByOrder.apply_async((order.id,), retry=True, retry_policy={
                'max_retries': 3,
                'interval_start': 10,
                'interval_step': 20,
                'interval_max': 30,
            })
