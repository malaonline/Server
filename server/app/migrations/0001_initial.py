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
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50, unique=True)),
                ('level', models.PositiveIntegerField()),
                ('leaf', models.BooleanField()),
                ('parent', models.ForeignKey(to='app.Area', blank=True, default=None, null=True, on_delete=django.db.models.deletion.SET_NULL)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='AreaGradeSubjectLevelingPrice',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('price', models.PositiveIntegerField()),
                ('area', models.ForeignKey(to='app.Area')),
            ],
        ),
        migrations.CreateModel(
            name='AreaTimeSegment',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('area', models.ForeignKey(to='app.Area')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Balance',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('balance', models.PositiveIntegerField()),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='BankCard',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('bank_name', models.CharField(max_length=100)),
                ('card_number', models.CharField(max_length=100, unique=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='BankCardRule',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('org_code', models.CharField(max_length=30)),
                ('bank_name', models.CharField(max_length=30)),
                ('card_name', models.CharField(max_length=30)),
                ('card_type', models.CharField(max_length=2)),
                ('card_number_length', models.PositiveIntegerField()),
                ('bin_code_length', models.PositiveIntegerField()),
                ('bin_code', models.CharField(max_length=30)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Certification',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100)),
                ('img', models.ImageField(upload_to='', null=True, blank=True)),
                ('verified', models.BooleanField()),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Comment',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('ma_degree', models.PositiveIntegerField()),
                ('la_degree', models.PositiveIntegerField()),
                ('content', models.CharField(max_length=500)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Coupon',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50)),
                ('amount', models.PositiveIntegerField()),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('expired_at', models.DateTimeField()),
                ('used', models.BooleanField()),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Course',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('date', models.DateField()),
                ('cancled', models.BooleanField()),
                ('attended', models.BooleanField()),
                ('commented', models.BooleanField()),
                ('confirmed_by', models.CharField(max_length=1, choices=[('s', 'System'), ('h', 'Human')])),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('last_updated_at', models.DateTimeField(auto_now=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Feedback',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('contact', models.CharField(max_length=30)),
                ('content', models.CharField(max_length=500)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Grade',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=10, unique=True)),
                ('leaf', models.BooleanField()),
                ('parent', models.ForeignKey(to='app.Grade', blank=True, default=None, null=True, on_delete=django.db.models.deletion.SET_NULL)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='GradeSubject',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('grade', models.ForeignKey(to='app.Grade')),
            ],
        ),
        migrations.CreateModel(
            name='Interview',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('reviewed_at', models.DateTimeField(auto_now=True)),
                ('review_msg', models.CharField(max_length=1000)),
                ('status', models.CharField(default='t', max_length=1, choices=[('t', '待认证'), ('a', '已认证'), ('r', '已拒绝')])),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Leveling',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=2, unique=True, choices=[('pr', '初级'), ('md', '中级'), ('se', '高级'), ('pa', '合伙人')])),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Message',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('viewed', models.BooleanField()),
                ('deleted', models.BooleanField()),
                ('title', models.CharField(max_length=100)),
                ('content', models.CharField(max_length=1000)),
                ('_type', models.CharField(max_length=1, choices=[('s', '系统消息'), ('f', '收入消息'), ('c', '课程消息'), ('a', '审核消息'), ('m', '评论消息')])),
                ('via', models.CharField(max_length=1, choices=[('s', '短信'), ('m', '邮件'), ('n', '通知栏提醒')])),
                ('created_at', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Order',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('price', models.PositiveIntegerField()),
                ('hours', models.PositiveIntegerField()),
                ('charge_id', models.CharField(max_length=100)),
                ('total', models.PositiveIntegerField()),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('paid_at', models.DateTimeField()),
                ('status', models.CharField(max_length=2, choices=[('u', '待付款'), ('p', '已付款'), ('d', '已取消'), ('c', '已完成')])),
                ('coupon', models.ForeignKey(to='app.Coupon')),
                ('grade_subject', models.ForeignKey(to='app.GradeSubject')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Parent',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('student_name', models.CharField(max_length=50)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Person',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(default='', max_length=200)),
                ('gender', models.CharField(default='u', max_length=1, choices=[('f', '女'), ('m', '男'), ('u', '未知')])),
                ('avatar', models.ImageField(upload_to='', null=True, blank=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Role',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(default='pa', max_length=2, unique=True, choices=[('su', '超级管理员'), ('ma', '普通管理员'), ('te', '老师'), ('st', '学生'), ('pa', '家长'), ('ca', '出纳'), ('se', '客服'), ('sm', '社区店管理员'), ('cm', '城市管理员')])),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='School',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100)),
                ('address', models.CharField(max_length=200)),
                ('thumbnail', models.ImageField(upload_to='schools', null=True, blank=True)),
                ('center', models.BooleanField()),
                ('longitude', models.IntegerField()),
                ('latitude', models.IntegerField()),
                ('area', models.ForeignKey(to='app.Area')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Subject',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=2, unique=True, choices=[('ch', '语文'), ('ma', '数学'), ('en', '英语'), ('ph', '物理 '), ('cm', '化学'), ('bi', '生物'), ('hi', '历史'), ('ge', '地理'), ('po', '政治')])),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Teacher',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=200)),
                ('degree', models.CharField(max_length=2, choices=[('h', '高中'), ('s', '专科'), ('b', '本科'), ('p', '研究生')])),
                ('active', models.BooleanField()),
                ('fulltime', models.BooleanField()),
                ('grade_subjects', models.ManyToManyField(to='app.GradeSubject')),
                ('leveling', models.ForeignKey(to='app.Leveling')),
                ('person', models.ForeignKey(to='app.Person')),
                ('schools', models.ManyToManyField(to='app.School')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='TimeSegment',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('weekday', models.PositiveIntegerField()),
                ('start', models.TimeField()),
                ('end', models.TimeField()),
            ],
        ),
        migrations.CreateModel(
            name='Withdraw',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('amount', models.PositiveIntegerField()),
                ('submit_time', models.DateTimeField()),
                ('done', models.BooleanField()),
                ('done_at', models.DateTimeField()),
                ('bankcard', models.ForeignKey(to='app.BankCard')),
                ('done_by', models.ForeignKey(to='app.Person', blank=True, related_name='processed_withdraws', null=True)),
                ('person', models.ForeignKey(related_name='my_withdraws', to='app.Person')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.AlterUniqueTogether(
            name='timesegment',
            unique_together=set([('weekday', 'start', 'end')]),
        ),
        migrations.AddField(
            model_name='person',
            name='role',
            field=models.ForeignKey(to='app.Role', blank=True, null=True, on_delete=django.db.models.deletion.SET_NULL),
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
            field=models.ForeignKey(to='app.Parent', null=True),
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
        migrations.AlterUniqueTogether(
            name='gradesubject',
            unique_together=set([('grade', 'subject')]),
        ),
        migrations.AlterUniqueTogether(
            name='areagradesubjectlevelingprice',
            unique_together=set([('area', 'grade_subject', 'leveling')]),
        ),
    ]
