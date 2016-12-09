package com.example.exchange;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExchangeDatabase {
    private Connection conn;
    private boolean lock=false;
    private boolean is_success=false;
    private List<Map<String,Object>> data;

    public ExchangeDatabase(){
    }

    public boolean connect() {
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String connectString = "jdbc:mysql://172.18.59.33:3306/webdb"
                        + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&&useSSL=false";
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection(connectString, "wt", "1234");
                    is_success=true;
                    Log.d("connect","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean renewUsers(final String stu_name, final String stu_id,
                              final String user_name, final String user_pwd){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSQL="select * from user_stu where user_name='"+user_name+"';";
                String insertSQL="insert into user_stu values('"+stu_name+"','"+stu_id+"','"
                        +user_name+"','"+user_pwd+"'); ";
                Log.i("sql",insertSQL);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs=stat.executeQuery(selectSQL);
                    if(!rs.next()){
                        stat.executeUpdate(insertSQL);
                    }
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean renewCourses(final String user_name,
                                final String course_class_id, final String course_year,
                                final String course_semester, final String course_id,
                                final String course_name,final String course_type,
                                final String course_department,final String course_zone,
                                final String course_teacher,final String course_credit,
                                final String course_remain,final String course_to_filter,
                                final String course_time_and_place,final String course_require,

                                final String course_class_name,final String course_english_name,
                                final String course_capacity,final String course_hour,
                                final String course_chosen,final String course_real_chosen,
                                final String course_exam_type,final String course_filter_type,
                                final String course_intr,final String course_comments,
                                final String course_select){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectSC="select * from stu_course where user_name='"+user_name
                        +"' and course_class_id='"+course_class_id+"';";
                String selectCourse="select * from course_basic where course_class_id='"+course_class_id+"';";
                String updateCourseB="update course_basic set course_remain='"+course_remain
                        +"',course_to_filter='"+course_to_filter+"' where course_class_id='"
                        +course_class_id+"';";
                String insertCourseB="insert into course_basic values('"+
                        course_class_id+"','"+course_year+"','"+
                        course_semester+"','"+course_id+"','"+
                        course_name+"','"+course_type+"','"+
                        course_department+"','"+course_zone+"','"+
                        course_teacher+"','"+course_credit+"','"+
                        course_remain+"','"+course_to_filter+"','"+
                        course_time_and_place+"','"+course_require+"'); ";
                String insertCourseI="insert into course_info values('"+
                        course_class_id+"','"+course_class_name+"','"+
                        course_english_name+"','"+course_capacity+"','"+
                        course_hour+"','"+course_chosen+"','"+
                        course_real_chosen+"','"+course_exam_type+"','"+
                        course_filter_type+"','"+course_intr+"','"+
                        course_comments+"'); ";
                String insertSC="insert into stu_course values('"+
                        user_name+"','"+course_class_id+"','"+
                        "public"+"','"+course_select+"'); ";
                Log.i("sql",selectSC);
                Log.i("sql",selectCourse);
                Log.i("sql",insertCourseB);
                Log.i("sql",insertCourseI);
                Log.i("sql",updateCourseB);
                Log.i("sql",insertSC);
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs=stat.executeQuery(selectSC);
                    if(!rs.next()){
                        rs=stat.executeQuery(selectCourse);
                        if(rs.next()){
                            stat.executeUpdate(updateCourseB);
                        }
                        else{

                            stat.executeUpdate(insertCourseB);
                            stat.executeUpdate(insertCourseI);
                        }
                        stat.executeUpdate(insertSC);
                    }
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public List selectCourses(final String user_name,final boolean isPrivate){
        data=new ArrayList<>();
        lock=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String selectCourseB="select * from course_basic where course_class_id in(select course_class_id from stu_course where user_name='"
                        +user_name+"');";
                String selectCourseI="select * from course_info where course_class_id in(select course_class_id from stu_course where user_name='"
                        +user_name+"');";
                String selectSC="select * from stu_course where user_name='"+user_name+"';";
                try {
                    Statement stat=conn.createStatement();
                    ResultSet rs1=stat.executeQuery(selectCourseB);
                    ResultSet rs2=stat.executeQuery(selectCourseI);
                    ResultSet rs3=stat.executeQuery(selectSC);
                    while (rs1.next()){
                        rs2.next();
                        rs3.next();
                        if(isPrivate&&rs3.getString("course_public").equals("private"))
                            continue;
                        Map<String,Object>tmp=new LinkedHashMap<>();
                        tmp.put("course_class_id",rs1.getString("course_class_id"));
                        tmp.put("course_year",rs1.getString("course_year"));
                        tmp.put("course_semester",rs1.getString("course_semester"));
                        tmp.put("course_id",rs1.getString("course_id"));
                        tmp.put("course_name",rs1.getString("course_name"));
                        tmp.put("course_type",rs1.getString("course_type"));
                        tmp.put("course_department",rs1.getString("course_department"));
                        tmp.put("course_zone",rs1.getString("course_zone"));
                        tmp.put("course_teacher",rs1.getString("course_teacher"));
                        tmp.put("course_credit",rs1.getString("course_credit"));
                        tmp.put("course_remain",rs1.getString("course_remain"));
                        tmp.put("course_to_filter",rs1.getString("course_to_filter"));
                        tmp.put("course_time_and_place",rs1.getString("course_time_and_place"));
                        tmp.put("course_require",rs1.getString("course_require"));

                        tmp.put("course_class_name",rs2.getString("course_class_name"));
                        tmp.put("course_english_name",rs2.getString("course_english_name"));
                        tmp.put("course_capacity",rs2.getString("course_capacity"));
                        tmp.put("course_hour",rs2.getString("course_hour"));
                        tmp.put("course_chosen",rs2.getString("course_chosen"));
                        tmp.put("course_real_chosen",rs2.getString("course_real_chosen"));
                        tmp.put("course_exam_type",rs2.getString("course_exam_type"));
                        tmp.put("course_filter_type",rs2.getString("course_filter_type"));
                        tmp.put("course_intr",rs2.getString("course_intr"));
                        tmp.put("course_comments",rs2.getString("course_comments"));

                        tmp.put("course_public",rs3.getString("course_public"));
                        tmp.put("course_select",rs3.getString("course_select"));

                        data.add(tmp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return data;
    }

    public boolean setPublic(final String course_public,final String course_class_id){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String updateSQL="update stu_course set course_public='"+course_public
                        +"' where course_class_id='"
                        +course_class_id+"';";
                try {
                    Statement stat=conn.createStatement();
                    stat.executeUpdate(updateSQL);
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }

    public boolean deleteSC(final String course_class_id,final String user_name){
        lock=true;
        is_success=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String updateSQL="delete from stu_course where user_name='"
                        +user_name+"' and course_class_id='"+course_class_id+"';";
                try {
                    Statement stat=conn.createStatement();
                    stat.executeUpdate(updateSQL);
                    is_success=true;
                    Log.d("in","successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lock=false;
            }
        }).start();
        while (lock);
        return is_success;
    }













}
