package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity_search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위젯을 가져온다
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        TextView textViewResult = findViewById(R.id.textViewResult);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // 검색 버튼 시 실행한다
        buttonSearch.setOnClickListener(v -> {

            // 키워드를 확인한다
            String foodName = editTextSearch.getText().toString().trim();
            if (foodName.isEmpty()) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // API 를 이용한 검색을 실행한다
            new Thread(() -> {
                Food food = FoodApi.get(foodName);

                // 결과를 출력한다
                runOnUiThread(() -> {
                    if (food != null) {
                        textViewResult.setText(food.toString());
                    } else {
                        textViewResult.setText("검색된 결과가 없습니다.");
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }).start();
        });
    }
}
