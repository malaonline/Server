import uuid


def random_string():
    return str(uuid.uuid4())


class classproperty(object):
    def __init__(self, getter):
        self.getter = getter

    def __get__(self, instance, owner):
        return self.getter(owner)
