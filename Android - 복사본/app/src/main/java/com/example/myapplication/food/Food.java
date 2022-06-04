package com.example.myapplication.food;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Food {

    private final String name;              // 식품이름, DESC_KOR
    private final String servingSize;       // 총내용량, SERVING_SIZE
    private final double calories;          // 칼로리(kcal), NUTR_CONT1
    private final double carbohydrate;      // 탄수화물(g), NUTR_CONT2
    private final double protein;           // 단백질(g), NUTR_CONT3
    private final double fat;               // 지방(g), NUTR_CONT4
    private final double sugar;             // 당류(g), NUTR_CONT5
    private final double sodium;            // 나트륨(mg), NUTR_CONT6
    private final double cholesterol;       // 콜레스테롤(mg), NUTR_CONT7
    private final double sfa;               // 포화지방산(g), NUTR_CONT8
    private final double transFat;          // 트랜스지방(g), NUTR_CONT9

    public Food(String name, String servingSize, double calories, double carbohydrate, double protein, double fat, double sugar, double sodium, double cholesterol, double sfa, double transFat) {
        this.name = name;
        this.servingSize = servingSize;
        this.calories = calories;
        this.carbohydrate = carbohydrate;
        this.protein = protein;
        this.fat = fat;
        this.sugar = sugar;
        this.sodium = sodium;
        this.cholesterol = cholesterol;
        this.sfa = sfa;
        this.transFat = transFat;
    }

    public String getName() {
        return name;
    }

    public String getServingSize() {
        return servingSize;
    }

    public double getCalories() {
        return calories;
    }

    public double getCarbohydrate() {
        return carbohydrate;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public double getSugar() {
        return sugar;
    }

    public double getSodium() {
        return sodium;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public double getSfa() {
        return sfa;
    }

    public double getTransFat() {
        return transFat;
    }


    @NonNull
    @Override
    public String toString() {
        return name +
                "\n\n총내용량: " + servingSize +
                "\n열량: " + String.format(Locale.getDefault(), "%.1f kcal", calories) +
                "\n탄수화물: " + carbohydrate + "g" +
                "\n단백질: " + protein + "g" +
                "\n지방: " + fat + "g" +
                "\n당류: " + sugar + "g" +
                "\n나트륨: " + sodium + "mg" +
                "\n콜레스테롤: " + cholesterol + "mg" +
                "\n포화지방산: " + sfa + "g" +
                "\n트랜스지방: " + transFat + "g";
    }
}