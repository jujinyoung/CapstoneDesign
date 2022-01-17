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
import com.example.myapplication.login.GMailSender;
import com.example.myapplication.login.LoginRequest;
import com.example.myapplication.login.PasswordRequest;

import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class PasswordActivity extends AppCompatActivity {

    private EditText et_id_password;
    private Button btn_sendEmail;
    private String GmailCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        //이메일
        et_id_password = findViewById(R.id.et_id_password);
        //이메일 보내기 버튼
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
                                //메일을 보내주는 쓰레드
                                MailTread mailTread = new MailTread();
                                mailTread.start();
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

    //메일 보내는 쓰레드
    class MailTread extends Thread{

        public void run(){
            //이메일 보내는 계정(추후 새로 생성)
            GMailSender gMailSender = new GMailSender("jujinyoung1838@gmail.com", "jujinyoung8!");
            //GMailSender.sendMail(제목, 본문내용, 받는사람);


            //인증코드
            GmailCode=gMailSender.getEmailCode();
            try {
                gMailSender.sendMail("다이어트 플랜A 비밀번호 변경", "변경된 비밀번호는 "+GmailCode +" 입니다.\n로그인 후 설정창에서 비밀번호를 재설정해 주세요." , et_id_password.getText().toString());
            } catch (SendFailedException e) {

            } catch (MessagingException e) {
                System.out.println("인터넷 문제"+e);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

