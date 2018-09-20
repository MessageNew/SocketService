package Action;

import Server.OtherServies;
import Server.UserServers;

import java.io.DataInputStream;
import java.net.Socket;
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
                    /** 点对点消息发送格式 ufid+msg **/
                    /** 如果第一个是u说明这是一个点对点，是g说明是一个点对多， 是r说明这是一个交友请求 **/
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    String msg = in.readUTF();
                    String total = msg.substring(0, 1);
                    /** 这是一个点对点的消息 **/
                    if(total.equals("u")){
                        int fid = Integer.valueOf(msg.substring(1, msg.indexOf("+")));
                        msg = msg.substring(msg.indexOf("+")+1, msg.length());
                        if(userServers.FindByFid(fid, uid)){
                            String fip = userServers.FindByFidState(fid);
                            if(!fip.equals("false")){
                                //向好友写（发送）信息
                                for(Socket li : list){
                                    if(fip.equals(li.toString())){
                                        OtherServies.SendMsg("【"+uid+"】:"+msg, li);
                                    }
                                }
                            }
                            userServers.InsertMsg(uid, fid, msg);
                            //向客户端写（发送）信息
                            OtherServies.SendMsg(SYSTEM+SUCCESS,socket);
                        }else {
                            OtherServies.SendMsg(SYSTEM+IS_NOT_A_FRIEND,socket);
                        }
                    }
                    /** 这是一个点对多的消息 **/
                    else if(total.equals("g")){
                        int gid = Integer.valueOf(msg.substring(1, msg.indexOf("+")));
                        msg = msg.substring(msg.indexOf("+")+1, msg.length());
                        List glists = UserServers.FindByGid(gid);
                        for(Object fid : glists){
                            String state = UserServers.FindByFidState(Integer.valueOf(fid.toString()));
                            if(!state.equals("false")){
                                for(Socket fsocket : list){
                                    if(state.equals(fsocket.toString())){
                                        OtherServies.SendMsg("【"+gid+"】【"+uid+"】:"+msg, fsocket);
                                    }
                                }
                            }
                        }
                        UserServers.InsertMsgToGroupMsg(uid, gid, msg);
                    }
                    /**这是一个交友请求，格式rfid+msg **/
                    /**注意，这里的请求有一点不同，这一个请求还是会发送到对方，但是不一样的是，这个请求
                     * 是存储在【请求消息表】中的，如果msg为true，则证明同意加好友的请求，在设计页面的时候注意
                     * 这个消息不会显示在消息页面，而是显示在好友请求页面**/
                    else if(total.equals("r")){
                        int fid = Integer.valueOf(msg.substring(1, msg.indexOf("+")));
                        msg = msg.substring(msg.indexOf("+")+1, msg.length());
                        String state = UserServers.FindByFidState(fid);
                        if(!msg.equals("true")){
                            if(!state.equals("false")){
                                for(Socket li : list){
                                    if(state.equals(li.toString())){
                                        OtherServies.SendMsg("【好友请求】:【"+uid+"】"+msg,li);
                                    }
                                }
                            }
                            UserServers.InsertMsgToRequestMsg(uid, fid, msg);
                        }else {
                            if(UserServers.InsertFriend(uid, fid)){
                                OtherServies.SendMsg(SYSTEM+fid+"你们已经是好友了，一起来聊天吧",socket);
                            }
                        }
                    }
                    /**这是一个申请加群的请求，只会发送给群主，格式为qgid+msg-rid,注意事项同加好友申请
                     * rid为申请人id, rid只会在请求被同意时出现**/
                    else if(total.equals("q")){
                        int gid = Integer.valueOf(msg.substring(1, msg.indexOf("+")));
                        String msg1 = msg.substring(msg.indexOf("+")+1, msg.indexOf("-"));
                        if(!msg1.equals("true")){
                            int g_master = UserServers.FindByGidForGroups(gid);
                            String state = UserServers.FindByFidState(g_master);
                            if(!state.equals("false")){
                                for(Socket li : list){
                                    if(state.equals(li.toString())){
                                        OtherServies.SendMsg("【加群提示】:群【"+gid+"】【"+uid+"】"+msg1,li);
                                    }
                                }
                            }
                            UserServers.InsertRGMsgToRGM(gid, uid, msg1);
                        }else {
                            int rid = Integer.valueOf(msg.substring(msg.indexOf("-")+1, msg.length()));
                            UserServers.UpdatePersonInGroups(rid, gid);
                        }

                    }else{
                        OtherServies.SendMsg(SYSTEM+RULES_OF_THE_DAMAGE, socket);
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
