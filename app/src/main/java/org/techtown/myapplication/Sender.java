package org.techtown.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Sender extends AppCompatActivity {
    private EditText et_things,et_recive;
    private Spinner et_togo;
    private AlertDialog dialog;
    private String state,cartID,startP,userID;
    private TextView tv_cartName;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sender);


        et_recive = (EditText) findViewById(R.id.sp_recive);
        et_things = (EditText) findViewById(R.id.sp_things);
        et_togo = (Spinner) findViewById(R.id.sp_togo);
        tv_cartName=(TextView)findViewById(R.id.tv_cartID);;
        state="wait";
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        startP = intent.getStringExtra("startP");
        cartID = intent.getStringExtra("cartID");

        tv_cartName.setText(cartID);


        Button btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recive = et_recive.getText().toString();
                String destination = et_togo.getSelectedItem().toString();
                String thingsName = et_things.getText().toString();


                //한 칸이라도 입력 안했을 경우
                if (thingsName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sender.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {
                                Toast.makeText(getApplicationContext(), "배송에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Sender.this, Satisfaction.class);
                                intent.putExtra("userID", userID);
                                intent.putExtra("startP", startP);
                                intent.putExtra("cartID", cartID);
                                intent.putExtra("state",state);
                                startActivity(intent);
                                finish();

                            } else { // 회원등록에 실패한 경우
                                Toast.makeText(getApplicationContext(), "배송에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                };

                // 서버로 Volley를 이용해서 요청을 함.
                SenderRequest senderRequest = new SenderRequest(userID, recive, thingsName,startP, destination,state,cartID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Sender.this);
                queue.add(senderRequest);


            }
        });


    }
    @Override
    public void onBackPressed () {
        //super.onBackPressed();
    }

}
