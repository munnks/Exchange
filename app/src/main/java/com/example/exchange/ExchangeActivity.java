package com.example.exchange;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExchangeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mycourse_img,platform_img,chat_img,exchange_img,contacts_img,request_img;
    private LinearLayout course_layout,platform_layout,chat_layout,exchange_layout,contacts_layout,request_layout,current_layout;
    boolean layout_flag=true;
    private ListView course_listView,post_listView;
    private String cookie,sid;
    private Button new_post,next_page,last_page;

    private List<Map<String,Object>> courseList;
    private List<Map<String,Object>> postList;
    private CourseAdapter courseAdapter;
    private PostAdapter postAdapter;

    private String courseAll;
    private String coursePage;
    private boolean lock=false;

    private String current_xnd="2016-2017";
    private String current_xq="1";

    private ExchangeDatabase database;

    private String username="",password="";

    private int semaphore;

    private int current_page=0;
    private int num_in_each_page=8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);
        findView();
        init();
        bindButton();
        getCourseAll();
        database.connect();
        renewUser();
        renewCourses();
        course_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                bundle.putString("cookie",cookie);
                bundle.putString("sid",sid);
                bundle.putString("username",username);
                bundle.putString("course_class_id",(String)courseList.get(i).get("course_class_id"));
                Intent intent=new Intent();
                intent.setClass(ExchangeActivity.this,CourseDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        courseList=database.selectSimpleCourses(username);
        courseAdapter=new CourseAdapter(this,courseList);
        course_listView.setAdapter(courseAdapter);
    }

    private void init(){
        Bundle bundle=this.getIntent().getExtras();
        cookie=bundle.getString("cookie");
        sid=bundle.getString("sid");
        username=bundle.getString("username");
        password=bundle.getString("password");
        database=new ExchangeDatabase();

    }

    private void findView(){
        mycourse_img=(ImageView)findViewById(R.id.mycourse_img);
        platform_img=(ImageView)findViewById(R.id.platform_img);
        chat_img=(ImageView)findViewById(R.id.chat_img);
        exchange_img=(ImageView)findViewById(R.id.exchange_img);
        contacts_img=(ImageView)findViewById(R.id.contacts_img);
        request_img=(ImageView)findViewById(R.id.request_img);

        course_listView=(ListView)findViewById(R.id.course_list);
        post_listView=(ListView)findViewById(R.id.post_list);

        course_layout=(LinearLayout)findViewById(R.id.course_layout);
        platform_layout=(LinearLayout)findViewById(R.id.platform_layout);
        chat_layout=(LinearLayout)findViewById(R.id.chat_layout);
        exchange_layout=(LinearLayout)findViewById(R.id.exchange_layout);
        contacts_layout=(LinearLayout)findViewById(R.id.contacts_layout);
        request_layout=(LinearLayout)findViewById(R.id.request_layout);

        new_post=(Button)findViewById(R.id.new_post);
        next_page=(Button)findViewById(R.id.next_page);
        last_page=(Button)findViewById(R.id.last_page);
    }

    private void bindButton(){
        mycourse_img.setOnClickListener(this);
        platform_img.setOnClickListener(this);
        chat_img.setOnClickListener(this);
        exchange_img.setOnClickListener(this);
        contacts_img.setOnClickListener(this);
        request_img.setOnClickListener(this);

        new_post.setOnClickListener(this);
        next_page.setOnClickListener(this);
        last_page.setOnClickListener(this);
    }


    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.mycourse_img:
                current_layout=course_layout;
                layout_flag=false;
            case R.id.platform_img:
                if(layout_flag){
                    current_layout=platform_layout;
                    layout_flag=false;
                    renewPostList(current_page,num_in_each_page);
                }
            case R.id.chat_img:
                if(layout_flag){
                    current_layout=chat_layout;
                    layout_flag=false;
                }
            case R.id.exchange_img:
                if(layout_flag){
                    current_layout=exchange_layout;
                    layout_flag=false;
                }
            case R.id.contacts_img:
                if(layout_flag){
                    current_layout=contacts_layout;
                    layout_flag=false;
                }
            case R.id.request_img:
                if(layout_flag){
                    current_layout=request_layout;
                    layout_flag=false;
                }
                course_layout.setVisibility(View.GONE);
                platform_layout.setVisibility(View.GONE);
                chat_layout.setVisibility(View.GONE);
                exchange_layout.setVisibility(View.GONE);
                contacts_layout.setVisibility(View.GONE);
                request_layout.setVisibility(View.GONE);
                current_layout.setVisibility(View.VISIBLE);
                Drawable drawable=getResources().getDrawable(R.color.colorPrimaryGreen);
                mycourse_img.setBackground(drawable);
                platform_img.setBackground(drawable);
                chat_img.setBackground(drawable);
                exchange_img.setBackground(drawable);
                contacts_img.setBackground(drawable);
                request_img.setBackground(drawable);
                v.setBackground(getResources().getDrawable(R.color.darkGreen));
                layout_flag=true;
                break;
            case R.id.new_post:
                LayoutInflater factory=LayoutInflater.from(ExchangeActivity.this);
                View view2=factory.inflate(R.layout.new_post,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(ExchangeActivity.this);
                final EditText editTitle=(EditText)view2.findViewById(R.id.edit_title);
                final EditText editContent=(EditText)view2.findViewById(R.id.edit_content);
                builder.setView(view2);
                builder.setPositiveButton("发布", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.insertPost(editTitle.getText().toString()
                                ,editContent.getText().toString(),username);
                        renewPostList(current_page,num_in_each_page);
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.next_page:
                current_page++;
                renewPostList(current_page,num_in_each_page);
                if(current_page!=0)last_page.setVisibility(View.VISIBLE);
                break;
            case R.id.last_page:
                current_page--;
                if(current_page<0)current_page=0;
                renewPostList(current_page,num_in_each_page);
                if(current_page==0)last_page.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

    }

    public void renewPostList(int pgno,int pgcnt){
        postList=database.selectPost(pgno,pgcnt);
        postAdapter=new PostAdapter(this,postList);
        post_listView.setAdapter(postAdapter);
    }

    public void getCourseAll(){
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url="http://uems.sysu.edu.cn/elect/s/courseAll?xnd="
                            +current_xnd+"&xq="+current_xq+"&sid="+sid;
                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(url)).openConnection();
                    connection.addRequestProperty("Cookie",cookie);
                    StringBuilder sb = new StringBuilder("");
                    int c1;
                    InputStreamReader in =new InputStreamReader(connection.getInputStream());
                    while ((c1 = in.read()) != -1) {
                        if(c1!='\t'&&c1!='\n'&&c1!='\r')       //去除空格
                            sb.append((char) c1);
                    }
                    courseAll=sb.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
    }

    public void renewUser(){
        String pattern3="(?<=<td class='val'>)[^<]*(?=</td>)";
        Pattern p3= Pattern.compile(pattern3);
        Matcher m3=p3.matcher(courseAll);
        int i=0;
        String name="",id="";
        while(m3.find()){
            if(i==0)
                name=m3.group();
            if(i==1)
                id=m3.group();
            //System.out.println(m3.group());
            i++;
        }
        database.renewUsers(name,id,username,password);
    }

    public void renewCourses(){
        String pattern1="(?<=<td class='c'>)[^<]*(?=</td>)";
        String pattern2="(?<=\" >).*?(?=</tr>)";
        Pattern p=Pattern.compile(pattern2);
        int i=0;
        Matcher m=p.matcher(courseAll);
        semaphore=0;
        while(m.find()){
            String str=m.group();
            if(i!=0){
                String course_class_id="";
                String course_select="";
                int j=0;
                Pattern p2=Pattern.compile(pattern1);
                Matcher m2=p2.matcher(str);
                while(m2.find()){
                    if(j==8)
                        course_select=m2.group();
                    if(j==11)
                        course_class_id=m2.group();
                    j++;
                }
                renewSC(course_class_id,course_select);
            }
            i++;
        }
        while (semaphore!=0);
    }

    public void renewSC(final String course_class_id,final String course_select){
        semaphore++;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(database.findCourse(course_class_id,username)){
                    semaphore--;
                    return;
                }
                try {
                    String url="http://uems.sysu.edu.cn/elect/s/courseDet?jxbh="+course_class_id
                            +"&xnd="+current_xnd+"&xq="+current_xq+"&sid="+sid;
                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(url)).openConnection();
                    connection.addRequestProperty("Cookie",cookie);
                    StringBuilder sb = new StringBuilder("");
                    int c1;
                    InputStreamReader in =new InputStreamReader(connection.getInputStream());
                    while ((c1 = in.read()) != -1) {
                        if(c1!='\t'&&c1!='\n'&&c1!='\r')       //去除空格
                            sb.append((char) c1);
                    }
                    coursePage=sb.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                String pattern4="(?<=<td class='val'( colspan='3')?>)[^<]*(?=</td>)";
                Pattern p4=Pattern.compile(pattern4);
                Matcher m4=p4.matcher(coursePage);
                String course_year="",course_semester="",course_id="";
                String course_name="",course_type="",course_department="",course_zone="";
                String course_teacher="",course_credit="",course_remain="",course_to_filter="";
                String course_time_and_place="",course_require="";

                String course_class_name="",course_english_name="",course_capacity="",course_hour="";
                String course_chosen="",course_real_chosen="",course_exam_type="",course_filter_type="";
                String course_intr="",course_comments="";
                int k=0;
                while(m4.find()){
                    if(k==0) course_year=m4.group();
                    else if(k==1) course_semester=m4.group();
                    else if(k==2) course_id=m4.group();
                    else if(k==3) course_name=m4.group();
                    else if(k==5) course_class_name=m4.group();
                    else if(k==6) course_type=m4.group();
                    else if(k==7) course_english_name=m4.group();
                    else if(k==8) course_department=m4.group();
                    else if(k==9) course_zone=m4.group();
                    else if(k==10) course_teacher=m4.group();
                    else if(k==11) course_credit=m4.group();
                    else if(k==12) course_capacity=m4.group();
                    else if(k==13) course_hour=m4.group();
                    else if(k==14) course_chosen=m4.group();
                    else if(k==15) course_real_chosen=m4.group();
                    else if(k==16) course_remain=m4.group();
                    else if(k==17) course_to_filter=m4.group();
                    else if(k==18) course_exam_type=m4.group();
                    else if(k==19) course_filter_type=m4.group();
                    else if(k==20) course_time_and_place=m4.group();
                    else if(k==21) course_require=m4.group();
                    else if(k==22) course_intr=m4.group();
                    else if(k==23) course_comments=m4.group();
                    k++;
                }
                database.renewCourse(username,course_class_id,course_year,course_semester
                        ,course_id,course_name,course_type,course_department,course_zone,course_teacher
                        ,course_credit,course_remain,course_to_filter,course_time_and_place,course_require
                        , course_class_name,course_english_name,course_capacity,course_hour,course_chosen
                        ,course_real_chosen,course_exam_type,course_filter_type,course_intr,course_comments,course_select);
                semaphore--;
            }
        }).start();
    }


    public void getCoursePage(final String course_class_id){
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url="http://uems.sysu.edu.cn/elect/s/courseDet?jxbh="+course_class_id
                            +"&xnd="+current_xnd+"&xq="+current_xq+"&sid="+sid;
                    HttpURLConnection connection;
                    connection=(HttpURLConnection)(new URL(url)).openConnection();
                    connection.addRequestProperty("Cookie",cookie);
                    StringBuilder sb = new StringBuilder("");
                    int c1;
                    InputStreamReader in =new InputStreamReader(connection.getInputStream());
                    while ((c1 = in.read()) != -1) {
                        if(c1!='\t'&&c1!='\n'&&c1!='\r')       //去除空格
                            sb.append((char) c1);
                    }
                    coursePage=sb.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
    }


}
