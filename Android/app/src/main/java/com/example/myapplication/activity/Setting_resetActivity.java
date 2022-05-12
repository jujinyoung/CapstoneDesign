package com.example.myapplication.activity;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.DBHelper;
import com.example.myapplication.database.Diary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Setting_resetActivity extends AppCompatActivity {
    Button btn_resetButton;

    //DB
    public static DBHelper mDatabase = null;
    Diary diary;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_reset);

        openDatabase();

        btn_resetButton = findViewById(R.id.btn_resetButton);
        btn_resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDateTime now = LocalDateTime.now();
                String message = monthYearFromDate(now);
                diary = getTableData(message);
                deleteNote(message);
            }
        });
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
                    "   _id = " + message.replace(" ","");

            DBHelper database = DBHelper.getInstance(getApplicationContext());
            database.execSQL(sql);

//            Toast.makeText(getApplicationContext(),"DB삭제",Toast.LENGTH_SHORT).show();
        }
    }
}
