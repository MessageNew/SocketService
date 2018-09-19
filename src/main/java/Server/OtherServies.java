package Server;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Mysteriouseyes on 2018/9/19.
 */
public class OtherServies {

    /**
     * 发送消息
     * @param msg
     * @param socket
     */
    public static void SendMsg(String msg, Socket socket){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(msg);
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

}
