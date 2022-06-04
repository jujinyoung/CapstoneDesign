package com.example.myapplication.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.CalBMR;
import com.example.myapplication.R;
import com.example.myapplication.calendar.CalendarAdapter;
import com.example.myapplication.database.DBHelper;
import com.example.myapplication.database.Diary;
import com.example.myapplication.utils.UserData;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    //캘린더
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private CalendarAdapter calendarAdapter;

    //차트
    private PieChart pieChart;
    private HorizontalBarChart barChart;
    private int[] colorArray;

    //하단바 버튼
    private Button btn_setting,btn_graph,btn_diary;

    long diffDays;
    double calTotalRemoveCalorie,recommentCal;

    //월 변경 화살표
    TextView left_arrow,right_arrow;

    //목표
    TextView goal;

    //성분
    TextView tan_txt,dan_txt,gi_txt;
    float bmr = 0;

    //게시판
    Button btn_search;

    //권장 칼로리
    TextView recommend_cal,diet_cal,left_cal;

    //DB
    public static DBHelper mDatabase = null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initWidgets();
        openDatabase();
        //현재날짜
        selectedDate = LocalDate.now();
        setMonthView();
        UserData.init(getApplicationContext());

        btn_graph = findViewById(R.id.btn_graph);
        btn_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this,StatActivity.class);
                startActivity(intent);
            }
        });

        btn_setting = findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        //목표
        goal = findViewById(R.id.goal);
        diffDays = 0;
        if(UserData.read("goal_day","") == null){
            goal.setHint("목표를 설정해주세요");
        } else {
            CalBMR calBMR = new CalBMR();
            String goal_day = UserData.read("goal_day","");
            LocalDate now = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            String formatedNow = now.format(formatter);
            try {
                Date format1 = new SimpleDateFormat("yyyy/MM/dd").parse(goal_day);
                Date format2 = new SimpleDateFormat("yyyy/MM/dd").parse(formatedNow);
                long diffSec = (format1.getTime() - format2.getTime()) / 1000;
                diffDays = diffSec / (24*60*60); //일자 계산
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String user_weight = UserData.read("user_weight","");
            String goal_weight = UserData.read("goal_weight","");
            calTotalRemoveCalorie = calBMR.CalTotalRemoveCalorie(Integer.parseInt(user_weight),Integer.parseInt(goal_weight));
            int weight = Integer.parseInt(goal_weight) - Integer.parseInt(user_weight);
            if(diffDays == 0){
                goal.setHint("GOAL(" + "D-day,-" + weight + "kg)");
            }else {
                goal.setHint("GOAL(" + "D-" + diffDays+ "," + weight + "kg)");
            }
        }
        goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //목표 설정 인텐트 이동
                Intent intent = new Intent(CalendarActivity.this,Setting_goalActivity.class);
                startActivity(intent);
            }
        });

        //게시판
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this,BoardActivity.class);
                startActivity(intent);
            }
        });

        btn_diary = findViewById(R.id.btn_diary);
        btn_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate now = LocalDate.now();
                int dayOfMonth = now.getDayOfMonth();
                Intent intent = new Intent(CalendarActivity.this, DiaryActivity.class);
                String message = monthYearFromDate(selectedDate) + " " + dayOfMonth;
                intent.putExtra("날짜",message);
                startActivity(intent);
                finish();
            }
        });

        //막대차트
        barChart = (HorizontalBarChart) findViewById(R.id.bar_chart);
        SetBarChart(barChart);

        //파이차트
        pieChart = findViewById(R.id.pie_chart);
        SetPieChart(pieChart);

        left_arrow = findViewById(R.id.left_arrow);
        left_arrow.setText("<-");
        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction(v);
            }
        });

        right_arrow = findViewById(R.id.right_arrow);
        right_arrow.setText("->");
        right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction(v);
            }
        });

        Diary diary = getTableData();
        if(diary != null){
            int leftkcal = (int)bmr - Integer.parseInt(diary.getKcal().replace("kcal",""));
            left_cal = findViewById(R.id.left_cal);
            left_cal.setText(leftkcal+"kcal");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CalendarActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //region 캘린더
    //초기화
    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendar);
        monthYearText = findViewById(R.id.title);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        //캘린더어댑터 생성
        calendarAdapter = new CalendarAdapter(daysInMonth, this);
        //요일 개수 설정
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    //월별 날짜 만들기
    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        //해당 달의 첫째 날 반환
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        //요일 반환
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        //한달에 들어가는 총 칸수는 42개
        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                //빈칸 출력
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return  daysInMonthArray;
    }

    //월 파싱
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date)
    {
        //언어 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.forLanguageTag("En"));
        return date.format(formatter);
    }
    //달 전환 버튼
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view)
    {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    //클릭할 시  구현
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText)
    {
        if(!dayText.equals(""))
        {
            Intent intent = new Intent(CalendarActivity.this, DiaryActivity.class);
            String message = monthYearFromDate(selectedDate) + " " + dayText;
            intent.putExtra("날짜",message);
            startActivity(intent);
        }
    }

    //endregion

    //region 파이차트
    public void SetPieChart(PieChart pieChart) {

        recommend_cal = findViewById(R.id.recommend_cal);
        diet_cal = findViewById(R.id.diet_cal);
        left_cal = findViewById(R.id.left_cal);
        recommend_cal.setText((int)bmr +"kcal");
        diet_cal.setText((int)recommentCal +"kcal");
        left_cal.setText((int)bmr+"kcal");


        //파이차트 밸류 색상
        colorArray = new int[]{Color.parseColor("#263545")};

        ArrayList<PieEntry> dataValue = new ArrayList<>();

        dataValue.add(new PieEntry(1500));
        dataValue.add(new PieEntry(375));
        dataValue.add(new PieEntry(625));

        PieDataSet pieDataSet = new PieDataSet(dataValue,null);
        //그래프 색상 결정
        pieDataSet.setColors(colorArray);
//        //텍스트 설정
//        pieDataSet.setValueTextColor(Color.WHITE);
//        pieDataSet.setValueTextSize(8f);
        //텍스트 제거
        pieDataSet.setDrawValues(false);
        //경계선
        pieDataSet.setSliceSpace(3f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(0,0,0,0);
        //유저에 맞는 칼로리 넣기
        pieChart.setCenterText((int)bmr+"kcal");
        //중앙 텍스트 크기
        pieChart.setCenterTextSize(12f);
        //하단 라벨 제거
        pieChart.getLegend().setEnabled(false);
        //상호작용 제거
        pieChart.setRotationEnabled(false);
        pieChart.setTouchEnabled(false);
    }
    //endregion

    //region 막대차트
    public void SetBarChart(HorizontalBarChart barChart){
        CalBMR calBMR = new CalBMR();
        Float height = Float.parseFloat(UserData.read("user_height",""));
        Float weight = Float.parseFloat(UserData.read("user_weight",""));
        int age = UserData.readInt("user_age",0);
        if(UserData.readInt("user_gender",0) == 0) {
            bmr = (float) calBMR.CalBmr(0, height, weight, age);
        }else{
            bmr = (float) calBMR.CalBmr(1, height, weight, age);
        }
        recommentCal = calBMR.CalDietRecommendedIntake(bmr,calTotalRemoveCalorie,(int)diffDays);
        double gi = (int)recommentCal * 0.15;
        double dan = (int)recommentCal * 0.25;
        double tan = (int)recommentCal * 0.6;
        tan_txt = findViewById(R.id.tan);
        tan_txt.setText((int)tan + "kcal");
        dan_txt = findViewById(R.id.dan);
        dan_txt.setText((int)dan + "kcal");
        gi_txt = findViewById(R.id.gi);
        gi_txt.setText((int)gi + "kcal");

        //내부 데이터
        ArrayList<BarEntry> dataValue = new ArrayList<>();
        dataValue.add(new BarEntry(0,(int)gi));
        dataValue.add(new BarEntry(1,(int)dan));
        dataValue.add(new BarEntry(2,(int)tan));


        //라벨 데이터
//        ArrayList<String> getXAxisValues = new ArrayList<>();
//        getXAxisValues.add("탄수화물");
//        getXAxisValues.add("단백질");
//        getXAxisValues.add("지방");

        colorArray = new int[]{Color.parseColor("#263545")};

        BarDataSet barDataSet = new BarDataSet(dataValue,"");
        barDataSet.setColors(colorArray);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barDataSet.setDrawValues(true);
//        //경계선
//        barDataSet.setBarBorderWidth(2f);
//        barDataSet.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return (String.valueOf((int) value)) + "kcal";
//            }
//        });

        BarData barData = new BarData(barDataSet);

//        //데이터 값 넓이 설정
        barData.setBarWidth(0.7f);
//        barData.setValueTextSize(12);

//        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        //격자 선 제거
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        //최대,최소,차트 개수
//        barChart.getAxisLeft().setAxisMaximum(1500);
//        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.setMaxVisibleValueCount(3);
        //간격
//        barChart.setExtraOffsets(0f,0f,0f,0f);
        //상호작용 제거
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setTouchEnabled(false);

//        //세로축 이름
//        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues));

        //데이터 내부에 표시
//        barChart.setDrawValueAboveBar(true);

        // Hide graph legend
        barChart.getLegend().setEnabled(false);

        //적용
        barChart.invalidate();


    }
    //endregion

    //region DB
    public  void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = DBHelper.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {

        } else {

        }
    }

    private Diary getTableData(){
        try {
            LocalDateTime now = LocalDateTime.now();
            String today_date = TodayDate(now);
            mDatabase.db = mDatabase.Readdb();
            String sql = "SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE _id='" + today_date.replace(" ","") +"'";

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

    private String TodayDate(LocalDateTime date)
    {
        //언어 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d").withLocale(Locale.forLanguageTag("En"));
        return date.format(formatter);
    }
    //endregion
}
