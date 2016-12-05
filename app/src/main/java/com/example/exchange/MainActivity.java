package com.example.exchange;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    private final static String checkcodeURL="http://uems.sysu.edu.cn/elect/login/code";
    private final static String electURL="http://uems.sysu.edu.cn/elect";
    private final static String loginURL="http://uems.sysu.edu.cn/elect/login";
    EditText username;
    EditText password;
    EditText checkcode;
    ImageView checkcodeImg;
    String mpassword;
    String sessionId="";
    Bitmap bitmap;
    boolean connect_finish=false;
   // HttpClient client;
    //OkHttpClient client;


    private final static int CHECK_CODE=111;
    private final static int LOGIN_CODE=222;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        checkcode=(EditText)findViewById(R.id.checkcode);
        checkcodeImg=(ImageView)findViewById(R.id.img_checkcode);
        Button login=(Button)findViewById(R.id.login);
        //client=new OkHttpClient();
        //client=new DefaultHttpClient();
        //getCheckCodeWithClient();
        connectElectSystem();
        //Log.i("miao","miao");
        checkcodeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCheckCode();
                //getCheckCodeWithClient();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
                try {
                    byte[] btInput = password.getText().toString().getBytes();
                    // 获得MD5摘要算法的 MessageDigest 对象
                    MessageDigest mdInst = MessageDigest.getInstance("MD5");
                    // 使用指定的字节更新摘要
                    mdInst.update(btInput);
                    // 获得密文
                    byte[] md = mdInst.digest();
                    // 把密文转换成十六进制的字符串形式
                    int j = md.length;
                    char str[] = new char[j * 2];
                    int k = 0;
                    for (int i = 0; i < j; i++) {
                        byte byte0 = md[i];
                        str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                        str[k++] = hexDigits[byte0 & 0xf];
                    }
                    mpassword=new String(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loginElectSystem();
                //loginWithClient();
                //Toast.makeText(MainActivity.this, mpassword, Toast.LENGTH_SHORT).show();
            }
        });

    }
/*
    private void getCheckCodeWithClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Request request=new Request.Builder().url(checkcodeURL).build();
                    Response response=client.newCall(request).execute();
                    InputStream in=response.body().byteStream();
                    bitmap= BitmapFactory.decodeStream(in);
                    handler.sendEmptyMessage(CHECK_CODE);
                    Log.i("response code",""+response.code());
                    Headers header=response.headers();
                    for(int i=0;i<header.size();i++){
                        Log.d("Check "+header.name(i),header.value(i));
                    }
                    get_code_finish=true;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loginWithClient(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!get_code_finish);
                    FormBody.Builder builder=new FormBody.Builder();
                    builder.add("username", username.getText().toString())
                            .add("password", mpassword)
                            .add("j_code", checkcode.getText().toString())
                            .add("lt", "")
                            .add("_eventId", "submit")
                            .add("gateway", "true");
                    RequestBody formBody=builder.build();
                    Request request=new Request.Builder()
                            .url(loginURL)
                            .post(formBody)
                            .build();
                    Response response=client.newCall(request).execute();
                    Log.i("response code",""+response.code());
                    Headers header=response.headers();
                    for(int i=0;i<header.size();i++){
                        Log.d("Check "+header.name(i),header.value(i));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
*/
    private void getCheckCode(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection cnct=(HttpURLConnection)(new URL(checkcodeURL)).openConnection();
                    if(sessionId.isEmpty()){
                        String cookieValue=cnct.getHeaderField("Set-Cookie");
                        sessionId=cookieValue.substring(0, cookieValue.indexOf(";"));
                        cnct.setRequestProperty("Set-Cookie",sessionId);
                    }else{
                        cnct.addRequestProperty("Cookie",sessionId);
                    }

                    //String cookieValue=cnct.getHeaderField("Set-Cookie");
                    //sessionId=cookieValue.substring(0, cookieValue.indexOf(";"));
                    //cnct.setRequestProperty("Set-Cookie",Cookie);
                    //Log.d("save cookie",sessionId);
                    //cnct.addRequestProperty("Cookie",sessionId);
                    //cnct.connect();

                    InputStream in=cnct.getInputStream();
                    showRequestHeader("check",cnct);
                    bitmap= BitmapFactory.decodeStream(in);
                    handler.sendEmptyMessage(CHECK_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void connectElectSystem(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection cnct=(HttpURLConnection)(new URL(checkcodeURL)).openConnection();
                    String cookieValue=cnct.getHeaderField("Set-Cookie");
                    Log.d("Set-Cookie",cookieValue);
                    sessionId=cookieValue.substring(0, cookieValue.indexOf(";"));
                    Log.d("sessionId",sessionId);
                    InputStream in=cnct.getInputStream();
                    showRequestHeader("check",cnct);
                    bitmap= BitmapFactory.decodeStream(in);
                    handler.sendEmptyMessage(CHECK_CODE);

                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(electURL)).openConnection();
                    connection.addRequestProperty("Cookie",sessionId);
                    showRequestHeader("connect",connection);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void loginElectSystem(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    StringBuilder sbR = new StringBuilder();
                    Log.d("save cookie",sessionId);
                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(loginURL)).openConnection();
                    connection.addRequestProperty("Cookie",sessionId);
                    connection.setRequestMethod("POST");//网页默认“GET”提交方式

                    StringBuffer sb = new StringBuffer();
                    sb.append("username=").append(username.getText().toString());
                    sb.append("&password=").append(mpassword);
                    sb.append("&j_code=").append(checkcode.getText().toString());
                    sb.append("&lt=&_eventId=submit&gateway=true");
                    Log.d("form data",sb.toString());
                    connection.setRequestProperty("Content-Length",
                            String.valueOf(sb.toString().length()));

                    OutputStream os = connection.getOutputStream();
                    os.write(sb.toString().getBytes());
                    os.close();

                    String response=connection.getResponseMessage();
                    Log.d("login response",response);

                    showRequestHeader("login",connection);
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while (br.read()!=-1){
                        String str=br.readLine();
                        Log.d("body",str);
                    }
                    Log.d("url",connection.getURL().toString());
                    //取Cookie
//                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showRequestHeader(String what,HttpURLConnection connection){
        what=what+" ";
        try{
            int code=connection.getResponseCode();
            Log.i(what+"Response Code",""+code);
        }catch (Exception e){
            e.printStackTrace();
        }
        Map headers = connection.getHeaderFields();
        Set<String> keys = headers.keySet();
        for( String key : keys ){
            String val = connection.getHeaderField(key);
            Log.i(what+key,val);
        }
    }

    private Handler handler=new Handler() {
        public void handleMessage(Message message) {
            switch (message.what){
                case CHECK_CODE:
                    Matrix matrix = new Matrix();
                    matrix.postScale(2f,2f); //长和宽放大缩小的比例
                    Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                    checkcodeImg.setImageBitmap(resizeBmp);
                    break;
                default:
                    break;
            }

        }
    };
}

