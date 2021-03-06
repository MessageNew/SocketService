package Action;

import Server.UserServers;

import java.io.DataInputStream;
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
    private String ONLINE = "在线";

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
                System.out.println("等待客户端连接");
                Socket socket = serverSocket.accept(); //等待客户端连接
                System.out.println("客户端连接成功");
                list.add(socket);
                System.out.println("客户端连接成功1");
                /** 获取登录uid信息 **/
                DataInputStream in = new DataInputStream(socket.getInputStream());
                System.out.println("客户端连接成功2");
                String uid = in.readUTF();
                System.out.println("客户端连接成功3");
                System.out.println("UID:"+uid);
                userServers.UpdateIpAndStateForUid(socket.toString(), ONLINE, Integer.valueOf(uid));
                System.out.println("客户端连接成功4");
                System.out.println("在线列表:"+list);
                System.out.println("客户端连接成功5");
                System.out.println("【"+socket.getRemoteSocketAddress()+"】已上线");
                System.out.println("客户端连接成功6");
                new ServiceGetMsgAction(socket, userServers, Integer.valueOf(uid), list).start();
            }
        }catch (Exception e){
            System.out.println("服务器:"+e);
            System.out.println(e.toString());
        }

    }
}
