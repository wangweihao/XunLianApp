import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.json.JSONObject;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wwh on 15-6-10.
 */
public class DBPollClass {
    private static DBPollClass dbPoll;
    private ComboPooledDataSource dbSource;


    public DBPollClass(){
        try{
            dbSource = new ComboPooledDataSource();
            dbSource.setUser("root");
            dbSource.setPassword("w13659218813");
            dbSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/XL_db?user=root&password=w13659218813&useUnicode=true");
            dbSource.setDriverClass("com.mysql.jdbc.Driver");
            dbSource.setInitialPoolSize(1);
            dbSource.setMinPoolSize(2);
            dbSource.setMaxPoolSize(10);
            dbSource.setMaxStatements(50);
            dbSource.setMaxIdleTime(60);
        }catch (PropertyVetoException e){
            throw new RuntimeException(e);
        }
    }

    public final static DBPollClass getInstance(){
        if(dbPoll == null){
            dbPoll = new DBPollClass();
        }
        return dbPoll;
    }

    public final Connection getConnection(){
        try{
            return dbSource.getConnection();
        }catch (SQLException e){
            throw new RuntimeException("无法获取连接", e);
        }
    }

    public final static String queryMysql(JSONObject js) throws SQLException{
        //获取标记，检测是什么类型的数据库查询
        int mark = js.getInt("mark");
        //创建一个连接
        Connection con = null;
        //查询结果
        String result = "";
        try{
            //取得空闲连接
            con = dbPoll.getInstance().getConnection();
            String sql = null;
            //获得sql语句
            sql = getSqlString(js);
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet ret = pst.executeQuery();
            while(ret.next()){
                result += ret.getObject(1);
            }
            System.out.println("查询结果为:" + result);
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(con != null){
                con.close();
            }
        }
        return result;
    }

    public static String getSqlString(JSONObject js){
        String sql = null;
        int mark = js.getInt("mark");
        System.out.println(mark);
        switch (mark){
            //检测帐号是否注册过
            case 1:
                String phonenumber = js.getString("phonenumber");
                sql = "select count(*) from UserInfo where account = " + phonenumber;
                System.out.println(sql);
                break;
            //检测帐号是否存在
            case 2:
                String account = js.getString("phonenumber");
                sql = "select count(*) from UserInfo where account = " + account;
                System.out.println(sql);
                break;
            //修改密码验证阶段
            case 3:
                String contact = js.getString("contact");
                int type = 1;
                sql = "select count(*) from UserContact where type = " + type + "and content = " + contact;
                System.out.println(sql);
                break;
            //输入新密码
            case 4:
                break;
            //用户注册填写更新信息
            case 5:
                break;
            //输入新的联系方式
            case 6:
                break;
            //本地没有数据，服务端发送全部数据
            case 7:
                break;
            //本地有数据，下拉刷新
            case 8:
                break;
            //添加好友--二维码
            case 9:
                break;
            //添加好友--帐号
            case 10:
                break;
            //生成二维码
            case 11:
                break;
            //登录
            case 12:
                break;
        }
        return sql;
    }

    public static void main(String[] args) throws SQLException {
        for(int i = 0; i < 100; i++) {
            long begintime = System.currentTimeMillis();
            Connection con = null;
            try {
                //取得空闲连接
                con = dbPoll.getInstance().getConnection();
                //ResultSet rs = con.createStatement().executeQuery("SELECT * from UserInfo");
                PreparedStatement pst = con.prepareStatement("SELECT * from UserInfo");
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    System.out.println(rs.getObject(1) + " " + rs.getObject(2) + " " + rs.getObject(3) + " " + rs.getObject(4));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    //归还空闲连接
                    con.close();
                }
            }
            long endtime = System.currentTimeMillis();
            System.out.println((i+1) + " time is:" + (endtime-begintime));
        }
    }
}
