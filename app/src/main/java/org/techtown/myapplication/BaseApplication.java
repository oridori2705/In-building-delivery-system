package org.techtown.myapplication;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

public class BaseApplication extends Application {

    private int state,result;

    private static BaseApplication baseApplication;
    AppCompatDialog progressDialog;
    public static BaseApplication getInstance()
    {
        return baseApplication;
    }
    @Override
    public void onCreate()
    {
        result = 0;
        state = 0;
        super.onCreate();
        baseApplication = this;
    }
    public void progressON(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressSET(message);
        }
        else
            {
                progressDialog = new AppCompatDialog(activity);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressDialog.setContentView(R.layout.progress_loading);
                progressDialog.show();
            }
        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run()
            {
                frameAnimation.start();
            }
        });
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }
    public void progressSET(String message)
    {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message))
        {
            tv_progress_message.setText(message);
        }
    }
    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setState(int state){
        this.state = state;
    }

    public int getState(){
        return state;
    }
    public void setResult(int result){
        this.result = result;
    }

    public int getResult(){
        return result;
    }
}

