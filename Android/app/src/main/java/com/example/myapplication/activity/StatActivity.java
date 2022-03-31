package com.example.myapplication.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
            float bmr = (float)CalBmr(0,height,weight,age);
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





    /**
     * BMI수치를 계산하는 메소드
     * @param weight 체중(kg)
     * @param height 키(cm)
     * @return BMI수치
     */
    public double CalBmi(int weight, int height){

        double bmi = ((double)weight / (((double)height/100) * ((double)height/100)));
        return bmi;
    }

    /**
     * BMI수치별 BMI Level 계산 메소드
     * @param bmi BMI수치
     * @return BMI수치별 해당 Level
     */
    public int BmiLevel(double bmi) {
        if(bmi < 18.5) // 저체중
            return 1;
        else if(bmi >= 18.5 && bmi < 23) // 정상
            return 2;
        else if(bmi >= 23 && bmi < 25) // 과체중
            return 3;
        else if(bmi >= 25 && bmi < 30) // 위험체중
            return 4;
        else if(bmi >= 30 && bmi < 35) // 초도비만
            return 5;
        else if(bmi >= 35 && bmi < 40) // 중등도비만
            return 6;
        else // 고도비만
            return 0;
    }

    /**
     * BMI Level별 상태 분류 메소드
     * @param user_bmi_level
     * @return BMI따른 상태문자열
     */
    public String CalUserState(int user_bmi_level){
        if(user_bmi_level == 1)
            return("저체중");
        else if(user_bmi_level == 2)
            return("정상");
        else if(user_bmi_level == 3)
            return("과체중");
        else if(user_bmi_level == 4)
            return("위험체중");
        else if(user_bmi_level == 5)
            return("초도비만");
        else if(user_bmi_level == 6)
            return("중등도비만");
        else
            return("고도비만");
    }

    /**
     * 활동량에 따른 계산된 Activation Level 계산 메소드
     * @param activationvalue 활동량 값
     * @return 활동량 값에 따른 해당 Level
     */
    public int CalActivation(int activationvalue){
        if(activationvalue >= 0 && activationvalue<= 5)
            return 1;
        else if(activationvalue > 5 && activationvalue <= 15)
            return 2;
        else if(activationvalue > 15 && activationvalue <= 25)
            return 3;
        else if(activationvalue > 25 && activationvalue <= 35)
            return 4;
        else if(activationvalue > 35 && activationvalue <= 40)
            return 5;
        else
            return 0;
    }

    /**
     * 기초대사량(BMR)공식에 의해 구하는 계산 메소드
     * (*Harris-Benedict equation(B.E.E)방법)
     * @param gender 성별
     * @param height 키(cm)
     * @param weight 몸무게(kg)
     * @param age 나이
     * @return 일일 기초대사량 수치
     */
    public double CalBmr(int gender, float height, float weight, int age){
        double bmr = 0;

        if(gender == 0)
            bmr = 66.47 + (13.75 * weight) + (5 * height) - (6.76 * age);
        else if(gender == 1)
            bmr = 655.1 + (9.56*weight) + (1.85*height) - (4.68*age);
        else
            bmr = 0;

        return bmr;
    }

    /**
     * 활동량에 따른 일일 권장 칼로리 계산 메소드
     * @param bmr 일일 기초대사량
     * @param activationvalue 활동량Level
     * @return 일일 권장 칼로리
     */
    public double CalRecommendedIntake(double bmr, int activationvalue){
        double recommended_intake = 0;

        if(activationvalue == 1)
            recommended_intake = bmr * 1.2;
        else if(activationvalue == 2)
            recommended_intake = bmr * 1.375;
        else if(activationvalue == 3)
            recommended_intake = bmr * 1.55;
        else if(activationvalue == 4)
            recommended_intake = bmr * 1.725;
        else if(activationvalue == 5)
            recommended_intake = bmr * 1.9;
        else
            recommended_intake = 0;

        return recommended_intake;
    }

    /**
     * 목표체중이 되기까지 소비해야 되는 칼로리 계산 메소드
     * @param weight 현재 체중(kg)
     * @param goal_weight 목표 체중(kg)
     * @return 소비해야 되는 총 칼로리(현재체중->목표체중)
     */
    public double CalTotalRemoveCalorie(int weight, int goal_weight){
        double total_remove_cal = 0;
        total_remove_cal = 7.2 * ((weight - goal_weight) * 1000);
        return total_remove_cal;
    }

    /**
     * 다이어트용 총 권장 칼로리 계산 메소드(목표기간 * 일일 권장 칼로리-소비해야 되는 총 칼로리)
     * @param recommended_intake 일일 권장 칼로리
     * @param total_remove_cal 소비해야 되는 총 칼로리
     * @param goal_term 목표기간
     * @return 다이어트용 총 권장 칼로리
     */
    public double CalDietRecommendedIntake(double recommended_intake, double total_remove_cal, int goal_term){
        double total_diet_recommended_intake = 0;
        total_diet_recommended_intake = (recommended_intake  * goal_term) - total_remove_cal;
        return total_diet_recommended_intake;
    }

    /**
     * 일일 다이어트용 권장 칼로리와 현재 칼로리를 비교해서 현재 상태를 나타내는 메소드
     * @param now_cal 현재 칼로리
     * @param day_diet_rcmd_cal 일일 다이어트용 권장 칼로리
     * @return 현재 상태Level
     */
    public int NowStatus(double now_cal, int day_diet_rcmd_cal) {
        double term_status = (double)day_diet_rcmd_cal - now_cal;
        if( term_status < -(day_diet_rcmd_cal))
        {
            return 0;
        }
        else if(term_status > -day_diet_rcmd_cal && term_status < -(day_diet_rcmd_cal/2) ) {
            return 1;
        }
        else if(term_status > -(day_diet_rcmd_cal/2) && term_status < -(day_diet_rcmd_cal/8)) {
            return 2;
        }
        else if(term_status > -(day_diet_rcmd_cal/8) && term_status < (day_diet_rcmd_cal/8)) {
            return 3;
        }
        else if(term_status > (day_diet_rcmd_cal/8) && term_status < (day_diet_rcmd_cal/2)) {
            return 4;
        }
        else {
            return 5;
        }
    }
}

