package ysoserial.vulndemo;

import java.io.*;
import java.util.Base64;

public class ClassToBase {
    public static void main(String[] args) throws IOException {
        File file = new File("D://浏览器下载//ysoserial//evil.class");
        FileInputStream fileInputStream = new FileInputStream(file);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len;
        while ((len = fileInputStream.read(b)) != -1){
            byteArrayOutputStream.write(b, 0, len);
        }
        fileInputStream.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        System.out.println(Base64.getEncoder().encodeToString(bytes));
    }
}
