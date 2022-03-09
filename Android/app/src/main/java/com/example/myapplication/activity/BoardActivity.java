package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.gallery.ImageAdapter;

public class BoardActivity extends AppCompatActivity {

    GridView gridView;

    String[] numberWord = {"One", "Two", "Three", "Four", "Five", "Six"};

    int[] numberImage = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        gridView = findViewById(R.id.gridview);

        ImageAdapter numberAdapter = new ImageAdapter(BoardActivity.this, numberWord, numberImage);
        gridView.setAdapter(numberAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Toast.makeText(getApplicationContext(), numberWord[position]+ "을 클릭했습니다."
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }
}