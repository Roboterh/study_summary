## 代码说明

- `CB_withCC.java`
  因为`BeanComparator`默认的comparator是需要CC依赖的
  动态加载字节码
  PriorityQueue + BeanComparator + getOutputProperties

- `CB_withoutCC.java`
  自定义一个comparator来打无CC依赖

  动态加载字节码

  ```
  - java.util.Collections$ReverseComparator
  - java.lang.String$CaseInsensitiveComparator
  setFieldValue(beanComparator, "comparator", String.CASE_INSENSITIVE_ORDER);
  setFieldValue(beanComparator, "comparator", Collections.reverseOrder());
  ```