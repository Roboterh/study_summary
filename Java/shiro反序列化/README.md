### 绕过数组

- `JRMP`

  首先在vps上搭建一个JRMP服务端

  ![image-20220416162858794](README/image-20220416162858794-16501090111011.png)

  之后在本地搭建一个JRMP客户端，接受返回的数据

  ![image-20220416163510189](README/image-20220416163510189-16501090374432.png)

  嫖个脚本`exp.py`进行数据的shiro加密处理

  ![image-20220416162933410](README/image-20220416162933410-16501090467353.png)

  ![image-20220416162917513](README/image-20220416162917513-16501090572494.png)

  将返回的exp传入，成功弹出了计算器

  ![image-20220416162741924](README/image-20220416162741924-16501090728445.png)

- `shiro_POC1.java`

  使用`TemplatesImpl#newTransformer`绕过
  只需要使用一个`InvokerTransformer`方法

- `shiro_POC2.java`
  `TrAXFilter.class`+`Templates.class`组合进行漏洞触发