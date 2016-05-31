from django.core.management.base import BaseCommand
from django.contrib.auth.models import Group, Permission
from app.models import StaffPermission

group_and_permission = [
    ("超级管理员", [], [
        'teachers_income_list',
    ]),
    ("地区管理员", [], []),
    ("社区店管理员", [], []),
    ("出纳", [], []),
    ("客服", [], []),
    ("老师", [], []),
    ("家长", ['Can change parent', 'Can change profile'], []),
    ("学生", [], []),
    ("师资管理员", ['Can change teacher'], [])
]


class Command(BaseCommand):
    help = "Build Groups and Permissions"

    def handle(self, *args, **options):
        global group_and_permission
        write = print

        # create group and permission
        for group_name, permission_list, allowed_url_name_list in group_and_permission:
            new_group, group_create = Group.objects.get_or_create(name=group_name)

            for allowed_url_name in allowed_url_name_list:
                new_url_name, created = StaffPermission.objects.get_or_create(allowed_url_name=allowed_url_name)
                new_group.staffpermission_set.add(new_url_name)

            for permission_name in permission_list:
                permission = Permission.objects.get(name=permission_name)
                new_group.permissions.add(permission)

            new_group.save()

        # print exist group
        '''
        for exist_group in Group.objects.all():
            write("{group} now has below permissions:".format(group=exist_group))
            for permission in exist_group.permissions.all():
                write(" {permission}".format(permission=permission))
            write("")
        '''
