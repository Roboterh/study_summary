## 信息收集

### 选择目标

#### Google语法

```bash
intext:教务系统管理平台
intext:职业技术学院
intext:职业技术学院 inurl:login
# 大概率找到主站的ip
```

### C段收集

- Cscan

- fofa语法

  ```bash
  ip="xxx.xxx.xx.xx/24"
  ```

  

### 子域名收集

- [phpinfo](https://phpinfo.me/domain/)

- google语法

  ```java
  site:***.edu.cn
  ```

### 敏感信息收集

- google语法

  ```bash
  filetype:xls site:xx.edu 身份证
  ```

  之后就可以连接vpn，使用`fscan`扫描

### 指纹识别

### js api接口发现

- jsfind
- Packer-Fuzzer

## 实例

### 弱口令

```bash
# 后台弱口令登录文件上传
# Tomcat /manager/html
tomcat:tomcat admin:admin manager:manager 123456 admin123 s3cret
```

### 框架漏洞

使用图标hash来判断网站

```bash
ip.icp="教育" and web.icon="hash"
```

### 使用cve

#### gitlab cve

```bash
web.icon="hash" and ip.icp="教育" and web.body="gitlab"
```

之后将数据导出，使用漏洞利用脚本检测

#### Springboot未授权

```bash
web.body="Whitelabel" and ip.icp="教育" and web.icon="hash"
```

搜取java白页