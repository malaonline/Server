import time
import base64
import random

from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA256

from django.utils import timezone


def to_timestamp(tz_time):
    timestamp = time.mktime(tz_time.timetuple())
    return int(timestamp)


def timestamp():
    now = timezone.now()
    return to_timestamp(now)


def orderid():
    '''
    This method does NOT ensure unique id
    '''
    begin = 1340861754.0
    ans = timestamp() - begin
    assert ans > 0
    rand = random.randint(0, 999999)
    ans = int('%d%06d' % (ans, rand))
    ans = '%d%d' % (ans, ans % 7)
    return ans


def decode_base64(data):
    missing_padding = 4 - len(data) % 4
    if missing_padding:
        data += b'='*missing_padding
    return base64.decodestring(data)


def verify_sig(body, sig, pub_key):
    '''
    body: binary
    sig: binary
    pubkey: binary
    '''
    sig = decode_base64(sig)
    digest = SHA256.new(body)
    pubkey = RSA.importKey(pub_key)
    pkcs = PKCS1_v1_5.new(pubkey)
    return pkcs.verify(digest, sig)


class Node:
    def __init__(self, val):
        self.val = val
        self.left = None
        self.right = None


class Tree:
    def __init__(self):
        self.root = None
        self.last = None
        self.current = None

    def get_val(self, val, node: Node = None)->Node:
        if not node:
            node = self.root
        if node.val == val:
            return node
        if node.left:
            ret = self.get_val(val, node.left)
            if ret:
                return ret
        if node.right:
            ret = self.get_val(val, node.right)
            if ret:
                return ret
        return None

    def insert_val(self, val, left=None, right=None):
        node = self.get_val(val)
        if node:
            if left:
                node.left = Node(left)
            if right:
                node.right = Node(right)

    def _get_path(self, val, node, path):
        path.append(node.val)
        if node.val == val:
            return path, True
        else:
            if node.left:
                new_path, ret = self._get_path(val, node.left, path)
                if ret is True:
                    return new_path, True
            if node.right:
                new_path, ret = self._get_path(val, node.right, path)
                if ret is True:
                    return new_path, True
        path.pop()
        return path, False

    def get_path(self, val)->[]:
        return self._get_path(val, self.root, [])[0]
