## 原理

 flask的session是存储在客户端cookie中的，而且flask仅仅对数据进行了签名。众所周知的是，签名的作用是防篡改，而无法防止被读取。而flask并没有提供加密操作，所以其session的全部内容都是可以在客户端读取的，这就可能造成一些安全问题。

## 利用

### session加密

需要有一个`SECRET_KEY`用来签名

```python
python flask_session_cookie_manager3.py encode -s "key" -t "需要伪造的值""
```

### session解密

```python
python flask_session_cookie_manager3.py decode -c "session" -s "key"
```

