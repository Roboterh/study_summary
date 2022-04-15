## 反序列RCE

- `c3p0_POC.java`:
  通过URLClassLoader进行远程加载恶意类

- `c3p0_no_network.java`:
  通过`org.apache.naming.factory.BeanFactory` + `EL表达式`进行RCE

- `c3p0_fastjson.java`:
  fastjson利用链

  通过`com.mchange.v2.c3p0.JndiRefForwardingDataSource`类进行JNDI注入

- `c3p0_fastjson2.java`
  fastjson利用链
  通过`com.mchange.v2.c3p0.WrapperConnectionPoolDataSource`类进行不出网利用
  这里是通过的CC4链进行漏洞触发