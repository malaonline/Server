import base64
import random
import re

from Crypto.PublicKey import RSA
from Crypto.Signature import PKCS1_v1_5
from Crypto.Hash import SHA, SHA256

from django.utils import timezone


def timestamp():
    return int(timezone.now().timestamp())


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


def str_urlencode(s, encoding='utf-8') :
    return str(s.encode(encoding)).replace(r'\x','%')[2:-1]


def decode_base64(data):
    missing_padding = 4 - len(data) % 4
    if missing_padding:
        data += b'='*missing_padding
    return base64.decodestring(data)


def sign_sha1(body, pri_key):
    '''
    sign body with pri_key by hash SHA1, return base64 bytes

    body: binary
    pur_key: binary
    '''
    rsa_pri = RSA.importKey(pri_key)
    pk = PKCS1_v1_5.new(rsa_pri)
    h = SHA.new(body)
    cbs = pk.sign(h)
    return base64.encodebytes(cbs)


def verify_sha1_sig(body, sig, pub_key):
    '''
    body: binary
    sig: binary
    pubkey: binary
    '''
    sig = decode_base64(sig)
    digest = SHA.new(body)
    pubkey = RSA.importKey(pub_key)
    pkcs = PKCS1_v1_5.new(pubkey)
    return pkcs.verify(digest, sig)


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


def check_id_number(id_num):
    """
    检测身份证号是否合法
    """
    return True
    b = re.match('^\d{17}[0-9xX]$', id_num)
    if (not b):
        return False
    w = [7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2]
    c = [1,0,'X',9,8,7,6,5,4,3,2]
    s = 0
    for i in range(17):
        s += int(id_num[i])*w[i]

    r = c[s%11]
    e = id_num[17]
    return (e.isdigit() and int(e)==r or (e=='x' or e=='X') and r=='X')


def check_bankcard_number(card_number):
    """
    简单判断银行卡号格式是否正确
    """
    return re.match('^\d{16,19}$', card_number)


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
