from django.core.management.base import BaseCommand
from django.contrib.auth.models import Group, Permission
from django.core.management import call_command
from django.conf import settings
from django.contrib.auth.models import User


class Command(BaseCommand):
    help = "Add groups to sample users, "

    def handle(self, *args, **options):
        write = print
        build_group_command = "build_groups_and_permissions"
        write("run {command_name}".format(command_name=build_group_command))
        call_command(build_group_command,)

        write("add parent group to parent users")
        parent_name_formula = settings.SAMPLE_PARENT_USER_FORMULA
        group_name = "家长"
        parent_group = Group.objects.get(name=group_name)
        for i in range(settings.SAMPLE_DATA_LENGTH):
            username = parent_name_formula.format(id=i)
            try:
                parent_user = User.objects.get(username=username)
            except User.DoesNotExist:
                write("{user} not exist".format(parent_name_formula))
                continue
            parent_user.groups.add(parent_group)
            parent_user.save()
            print("{user} added {group}".format(user=parent_user, group=parent_group))

