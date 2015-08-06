import org.json.JSONObject;

import java.sql.SQLException;

/**
 * Created by wwh on 15-8-6.
 */
public class ParseJson {
    /* 待解析的json对象 */
    JSONObject js;

    /* 构造函数，获取json */
    ParseJson(JSONObject json) {
        this.js  = json;
    }

    /* 解析json并获取相应的结果 */
    String Parse() throws SQLException {
        /* 获得mark，创建对应的Executesql对象 */
        int mark = js.getInt("mark");
        System.out.println("mark:" + mark);
        /* 结果 */
        String result = null;

        switch (mark){
            /* mark == 1 检测此帐号是否正确且被注册过 */
            case 1:
                /* 获得帐号 */
                String account = js.getString("PhoneNumber");
                /* 得到获得帐号的mysql查询语句 */
                String sql1 = "select count(*) from UserInfo where account = \"" + account + "\";";
                /* 获得查询结果 */
                ExecuteSql es1 = new ExecuteSql(sql1, 1);
                result = es1.execute();
                /* 将结果转换为整型 */
                int ret = Integer.parseInt(result);
                /* 判断结果并且返回 */
                if(ret == 1) {
                    result = "帐号已存在\0";
                }else{
                    result = "此帐号可以注册\0";
                }
                break;

            case 2:

                String sql2 = "";
                ExecuteSql es2 = new ExecuteSql(sql2, 2);
                result = es2.execute();
                break;
        }
        return result;
    }
}
