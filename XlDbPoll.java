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
    private ComboPooledDataSource dbScource;

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
    public Connection getConnection(){
        try{
            return dbScource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("无法获取连接", e);
        }
    }

    //查询mysql
    public String queryMysql(JSONObject js) throws SQLException{
        //获取标记
        int mark = js.getInt("mark");
        //创建一个连接
        Connection con = null;
        //查询的结果
        String result = "";
        try{
            //获得空闲连接
            con = getConnection();
            String sql = null;
            //获得要执行的sql语句
            sql = getSqlString(js);
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet ret = pst.executeQuery();
            while(ret.next()){
                result += ret.getObject(1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(con != null){
                con.close();
            }
        }
        return result;
    }

    //获得sql语句
    public String getSqlString(JSONObject js){
        String sql = null;
        //取得标识
        int mark = js.getInt("mark");
        switch (mark){
            /*检测帐号是否注册过*/
            case 1:
                String account = js.getString("account");
                sql = "select count(*) from UserInfo where account = " + account;
                System.out.println(sql);
                break;
        }

        return sql;
    }

    public static void main(String[] args) throws SQLException {
        XlDbPoll xl = new XlDbPoll();
        String s = "{\"mark\":1, \"account\":\"18829290435\"}";
        JSONObject js = new JSONObject(s);
        String result = xl.queryMysql(js);
        System.out.println(result);
    }
}
