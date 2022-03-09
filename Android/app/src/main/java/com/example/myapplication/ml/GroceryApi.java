package com.example.myapplication.ml;

import android.util.Log;

import androidx.annotation.WorkerThread;

import com.example.myapplication.food.Food;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class GroceryApi {

    public static final String URL_FORMAT = "https://www.fatsecret.kr/%EC%B9%BC%EB%A1%9C%EB%A6%AC-%EC%98%81%EC%96%91%EC%86%8C/%EC%9D%BC%EB%B0%98%EB%AA%85/{GROCERY}";
    public static final String ARG_GROCERY = "{GROCERY}";


    @WorkerThread
    public static Food get(String grocery) {

        String url = URL_FORMAT.replace(ARG_GROCERY, grocery);

        try {
            Document document = Jsoup.connect(url).get();

            Elements divs = document.select("div.nutrition_facts.international div");
            String servingSize = "";
            double calories = 0;
            double carbohydrate = 0;
            double protein = 0;
            double fat = 0;
            double sugar = 0;
            double sodium = 0;
            double cholesterol = 0;
            double saturatedFat = 0;
            double transFat = 0;

            for (int i = 0; i < divs.size() - 1; i++) {
                String text = divs.get(i).text();
                String nextText = divs.get(i + 1).text();
                Log.d("TAG", "text: " + text + ", next: " + nextText);

                if (text.equals("서빙 사이즈")) {
                    servingSize = nextText;
                } else if (text.equals("열량")) {
                    String strCalories = nextText.replace("kJ", "").trim();
                    calories = Double.parseDouble(strCalories) / 4.2;
                } else if (text.equals("탄수화물")) {
                    String strCarbohydrate = nextText.replace("g", "").trim();
                    carbohydrate = Double.parseDouble(strCarbohydrate);
                } else if (text.equals("단백질")) {
                    String strProtein = nextText.replace("g", "").trim();
                    protein = Double.parseDouble(strProtein);
                } else if (text.equals("지방")) {
                    String strFat = nextText.replace("g", "").trim();
                    fat = Double.parseDouble(strFat);
                } else if (text.equals("설탕당")) {
                    String strSugar = nextText.replace("g", "").trim();
                    sugar = Double.parseDouble(strSugar);
                } else if (text.equals("나트륨")) {
                    String strSodium = nextText.replace("mg", "").trim();
                    sodium = Double.parseDouble(strSodium);
                } else if (text.equals("콜레스테롤")) {
                    String strCholesterol = nextText.replace("mg", "").trim();
                    cholesterol = Double.parseDouble(strCholesterol);
                } else if (text.equals("포화지방")) {
                    String strSaturatedFat = nextText.replace("g", "").trim();
                    saturatedFat = Double.parseDouble(strSaturatedFat);
                } else if (text.equals("트랜스 지방")) {
                    String strTransFat = nextText.replace("g", "").trim();
                    transFat = Double.parseDouble(strTransFat);
                }
            }

            return new Food(grocery, servingSize, calories, carbohydrate, protein, fat,
                    sugar, sodium, cholesterol, saturatedFat, transFat);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
