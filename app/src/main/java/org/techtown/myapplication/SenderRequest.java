package org.techtown.myapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SenderRequest extends StringRequest {

    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://ydoag2003.dothome.co.kr/listMake.php";
    private Map<String, String> map;


    public SenderRequest(String userID, String recive,String thingsName,String startP,String destination,String state,String cartID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID",userID);
        map.put("recipientID", recive);
        map.put("product", thingsName);
        map.put("arrival", destination);
        map.put("departure",startP);
        map.put("state",state);
        map.put("cartID",cartID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
