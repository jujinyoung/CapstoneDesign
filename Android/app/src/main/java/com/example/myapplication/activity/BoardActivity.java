package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.gallery.ImageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class BoardActivity extends AppCompatActivity {
    final static private String URL = "http://121.127.86.101:90/imgdownload00.php";

    GridView gridView;

    String[] numberWord;

    Bitmap[] numberImage;

    int[] dbnumber;

    Button board_searchBtn,board_CancelBtn,btn_back;

    EditText board_foodName;

    static public int list_cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        LoadImage(null);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        board_foodName = findViewById(R.id.board_foodName);

        board_searchBtn = findViewById(R.id.board_searchBtn);
        board_searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadImage(board_foodName.getText().toString());
            }
        });

        board_CancelBtn = findViewById(R.id.board_CancelBtn);
        board_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board_foodName.setText(null);
                LoadImage(null);
            }
        });
    }

    private void LoadImage(String food){
        RequestQueue queue = Volley.newRequestQueue(BoardActivity.this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST,URL,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                list_cnt = response.length();
                numberImage = new Bitmap[list_cnt];
                numberWord = new String[list_cnt];
                dbnumber = new int[list_cnt];
                int cnt = 0;

                for(int i  = 0; i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String image = jsonObject.getString("picture1");
                        String foodname = jsonObject.getString("food1");
                        int num = jsonObject.getInt("NO");
                        Log.e("이미지 확인", image.length() + "");
                        if(food != null){
                            if(food.equals(foodname)) {
                                numberImage[cnt] = StringToBitMap(image);
                                numberWord[cnt] = foodname;
                                dbnumber[cnt] = num;
                                cnt += 1;
                            }
                        }else {
                            numberImage[i] = StringToBitMap(image);
                            numberWord[i] = foodname;
                            dbnumber[i] = num;
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    gridView = findViewById(R.id.gridview);

                    ImageAdapter numberAdapter = new ImageAdapter(BoardActivity.this, numberWord, numberImage);
                    gridView.setAdapter(numberAdapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Intent intent = new Intent(getApplicationContext(),BoardActivity_detail.class);
                            intent.putExtra("NO",dbnumber[position]);
                            startActivity(intent);
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("에러","ㅇ[러터짐");
            }
        });

        queue.add(jsonArrayRequest);
    }


    public static Bitmap StringToBitMap(String image){
        String temp = "";
        Log.e("StringToBitMap","StringToBitMap");

        try {
            temp = URLDecoder.decode(image,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte [] encodeByte=Base64.decode(temp,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        Log.e("StringToBitMap","good");
        return bitmap;
    }

}