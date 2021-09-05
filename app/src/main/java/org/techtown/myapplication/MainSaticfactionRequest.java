package org.techtown.myapplication;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MainSaticfactionRequest extends StringRequest {
    // 서버 URL 설정 ( PHP 파일 연동 )
    final static private String URL = "http://ydoag2003.dothome.co.kr/CheckToDB.php";
    private Map<String, String> map;


    public MainSaticfactionRequest(String sender, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID",sender);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }


}
