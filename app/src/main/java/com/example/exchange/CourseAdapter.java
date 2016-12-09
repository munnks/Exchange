package com.example.exchange;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CourseAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,Object>> list;
    private LayoutInflater listContainer;
    public CourseAdapter(Context context, List<Map<String,Object>> list){
        this.context=context;
        listContainer= LayoutInflater.from(context);
        this.list=list;
    }
    @Override
    public int getCount(){
        if(list==null) return 0;
        return list.size();
    }
    @Override
    public Object getItem(int i){
        if(list==null) return 0;
        return list.get(i);
    }
    @Override
    public long getItemId(int i){
        return i;
    }
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= listContainer.inflate(R.layout.course_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.courseName=(TextView)convertView.findViewById(R.id.course_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.courseName.setText((String)list.get(i).get("name"));
        return convertView;
    }
    private class ViewHolder{
        public TextView courseName;
    }
}
