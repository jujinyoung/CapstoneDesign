package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity_loadc extends AppCompatActivity {

    /*프로그래스바 사용*/
    private TextView mTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_loadc);

        mTextView = (TextView) findViewById(R.id.loading_percent);
        mProgressBar = findViewById(R.id.progressBar_loadc);

        new DownloadTask().execute();    //내부 클래스 실행
}
    /*내부 클래스*/

    class DownloadTask extends AsyncTask<Void, Integer, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i <= 100; i++){
                try {
                    Thread.sleep(100);                  //0.1초 간격으로 sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final int percent = i;
                publishProgress(percent);

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mTextView.setText(values[0] +"%");
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
