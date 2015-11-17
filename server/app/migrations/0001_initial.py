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
            name='Balance',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('balance', models.PositiveIntegerField()),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='BankCard',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('bank_name', models.CharField(max_length=100)),
                ('card_number', models.CharField(unique=True, max_length=100)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='BankCodeInfo',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
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
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(max_length=100)),
                ('img', models.ImageField(blank=True, null=True, upload_to='')),
                ('verified', models.BooleanField()),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Comment',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
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
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
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
            name='Feedback',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
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
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(unique=True, max_length=10)),
                ('leaf', models.BooleanField()),
                ('superset', models.ForeignKey(to='app.Grade', null=True, on_delete=django.db.models.deletion.SET_NULL, blank=True, default=None)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='GradeSubject',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('grade', models.ForeignKey(to='app.Grade')),
            ],
        ),
        migrations.CreateModel(
            name='Interview',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('reviewed_at', models.DateTimeField(auto_now=True)),
                ('review_msg', models.CharField(max_length=1000)),
                ('status', models.CharField(default='t', choices=[('t', '待认证'), ('a', '已认证'), ('r', '已拒绝')], max_length=1)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Level',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(unique=True, choices=[('pr', '初级'), ('md', '中级'), ('se', '高级'), ('pa', '合伙人')], max_length=2)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Message',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('viewed', models.BooleanField()),
                ('deleted', models.BooleanField()),
                ('title', models.CharField(max_length=100)),
                ('content', models.CharField(max_length=1000)),
                ('_type', models.CharField(choices=[('s', '系统消息'), ('f', '收入消息'), ('c', '课程消息'), ('a', '审核消息'), ('m', '评论消息')], max_length=1)),
                ('via', models.CharField(choices=[('s', '短信'), ('m', '邮件'), ('n', '通知栏提醒')], max_length=1)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Order',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('price', models.PositiveIntegerField()),
                ('hours', models.PositiveIntegerField()),
                ('charge_id', models.CharField(max_length=100)),
                ('total', models.PositiveIntegerField()),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('paid_at', models.DateTimeField()),
                ('status', models.CharField(choices=[('u', '待付款'), ('p', '已付款'), ('d', '已取消'), ('c', '已完成')], max_length=2)),
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
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('student_name', models.CharField(max_length=50)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Person',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(default='', max_length=200)),
                ('gender', models.CharField(default='u', choices=[('f', '女'), ('m', '男'), ('u', '未知')], max_length=1)),
                ('avatar', models.ImageField(blank=True, null=True, upload_to='')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='PlannedCourse',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('date', models.DateField()),
                ('cancled', models.BooleanField()),
                ('attended', models.BooleanField()),
                ('commented', models.BooleanField()),
                ('confirmed_by', models.CharField(choices=[('s', 'System'), ('h', 'Human')], max_length=1)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('last_updated_at', models.DateTimeField(auto_now=True)),
                ('last_updated_by', models.ForeignKey(to='app.Person', null=True, blank=True)),
                ('order', models.ForeignKey(to='app.Order')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Region',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(unique=True, max_length=50)),
                ('admin_level', models.PositiveIntegerField()),
                ('leaf', models.BooleanField()),
                ('superset', models.ForeignKey(to='app.Region', null=True, on_delete=django.db.models.deletion.SET_NULL, blank=True, default=None)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='RegionGradeSubjectLevelPrice',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('price', models.PositiveIntegerField()),
                ('grade_subject', models.ForeignKey(to='app.GradeSubject')),
                ('level', models.ForeignKey(to='app.Level')),
                ('region', models.ForeignKey(to='app.Region')),
            ],
        ),
        migrations.CreateModel(
            name='RegionTimeTable',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('region', models.ForeignKey(to='app.Region')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Role',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(unique=True, default='pa', choices=[('su', '超级管理员'), ('ma', '普通管理员'), ('te', '老师'), ('st', '学生'), ('pa', '家长'), ('ca', '出纳'), ('se', '客服'), ('sm', '社区店管理员'), ('cm', '城市管理员')], max_length=2)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='School',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(max_length=100)),
                ('address', models.CharField(max_length=200)),
                ('thumbnail', models.ImageField(blank=True, null=True, upload_to='schools')),
                ('center', models.BooleanField()),
                ('longitude', models.IntegerField()),
                ('latitude', models.IntegerField()),
                ('region', models.ForeignKey(to='app.Region')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Subject',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(unique=True, choices=[('ch', '语文'), ('ma', '数学'), ('en', '英语'), ('ph', '物理 '), ('cm', '化学'), ('bi', '生物'), ('hi', '历史'), ('ge', '地理'), ('po', '政治')], max_length=2)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='Teacher',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('name', models.CharField(max_length=200)),
                ('degree', models.CharField(choices=[('h', '高中'), ('s', '专科'), ('b', '本科'), ('p', '研究生')], max_length=2)),
                ('active', models.BooleanField()),
                ('fulltime', models.BooleanField()),
                ('grade_subjects', models.ManyToManyField(to='app.GradeSubject')),
                ('level', models.ForeignKey(to='app.Level')),
                ('person', models.ForeignKey(to='app.Person')),
                ('schools', models.ManyToManyField(to='app.School')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='TimeTable',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('weekday', models.PositiveIntegerField()),
                ('start', models.TimeField()),
                ('end', models.TimeField()),
            ],
        ),
        migrations.CreateModel(
            name='Withdraw',
            fields=[
                ('id', models.AutoField(primary_key=True, serialize=False, verbose_name='ID', auto_created=True)),
                ('amount', models.PositiveIntegerField()),
                ('submit_time', models.DateTimeField()),
                ('done', models.BooleanField()),
                ('done_at', models.DateTimeField()),
                ('bankcard', models.ForeignKey(to='app.BankCard')),
                ('done_by', models.ForeignKey(to='app.Person', related_name='processed_withdraws', null=True, blank=True)),
                ('person', models.ForeignKey(to='app.Person', related_name='my_withdraws')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.AlterUniqueTogether(
            name='timetable',
            unique_together=set([('weekday', 'start', 'end')]),
        ),
        migrations.AddField(
            model_name='regiontimetable',
            name='time_tables',
            field=models.ManyToManyField(to='app.TimeTable'),
        ),
        migrations.AddField(
            model_name='plannedcourse',
            name='time_table',
            field=models.ForeignKey(to='app.TimeTable'),
        ),
        migrations.AddField(
            model_name='plannedcourse',
            name='transformed_from',
            field=models.ForeignKey(to='app.PlannedCourse', null=True, blank=True),
        ),
        migrations.AddField(
            model_name='person',
            name='role',
            field=models.ForeignKey(to='app.Role', null=True, on_delete=django.db.models.deletion.SET_NULL, blank=True),
        ),
        migrations.AddField(
            model_name='person',
            name='user',
            field=models.OneToOneField(to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='parent',
            name='person',
            field=models.ForeignKey(to='app.Person', null=True, blank=True),
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
            name='time_tables',
            field=models.ManyToManyField(to='app.TimeTable'),
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
            field=models.ForeignKey(to='app.Person', null=True, blank=True),
        ),
        migrations.AddField(
            model_name='coupon',
            name='person',
            field=models.ForeignKey(to='app.Person'),
        ),
        migrations.AddField(
            model_name='comment',
            name='course',
            field=models.ForeignKey(to='app.PlannedCourse'),
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
        migrations.AlterUniqueTogether(
            name='regiongradesubjectlevelprice',
            unique_together=set([('region', 'grade_subject', 'level')]),
        ),
        migrations.AlterUniqueTogether(
            name='gradesubject',
            unique_together=set([('grade', 'subject')]),
        ),
    ]
