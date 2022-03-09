package com.example.myapplication.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PasswordRequest extends StringRequest {
    //서버 URl 설정
    final static private String URL = "라즈베리파이ip주소/로그인js파일";
    private Map<String,String> map;

    public PasswordRequest(String et_id_password, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("et_id_password",et_id_password);
    }

    //서버에서 요청하는 파라미터 전송
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
