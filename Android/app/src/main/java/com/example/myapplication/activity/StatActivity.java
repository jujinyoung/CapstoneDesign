package com.example.myapplication.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.CalBMR;
import com.example.myapplication.R;
import com.example.myapplication.utils.UserData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StatActivity extends AppCompatActivity {

    private LineChart chart_weekly_cal,chart_weekly_kg;
    int[] ave_bmr;
    float cal_kg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        UserData.init(getApplicationContext());
        ave_bmr = new int[]{2500,2632,2851,2342,2632,2423,2932};

        chart_weekly_cal = findViewById(R.id.chart_weekly_cal);
        SetCalLineChart();

        chart_weekly_kg = findViewById(R.id.chart_weekly_kg);
        SetKgLineChart();

    }

    public void SetCalLineChart(){
        ArrayList<Entry> values = new ArrayList<>();

        values.add(new Entry(0, 2500));
        values.add(new Entry(1, 2632));
        values.add(new Entry(2, 2851));
        values.add(new Entry(3, 2342));
        values.add(new Entry(4, 2632));
        values.add(new Entry(5, 2423));
        values.add(new Entry(6, 2932));


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
        if(UserData.readInt("user_gender",0) == 0){
            CalBMR calBMR = new CalBMR();
            float bmr = (float)calBMR.CalBmr(0,height,weight,age);
            int ave_bmr1 = 0;
            for(int i = 1; i<=7; i++){
                ave_bmr1 = ave_bmr1 + ave_bmr[i-1];
                cal_kg = (weight) - ((bmr*i) - (ave_bmr1/i))/7200;
                values.add(new Entry(i-1, cal_kg));
            }

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

