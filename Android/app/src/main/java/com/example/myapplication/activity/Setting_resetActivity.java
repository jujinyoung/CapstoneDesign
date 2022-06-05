package com.example.myapplication.activity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.DBHelper;
import com.example.myapplication.database.Diary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Setting_resetActivity extends AppCompatActivity {
    Button btn_resetButton,btn_back;

    //DB
    public static DBHelper mDatabase = null;
    Diary diary;

    //datepicker
    int mYear, mMonth, mDay;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_reset);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        openDatabase();

        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        btn_resetButton = findViewById(R.id.btn_resetButton2);
        btn_resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Setting_resetActivity.this, mDateSetListener, mYear, mMonth, mDay).show();
            }
        });
    }

    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //사용자가 입력한 값을 가져온뒤
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;

            LocalDate day = LocalDate.of(mYear,mMonth+1,mDay);
            String message = monthYearFromDate(day);
            diary = getTableData(message);
            deleteNote(message);
            Log.e("삭제확인",message);
            Toast.makeText(getApplicationContext(), "해당 날짜의 일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        }
    };


    private String monthYearFromDate(LocalDate date)
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
                    diary = new Diary();
//
            }
            return diary;
        }
        catch (SQLException mSQlException)
        {
            throw mSQlException;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    public  void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = DBHelper.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d("TAG", "database is open.");
        } else {
            Log.d("TAG", "database is not open.");
        }
    }

    private void deleteNote(String message) {

        if (diary != null) {
            // delete note
            String sql = "delete from " + DBHelper.TABLE_NAME +
                    " where " +
                    "_id ='" + message.replace(" ","")+"'";

            DBHelper database = DBHelper.getInstance(getApplicationContext());
            database.execSQL(sql);

//           Toast.makeText(getApplicationContext(),"DB삭제",Toast.LENGTH_SHORT).show();
        }
    }
}
