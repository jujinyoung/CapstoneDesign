package com.example.myapplication.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ImageRequest extends StringRequest {
    //서버 URl 설정
    final static private String URL = "http://121.127.86.101:80/imagetest.php";
    private Map<String,String> map;

    public ImageRequest(String _id, String picture0,String food0,String mood0, String comment0,
                        String picture1,String food1,String mood1, String comment1,
                        String picture2,String food2,String mood2, String comment2,
                        String picture3,String food3,String mood3, String comment3,
                        Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("_id",_id);
        map.put("picture0",picture0);
        map.put("food0",food0);
        map.put("mood0",mood0);
        map.put("comment0",comment0);
        map.put("picture1",picture1);
        map.put("food1",food1);
        map.put("mood1",mood1);
        map.put("comment1",comment1);
        map.put("picture2",picture2);
        map.put("food2",food2);
        map.put("mood2",mood2);
        map.put("comment2",comment2);
        map.put("picture3",picture3);
        map.put("food3",food3);
        map.put("mood3",mood3);
        map.put("comment3",comment3);
    }

    //서버에서 요청하는 파라미터 전송
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}