#### 基本语法

```json
$gt : >
$lt : <
$gte : >=
$gle : <=
$ne : != <>
$in : in
$nin : not in
$all : all
$or : or
$not : 反匹配，（1.3.3以上的版本具有）
模糊查询用正则式：db.customer.find({'name': {'$regex':'.*s.*'} })
/**

* : 范围查询 { "age" : { "$gte" : 2 , "$lte" : 21}}

* : $ne { "age" : { "$ne" : 23}}

* : $lt { "age" : { "$lt" : 23}}

*/
```



