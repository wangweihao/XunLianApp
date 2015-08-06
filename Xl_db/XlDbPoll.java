import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.json.JSONObject;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wwh on 15-7-29.
 */
public class XlDbPoll {
    private static ComboPooledDataSource dbScource;
    private static final XlDbPoll xldb;

    static {
        xldb = new XlDbPoll();
    }

    //构造函数创建连接池
    public XlDbPoll(){
        try{
            dbScource = new ComboPooledDataSource();
            dbScource.setUser("root");
            dbScource.setPassword("w13659218813");
            dbScource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/XL_db?user=root&password=w13659218813&useUnicode=true");
            dbScource.setDriverClass("com.mysql.jdbc.Driver");
            dbScource.setInitialPoolSize(1);
            dbScource.setMinPoolSize(2);
            dbScource.setMaxPoolSize(10);
            dbScource.setMaxStatements(50);
            dbScource.setMaxIdleTime(60);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    //从线程池中获取一个连接
    public static Connection getConnection(){
        try{
            return dbScource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("无法获取连接", e);
        }
    }

    public static void main(String[] args)
    {
        Connection con = getConnection();
    }
}
