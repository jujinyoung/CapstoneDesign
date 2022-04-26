package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.AsyncTask;
import android.os.Bundle;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.bluetooth.DevicesFragment;


public class DiaryActivity_loadc extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

//    /*프로그래스바 사용*/
//    private TextView mTextView;
//    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_loadc);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.fragment1, new DevicesFragment(), "devices").commit();
        else
            onBackStackChanged();
    }

    @Override
    public void onBackStackChanged() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



//    /*내부 클래스*/
//    class DownloadTask extends AsyncTask<Void, Integer, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            for (int i = 0; i <= 100; i++) {
//                try {
//                    Thread.sleep(100);                  //0.1초 간격으로 sleep
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                final int percent = i;
//                publishProgress(percent);
//
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            mTextView.setText(values[0] + "%");
//            mProgressBar.setProgress(values[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//    }
}


