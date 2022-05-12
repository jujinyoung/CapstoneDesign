package com.example.myapplication.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class imagetest extends StringRequest {
    //서버 URl 설정
    final static private String URL = "http://121.127.86.101:90/imgupload00.php";
    private Map<String,String> map;

    public imagetest(String picture0,Response.Listener<String> listener){
        super(Request.Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("IMAGE",picture0);
    }

    //서버에서 요청하는 파라미터 전송
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
