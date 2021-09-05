package org.techtown.myapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SatisfactionRequestDelete extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://ydoag2003.dothome.co.kr/Delete.php";
    private Map<String, String> map;


    public SatisfactionRequestDelete(String sender,String recipientID,String product,String departure,String arrival,String cartID,String result,String satisF, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID",sender);
        map.put("recipientID",recipientID);
        map.put("product",product);
        map.put("departure",departure);
        map.put("arrival",arrival);
        map.put("cartID",cartID);
        map.put("finish_state",result);
        map.put("satisF",satisF);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
