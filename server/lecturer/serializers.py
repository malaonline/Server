from rest_framework import serializers

# local modules
from app import models


class QuestionGroupSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.QuestionGroup
        fields = ('id', 'title', 'desc')


class QuestionSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Question
        fields = ('id', 'title', 'solution', 'analyse')
