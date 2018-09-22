package Server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("Info", "成功");
            Gson gson = new Gson();
            jsonObject.add("data", gson.fromJson(data.toString(), jsonObject.getClass()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(jsonObject.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

}
