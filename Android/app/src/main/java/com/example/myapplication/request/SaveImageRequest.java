package com.example.myapplication.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SaveImageRequest extends StringRequest {
    //서버 URl 설정
    final static private String URL = "http://121.127.86.101:90/test02.php";
    private Map<String,String> map;

    public SaveImageRequest(String _id, String picture0, String food0, String kcal0,
                            String picture1, String food1, String kcal1,
                            String picture2, String food2, String kcal2,
                            String picture3, String food3, String kcal3,
                            Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("_id",_id);
        map.put("picture0",picture0);
        map.put("food0",food0);
        map.put("mood0",kcal0);
        map.put("picture1",picture1);
        map.put("food1",food1);
        map.put("mood1",kcal1);
        map.put("picture2",picture2);
        map.put("food2",food2);
        map.put("mood2",kcal2);
        map.put("picture3",picture3);
        map.put("food3",food3);
        map.put("mood3",kcal3);
    }

    //서버에서 요청하는 파라미터 전송
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}