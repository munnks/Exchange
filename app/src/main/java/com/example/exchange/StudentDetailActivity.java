package com.example.exchange;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class StudentDetailActivity extends AppCompatActivity {
    List<Map<String,Object>> data;
    Map<String,Object> stu_info;
    ExchangeDatabase db;
    String myusername,username;
    boolean isFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        Bundle bundle=this.getIntent().getExtras();
        myusername=bundle.getString("myusername");
        username=bundle.getString("username");
        db=new ExchangeDatabase();
        db.connect();
        data=db.selectSimplePublicCourses(username);
        ListView courseList=(ListView)findViewById(R.id.course_list);
        CourseAdapter courseAdapter=new CourseAdapter(this,data);
        courseList.setAdapter(courseAdapter);
        stu_info=db.selectStu(username);
        TextView stu_id=(TextView)findViewById(R.id.id);
        TextView stu_name=(TextView)findViewById(R.id.name);
        stu_id.setText(username);
        stu_name.setText((String)stu_info.get("stu_name"));
        isFriend=db.isFriend(myusername,username);
        Button button=(Button)findViewById(R.id.button);
        if(isFriend){
            button.setText("发送消息");
        }else {
            button.setText("加为好友");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFriend){

                }else{

                }
            }
        });
    }

}
