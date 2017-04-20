from django.core.management.base import BaseCommand

from app.models import School


class Command(BaseCommand):
    help = '''
        网站升级命令
        每次网站升级, 一些不在migrate里的处理, 写在该脚本中处理
        如果不需要处理, handle方法中就不写任何操作
        '''

    def create_parser(self, prog_name, subcommand):
        from argparse import RawTextHelpFormatter
        parser = super(Command, self).create_parser(prog_name, subcommand)
        parser.formatter_class = RawTextHelpFormatter
        return parser

    def update_school_stairs_price(self):
        '''
        校区阶梯定价升级处理
        '''
        opened_schools = School.objects.filter(opened=True)
        for school in opened_schools:
            school.init_prices()

    def handle(self, *args, **options):
        self.update_school_stairs_price()
        print('升级完毕')
        return 0
