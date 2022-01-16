package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.login.LoginRequest;
import com.example.myapplication.login.PasswordRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordActivity extends AppCompatActivity {

    private EditText et_id_password;
    private Button btn_sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        et_id_password = findViewById(R.id.et_id_password);
        btn_sendEmail = findViewById(R.id.btn_sendEmail);

        btn_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String et_id_pass = et_id_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //성공여부
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success){
                                Toast.makeText(getApplicationContext(), "이메일 전송 완료",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"등록되지 않은 이메일입니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                PasswordRequest passwordRequest = new PasswordRequest(et_id_pass,responseListener);
                RequestQueue queue = Volley.newRequestQueue(PasswordActivity.this);
                queue.add(passwordRequest);
            }
        });
    }


}