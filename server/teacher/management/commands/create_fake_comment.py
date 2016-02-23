from django.core.management.base import BaseCommand
from app import models


class Command(BaseCommand):
    help = "Add fake comment to exists time slot"

    def handle(self, *args, **options):
        for order in models.Order.objects.all():
            for timeslot in order.timeslot_set.filter(comment__isnull=True):
                fake_comment = models.Comment()
                fake_comment.score = timeslot.id % 5 + 1
                fake_comment.content = "fake comment {id}".format(id=timeslot.id)
                fake_comment.save()
                timeslot.comment = fake_comment
                timeslot.save()

