package Server;

import beans.SqlData;
import db.Jdbc;

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

}
