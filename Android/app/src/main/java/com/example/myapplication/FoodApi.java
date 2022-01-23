package com.example.myapplication;

import androidx.annotation.WorkerThread;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodApi {

    private static final String TAG = "FoodApi";

    public static final String URL_FORMAT = "http://openapi.foodsafetykorea.go.kr/api/{SERVICE_KEY}/I2790/xml/1/5/DESC_KOR={FOOD_NAME}";
    public static final String ARG_SERVICE_KEY = "{SERVICE_KEY}";
    public static final String ARG_FOOD_NAME = "{FOOD_NAME}";
    public static final String SERVICE_KEY = "4e5f17368ca643069717";

    @WorkerThread
    public static Food get(String foodName) {

        String strUrl = URL_FORMAT
                .replace(ARG_SERVICE_KEY, SERVICE_KEY)
                .replace(ARG_FOOD_NAME, foodName);

        System.out.println(strUrl);
        //Log.d(TAG, "get: " + strUrl);

        try {
            // API 를 호출하고 xml 을 파싱.
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            return FoodXmlParser.parse(conn.getInputStream());

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

