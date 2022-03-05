package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Food;
import com.example.myapplication.FoodApi;
import com.example.myapplication.R;

public class MainActivity_search extends AppCompatActivity {

    EditText editTextSearch;
    Button buttonSearch,save_food,btn_cancel;
    TextView textViewResult;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_search);

        Intent intent = getIntent();
        int num = intent.getIntExtra("num_i",0);

        // 위젯을 가져온다
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewResult = findViewById(R.id.textViewResult);
        progressBar = findViewById(R.id.progressBar);

        buttonSearch = (Button) findViewById(R.id.button_Search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        save_food = findViewById(R.id.save_food);
        save_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String foodName = editTextSearch.getText().toString();
                intent.putExtra("foodName",foodName);
                intent.putExtra("num",num);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSearch.setText(null);
                textViewResult.setText(null);
            }
        });
    }
}
