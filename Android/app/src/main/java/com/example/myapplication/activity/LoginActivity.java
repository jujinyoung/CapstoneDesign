package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.utils.UserData;
import com.example.myapplication.request.LoginRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    //id
    static public String user_id;

    private EditText et_id, et_pass;    //아이디,패스워드 텍스트
    private TextView btn_register,btn_forgot_pw;
    private Button btn_login;  //로그인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_forgot_pw = findViewById(R.id.btn_forgot_pw);


        //region 로그인

        //로그인 버튼 클릭
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                user_id = id;
                UserData.PREFERENCE_NAME = user_id+"userdata";
                UserData.init(getApplicationContext());
                //id,password 값저장
                UserData.write("user_id",id);
//                String userID = et_id.getText().toString();
                String userPass = et_pass.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //서버에 로그인 요청을 하면 결과값을 json타입으로 받음
                            JSONObject jsonObject = new JSONObject(response);
                            //서버통신 성공여부
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
//                                String userID = jsonObject.getString("userID");
//                                String userPass = jsonObject.getString("userPassword");
//                                Toast.makeText(getApplicationContext(), "로그인에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                //메인화면으로 이동
                                String userheight = UserData.read("user_height","");
                                if(userheight == ""){
                                    Intent intent = new Intent(LoginActivity.this, Setting_goalActivity.class);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(LoginActivity.this, CalendarActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //vollyer를 이용해서 서버에 요청
                LoginRequest loginRequest = new LoginRequest(UserData.read("user_id",""),userPass,responseListener);
//                LoginRequest loginRequest = new LoginRequest(userID,userPass,responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
        //endregion

        //region 회원가입

        //회원가입 버튼 클릭
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면으로 이동
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        //endregion

        //region 비밀번호 찾기
        btn_forgot_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,PasswordActivity.class);
                startActivity(intent);
            }
        });
        //endregion
    }
}