package com.king.batterytest.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.king.batterytest.R;
import com.king.batterytest.main.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;


public class LoadingActivity extends BaseActivity {
    private long timelong = 4000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);
        Timer time = new Timer();
        TimerTask tk = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                gotoActivity();
                finish();
            }
        };
        time.schedule(tk, timelong);
    }

    @Override
    protected void setStatusBar() {

    }

    private void gotoActivity() {
        Intent go;
        go = new Intent(this, HomeActivity.class);
        startActivity(go);
        finish();

    }
}
