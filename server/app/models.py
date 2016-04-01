import logging
import datetime

import posix_ipc
from segmenttree import SegmentTree

import random

from django.contrib.auth.models import User
from django.db import models
from django.db import IntegrityError, transaction
from django.contrib.auth.hashers import make_password
from django.contrib.auth.models import Group
from django.contrib.auth import authenticate
from django.apps import apps
from django.utils import timezone
from django.utils.timezone import make_aware, localtime
from django.conf import settings
from django.db.models import Q

from app.exception import TimeSlotConflict, OrderStatusIncorrect, RefundError
from app.utils.algorithm import orderid, Tree, Node
from app.utils import random_string, classproperty
from app.utils.smsUtil import isTestPhone, sendCheckcode, SendSMSError,\
        tpl_send_sms, TPL_STU_PAY_SUCCESS, TPL_TEACHER_COURSE_PAID

logger = logging.getLogger('app')


class BaseModel(models.Model):
    class Meta:
        abstract = True

    def local_time_str(self, date, time_formula="%Y-%m-%d %H:%M"):
        # 格式化一个本地时间
        if date:
            return localtime(date).strftime(time_formula)
        return "空白时间"

    def money_str(self, money):
        return "¥{0:.2f}".format(money/100)


class Region(BaseModel):
    """
    Province, City & District
    """
    name = models.CharField(max_length=50)
    superset = models.ForeignKey('Region', blank=True, null=True, default=None,
                                 on_delete=models.SET_NULL)
    admin_level = models.PositiveIntegerField()
    leaf = models.BooleanField()
    weekly_time_slots = models.ManyToManyField('WeeklyTimeSlot')
    opened = models.BooleanField(default=False)

    def __str__(self):
        return '%s (%d)' % (self.name, self.admin_level)

    def full_name(self, sep='-'):
        full_name = self.name
        upper = self.superset
        while upper:
            full_name = upper.name + sep + full_name
            upper = upper.superset
        return full_name

    def make_dict(self):
        _dict = {}
        _region = self
        while _region:
            if _region.admin_level == 1:
                _dict['province'] = _region
            elif _region.admin_level == 2:
                _dict['city'] = _region
            elif _region.admin_level == 3:
                _dict['district'] = _region
            _region = _region.superset
        return _dict


class Memberservice(BaseModel):
    name = models.CharField(max_length=30, unique=True)
    detail = models.CharField(max_length=1000)

    def __str__(self):
        return '%s' % self.name


class School(BaseModel):
    name = models.CharField(max_length=100)
    address = models.CharField(max_length=200)
    thumbnail = models.ImageField(upload_to='schools', null=True, blank=True)
    region = models.ForeignKey(Region, limit_choices_to={'opened': True})
    center = models.BooleanField()
    longitude = models.FloatField()
    latitude = models.FloatField()
    opened = models.BooleanField(default=False)
    class_seat = models.IntegerField(default=0, null=True)
    study_seat = models.IntegerField(default=0, null=True)
    phone = models.CharField(max_length=20, default=None, null=True)
    member_services = models.ManyToManyField(Memberservice)
    desc_title = models.CharField(max_length=200, null=True)
    desc_content = models.CharField(max_length=500, null=True)

    def __str__(self):
        return '%s%s %s' % (self.region, self.name, 'C' if self.center else '')

    @property
    def get_thumbnail(self):
        if self.schoolphoto_set.first():
            return self.schoolphoto_set.first().img.url
        else:
            return ""

    def get_photo_url_list(self):
        if self.schoolphoto_set.first():
            return list(map(lambda x: x.img_url(), self.schoolphoto_set.all()))
        else:
            return ""

    def get_member_service_list(self):
        return self.member_services.all().values_list('id', flat=True) or []


class SchoolPhoto(BaseModel):
    school = models.ForeignKey(School)
    img = models.ImageField(null=True, blank=True, upload_to='schools')

    def __str__(self):
        return self.school

    def img_url(self):
        return self.img and self.img.url or ''


class Subject(BaseModel):
    ENGLISH = None
    name = models.CharField(max_length=10, unique=True)

    def __str__(self):
        return self.name

    @classmethod
    def get_english(cls):
        if not cls.ENGLISH:
            cls.ENGLISH = Subject.objects.get(name='英语')
        return cls.ENGLISH


class Tag(BaseModel):
    name = models.CharField(max_length=20, unique=True)

    def __str__(self):
        return self.name


class Grade(BaseModel):
    name = models.CharField(max_length=10, unique=True)
    superset = models.ForeignKey('Grade', blank=True, null=True, default=None,
                                 on_delete=models.SET_NULL,
                                 related_name='subset')
    leaf = models.BooleanField()

    def __str__(self):
        return self.name

    @property
    def subjects(self):
        Ability = apps.get_model('app', 'Ability')
        ans = Ability.objects.filter(grade=self)
        for one in ans:
            yield one.subject

    @staticmethod
    def get_all_grades():
        """
        获得所有的grade名称
        """
        return [[item.name for item in item.subset.all()]
                for item in Grade.objects.filter(superset=None)]


class Ability(BaseModel):
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)

    class Meta:
        unique_together = ('grade', 'subject')

    def __str__(self):
        return '%s, %s' % (self.grade, self.subject)


class Level(BaseModel):
    # 老师等级信息
    name = models.CharField(max_length=20, unique=True)
    # 级别,最小,最大上限设置好,低级别对应小数字,高级别对应大数字
    level_order = models.IntegerField()

    def __str__(self):
        return self.name


class LevelRecord(BaseModel):
    # 老师等级调整记录,时间排序
    to_level = models.ForeignKey(Level, null=True, on_delete=models.SET_NULL)
    # 最后一条记录,即当前老师的级别评定时间
    create_at = models.DateTimeField(auto_now=True)
    teacher = models.ForeignKey(
            "Teacher", null=True, on_delete=models.SET_NULL)

    DEGRADE = 'd'
    UPGRADE = 'u'
    BECOME = 'b'
    OPERATION_CHOICE = (
        (DEGRADE, "降级"),
        (UPGRADE, "升级"),
        (BECOME, "成为")
    )
    # 升级,降级,设成choice
    operation = models.CharField(
            max_length=1, choices=OPERATION_CHOICE, default=UPGRADE)

    def __str__(self):
        operation = {}
        for key, val in self.OPERATION_CHOICE:
            operation[key] = val
        try:
            msg = "{time} {name}{operation}{level}".format(
                time=self.create_at.strftime("%Y-%m-%d"),
                name=self.teacher.name,
                operation=operation.get(self.operation, "未知"),
                level=self.to_level.name
            )
        except Exception as e:
            msg = "异常记录, <{pk}>{create_at}, {err}".format(
                    pk=self.pk, err=e,
                    create_at=self.local_time_str(self.create_at))
        return msg


class Price(BaseModel):
    region = models.ForeignKey(Region, limit_choices_to={'opened': True})
    ability = models.ForeignKey(Ability, default=1)
    level = models.ForeignKey(Level)
    # 单位也是分
    price = models.PositiveIntegerField()  # Lesson price
    # 薪资,单位是分
    salary = models.PositiveIntegerField(default=0)
    # 佣金比例,10表示10%
    commission_percentage = models.PositiveIntegerField(default=0)

    class Meta:
        unique_together = ('region', 'ability', 'level')

    def __str__(self):
        return '%s,%s,%s => %d' % (
                self.region, self.ability, self.level, self.price)

    @property
    def grade(self):
        return self.ability.grade

    @property
    def subject(self):
        return self.ability.subject


class Profile(BaseModel):
    """
    For extending the system class: User
    """
    MALE = 'm'
    FEMALE = 'f'
    UNKNOWN = 'u'
    GENDER_CHOICES = (
        (FEMALE, '女'),
        (MALE, '男'),
        (UNKNOWN, '未知'),
    )

    user = models.OneToOneField(User)
    phone = models.CharField(max_length=20, unique=True)
    # deprecated: use django group instead
    # role = models.ForeignKey(Role, null=True, blank=True,
    #                          on_delete=models.SET_NULL)
    gender = models.CharField(max_length=1,
                              choices=GENDER_CHOICES,
                              default=UNKNOWN,
                              )
    avatar = models.ImageField(null=True, blank=True, upload_to='avatars')
    birthday = models.DateField(blank=True, null=True, default=None)
    wx_openid = models.CharField(
            max_length=100, default=None, null=True, blank=True)

    def __str__(self):
        teacher_name = ""
        student_name = ""
        if hasattr(self.user, "teacher"):
            teacher_name = '%s(老师)' % self.user.teacher.name
        if hasattr(self.user, "parent"):
            student_name = '%s(家长)' % self.user.parent.student_name

        return 'phone:{phone} {user_id} {teacher_name}{student_name}'.format(
                phone=self.phone, user_id=self.user.pk,
                teacher_name=teacher_name, student_name=student_name)

    # 带有掩码的手机号码
    def mask_phone(self):
        return "{before}****{after}".format(
                before=self.phone[:3], after=self.phone[-4:])

    def avatar_url(self):
        return self.avatar and self.avatar.url or ''


class Teacher(BaseModel):
    DEGREE_CHOICES = (
        ('h', '高中'),
        ('s', '专科'),
        ('b', '本科'),
        ('p', '研究生'),
    )
    TO_CHOOSE = 1
    NOT_CHOSEN = 2
    TO_INTERVIEW = 3
    INTERVIEW_OK = 4
    INTERVIEW_FAIL = 5
    STATUS_CHOICES = (
        (TO_CHOOSE, '待处理'),
        (NOT_CHOSEN, '初选淘汰'),
        (TO_INTERVIEW, '邀约面试'),
        (INTERVIEW_OK, '面试通过'),
        (INTERVIEW_FAIL, '面试失败'),
    )
    user = models.OneToOneField(User)
    name = models.CharField(max_length=200)
    degree = models.CharField(max_length=2,
                              choices=DEGREE_CHOICES,
                              )
    published = models.BooleanField(default=False)
    fulltime = models.BooleanField(default=True)
    # 教龄
    teaching_age = models.PositiveIntegerField(default=0)
    level = models.ForeignKey(Level, null=True, blank=True,
                              on_delete=models.SET_NULL)
    experience = models.PositiveSmallIntegerField(null=True, blank=True)
    profession = models.PositiveSmallIntegerField(null=True, blank=True)
    interaction = models.PositiveSmallIntegerField(null=True, blank=True)
    video = models.FileField(null=True, blank=True, upload_to='video')
    audio = models.FileField(null=True, blank=True, upload_to='audio')

    # 风格标签
    tags = models.ManyToManyField(Tag)
    schools = models.ManyToManyField(School)
    weekly_time_slots = models.ManyToManyField('WeeklyTimeSlot')
    abilities = models.ManyToManyField('Ability')

    region = models.ForeignKey(Region, null=True, blank=True,
                               limit_choices_to={'opened': True})
    status = models.IntegerField(default=1, choices=STATUS_CHOICES)
    # 老师审核状态的那个按钮
    status_confirm = models.BooleanField(default=False)
    # 毕业院校
    graduate_school = models.CharField(max_length=50, blank=True, null=True)
    # 自我介绍
    introduce = models.CharField(max_length=200, blank=True, null=True)

    recommended_on_wechat = models.BooleanField(default=False)

    def __str__(self):
        return '%s %s %s' % (
                '⬆' if not self.published else '  ', self.name, self.phone())

    def phone(self):
        if not hasattr(self.user, 'profile'):
            return None
        return self.user.profile.phone or None

    def avatar(self):
        if not hasattr(self.user, 'profile'):
            return None
        return self.user.profile.avatar or None

    def gender(self):
        if not hasattr(self.user, 'profile'):
            return None
        return self.user.profile.gender

    def subject(self):
        abilities = self.abilities.all()
        if not abilities:
            return None
        return abilities[0].subject

    def grades(self):
        abilities = self.abilities.all()
        return [ability.grade for ability in abilities]

    def grades_shortname(self):
        grades = self.grades()
        grades = list(set(x.superset if x.superset else x for x in grades))
        sort_dict = {'小学': 1, '初中': 2, '高中': 3}
        grades = sorted(grades, key=lambda x: sort_dict.get(x.name, 4))
        if len(grades) == 0:
            return ''
        if len(grades) == 1:
            return grades[0].name
        else:
            return ''.join(x.name[0] for x in grades)

    def prices(self):
        abilities = self.abilities.all()
        return Price.objects.filter(
                level=self.level, region=self.region,
                ability__grade__leaf=True,
                ability__in=abilities).order_by('pk')

    def min_price(self):
        prices = list(self.prices())
        if not prices:
            return None
        return min(x.price for x in prices)

    def max_price(self):
        prices = list(self.prices())
        if not prices:
            return None
        return max(x.price for x in prices)

    def is_english_teacher(self):
        subject = self.subject()
        ENGLISH = Subject.get_english()
        return subject and (subject.id == ENGLISH.id)

    def audio_url(self):
        return self.audio and self.audio.url or ''

    def set_level(self, new_level: Level):
        # 设置老师等级
        if self.level_id:
            # 如果反复重复设置
            self.level_id == new_level.id
            return
        new_level_record = LevelRecord(to_level=new_level, teacher=self)
        if self.level_id:
            if self.level.level_order < new_level.level_order:
                # 升级
                new_level_record.operation = LevelRecord.UPGRADE
            else:
                # 降级
                new_level_record.operation = LevelRecord.DEGRADE
        else:
            # 初始化->成为
            new_level_record.operation = LevelRecord.BECOME
        new_level_record.save()
        self.level = new_level
        self.save()

    def set_status(self, operator: User, new_status: int):
        # operator: 操作人
        # new_status: 新的状态
        AuditRecord.new_audit_record(
                operator, teacher=self, old_statue=self.status,
                new_statue=new_status)
        self.status = new_status
        self.save()

    def init_level(self):
        pass

    def video_url(self):
        return self.video and self.video.url or ''

    def cert_verified_count(self):
        Certificate = apps.get_model('app', 'Certificate')
        if self.is_english_teacher():
            cert_types = [Certificate.ID_HELD, Certificate.ACADEMIC,
                          Certificate.TEACHING, Certificate.ENGLISH,
                          Certificate.OTHER]
        else:
            cert_types = [Certificate.ID_HELD, Certificate.ACADEMIC,
                          Certificate.TEACHING, Certificate.OTHER]
        return Certificate.objects.filter(
                teacher=self, verified=True, type__in=cert_types).distinct(
                        'type').count()

    # 获得当前审核进度
    def get_progress(self):
        if self.status in [self.TO_CHOOSE, self.NOT_CHOSEN]:
            return 1
        if self.status in [self.TO_INTERVIEW, self.INTERVIEW_FAIL]:
            return 2
        if self.status in [self.INTERVIEW_OK]:
            return 3

    # 建立审核信息
    def build_progress_info(self):
        tree = Tree()
        tree.root = Node(1)
        tree.insert_val(1, 3, 2)
        tree.insert_val(3, 4)
        tree.insert_val(4, 5)
        tree.insert_val(5, 7, 6)
        tree.insert_val(7, 8)
        tree.insert_val(8, 9)
        status_2_node = {
            self.TO_CHOOSE: 1,
            self.NOT_CHOSEN: 2,
            self.TO_INTERVIEW: 5,
            self.INTERVIEW_FAIL: 6,
            self.INTERVIEW_OK: 9
        }
        # return tree.get_path(status_2_node.get(self.INTERVIEW_OK, 1))
        # return range(1,10)
        return tree.get_path(status_2_node.get(self.status, 1))

    def safe_get_account(self):
        # 获得账户,如果没有账户就创建一个
        try:
            account = self.user.account
        except AttributeError:
            # 新建一个账户
            account = Account(user=self.user)
            account.save()
        return account

    def longterm_available_dict(self, school, parent):
        TimeSlot = apps.get_model('app', 'TimeSlot')

        renew_time = TimeSlot.RENEW_TIME
        traffic_time = int(TimeSlot.TRAFFIC_TIME.total_seconds()) // 60

        teacher = self
        region = school.region
        weekly_time_slots = region.weekly_time_slots.all()

        date = timezone.now() - renew_time
        occupied = TimeSlot.objects.filter(
                order__teacher=teacher, start__gte=date, deleted=False)
        occupied = occupied.filter(~Q(order__parent=parent))
        occupied = [
                (x if x.transferred_from is None else x.transferred_from)
                for x in occupied]

        segtree = SegmentTree(0, 7 * 24 * 60 - 1)
        for occ in occupied:
            cur_school = occ.order.school
            occ.start = timezone.localtime(occ.start)
            occ.end = timezone.localtime(occ.end)
            start = (occ.start.weekday() * 24 * 60 + occ.start.hour * 60 +
                     occ.start.minute)

            end = (occ.end.weekday() * 24 * 60 + occ.end.hour * 60 +
                   occ.end.minute - 1)

            if cur_school.id != school.id:
                start, end = start - traffic_time, end + traffic_time
            segtree.add(start, end)

        def w2m(w, t):
            return (w - 1) * 24 * 60 + t.hour * 60 + t.minute

        data = {(s.weekday, s.start, s.end): (segtree.query_len(
            w2m(s.weekday, s.start), w2m(s.weekday, s.end) - 1) == 0)
            for s in weekly_time_slots
            }
        return data

    def is_longterm_available(self, periods, school, parent):
        '''
        periods: [(weekday, start, end), ...]
        weekday: int (1-7)
        start: time
        end: time
        '''
        la_dict = self.longterm_available_dict(school, parent)
        for period in periods:
            if not la_dict[period]:
                return False
        return True

    def shortterm_available_dict(self, school):
        TimeSlot = apps.get_model('app', 'TimeSlot')

        renew_time = TimeSlot.RENEW_TIME
        shortterm = TimeSlot.SHORTTERM
        traffic_time = int(TimeSlot.TRAFFIC_TIME.total_seconds()) // 60

        teacher = self
        region = school.region
        weekly_time_slots = region.weekly_time_slots.all()

        date = timezone.now()
        occupied = TimeSlot.objects.filter(
                order__teacher=teacher, start__gte=date - renew_time,
                end__lt=date + shortterm + renew_time, deleted=False)

        segtree = SegmentTree(0, 7 * 24 * 60 - 1)
        for occ in occupied:
            cur_school = occ.order.school
            occ.start = timezone.localtime(occ.start)
            occ.end = timezone.localtime(occ.end)
            start = (occ.start.weekday() * 24 * 60 + occ.start.hour * 60 +
                     occ.start.minute)

            end = (occ.end.weekday() * 24 * 60 + occ.end.hour * 60 +
                   occ.end.minute - 1)

            if cur_school.id != school.id:
                start, end = start - traffic_time, end + traffic_time
            segtree.add(start, end)

        def w2m(w, t):
            return (w - 1) * 24 * 60 + t.hour * 60 + t.minute

        data = {(s.weekday, s.start, s.end): (segtree.query_len(
            w2m(s.weekday, s.start), w2m(s.weekday, s.end) - 1) == 0)
            for s in weekly_time_slots
            }
        return data

    def is_shortterm_available(self, start, end, school):
        '''
        start: datetime
        end: datetime
        '''
        assert end > start
        assert start.weekday() == end.weekday()

        shortterm = TimeSlot.SHORTTERM
        sa_dict = self.shortterm_available_dict(school)
        date = timezone.now()

        if not (start >= date and end < date + shortterm):
            return False

        weekday = start.isoweekday()
        start = datetime.time(hour=start.hour, minute=start.minute)
        end = datetime.time(hour=end.hour, minute=end.minute)

        return sa_dict[(weekday, start, end)]

    # 新建一个空白老师用户
    @staticmethod
    def new_teacher(phone: str)->User:
        # 新建用户
        username = random_string()[:30]
        salt = random_string()[:5]
        password = "malalaoshi"
        user = User(username=username)
        user.email = ""
        user.password = make_password(password, salt)
        user.save()
        # 创建老师身份
        profile = Profile(user=user, phone=phone)
        profile.save()
        teacher = Teacher(user=user)
        teacher.save()
        teacher_group = Group.objects.get(name="老师")
        user.groups.add(teacher_group)
        ret_user = authenticate(username=username, password=password)
        return ret_user

    @staticmethod
    def add_teacher_role(user: User):
        # 特殊情况,一个已经存在的家长角色,又用老师角色登录
        # 这里不关心user的登录状态,登录状态见teacher.views.VerifySmsCode.post里的处理
        teacher = Teacher(user=user)
        teacher.save()
        teacher_group = Group.objects.get(name="老师")
        user.groups.add(teacher_group)
        user.save()
        return teacher


class AuditRecord(BaseModel):
    # 新老师审核记录
    # 记录创建时间
    create_at = models.DateTimeField(auto_now=Tree)
    # 审核老师
    teacher = models.ForeignKey(Teacher)
    # 操作员
    operator = models.ForeignKey(User)

    INFORMATION_COMPLETE = "ic"
    PRIMARY_REJECT = "pr"
    PRIMARY_PASS = "pp"
    INTERVIEW_REJECT = "ir"
    INTERVIEW_PASS = "ip"
    OPERATION_CHOICE = (
        # 在老师初选被淘汰后,可以补填资料来让老师再次进入面试环节
        (INFORMATION_COMPLETE, "补填资料"),
        (PRIMARY_REJECT, "初选淘汰"),
        (PRIMARY_PASS, "邀约面试"),
        (INTERVIEW_REJECT, "面试失败"),
        (INTERVIEW_PASS, "面试通过"),
    )
    # 定义路径两头的状态
    OPERATION_PATH = {
        (Teacher.TO_CHOOSE, Teacher.NOT_CHOSEN): PRIMARY_REJECT,
        (Teacher.TO_CHOOSE, Teacher.TO_INTERVIEW): PRIMARY_PASS,
        (Teacher.NOT_CHOSEN, Teacher.TO_CHOOSE): INFORMATION_COMPLETE,
        (Teacher.TO_INTERVIEW, Teacher.INTERVIEW_OK): INTERVIEW_PASS,
        (Teacher.TO_INTERVIEW, Teacher.INTERVIEW_FAIL): INTERVIEW_REJECT
    }
    # 操作动作,这里特指路径,而不是结点
    operation = models.CharField(max_length=3, choices=OPERATION_CHOICE)

    @staticmethod
    def new_audit_record(
            operator: User, teacher: Teacher, old_statue: int,
            new_statue: int):
        # 创建一个新的审核记录
        new_audit_record = AuditRecord(
                teacher=teacher, operator=operator,
                operation=AuditRecord.OPERATION_PATH[(old_statue, new_statue)])
        new_audit_record.save()
        return new_audit_record

    @property
    def operation_description(self):
        msg = "未知操作"
        for key, val in self.OPERATION_CHOICE:
            if self.operation == key:
                msg = val
                break
        return msg

    def __str__(self):
        time_str = localtime(self.create_at).strftime("%Y-%m-%d %H:%M")
        return "{time} {operator}对{teacher}进行{operation}操作".format(
            time=time_str,
            operator=self.operator.id,
            teacher=self.teacher.name,
            operation=self.operation_description
        )

    def html_description(self):
        # 用于html的显示
        time_str = localtime(self.create_at).strftime("%Y-%m-%d %H:%M")
        return {"time": time_str,
                "text": self.operation_description}


class Highscore(BaseModel):
    """
    提分榜
    """
    teacher = models.ForeignKey(Teacher)
    name = models.CharField(max_length=200)
    increased_scores = models.IntegerField(default=0)
    school_name = models.CharField(max_length=300)
    # 考入学校
    admitted_to = models.CharField(max_length=300)

    def __str__(self):
        return '%s: %s %s (%s => %s)' % (
                self.teacher.name,
                self.name, self.increased_scores, self.school_name,
                self.admitted_to)


class Achievement(BaseModel):
    """
    特殊成果
    """
    teacher = models.ForeignKey(Teacher)
    title = models.CharField(max_length=30)
    img = models.ImageField(null=True, blank=True, upload_to='achievements')

    def img_url(self):
        return self.img and self.img.url or ''


class Photo(BaseModel):
    teacher = models.ForeignKey(Teacher)
    img = models.ImageField(null=True, blank=True, upload_to='photos')
    order = models.PositiveIntegerField(default=0)
    public = models.BooleanField(default=False)

    def __str__(self):
        return '%s img (%s)' % (
                self.teacher, 'public' if self.public else 'private')

    def img_url(self):
        return self.img and self.img.url or ''


class Certificate(BaseModel):
    """
    身份认证用了两条记录(手持照和正面照), 因为身份认证有手持照, 判断是否通过认证以手持照记录为准, 正面照只是一个图片
    以手持照记录的name作为ID Card NO.
    """
    ID_HELD = 1
    ID_FRONT = 2
    ACADEMIC = 3
    TEACHING = 4
    ENGLISH = 5
    OTHER = 6

    TYPE_CHOICES = (
        (ID_HELD, '身份证手持照'),
        (ID_FRONT, '身份证正面'),
        (ACADEMIC, '学历认证'),
        (TEACHING, '教师资格证'),
        (ENGLISH, '英语水平证书'),
        (OTHER, '其他资质认证'),
    )

    teacher = models.ForeignKey(Teacher)
    name = models.CharField(max_length=100)
    type = models.IntegerField(null=True, blank=True, choices=TYPE_CHOICES)
    img = models.ImageField(null=True, blank=True, upload_to='certs')
    verified = models.BooleanField(default=False)
    # 优化认证过程提示时添加: audited, show_hint
    audited = models.BooleanField(default=False)    # 是否审核过

    # 是否显示提示'审核成功!''未通过审核!',只显示一次
    show_hint = models.BooleanField(default=False)

    def __str__(self):
        msg = ""
        for key, val in self.TYPE_CHOICES:
            if self.type == key:
                msg = val
                break
        return '%s, 证书名称:%s [%s][%s]' % (self.teacher, self.name, msg,
                                         '已认证' if self.verified else '未认证')

    def img_url(self):
        return self.img and self.img.url or ''

    def is_to_audit(self):
        return (not self.audited) and (not self.verified) and bool(self.img)

    def is_approved(self):
        return self.verified

    def is_rejected(self):
        return self.audited and (not self.verified)

    def mask_id_num(self):
        if self.type == self.ID_HELD and self.name:
            return ('* ' * 10) + str(self.name[-4:])
        return ''

    def show_hint_once(self):
        if self.show_hint:
            self.show_hint = False
            self.save()
            return True
        return False


class InterviewRecord(BaseModel):
    PENDING = 'p'
    APPROVED = 'a'
    REJECTED = 'r'
    STATUS_CHOICES = (
        (PENDING, '待认证'),
        (APPROVED, '已认证'),
        (REJECTED, '已拒绝'),
    )

    teacher = models.ForeignKey(Teacher)
    created_at = models.DateTimeField(auto_now_add=True)
    reviewed_at = models.DateTimeField(auto_now=True)
    reviewed_by = models.ForeignKey(User, null=True, blank=True)
    review_msg = models.CharField(max_length=1000)
    status = models.CharField(max_length=1,
                              choices=STATUS_CHOICES,
                              default=PENDING)

    def __str__(self):
        return '%s by %s' % (self.teacher, self.reviewed_by)


class Account(BaseModel):
    """
    用户(老师)财务账户
    """
    user = models.OneToOneField(User)

    # @property
    # def implicit_balance(self):
    #     # 隐含的余额,
    #     AccountHistory = apps.get_model('app', 'AccountHistory')
    #     ret = AccountHistory.objects.filter(
    #             account=self, done=True).aggregate(models.Sum('amount'))
    #     return ret['amount__sum'] or 0

    @property
    def calculated_balance(self):
        # 可用余额, 减去提现申请中的部分
        return self.accounthistory_set.all().filter(valid=True).aggregate(
                models.Sum('amount'))["amount__sum"] or 0

    @property
    def accumulated_income(self):
        # 累计收入,统计AccountHistory中大于0的记录,求和
        AccountHistory = apps.get_model('app', 'AccountHistory')
        ret = AccountHistory.objects.filter(
                account=self, amount__gt=0, valid=True).aggregate(
                        models.Sum('amount'))
        sum = ret['amount__sum']
        return sum or 0

    @property
    def anticipated_income(self):
        """
        预计收入, 完成未来所有课时后将会得到的金额
        TODO: 这里需要重新计算
        :return:
        """
        Order = apps.get_model('app', 'Order')
        orders = Order.objects.filter(
                teacher__user=self.user, status=Order.PAID)
        sum = 0
        for order in orders:
            sum += order.remaining_amount()
        return sum

    def __str__(self):
        if hasattr(self.user, "teacher"):
            return ("{teacher_name} 可用余额:{cal_bal} 累计收入:{acc_incom} " +
                    "预计收入:{anti_income}").format(
                            teacher_name=self.user.teacher.name,
                            cal_bal=self.calculated_balance/100,
                            # withdrawable_amount=self.withdrawable_amount/100,
                            acc_incom=self.accumulated_income/100,
                            anti_income=self.anticipated_income/100)
        else:
            return "非法帐户 user_id:{user_id}".format(
                user_id=self.user_id,
            )


class BankCard(BaseModel):
    bank_name = models.CharField(max_length=100)
    card_number = models.CharField(max_length=100, unique=True)
    account = models.ForeignKey(Account)
    region = models.ForeignKey(Region, null=True, blank=True)
    opening_bank = models.CharField(max_length=100, null=True, blank=True)

    # 显示的储蓄卡号,最后四位有,前面是 ****,每四个一分组
    # 返回数值为 ["****", "****", "****", "6607"],用返回结果,进一步自行拼接
    def mask_card_number(self):
        card_text = ["****", "****", "****", self.card_number[-4:]]
        return card_text

    def __str__(self):
        try:
            return '%s %s (%s)' % (self.bank_name, self.card_number,
                                   self.account.user.teacher.name)
        except Teacher.DoesNotExist:
            return "{bank_name} {card_number} (未绑定老师)".format(
                bank_name=self.bank_name, card_number=self.card_number
            )

    def mask_number(self):
        return " ".join(self.mask_card_number())


class BankCodeInfo(BaseModel):
    org_code = models.CharField(max_length=30)
    bank_name = models.CharField(max_length=30)
    card_name = models.CharField(max_length=30)
    card_type = models.CharField(max_length=2)
    card_number_length = models.PositiveIntegerField()
    bin_code_length = models.PositiveIntegerField()
    bin_code = models.CharField(max_length=30)

    def __str__(self):
        return '%s, %s, %s (%s)' % (self.bank_name, self.card_name,
                                    self.card_type, self.bin_code)


class Withdrawal(BaseModel):
    # 记录用户提现申请
    PENDING = 'u'
    APPROVED = 'a'
    REJECTED = 'r'
    STATUS_CHOICES = (
        (PENDING, '待审核'),
        (APPROVED, '已通过'),
        (REJECTED, '已驳回')
    )
    # 这些字段都是无效的, account, amount, bankcard, submit_time, comment
    bankcard = models.ForeignKey(BankCard, null=True, blank=True)
    status = models.CharField(max_length=2,
                              choices=STATUS_CHOICES,
                              default=PENDING, )
    audit_by = models.ForeignKey(User, null=True, blank=True)
    audit_at = models.DateTimeField(null=True, blank=True)

    @property
    def status_des(self):
        if self.status == Withdrawal.PENDING:
            return "处理中"
        if self.status == Withdrawal.APPROVED:
            return localtime(self.audit_at).strftime("%Y-%m-%d %H:%M")
        if self.status == Withdrawal.REJECTED:
            return "被驳回"

    def is_rejected(self):
        return self.status == Withdrawal.REJECTED

    def is_pending(self):
        return self.status == Withdrawal.PENDING


class AccountHistory(BaseModel):
    class Meta:
        ordering = ["-submit_time"]

    # 老师课程完成,就记录一条增值记录
    # 钱转到银行卡,会记录一条减值记录
    # 这里的记录都是被审核通过的记录
    account = models.ForeignKey(Account)
    # 本条记录的收入具体金额,正表示入账(比如,老师上课后获得收入),负表示出账(比如,提现)
    amount = models.IntegerField()
    submit_time = models.DateTimeField(auto_now_add=True)
    # 备注
    comment = models.CharField(max_length=100, null=True, blank=True)
    # 如果这条account history是从timeslot中转来的,就记录timeslot
    timeslot = models.OneToOneField(
            'TimeSlot', null=True, blank=True, on_delete=models.SET_NULL)
    # 如果这条account history是从withdrawal提现中创建出来的,就在模型中建立withdrawal外键
    withdrawal = models.OneToOneField(
            Withdrawal, null=True, blank=True, on_delete=models.SET_NULL)
    # 用来说明本条account_history是否参与求和或者其它统计运算,
    # False表示因为提现或者其它原因,比如审核被驳回,这样就不参与求和和其它统计计算
    valid = models.BooleanField(default=True)

    @property
    def abs_amount(self):
        return abs(self.amount)

    def audit_withdrawal(self, approve: bool, user: User=None):
        """
        设置提现流程通过或者不通过,进行操作后,就不需要再save
        :param approve: True,表示通过,False,表示不通过
        :return: 没有返回
        """
        self.withdrawal.audit_by = user
        self.withdrawal.audit_at = timezone.now()
        if approve:
            # 审核通过
            self.withdrawal.status = Withdrawal.APPROVED
            self.valid = True
            self.op_by_function = True
        else:
            # 审核不通过
            self.withdrawal.status = Withdrawal.REJECTED
            self.valid = False
            self.op_by_function = True
        self.withdrawal.save()
        self.save()

    def save(self, force_insert=False, force_update=False, using=None,
             update_fields=None):
        # 用来检查,不可以裸写account history
        if not hasattr(self, "op_by_function"):
            raise Exception("不可以裸操作save函数,请用AccountHistory的指定函数来进行调用")
        super().save()

    def __str__(self):
        teacher_name = self.account.user.teacher.name
        operation = "未知操作"
        if self.withdrawal_id:
            # 老师提现
            # operation = "提现获得"
            operation = "提现获得 {withdrawal_status}{des}".format(
                withdrawal_status=self.withdrawal.status,
                des=self.withdrawal.status_des
            )
        elif self.timeslot_id:
            # 老师上课收入
            try:
                student_name = self.timeslot.order.parent.student_name or \
                        self.timeslot.order.parent.user.profile.phone
                operation = (
                        "上课收入, 给{student_name}教学{subject}{grade}" +
                        "从{start}到{end} {order_status}{order_des}").format(
                                student_name=student_name,
                                subject=self.timeslot.order.subject.name,
                                grade=self.timeslot.order.grade.name,
                                start=self.local_time_str(self.timeslot.start),
                                end=self.local_time_str(self.timeslot.end),
                                order_status=self.timeslot.order.status,
                                order_des=self.timeslot.order.status_des
                )
            except Profile.DoesNotExist:
                operation = "上课收入, 给{student_name}教学{subject}{grade}从{start}到{end} {order_status}{order_des}".format(
                    student_name=self.timeslot.order.parent.student_name or "(用户没有profile model)",
                    subject=self.timeslot.order.subject.name,
                    grade=self.timeslot.order.grade.name,
                    start=self.local_time_str(self.timeslot.start), end=self.local_time_str(self.timeslot.end),
                    order_status=self.timeslot.order.status,
                    order_des=self.timeslot.order.status_des
                )
        return ("{teacher_name} 创建于:{create_at} {operation} " +
                "金额:{amount} [{valid}]").format(
                        teacher_name=teacher_name, operation=operation,
                        amount=self.money_str(self.amount),
                        valid="有效" if self.valid else "无效",
                        create_at=self.local_time_str(self.submit_time)
                )

    @staticmethod
    def build_withdrawal_history(
            withdrawal: Withdrawal, account: Account, amount: int):
        """
        创建提现历史记录
        :param withdrawal: 提现对象
        :param account: 指定账户
        :param amount: 提现金额,注意,这里金额必须为负
        :return: 新的account_history
        """
        if amount > 0:
            raise Exception("转账金额记录入AccountHistory必须为负")
        new_acc_history = AccountHistory(
                account=account, withdrawal=withdrawal, amount=amount)
        new_acc_history.op_by_function = True
        new_acc_history.save()
        return new_acc_history

    @staticmethod
    def build_timeslot_history(timeslot, account: Account, amount: int):
        """
        创建因为上课产生收入的记录
        :param timeslot: 上课记录
        :param account: 指定账户
        :param amount: 存入金额,注意,这里的金额必须为正
        :return:
        """
        if amount < 0:
            raise Exception("上课报酬必须为正")
        new_acc_history = AccountHistory(
                account=account, timeslot=timeslot, amount=amount)
        new_acc_history.valid = True
        new_acc_history.op_by_function = True
        new_acc_history.save()
        return new_acc_history


class Feedback(BaseModel):
    user = models.ForeignKey(User, null=True, blank=True)
    contact = models.CharField(max_length=30)
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s %s %s' % (self.user, self.contact, self.created_at)


class Parent(BaseModel):
    user = models.OneToOneField(User)

    student_name = models.CharField(max_length=50)
    student_school_name = models.CharField(max_length=100, default='')

    def recent_orders(self):
        one_month_before = timezone.now() - datetime.timedelta(days=90)
        return self.order_set.filter(created_at__gt=one_month_before)

    def __str__(self):
        try:
            return "<{pk}>{student_name}同学,手机{phone}".format(
                pk=self.pk, student_name=self.student_name,
                phone=self.user.profile.phone
            )
        except Exception as e:
            return "<{pk}>!!!异常记录,{student_name}同学,手机{msg}".format(
                pk=self.pk, student_name=self.student_name, msg=e
            )

    # 新建一个空白parent
    @staticmethod
    def new_parent()->User:
        # 新建用户
        username = random_string()[:30]
        salt = random_string()[:5]
        password = "malalaoshi"
        user = User(username=username)
        user.email = ""
        user.password = make_password(password, salt)
        user.save()
        parent_group = Group.objects.get(name="家长")
        user.groups.add(parent_group)
        # 创建家长身份
        profile = Profile(user=user, phone="")
        profile.save()
        parent = Parent(user=user)
        parent.save()
        # 集体保存
        user.save()
        profile.save()
        parent.save()
        ret_user = authenticate(username=username, password=password)
        return ret_user

    def save(self, *args, **kwargs):
        if not self.id:
            super(Parent, self).save(*args, **kwargs)

            couponGenerators = CouponGenerator.objects.order_by('-id')
            couponGenerator = list(couponGenerators) and couponGenerators[0]
            if couponGenerator and couponGenerator.activated and (
                    couponGenerator.expired_at > timezone.now()):
                Coupon.objects.get_or_create(
                        parent=self, name='新生奖学金',
                        amount=couponGenerator.amount,
                        mini_course_count=couponGenerator.mini_course_count,
                        validated_start=couponGenerator.validated_start,
                        expired_at=couponGenerator.expired_at, used=False)
        else:
            super(Parent, self).save(*args, **kwargs)


# 因为李鑫还没想好,就先不要这个model,以后再放出来
# class TeacherVistParent(BaseModel):
#     # 这个模型用来记录老师和家长一对一产生的关系
#     teacher = models.ForeignKey(Teacher)
#     parent = models.ForeignKey(Parent)
#     # 记录老师是否已经访问过这个家长
#     web_visited = models.BooleanField(default=False)
#
#     class Meta:
#         unique_together = ("teacher", "parent",)


class CouponRule(BaseModel):
    """
    奖学金使用规则
    """
    content = models.CharField(max_length=50)


class CouponGenerator(BaseModel):
    """
    奖学金生成规则
    """
    activated = models.BooleanField()
    created_at = models.DateTimeField(auto_now_add=True)
    validated_start = models.DateTimeField(
            null=False, blank=False, default=timezone.now)
    expired_at = models.DateTimeField(
            null=False, blank=False, default=timezone.now)
    amount = models.PositiveIntegerField()
    mini_course_count = models.PositiveSmallIntegerField(default=0)


class Coupon(BaseModel):
    # 优惠卷
    parent = models.ForeignKey(Parent, null=True, blank=True)
    name = models.CharField(max_length=50)
    amount = models.PositiveIntegerField()
    created_at = models.DateTimeField(auto_now_add=True)
    validated_start = models.DateTimeField(
            null=False, blank=False, default=timezone.now)
    expired_at = models.DateTimeField(
            null=False, blank=False, default=timezone.now)
    used = models.BooleanField(default=False)
    mini_course_count = models.PositiveSmallIntegerField(default=0)

    def __str__(self):
        return '%s, %s (%s) %s' % (self.parent, self.amount, self.expired_at,
                                   'D' if self.used else '')

    @property
    def status(self):
        now = timezone.now()
        if self.used:
            return 'used'
        elif (not self.used) and now > self.expired_at:
            return 'expired'
        else:
            return 'unused'

    def print_validate_period(self):
        return '%s ~ %s' % (
                self.validated_start.date(), self.expired_at.date())

    def amount_yuan(self):
        return self.amount and self.amount//100 or 0

    class Meta:  # 添加默认排序使列表更合理
        ordering = ["-created_at"]


class WeeklyTimeSlot(BaseModel):
    weekday = models.PositiveIntegerField()  # 1 - 7
    start = models.TimeField()  # [0:00 - 24:00)
    end = models.TimeField()

    class Meta:
        unique_together = ('weekday', 'start', 'end')
        ordering = ['weekday', 'start', 'end']

    def __str__(self):
        return '%s from %s to %s' % (self.weekday, self.start, self.end)

    @classproperty
    def DAILY_TIME_SLOTS(cls):
        return [
                dict(start=item.start, end=item.end) for item in
                cls.objects.filter(weekday=1).order_by('start')]


class OrderManager(models.Manager):
    def create(self, parent, teacher, school, grade, subject, hours, coupon):
        ability = grade.ability_set.filter(subject=subject)[0]

        price = teacher.region.price_set.get(
                ability=ability, level=teacher.level).price

        # pure total price, not calculate coupon's amount
        total = price * hours
        to_pay = max(total - (coupon.amount if coupon else 0), 1)

        order_id = orderid()

        order = super(OrderManager, self).create(
                parent=parent, teacher=teacher, school=school, grade=grade,
                subject=subject, price=price, hours=hours,
                total=total, coupon=coupon, order_id=order_id, to_pay=to_pay)

        order.save()
        return order

    def _weekly_date_to_minutes(self, date):
        return date.weekday() * 24 * 60 + date.hour * 60 + date.minute

    def _delta_minutes(self, weekly_ts, cur_min):
        return (
                (weekly_ts.weekday - 1) * 24 * 60 + weekly_ts.start.hour * 60 +
                weekly_ts.start.minute - cur_min + 7 * 24 * 60) % (7 * 24 * 60)

    def concrete_timeslots(self, hours, weekly_time_slots, teacher):
        grace_time = TimeSlot.GRACE_TIME
        date = timezone.localtime(timezone.now()) + grace_time
        date = date.replace(second=0, microsecond=0)
        date += datetime.timedelta(minutes=1)

        cur_min = self._weekly_date_to_minutes(date)
        wtss = sorted(
                weekly_time_slots,
                key=lambda x: self._delta_minutes(x, cur_min))

        occupied_dict = {}
        for wts in wtss:
            occupied_dict[(wts.weekday, wts.start)] = set()

        occupied = TimeSlot.objects.filter(
                order__teacher=teacher, start__gte=date, deleted=False)
        for occ in occupied:
            occ.start = timezone.localtime(occ.start)
            occ.end = timezone.localtime(occ.end)
            key = (occ.start.isoweekday(), occ.start.time())
            if key in occupied_dict:
                occupied_dict[key].add((occ.start, occ.end))

        n = len(wtss)
        h = hours
        i = 0
        ans = []
        while h > 0:
            weekly_ts = wtss[i % n]
            start = date + datetime.timedelta(
                    minutes=self._delta_minutes(weekly_ts, cur_min)
                    ) + datetime.timedelta(days=7 * (i // n))

            end = start + datetime.timedelta(
                    minutes=(weekly_ts.end.hour - weekly_ts.start.hour) * 60 +
                    weekly_ts.end.minute - weekly_ts.start.minute)

            key = (start.isoweekday(), start.time())
            if (start, end) not in occupied_dict[key]:
                ans.append(dict(start=start, end=end))
                h = h - 2  # for now, 1 time slot include 2 hours
            i = i + 1
        return ans

    def get_order_timeslots(self, order, check_conflict=True):
        weekly_time_slots = list(order.weekly_time_slots.all())
        periods = [(s.weekday, s.start, s.end) for s in weekly_time_slots]

        school = order.school
        teacher = order.teacher
        parent = order.parent
        if check_conflict:
            if not teacher.is_longterm_available(periods, school, parent):
                raise TimeSlotConflict()
        return self.concrete_timeslots(order.hours, weekly_time_slots, teacher)

    def allocate_timeslots(self, order, force=False):
        TimeSlot = apps.get_model('app', 'TimeSlot')

        if order.is_timeslot_allocated():
            logger.warn('Time slot already allocated for order %s' % order.id)
            return

        name = '/teacher_%d' % order.teacher.id
        semaphore = posix_ipc.Semaphore(
                name, flags=posix_ipc.O_CREAT, initial_value=1)
        semaphore.acquire()
        try:
            timeslots = self.get_order_timeslots(order)
            for ts in timeslots:
                timeslot = TimeSlot(
                        order=order, start=ts['start'], end=ts['end'])
                timeslot.save()
        except Exception as e:
            raise e
        finally:
            semaphore.release()
        # 短信通知老师, 以及家长
        teacher_name = order.teacher.name
        student_name = order.parent.student_name
        grade = order.grade.name + order.subject.name
        try:
            tpl_send_sms(order.teacher.phone(), TPL_TEACHER_COURSE_PAID, {
                'username': teacher_name, 'studentname': student_name,
                'grade': grade, 'number': order.hours})
        except Exception as ex:
            logger.error(ex)
        try:
            amount_str = "%.2f" % (order.to_pay / 100)
            tpl_send_sms(
                    order.parent.user.profile.phone, TPL_STU_PAY_SUCCESS, {
                        'studentname': student_name, 'orderid': order.order_id,
                        'amount': amount_str})
        except Exception as ex:
            logger.error(ex)
        # 课时分配成功, 判断是否生成 测评建档
        order_count = Order.objects.filter(
            parent=order.parent, subject=order.subject,
            status=Order.PAID).count()
        if order_count == 1:
            # 首单, 创建 测评建档
            evaluation = Evaluation(order=order)
            evaluation.save()
        return timeslots

    def refund(self, order, reason, user):
        OrderRefundRecord = apps.get_model('app', 'OrderRefundRecord')
        TimeSlot = apps.get_model('app', 'TimeSlot')

        if order.status != order.PAID:
            raise OrderStatusIncorrect('订单未支付')
        if order.refund_status == order.REFUND_PENDING:
            raise OrderStatusIncorrect('订单退费正在申请中, 请勿重复提交')
        if order.refund_status == order.REFUND_APPROVED:
            raise OrderStatusIncorrect('订单退费已经审核通过, 请勿重复提交')

        try:
            with transaction.atomic():
                # 生成新的 OrderRefundRecord, 根据当前时间点, 计算退费信息, 并保存在退费申请记录中
                record = OrderRefundRecord(
                    order=order,
                    remaining_hours=order.remaining_hours(),
                    refund_hours=order.preview_refund_hours(),
                    refund_amount=order.preview_refund_amount(),
                    reason=reason,
                    last_updated_by=user
                )
                record.save()
                # 同时更新订单的退费状态字段
                order.refund_status = order.REFUND_PENDING
                # 记录申请时间, 用于 query
                order.refund_at = record.created_at
                order.save()
                if TimeSlot.objects.filter(
                        order=order, deleted=False).count() > 0:
                    # 断言以确保将释放的时间无误, 已经 deleted 的课不要计算在内
                    assert TimeSlot.objects.all().filter(
                        order=order,
                        deleted=False,
                        accounthistory__isnull=True,
                        order__status=Order.PAID
                    ).count() * 2 == record.remaining_hours
                    # 释放该订单内的所有未完成的课程时间
                    TimeSlot.objects.all().filter(
                        order=order,
                        deleted=False,
                        accounthistory__isnull=True,
                        order__status=Order.PAID
                    ).update(deleted=True)
                else:
                    coupon = order.coupon
                    if coupon is not None:
                        coupon.used = False
                        coupon.save()

        except IntegrityError as err:
            logger.error(err)
            raise RefundError('退费失败, 请稍后重试或联系管理员')
        except AssertionError as err:
            logger.error(err)
            raise RefundError('退费失败, 订单剩余小时与将要退费的课程时间不符, 请稍后重试或联系管理员')


class Order(BaseModel):
    PENDING = 'u'
    PAID = 'p'
    CANCELED = 'd'

    # REFUND 表示已经退费成功的订单
    # todo: 订单的退费成功状态只应该在审核通过时设置, 其他地方不应操作
    REFUND = "r"
    STATUS_CHOICES = (
        (PENDING, '待付款'),
        (PAID, '已付款'),
        (CANCELED, '已取消'),
        (REFUND, '退费成功')
    )

    objects = OrderManager()

    parent = models.ForeignKey(Parent, null=True, blank=True)
    teacher = models.ForeignKey(Teacher)
    school = models.ForeignKey(School)
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)
    coupon = models.ForeignKey(Coupon, null=True, blank=True)
    weekly_time_slots = models.ManyToManyField(WeeklyTimeSlot)
    level = models.ForeignKey(
            Level, null=True, blank=True, on_delete=models.SET_NULL)

    commission_percentage = models.PositiveIntegerField(default=0)
    price = models.PositiveIntegerField()
    hours = models.PositiveIntegerField()
    order_id = models.CharField(max_length=20, default=orderid, unique=True)
    total = models.PositiveIntegerField()
    to_pay = models.PositiveIntegerField(default=0)

    created_at = models.DateTimeField(auto_now_add=True)
    paid_at = models.DateTimeField(null=True, blank=True)

    status = models.CharField(max_length=2,
                              choices=STATUS_CHOICES,
                              default=PENDING, )

    # 最后审核状态
    REFUND_PENDING = 'rp'
    REFUND_APPROVED = 'ra'
    REFUND_REJECTED = 'rr'
    REFUND_STATUS_CHOICES = (
        (REFUND_PENDING, '退费审核中'),
        (REFUND_APPROVED, '退费成功'),
        (REFUND_REJECTED, '退费被驳回')
    )
    refund_status = models.CharField(max_length=2,
                                     choices=REFUND_STATUS_CHOICES,
                                     null=True,
                                     blank=True)
    # 保存的最后退费申请时间
    refund_at = models.DateTimeField(null=True, blank=True)

    @property
    def status_des(self):
        msg = "未知状态"
        for key, val in self.STATUS_CHOICES:
            if self.status == key:
                msg = val
                break
        return msg

    def __str__(self):
        try:
            return ("<{pk}> {order_status} {student_name}同学{student_phone}于" +
                "{submit_time}向{teacher_name}老师{teacher_phone}在{local}" +
                ",下了一个{subject}{grade}订单,每小时价格{price}").format(
                        pk=self.pk, student_name=self.parent.student_name,
                        submit_time=self.local_time_str(self.created_at),
                        teacher_name=self.teacher.name, local=self.school,
                        subject=self.subject.name, grade=self.grade.name,
                        teacher_phone=self.teacher.user.profile.phone,
                        student_phone=self.parent.user.profile.phone,
                        order_status=self.status,
                        price=self.money_str(self.price)
            )
        except Profile.DoesNotExist:
            return "<{pk}> {order_status} {student_name}同学{student_phone}于{submit_time}向{teacher_name}老师{teacher_phone}在{local},下了一个{subject}{grade}订单,每小时价格{price}".format(
                pk=self.pk, student_name=self.parent.student_name, submit_time=self.local_time_str(self.created_at),
                teacher_name=self.teacher.name, local=self.school, subject=self.subject.name, grade=self.grade.name,
                teacher_phone="角色没有Profile", student_phone="",
                order_status=self.status, price=self.money_str(self.price)
            )

    def is_timeslot_allocated(self):
        return self.timeslot_set.filter(deleted=False).count() > 0

    def fit_statistical(self):
        # 主要用于FirstPage中
        return self.status == self.PAID

    def fit_school_time(self):
        # 主要用于学校课程表中
        return self.status == self.PAID

    def enum_timeslot(self, handler):
        for one_timeslot in self.timeslot_set.filter(deleted=False):
            handler(one_timeslot)

    # 计算订单内已经完成课程的小时数(消耗小时)
    def completed_hours(self):
        completed_hours = 0
        for one_timeslot in self.timeslot_set.filter(deleted=False):
            if one_timeslot.is_settled():
                completed_hours += one_timeslot.duration_hours()
        return completed_hours

    # 计算消耗金额
    def completed_amount(self):
        return self.price * self.completed_hours()

    # 计算剩余小时
    def remaining_hours(self):
        total_hours = self.timeslot_set.filter(deleted=False).count()*2
        return total_hours - self.completed_hours()

    # 计算剩余金额,单位是分
    def remaining_amount(self):
        return self.price * self.remaining_hours()

    # 计算退费小时(预览, 不是记录)
    def preview_refund_hours(self):
        discount_amount = self.coupon.amount if self.coupon is not None else 0
        # 退费小时 = 剩余小时 - (奖学金 / 课程单价)(向下取整)
        hours = self.remaining_hours() - int(discount_amount / self.price)
        return max(hours, 0)

    # 计算退费金额(预览, 不是记录)
    def preview_refund_amount(self):
        discount_amount = self.coupon.amount if self.coupon is not None else 0
        amount = self.total - discount_amount - self.completed_amount()
        return max(amount, 0)

    # 计算实际上课小时
    def real_completed_hours(self):
        # 等于消耗小时
        return self.completed_hours()

    # 计算实际订单金额
    def real_order_amount(self):
        # 若有退费, 则等于消耗金额
        if self.status == Order.REFUND:
            return self.completed_amount()
        # 若无退费, 则等于订单金额 - 奖学金
        else:
            discount_amount = (
                    self.coupon.amount if self.coupon is not None else 0)
            return max(self.total - discount_amount, 0)

    # 最后退费申请记录
    def last_refund_record(self):
        # 有可能是 None
        return self.orderrefundrecord_set.order_by('created_at').last()

    # 最后退费信息, 是当时申请的记录
    def refund_info(self):
        last_record = self.last_refund_record()
        if last_record is None:
            return None
        else:
            class RefundInfo:
                pass
            refund_info = RefundInfo()
            # 最后申请退费时间
            refund_info.refunded_at = last_record.created_at
            # 最后审核时间
            refund_info.audited_at = last_record.last_updated_at
            # 最后退费审核人
            refund_info.auditor = last_record.last_updated_by
            # 剩余小时
            refund_info.remaining_hours = last_record.remaining_hours
            # 退费小时
            refund_info.refund_hours = last_record.refund_hours
            # 退费金额
            refund_info.refund_amount = last_record.refund_amount
            # 退费原因
            refund_info.reason = last_record.reason
            return refund_info

    def save(self, *args, **kwargs):
        if not self.id:
            super(Order, self).save(*args, **kwargs)
            if self.coupon is not None:
                self.coupon.used = True
                self.coupon.save()
        else:
            super(Order, self).save(*args, **kwargs)

    def is_student_first_subject(self):
        # 首单必须是已支付的, 而且存在测评建档的订单
        if self.status == Order.PAID and self.evaluation is not None:
            return True
        # 其余情况都不是首单
        return False


class OrderRefundRecord(BaseModel):
    status = models.CharField(max_length=2,
                              choices=Order.REFUND_STATUS_CHOICES,
                              default=Order.REFUND_PENDING)

    order = models.ForeignKey(Order)
    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User)

    # 申请退费时, 保存的退费信息(剩余小时/退费小时/退费金额)
    remaining_hours = models.PositiveIntegerField(default=0)
    refund_hours = models.PositiveIntegerField(default=0)
    refund_amount = models.PositiveIntegerField(default=0)
    reason = models.CharField(max_length=100, default="退费原因", blank=True)

    class Meta:
        ordering = ["-created_at"]

    def approve_refund(self):
        if self.status == Order.REFUND_PENDING:
            self.status = Order.REFUND_APPROVED
            self.order.refund_status = Order.REFUND_APPROVED
            # todo: 订单的退费成功状态只应该在这一处操作
            self.order.status = Order.REFUND
            self.order.save()
            self.save()
            return True
        return False

    def reject_refund(self):
        if self.status == Order.REFUND_PENDING:
            self.status = Order.REFUND_REJECTED
            self.order.refund_status = Order.REFUND_REJECTED
            self.order.save()
            self.save()
            return True
        return False

    def __str__(self):
        return ("<{pk}> {student_name}同学于{create_at} 退了 {teacher_name}" +
                "老师 {subject} {grade} order_id<{order_pk}>").format(
                        pk=self.pk,
                        student_name=self.order.parent.student_name,
                        teacher_name=self.order.teacher.name,
                        subject=self.order.subject.name,
                        grade=self.order.grade.name, order_pk=self.order_id,
                        create_at=self.local_time_str(self.created_at)
                )


class Charge(BaseModel):
    """
    用户直接用微信公共号支付下单, 时channel为WX_PUB_MALA, 后缀mala区别ping++的微信公共号渠道
    而且, 微信的prepay_id存为ch_id
    """
    WX_PUB_MALA = 'wx_pub_mala'

    order = models.ForeignKey(Order, null=True, blank=True)
    ch_id = models.CharField(max_length=40, unique=True)
    created = models.DateTimeField(null=True, blank=True)
    livemode = models.BooleanField(default=False)
    paid = models.BooleanField(default=False)
    refunded = models.BooleanField(default=False)
    app = models.CharField(max_length=40)
    channel = models.CharField(max_length=20)
    order_no = models.CharField(max_length=20)
    client_ip = models.CharField(max_length=15)
    amount = models.IntegerField(default=0)
    amount_settle = models.IntegerField(default=0)
    currency = models.CharField(max_length=3)
    subject = models.CharField(max_length=32)
    body = models.CharField(max_length=128)
    extra = models.TextField()
    time_paid = models.DateTimeField(null=True, blank=True)
    time_expire = models.DateTimeField(null=True, blank=True)
    time_settle = models.DateTimeField(null=True, blank=True)
    transaction_no = models.CharField(max_length=50)
    amount_refunded = models.IntegerField(default=0)
    failure_code = models.CharField(max_length=10)
    failure_msg = models.CharField(max_length=30)
    metadata = models.CharField(max_length=50)
    credential = models.TextField()
    description = models.CharField(max_length=255)

    def __str__(self):
        return "<{pk}>[通道:{channel}] [body:{body}] {order}".format(order=self.order, pk=self.pk, channel=self.channel,
                                                             body=self.body)


class Refund(BaseModel):
    charge = models.ForeignKey(Charge)
    re_id = models.CharField(max_length=27, unique=True)
    order_no = models.CharField(max_length=27)
    amount = models.IntegerField(default=0)
    succeed = models.BooleanField(default=False)
    status = models.CharField(max_length=10)
    created = models.DateTimeField(null=True, blank=True)
    time_succeed = models.DateTimeField(null=True, blank=True)
    description = models.CharField(max_length=255)
    failure_code = models.CharField(max_length=10)
    failure_msg = models.CharField(max_length=30)
    metadata = models.CharField(max_length=50)
    transaction_no = models.CharField(max_length=40)


class TimeSlotComplaint(BaseModel):
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User, null=True, blank=True)

    def __str__(self):
        return '%s' % (self.content)


class TimeSlotAttendance(BaseModel):
    NORMAL = 'a'
    ABSENT = 'b'
    LATE10 = 'c'
    LATE10_30 = 'd'
    LATE30 = 'e'
    TYPE_CHOICES = (
        (LATE10, '10分钟内'),
        (LATE10_30, '10-30分钟'),
        (LATE30, '30分钟以上'),
        (ABSENT, '缺勤'),
        (NORMAL, '正常出勤'),
    )

    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User, null=True, blank=True)
    record_type = models.CharField(max_length=1,
                                   choices=TYPE_CHOICES,
                                   default=NORMAL)

    def __str__(self):
        return '%s' % (self.get_record_type_display())


class Comment(BaseModel):
    # 评分, 评分低于3分是差评
    score = models.PositiveIntegerField()
    content = models.CharField(max_length=500)
    # 回复
    reply = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)
    # 页面浏览过的comments,如果没有浏览过,就会在边栏显示出来
    web_visited = models.BooleanField(default=False)

    class Meta:
        ordering = ["-created_at"]

    def __str__(self):
        try:
            student_name = self.timeslot.order.parent.student_name
            return ("老师:{teacher_name} 学生:{student_name}:[{score}星] " +
                    "{created_at} comment:{comment} reply:{reply}").format(
                            teacher_name=self.timeslot.order.teacher.name,
                            student_name=student_name,
                            score=self.score, comment=self.content,
                            reply=self.reply,
                            created_at=self.local_time_str(self.created_at),)
        except Exception as e:
            return ("异常评论, <{pk}>, comment:{comment} [{score}星] " +
                    "{created_at} [{msg}]").format(
                            pk=self.pk, comment=self.content,
                            score=self.score,
                            created_at=self.local_time_str(self.created_at),
                            msg=e)

    def is_bad_comment(self):
        # 差评
        if self.score < 3:
            return True
        return False

    def is_mediu_evaluation(self):
        # 中评
        if 2 < self.score < 5:
            return True
        return False

    def is_high_praise(self):
        # 好评
        if self.score == 5:
            return True
        return False


class TimeSlotShouldAutoConfirmManager(models.Manager):
    def get_queryset(self):
        now = timezone.now()
        autoConfirmDeltaTime = TimeSlot.CONFIRM_TIME
        return super(TimeSlotShouldAutoConfirmManager, self).get_queryset(
                ).filter(attendance__isnull=True,
                         end__lt=now - autoConfirmDeltaTime,
                         order__status=Order.PAID,
                         deleted=False,
                         accounthistory__isnull=True)


class TimeSlot(BaseModel):
    class Meta:
        ordering=["-start", "-created_at"]
    TRAFFIC_TIME = datetime.timedelta(hours=1)
    RENEW_TIME = datetime.timedelta(hours=12)
    SHORTTERM = datetime.timedelta(days=7)
    GRACE_TIME = datetime.timedelta(days=2)
    CONFIRM_TIME = datetime.timedelta(hours=2)

    order = models.ForeignKey(Order)
    start = models.DateTimeField()
    end = models.DateTimeField()

    confirmed_by = models.ForeignKey(Parent, null=True, blank=True)
    transferred_from = models.ForeignKey(
            'TimeSlot', related_name='trans_to_set', null=True, blank=True)

    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User, null=True, blank=True)

    comment = models.OneToOneField(Comment, null=True, blank=True)
    complaint = models.OneToOneField(TimeSlotComplaint, null=True, blank=True)
    attendance = models.OneToOneField(
            TimeSlotAttendance, null=True, blank=True)

    deleted = models.BooleanField(default=False)
    # suspended 单独指示被停的课, 和被删掉的区分开
    suspended = models.BooleanField(default=False)

    # The default manager.
    objects = models.Manager()
    # 返回应该被自动确认的TimeSlot
    should_auto_confirmed_objects = TimeSlotShouldAutoConfirmManager()

    # 用于记录侧边栏的显示,没有访问过的新课程,会被记录
    web_visited = models.BooleanField(default=False)

    def __str__(self):
        try:
            return ('<{id}> [{start} - {end}] 老师:{teacher_name} ' +
                    '学生:{student_name} [{is_valid}]').format(
                            id=self.pk, start=self.local_time_str(self.start),
                            end=localtime(self.end).strftime('%H:%M'),
                            teacher_name=self.order.teacher.name,
                            student_name=self.order.parent.student_name,
                            create_at=self.local_time_str(self.created_at),
                            is_valid="无效" if self.deleted else "有效")
        except:
            return "异常timeslot <{id}> {create_at}".format(
                id=self.pk, create_at=localtime(self.created_at)
            )

    def is_settled(self):
        # 课程是否结束并被结算
        # 条件:delete是False,同时被课程审核批处理处理过
        return not self.deleted and hasattr(self, "accounthistory")

    def is_complete(self, given_time=timezone.now()):
        # 课程是否已经上过
        return self.end < given_time - TimeSlot.CONFIRM_TIME

    def is_waiting(self, given_time):
        # 对于给定时间,课程是否处于等待
        if given_time < self.start:
            return True
        return False

    def is_running(self, given_time):
        # 对于给定时间,课程是否处于上课中
        if self.start < given_time < self.end:
            return True
        return False

    def duration_hours(self):
        return int((self.end - self.start).seconds/3600)

    @property
    def trans_to_time(self):
        if self.deleted and self.trans_to_set.exists():
            return self.trans_to_set.first().start

    @property
    def is_transfered(self):
        # 判断为已经被调课状态
        if self.deleted and self.trans_to_set.exists():
            return True
        return False

    @property
    def is_suspended(self):
        # 判断为被停课状态
        if self.suspended and not self.trans_to_set.exists():
            return True
        return False

    @property
    def is_commented(self):
        return self.comment is not None

    @property
    def school(self):
        return self.order.school

    @property
    def subject(self):
        return self.order.subject

    @property
    def is_passed(self):
        # 不再增加2小时延长时间
        return self.end < timezone.now()
        # return self.is_complete()

    @property
    def teacher(self):
        return self.order.teacher

    def confirm(self):
        # try:
        """
        确认课时, 老师收入入账
        """
        # 检查这个timeslot是否需要被记账

        teacher = self.order.teacher
        account = teacher.safe_get_account()
        amount = self.duration_hours() * self.order.price
        amount = amount * (100 - self.order.commission_percentage) // 100
        if amount < 0:
            amount = 0
        if not AccountHistory.objects.filter(timeslot=self).exists():
            AccountHistory.build_timeslot_history(self, account, amount)
            # 短信通知老师
            try:
                logger.debug("send sms success."+teacher.phone())
            except Exception as ex:
                logger.error(ex)
        attendance = TimeSlotAttendance.objects.create(record_type='a')
        self.attendance = attendance
        self.save()
        return True
        # except IntegrityError as err:
        #     print('2')
        #     logger.error(err)
        #     return False

    def suspend(self):
        # 用 suspended 字段表示停课, 但为了兼容性, 同时也要设置课程状态为被删除
        self.deleted = True
        self.suspended = True
        self.save()
        return

    def reschedule_for_suspend(self, user):
        semaphore = posix_ipc.Semaphore(
                'reschedule', flags=posix_ipc.O_CREAT, initial_value=1)
        semaphore.acquire()

        # 防止重复停课
        if self.deleted:
            semaphore.release()
            return False

        # 如果是已调课后的, 先获取原始课程
        old_timeslot = (
                self.transferred_from
                if self.transferred_from is not None
                else self)
        # 这里不能只看当前订单的最后一次课, 同一个学生可能还有其他订单约了老师后续的课程
        # 因此需要得到"这个老师"最后一个 weekday 和 start, end 的 time 相同的 slot
        # 同时过滤掉已经失效的 和 已经完成(结束2小时以后的)的课程
        time_slots = TimeSlot.objects.filter(
            deleted=False,
            end__gt=timezone.now() - TimeSlot.CONFIRM_TIME,
            order__teacher=old_timeslot.order.teacher
        ).order_by('-start')
        last_slot = None
        for one in time_slots:
            if one.start.time() == old_timeslot.start.time() and \
                    one.start.weekday() == old_timeslot.start.weekday():
                last_slot = one
                break

        if last_slot is None:
            # 找不到"这个老师"最后一个 weekday 和 start, end 的 time 相同的 slot
            # 说明该课程肯定是调课过来的, 原始课程,即是最后一个 slot
            if self.transferred_from:
                last_slot = old_timeslot
            else:  # 不是调来的, 肯定有问题
                semaphore.release()
                raise TimeSlotConflict()

        # 增加事务处理
        try:
            with transaction.atomic():
                # 创建一个新的slot, 时间为上面得到的 last + 7 天
                # todo: 应该同时检测该 last 是否已经被学生自己占用, 这个版本先不做
                new_timeslot = TimeSlot(
                    order=last_slot.order,
                    start=last_slot.start + datetime.timedelta(days=7),
                    end=last_slot.end + datetime.timedelta(days=7),
                    last_updated_by=user
                )
                new_timeslot.save()
                # 把当前的课程停掉, 注意是 self, 不是 old_timeslot
                self.last_updated_by = user
                self.suspend()
                # 添加停课记录
                change_log = TimeSlotChangeLog(
                    old_timeslot=self,
                    new_timeslot=new_timeslot,
                    record_type=TimeSlotChangeLog.SUSPEND,
                    created_by=user
                )
                change_log.save()
        except IntegrityError as err:
            logger.error(err)
            semaphore.release()
            return False
        semaphore.release()
        return True

    def reschedule_for_transfer(self, new_start, new_end, user):
        semaphore = posix_ipc.Semaphore(
                'reschedule', flags=posix_ipc.O_CREAT, initial_value=1)
        semaphore.acquire()

        # 0 代表成功
        ret_code = 0

        # 防止重复调课
        if self.deleted:
            ret_code = -1
            semaphore.release()
            return ret_code
        try:
            with transaction.atomic():
                # 首先, 校验这个课有没有冲突
                # 筛选这个老师, 或者这个家长的有效课程, 并且时间与新课程时间一致, 应该是没有的才对
                should_be_empty = TimeSlot.objects.filter(
                    (Q(order__teacher=self.order.teacher) |
                     Q(order__parent=self.order.parent)) &
                    Q(deleted=False, start=new_start, end=new_end)
                )
                assert should_be_empty.count() == 0

                # 校验成功
                # 生成新的调课后课程, transferred_from 为最原始的课程(如果原课程已经被调过)
                transferred_from = (
                        self.transferred_from
                        if self.transferred_from is not None else self)

                new_timeslot = TimeSlot(
                    order=self.order,
                    start=new_start,
                    end=new_end,
                    transferred_from=transferred_from,
                    last_updated_by=user
                )
                new_timeslot.save()
                # 先把当前 slot 置为取消状态
                self.deleted = True
                self.save()
                # 添加调课记录
                change_log = TimeSlotChangeLog(
                    old_timeslot=self,
                    new_timeslot=new_timeslot,
                    record_type=TimeSlotChangeLog.TRANSFER,
                    created_by=user
                )
                change_log.save()
        except IntegrityError as err:
            logger.error(err)
            ret_code = -1
        except AssertionError as err:
            logger.error(err)
            ret_code = -2

        semaphore.release()
        return ret_code


class TimeSlotChangeLog(BaseModel):
    TRANSFER = 't'
    SUSPEND = 's'
    TYPE_CHOICES = (
        (TRANSFER, '调课'),
        (SUSPEND, '停课')
    )

    record_type = models.CharField(max_length=2, choices=TYPE_CHOICES)
    old_timeslot = models.ForeignKey(TimeSlot, related_name='old')
    new_timeslot = models.ForeignKey(TimeSlot, related_name='new')
    created_at = models.DateTimeField(auto_now_add=True)
    created_by = models.ForeignKey(User)

    class Meta:  # 添加默认排序使列表更合理
        ordering = ["-created_at"]


class Message(BaseModel):
    SYSTEM = 's'
    FINANCE = 'f'
    COURSE = 'c'
    AUDIT = 'a'
    COMMENT = 'm'
    TYPE_CHOICES = (
        (SYSTEM, '系统消息'),
        (FINANCE, '收入消息'),
        (COURSE, '课程消息'),
        (AUDIT, '审核消息'),
        (COMMENT, '评论消息'),
    )

    SMS = 's'
    MAIL = 'm'
    NOTIFICATION = 'n'
    VIA_CHOICES = (
        (SMS, '短信'),
        (MAIL, '邮件'),
        (NOTIFICATION, '通知栏提醒'),
    )

    to = models.ForeignKey(User)
    viewed = models.BooleanField()
    deleted = models.BooleanField()
    title = models.CharField(max_length=100)
    content = models.CharField(max_length=1000)
    _type = models.CharField(max_length=1,
                             choices=TYPE_CHOICES,
                             )
    via = models.CharField(max_length=1,
                           choices=VIA_CHOICES,
                           )
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s, %s, to %s, %s' % (
                self.get__type_display(), self.get_via_display(),
                self.to, self.title)


class Checkcode(BaseModel):
    # 手机发送的校验码
    EXPIRED_TIME = 30    # 30 minutes
    RESEND_SPAN = 1      # 1 minute
    MAX_VERIFY_TIMES = 3  # prevent attacking
    BLOCK_TIME = 10  # 每10分钟允许校验3次

    phone = models.CharField(max_length=20, unique=True)
    checkcode = models.CharField(max_length=30)
    updated_at = models.DateTimeField(auto_now_add=True)
    verify_times = models.PositiveIntegerField(default=0)
    # 当MAX_VERIFY_TIMES触发以后,过多久可以解锁
    block_start_time = models.DateTimeField(
            blank=True, null=True, default=None)
    resend_at = models.DateTimeField(blank=True, null=True, default=None)

    def __str__(self):
        return "{phone} - {sms_code} 更新时间:{updated_at} 验证次数:{verify_times}次 阻止起始时间:{block_start_time} 重新发送时间:{resend_at}".format(
            phone=self.phone, sms_code=self.checkcode, updated_at=self.local_time_str(self.updated_at),
            verify_times=self.verify_times, block_start_time=self.local_time_str(self.block_start_time),
            resend_at=self.local_time_str(self.resend_at))

    @staticmethod
    def has_sms(phone, code):
        try:
            Checkcode.objects.get(phone=phone, checkcode=code)
            return True
        except Checkcode.DoesNotExist:
            return False

    @classmethod
    def generate(cls, phone):
        # 注意,返回的元组,第一项是布尔型,如果False,表示返回的第二项无效或有问题,True,表示返回的第二项有效
        # 生成，并保存到数据库或缓存，10分钟后过期
        # 返回的元组第一项为False,表示有错误,True表示没有错误
        def _generate_code(is_test):
            # is_test是True,就生成固定的1111,否则是一个4位随机数
            return is_test and '1111' or str(random.randrange(1000, 9999))

        if settings.FAKE_SMS_SERVER is True:
            # 如果配置文件里的FAKE_SMS_SERVER就是True,则直接进入测试状态
            is_test = True
        else:
            is_test = isTestPhone(phone)
        obj, created = Checkcode.objects.get_or_create(
                phone=phone, defaults={'checkcode': _generate_code(is_test)})
        if not created:
            # 数据库已经存在sms code
            now = timezone.now()
            delta = now - obj.updated_at
            if delta > datetime.timedelta(minutes=cls.EXPIRED_TIME):
                # 过期就创建一个新的smsCODE
                # print("expired")
                obj.checkcode = _generate_code(is_test)
                obj.updated_at = now
                obj.verify_times = 0
                obj.resend_at = now
                obj.save()
            else:
                # 未过期
                resend_at = obj.resend_at and obj.resend_at or obj.updated_at
                delta = now - resend_at
                if delta < datetime.timedelta(minutes=cls.RESEND_SPAN):
                    # 如果请求过于密集,就不发送,而是直接返回该sms
                    return True, obj.checkcode
                else:
                    # 准备进行重新发送
                    obj.resend_at = now
                    obj.save()
        if is_test is False:
            # 如果不是测试的类型就直接发送了
            try:
                sendCheckcode(phone, obj.checkcode)
            except SendSMSError as e:
                return False, e
        return True, obj.checkcode

    @classmethod
    def verify(cls, phone, code):
        # return is_valid, err_no
        # 0: 正常情况,有错误和正确两种情况
        # 1: 没有生成验证码
        # 2: 验证码已过期
        # 3: 检测过于频繁,于1分钟后再试
        try:
            obj = Checkcode.objects.get(phone=phone)
            delta = timezone.now() - obj.updated_at
            if delta > datetime.timedelta(minutes=cls.EXPIRED_TIME):
                return False, 2
            if obj.verify_times >= cls.MAX_VERIFY_TIMES:  # maybe got attack
                # now = make_aware(datetime.datetime.now())
                now = timezone.now()
                if obj.block_start_time is None:
                    obj.block_start_time = make_aware(datetime.datetime.now())
                    obj.save()
                if obj.block_start_time + \
                        datetime.timedelta(minutes=cls.BLOCK_TIME) < now:
                    # 过了1分钟,就又能检验3次
                    obj.block_start_time = None
                    obj.verify_times = 0
                    obj.save()
                else:
                    obj.save()
                    return False, 3
            is_valid = code == obj.checkcode
            if is_valid:
                obj.delete()
            else:
                obj.verify_times += 1
                obj.save()
            return is_valid, 0
        except Checkcode.DoesNotExist:
            return False, 1

    @classmethod
    def verify_msg(cls, result, code):
        # 用于翻译上面那个函数的code值
        if result is True:
            return "验证通过"
        else:
            msg = {
                0: (settings.FAKE_SMS_SERVER and
                    "测试期间,短信验证码默认为 1111" or "验证码不正确"),
                1: "没有生成验证码,请重新生成",
                2: "验证码已过期,请重新生成",
                3: "检测过于频繁,于1分钟后再试",
                # 3: "验证码不正确", # "检测过于频繁,于1分钟后再试"这样的信息对于攻击者过于友好.
            }
            # if settings.FAKE_SMS_SERVER is True:
            #     msg[0] = "测试期间,短信验证码默认为 1111"
            return msg.get(code, "未知情况{code}".format(code=code))


class WeiXinToken(BaseModel):
    ACCESS_TOKEN = 1
    JSAPI_TICKET = 2

    TYPE_CHOICES = (
        (ACCESS_TOKEN, 'access_token'),
        (JSAPI_TICKET, 'jsapi_ticket'),
    )

    token = models.CharField(max_length=600, null=False, blank=False)
    created_at = models.DateTimeField(auto_now_add=True)
    expires_in = models.PositiveIntegerField(default=0)
    token_type = models.PositiveIntegerField(
            null=True, blank=True, choices=TYPE_CHOICES)

    def __str__(self):
        return self.token

    def is_token_expired(self):
        now = timezone.now()
        expires_date = self.created_at + datetime.timedelta(
                seconds=(self.expires_in-20))
        delta = expires_date - now
        return delta.total_seconds() <= 0


class Config(BaseModel):
    withdraw_weekday = models.PositiveIntegerField(default=2)

    def __str__(self):
        return 'withdraw on weekday %s' % self.withdraw_weekday


class StaticContent(BaseModel):
    name = models.CharField(max_length=100, null=False, unique=True)
    content = models.TextField()
    updated_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.name


class Evaluation(BaseModel):
    PENDING = 'ep'
    SCHEDULED = 'es'
    COMPLETED = 'ec'
    STATUS_CHOICES = (
        (PENDING, '待处理'),
        (SCHEDULED, '已安排时间'),
        (COMPLETED, '已完成测评')
    )

    order = models.OneToOneField(Order)
    start = models.DateTimeField(null=True, blank=True)
    end = models.DateTimeField(null=True, blank=True)
    status = models.CharField(
        max_length=2,
        choices=STATUS_CHOICES,
        default=PENDING
    )

    @property
    def status_display(self):
        if self.order.status == Order.REFUND:
            return '已退费'
        return self.get_status_display()

    def schedule(self, start_datetime, end_datetime):
        if self.status is not Evaluation.COMPLETED:
            self.start = start_datetime
            self.end = end_datetime
            self.status = Evaluation.SCHEDULED
            self.save()
            return True
        return False

    def complete(self):
        if self.status == Evaluation.SCHEDULED:
            self.status = Evaluation.COMPLETED
            self.save()
            return True
        return False

