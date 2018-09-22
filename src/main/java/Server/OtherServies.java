package Server;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by Mysteriouseyes on 2018/9/19.
 */
public class OtherServies {

    /**
     * 发送消息
     */
    public static void SendMsg(HashMap data, Socket socket){
        try {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("code", 200);
            hashMap.put("Info", "成功");
            hashMap.put("data", data);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(hashMap.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

}
