from rest_framework.renderers import JSONRenderer
class CustomJSONRenderer(JSONRenderer):
    charset = 'utf-8'
