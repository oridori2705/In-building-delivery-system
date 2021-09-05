package org.techtown.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Hochul extends BaseActivity {
    private String startP;
    private Spinner arrival_spinner,cart_spinner;
    private boolean end=false; //로봇이 호출한목적지로 도착했을 때
    private String userName,userID,cartID,name;
    Thread thread;
    private boolean isThread=false;
    /*startProgress();
    progressOFF();*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hochul);
        Button btn_hochul= findViewById(R.id.btn_hochul);
        arrival_spinner=(Spinner)findViewById(R.id.hochul_spinner);
        cart_spinner=(Spinner)findViewById(R.id.cartID_spinner);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userName = intent.getStringExtra("userName");



        btn_hochul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startP = arrival_spinner.getSelectedItem().toString();
                cartID = cart_spinner.getSelectedItem().toString();

                arrivalinsert();

            }
        });

    }
    private void arrivalinsert()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가
                    System.out.println("hongchul" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Toast.makeText(getApplicationContext(), "호출지 삽입성공"+startP, Toast.LENGTH_SHORT).show();
                        startProgress();
                        isThread = true;
                        thread = new Thread() {
                            public void run() {
                                while (isThread) {
                                    try {
                                        sleep(5000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    handler.sendEmptyMessage(0);


                                }
                                progressOFF();

                                Intent intent = new Intent(Hochul.this, Sender.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("userName", userName);
                                intent.putExtra("startP", startP);
                                intent.putExtra("cartID", name);
                                startActivity(intent);
                                finish();

                            }
                        };
                        thread.start();
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(), "현재 카트를 사용할 수 없는 상태입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        HochulCallRequest hochulCallRequest = new HochulCallRequest(cartID,startP, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(Hochul.this);
        queue1.add(hochulCallRequest);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            endcart();
        }
    };


    private void startProgress() {

        progressON("호출하신 목적지로 이동 중...");


        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);
            이건 딜레이를 주는 메소드 딜레이 3500 를 준후 progressOFF실행
         */


    }

    private void endcart()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가
                    System.out.println("hongchul" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        isThread=false;

                        Toast.makeText(getApplicationContext(), "현재카트가 도착하였습니다.", Toast.LENGTH_SHORT).show();
                        name = jsonObject.getString("cart_ID");



                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(), "현재 카트가 이동중 또는 준비중입니다..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        HochulRequest hochulRequest = new HochulRequest(cartID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Hochul.this);
        queue.add(hochulRequest);
    }

    @Override
    public void onBackPressed () {
        Toast.makeText(getApplicationContext(),"호출화면에서부터는 뒤로가실 수 없습니다.",Toast.LENGTH_SHORT).show();
    }



}
