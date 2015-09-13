import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



/**
 * Created by wangweihao on 15-8-6.
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
        /* 结果 */
        String result = "";
        int iret = 0;
        int mark = 0;
        /* 获得mark，创建对应的Executesql对象 */
        try {
            mark = js.getInt("mark");
        }catch (Exception e){
            result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                    "\"result\":{\"requestPhoneNum\":\"\", \"IsSuccess\":\"failure\"," +
                    "\"mark\":0, \"ResultINFO\":\"json解析失败，您的输入有误\"}}";
            return result;
        }

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
                String account2 = "";
                String password2 = "";

                try {
                    account2 = js.getString("account");
                /* 获取注册用户的密码*/
                    password2 = js.getString("secret");
                }catch (Exception e){
                    System.out.println("json解析失败");
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account2 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"客户端json格式有误\"}}";
                    break;
                }
                /* 生成对应的sql语句 */
                String sql2 = "insert into UserInfo (account, password) values (\"" +
                        account2 + "\",\"" + password2 + "\");";
                /* 标识2是更新语句，生成一个可执行sql语句的对象 */
                ExecuteSql es2 = null;
                try {
                    es2 = new ExecuteSql(sql2, 1);
                    /* 执行sql语句 */
                    iret = es2.update();
                }catch (Exception e){
                    System.out.println("帐号以存在");
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account2 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"帐号以存在\"}}";
                    break;
                }

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

                String account3 = null;
                int type3 = 0;
                String verify = null;
                try {
                    account3 = js.getString("account");
                    type3 = js.getInt("type");
                    verify = js.getString("verify");
                }catch (Exception e){
                    System.out.println("json解析失败");
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"00000000\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"json解析失败，检测输入是否正确\"}}";
                }
                String sql3 = "select count(*) from UserContact where type = \"" + type3 + "\"and uid = " +
                        "(select uid from UserInfo where account = \"" + account3 + "\") and " +
                        "content = \"" + verify + "\";";
                /* 标识1是select查询语句 */
                ExecuteSql es3 = new ExecuteSql(sql3, 1);
                result = es3.execute();
                /* 将结果转化为整形 */
                int ret3 = Integer.parseInt(result);
                if(ret3 == 1){
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account3 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"帐号验证成功\"}}";
                }else {
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account3 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"帐号验证失败\"}}";
                }
                break;

            /* mark ==4 用户更新密码 */
            case 4:
                /* 获取用户账户 */
                String account4 = js.getString("account");
                String password4 = js.getString("secret");
                String sql4 = "update UserInfo set password = \"" + password4 + "\"where account = \"" + account4
                        + "\";";
                /* 标识2表示是更新语句 */
                ExecuteSql es4 = new ExecuteSql(sql4, 1);
                /* 执行更新语句 */
                iret = es4.update();
                /* 获取返回值 */
                result = Integer.valueOf(iret).toString();
                if(iret == 1){
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account4 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"用户更新密码成功\"}}";
                }else{
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account4 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"用户更新密码失败\"}}";
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
                /* 获得type */
                int type6 = js.getInt("type");
                /* 获得联系方式的具体内容 */
                String contact6 = js.getString("contact");
                /* 根据type判断对应的联系方式，1.手机号 2.邮箱 3.qq号 4.微博
                 * 更加细分type 11.个人 12.工作 13.家庭
                 *             21.个人 22.工作 23.其他
                 *             31
                 *             41*/
                /* type设置20位 varchar(20) 类似位运算，每一位为一个标记 */

                /* 找到此用户的uid，并且通过此用户的uid找到用户的friend的uid，
                 * 更新所有对应的friendId--uid 的isUpdate 标记*/
                /* 获取用户uid */
                String UidSql6 = "select uid from UserInfo where account = \"" + account6 + "\";";
                ExecuteSql oldes6 = new ExecuteSql(UidSql6, 1);
                String suid = oldes6.execute();
                List<String> friendId = new LinkedList<String>();
                String friendSql = "select friendId from UserFriend where uid = \"" + suid + "\";";
                Connection connection = XlDbPoll.getConnection();
                PreparedStatement pst = connection.prepareStatement(friendSql);
                ResultSet rs = pst.executeQuery();
                /* 将查询到的friendId添加进集合中，依次对每个好友更新isUpdate */
                while(rs.next()){
                    friendId.add(rs.getString(1));
                }
                connection.close();

                /* 获得uid */
                int uid = Integer.parseInt(suid);
                /* 根据标记来判断是更新数据还是插入数据，都要给好友设置isUdate，更新 */
                int flag = js.getInt("isUpdateOrInsert");
                switch (flag){
                    /* insert */
                    case 1:
                        /* 生成sql语句 */
                        String insertSql = "insert into UserContact (uid, type, content) values ((select uid from UserInfo" +
                                " where account = \'" + account6 + "\'), " + type6 + ", \'" + contact6 +"\');";
                        System.out.println(insertSql);
                        /* 根据sql生成可执行对象，获取数量为1 */
                        ExecuteSql es6 = new ExecuteSql(insertSql, 1);
                        /* 执行sql */
                        iret = es6.update();
                        /* 获取返回值 */
                        result = Integer.valueOf(iret).toString();
                        if(iret == 1){
                            result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                                    "\"result\":{\"requestPhoneNum\":\"" + account6 + "\", \"IsSuccess\":\"success\"," +
                                    "\"mark\":" + mark + ",\"ResultINFO\":\"插入信息成功\"}}";
                        }else {
                            result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                                    "\"result\":{\"requestPhoneNum\":\"" + account6 + "\", \"IsSuccess\":\"failure\"," +
                                    "\"mark\":" + mark + ",\"ResultINFO\":\"插入信息失败\"}}";
                        }
                        break;
                    /* update */
                    case 2:
                        /* 生成sql语句 */
                        String updateSql = "update UserContact set content = \"" + contact6 + "\" where " +
                                "uid = \"" + suid + "\" and type = \"" + type6 + "\";";
                        System.out.println(updateSql);
                        /* 根据sql生成可执行对象，获得数量为1 */
                        ExecuteSql estwo = new ExecuteSql(updateSql, 1);
                        /* 执行sql */
                        int tret = estwo.update();
                        if(tret == 1){
                            result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                                    "\"result\":{\"requestPhoneNum\":\"" + account6 + "\", \"IsSuccess\":\"success\"," +
                                    "\"mark\":" + mark + ",\"ResultINFO\":\"更新信息成功\"}}";
                        }else{
                            result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                                    "\"result\":{\"requestPhoneNum\":\"" + account6 + "\", \"IsSuccess\":\"failure\"," +
                                    "\"mark\":" + mark + ",\"ResultINFO\":\"更新信息失败\"}}";
                        }
                        break;
                }

                /* 根据好友的uid来更新好友表的isUpdate的值 */
                Connection con = XlDbPoll.getConnection();
                String obtainSql = "select isUpdate from UserFriend where uid = \"" + suid +"\"";
                for(String s : friendId){
                    /* 获得旧的isUpdate标记 */
                    String exeSql = obtainSql + " and friendId = \"" + s + "\";";
                    PreparedStatement pstIsupdate = con.prepareStatement(exeSql);
                    ResultSet rst = pstIsupdate.executeQuery();
                    String oldisUpdate = "";
                    /* 得到旧的标记 */
                    while(rst.next()){
                        oldisUpdate += rst.getObject(1);
                    }
                    System.out.println("oldisUpdate = " + oldisUpdate);
                    /* 通过位运算得到新的标记 */
                    int ioldisUpdate = Integer.parseInt(oldisUpdate);
                    int inewisUpdate = ioldisUpdate | type6;
                    System.out.printf("new isUpdate " + inewisUpdate);
                    /* 更新isupdate标记 */
                    String sql = "update UserFriend set isUpdate = \"" + inewisUpdate + "\" where uid = " +
                            "\"" + uid + "\" and friendId = \"" + s + "\";";
                    PreparedStatement rrst = con.prepareStatement(sql);
                    /* 执行 */
                    int bret = rrst.executeUpdate();
                    /* 如果错误，覆盖掉result，并且返回错误 */
                    if(bret != 1){
                        System.out.println("hehe");
                        result = "{\"error\":1, \"status\":\"failure\", \"date\":\"2015-08\", " +
                                "\"result\":{\"requestPhoneNum\":\"" + account6 + "\", \"IsSuccess\":\"failure\"," +
                                "\"mark\":" + mark + ",\"ResultINFO\":\"更新好友信息失败\"}}";
                    }
                }
                con.close();
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
                /* 获得所有好友的uid集合 */
                while(fret.next())
                {
                    FidSet.add(fret.getInt(1));
                }
                con7.close();
                /* 通过好友的uid集合来一一获取好友信息
                 * 然后 组装成json数组 返回给用户 */
                /* 获取连接池中的一条连接 */
                Connection connectionInfoSql = XlDbPoll.getConnection();
                result += "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                        "\"result\":[";
                /* 设置一个标签 */
                int tap = 0;
                for(int userId : FidSet){
                    /* 查询好友的信息 */
                    String infoSql = "select name, head from UserInfo where uid = \"" + userId + "\";";
                    PreparedStatement PreInfo = connectionInfoSql.prepareStatement(infoSql);
                    ResultSet rsInfo = PreInfo.executeQuery();
                    /* 必须要有next(),跳转到第一个信息 */
                    rsInfo.next();
                    /* 获取姓名和头像 */
                    String tname = rsInfo.getString(1);
                    String thead = rsInfo.getString(2);
                    System.out.println("tname:" + tname);
                    System.out.println("thead:" + thead);
                    /* 获取联系信息 */
                    String contactSql = "select type, content from UserContact where uid = \"" + userId + "\";";
                    PreparedStatement PreContact = connectionInfoSql.prepareStatement(contactSql);
                    ResultSet rsCon = PreContact.executeQuery();
                    /* 用map来保存查询结果
                    *  因为稍后要将数据序列化发送给对方
                    *  map是默认有序的
                    *  这样会比较好序列化 */
                    //HashMap<Integer, String> FriendInfo = new HashMap<Integer, String>();
                    /* 准备设置一个好友的json格式的消息
                    * 消息包括
                    * 姓名，头像
                    * 家庭，工作，个人电话
                    * 家庭，工作，个人邮箱
                    * qq和微博
                    * */
                    String name;
                    if(tap == 0){
                        name = "{\"name\":\"" + tname + "\",";
                        ++tap;
                    }else{
                        name = ",{\"name\":\"" + tname + "\",";
                    }
                    String head = "\"head\":\"" + thead + "\",";
                    String personPhoneNumber = "\"personNumber\":\"";
                    String workPhoneNumber = "\"workPhoneNumber\":\"";
                    String homePhoneNumber = "\"homePhoneNumber\":\"";
                    String personEmail = "\"personEmail\":\"";
                    String workEmail = "\"workEmail\":\"";
                    String homeEmail = "\"homeEmail\":\"";
                    String qqNumber = "\"qqNumber\":\"";
                    String weiboNumber = "\"weiboNumber\":\"";

                    result += name;
                    result += head;
                    /* 获得每个好友的信息及联系方式
                     * 下面构造result
                     * */
                    while(rsCon.next()){
                        /* 获得标记联系方式和具体的联系方式 */
                        int type = rsCon.getInt(1);
                        String content = rsCon.getString(2);
                        /* 由于每个好友的信息不一定完全
                        * 比如好友1只填写了电话，微博qq等都为空，
                        * 所以在这需要自己进行判断
                        * */
                        switch(type){
                            case 1:
                                /* 如果存在个人电话，获取 */
                                personPhoneNumber += content;
                                personPhoneNumber += "\",";
                                break;
                            case 2:
                                /* 如果存在工作电话，获取 */
                                workPhoneNumber += content;
                                workPhoneNumber += "\",";
                                break;
                            case 4:
                                /* 如果存在家庭电话，获取 */
                                homePhoneNumber += content;
                                homePhoneNumber += "\",";
                                break;
                            case 8:
                                /* 如果存在个人邮箱，获取 */
                                personEmail += content;
                                personEmail += "\",";
                                break;
                            case 16:
                                /* 如果存在工作邮箱，获取 */
                                workEmail += content;
                                workEmail += "\",";
                                break;
                            case 32:
                                /* 如果存在家庭邮箱，获取*/
                                homeEmail += content;
                                homeEmail += "\",";
                                break;
                            case 64:
                                /* 如果存在qq，获取 */
                                qqNumber += content;
                                qqNumber += "\",";
                                break;
                            case 128:
                                /* 如果存在微博，获取 */
                                weiboNumber += content;
                                weiboNumber += "\"}";
                                break;
                        }
                    }
                    result += personPhoneNumber;
                    result += workPhoneNumber;
                    result += homePhoneNumber;
                    result += personEmail;
                    result += workEmail;
                    result += homeEmail;
                    result += qqNumber;
                    result += weiboNumber;
                }
                connectionInfoSql.close();
                result += "]};";
                break;

            /* mark == 8
            *  说明本地有数据，只更新发送好友的数据
            *  相当于下拉刷新 */
            /*
            *  先通过account 获得用户 uid
            *  uid 获取 friendId
            *  friendId and uid 获取不为0的 isUpdate数据
            *  分析isUpdate数据，找出标记，组装好友更新的数据，返回
            */
            case 8:
                /* 从连接池中获取一条数据库连接 */
                Connection connectionUpdate = XlDbPoll.getConnection();
                /* 获取用户帐号 */
                String account8 = js.getString("account");
                /* 生成获得uid的sql语句 */
                String getUidSql = "select Uid from UserInfo where account = \"" + account8 + "\";";
                PreparedStatement preUpdate = connectionUpdate.prepareStatement(getUidSql);
                ResultSet rst = preUpdate.executeQuery();
                rst.next();
                /* 获取用户id */
                int UserUid = rst.getInt(1);
                /* 通过用户uid 获得friendId */
                String getIsupdateFriendIdSql = "select friendId from UserFriend where uid = \"" + UserUid  + "\";";
                PreparedStatement preGetisUpdateUid = connectionUpdate.prepareStatement(getIsupdateFriendIdSql);
                ResultSet isUpdateFriendId = preGetisUpdateUid.executeQuery();
                HashSet<Integer> friend = new HashSet<Integer>();
                /* 获取好友的Id */
                while(isUpdateFriendId.next()){
                    friend.add(isUpdateFriendId.getInt(1));
                }

                /* 用hashMap来保存待更新的好友的uid和标记isUpdate */
                HashMap<Integer,Integer> FriendId = new HashMap<Integer, Integer>();

                for(Integer id : friend){
                    /* 通过好友和自己的映射关系来获得isUpdate
                    *  比如我更新数据，好友表上的关系是好友-我 的 isUpdate设置更新 */
                    String tSql = "select isUpdate from UserFriend where uid = \"" + id + "\" and isUpdate != 0 " +
                            "and friendId = \"" + UserUid + "\";";
                    PreparedStatement updatePst = connectionUpdate.prepareStatement(tSql);
                    ResultSet updateDate = updatePst.executeQuery();
                    int isUpdate = 0;
                    /* 不用循环可能会抛出异常， 因为ResultSet可能为空
                    *  获得isUpdate标记 */
                    while(updateDate.next()){
                        isUpdate = updateDate.getByte(1);
                    }
                    if(isUpdate != 0) {
                        FriendId.put(id, isUpdate);
                    }
                }

                /* 组装时需要判断一下 */
                int tapp = 0;
                /* 返回的信息 */
                result += "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                        "\"result\":[";

                /* 需要判断集合是否为空 */
                if(FriendId.isEmpty()){
                    /* 集合为空，说明没有好友需要更新数据 */
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account8 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"无好友更新信息\"}}";
                }else {
                    /* 遍历集合，分析isUpdate数据，找出标记，组装好友更新的数据 */
                    for (Integer keyUid : FriendId.keySet()) {
                        /* 获取需要更新的好友的姓名和头像 */
                        String friendInfoSql = "select name, head from UserInfo where uid = \"" + keyUid + "\";";
                        PreparedStatement friendInfoPst = connectionUpdate.prepareStatement(friendInfoSql);
                        ResultSet friendInfoRs = friendInfoPst.executeQuery();
                        friendInfoRs.next();
                        /* 获取姓名和头像 */
                        String tname = friendInfoRs.getString(1);
                        String thead = friendInfoRs.getString(2);

                        String name;
                        if(tapp == 0){
                            name = "{\"name\":\"" + tname + "\",";
                            ++tapp;
                        }else{
                            name = ",{\"name\":\"" + tname + "\",";
                        }
                        String head = "\"head\":\"" + thead + "\",";
                        String personPhoneNumber = "\"personNumber\":\"";
                        String workPhoneNumber = "\",\"workPhoneNumber\":\"";
                        String homePhoneNumber = "\",\"homePhoneNumber\":\"";
                        String personEmail = "\",\"personEmail\":\"";
                        String workEmail = "\",\"workEmail\":\"";
                        String homeEmail = "\",\"homeEmail\":\"";
                        String qqNumber = "\",\"qqNumber\":\"";
                        String weiboNumber = "\",\"weiboNumber\":\"";

                        int isUpdate = FriendId.get(keyUid);
                        /* 8bit位 若标记为1 说明qqNumber更新 */
                        if(isUpdate >= 128){
                            /* 删除log128位 标记*/
                            isUpdate -= 128;
                            String weiboNumberSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 128;";
                            PreparedStatement weiboNUmberPst = connectionUpdate.prepareStatement(weiboNumberSql);
                            ResultSet weiboNumberRs = weiboNUmberPst.executeQuery();
                            weiboNumberRs.next();
                            weiboNumber += weiboNumberRs.getString(1);
                        }
                        if(isUpdate >= 64){
                            isUpdate -= 64;
                            String qqNumberSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 64;";
                            PreparedStatement qqNumberPst = connectionUpdate.prepareStatement(qqNumberSql);
                            ResultSet qqNumberRs = qqNumberPst.executeQuery();
                            qqNumberRs.next();
                            qqNumber += qqNumberRs.getString(1);
                        }
                        if(isUpdate >= 32){
                            isUpdate -= 32;
                            String homeEmailSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 32;";
                            PreparedStatement homeEmailPst = connectionUpdate.prepareStatement(homeEmailSql);
                            ResultSet homeEmailRs = homeEmailPst.executeQuery();
                            homeEmailRs.next();
                            homeEmail += homeEmailRs.getString(1);
                        }
                        if(isUpdate >= 16){
                            isUpdate -= 16;
                            String workEmailSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 16;";
                            PreparedStatement workEmailPst = connectionUpdate.prepareStatement(workEmailSql);
                            ResultSet workEmailRs = workEmailPst.executeQuery();
                            workEmailRs.next();
                            workEmail += workEmailRs.getString(1);
                        }
                        if(isUpdate >= 8){
                            isUpdate -= 8;
                            String personEmailSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 8;";
                            PreparedStatement personEmailPst = connectionUpdate.prepareStatement(personEmailSql);
                            ResultSet personEmailRs = personEmailPst.executeQuery();
                            personEmailRs.next();
                            personEmail += personEmailRs.getString(1);
                        }
                        if(isUpdate >= 4){
                            isUpdate -= 4;
                            String homePhoneNumberSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 4;";
                            PreparedStatement homePhoneNumberPst = connectionUpdate.prepareStatement(homePhoneNumberSql);
                            ResultSet homePhoneNumberRs = homePhoneNumberPst.executeQuery();
                            homePhoneNumberRs.next();
                            homePhoneNumber += homePhoneNumberRs.getString(1);
                        }
                        if(isUpdate >= 2){
                            isUpdate -= 2;
                            String workPhoneNumberSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 2;";
                            PreparedStatement workPhoneNumberPst = connectionUpdate.prepareStatement(workPhoneNumberSql);
                            ResultSet workPhoneNumberRs = workPhoneNumberPst.executeQuery();
                            workPhoneNumberRs.next();
                            workPhoneNumber += workPhoneNumberRs.getString(1);
                        }
                        if(isUpdate >= 1){
                            isUpdate -= 1;
                            String personPhoneNumberSql = "select content from UserContact where uid = \"" + keyUid
                                    + "\" and type = 1;";
                            PreparedStatement personPhoneNumberPst = connectionUpdate.prepareStatement(personPhoneNumberSql);
                            ResultSet personPhoneNumberRs = personPhoneNumberPst.executeQuery();
                            personPhoneNumberRs.next();
                            personPhoneNumber += personPhoneNumberRs.getString(1);
                        }
                        result += personPhoneNumber;
                        result += workPhoneNumber;
                        result += homePhoneNumber;
                        result += personEmail;
                        result += workEmail;
                        result += homeEmail;
                        result += qqNumber;
                        result += weiboNumber;
                    }
                }
                result += "\"]};";
                connectionUpdate.close();
                break;


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
                    //result += " 添加好友成功";
                    System.out.println("添加好友成功");
                }else{
                    //result += " 添加好友失败";
                    System.out.println("添加好友失败");
                }
                /* 准备查询该好友的全部信息，并且返回 */
                String friendInfo = "select name,head from UserInfo where account = \"" + friendAccount
                        + "\";";
                Connection addFriendCon = XlDbPoll.getConnection();
                PreparedStatement addFriendPst = addFriendCon.prepareStatement(friendInfo);
                ResultSet addFriendRs = addFriendPst.executeQuery();
                /* 获取姓名和头像 */
                String tname = "";
                String thead = "";
                while(addFriendRs.next()){
                    tname = addFriendRs.getString(1);
                    thead = addFriendRs.getString(2);
                }
                /* 准备返回信息 */
                String name = "{\"name\":\"" + tname + "\",";
                String head = "\"head\":\"" + thead + "\",";
                String personPhoneNumber = "\"personNumber\":\"";
                String workPhoneNumber = "\",\"workPhoneNumber\":\"";
                String homePhoneNumber = "\",\"homePhoneNumber\":\"";
                String personEmail = "\",\"personEmail\":\"";
                String workEmail = "\",\"workEmail\":\"";
                String homeEmail = "\",\"homeEmail\":\"";
                String qqNumber = "\",\"qqNumber\":\"";
                String weiboNumber = "\",\"weiboNumber\":\"";
                result += "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                        "\"result\":[";

                /* 查询该好友的全部信息并组装成json发送回客户端 */
                result += name;
                result += head;
                String tsql = "select uid from UserInfo where account = \"" + friendAccount + "\";";
                PreparedStatement tPst = addFriendCon.prepareStatement(tsql);
                ResultSet tRs = tPst.executeQuery();
                tRs.next();
                int friendUid = tRs.getInt(1);
                String addFriendSql = "select type, content from UserContact where uid = \"" + friendUid + "\";";
                PreparedStatement friendPst = addFriendCon.prepareStatement(addFriendSql);
                ResultSet addFriendInfoRs = friendPst.executeQuery();
                /* 获得好友的信息及联系方式
                 * 下面构造result
                 * */
                while(addFriendInfoRs.next()){
                    /* 获得标记联系方式和具体的联系方式 */
                    int type = addFriendInfoRs.getInt(1);
                    String content = addFriendInfoRs.getString(2);
                    /* 由于好友的信息不一定完全
                     * 比如好友只填写了电话，微博qq等都为空，
                     * 所以在这需要自己进行判断
                     * */
                    switch(type){
                        case 1:
                                /* 如果存在个人电话，获取 */
                            personPhoneNumber += content;
                            personPhoneNumber += "\",";
                            break;
                        case 2:
                                /* 如果存在工作电话，获取 */
                            workPhoneNumber += content;
                            workPhoneNumber += "\",";
                            break;
                        case 4:
                                /* 如果存在家庭电话，获取 */
                            homePhoneNumber += content;
                            homePhoneNumber += "\",";
                            break;
                        case 8:
                                /* 如果存在个人邮箱，获取 */
                            personEmail += content;
                            personEmail += "\",";
                            break;
                        case 16:
                                /* 如果存在工作邮箱，获取 */
                            workEmail += content;
                            workEmail += "\",";
                            break;
                        case 32:
                                /* 如果存在家庭邮箱，获取*/
                            homeEmail += content;
                            homeEmail += "\",";
                            break;
                        case 64:
                                /* 如果存在qq，获取 */
                            qqNumber += content;
                            qqNumber += "\",";
                            break;
                        case 128:
                                /* 如果存在微博，获取 */
                            weiboNumber += content;
                            weiboNumber += "\"}";
                            break;
                    }
                }
                /* 拼装返回的result */
                result += personPhoneNumber;
                result += workPhoneNumber;
                result += homePhoneNumber;
                result += personEmail;
                result += workEmail;
                result += homeEmail;
                result += qqNumber;
                result += weiboNumber;
                result += "\"]};";
                /* 关闭连接池 */
                addFriendCon.close();
                break;

            /* mark == 10
            *  生成二维码加好友 */
            case 10:
                /* 获取用户帐号 */
                String account10 = js.getString("account");
                /* 获取好友账户(二维码上) */
                String addFriendAccount = js.getString("friendaccount");
                /* 获取权限(二维码上) */
                int addAuthority = js.getInt("authority");
                /* 获取超时时间，取时比较，若超时返回错误(二维码上) */
                int addTime_out = js.getInt("time_out");
                /* 获取本人和好友的uid
                *  向好友表中插入一个数据
                *  根据authority获取本人信息
                *  将获取的本人信息组装成result返回给用户
                * */
                /* 从数据库连接池中取出一条记录 */
                Connection QrAddFriendConn = XlDbPoll.getConnection();
                /* 获取本人和好友uid的sql语句 */
                String getUserUid = "select uid from UserInfo where account = \"" + account10 + "\";";
                String getFriendUid = "select uid from UserInfo where account = \"" + addFriendAccount + "\";";
                /* 得到本人和好友的uid */
                System.out.println("getUserUid:" + getUserUid);
                System.out.println("getFriendUid:" + getFriendUid);
                PreparedStatement getUserUidPst = QrAddFriendConn.prepareStatement(getUserUid);
                ResultSet getUserRs = getUserUidPst.executeQuery();
                getUserRs.next();
                int userUid = getUserRs.getInt(1);
                PreparedStatement getUserFriendUid = QrAddFriendConn.prepareStatement(getFriendUid);
                ResultSet getFriendRs = getUserFriendUid.executeQuery();
                getFriendRs.next();
                int getfriendUid = getFriendRs.getInt(1);
                System.out.println("uid:" + userUid);
                System.out.println("friendUid:" + getfriendUid);

                /* 需要查询数据库看二维码是否过期 */

                /* 向好友表中插入一条数据 */
                String insertFriendTable = "insert into UserFriend (uid, friendId) values (" + getfriendUid
                        + ", " + userUid + ");";
                System.out.println("------------" + insertFriendTable);
                PreparedStatement insertFriend = QrAddFriendConn.prepareStatement(insertFriendTable);
                int insertRet = insertFriend.executeUpdate();
                if(insertRet == 1){
                    System.out.println("yes");
                }else {
                    System.out.println("no");
                }
                /* 根据authority获取信息 */
                String getNameHeadSql = "select name, head from UserInfo where uid = \"" + getfriendUid + "\";";
                System.out.println(getNameHeadSql + "---------------");
                PreparedStatement getNameHeadPst = QrAddFriendConn.prepareStatement(getNameHeadSql);
                ResultSet getNameHeadRs = getNameHeadPst.executeQuery();
                getNameHeadRs.next();
                String itname = getNameHeadRs.getString(1);
                String ithead = getNameHeadRs.getString(2);
                /* 准备组装result */
                String iname = "{\"name\":\"" + itname + "\",";
                String ihead = "\"head\":\"" + ithead + "\",";
                String ipersonPhoneNumber = "\"personNumber\":\"";
                String iworkPhoneNumber = "\",\"workPhoneNumber\":\"";
                String ihomePhoneNumber = "\",\"homePhoneNumber\":\"";
                String ipersonEmail = "\",\"personEmail\":\"";
                String iworkEmail = "\",\"workEmail\":\"";
                String ihomeEmail = "\",\"homeEmail\":\"";
                String iqqNumber = "\",\"qqNumber\":\"";
                String iweiboNumber = "\",\"weiboNumber\":\"";
                result += "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                        "\"result\":[";

                if(addAuthority >= 128){
                            /* 删除log128位 标记*/
                    addAuthority -= 128;
                    String weiboNumberSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 128;";
                    PreparedStatement weiboNUmberPst = QrAddFriendConn.prepareStatement(weiboNumberSql);
                    ResultSet weiboNumberRs = weiboNUmberPst.executeQuery();
                    weiboNumberRs.next();
                    iweiboNumber += weiboNumberRs.getString(1);
                }
                if(addAuthority >= 64){
                    addAuthority -= 64;
                    String qqNumberSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 64;";
                    PreparedStatement qqNumberPst = QrAddFriendConn.prepareStatement(qqNumberSql);
                    ResultSet qqNumberRs = qqNumberPst.executeQuery();
                    qqNumberRs.next();
                    iqqNumber += qqNumberRs.getString(1);
                }
                if(addAuthority >= 32){
                    addAuthority -= 32;
                    String homeEmailSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 32;";
                    PreparedStatement homeEmailPst = QrAddFriendConn.prepareStatement(homeEmailSql);
                    ResultSet homeEmailRs = homeEmailPst.executeQuery();
                    homeEmailRs.next();
                    ihomeEmail += homeEmailRs.getString(1);
                }
                if(addAuthority >= 16){
                    addAuthority -= 16;
                    String workEmailSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 16;";
                    PreparedStatement workEmailPst = QrAddFriendConn.prepareStatement(workEmailSql);
                    ResultSet workEmailRs = workEmailPst.executeQuery();
                    workEmailRs.next();
                    iworkEmail += workEmailRs.getString(1);
                }
                if(addAuthority >= 8){
                    addAuthority -= 8;
                    String personEmailSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 8;";
                    PreparedStatement personEmailPst = QrAddFriendConn.prepareStatement(personEmailSql);
                    ResultSet personEmailRs = personEmailPst.executeQuery();
                    personEmailRs.next();
                    ipersonEmail += personEmailRs.getString(1);
                }
                if(addAuthority >= 4){
                    addAuthority -= 4;
                    String homePhoneNumberSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 4;";
                    PreparedStatement homePhoneNumberPst = QrAddFriendConn.prepareStatement(homePhoneNumberSql);
                    ResultSet homePhoneNumberRs = homePhoneNumberPst.executeQuery();
                    homePhoneNumberRs.next();
                    ihomePhoneNumber += homePhoneNumberRs.getString(1);
                }
                if(addAuthority >= 2){
                    addAuthority -= 2;
                    String workPhoneNumberSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 2;";
                    PreparedStatement workPhoneNumberPst = QrAddFriendConn.prepareStatement(workPhoneNumberSql);
                    ResultSet workPhoneNumberRs = workPhoneNumberPst.executeQuery();
                    workPhoneNumberRs.next();
                    iworkPhoneNumber += workPhoneNumberRs.getString(1);
                }
                if(addAuthority >= 1){
                    addAuthority -= 1;
                    String personPhoneNumberSql = "select content from UserContact where uid = \"" + getfriendUid
                            + "\" and type = 1;";
                    PreparedStatement personPhoneNumberPst = QrAddFriendConn.prepareStatement(personPhoneNumberSql);
                    ResultSet personPhoneNumberRs = personPhoneNumberPst.executeQuery();
                    personPhoneNumberRs.next();
                    ipersonPhoneNumber += personPhoneNumberRs.getString(1);
                }
                result += ipersonPhoneNumber;
                result += iworkPhoneNumber;
                result += ihomePhoneNumber;
                result += ipersonEmail;
                result += iworkEmail;
                result += ihomeEmail;
                result += iqqNumber;
                result += iweiboNumber;

                result += "\"]};";
                QrAddFriendConn.close();
                break;



            /* mark == 11
            *  生成二维码
            *  客户端生成一张二维码发送给服务器服务器保存 */
            case 11:
                /* 服务器并不保存图片，仅仅保存二维码的内容（请求）
                * 当添加好友时也仅仅是获取到二维码请求，然后
                * 发送给服务端，服务端判断此请求是否有效及内容
                * */
                /* 获取用户账户 */
                String account11 = js.getString("account");
                /* 获取权限 */
                int authority = js.getInt("authority");
                /* 获取超时时间 */
                int time_out = js.getInt("time_out");
                /* 生成插入QRcode数据的sql语句 */
                String insertQRcodeSql = "insert into QRcode (uid, authority, time_out) values ((" +
                        "select uid from UserInfo where account = \"" + account11  + "\"), \"" + authority +
                        "\", \"" + time_out + "\")";
                /* 从数据库连接池中获取数据库连接 */
                Connection insertQRcodeConn = XlDbPoll.getConnection();
                PreparedStatement insertQRcodePst = insertQRcodeConn.prepareStatement(insertQRcodeSql);
                int insertQRcodeRet = insertQRcodePst.executeUpdate();
                /* 构建result，返回给客户端 */
                if(insertQRcodeRet == 1){
                        result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                                "\"result\":{\"requestPhoneNum\":\"" + account11 + "\", \"IsSuccess\":\"success\"," +
                                "\"mark\":" + mark + ",\"ResultINFO\":\"新建二维码成功\"}}";
                }else {
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + account11 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"新建二维码失败\"}}";
                }
                break;


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
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + acconut13 + "\", \"IsSuccess\":\"success\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"删除成功\"}}";
                }else{
                    result = "{\"error\":0, \"status\":\"success\", \"date\":\"2015-08\", " +
                            "\"result\":{\"requestPhoneNum\":\"" + acconut13 + "\", \"IsSuccess\":\"failure\"," +
                            "\"mark\":" + mark + ",\"ResultINFO\":\"删除失败\"}}";
                }
                break;
        }
        return result;
    }
}
