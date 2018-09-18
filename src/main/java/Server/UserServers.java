package Server;

import beans.ResultSql;
import beans.SqlData;
import db.Jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String FindByFidState(int fid){
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
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = df.format(date);
        String sql = "insert into user_message(put_user, get_user, message, time) values("+uid+", "+fid+", '"+msg+"', '"+time+"')";
        if(Jdbc.AddData(new SqlData(), Jdbc.Jdbc(), sql)){
            return true;
        }
        return false;
    }

}
