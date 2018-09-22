import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.lang.StringBuffer;

import java.util.HashMap;

/**
 * Created by Mysteriouseyes on 2018/9/20.
 */
public class Test {
    public static void main(String[] args){
        JsonObject jsonObject = new JsonObject();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        String msg = "你好,彭友";
        msg = msg.replace(",", "，");
        msg = stringToAscii(msg);
        System.out.println(msg);
        String nmsg = "";
        for(String a : msg.split(",")){
            int b = Integer.valueOf(a);
            b = b*10+5;
            nmsg += String.valueOf(b)+",";
        }
        msg = nmsg.substring(0, nmsg.length() - 1);
        msg = msg.replace(",", "，");
        hashMap.put("key", msg);
        Gson gson = new Gson();
        jsonObject.add("data", gson.fromJson(hashMap.toString(), jsonObject.getClass()));
        System.out.printf(jsonObject.toString());
        System.out.println(jsonObject.get("data"));
        JsonObject jsonObject1 = gson.fromJson(jsonObject.get("data"), jsonObject.getClass());
        String string = jsonObject1.get("key").getAsString();
        string = string.replace("，", ",");
        System.out.println(asciiToString(string));
        String newString = "";
        for (String a : string.split(",")){
            int b = Integer.valueOf(a);
            b = (b - 5)/10;
            newString += String.valueOf(b)+",";
        }
        newString = newString.substring(0, newString.length() - 1);
        System.out.println(asciiToString(newString));
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
}
