from django.db import models

class Teacher(models.Model):
    name = models.CharField(max_length=200)

class Parent(models.Model):
    name = models.CharField(max_length=200)
