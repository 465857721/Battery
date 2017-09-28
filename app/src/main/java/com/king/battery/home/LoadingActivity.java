package com.king.battery.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.battery.main.BaseActivity;
import com.king.batterytest.R;


public class LoadingActivity extends BaseActivity {
    private long timelong = 4000;

    private ViewGroup container;
    private TextView skipView;
    private ImageView splashHolder;
    private static final String SKIP_TEXT = "点击跳过 %d";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        container = (ViewGroup) this.findViewById(R.id.splash_container);
        skipView = (TextView) findViewById(R.id.skip_view);
        splashHolder = (ImageView) findViewById(R.id.splash_holder);


//        Timer time = new Timer();
//        TimerTask tk = new TimerTask() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                gotoActivity();
//                finish();
//            }
//        };
//        time.schedule(tk, timelong);
    }

    @Override
    protected void setStatusBar() {

    }

    private void gotoActivity() {
        Intent go;
        go = new Intent(this, HomeActivity.class);
        getIntent().getExtras();
        if (getIntent() != null) {
            go.putExtra("type", getIntent().getIntExtra("type", 0));
        }

        startActivity(go);
        finish();

    }


    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
