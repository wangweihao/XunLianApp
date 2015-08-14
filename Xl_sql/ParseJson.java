import org.json.JSONObject;

import javax.naming.InsufficientResourcesException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
        String result = "";
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
                /* 判断结果并且返回 1表示失败帐号存在，其他表示成功，帐号未注册 */
                if(ret == 1) {
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account1 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"该帐号已存在\"}}";
                }else{
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account1 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"该帐号不存在\"}}";
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
                if(iret == 1){
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account2 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"注册成功\"}}";
                }else{
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account2 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"注册失败\"}}";
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
                break;

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
                break;

            /* mark ==5 用户更新信息
             * 自己的name，头像等
             * 一般用户点击详细信息会有更新操作 */
            case 5:
                /* update数据库个人信息记录*/
                /* 获取用户账户*/
                String account5 = js.getString("account");
                /* 获取用户姓名 */
                String name5 = js.getString("name");
                /* 获取用户头像信息 */
                String position5 = js.getString("head");
                /* 生成sql语句 */
                String sql5 = "update UserInfo set name = \'" + name5 + "\', head = \'"
                        + position5 + "\' where account = \'" + account5 + "\';";
                /* 根据sql生成可执行对象，获取数量为1 */
                ExecuteSql es5 = new ExecuteSql(sql5, 1);
                /* 执行更新语句，因为个人信息未定。暂定为如下 */
                iret = es5.update();
                /* 获取返回值 */
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account5 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"修改资料成功\"}}";
                }else{
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account5 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"修改资料失败\"}}";
                }
                break;


            /* mark ==6
             * 登记新的联系方式
             * 需要修改UserFriend 好友and me 的 is update
             * 每次好友登录需要检查好友的isupdate
             * 看谁换联系方式了 */
            case 6:
                /* 获取用户账户 */
                String account6 = js.getString("account");
                /* 根据type判断对应的联系方式，1.手机号 2.邮箱 3.qq号 4.微博
                 * 更加细分type 11.个人 12.工作 13.家庭
                 *             21.个人 22.工作 23.其他
                 *             31
                 *             41*/
                int type6 = js.getInt("type");
                /* 获得联系方式的具体内容 */
                String contact6 = js.getString("contact");
                /* 生成sql语句 */
                String sql6 = "insert into UserContact (uid, type, content) values ((select uid from UserInfo" +
                        " where account = \'" + account6 + "\'), " + type6 + ", \'" + contact6 +"\');";
                System.out.println(sql6);
                /* 根据sql生成可执行对象，获取数量为1 */
                ExecuteSql es6 = new ExecuteSql(sql6, 1);
                /* 执行sql */
                iret = es6.update();
                /* 获取返回值 */
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account6 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"更新信息成功\"}}";
                }else {
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account6 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"更新信息失败\"}}";
                }
                /* 更新UserFriend好友关系表的
                 *  好友and me的is update
                 *  问题 type应该更加细分 */
                String querysql = "select uid from UserInfo where account = \'" + account6 + "\';";
                ExecuteSql esq = new ExecuteSql(querysql, 1);
                String id = esq.execute();
                String updatesql = "update UserFriend set isUpdate = "+ type6 + " where friendId = " +
                        id;
                System.out.println(updatesql);
                /* 生成更新sql语句的对象 */
                ExecuteSql es61 = new ExecuteSql(updatesql, 1);
                /* 执行sql，更新数据 */
                /* 会更新好友和我的isUpdate，下拉刷新时检查isUpdate */
                int tret = es61.update();
                System.out.println("update " + tret);
                if(tret == 1){
                    System.out.println("更新成功");
                }else{
                    System.out.println("更新失败");
                }
                break;

            /* mark == 7
            *  说明本地没有数据，客户端需要发送全部数据 */
            case 7:
                Connection con7 = XlDbPoll.getConnection();
                /* 获取用户账户 */
                String account7 = js.getString("account");
                /* 获取查询改帐号uid的语句 */
                String getAccount = "select uid from UserInfo where account = \'" + account7 + "\';";
                /* 返回预编译statement对象 */
                PreparedStatement ps7 = con7.prepareStatement(getAccount);
                /* 查询 */
                ResultSet rs7 = ps7.executeQuery();
                /* 必须有.next()才会移动到第一行 */
                rs7.next();
                /* uid是第一个对象，获得结果 */
                int uid7 = rs7.getInt(1);
                System.out.println(result);
                /* 得到friendId */
                String getFriendId = "select friendId from UserFriend where uid = " + uid7 + ";";
                /* 返回预编译对象 */
                ps7 = con7.prepareStatement(getFriendId);
                /* 查询 */
                ResultSet fret = ps7.executeQuery();
                /* 定义结果集，并且将查询内容添加到结果集合中，用HashSet是为了去除重复 */
                HashSet<Integer> FidSet = new HashSet<Integer>();
                while(fret.next())
                {
                    FidSet.add(fret.getInt(1));
                }
                /* 获得每个好友的信息及联系方式 */
                result += "\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                        "\"result\":(";
                for(Integer i : FidSet){
                    System.out.println(i);
                    String getFriend = "select type, content from UserContact where uid = " + i + ";";
                    ps7 = con7.prepareStatement(getFriend);
                    ResultSet friendSet = ps7.executeQuery();
                    while(friendSet.next()){
                        result += friendSet.getInt(1);
                        result += "Phone";
                        result += friendSet.getString(2);
                        result += "  ";
                    }
                }

                break;

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
                String sql12 = "select count(*) from UserInfo where account = \'" + account12
                        + "\'and password = \'" + password12 + "\';";
                /* 获得执行sql语句的对象 */
                ExecuteSql es12 = new ExecuteSql(sql12, 1);
                /* 获得结果 */
                result = es12.execute();
                iret = Integer.parseInt(result);
                if(iret == 1){
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account12 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"登录成功\"}}";
                }else{
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account12 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"登录失败\"}}";
                }
                break;

            /* mark == 13
            *  删除联系人 */
            case 13:
                /* 获得用户帐号 */
                String acconut13 = js.getString("account");
                /* 获得好友帐号 */
                String friendAccount13 = js.getString("friendaccount");
                /* 生成待执行的sql语句 */
                String sql13 = "delete from UserFriend where " +
                        "friendId = ( select uid from UserInfo where account = \'" + friendAccount13
                        + "\') and (select uid from UserInfo where account = \'"  + acconut13 + "\');";
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
