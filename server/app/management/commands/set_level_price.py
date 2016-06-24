from django.core.management.base import BaseCommand

from app.models import Region, Grade, Subject, Ability, Level, Price


class Command(BaseCommand):
    help = "设置教师级别的价格和佣金比例\n" \
           "例如: \n" \
           "  python manage.py set_level_price 郑州市 --percentages '20,20,20,20,20,20,20,20,20,20' --prices '2000,3000,4000,5000,6000,7000,8000,9000,10000,11000'"

    def create_parser(self, prog_name, subcommand):
        from argparse import RawTextHelpFormatter
        parser = super(Command, self).create_parser(prog_name, subcommand)
        parser.formatter_class = RawTextHelpFormatter
        return parser

    def add_arguments(self, parser):
        parser.add_argument(
            'region_name',
            help='地区名称',
        )
        parser.add_argument(
            '--open',
            type=int,
            default=1,
            help='是否设置开发此地区. 1[默认] or 0',
        )
        parser.add_argument(
            '--prices',
            required=True,
            help='价格数字串, 英文逗号分隔\n单位是分',
        )
        parser.add_argument(
            '--percentages',
            required=True,
            help='佣金比例数字串, 引文逗号分隔\n每个数在0-100之间',
        )

    def handle(self, *args, **options):
        # print(args)
        # print(options)
        region_name = options.get('region_name')
        is_open = options.get('open') and True or False
        prices = options.get('prices')
        percentages = options.get('percentages')

        price_cfg = [int(p) for p in prices.split(',')]
        commission_percentages = [int(p) for p in percentages.split(',')]
        if len(price_cfg) != len(commission_percentages):
            return ("价格和佣金比例个数不同")

        levels = list(Level.objects.all())
        if len(levels) != len(price_cfg):
            return ("价格和佣金比例个数和现有级别数不同")

        region = Region.objects.get(name=region_name)
        if is_open != region.opened:
            region.opened = is_open
            region.save()
        abilities = Ability.objects.all()
        for level in levels:
            # print(" {level_name}".format(level_name=level.name))
            i = level.id - 1
            for ability in abilities:
                c = price_cfg[i]
                price, _ = Price.objects.get_or_create(region=region, level=level, ability=ability,
                                                       defaults={'price': c})
                price.price = c
                price.commission_percentage = commission_percentages[i]
                price.save()

        print('设置完毕')
        return 0
