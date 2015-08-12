import org.json.JSONObject;

import javax.naming.InsufficientResourcesException;
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
                String account1 = js.getString("account");
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

            /* mark == 2  帐号注册*/
            case 2:
                /* 在数据库中设置帐号和密码 */
                /* 获取用户账户 */
                String account2 = js.getString("account");
                /* 获取注册用户的密码*/
                String password2 = js.getString("secret");
                /* 生成对应的sql语句 */
                String sql2 = "insert into UserInfo (account, password) values (\"" +
                        account2 + "\",\"" + password2 + "\");";
                /* 标识2是更新语句，生成一个可执行sql语句的对象 */
                ExecuteSql es2 = new ExecuteSql(sql2, 1);
                /* 执行sql语句 */
                iret = es2.update();
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result += " 注册成功";
                }else{
                    result += " 注册失败";
                }
                break;

            /* mark == 3 忘记密码 注意：应该验证密宝后返回用户一个key值，mark4根据key值来改新密码 */
            case  3:
                /* 查询数据库是否有此条记录 */
                String account3 = js.getString("account");
                int type = js.getInt("type");
                String verify = js.getString("verify");
                String sql3 = "select count(*) from UserInfo where type = " + type + "and uid = " +
                        "(select uid from UserInfo where account = " + account3 + ") and " +
                        "content = " + verify + ";";
                /* 标识1是select查询语句 */
                ExecuteSql es3 = new ExecuteSql(sql3, 1);
                result = es3.execute();
                /* 将结果转化为整形 */
                int ret3 = Integer.parseInt(result);
                if(ret3 == 1){
                    result += " 验证成功";
                }else {
                    result += " 验证失败";
                }

            /* mark ==4 用户更新密码 */
            case 4:
                /* 获取用户账户 */
                String account4 = js.getString("account");
                String password4 = js.getString("secret");
                String sql4 = "update UserInfo set password = " + password4 + "where account = " + account4
                        + ";";
                /* 标识2表示是更新语句 */
                ExecuteSql es4 = new ExecuteSql(sql4, 1);
                /* 执行更新语句 */
                iret = es4.update();
                /* 获取返回值 */
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result += " 修改密码成功";
                }else{
                    result += " 修改密码失败";
                }

            /* mark ==5 用户更新信息
             * 自己的name，头像等 */
            case 5:
                /* update数据库个人信息记录*/
                /* 获取用户账户*/
                String account5 = js.getString("account");
                /* 获取用户姓名 */
                String name5 = js.getString("name");
                /* 获取用户头像信息 */
                String position5 = js.getString("head");
                /* 生成sql语句 */
                String sql5 = "update UserInfo set name = " + name5 + ", head = "
                        + position5 + "where account = " + account5 + ";";
                /* 根据sql生成可执行对象，获取数量为1 */
                ExecuteSql es5 = new ExecuteSql(sql5, 1);
                /* 执行更新语句，因为个人信息未定。暂定为如下 */
                iret = es5.update();
                /* 获取返回值 */
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result += " 修改个人资料成功";
                }else{
                    result += " 修改个人资料失败";
                }


            /* mark ==6
             * 登记新的联系方式*/
            case 6:
                /* 获取用户账户 */
                String account6 = js.getString("account");
                /* 根据type判断对应的联系方式，1.手机号 2.邮箱 3.qq号 4.微博 */
                int type6 = js.getInt("type");
                /* 获得联系方式的具体内容 */
                String contact6 = js.getString("contact");
                /* 生成sql语句 */
                String sql6 = "insert into UserContact (uid, type, content) values ((select uid from UserInfo" +
                        "where account = " + account6 + ")," + type6 + "," + contact6 +");";
                /* 根据sql生成可执行对象，获取数量为1 */
                ExecuteSql es6 = new ExecuteSql(sql6, 1);
                /* 执行sql */
                iret = es6.update();
                /* 获取返回值 */
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result += " 插入新的数据成功";
                }else {
                    result += " 插入新的数据失败";
                }

            /* mark == 7
            *  说明本地没有数据，客户端需要发送全部数据 */
            case 7:
                /* 获取用户账户 */
                String account7 = js.getString("account");
                /* 生成待查询的sql语句 */
                String sql7 = "select uid, type, content from UserInfo UserContact where uid in" +
                        "(select friendId from UserFriend where uid = (select uid from UserInfo " +
                        "where account = " + account7 + "));";
                /* 根据sql生成查询可执行对象， 获得查询数量 */

                /* !!!这块需要修改，整体查询数据量比较大，并且需要备注 */
                ExecuteSql es7 = new ExecuteSql(sql7, 1);
                /* 执行sql语句 */
                result = es7.execute();

            /* mark == 8
            *  说明本地有数据，只发送好友的数据 */
            case 8:
                /* 获取用户帐号 */
                String account8 = js.getString("account");
                /* 生成待查询的sql语句 */
                String sql8 = "";

            /* mark == 9
            *  添加联系人，UserFriend中添加数据，注意要返回好友的信息 */
            case 9:
                /* 获取用户帐号 */
                String account9 = js.getString("account");
                /* 获得好友帐号 */
                String friendAccount = js.getString("friendaccount");
                /* 生成待查询的sql语句 */
                String sql9 = "insert into UserFriend (uid, friendId) values (" +
                        "(select uid from UserInfo where account = " + account9 + ")," +
                        "(select uid from UserInfo where account = " + friendAccount + "));";
                /* 生成可执行sql对象 */
                ExecuteSql es9 = new ExecuteSql(sql9, 1);
                /* 执行sql并返回结果 */
                iret = es9.update();
                if(iret == 1){
                    result += " 添加好友成功";
                }else{
                    result += " 添加好友失败";
                }
                /* 查询该好友的全部信息并组装成json发送回客户端 */


            /* mark == 10
            *  生成二维码加好友 */
            case 10:
                /* 获取用户帐号 */
                String account10 = js.getString("account");
                /* 获取二维码 */
                String qcode10 = js.getString("qcode");
                /* 看服务端是否保存了此二维码，取出比较，如果时间过了回复客户端请求失败
                *  并发从数据库删除二维码，如果时间未过则执行添加好友语句，返回是否成功 */


            /* mark == 11
            *  生成二维码
            *  客户端生成一张二维码发送给服务器服务器保存 */
            case 11:
                /* 获取用户账户 */
                String account11 = js.getString("account");
                /* 获取二维码 */
                String qcode11 = js.getString("qcode");
                /* 生成sql语句 */
                String sql11 = "";
                /* 二维码时间问题， 权限问题 */


            /* mark ==12
             * 登录*/
            case 12:
                /* 用户帐号 */
                String account12 = js.getString("account");
                /* 获得密码 */
                String password12 = js.getString("secret");
                /* 获得执行的sql语句 */
                String sql12 = "select count(*) from UserInfo where account = " + account12
                        + "and password = " + password12 + ";";
                /* 获得执行sql语句的对象 */
                ExecuteSql es12 = new ExecuteSql(sql12, 1);
                /* 获得结果 */
                result = es12.execute();
                iret = Integer.parseInt(result);
                if(iret == 1){
                    result += " 登录成功";
                }else{
                    result += " 登录失败，帐号或密码错误";
                }

            /* mark == 13
            *  删除联系人 */
            case 13:
                /* 获得用户帐号 */
                String acconut13 = js.getString("account");
                /* 获得好友帐号 */
                String friendAccount13 = js.getString("friendaccount");
                /* 生成待执行的sql语句 */
                String sql13 = "delete from UserFriend where " +
                        "friendId = ( select uid from UserInfo where account = " + friendAccount13
                        + ") and (select uid from UserInfo where account = "  + acconut13 + ");";
                /* 获得执行sql语句的对象 */
                ExecuteSql es13 = new ExecuteSql(sql13, 1);
                /* 执行sql语句 */
                iret = es13.update();
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result += " 删除成功";
                }else{
                    result += " 删除失败";
                }

        }
        return result;
    }
}
