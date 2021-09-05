package org.techtown.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Satisfaction extends BaseActivity{



    private TextView tv_togo, tv_recive, tv_things,tv_time,tv_re_SatisF ;
    private String recive;
    private String sender;
    private String destination;
    private String thingsName;
    private String startP;
    private String cartID;
    private String receive_answer="";
    private int time;
    private ImageView iv_deliveryStat;
    private Button btn_final,btn_refresh;
    private String result,re_SatisF ;
    Thread thread;
    private AlertDialog dialog;
    private boolean isThread,isContinue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_satisfaction);

        Intent intent = getIntent();
        sender = intent.getStringExtra("userID");
        startP = intent.getStringExtra("startP");
        cartID= intent.getStringExtra("cartID");

        iv_deliveryStat=findViewById(R.id.iv_delivertStat);
        tv_togo = findViewById(R.id.tv_togo);
        tv_recive = findViewById(R.id.tv_recive);
        tv_things = findViewById(R.id.tv_things);
        tv_time =findViewById(R.id.tv_time);
        tv_re_SatisF =findViewById(R.id.tv_re_SatisF);




        progressON("잠시만 기다려주세요..");
        isThread = true;
        thread = new Thread() {
            public void run() {
                while (isThread) {
                    try {
                        sleep(3500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);


                }
                progressOFF();
            }
        };
        thread.start();

        btn_refresh = findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh();
                if(receive_answer.equals("success"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Satisfaction.this);
                    dialog = builder.setMessage("배송이 완료되었습니다. 배송완료처리를 눌러주세요").setPositiveButton("확인", null).create();
                    dialog.show();
                    iv_deliveryStat.setImageResource(R.drawable.de_finish);
                    btn_final.setEnabled(true);
                    btn_refresh.setEnabled(false);
                }
                else if(receive_answer.equals("timeout"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Satisfaction.this);
                    dialog = builder.setMessage("목적지에 도착했지만 수령인이 물건을 수령하지 못했습니다. 완료를 눌러주세요").setPositiveButton("확인", null).create();
                    dialog.show();
                    iv_deliveryStat.setImageResource(R.drawable.de_finish);
                    btn_final.setEnabled(true);
                    btn_refresh.setEnabled(false);

                }

            }
        });


        btn_final = findViewById(R.id.btn_final2);
        btn_final.setEnabled(false);

        btn_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                result=receive_answer;
                if(receive_answer.equals("deny"))
                {

                    result= "Failure due to rejection";
                }
                else if(receive_answer.equals("timeout")){
                    result="Failure due to timeout";
                }
                delete();

            }
        });

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            Toast.makeText(getApplicationContext(), "배송상태 확인중", Toast.LENGTH_SHORT).show();
            show2();
        }
    };
    private void startProgress() {

        progressON("수령인의 배송수락을 기다리는 중...");


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
    //뒤로가면 다시 올수가 없는 버그가 있음 왜그런지 모르겠음 아님 뒤로가는걸 막아야함
    @Override
    public void onBackPressed () {
        Intent intent = new Intent(Satisfaction.this, Main.class);
        startActivity(intent);
        finish();

    }
    private void delete()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가
                    System.out.println("hongchul" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) { // 로그인에 성공한 경우
                        Toast.makeText(getApplicationContext(), "배송완료", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Satisfaction.this, Main.class);
                        startActivity(intent);
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(), "배송완료처리 실패.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        SatisfactionRequestDelete satisfactionRequestDelete = new SatisfactionRequestDelete(sender,recive,thingsName,startP,destination,cartID,result,re_SatisF,  responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(Satisfaction.this);
        queue1.add(satisfactionRequestDelete);
    }
        private void show2() {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가

                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) { // 로그인에 성공한 경우
                            isThread = false;

                            recive = jsonObject.getString("recipientID");
                            destination = jsonObject.getString("arrival");
                            thingsName = jsonObject.getString("product");
                            receive_answer = jsonObject.getString("state");
                            time = jsonObject.getInt("time");
                            re_SatisF = jsonObject.getString("Satisfiction");

                            if (receive_answer.equals("accept")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Satisfaction.this);
                                dialog = builder.setMessage("수령인이 수락하였습니다.").setPositiveButton("확인", null).create();
                                iv_deliveryStat.setImageResource(R.drawable.delivering);
                                dialog.show();
                            } else if (receive_answer.equals("deny")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Satisfaction.this);
                                dialog = builder.setMessage("수령인이 거부하였습니다. 배송완료처리를 눌러 주문을 취소해주세요.").setPositiveButton("확인", null).create();
                                dialog.show();
                                btn_final.setEnabled(true);
                            }
                            else if(receive_answer.equals("success"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Satisfaction.this);
                                dialog = builder.setMessage("배송이 완료되었습니다. 배송완료처리를 눌러주세요").setPositiveButton("확인", null).create();
                                dialog.show();
                                iv_deliveryStat.setImageResource(R.drawable.de_finish);
                                btn_final.setEnabled(true);
                                btn_refresh.setEnabled(false);
                            }
                            else if (receive_answer.equals("timeout"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Satisfaction.this);
                                dialog = builder.setMessage("목적지에 도착했지만 수령인이 물건을 수령하지 못했습니다. 완료를 눌러주세요").setPositiveButton("확인", null).create();
                                dialog.show();
                                iv_deliveryStat.setImageResource(R.drawable.de_finish);
                                btn_final.setEnabled(true);
                                btn_refresh.setEnabled(false);

                            }





                            tv_recive.setText(recive);
                            tv_things.setText(thingsName);
                            tv_togo.setText(destination);
                            tv_time.setText(String.valueOf(time));
                            tv_re_SatisF.setText(re_SatisF);

                        } else { // 로그인에 실패한 경우
                            Toast.makeText(getApplicationContext(), "수령인이 아직 수락을 하지 않았습니다..", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
            SatisfactionRequest satisfactionRequest = new SatisfactionRequest(sender, responseListener);
            RequestQueue queue = Volley.newRequestQueue(Satisfaction.this);
            queue.add(satisfactionRequest);
        }
    private void refresh()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // TODO : 인코딩 문제때문에 한글 DB인 경우 로그인 불가

                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) { // 로그인에 성공한 경우
                        isThread = false;

                        recive = jsonObject.getString("recipientID");
                        destination = jsonObject.getString("arrival");
                        thingsName = jsonObject.getString("product");
                        receive_answer = jsonObject.getString("state");
                        re_SatisF = jsonObject.getString("Satisfiction");
                        time = jsonObject.getInt("time");

                        tv_recive.setText(recive);
                        tv_things.setText(thingsName);
                        tv_togo.setText(destination);
                        tv_time.setText(String.valueOf(time));
                        tv_re_SatisF.setText(re_SatisF);

                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(), "새로고침에 오류가 생겼습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        SatisfactionRequest satisfactionRequest = new SatisfactionRequest(sender, responseListener);
        RequestQueue queue = Volley.newRequestQueue(Satisfaction.this);
        queue.add(satisfactionRequest);
    }



}
