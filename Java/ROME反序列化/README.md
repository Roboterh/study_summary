调用链：

```java
HashMap.readObject()
    ObjectBean.hashCode()
            EqualsBean.beanHashCode()
                ObjectBean.toString()
                    ToStringBean.toString()
                        TemplatesImpl.getOutputProperties()
```

利用`HashMap`反序列化来触发`ObjectBean#hashCode`方法, 接着在`EqualsBean#beanHashCode`会进一步调用`ObjectBean`封装的`Object#toString`方法, 从而调用的`ToStringBean#toString`方法, 在第二个`toString`方法中触发`pReadMethod#invoke`, 从而达到恶意反序列化的操作.

POC：

`Rome_POC.java`

#### bypass

##### 禁用HashMap

这里的HashMap同样可以使用HashSet或者Hashtable来绕过

##### spring不出网

我们就需要加载一个恶意类，进行回显

恶意类：`SpringEvil.java`

这里提供两个小工具`BaseToClass.java` `ClassToBase.java`，是class文件和base64编码的相互转换

使用`Rome_bypass_noShow.java`生成payload

##### 缩短payload

- `RomeShorter.java`采用了asm和缩短TemplateImpl，和很短的EqualsBean链触发TemplatesImpl链
- `Rome_shorter2.java`采用了ysoserial项目的链子进行缩短
- `Rome_shorter3.java`采用了`BadAttributeValueExpException`的链子进行缩短