package com.example.exchange;



import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Server
{
    //服务器端口
    private static final int SERVERPORT = 54321;
    //客户端连接
    private static List<Map<String,Object>> mClientList = new ArrayList<>();
    //线程池
    private ExecutorService mExecutorService;
    //ServerSocket对象
    private Connection dbConn;
    //连接
    private ServerSocket mServerSocket;
    //开启服务器
    public static void main(String[] args)
    {
        new Server();
    }
    public Server()
    {
        try
        {
            //设置服务器端口
            mServerSocket = new ServerSocket(SERVERPORT);
            //创建一个线程池
            mExecutorService = Executors.newCachedThreadPool();
            System.out.println("start...");
            //用来临时保存客户端连接的Socket对象
            Socket client = null;
            while (true)
            {
                //接收客户连接并添加到list中
                client = mServerSocket.accept();
                Map<String,Object>tmp=new LinkedHashMap<>();
                tmp.put("client",client);
                mClientList.add(tmp);
                //开启一个客户端线程
                mExecutorService.execute(new ThreadServer(client));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //每个客户端单独开启一个线程
    static class ThreadServer implements Runnable
    {
        private Socket			mSocket;
        private BufferedReader	mBufferedReader;
        private PrintWriter		mPrintWriter;
        private String			mStrMSG;

        public ThreadServer(Socket socket) throws IOException
        {
            this.mSocket = socket;
            mBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            mStrMSG = "user:"+this.mSocket.getInetAddress()+" come total:" + mClientList.size();
            sendMessage();
        }
        public void run()
        {
            try
            {
                while ((mStrMSG = mBufferedReader.readLine()) != null)
                {
                    if (mStrMSG.trim().equals("exit"))
                    {
                        //当一个客户端退出时
                        mClientList.remove(mSocket);
                        mBufferedReader.close();
                        mPrintWriter.close();
                        mStrMSG = "user:"+this.mSocket.getInetAddress()+" exit total:" + mClientList.size();
                        mSocket.close();
                        sendMessage();
                        break;
                    }
                    else
                    {
                        mStrMSG = mSocket.getInetAddress() + ":" + mStrMSG;
                        sendMessage();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //发送消息给所有客户端
        private void sendMessage() throws IOException
        {
            System.out.println(mStrMSG);
            for (Socket client : mClientList)
            {
                mPrintWriter = new PrintWriter(client.getOutputStream(), true);
                mPrintWriter.println(mStrMSG);
            }
        }
    }

    public int findClientIndexByUsername(String username){
        int find=-1;
        for(int index=0;index<mClientList.size();index++){
            if(mClientList.get(index).get("username").equals(username)){
                find=index;
                break;
            }
        }
        return find;
    }
0
    public int getCache(String username){

    }

    public boolean connect(){
        String connectString = "jdbc:mysql://172.18.59.33:3306/webdb"
                + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&&useSSL=false";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConn = DriverManager.getConnection(connectString, "wt", "1234");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

