package Action;

import Server.OtherServies;
import Server.UserServers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.DataInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mysteriouseyes on 2018/9/19.
 */
public class ServiceGetMsgAction extends Thread{
    private Socket socket;
    private Integer i = 0;
    private UserServers userServers;
    private List<Socket> list;
    private int uid;
    private String SYSTEM = "【系统提示】:";
    private String SUCCESS = "发送成功";
    private String IS_NOT_A_FRIEND = "你们还不是好友";
    private String OFFLINE = "离线";
    private String ONLINE = "在线";
    private String RULES_OF_THE_DAMAGE = "消息格式不正确";

    public ServiceGetMsgAction(Socket socket, UserServers userServers, int uid, List<Socket> list){
        this.socket = socket;
        this.userServers = userServers;
        this.uid = uid;
        this.list = list;
    }

    public void run() {
        String ip = socket.getInetAddress().getHostAddress();
        try {
            while (true){
                if(i != 0){
                    //读（接收）取客户端信息
                    /** 向某个人发送消息时需要先查询它是否是你的好友，如果是，则可以发送 **/
                    /** 点对点消息发送格式{type:u,uid:123,msg:你好}**/
                    /** 如果第一个是u说明这是一个点对点，是g说明是一个点对多， 是r说明这是一个交友请求 **/
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String msg = in.readUTF();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(msg, JsonObject.class);
                    String type = jsonObject.get("type").getAsString();
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("time",UserServers.GetTime());
                    System.out.println("type:"+type+",origin:"+jsonObject.get("type"));
                    /** 这是一个点对点的消息 {type:u,uid:1,msg:hello}**/
                    if(type.equals("u")){
                        int fid = jsonObject.get("uid").getAsInt();
                        msg = jsonObject.get("msg").getAsString();
                        hashMap.put("type", "u");
                        if(userServers.FindByFid(fid, uid)){
                            String fip = userServers.FindByFidState(fid);
                            if(!fip.equals("false")){
                                //向好友写（发送）信息
                                for(Socket li : list){
                                    if(fip.equals(li.toString())){
                                        hashMap.put("uid", uid);
                                        hashMap.put("msg", msg);
                                        OtherServies.SendMsg(hashMap, li);
                                    }
                                }
                            }
                            userServers.InsertMsg(uid, fid, msg);
                            //向客户端写（发送）信息
                            OtherServies.SendMsg(hashMap,socket);
                        }else {
                            hashMap.put("msg", IS_NOT_A_FRIEND);
                            OtherServies.SendMsg(hashMap,socket);
                        }
                    }
                    /** 这是一个点对多的消息{type:g,gid:1,msg:hello} **/
                    else if(type.equals("g")){
                        int gid = jsonObject.get("gid").getAsInt();
                        msg = jsonObject.get("msg").getAsString();
                        List glists = UserServers.FindByGid(gid);
                        hashMap.put("type", "g");
                        for(Object fid : glists){
                            String state = UserServers.FindByFidState(Integer.valueOf(fid.toString()));
                            if(!state.equals("false")){
                                for(Socket fsocket : list){
                                    if(state.equals(fsocket.toString())){
                                        hashMap.put("gid", gid);
                                        hashMap.put("uid", uid);
                                        hashMap.put("msg", msg);
                                        OtherServies.SendMsg(hashMap, fsocket);
                                    }
                                }
                            }
                        }
                        UserServers.InsertMsgToGroupMsg(uid, gid, msg);
                    }
                    /**这是一个交友请求，格式rfid+msg **/
                    /**注意，这里的请求有一点不同，这一个请求还是会发送到对方，但是不一样的是，这个请求
                     * 是存储在【请求消息表】中的，如果msg为true，则证明同意加好友的请求，在设计页面的时候注意
                     * 这个消息不会显示在消息页面，而是显示在好友请求页面{type:r, uid:1, msg:hello}**/
                    else if(type.equals("r")){
                        int fid = jsonObject.get("uid").getAsInt();
                        msg = jsonObject.get("msg").getAsString();
                        hashMap.put("type", "r");
                        String state = UserServers.FindByFidState(fid);
                        if(!msg.equals("true")){
                            if(!state.equals("false")){
                                for(Socket li : list){
                                    if(state.equals(li.toString())){
                                        hashMap.put("uid", uid);
                                        hashMap.put("msg", msg);
                                        OtherServies.SendMsg(hashMap,li);
                                    }
                                }
                            }
                            UserServers.InsertMsgToRequestMsg(uid, fid, msg);
                        }else {
                            if(UserServers.InsertFriend(uid, fid)){
                                hashMap.put("uid", fid);
                                hashMap.put("msg", "你们已经是好友了，一起来聊天吧");
                                OtherServies.SendMsg(hashMap,socket);
                            }
                        }
                    }
                    /**这是一个申请加群的请求，只会发送给群主，格式为qgid+msg-rid,注意事项同加好友申请
                     * rid为申请人id, rid只会在请求被同意时出现{type:q, gid:12, msg:true, uid:2}**/
                    else if(type.equals("q")){
                        int gid = jsonObject.get("gid").getAsInt();
                        String msg1 = jsonObject.get("msg").getAsString();
                        hashMap.put("type", "q");
                        if(!msg1.equals("true")){
                            int g_master = UserServers.FindByGidForGroups(gid);
                            String state = UserServers.FindByFidState(g_master);
                            if(!state.equals("false")){
                                for(Socket li : list){
                                    if(state.equals(li.toString())){
                                        hashMap.put("gid", gid);
                                        hashMap.put("uid", uid);
                                        hashMap.put("msg", msg1);
                                        OtherServies.SendMsg(hashMap,li);
                                    }
                                }
                            }
                            UserServers.InsertRGMsgToRGM(gid, uid, msg1);
                        }else {
                            int rid = jsonObject.get("uid").getAsInt();
                            UserServers.UpdatePersonInGroups(rid, gid);
                            UserServers.UpdateGidListInUser(rid, gid);
                            List glists = UserServers.FindByGid(gid);
                            for(Object fid : glists){
                                String state = UserServers.FindByFidState(Integer.valueOf(fid.toString()));
                                if(!state.equals("false")){
                                    for(Socket fsocket : list){
                                        if(state.equals(fsocket.toString())){
                                            hashMap.put("gid", gid);
                                            hashMap.put("uid", rid);
                                            hashMap.put("msg", "已经是群成员了");
                                            OtherServies.SendMsg(hashMap, fsocket);
                                        }
                                    }
                                }
                            }
                        }

                    }else{
                        hashMap.put("type", "error");
                        hashMap.put("msg", RULES_OF_THE_DAMAGE);
                        OtherServies.SendMsg(hashMap, socket);
                    }
                    System.out.println("【"+socket.getInetAddress().getHostAddress()+"】:"+msg);
                }
                i ++;
                if(i == 255) i = 1;
            }
        }catch (Exception e){
            if(e.getMessage().equals("Connection reset")){
                System.out.println("【"+ip+"】已经断开连接");
                userServers.UpdateIpAndStateForUid(socket.toString(), OFFLINE, uid);
                list.remove(socket);
                System.out.println("在线列表:"+list);
                try {
                    socket.close();
                }catch (Exception v){
                    System.out.printf(v.toString());
                }
            }else {
                System.out.println(e.toString());
            }
        }
    }
}
