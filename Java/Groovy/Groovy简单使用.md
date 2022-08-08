## 概述

Groovy是用于Java虚拟机的一种敏捷的动态语言，它是一种成熟的面向对象编程语言，既可以用于面向对象编程，又可以用作纯粹的脚本语言。使用该种语言不必编写过多的代码，同时又具有闭包和动态语言中的其他特性。

Groovy是JVM的一个替代语言（替代是指可以用 Groovy 在Java平台上进行 Java 编程），使用方式基本与使用 Java代码的方式相同，该语言特别适合与Spring的动态语言支持一起使用，设计时充分考虑了Java集成，这使 Groovy 与 Java 代码的互操作很容易。（注意：不是指Groovy替代java，而是指Groovy和java很好的结合编程。

## 基本语法

### 前置

他的类是以`.groovy`结尾的代码，只需要在maven中导入`groovy`的坐标就可以使用

```java
package pers.groovy

class GroovyTest {
    static void main(String[] args) {
        println("Hello, groovy");
    }
}

```

#### 变量

可以使用def进行定义变量

```java
package pers.groovy

class GroovyTest {
    static void main(String[] args) {
        def _name = 'Json';
        println(_name);
    }
}
```

#### 运算符

- 范围运算符，支持`def range = 0..5`格式

  ```java
  package pers.groovy
  
  class GroovyTest {
      static void main(String[] args) {
          def range = 0..5;
          println(range);
          println(range.get(2));
      }
  }
  // [0, 1, 2, 3, 4, 5]
  // 2
  ```

#### 方法

Groovy 中的方法是使用返回类型或使用 def 关键字定义的。方法可以接收任意数量的参数。定义参数时，不必显式定义类型。可以添加修饰符，如 public，private 和 protected。默认情况下，如果未提供可见性修饰符，则该方法为 public。

```java
class Example {
   static def DisplayName() {
      println("This is how methods work in groovy");
      println("This is an example of a simple method");
   } 
	
   static void main(String[] args) {
      DisplayName();
   } 
}
```

- 实例方法

- ```java
  package pers.groovy
  
  class GroovyTest {
      int x;
  
      public int getX() {
          return x;
      }
  
      public void setX(int pX) {
          x = pX;
      }
      static void main(String[] args) {
          GroovyTest ex = new GroovyTest();
          ex.setX(100);
          println(ex.getX());
      }
  }
  ```

#### 文件

```java
package pers.groovy

class GroovyTest {
    static void main(String[] args) {
        //写入文件
//        new File("E:/", "Example.txt").withWriter("utf-8") {
//            writer -> writer.writeLine("Line1: line1 \nLine2: line2");
//        }

        //读文件
//        new File("E:/", "Example.txt").eachLine {
//            line -> println("line: $line");
//        }

        //将文件内容以字符串方式获取
//        println(new File("E:/", "Example.txt").text);

        //获取文件大小和绝对路径
//        File file = new File("E:/Example.txt")
//        println("absolutePath:" + file.getAbsolutePath());
//        println("bytes: " + file.length());

        //创建目录
//        file.mkdir()

        //删除文件
//        file.delete()

        
    }
}
```

### as Script

`execute()` 方法可以将这个字符串视作是一个**命令**交给系统去执行，`.text` 可以获取该命令在系统下的执行结果。

```java
package pers.groovy

class GroovyTest {
    static void main(String[] args) {
        // 通过execute执行命令，text获取结果
        println("cmd /c ipconfig".execute().text);

        // 调用另一个脚本
        new GroovyShell().evaluate(new File("E:\\Tomcatproject\\demo\\src\\main\\java\\pers\\groovy\\TestScript.groovy"));
    }
}
```

### 精简的 JavaBean

在 Groovy 当中，编译器总是自动在底层为属性生成对应的 Set 和 Get 方法：

如果希望某个属性在对象被构造之后就不可变，则需使用 `final` 关键字，编译器将不会主动地为其生成 Set 方法 ( 意味着该属性是只读的 ) 。另外，属性可以不主动声明类型，此时原本的类型被 `def` 关键字替代。

如果一个属性被声明为了 `private`，则编译器不会再自动地为该属性声明 Get 和 Set 方法。\

### 注解

这里或许有一些官方提供的注解帮助快速开发，它们绝大部分都是来自于 `groovy.lang` 包，这意味着不需要通过 `import` 关键字额外地导入外部依赖

#### @Canonical 替代 toString

假如希望打印一个类信息，又不想自己生成 `toString()` 方法，则可以使用 `@Canonical` 注解。该注解有额外的 `excludes` 选项：允许我们忽略一些属性

```java
package pers.groovy

import groovy.transform.Canonical

class GroovyTest {
    static void main(String[] args) {
        print new Student(id: 1,name:"Wang Fang",age: 20,major: "CS",score: 90.0d);
    }
}
@Canonical
// 如果不想打印 id 和 score，可以：
// @Canonical(excludes="id,score")
class Student {
    Integer id
    String name
    Integer age
    String major
    Integer score
}
```

#### @Delegate 实现委托

```java
class Worker{
    void work(){
        print("worker is working exactly.")
    }
}

// Manager 获得了 Worker 的公开方法，尽管 worker 属性本身是 private.
class Manager{
    @Delegate private Worker worker = new Worker()
}

// 检查 Manager 实例有没有 work 方法，没有就去委托 worker 执行此方法。
new Manager().work()
```

####  @Immutable 不可变对象

不可变的对象天生就是线程安全的。想要创建一个不可变对象，需要限制它的类属性全部是 `final` ，一旦属性被初始化之后就不可以再被改变

```java
@Immutable
class Student_{
    String id
    String name
}

def s = new Student_(id:"0001",name:"Wang Fang")

print s
```

