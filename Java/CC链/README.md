#### 代码说明

- CC1_POC:

```
ConstantTransformer + InvokerTransformer进行恶意命令执行
AnnotationInvocationHandler + Retention注释 
TransformedMap.decorate进行返回实例类
```

- CC1:

```java
ConstantTransformer + InvokerTransformer进行恶意命令执行
进行动态代理Map
LazyMap的反射创建实例对象
```

- CC6_POC:

```java
ConstantTransformer + InvokerTransformer进行恶意命令执行
LazyMap.decorate直接创建实例对象
TiedMapEntry中的getValue方法调用了get方法
```

- CC3:

```java
TemplatesImpl动态加载字节码
InstantiateTransformer + TrAXFilter触发
AnnotationInvocationHandler + Attributes注释
TransformedMap.decorate
```

- CC3_plus:

```java
TemplatesImpl动态加载字节码
InstantiateTransformer + TrAXFilter触发
DefaultedMap.decorate
TiedMapEntry
```

- CC5:

```java
BadAttributeValueExpException触发他的readObject
    TiedMapEntry#toString方法触发Get方法
    
//要求
//    System.getSecurityManager() == null
```

- CC7:

```java
ConstantTransformer + InvokerTransformer进行恶意命令执行
AbstractMap.equals调用了get方法
其中的yy和zZ的hash值相等
```

- CC2:

```java
CC4版本的链子
ConstantTransformer + InvokerTransformer进行恶意命令执行
TransformingComparator.compare()之后调用了transformer
```

- CC2_plus:

```java
使用的是TemplatesImpl.newTransformer
```



