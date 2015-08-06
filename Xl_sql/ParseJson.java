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
        int iret = 0;

        switch (mark){
            /* mark == 1 检测此帐号是否正确且被注册过 */
            case 1:
                /* 获得帐号 */
                String account1 = js.getString("PhoneNumber");
                /* 得到获得帐号的mysql查询语句 */
                String sql1 = "select count(*) from UserInfo where account = \"" + account1 + "\";";
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

            case 15:
                /* 在数据库中设置帐号和密码 */
                String account15 = js.getString("PhoneNumber");
                String password15 = js.getString("password");
                String sql15 = "insert into UserInfo (account, password) values (\"" +
                        account15 + "\",\"" + password15 + "\");";
                ExecuteSql es15 = new ExecuteSql(sql15, 2);
                iret = es15.update();
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result += " 注册成功";
                }else{
                    result += " 注册失败";
                }
                break;
        }
        return result;
    }
}
