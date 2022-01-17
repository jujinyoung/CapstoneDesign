package com.example.myapplication.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.calendar.CalendarAdapter;
import com.example.myapplication.calendar.CalendarViewHolder;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    //캘린더
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    //차트
    private PieChart pieChart;
    private HorizontalBarChart barChart;
    private int[] colorArray;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initWidgets();
        //현재날짜
        selectedDate = LocalDate.now();
        setMonthView();

        //파이차트
        pieChart = findViewById(R.id.pie_chart);
        SetPieChart(pieChart);

        //막대차트
        barChart = findViewById(R.id.bar_chart);
        SetBarChart(barChart);
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
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
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

    //년도 월 파싱
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MMMM");
        return date.format(formatter);
    }
    //달 전환 버튼
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void previousMonthAction(View view)
//    {
//        selectedDate = selectedDate.minusMonths(1);
//        setMonthView();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void nextMonthAction(View view)
//    {
//        selectedDate = selectedDate.plusMonths(1);
//        setMonthView();
//    }

    //클릭할 시  구현
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText)
    {
        if(!dayText.equals(""))
        {
//            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
//            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region 파이차트
    public void SetPieChart(PieChart pieChart) {
        //파이차트 밸류 색상
        colorArray = new int[]{Color.parseColor("#263545")};

        ArrayList<PieEntry> dataValue = new ArrayList<>();

        dataValue.add(new PieEntry(1000,"탄수화물"));
        dataValue.add(new PieEntry(400,"단백질"));
        dataValue.add(new PieEntry(600,"지방"));

        PieDataSet pieDataSet = new PieDataSet(dataValue,"하루 칼로리 파이차트");
        pieDataSet.setColors(colorArray);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
        //경계선
        pieDataSet.setSliceSpace(3f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        //유저에 맞는 칼로리 넣기
        pieChart.setCenterText("kcal");
        //중앙 텍스트 크기
        pieChart.setCenterTextSize(16f);
    }
    //endregion

    //region 막대차트
    public void SetBarChart(HorizontalBarChart barChart){
        //내부 데이터
        ArrayList<BarEntry> dataValue = new ArrayList<>();
        dataValue.add(new BarEntry(0,200));
        dataValue.add(new BarEntry(1,100));
        dataValue.add(new BarEntry(2,50));

//        //라벨 데이터
//        ArrayList<String> getXAxisValues = new ArrayList<>();
//        getXAxisValues.add("탄수화물");
//        getXAxisValues.add("단백질");
//        getXAxisValues.add("지방");

        BarDataSet barDataSet = new BarDataSet(dataValue,"");
        barDataSet.setColors(colorArray);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        //데이터 값 넓이 설정
        barData.setBarWidth(0.35f);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        //격자 선 제거
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        //상호작용 제거
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);

//        //세로축 이름
//        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisValues));

        //데이터 내부에 표시
        barChart.setDrawValueAboveBar(true);

        // Hide graph legend
        barChart.getLegend().setEnabled(false);

        //적용
        barChart.invalidate();
    }
    //endregion
}
