package Server;

import beans.ResultSql;
import beans.SqlData;
import db.Jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Mysteriouseyes on 2018/9/18.
 */
public class UserServers {

    public void UpdateIpAndStateForUid(String uip, String state, int uid){
        String sql = "update User u set u.ip = ? , u.state = ? where u.uid =?";
        SqlData sqlData = new SqlData();
        sqlData.setColumnValue1(uip);
        sqlData.setColumnValue2(state);
        sqlData.setColumnValue3(uid);
        Jdbc.UpdateData(sqlData, Jdbc.Jdbc(), sql);
    }

    /**
     * 判断是否是好友
     * @param fid
     * @param uid
     * @return
     */
    public boolean FindByFid(int fid, int uid){
        try {
            String sql = "select * from friend f where f.uid = ? and f.fid = ?";
            SqlData sqlData = new SqlData();
            sqlData.setColumnValue1(uid);
            sqlData.setColumnValue2(fid);
            Connection conn = Jdbc.Jdbc();
            ResultSql resultSql = Jdbc.SearchData(sqlData, conn, sql);
            ResultSet rs = resultSql.getRs();
            if(rs.next()){
                rs.close();
                resultSql.getPstmt().close();
                conn.close();
                return true;
            }
            rs.close();
            resultSql.getPstmt().close();
            conn.close();
            return false;
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
    }

    public static String FindByFidState(int fid){
        try {
            String sql = "select * from user u where u.uid = ?";
            SqlData sqlData = new SqlData();
            sqlData.setColumnValue1(fid);
            Connection conn = Jdbc.Jdbc();
            ResultSql resultSql = Jdbc.SearchData(sqlData, conn, sql);
            ResultSet rs = resultSql.getRs();
            if(rs.next()){
                String state = rs.getString("state");
                if(state ==null || state.equals("离线")){
                    rs.close();
                    resultSql.getPstmt().close();
                    conn.close();
                    return "false";
                }
                String ip = rs.getString("ip");
                rs.close();
                resultSql.getPstmt().close();
                conn.close();
                return ip;
            }
            rs.close();
            resultSql.getPstmt().close();
            conn.close();
            return "false";
        }catch (Exception e){
            System.out.println(e.toString());
            return "false";
        }
    }

    public boolean InsertMsg(int uid, int fid, String msg){
        String time = GetTime();
        String sql = "insert into user_message(put_user, get_user, message, time) values("+uid+", "+fid+", '"+msg+"', '"+time+"')";
        if(Jdbc.AddData(new SqlData(), Jdbc.Jdbc(), sql)){
            return true;
        }
        return false;
    }

    public static boolean InsertMsgToGroupMsg(int uid, int gid, String msg){
        String time = GetTime();
        String sql = "insert into group_message(msg, time, uid, gid) values('"+msg+"', '"+time+"', "+uid+", "+gid+")";
        if(Jdbc.AddData(new SqlData(), Jdbc.Jdbc(), sql)){
            return true;
        }
        return false;
    }

    public static boolean InsertMsgToRequestMsg(int uid, int fid, String msg){
        String time = GetTime();
        String sql = "insert into request_msg(message, get_user, put_user, time) values('"+msg+"', "+fid+", "+uid+", '"+time+"')";
        if(Jdbc.AddData(new SqlData(), Jdbc.Jdbc(), sql)){
            return true;
        }
        return false;
    }

    public static boolean InsertFriend(int uid, int fid){
        String time = GetTime();
        String sql = "insert into friend(fid, uid, time) values("+fid+", "+uid+", '"+time+"')";
        if(Jdbc.AddData(new SqlData(), Jdbc.Jdbc(), sql)){
            sql = "insert into friend(fid, uid, time) values("+uid+", "+fid+", '"+time+"')";
            if(Jdbc.AddData(new SqlData(), Jdbc.Jdbc(), sql)){
                return true;
            }
            return false;
        }
        return false;
    }

    public static int FindByGidForGroups(int gid){
        try {
            String sql = "select * from groups where g_id = ?";
            SqlData sqlData = new SqlData();
            sqlData.setColumnValue1(gid);
            Connection conn = Jdbc.Jdbc();
            ResultSql resultSql = Jdbc.SearchData(sqlData, conn, sql);
            ResultSet rs = resultSql.getRs();
            if(rs.next()){
                int master = Integer.valueOf(rs.getString("g_master"));
                rs.close();
                resultSql.getPstmt().close();
                conn.close();
                return master;
            }
            rs.close();
            resultSql.getPstmt().close();
            conn.close();
            return 0;
        }catch (Exception e){
            System.out.println(e.toString());
            return 0;
        }
    }

    public static boolean InsertRGMsgToRGM(int gid, int uid, String msg){
        String time = GetTime();
        String sql = "insert into requestgmsg(gid, msg, uid, time) values("+gid+", '"+msg+"', "+uid+", '"+time+"')";
        if(Jdbc.AddData(new SqlData(), Jdbc.Jdbc(), sql)){
            return true;
        }
        return false;
    }

    public static boolean UpdatePersonInGroups(int uid, int gid){
        List list = FindByGid(gid);
        String lists = list.toString().replace(" ","");
        System.out.println(lists);
        lists = lists.substring(1, lists.length()-1);
        lists += ","+String.valueOf(uid);
        System.out.println(lists);
        String sql = "update groups set g_personnel = ? where g_id = ?";
        SqlData sqlData = new SqlData();
        sqlData.setColumnValue1(lists);
        sqlData.setColumnValue2(gid);
        if(Jdbc.UpdateData(sqlData, Jdbc.Jdbc(), sql)){
            return true;
        }
        return false;
    }

    public static List FindByGid(int gid){
        try {
            String sql = "select * from groups where g_id = ?";
            SqlData sqlData = new SqlData();
            sqlData.setColumnValue1(gid);
            Connection conn = Jdbc.Jdbc();
            ResultSql resultSql = Jdbc.SearchData(sqlData, conn, sql);
            ResultSet rs = resultSql.getRs();
            if(rs.next()){
                List list = Arrays.asList(rs.getString("g_personnel").split(","));
                rs.close();
                resultSql.getPstmt().close();
                conn.close();
                return list;
            }
            rs.close();
            resultSql.getPstmt().close();
            conn.close();
            return new ArrayList();
        }catch (Exception e){
            System.out.println(e.toString());
            return new ArrayList();
        }
    }

    public static String GetTime(){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(date);
    }

}
