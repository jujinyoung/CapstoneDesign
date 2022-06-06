package com.example.myapplication.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.icu.text.UFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.CalBMR;
import com.example.myapplication.R;
import com.example.myapplication.database.DBHelper;
import com.example.myapplication.database.Diary;
import com.example.myapplication.utils.UserData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StatActivity extends AppCompatActivity {
    private static final String TAG = "StatActivity";

    private LineChart chart_weekly_cal,chart_weekly_kg;
    int[] ave_bmr;
    float cal_kg;
    Button btn_back;

    //DB
    public static DBHelper mDatabase = null;
    String[] date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // 데이터베이스 열기
        openDatabase();

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ave_bmr = new int[7];

        LocalDateTime now = LocalDateTime.now();

        try {
            for(int i = 0; i<7; i++){
                String message = monthYearFromDate(now.minusDays(i));
                Diary diary = getTableData(message);
                ave_bmr[i] = Integer.parseInt(diary.getKcal());

                chart_weekly_cal = findViewById(R.id.chart_weekly_cal);
                SetCalLineChart();

                chart_weekly_kg = findViewById(R.id.chart_weekly_kg);
                SetKgLineChart();
            }
        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("일주일간의 기록");
            builder.setMessage("일기장을 작성해주세요.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();

            e.printStackTrace();
        }


    }

    public void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = DBHelper.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "database is open.");
        } else {
            Log.d(TAG, "database is not open.");
        }
    }

    private String monthYearFromDate(LocalDateTime date)
    {
        //언어 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d").withLocale(Locale.forLanguageTag("En"));
        return date.format(formatter);
    }

    private Diary getTableData(String day){
        try {
            mDatabase.db = mDatabase.Readdb();
            String sql = "SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE _id='" + day.replace(" ","") +"'";

            Diary diary = null;

            if(mDatabase.db != null){
                Cursor mCur = mDatabase.db.rawQuery(sql,null);

                int count = mCur.getCount();

                for(int i = 0; i<count; i++){
                    mCur.moveToNext();

                    diary = new Diary();

                    diary.set_id(mCur.getString(0));
                    diary.setPicture0(mCur.getString(1));
                    diary.setFood0(mCur.getString(2));
                    diary.setMood0(mCur.getString(3));
                    diary.setComment0(mCur.getString(4));
                    diary.setPicture1(mCur.getString(5));
                    diary.setFood1(mCur.getString(6));
                    diary.setMood1(mCur.getString(7));
                    diary.setComment1(mCur.getString(8));
                    diary.setPicture2(mCur.getString(9));
                    diary.setFood2(mCur.getString(10));
                    diary.setMood2(mCur.getString(11));
                    diary.setComment2(mCur.getString(12));
                    diary.setPicture3(mCur.getString(13));
                    diary.setFood3(mCur.getString(14));
                    diary.setMood3(mCur.getString(15));
                    diary.setComment3(mCur.getString(16));
                    diary.setTan(mCur.getString(17));
                    diary.setDan(mCur.getString(18));
                    diary.setGi(mCur.getString(19));
                    diary.setKcal(mCur.getString(20));

//                    Toast.makeText(getApplicationContext(),"레코드 #" + i + " : " + diary.get_id(),Toast.LENGTH_SHORT).show();
                }
            }
            return diary;
        }
        catch (SQLException mSQlException)
        {
            throw mSQlException;
        }
    }

    public void SetCalLineChart(){
        ArrayList<Entry> values = new ArrayList<>();

        values.add(new Entry(0, ave_bmr[0]));
        values.add(new Entry(1, ave_bmr[1]));
        values.add(new Entry(2, ave_bmr[2]));
        values.add(new Entry(3, ave_bmr[3]));
        values.add(new Entry(4, ave_bmr[4]));
        values.add(new Entry(5, ave_bmr[5]));
        values.add(new Entry(6, ave_bmr[6]));


        LineDataSet set1;
        set1 = new LineDataSet(values, null);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // black lines and points
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);
        set1.setValueTextSize(10f);
//        set1.setDrawValues(false);

        //x축 제거,오른쪽 값 제거, 위 제거
        chart_weekly_cal.getAxisLeft().setDrawGridLines(false);
        chart_weekly_cal.getAxisRight().setEnabled(false);
//        chart_weekly_cal.getXAxis().setEnabled(false);
//        chart_weekly_cal.getXAxis().setDrawLabels(false);
        chart_weekly_cal.getDescription().setEnabled(false);

        //x라벨 하단
        XAxis xAxis = chart_weekly_cal.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        chart_weekly_cal.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert float value to date string
                // Convert from days back to milliseconds to format time  to show to the user
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd");
                Date nowDate = new Date();
                long emissionsMilliSince1970Time = nowDate.getTime() - TimeUnit.DAYS.toMillis((long)value);
                // Show time in local version
                Date timeMilliseconds = new Date(emissionsMilliSince1970Time);

                return dateTimeFormat.format(timeMilliseconds);
            }
        });

        //x,y축 공간 두기
        chart_weekly_cal.getXAxis().setYOffset(10f);
        chart_weekly_cal.getAxisLeft().setXOffset(15f);

        //상호작용
        chart_weekly_cal.setScaleEnabled(false);
        chart_weekly_cal.setTouchEnabled(false);
        chart_weekly_cal.setPinchZoom(false);

        // set data
        chart_weekly_cal.setData(data);
    }

    public void SetKgLineChart(){
        Float height = Float.parseFloat(UserData.read("user_height",""));
        Float weight = Float.parseFloat(UserData.read("user_weight",""));
        int age = UserData.readInt("user_age",0);
        ArrayList<Entry> values = new ArrayList<>();
        CalBMR calBMR = new CalBMR();
        float bmr = 0;
        if(UserData.readInt("user_gender",0) == 0){
            bmr = (float)calBMR.CalBmr(0,height,weight,age);
        }else{
            bmr = (float)calBMR.CalBmr(1,height,weight,age);
        }

        int ave_bmr1 = 0;
        for(int i = 1; i<=7; i++){
            ave_bmr1 = ave_bmr1 + ave_bmr[7-i];
            cal_kg = (weight) - (((bmr*i) - ave_bmr1)/7000);
            cal_kg = Float.parseFloat(String.format("%.2f", cal_kg));
            values.add(new Entry(i-1, cal_kg));
        }



        LineDataSet set1;
        set1 = new LineDataSet(values, null);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the data sets

        // create a data object with the data sets
        LineData data = new LineData(dataSets);

        // black lines and points
        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);
        set1.setValueTextSize(10f);
//        set1.setDrawValues(false);

        //x축 제거,오른쪽 값 제거, 위 제거
        chart_weekly_kg.getAxisLeft().setDrawGridLines(false);
        chart_weekly_kg.getAxisRight().setEnabled(false);
//        chart_weekly_cal.getXAxis().setEnabled(false);
//        chart_weekly_cal.getXAxis().setDrawLabels(false);
        chart_weekly_kg.getDescription().setEnabled(false);

        //x라벨 하단
        XAxis xAxis = chart_weekly_kg.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        chart_weekly_kg.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert float value to date string
                // Convert from days back to milliseconds to format time  to show to the user
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM-dd");
                Date nowDate = new Date();
                long emissionsMilliSince1970Time = nowDate.getTime() + TimeUnit.DAYS.toMillis((long)value);
                // Show time in local version
                Date timeMilliseconds = new Date(emissionsMilliSince1970Time);

                return dateTimeFormat.format(timeMilliseconds);
            }
        });

        //x,y축 공간 두기
        chart_weekly_kg.getXAxis().setYOffset(10f);
        chart_weekly_kg.getAxisLeft().setXOffset(15f);

        //상호작용
        chart_weekly_kg.setScaleEnabled(false);
        chart_weekly_kg.setTouchEnabled(false);
        chart_weekly_kg.setPinchZoom(false);

        // set data
        chart_weekly_kg.setData(data);
    }
}

