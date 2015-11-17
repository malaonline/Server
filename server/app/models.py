from django.contrib.auth.models import User
from django.db import models

class BaseModel(models.Model):
    class Meta:
        abstract = True

class Region(BaseModel):
    '''
    Provice, City & Distric
    '''
    name = models.CharField(max_length=50, unique=True)
    superset = models.ForeignKey('Region', blank=True, null=True, default=None,
            on_delete=models.SET_NULL)
    admin_level = models.PositiveIntegerField()
    leaf = models.BooleanField()

    def __str__(self):
        return '%s (%d)' % (self.name, self.admin_level)

class School(BaseModel):
    name = models.CharField(max_length=100)
    address = models.CharField(max_length=200)
    thumbnail = models.ImageField(upload_to='schools', null=True, blank=True)
    region = models.ForeignKey(Region)
    center = models.BooleanField()
    longitude = models.IntegerField()
    latitude = models.IntegerField()

    def __str__(self):
        return '%s %s %s' % (self.region, self.name, 'C' if self.center else '')

class Grade(BaseModel):
    name = models.CharField(max_length=10, unique=True)
    superset = models.ForeignKey('Grade', blank=True, null=True, default=None,
            on_delete=models.SET_NULL)
    leaf = models.BooleanField()

    def __str__(self):
        return self.name

class Subject(BaseModel):
    name = models.CharField(max_length=10, unique=True)

    def __str__(self):
        return self.name

class GradeSubject(BaseModel):
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)

    class Meta:
        unique_together = ('grade', 'subject')

    def __str__(self):
        return '%s%s' % (self.grade, self.subject)

class Level(BaseModel):
    name = models.CharField(max_length=20, unique=True)
    def __str__(self):
        return self.name

class RegionGradeSubjectLevelPrice(BaseModel):
    region = models.ForeignKey(Region)
    grade_subject = models.ForeignKey(GradeSubject)
    level = models.ForeignKey(Level)
    price = models.PositiveIntegerField()

    class Meta:
        unique_together = ('region', 'grade_subject', 'level')

    def __str__(self):
        return '%s%s%s => %d' % (self.region, self.grade_subject, self.level,
                self.price)

class Role(BaseModel):
    name = models.CharField(max_length=20, unique=True)

    def __str__(self):
        return self.name

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
    avatar = models.ImageField(null=True, blank=True)

    def __str__(self):
        return '%s (%s, %s)' % (self.name, self.role, self.gender)

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

    level = models.ForeignKey(Level)

    grade_subjects = models.ManyToManyField(GradeSubject)

    def __str__(self):
        return '%s %s %s' % (self.name, 'F' if self.fulltime else '',
                'Banned' if not self.active else '')

class Certification(BaseModel):
    person = models.ForeignKey(Person)
    name = models.CharField(max_length=100)
    img = models.ImageField(null=True, blank=True)
    verified = models.BooleanField()

    def __str__(self):
        return '%s, %s : %s' % (self.person, self.name,
                'V' if self.verified else '')

class InterviewRecord(BaseModel):
    TOAPPROVE = 't'
    APPROVED = 'a'
    REJECTED = 'r'
    STATUS_CHOICES = (
        (TOAPPROVE, '待认证'),
        (APPROVED, '已认证'),
        (REJECTED, '已拒绝'),
    )

    teacher = models.ForeignKey(Teacher)
    created_at = models.DateTimeField(auto_now_add=True)
    reviewed_at = models.DateTimeField(auto_now=True)
    reviewed_by = models.ForeignKey(Person)
    review_msg = models.CharField(max_length=1000)
    status = models.CharField(max_length=1,
            choices=STATUS_CHOICES,
            default=TOAPPROVE)

    def __str__(self):
        return '%s by %s' % (self.teacher, self.reviewed_by)

class BankCard(BaseModel):
    bank_name = models.CharField(max_length=100)
    card_number = models.CharField(max_length=100, unique=True)
    person = models.ForeignKey(Person)

    def __str__(self):
        return '%s %s (%s)' % (self.bank_name, self.card_number, self.person)

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

class Balance(BaseModel):
    person = models.OneToOneField(Person)
    balance = models.PositiveIntegerField()

    def __str__(self):
        return '%s : %d' % (self.person, self.balance)

class Withdraw(BaseModel):
    person = models.ForeignKey(Person, related_name='my_withdraws')
    amount = models.PositiveIntegerField()
    bankcard = models.ForeignKey(BankCard)
    submit_time = models.DateTimeField()
    done = models.BooleanField()
    done_by = models.ForeignKey(Person, related_name='processed_withdraws', null=True, blank=True)
    done_at = models.DateTimeField()

    def __str__(self):
        return '%s %s : %s' % (self.person, self.amount,
                'D' if self.done else '')

class Feedback(BaseModel):
    person = models.ForeignKey(Person, null=True, blank=True)
    contact = models.CharField(max_length=30)
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s %s %s' % (self.person, self.contact, self.created_at)

class Parent(BaseModel):
    person = models.ForeignKey(Person, null=True, blank=True)

    student_name = models.CharField(max_length=50)

    def __str__(self):
        return "%s's parent" % self.student_name

class Coupon(BaseModel):
    person = models.ForeignKey(Person)
    name = models.CharField(max_length=50)
    amount = models.PositiveIntegerField()
    created_at = models.DateTimeField(auto_now_add=True)
    expired_at = models.DateTimeField()
    used = models.BooleanField()

    def __str__(self):
        return '%s, %s (%s) %s' % (self.person, self.amount, self.expired_at,
                'D' if self.used else '')

class TimeTable(BaseModel):
    weekday = models.PositiveIntegerField()
    start = models.TimeField()
    end = models.TimeField()

    class Meta:
        unique_together = ('weekday', 'start', 'end')

    def __str__(self):
        return '%s from %s to %s' % (self.weekday, self.start, self.end)


class RegionTimeTable(BaseModel):
    region = models.ForeignKey(Region)
    time_tables = models.ManyToManyField(TimeTable)

    def __str__(self):
        return self.region

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
    time_tables = models.ManyToManyField(TimeTable)

    price = models.PositiveIntegerField()
    hours = models.PositiveIntegerField()
    charge_id = models.CharField(max_length=100) # For Ping++ use
    total = models.PositiveIntegerField()

    created_at = models.DateTimeField(auto_now_add=True)
    paid_at = models.DateTimeField()

    status = models.CharField(max_length=2,
        choices=STATUS_CHOICES,
    )

    def __str__(self):
        return '%s %s %s %s : %s' % (self.school, self.parent, self.teacher,
                self.grade_subect, self.total)

class PlannedCourse(BaseModel):
    CONFIRMED_CHOICES = (
        ('s', 'System'),
        ('h', 'Human'),
    )
    order = models.ForeignKey(Order)
    date = models.DateField()
    time_table = models.ForeignKey(TimeTable)

    cancled = models.BooleanField()
    attended = models.BooleanField()
    commented = models.BooleanField()
    confirmed_by = models.CharField(max_length=1,
        choices=CONFIRMED_CHOICES,
    )
    transformed_from = models.ForeignKey('PlannedCourse', null=True,
            blank=True)

    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(Person, null=True, blank=True)

    def __str__(self):
        return '%s %s' % (self.date, self.last_updated_by)

class Comment(BaseModel):
    course = models.ForeignKey(PlannedCourse)
    ma_degree = models.PositiveIntegerField()
    la_degree = models.PositiveIntegerField()
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s : %d, %d' % (self.course, self.ma_degree, self.la_degree)

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
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s, %s, to %s, %s' % (self.get__type_display(), self.get_via_display(), self.to, self.title)
