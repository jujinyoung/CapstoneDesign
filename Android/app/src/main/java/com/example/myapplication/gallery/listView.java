package com.example.myapplication.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.myapplication.activity.BoardActivity;

public class listView extends BaseAdapter {


    private String[] no;
    private Bitmap[] blob;
    private Context mContext;
    private String temp_data[]=new String[getCount()];
    public listView(Context context){
        mContext = context;
    }

    public void setNo(String[] data){
        no=data;
    }
    public void setBlob(Bitmap[] data){
        blob=data;
    }

    @Override
    public int getCount() {

        return BoardActivity.list_cnt;		//리스트뷰의 갯수
    }

    @Override
    public Object getItem(int position) {

        return temp_data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parents) {


        showList layout=null;


        if(convertView!=null){	//스크롤로 넘어간 뷰를 버리지 않고 재사용
            layout=(showList) convertView;
        }
        else{
            layout=new showList(mContext.getApplicationContext());	//레이아웃 설정

        }


        try{
            layout.setNo(no[position]);	//레이아웃으로 뿌려줌
            layout.setBlob(blob[position]);

        }catch(Exception e){
            Log.d("getview", String.valueOf(e));
        }


        return layout;
    }

}

