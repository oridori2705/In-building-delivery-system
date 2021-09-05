package org.techtown.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryStat extends AppCompatActivity {

    private TextView tv_togo, tv_recive, tv_things,tv_sender,tv_time;
    private String recive,sender,destination,thingsName, user_Ch,state;
    private int time;
    private AlertDialog dialog;
    private Button btn_final,btn_refresh;
    private boolean YesNO,isContinue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deliverystat);


        tv_togo = findViewById(R.id.tv_su_destination);
        tv_recive = findViewById(R.id.tv_recive);
        tv_things = findViewById(R.id.tv_su_thingsName);
        tv_sender=findViewById(R.id.tv_su_sender);
        tv_time=findViewById(R.id.tv_time2);

        Intent intent = getIntent();
        recive = intent.getStringExtra("userID");

        user_Ch="wait";
        show();



        btn_final = findViewById(R.id.btn_final2);
        btn_final.setEnabled(false);
        btn_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_Ch="success";

                successinsert();
                Toast.makeText(getApplicationContext(), "배송수령을 완료했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeliveryStat.this, Main.class);
                startActivity(intent);
                finish();
            }
        });




        btn_refresh= findViewById(R.id.btn_refresh2);

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show();
                Toast.makeText(getApplicationContext(), "새로고침하였습니다."+time, Toast.LENGTH_SHORT).show();
                if(time==0)
                {
                    btn_final.setEnabled(true);
                    btn_refresh.setEnabled(false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryStat.this);
                    dialog = builder.setMessage("현재 사용자님께 배송물품이 도착하였습니다.").setPositiveButton("확인", null).create();
                    dialog.show();
                }

            }
        });



    }






    private void check_db()
    {
        if(state.equals("wait"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryStat.this);
            builder.setTitle("배송요청이있습니다."); //제목
            builder.setMessage("배송요청을 수락하시겠습니까?"); // 메시지
            builder.setIcon(R.drawable.robot); // 아이콘 설정
            builder.setPositiveButton("수락",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //토스트 메시지
                    Toast.makeText(DeliveryStat.this,"확인을 눌르셨습니다.",Toast.LENGTH_SHORT).show();
                    user_Ch="accept";
                    accpetinsert();

                }
            });
            builder.setNegativeButton("거부", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "배송요청을 거부하였습니다.", Toast.LENGTH_SHORT).show();
                    user_Ch="deny";
                    denyinsert();
                    Intent intent = new Intent(DeliveryStat.this, Main.class);
                    startActivity(intent);
                    finish();


                }
            });
            dialog = builder.create();
            dialog.show();
        }

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
                    if (success) {

                        sender = jsonObject.getString("userID");
                        destination = jsonObject.getString("arrival");
                        thingsName = jsonObject.getString("product");
                        state = jsonObject.getString("state");
                        time = jsonObject.getInt("time");

                        tv_recive.setText(recive);
                        tv_things.setText(thingsName);
                        tv_togo.setText(destination);
                        tv_sender.setText(sender);
                        tv_time.setText(String.valueOf(time));


                        check_db();









                    } else { // 로그인에 실패한 경우
                        AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryStat.this);
                        dialog = builder.setMessage("배송중인 물품이 없습니다...").setPositiveButton("확인", null).create();
                        dialog.show();
                        isContinue=false;
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        DeliveryStatRequest deliveryStatRequest = new DeliveryStatRequest(recive,user_Ch, responseListener);
        RequestQueue queue = Volley.newRequestQueue(DeliveryStat.this);
        queue.add(deliveryStatRequest);
    }
    private void accpetinsert()
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
                        Toast.makeText(getApplicationContext(), "배송수락요청 성공", Toast.LENGTH_SHORT).show();
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(), "배송수락요청 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        ReceiveRequest receiveRequest = new ReceiveRequest(sender,user_Ch, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(DeliveryStat.this);
        queue1.add(receiveRequest);
    }
    private void denyinsert()
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
                        Toast.makeText(getApplicationContext(), "배송거부요청 성공", Toast.LENGTH_SHORT).show();
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(), "배송거부요청 실패", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        ReceiveRequest receiveRequest = new ReceiveRequest(sender,user_Ch, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(DeliveryStat.this);
        queue1.add(receiveRequest);
    }
    private void successinsert()
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
                        Toast.makeText(getApplicationContext(), "물품수령하였습니다. 어플을 종료해주세요.", Toast.LENGTH_SHORT).show();
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(getApplicationContext(), "오류입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        ReceiveRequest receiveRequest = new ReceiveRequest(sender,user_Ch, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(DeliveryStat.this);
        queue1.add(receiveRequest);
    }
}

