import os
import unittest

from django.conf import settings
from django.core.management.base import BaseCommand


class Command(BaseCommand):
    help = '''
    冷测试: 不连接数据库的单元测试, 区别于django test
    类似于django test时, 将settings.DATABASES设置为空{}, 适用于"算法测试"或"外联API测试"
    '''
    test_suite = unittest.TestSuite
    test_runner = unittest.TextTestRunner
    test_loader = unittest.defaultTestLoader
    verbosity = 2
    failfast = False
    default_test_file = 'app.cold_tests'

    def add_arguments(self, parser):
        parser.add_argument(
            'test_labels',
            nargs='*',
            help='测试文件,类或方法。例如%s, 默认%s,' %('app.tests.TestAlgorithm', self.default_test_file),
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

    def init(self):
        unittest.installHandler()
        # do not override user-defined settings
        if not hasattr(settings, "COLD_TESTING"):
            settings.COLD_TESTING = True

    def deinit(self):
        unittest.removeHandler()

    def handle(self, *args, **options):
        test_labels = options.get('test_labels')
        self.init()
        suite = self.build_suite(test_labels)
        result = self.run_suite(suite)
        self.deinit()
