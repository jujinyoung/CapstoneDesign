package com.example.myapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.food.Food;
import com.example.myapplication.food.FoodApi;
import com.example.myapplication.ml.GroceryApi;
import com.example.myapplication.R;
import com.example.myapplication.ml.FoodModel;
import com.example.myapplication.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiaryActivity_search extends AppCompatActivity {

    EditText editTextSearch;
    Button buttonSearch,save_food,btn_cancel;
    TextView textViewResult,textViewIdentifiedFood;
    ProgressBar progressBar;
    ImageView search_image;
    double tan,dan,gi,kcal;
    String servingsize,foodname;
//    ListView food_list;
    public static ArrayList<String> items = new ArrayList<String>();
//    View View_image,View_search;


    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();

    private final Map<String, String> koreanMap = NameUtils.getKoreanMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_search);



//        View_image = findViewById(R.id.View_image);
//        View_search = findViewById(R.id.View_search);

        //이미지 받아오기
        Intent intent = getIntent();
        int num = intent.getIntExtra("num_i",0);
        search_image = findViewById(R.id.search_image);
        if(getIntent().getByteArrayExtra("image") != null){
            byte[] byteArray = getIntent().getByteArrayExtra("image");
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            image.setValue(bitmap);
            search_image.setImageBitmap(bitmap);
        } else{
            search_image.setVisibility(View.INVISIBLE);
//            food_list.setOnClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    String data = (String) parent.getItemAtPosition(position);
//                }
//            });
        }

        // 위젯을 가져온다
        textViewIdentifiedFood = findViewById(R.id.textViewIdentifiedFood);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewResult = findViewById(R.id.textViewResult);
        progressBar = findViewById(R.id.progressBar);

        //검색 기능
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
                            tan = food.getCarbohydrate();
                            dan = food.getProtein();
                            gi = food.getFat();
                            kcal = food.getCalories();
                            servingsize = food.getServingSize().replace("g","");
                            foodname = food.getName();
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
                intent.putExtra("foodName",foodname);
                intent.putExtra("num",num);
                intent.putExtra("tan",tan);
                intent.putExtra("dan",dan);
                intent.putExtra("gi",gi);
                intent.putExtra("kcal",kcal);
                intent.putExtra("servingsize",servingsize);


                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText(null);
                textViewResult.setText(null);
            }
        });

        //region 이미지 분석
        try {
            LiveData<String> foodName = Transformations.map(image, bitmap1 -> {
                String name = new FoodModel(this).getFoodName(bitmap1);
                return koreanMap.get(name);
            });

            // 음식 이름으로부터 식품 정보를 획득한다
            LiveData<Food> food = Transformations.switchMap(foodName, name -> {
                MutableLiveData<Food> data = new MutableLiveData<>();
                new Thread(() -> data.postValue(GroceryApi.get(name))).start();
                return data;
            });

            // 판별된 음식 이름을 텍스트뷰에 띄운다
            foodName.observe(this, name -> {
                String str = "판별값: " + name;
                textViewIdentifiedFood.setText(str);
            });

            // 식품 정보를 텍스트뷰에 띄운다
            food.observe(this, foodValue -> {
                if (foodValue != null) {
                    textViewResult.setText(foodValue.toString());
                    tan = foodValue.getCarbohydrate();
                    dan = foodValue.getProtein();
                    gi = foodValue.getFat();
                    kcal = foodValue.getCalories();
                    servingsize = foodValue.getServingSize();
                    foodname = foodValue.getName();
                } else {
                    textViewResult.setText("등록되지 않은 음식입니다");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

        //endregion

    }


}
