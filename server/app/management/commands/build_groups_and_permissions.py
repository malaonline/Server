from django.core.management.base import BaseCommand
from django.contrib.auth.models import Group, Permission
from app.models import StaffPermission

group_and_permission = [
    ("超级管理员", [], ['all']),
    ("运营部", ['Can change teacher'],
     [
         # 待上架, 已上架老师, 老师编辑
         'teachers_unpublished',
         'teachers_published',
         'teachers_unpublished_edit',
         'teachers_published_edit',
         'teachers_action',
         # 测评建档, 可查看课表, 不能调课停课, 可申请退款
         'evaluations',
         'evaluations_action',
         'student_schedule_manage',
         'orders_action',
         # 中心设置, 编辑
         'schools',
         'staff_school',
         # 订单查看, 申请退费
         'orders_review',
         'orders_action',
         # 奖学金设置, 领用列表
         'coupon_config',
         'coupons_list',
     ]),
    ("财务主管", [],
     [
         # 教师银行卡查询
         'teachers_bankcard_list',
         # 订单查看, 申请退费, 审核
         'orders_review',
         'orders_refund',
         'orders_action',
         # 老师收入列表, 收入明细, 提现审核
         'teachers_income_list',
         'teachers_income_detail',
         'teachers_withdrawal_list',
         # 奖学金, 领用列表
         'coupons_list',
     ]),
    ("会计出纳", [],
     [
         # 订单查看, 申请退费, 审核
         'orders_review',
         'orders_refund',
         'orders_action',
         # 老师收入列表, 收入明细, 提现审核
         'teachers_income_list',
         'teachers_income_detail',
         'teachers_withdrawal_list',
         # 奖学金, 领用列表
         'coupons_list',
     ]),
    ("中心主任", ['Can change teacher'],
     [
         # 新注册, 待上架, 已上架老师, 老师编辑
         'teachers',
         'teachers_unpublished',
         'teachers_published',
         'teachers_unpublished_edit',
         'teachers_published_edit',
         'teachers_action',
         # 测评建档, 查看课表, 调课停课(操作和记录), 可申请退款
         'evaluations',
         'evaluations_action',
         'student_schedule_manage',
         'student_schedule_action',
         'student_schedule_changelog',
         'orders_action',
         # 课程列表, 投诉, 考勤
         'school_timeslot',
         # 中心设置, 编辑
         'schools',
         'staff_school',
         # 订单查看, 申请退费
         'orders_review',
         'orders_action',
         # 老师收入列表, 收入明细
         'teachers_income_list',
         'teachers_income_detail',
         # 奖学金设置, 领用列表
         'coupon_config',
         'coupons_list',
     ]),
    ("教师委员会主任", ['Can change teacher'],
     [
         # 新注册, 待上架, 已上架老师, 老师编辑
         'teachers',
         'teachers_unpublished',
         'teachers_published',
         'teachers_unpublished_edit',
         'teachers_published_edit',
         'teachers_action',
         # 测评建档, 只能查看课表, 不能调课停课, 也不可申请退款
         'evaluations',
         'evaluations_action',
         'student_schedule_manage',
         # 课程列表
         # todo: 投诉, 考勤 也在这个页面, 因此目前是允许的
         'school_timeslot',
     ]),
    ("教师委员会主任助理", ['Can change teacher'],
     [
         # 新注册, 待上架, 已上架老师, 老师编辑
         'teachers',
         'teachers_unpublished',
         'teachers_published',
         'teachers_unpublished_edit',
         'teachers_published_edit',
         'teachers_action',
         # 测评建档, 只能查看课表, 不能调课停课, 也不可申请退款
         'evaluations',
         'evaluations_action',
         'student_schedule_manage',
     ]),
    ("学习顾问", [],
     [
         # 测评建档, 查看课表, 调课停课(操作和记录), 不可申请退款
         'evaluations',
         'evaluations_action',
         'student_schedule_manage',
         'student_schedule_action',
         'student_schedule_changelog',
         # 课程列表
         # todo: 投诉, 考勤 也在这个页面, 因此目前是允许的
         'school_timeslot',
     ]),
    ("社区店长", [],
     [
         # 课程列表
         # todo: 目前查看所有中心课程
         # todo: 投诉, 考勤 也在这个页面, 因此目前是允许的
         'school_timeslot',
     ]),
    ("老师", [], []),
    ("家长", ['Can change parent', 'Can change profile'], []),
    ("学生", [], []),
    # todo: 师资管理员暂时只作为登录后台
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

            # clear exist allowed_url
            new_group.staffpermission_set.clear()
            for allowed_url_name in allowed_url_name_list:
                new_url_name, created = StaffPermission.objects.get_or_create(allowed_url_name=allowed_url_name)
                new_group.staffpermission_set.add(new_url_name)

            # clear exist permissions
            new_group.permissions.clear()
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
