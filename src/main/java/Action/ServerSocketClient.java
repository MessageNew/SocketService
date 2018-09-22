package Action;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;

/**
 * Created by Mysteriouseyes on 2018/9/17.
 */
public class ServerSocketClient {

    public static void main(String[] args){
        new Connecting("127.0.0.1", 8000).start();
    }

    static class Connecting extends Thread{
        private String ip;
        private int port;

        public Connecting(String ip, int port){
            this.ip = ip;
            this.port = port;
        }

        public void run() {
            try {
                System.out.println("Connecting to " + ip + " on port " + port);
                Socket socket = new Socket(ip, port); //连接服务端
                System.out.println("Just connected to " + socket.getRemoteSocketAddress());
                new GetMsg(socket).start();
                OutputStream outToServer = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);
                /** 用户登录到服务器，同时发送uid给服务器做备份 **/
                out.writeUTF("1");
                while (true){
                    //向服务端发送信息
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//                    OutputStream outToServer = socket.getOutputStream();
//                    DataOutputStream out = new DataOutputStream(outToServer);
                    out.writeUTF(bufferedReader.readLine());
                }
            }catch (Exception e){
                if(e.getMessage().equals("Connection refused: connect")){
                    System.out.println("连接服务器失败,1秒后尝试重新连接");
                    try {
                        sleep(1000);
                        run();
                    }catch (Exception e0){
                        System.out.println(e0.toString());
                    }
                }else {
                    System.out.println(e.toString());
                }
            }
        }
    }

    static class GetMsg extends Thread{
        Socket socket;

        public GetMsg(Socket socket){
            this.socket = socket;
        }

        public void run() {
            try {
                while (true){
                    //从服务端读取信息
                    InputStream inFromServer = socket.getInputStream();
                    DataInputStream in = new DataInputStream(inFromServer);
                    String msg = in.readUTF();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(msg, JsonObject.class);
                    JsonObject msgJ = gson.fromJson(jsonObject.get("data").toString(), JsonObject.class);
                    String msgM = msgJ.get("msg").getAsString();
                    msgM = msgToDou(msgM);
                    msgM = jiemi(msgM);
                    msgM = asciiToString(msgM);
                    System.out.println(msg);
                    System.out.println(msgM);
                }
            }catch (Exception e){
                if(e.getMessage().equals("Connection reset")){
                    System.out.println("服务器断开连接，请重新启动程序");
                }else {
                    System.out.println(e.toString());
                }
            }
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
            msg = msg.replace("，", ",");
            return msg;
        }

        public static String jiemi(String msg){
            String result = "";
            for(String a : msg.split(",")){
                int b = Integer.valueOf(a);
                b = (b - 5)/10;
                result += String.valueOf(b)+",";
            }
            result = result.substring(0, result.length() - 1);
            return result;
        }
    }
}
