from django.contrib.auth.models import User
from django.db import models

class BaseModel(models.Model):
    class Meta:
        abstract = True

class Region(BaseModel):
    '''
    Provice, City & Distric
    '''
    name = models.CharField(max_length=50)
    superset = models.ForeignKey('Region', blank=True, null=True, default=None,
            on_delete=models.SET_NULL)
    admin_level = models.PositiveIntegerField()
    leaf = models.BooleanField()
    weekly_time_slots = models.ManyToManyField('WeeklyTimeSlot')
    opened = models.BooleanField(default=False)

    def __str__(self):
        return '%s (%d)' % (self.name, self.admin_level)

class School(BaseModel):
    name = models.CharField(max_length=100)
    address = models.CharField(max_length=200)
    thumbnail = models.ImageField(upload_to='schools', null=True, blank=True)
    region = models.ForeignKey(Region, limit_choices_to={'leaf': True})
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

class Level(BaseModel):
    name = models.CharField(max_length=20, unique=True)
    def __str__(self):
        return self.name

class Price(BaseModel):
    region = models.ForeignKey(Region, limit_choices_to={'leaf': True})
    grade = models.ForeignKey(Grade, limit_choices_to={'leaf': True})
    subject = models.ForeignKey(Subject)
    level = models.ForeignKey(Level)
    price = models.PositiveIntegerField()

    class Meta:
        unique_together = ('region', 'grade', 'subject', 'level')

    def __str__(self):
        return '%s,%s,%s,%s => %d' % (self.region, self.grade, self.subject,
                self.level, self.price)

class Role(BaseModel):
    name = models.CharField(max_length=20, unique=True)

    def __str__(self):
        return self.name

class Profile(BaseModel):
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
    phone = models.CharField(max_length=20, default='')
    role = models.ForeignKey(Role, null=True, blank=True, on_delete=models.SET_NULL)
    gender = models.CharField(max_length=1,
            choices=GENDER_CHOICES,
            default=UNKNOWN,
    )
    avatar = models.ImageField(null=True, blank=True, upload_to='avatars')

    def __str__(self):
        return '%s (%s, %s)' % (self.name, self.role, self.gender)

class Teacher(BaseModel):
    DEGREE_CHOICES = (
        ('h', '高中'),
        ('s', '专科'),
        ('b', '本科'),
        ('p', '研究生'),
    )
    user = models.ForeignKey(User)
    name = models.CharField(max_length=200)
    degree = models.CharField(max_length=2,
        choices=DEGREE_CHOICES,
    )
    active = models.BooleanField(default=True)
    fulltime = models.BooleanField(default=True)

    schools = models.ManyToManyField(School)
    weekly_time_slots = models.ManyToManyField('WeeklyTimeSlot')

    def __str__(self):
        return '%s %s %s' % (self.name, 'F' if self.fulltime else '',
                'Banned' if not self.active else '')

class Ability(BaseModel):
    teacher = models.ForeignKey(Teacher)
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)
    level = models.ForeignKey(Level)

    class Meta:
        unique_together = ('teacher', 'grade', 'subject')

    def __str__(self):
        return '%s <%s, %s> : %s' % (self.teacher, self.grade, self.subject,
                self.level)

class Certificate(BaseModel):
    teacher = models.ForeignKey(Teacher)
    name = models.CharField(max_length=100)
    img = models.ImageField(null=True, blank=True, upload_to='certs')
    verified = models.BooleanField()

    def __str__(self):
        return '%s, %s : %s' % (self.teacher, self.name,
                'V' if self.verified else '')

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
    teacher = models.OneToOneField(Teacher)
    balance = models.PositiveIntegerField(default=0)

    def __str__(self):
        return '%s : %d' % (self.teacher, self.balance)

class BankCard(BaseModel):
    bank_name = models.CharField(max_length=100)
    card_number = models.CharField(max_length=100, unique=True)
    account = models.ForeignKey(Account)

    def __str__(self):
        return '%s %s (%s)' % (self.bank_name, self.card_number, self.account.teacher)

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


class AccountHistory(BaseModel):
    account = models.ForeignKey(Account)
    amount = models.PositiveIntegerField()
    bankcard = models.ForeignKey(BankCard)
    submit_time = models.DateTimeField()
    done = models.BooleanField()
    done_by = models.ForeignKey(User, related_name='processed_withdraws', null=True, blank=True)
    done_at = models.DateTimeField()

    def __str__(self):
        return '%s %s : %s' % (self.account.teacher, self.amount,
                'D' if self.done else '')

class Feedback(BaseModel):
    user = models.ForeignKey(User, null=True, blank=True)
    contact = models.CharField(max_length=30)
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s %s %s' % (self.user, self.contact, self.created_at)

class Parent(BaseModel):
    user = models.ForeignKey(User, null=True, blank=True)

    student_name = models.CharField(max_length=50)

    def __str__(self):
        return "%s's parent" % self.student_name

class Coupon(BaseModel):
    parent = models.ForeignKey(Parent)
    name = models.CharField(max_length=50)
    amount = models.PositiveIntegerField()
    created_at = models.DateTimeField(auto_now_add=True)
    expired_at = models.DateTimeField()
    used = models.BooleanField()

    def __str__(self):
        return '%s, %s (%s) %s' % (self.parent, self.amount, self.expired_at,
                'D' if self.used else '')

class WeeklyTimeSlot(BaseModel):
    weekday = models.PositiveIntegerField() # 1 - 7
    start = models.TimeField() # [0:00 - 24:00)
    end = models.TimeField()

    class Meta:
        unique_together = ('weekday', 'start', 'end')

    def __str__(self):
        return '%s from %s to %s' % (self.weekday, self.start, self.end)

class Order(BaseModel):
    PENDING = 'u'
    PAID = 'p'
    CANCLED = 'd'
    CONFIRMED = 'c'
    NOSHOW = 'n'
    STATUS_CHOICES = (
        (PENDING, '待付款'),
        (PAID, '已付款'),
        (CANCLED, '已取消'),
        (NOSHOW, '没出现'),
        (CONFIRMED, '已确认'),
    )

    parent = models.ForeignKey(Parent, null=True)
    teacher = models.ForeignKey(Teacher)
    school = models.ForeignKey(School)
    grade = models.ForeignKey(Grade)
    subject = models.ForeignKey(Subject)
    coupon = models.ForeignKey(Coupon)
    weekly_time_slots = models.ManyToManyField(WeeklyTimeSlot)

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

class TimeSlot(BaseModel):
    order = models.ForeignKey(Order)
    start = models.DateTimeField()
    end = models.DateTimeField()

    confirmed_by = models.ForeignKey(Parent, null=True, blank=True)
    transferred_from = models.ForeignKey('TimeSlot', null=True, blank=True)

    created_at = models.DateTimeField(auto_now_add=True)
    last_updated_at = models.DateTimeField(auto_now=True)
    last_updated_by = models.ForeignKey(User, null=True, blank=True)

    def __str__(self):
        return '%s %s' % (self.date, self.last_updated_by)

class Comment(BaseModel):
    time_slot = models.ForeignKey(TimeSlot)
    ma_degree = models.PositiveIntegerField()
    la_degree = models.PositiveIntegerField()
    content = models.CharField(max_length=500)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return '%s : %d, %d' % (self.time_slot, self.ma_degree, self.la_degree)

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
        return '%s, %s, to %s, %s' % (self.get__type_display(), self.get_via_display(), self.to, self.title)
