import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wwh on 15-8-6.
 */
public class ExecuteSql {

    /* 待执行的sql 语句*/
    String sql;
    /* 数据库的一条连接 */
    Connection con;
    /* 待取得成员数量 */
    int num;

    /* 构造函数获得待执行的sql语句和需要返回成员的个数 */
    ExecuteSql(String sql, int num) throws SQLException {
        /* 知道获得ResultSet集合中的几个数据成员*/
        this.num = num;

        /* 获得sql语句 */
        this.sql = sql;

        /* 获得数据库连接池的连接 */
        try {
            con = XlDbPoll.getConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* 执行sql 语句 ，-->查询类   获得结果*/
    String execute() throws SQLException {
        String result = "";
        String temps = "";
        /* 准备sql语句 */
        PreparedStatement pst = con.prepareStatement(sql);
        /* 查询并返回结果 */
        ResultSet ret = pst.executeQuery();

        /* 根据构造函数传递的参数来获得num，从而得到result */
        while(ret.next()){
            //System.out.println("get+" + ret.getObject(1));
            /* 对象个数不确定 */
            for(int i = 1; i <= num; ++i) {
                temps += ret.getObject(i);
                System.out.println("result:" + ret.getObject(i));
            }
            result += temps;
        }
        /* 用完必须归还连接到连接池中*/
        con.close();
        /* 返回查询结果 */
        return result;
    }

    /* 执行sql语句，更新类 --> 返回结果*/
    int update() throws SQLException{
        /* 准备sql*/
        PreparedStatement pst2 = con.prepareStatement(sql);
        /* 返回更新结果 */
        int iret = pst2.executeUpdate();
        /* 用完必须归还连接到连接池中*/
        con.close();
        System.out.println(iret);
        return iret;
    }
}
