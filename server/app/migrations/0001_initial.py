# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name='Area',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(max_length=50)),
                ('level', models.PositiveIntegerField()),
                ('leaf', models.BooleanField()),
                ('parent', models.ForeignKey(on_delete=django.db.models.deletion.SET_NULL, blank=True, default=None, null=True, to='app.Area')),
            ],
        ),
        migrations.CreateModel(
            name='AreaGradeSubjectLevelingPrice',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('price', models.PositiveIntegerField()),
                ('area', models.ForeignKey(to='app.Area')),
            ],
        ),
        migrations.CreateModel(
            name='AreaTimeSegment',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('area', models.ForeignKey(to='app.Area')),
            ],
        ),
        migrations.CreateModel(
            name='Balance',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('balance', models.PositiveIntegerField()),
            ],
        ),
        migrations.CreateModel(
            name='BankCard',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('bankName', models.CharField(max_length=100)),
                ('cardNumber', models.CharField(max_length=100)),
            ],
        ),
        migrations.CreateModel(
            name='BankCardRule',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('org_code', models.CharField(max_length=30)),
                ('bank_name', models.CharField(max_length=30)),
                ('card_name', models.CharField(max_length=30)),
                ('card_type', models.CharField(max_length=2)),
                ('card_number_length', models.PositiveIntegerField()),
                ('bin_code_length', models.PositiveIntegerField()),
                ('bin_code', models.CharField(max_length=30)),
            ],
        ),
        migrations.CreateModel(
            name='Certification',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(max_length=100)),
                ('img', models.ImageField(upload_to='')),
                ('verified', models.BooleanField()),
            ],
        ),
        migrations.CreateModel(
            name='Comment',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('ma_degree', models.PositiveIntegerField()),
                ('la_degree', models.PositiveIntegerField()),
                ('content', models.CharField(max_length=500)),
                ('created_at', models.DateTimeField()),
            ],
        ),
        migrations.CreateModel(
            name='Coupon',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(max_length=50)),
                ('amount', models.PositiveIntegerField()),
                ('created_at', models.DateTimeField()),
                ('expired_at', models.DateTimeField()),
                ('used', models.BooleanField()),
            ],
        ),
        migrations.CreateModel(
            name='Course',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('date', models.DateField()),
                ('cancled', models.BooleanField()),
                ('attended', models.BooleanField()),
                ('commented', models.BooleanField()),
                ('confirmed_by', models.CharField(choices=[('s', 'System'), ('h', 'Human')], max_length=1)),
                ('created_at', models.DateTimeField()),
                ('last_updated_at', models.DateTimeField()),
            ],
        ),
        migrations.CreateModel(
            name='Feedback',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('contact', models.CharField(max_length=30)),
                ('content', models.CharField(max_length=500)),
                ('created_at', models.DateTimeField()),
            ],
        ),
        migrations.CreateModel(
            name='Grade',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(max_length=10)),
                ('leaf', models.BooleanField()),
                ('parent', models.ForeignKey(on_delete=django.db.models.deletion.SET_NULL, blank=True, default=None, null=True, to='app.Grade')),
            ],
        ),
        migrations.CreateModel(
            name='GradeSubject',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('grade', models.ForeignKey(to='app.Grade')),
            ],
        ),
        migrations.CreateModel(
            name='Interview',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('created_at', models.DateTimeField()),
                ('reviewed_at', models.DateTimeField()),
                ('review_msg', models.CharField(max_length=1000)),
                ('status', models.CharField(choices=[('t', '待认证'), ('a', '已认证'), ('r', '已拒绝')], max_length=1, default='t')),
            ],
        ),
        migrations.CreateModel(
            name='Leveling',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(choices=[('pr', '初级'), ('md', '中级'), ('se', '高级'), ('pa', '合伙人')], max_length=2)),
            ],
        ),
        migrations.CreateModel(
            name='Message',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('viewed', models.BooleanField()),
                ('deleted', models.BooleanField()),
                ('title', models.CharField(max_length=100)),
                ('content', models.CharField(max_length=1000)),
                ('_type', models.CharField(choices=[('s', '系统消息'), ('f', '收入消息'), ('c', '课程消息'), ('a', '审核消息'), ('m', '评论消息')], max_length=1)),
                ('via', models.CharField(choices=[('s', '短信'), ('m', '邮件'), ('n', '通知栏提醒')], max_length=1)),
                ('created_at', models.DateTimeField()),
            ],
        ),
        migrations.CreateModel(
            name='Order',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('price', models.PositiveIntegerField()),
                ('hours', models.PositiveIntegerField()),
                ('charge_id', models.CharField(max_length=100)),
                ('total', models.PositiveIntegerField()),
                ('created_at', models.DateTimeField()),
                ('paid_at', models.DateTimeField()),
                ('status', models.CharField(choices=[('u', '待付款'), ('p', '已付款'), ('d', '已取消'), ('c', '已完成')], max_length=2)),
                ('coupon', models.ForeignKey(to='app.Coupon')),
                ('grade_subject', models.ForeignKey(to='app.GradeSubject')),
            ],
        ),
        migrations.CreateModel(
            name='Parent',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
            ],
        ),
        migrations.CreateModel(
            name='Person',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(max_length=200, default='')),
                ('gender', models.CharField(choices=[('f', '女'), ('m', '男'), ('u', '未知')], max_length=1, default='u')),
                ('avatar', models.ImageField(upload_to='')),
                ('nGoodComments', models.PositiveIntegerField(default=0)),
                ('nMidComments', models.PositiveIntegerField(default=0)),
                ('nBadComments', models.PositiveIntegerField(default=0)),
            ],
        ),
        migrations.CreateModel(
            name='Role',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(choices=[('su', '超级管理员'), ('ma', '普通管理员'), ('te', '老师'), ('st', '学生'), ('pa', '家长'), ('ca', '出纳'), ('se', '客服'), ('sm', '社区店管理员'), ('cm', '城市管理员')], max_length=2, default='pa')),
            ],
        ),
        migrations.CreateModel(
            name='School',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(max_length=100)),
                ('address', models.CharField(max_length=200)),
                ('thumbnail', models.ImageField(upload_to='schools')),
                ('center', models.BooleanField()),
                ('longitude', models.IntegerField()),
                ('latitude', models.IntegerField()),
                ('area', models.ForeignKey(to='app.Area')),
            ],
        ),
        migrations.CreateModel(
            name='Subject',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(choices=[('ch', '语文'), ('ma', '数学'), ('en', '英语'), ('ph', '物理 '), ('cm', '化学'), ('bi', '生物'), ('hi', '历史'), ('ge', '地理'), ('po', '政治')], max_length=2)),
            ],
        ),
        migrations.CreateModel(
            name='Teacher',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('name', models.CharField(max_length=200)),
                ('degree', models.CharField(choices=[('h', '高中'), ('s', '专科'), ('b', '本科'), ('p', '研究生')], max_length=2)),
                ('active', models.BooleanField()),
                ('fulltime', models.BooleanField()),
                ('grade_subjects', models.ManyToManyField(to='app.GradeSubject')),
                ('leveling', models.ForeignKey(to='app.Leveling')),
                ('person', models.ForeignKey(to='app.Person')),
                ('schools', models.ManyToManyField(to='app.School')),
            ],
        ),
        migrations.CreateModel(
            name='TimeSegment',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('weekday', models.PositiveIntegerField()),
                ('start', models.PositiveIntegerField()),
                ('end', models.PositiveIntegerField()),
            ],
        ),
        migrations.CreateModel(
            name='Withdraw',
            fields=[
                ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True, serialize=False)),
                ('amount', models.PositiveIntegerField()),
                ('submit_time', models.DateTimeField()),
                ('done', models.BooleanField()),
                ('done_at', models.DateTimeField()),
                ('bankcard', models.ForeignKey(to='app.BankCard')),
                ('done_by', models.ForeignKey(to='app.Person', blank=True, null=True, related_name='processed_withdraws')),
                ('person', models.ForeignKey(related_name='my_withdraws', to='app.Person')),
            ],
        ),
        migrations.AddField(
            model_name='person',
            name='role',
            field=models.ForeignKey(on_delete=django.db.models.deletion.SET_NULL, blank=True, to='app.Role', null=True),
        ),
        migrations.AddField(
            model_name='person',
            name='user',
            field=models.OneToOneField(to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='parent',
            name='person',
            field=models.ForeignKey(to='app.Person', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='order',
            name='parent',
            field=models.ForeignKey(null=True, to='app.Parent'),
        ),
        migrations.AddField(
            model_name='order',
            name='school',
            field=models.ForeignKey(to='app.School'),
        ),
        migrations.AddField(
            model_name='order',
            name='teacher',
            field=models.ForeignKey(to='app.Teacher'),
        ),
        migrations.AddField(
            model_name='order',
            name='time_segments',
            field=models.ManyToManyField(to='app.TimeSegment'),
        ),
        migrations.AddField(
            model_name='message',
            name='to',
            field=models.ForeignKey(to='app.Person'),
        ),
        migrations.AddField(
            model_name='interview',
            name='reviewed_by',
            field=models.ForeignKey(to='app.Person'),
        ),
        migrations.AddField(
            model_name='interview',
            name='teacher',
            field=models.ForeignKey(to='app.Teacher'),
        ),
        migrations.AddField(
            model_name='gradesubject',
            name='subject',
            field=models.ForeignKey(to='app.Subject'),
        ),
        migrations.AddField(
            model_name='feedback',
            name='person',
            field=models.ForeignKey(to='app.Person', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='course',
            name='last_updated_by',
            field=models.ForeignKey(to='app.Person', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='course',
            name='order',
            field=models.ForeignKey(to='app.Order'),
        ),
        migrations.AddField(
            model_name='course',
            name='time_segment',
            field=models.ForeignKey(to='app.TimeSegment'),
        ),
        migrations.AddField(
            model_name='course',
            name='transformed_from',
            field=models.ForeignKey(to='app.Course', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='coupon',
            name='person',
            field=models.ForeignKey(to='app.Person'),
        ),
        migrations.AddField(
            model_name='comment',
            name='course',
            field=models.ForeignKey(to='app.Course'),
        ),
        migrations.AddField(
            model_name='certification',
            name='person',
            field=models.ForeignKey(to='app.Person'),
        ),
        migrations.AddField(
            model_name='bankcard',
            name='person',
            field=models.ForeignKey(to='app.Person'),
        ),
        migrations.AddField(
            model_name='balance',
            name='person',
            field=models.OneToOneField(to='app.Person'),
        ),
        migrations.AddField(
            model_name='areatimesegment',
            name='time_segments',
            field=models.ManyToManyField(to='app.TimeSegment'),
        ),
        migrations.AddField(
            model_name='areagradesubjectlevelingprice',
            name='grade_subject',
            field=models.ForeignKey(to='app.GradeSubject'),
        ),
        migrations.AddField(
            model_name='areagradesubjectlevelingprice',
            name='leveling',
            field=models.ForeignKey(to='app.Leveling'),
        ),
    ]
