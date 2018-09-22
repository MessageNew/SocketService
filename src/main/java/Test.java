import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mysteriouseyes on 2018/9/20.
 */
public class Test {
    public static void main(String[] args){
        JsonObject jsonObject = new JsonObject();
        HashMap hashMap = new HashMap();
        hashMap.put("key", "value");
        Gson gson = new Gson();
        jsonObject.add("data", gson.fromJson(hashMap.toString(), jsonObject.getClass()));
        System.out.printf(jsonObject.toString());
    }
}
