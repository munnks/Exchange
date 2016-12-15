package com.example.exchange;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatActivity extends AppCompatActivity {

    TextView history,name;
    EditText edit;
    Button send;
    String myusername,username;
    String stu_name;
    ScrollView scrollView;

    private final String		DEBUG_TAG	= "chat";
    //服务器IP、端口
    private static final String SERVERIP = "172.18.57.143";
    private static final int SERVERPORT = 54321;
    private static final String DIVIDER="/　/";
    private Thread mThread = null;
    private Socket             mSocket		= null;
    private BufferedReader mBufferedReader	= null;
    private PrintWriter mPrintWriter = null;
    private  String mStrMSG = "";
    private ExchangeDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        history=(TextView)findViewById(R.id.history);
        edit=(EditText)findViewById(R.id.edit);
        name=(TextView)findViewById(R.id.name);
        send=(Button)findViewById(R.id.send);
        scrollView=(ScrollView)findViewById(R.id.scrollView);

        Bundle bundle=this.getIntent().getExtras();
        myusername=bundle.getString("myusername");
        username=bundle.getString("username");

        db=new ExchangeDatabase();
        db.connect();
        stu_name=(String)db.selectStu(username).get("stu_name");
        name.setText(stu_name);

        //登陆
        try
        {
            //连接服务器
            mSocket = new Socket(SERVERIP, SERVERPORT);
            //取得输入、输出流
            mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mPrintWriter=new PrintWriter(mSocket.getOutputStream(), true);
        }
        catch (Exception e)
        {
            // TODO: handle exception

            Log.e(DEBUG_TAG, e.toString());
            e.printStackTrace();
            Toast.makeText(ChatActivity.this, "无法连接服务器", Toast.LENGTH_SHORT).show();
        }

        try {
            //初始化
            mPrintWriter.print("initial"+DIVIDER+myusername);
            mPrintWriter.flush();
        }
        catch (Exception e){

            e.printStackTrace();
        }



        //发送消息
        send.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    //取得编辑框中我们输入的内容
                    String str = edit.getText().toString();
                    StringBuffer sb=new StringBuffer();
                    sb.append("msg").append(DIVIDER)
                            .append(username).append(DIVIDER)
                            .append(myusername).append(DIVIDER)
                            .append(str).append(DIVIDER)
                            .append(db.getDate()).append(DIVIDER)
                            .append(db.getTime());
                    //发送给服务器
                    mPrintWriter.print(str);
                    mPrintWriter.flush();
                    history.append(" "+db.getDate()+" "+db.getTime()+"\n我:"+str+"\n");
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部

                }
                catch (Exception e)
                {
                    // TODO: handle exception
                    Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    Log.e(DEBUG_TAG, e.toString());
                }
            }
        });
        mThread = new Thread(mRunnable);
        mThread.start();
    }
    //线程:监听服务器发来的消息
    private Runnable	mRunnable	= new Runnable()
    {
        public void run()
        {
            while (true)
            {
                try
                {
                    if ( (mStrMSG = mBufferedReader.readLine()) != null )
                    {
                        //接收消息并处理
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                    // 发送消息
                }
                catch (Exception e)
                {
                    Log.e(DEBUG_TAG, e.toString());
                }
            }
        }
    };

    Handler	mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            // 刷新
            try
            {
                String arr[]=mStrMSG.split(DIVIDER);
                //将聊天记录添加进来
                history.append(" "+arr[4]+" "+arr[5]+"\n"+stu_name+":"+arr[3]+"\n");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN); //滚动到底部
            }
            catch (Exception e)
            {
                Log.e(DEBUG_TAG, e.toString());
            }
        }
    };
}
