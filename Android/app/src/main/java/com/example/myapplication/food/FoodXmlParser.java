package com.example.myapplication.food;

import android.util.Xml;

import com.example.myapplication.activity.DiaryActivity_search;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FoodXmlParser {

    private static final String ns = null;

    // 주어진 XML 파싱하기
    public static Food parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readI2790(parser);
        } finally {
            in.close();
        }
    }

    // I2790 태그 읽기
    private static Food readI2790(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "I2790");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // row 태그 찾기
            if (name.equals("row")) {
                return readRow(parser);
            } else {
                skip(parser);
            }
        }
        return null;
    }

    // row 태그 읽기
    private static Food readRow(XmlPullParser parser) throws XmlPullParserException, IOException {

        String name = "";
        String servingSize = "";
        double calories = 0;
        double carbohydrate = 0;
        double protein = 0;
        double fat = 0;
        double sugar = 0;
        double sodium = 0;
        double cholesterol = 0;
        double sfa = 0;
        double transFat = 0;

        parser.require(XmlPullParser.START_TAG, ns, "row");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tag = parser.getName();
            switch (tag) {
                case "DESC_KOR":
                    name = readText(parser);
                    DiaryActivity_search.items.add(name);
                    break;
                case "SERVING_SIZE":
                    servingSize = readText(parser);
                    break;
                case "NUTR_CONT1":
                    calories = readDouble(parser);
                    break;
                case "NUTR_CONT2":
                    carbohydrate = readDouble(parser);
                    break;
                case "NUTR_CONT3":
                    protein = readDouble(parser);
                    break;
                case "NUTR_CONT4":
                    fat = readDouble(parser);
                    break;
                case "NUTR_CONT5":
                    sugar = readDouble(parser);
                    break;
                case "NUTR_CONT6":
                    sodium = readDouble(parser);
                    break;
                case "NUTR_CONT7":
                    cholesterol = readDouble(parser);
                    break;
                case "NUTR_CONT8":
                    sfa = readDouble(parser);
                    break;
                case "NUTR_CONT9":
                    transFat = readDouble(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        return new Food(name, servingSize, calories, carbohydrate, protein, fat, sugar, sodium, cholesterol, sfa, transFat);
    }


    // 태그 안의 텍스트 읽기
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // 태그 안의 텍스트 읽기
    private static double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {

        String result = readText(parser);
        try {
            return Double.parseDouble(result);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 태그 건너뛰기
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {

        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

