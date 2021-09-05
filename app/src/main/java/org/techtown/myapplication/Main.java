package org.techtown.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Main extends AppCompatActivity {
    private Button btn_bal;
    private  Button btn_su;
    String userID,startP,cartID,state;
    boolean isThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");




        btn_su= findViewById(R.id.btn_su);
        btn_bal = findViewById(R.id.btn_bal);

        btn_su.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(Main.this, DeliveryStat.class);
                intent.putExtra("userID", userID);

                startActivity(intent);
                finish();


            }
        });
        btn_bal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();

            }
        });
    }
    private void show()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가

                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) { // 현재 배송중인 물건이있는 경우
                        startP= jsonObject.getString("departure");
                        cartID = jsonObject.getString("cartID");
                        isThread=false;
                        Toast.makeText(getApplicationContext(), "현재 배송현황으로 이동합니다..", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(Main.this, Satisfaction.class);
                        intent2.putExtra("startP", startP);
                        intent2.putExtra("cartID", cartID);
                        intent2.putExtra("userID", userID);
                        intent2.putExtra("state",state);

                        startActivity(intent2);
                        finish();

                    } else { //없는 경우
                        Toast.makeText(getApplicationContext(), "새로운 배송을 시작합니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Main.this, Hochul.class);
                        intent.putExtra("userID", userID);
                        isThread=true;
                        startActivity(intent);
                        finish();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        MainSaticfactionRequest mainSaticfactionRequest= new MainSaticfactionRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Main.this);
        queue.add(mainSaticfactionRequest);
    }
}
