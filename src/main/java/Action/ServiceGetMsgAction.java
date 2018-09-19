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
                    /** 如果第一个是u说明这是一个点对点，是g说明是一个点对多 **/
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
                    }else {
                        OtherServies.SendMsg(SYSTEM+RULES_OF_THE_DAMAGE, socket);
                    }
                    System.out.println("【"+socket.getInetAddress().getHostAddress()+"】:"+msg);
                }
                i ++;
                if(i == 255){
                    i = 1;
                }
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
