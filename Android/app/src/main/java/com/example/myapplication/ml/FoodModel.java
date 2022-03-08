package com.example.myapplication.ml;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.List;

public class FoodModel {

    private static final String TAG = "FoodModel";

    private final Context context;


    public FoodModel(Context context) {
        this.context = context;
    }

    public String getFoodName(Bitmap bitmap) {

        try {
            com.example.myapplication.ml.GroceryModel model = com.example.myapplication.ml.GroceryModel.newInstance(context);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true));

            // Runs model inference and gets result.
            com.example.myapplication.ml.GroceryModel.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            float maxScore = 0;
            Category bestCategory = null;
            for (Category category : probability) {
                Log.d(TAG,  category.getLabel() + " : " + category.getScore());
                if (category.getScore() > maxScore) {
                    maxScore = category.getScore();
                    bestCategory = category;
                }
            }

            // Releases model resources if no longer used.
            model.close();

            return bestCategory != null ? bestCategory.getLabel() : null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

