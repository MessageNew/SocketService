

import Server.UserServers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mysteriouseyes on 2018/9/17.
 */
public class ServerSocketServer extends Thread{

    private static List<Socket> list;
    private Integer port;

    private UserServers userServers;
    public ServerSocketServer(int port){
        this.port = port;
        userServers = new UserServers();
    }

    public void run(){
        list = new ArrayList<Socket>();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
            while (true){
                Socket socket = serverSocket.accept(); //等待客户端连接
                list.add(socket);
                /** 获取登录uid信息 **/
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String uid = in.readUTF();
                userServers.UpdateIpAndStateForUid(socket.toString(), "在线", Integer.valueOf(uid));
                System.out.println("在线列表:"+list);
                System.out.println("【"+socket.getRemoteSocketAddress()+"】已上线");
                new GetMsg(socket, userServers, Integer.valueOf(uid)).start();
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }

    }

    static class GetMsg extends Thread{
        private Socket socket;
        private Integer i = 0;
        private UserServers userServers;
        private int uid;

        public GetMsg(Socket socket, UserServers userServers, int uid){
            this.socket = socket;
            this.userServers = userServers;
            this.uid = uid;
        }

        public void run() {
            String ip = socket.getInetAddress().getHostAddress();
            try {
                while (true){
                    if(i != 0){
                        //读（接收）取客户端信息
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        String msg = in.readUTF();
                        System.out.println("【"+socket.getInetAddress().getHostAddress()+"】:"+msg);
                        //向客户端写（发送）信息
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF("发送成功");
                        //全局发送消息
                        for(Socket li : list){
                            DataOutputStream outputStream = new DataOutputStream(li.getOutputStream());
                            out.writeUTF("【"+li.getInetAddress().getHostAddress()+"】:"+msg);
                        }
                    }
                    i ++;
                    if(i == 255){
                        i = 1;
                    }
                }
            }catch (Exception e){
                if(e.getMessage().equals("Connection reset")){
                    System.out.println("【"+ip+"】已经断开连接");
                    userServers.UpdateIpAndStateForUid(socket.toString(), "离线", uid);
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
}
