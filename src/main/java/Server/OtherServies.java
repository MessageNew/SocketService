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
            String msg = "";
            if(data.get("msg") != null){
                msg = data.get("msg").toString();
                msg = stringToAscii(msg);
                msg = jiami(msg);
                msg = msgToDou(msg);
                data.put("msg", msg);
            }
            jsonObject.add("data", gson.fromJson(data.toString(), jsonObject.getClass()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(jsonObject.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    public static String stringToAscii(String value){
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }
    public static String asciiToString(String value)
    {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    public static String msgToDou(String msg){
        msg = msg.replace(",", "，");
        return msg;
    }

    public static String jiami(String msg){
        String miwen = "";
        for(String a : msg.split(",")){
            int b = Integer.valueOf(a);
            b = b * 10 + 5;
            miwen += String.valueOf(b)+",";
        }
        miwen = miwen.substring(0, miwen.length() - 1);
        return miwen;
    }

}
