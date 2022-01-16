package com.example.myapplication.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.calendar.CalendarAdapter;
import com.example.myapplication.calendar.CalendarViewHolder;
import com.github.mikephil.charting.charts.PieChart;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {

    //캘린더
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;

    //그래프
    private PieChart pieChart;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initWidgets();
        //현재날짜
        selectedDate = LocalDate.now();
        setMonthView();

        //그래프
        pieChart = findViewById(R.id.pie_chart);
        SetPieChart(pieChart);
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


    public void SetPieChart(PieChart pieChart) {

    }
}
