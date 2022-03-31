package com.example.myapplication.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.utils.UserData;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Setting_goalActivity extends AppCompatActivity {
    public static String TAG = "Setting_goalActivity";

    EditText et_height,et_pv,et_fv;
    Button btn_setup,goal_day;
    RadioGroup goal_radioGroup;
    RadioButton man,woman;

    //datepicker
    int mYear, mMonth, mDay;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_goal);
        UserData.init(getApplicationContext());

        et_height = findViewById(R.id.et_height);
        et_pv = findViewById(R.id.et_pv);
        et_fv = findViewById(R.id.et_fv);

        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        btn_setup = findViewById(R.id.btn_setup);
        btn_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UserData.write("user_height",et_height.getText().toString());
                    UserData.write("user_weight",et_pv.getText().toString());
                    UserData.write("goal_weight",et_fv.getText().toString());
                    UserData.write("goal_day",goal_day.getText().toString());
                    finish();
                }catch (NumberFormatException e){
                    Log.e(TAG,e + "");
                }
            }
        });

        goal_day = findViewById(R.id.goal_day);
        goal_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Setting_goalActivity.this, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });

        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);
        goal_radioGroup = findViewById(R.id.goal_radioGroup);
        goal_radioGroup.setOnCheckedChangeListener(userGenderRadioListener);

        SetData();
    }

    //라디오 그룹 클릭 리스너
    RadioGroup.OnCheckedChangeListener userGenderRadioListener = new RadioGroup.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.man){
                UserData.writeInt("user_gender",0);
            } else if(i == R.id.woman){
                UserData.writeInt("user_gender",1);
            }
        }
    };



    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    //사용자가 입력한 값을 가져온뒤
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    goal_day.setText(mMonth+1 + "월 " + mDay + "일");
                }

    };

    public void SetData(){
        try {
            if(UserData.readInt("user_gender",0) == 0){
                man.setChecked(true);
            }else {
                woman.setChecked(true);
            }
            et_height.setText(UserData.read("user_height","").toString());
            et_pv.setText(UserData.read("user_weight","").toString());
            et_fv.setText(UserData.read("goal_weight","").toString());
            goal_day.setText(UserData.read("goal_day","").toString());
        }catch (Exception e){
            Log.e(TAG,e + "");
        }
    }

}
