package com.example.myapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.request.BoardRequestdetail;
import com.example.myapplication.request.DeleteGallery;
import com.example.myapplication.request.LoginRequest;
import com.example.myapplication.utils.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BoardActivity_detail extends AppCompatActivity {
    int num;

    ImageView mor_image,lun_image,di_image,sna_image;

    TextView total_kcal;

    Button delete_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        Intent intent = getIntent();
        num = intent.getIntExtra("NO",0);
        Log.e("숫자",num+"");

        mor_image = findViewById(R.id.mor_image);
        lun_image = findViewById(R.id.lun_image);
        di_image = findViewById(R.id.di_image);
        sna_image = findViewById(R.id.sna_image);
        total_kcal = findViewById(R.id.total_kcal);

        delete_btn = findViewById(R.id.delete_btn);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //서버에 로그인 요청을 하면 결과값을 json타입으로 받음
                    JSONObject jsonObject = new JSONObject(response);
                    //서버통신 성공여부
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        String id = jsonObject.getString("_id");
                        Log.e("아이디 확인",id);
                        Log.e("아이디 확인",UserData.read("user_id",""));
                        if(id.equals(UserData.read("user_id",""))){
                            delete_btn.setEnabled(true);
                            Log.e("아이디 확인",id +" "+UserData.read("user_id",""));
                        }
                        String image0 = jsonObject.getString("picture0");
                        String image1 = jsonObject.getString("picture1");
                        String image2 = jsonObject.getString("picture2");
                        String image3 = jsonObject.getString("picture3");
                        String foodname0 = jsonObject.getString("food0");
                        String foodname1 = jsonObject.getString("food1");
                        String foodname2 = jsonObject.getString("food2");
                        String foodname3 = jsonObject.getString("food3");
                        String tot_kcal = jsonObject.getString("tot_kcal");
                        mor_image.setImageBitmap(StringToBitMap(image0));
                        lun_image.setImageBitmap(StringToBitMap(image1));
                        di_image.setImageBitmap(StringToBitMap(image2));
                        sna_image.setImageBitmap(StringToBitMap(image3));
                        total_kcal.setText("Total:"+tot_kcal);
                        Log.e("확인용1",image0);
                        Log.e("확인용2",image1);
                        Log.e("확인용3",tot_kcal);
                    } else {
                        Toast.makeText(getApplicationContext(), "통신 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        //vollyer를 이용해서 서버에 요청
        BoardRequestdetail boardRequestdetail = new BoardRequestdetail(num+"",responseListener);
        RequestQueue queue = Volley.newRequestQueue(BoardActivity_detail.this);
        queue.add(boardRequestdetail);


        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> deleteresponseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //서버에 로그인 요청을 하면 결과값을 json타입으로 받음
                            JSONObject jsonObject = new JSONObject(response);
                            //서버통신 성공여부
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Log.e("삭제","성공");
                                Intent intent = new Intent(BoardActivity_detail.this,BoardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "삭제 통신 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //vollyer를 이용해서 서버에 요청
                DeleteGallery deleteGallery = new DeleteGallery(num+"",deleteresponseListener);
                RequestQueue queue = Volley.newRequestQueue(BoardActivity_detail.this);
                queue.add(deleteGallery);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BoardActivity_detail.this,BoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public static Bitmap StringToBitMap(String image){
        String temp = "";

        try {
            temp = URLDecoder.decode(image,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte [] encodeByte= Base64.decode(temp,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        return bitmap;
    }
}
