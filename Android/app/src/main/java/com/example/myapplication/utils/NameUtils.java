package com.example.myapplication.utils;

import java.util.HashMap;
import java.util.Map;

public class NameUtils {

    public static Map<String, String> getKoreanMap() {

        Map<String, String> map = new HashMap<>();

        map.put("apple", "사과");
        map.put("carrot", "당근");
        map.put("cabbage", "양배추");
        map.put("cucumber", "오이");
        map.put("eggplant", "가지");

        return map;
    }

}
