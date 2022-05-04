package myssm.basedao;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnUtil {

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    /*
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String URL = "jdbc:mysql://localhost:3306/qqzonedb?useSSL=FALSE&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    public static final String USER = "root";
    public static final String PWD = "123456";
     */

    static Properties properties = new Properties();

    static {
        InputStream is = ConnUtil.class.getClassLoader().getResourceAsStream("jdbc.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Connection createConn() {
        try {

            DataSource druidDataSource = DruidDataSourceFactory.createDataSource(properties);

            return druidDataSource.getConnection();

            // 1. 加载驱动
            // Class.forName(DRIVER);
            // 2. 通过驱动管理器获取连接对象
            // return DriverManager.getConnection(URL, USER, PWD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConn() {
        Connection conn = threadLocal.get();
        if (conn == null) {
            conn = createConn();
            threadLocal.set(conn);
        }
        return threadLocal.get();
    }
    
    public static void closeConn() throws SQLException {
        Connection conn = threadLocal.get();
        if (conn == null) return;
        if (!conn.isClosed()) {
            conn.close();
            threadLocal.remove();
        }
    }
}
