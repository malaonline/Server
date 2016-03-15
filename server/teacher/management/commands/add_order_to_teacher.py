from django.core.management.base import BaseCommand
from app import models


class Command(BaseCommand):
    help = "Add order to teacher"

    def add_arguments(self, parser):
        parser.add_argument("teacher_name", nargs=1)
        parser.add_argument("student_id", nargs=1, type=int)

    def handle(self, *args, **options):
        teacher_name = options["teacher_name"][0]
        student_id = options["student_id"]
        teacher = models.Teacher.objects.get(name=teacher_name)
        parent = models.Parent.objects.get(pk=student_id)
        school = models.School.objects.all()[0]
        ability = teacher.abilities.all()[0]
        models.Order(parent=parent, teacher=teacher, school=school,
                     grade=ability.grade, subject=ability.subject,

                     )

