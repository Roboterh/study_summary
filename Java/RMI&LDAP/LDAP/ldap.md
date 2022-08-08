## 概述

LDAP`(Lightweight Directory Access Protocol)`，轻型目录访问协议，是一个开放的，中立的，工业标准的[应用协议](https://zh.wikipedia.org/w/index.php?title=应用协议&action=edit&redlink=1)，通过[IP协议](https://zh.wikipedia.org/wiki/IP协议)提供访问控制和维护分布式信息的[目录](https://zh.wikipedia.org/wiki/目录_(文件系统))信息。

其实`ldap`的流程与上面的`rmi`基本一致，它主要能储存以下`Java`对象：

1. Java serializable objects
2. Referenceable objects and JNDI References
3. Objects with attributes (DirContext)
4. RMI (Java Remote Method Invocation) objects (including those that use IIOP)
5. CORBA objects