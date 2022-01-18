package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

public class SettingActivity extends AppCompatActivity {

    private Button setting_goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setting_goal = findViewById(R.id.setting_goal);
        setting_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //목표 설정으로 이동하기
            }
        });
    }
}