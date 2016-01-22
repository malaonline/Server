from django.core.management.base import BaseCommand
from django.conf import settings
from django.core.management import call_command


class Command(BaseCommand):
    help = "a commands' collection"

    def handle(self, *args, **options):
        call_command("build_groups_and_permissions")
        call_command("update_regions")
        call_command("add_groups_to_sample_users")
