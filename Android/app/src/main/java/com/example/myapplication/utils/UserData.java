package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.myapplication.activity.CalendarActivity;
import com.example.myapplication.activity.LoginActivity;

public class UserData {
    public static String PREFERENCE_NAME= null;
    private Context mContext;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor prefsEditor;
    private static UserData instance;


    private static UserData userData = null;

    public static synchronized UserData init(Context context){
        if(instance == null)
            instance = new UserData(context);
        return instance;
    }

    public static synchronized  UserData del(Context context){
        instance = null;
        PREFERENCE_NAME= null;
        return instance;
    }

    private UserData(Context context) {
        mContext = context;
        prefs = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE );
        prefsEditor = prefs.edit();
        Log.e("userdata아이디",PREFERENCE_NAME);
    }

    public static String read(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public static void write(String key, String value) {
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static Integer readInt(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public static void writeInt(String key, Integer value) {
        prefsEditor.putInt(key, value).commit();
    }

    public static boolean read(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Float readFloat(String key, float defValue) {
        return prefs.getFloat(key, defValue);
    }

    public static void writeFloat(String key, float value) {
        prefsEditor.putFloat(key, value).commit();
    }

    //

}
