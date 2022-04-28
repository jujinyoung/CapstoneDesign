package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.R;
import com.example.myapplication.request.PasswordCheckRequest;
import com.example.myapplication.request.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Setting_editActivity extends AppCompatActivity {
    EditText et_id_pass_edit,et_id_pass_check,mailcode;
    Button btn_editpass;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_edit);

        et_id_pass_edit = findViewById(R.id.et_id_pass_edit);
        et_id_pass_check = findViewById(R.id.et_id_pass_check);
        mailcode = findViewById(R.id.mailcode);

        btn_editpass = findViewById(R.id.btn_editpass);

        btn_editpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_id_pass_edit.getText().toString() == et_id_pass_check.getText().toString()) {
                    Intent i = getIntent();
                    String et_id = i.getStringExtra("user_id");
                    String et_mailcode = i.getStringExtra("mailcode");
                    Log.d("메일 코드",et_mailcode);

                    if(mailcode.getText().toString() == et_mailcode){
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //성공여부
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");
                                    if (success) {
                                        Toast.makeText(getApplicationContext(), "비밀번호 변경 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "비밀번호 변경 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        //서버로 Volley를 이용해서 요청을 함
                        PasswordCheckRequest passwordCheckRequest = new PasswordCheckRequest(et_id, et_id_pass_edit.getText().toString(), responseListener);
                        RequestQueue queue = Volley.newRequestQueue(Setting_editActivity.this);
                        queue.add(passwordCheckRequest);
                    }
                }
            }
        });
    }
}

