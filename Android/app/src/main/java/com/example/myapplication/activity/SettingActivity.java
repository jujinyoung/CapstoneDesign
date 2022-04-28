package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class SettingActivity extends AppCompatActivity {

    private Button setting_goal,setting_info,setting_data,btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting_goal = findViewById(R.id.setting_goal);
        setting_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //목표 설정으로 이동하기
                Intent intent = new Intent(SettingActivity.this,Setting_goalActivity.class);
                startActivity(intent);
            }
        });

        setting_info = findViewById(R.id.setting_info);
        setting_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,PasswordActivity.class);
                startActivity(intent);
            }
        });

        setting_data = findViewById(R.id.setting_data);
        setting_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,Setting_resetActivity.class);
                startActivity(intent);
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,CalendarActivity.class);
                startActivity(intent);
            }
        });
    }
}