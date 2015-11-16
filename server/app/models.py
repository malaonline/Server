from django.contrib.auth.models import User
from django.db import models

class BaseModel(models.Model):
    class Meta:
        abstract = True

class Area(BaseModel):
    '''
    Provice, City & Distric
    '''
    name = models.CharField(max_length=50, unique=True)
    parent = models.ForeignKey('Area', blank=True, null=True, default=None,
            on_delete=models.SET_NULL)
    level = models.PositiveIntegerField()
    leaf = models.BooleanField()

class School(BaseModel):
    name = models.CharField(max_length=100)
    address = models.CharField(max_length=200)
    thumbnail = models.ImageField(upload_to='schools')
    area = models.ForeignKey(Area)
    center = models.BooleanField()
    longitude = models.IntegerField()
    latitude = models.IntegerField()

class Grade(BaseModel):
    name = models.CharField(max_length=10, unique=True)
    parent = models.ForeignKey('Grade', blank=True, null=True, default=None,
            on_delete=models.SET_NULL)
    leaf = models.BooleanField()

class Subject(BaseModel):
    CHINESE = 'ch'
    MATH = 'ma'
    ENGLISH = 'en'

    PHYSICS = 'ph'
    CHEMICAL = 'cm'
    BIOLOGY = 'bi'

    HISTORY = 'hi'
    GEOGOROPHY = 'ge'
    POLITICS = 'po'

    SUBJECT_CHOICES = (
        (CHINESE, '语文'),
        (MATH, '数学'),
        (ENGLISH, '英语'),
        (PHYSICS, '物理 '),
        (CHEMICAL, '化学'),
        (BIOLOGY, '生物'),
        (HISTORY, '历史'),
        (GEOGOROPHY, '地理'),
        (POLITICS, '政治'),
    )

    name = models.CharField(max_length=2, unique=True,
        choices=SUBJECT_CHOICES,
    )

class GradeSubject(BaseModel):
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)

    class Meta:
        unique_together = ('grade', 'subject')

class Leveling(BaseModel):
    PRIMARY = 'pr'
    MIDDLING = 'md'
    SENIOR = 'se'
    PARTNER = 'pa'

    LEVELING_CHOICES = (
        (PRIMARY, '初级'),
        (MIDDLING, '中级'),
        (SENIOR, '高级'),
        (PARTNER, '合伙人'),
    )

    name = models.CharField(max_length=2, unique=True,
        choices=LEVELING_CHOICES
    )
    def __str__(self):
        return self.get_name_display()

class AreaGradeSubjectLevelingPrice(BaseModel):
    area = models.ForeignKey(Area)
    grade_subject = models.ForeignKey(GradeSubject)
    leveling = models.ForeignKey(Leveling)
    price = models.PositiveIntegerField()

    class Meta:
        unique_together = ('area', 'grade_subject', 'leveling')

class Role(BaseModel):
    SUPERUSER = 'su'
    MANAGER = 'ma'
    TEACHER = 'te'
    STUDENT = 'st'
    PARENT = 'pa'
    CASHIER = 'ca'
    CUSTOMESERVICE = 'se'
    SCHOOLMANAGER = 'sm'
    CITYMANAGER = 'cm'
    ROLE_CHOICES = (
        (SUPERUSER, '超级管理员'),
        (MANAGER, '普通管理员'),
        (TEACHER, '老师'),
        (STUDENT, '学生'),
        (PARENT, '家长'),
        (CASHIER, '出纳'),
        (CUSTOMESERVICE, '客服'),
        (SCHOOLMANAGER, '社区店管理员'),
        (CITYMANAGER, '城市管理员'),
    )
    name = models.CharField(max_length=2, unique=True, choices=ROLE_CHOICES,
            default=PARENT)

class Person(BaseModel):
    '''
    For extending the system class: User
    '''
    MALE = 'm'
    FEMALE = 'f'
    UNKNOWN = 'u'
    GENDER_CHOICES = (
        (FEMALE, '女'),
        (MALE, '男'),
        (UNKNOWN, '未知'),
    )

    user = models.OneToOneField(User)
    name = models.CharField(max_length=200, default='')
    role = models.ForeignKey(Role, null=True, blank=True, on_delete=models.SET_NULL)
    gender = models.CharField(max_length=1,
            choices=GENDER_CHOICES,
            default=UNKNOWN,
    )
    avatar = models.ImageField()

    nGoodComments = models.PositiveIntegerField(default=0)
    nMidComments = models.PositiveIntegerField(default=0)
    nBadComments = models.PositiveIntegerField(default=0)

class Teacher(BaseModel):
    DEGREE_CHOICES = (
        ('h', '高中'),
        ('s', '专科'),
        ('b', '本科'),
        ('p', '研究生'),
    )
    person = models.ForeignKey(Person)
    name = models.CharField(max_length=200)
    degree = models.CharField(max_length=2,
        choices=DEGREE_CHOICES,
    )

    active = models.BooleanField()

    fulltime = models.BooleanField()

    schools = models.ManyToManyField(School)

    leveling = models.ForeignKey(Leveling)

    grade_subjects = models.ManyToManyField(GradeSubject)

class Certification(BaseModel):
    person = models.ForeignKey(Person)
    name = models.CharField(max_length=100)
    img = models.ImageField()
    verified = models.BooleanField()

class Interview(BaseModel):
    TOAPPROVE = 't'
    APPROVED = 'a'
    REJECTED = 'r'
    STATUS_CHOICES = (
        (TOAPPROVE, '待认证'),
        (APPROVED, '已认证'),
        (REJECTED, '已拒绝'),
    )

    teacher = models.ForeignKey(Teacher)
    created_at = models.DateTimeField()
    reviewed_at = models.DateTimeField()
    reviewed_by = models.ForeignKey(Person)
    review_msg = models.CharField(max_length=1000)
    status = models.CharField(max_length=1,
            choices=STATUS_CHOICES,
            default=TOAPPROVE)

class BankCard(BaseModel):
    bankName = models.CharField(max_length=100)
    cardNumber = models.CharField(max_length=100, unique=True)
    person = models.ForeignKey(Person)

class BankCardRule(BaseModel):
    org_code = models.CharField(max_length=30)
    bank_name = models.CharField(max_length=30)
    card_name = models.CharField(max_length=30)
    card_type = models.CharField(max_length=2)
    card_number_length = models.PositiveIntegerField()
    bin_code_length = models.PositiveIntegerField()
    bin_code = models.CharField(max_length=30)

class Balance(BaseModel):
    person = models.OneToOneField(Person)
    balance = models.PositiveIntegerField()

class Withdraw(BaseModel):
    person = models.ForeignKey(Person, related_name='my_withdraws')
    amount = models.PositiveIntegerField()
    bankcard = models.ForeignKey(BankCard)
    submit_time = models.DateTimeField()
    done = models.BooleanField()
    done_by = models.ForeignKey(Person, related_name='processed_withdraws', null=True, blank=True)
    done_at = models.DateTimeField()

class Feedback(BaseModel):
    person = models.ForeignKey(Person, null=True, blank=True)
    contact = models.CharField(max_length=30)
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField()

class Parent(BaseModel):
    person = models.ForeignKey(Person, null=True, blank=True)

    student_name = models.CharField(max_length=50)


class Coupon(BaseModel):
    person = models.ForeignKey(Person)
    name = models.CharField(max_length=50)
    amount = models.PositiveIntegerField()
    created_at = models.DateTimeField()
    expired_at = models.DateTimeField()
    used = models.BooleanField()


class TimeSegment(BaseModel):
    weekday = models.PositiveIntegerField()
    start = models.PositiveIntegerField()
    end = models.PositiveIntegerField()

    class Meta:
        unique_together = ('weekday', 'start', 'end')


class AreaTimeSegment(BaseModel):
    area = models.ForeignKey(Area)
    time_segments = models.ManyToManyField(TimeSegment)

class Order(BaseModel):
    UNPAID = 'u'
    PAID = 'p'
    CANCLED = 'd'
    COMPLETED = 'c'
    STATUS_CHOICES = (
        (UNPAID, '待付款'),
        (PAID, '已付款'),
        (CANCLED, '已取消'),
        (COMPLETED, '已完成'),
    )

    parent = models.ForeignKey(Parent, null=True)
    teacher = models.ForeignKey(Teacher)
    school = models.ForeignKey(School)
    grade_subject = models.ForeignKey(GradeSubject)
    coupon = models.ForeignKey(Coupon)
    time_segments = models.ManyToManyField(TimeSegment)

    price = models.PositiveIntegerField()
    hours = models.PositiveIntegerField()
    charge_id = models.CharField(max_length=100) # For Ping++ use
    total = models.PositiveIntegerField()

    created_at = models.DateTimeField()
    paid_at = models.DateTimeField()

    status = models.CharField(max_length=2,
        choices=STATUS_CHOICES,
    )

class Course(BaseModel):
    CONFIRMED_CHOICES = (
        ('s', 'System'),
        ('h', 'Human'),
    )
    order = models.ForeignKey(Order)
    date = models.DateField()
    time_segment = models.ForeignKey(TimeSegment)

    cancled = models.BooleanField()
    attended = models.BooleanField()
    commented = models.BooleanField()
    confirmed_by = models.CharField(max_length=1,
        choices=CONFIRMED_CHOICES,
    )
    transformed_from = models.ForeignKey('Course', null=True, blank=True)

    created_at = models.DateTimeField()
    last_updated_at = models.DateTimeField()
    last_updated_by = models.ForeignKey(Person, null=True, blank=True)

class Comment(BaseModel):
    course = models.ForeignKey(Course)
    ma_degree = models.PositiveIntegerField()
    la_degree = models.PositiveIntegerField()
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField()

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

    to = models.ForeignKey(Person)
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
    created_at = models.DateTimeField()

