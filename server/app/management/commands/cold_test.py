import os
import unittest

from django.conf import settings
from django.core.management.base import BaseCommand


class Command(BaseCommand):
    help = '''
    冷测试: 没有数据库migration的单元测试, 区别于django test.
    但是仍然可以连接数据库, 最好不要测试带有数据库操作的代码.
    适用于"工具算法测试"或"第三方API测试".
    '''
    test_suite = unittest.TestSuite
    test_runner = unittest.TextTestRunner
    test_loader = unittest.defaultTestLoader
    verbosity = 1
    failfast = False
    default_test_file = 'app.cold_tests'

    def add_arguments(self, parser):
        parser.add_argument(
            'test_labels',
            nargs='*',
            help='测试文件,类或方法。例如{0!s}, 默认{1!s},'.format('app.tests.TestAlgorithm', self.default_test_file),
        )

    def build_suite(self, test_labels=None):
        suite = self.test_suite()
        test_labels = test_labels or [self.default_test_file]

        for label in test_labels:
            label_as_path = os.path.abspath(label)

            # if a module, or "module.ClassName[.method_name]", just run those
            if not os.path.exists(label_as_path):
                tests = self.test_loader.loadTestsFromName(label)
                suite.addTests(tests)

        return suite

    def run_suite(self, suite, **kwargs):
        return self.test_runner(
            verbosity=self.verbosity,
            failfast=self.failfast,
        ).run(suite)

    def init(self, *args, **options):
        unittest.installHandler()
        # do not override user-defined settings
        if not hasattr(settings, "COLD_TESTING"):
            settings.COLD_TESTING = True

    def deinit(self):
        unittest.removeHandler()

    def handle(self, *args, **options):
        self.verbosity = options.get('verbosity', 1)
        test_labels = options.get('test_labels')
        self.init(*args, **options)
        suite = self.build_suite(test_labels)
        result = self.run_suite(suite)
        self.deinit()
