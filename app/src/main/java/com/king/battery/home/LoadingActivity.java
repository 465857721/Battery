package com.king.battery.home;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.battery.main.BaseActivity;
import com.king.battery.utils.APIID;
import com.king.batterytest.R;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;


public class LoadingActivity extends BaseActivity implements SplashADListener {
    private long timelong = 4000;
    private SplashAD splashAD;
    private ViewGroup container;
    private TextView skipView;
    private ImageView splashHolder;
    private static final String SKIP_TEXT = "点击跳过 %d";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading);

        container = findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        splashHolder = findViewById(R.id.splash_holder);


//        String[] cArray = getResources().getStringArray(R.array.channel);
//        for (String c : cArray) {
//            String channel = Tools.getAppMetaData(this, "UMENG_CHANNEL");
//            if (c.equals(channel)
//                    && (System.currentTimeMillis() - Long.valueOf(BuildConfig.releaseTime) < 18 * 60 * 60 * 1000)) {
//                next();
//                return;
//            }
//        }

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermistion();
        } else {
            initAD();
        }


    }

    private void initAD() {
        fetchSplashAD(this, container, skipView, APIID.ADAPP, APIID.loadingkey, this, 5000);
    }

    private void checkPermistion() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .onGranted(permissions -> {
                    initAD();
                })
                .onDenied(permissions -> {
                    showPerDialog();
                })
                .start();
    }

    private void showPerDialog() {
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(
                R.layout.dialog_tips_p, null);
        final AlertDialog tel_dialog = new AlertDialog.Builder(this).create();

        tel_dialog.setCanceledOnTouchOutside(false);
        tel_dialog.setCancelable(false);
        tel_dialog.show();
        tel_dialog.getWindow().setContentView(layout);
        TextView btnCancel = layout.findViewById(R.id.dialog_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel_dialog.dismiss();
                finish();
            }
        });

        TextView btnOK = layout.findViewById(R.id.dialog_btn_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tel_dialog.dismiss();
                checkPermistion();
            }
        });
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

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该view给SDK后，SDK会自动给它绑定点击跳过事件。SkipView的样式可以由开发者自由定制，其尺寸限制请参考activity_splash.xml或下面的注意事项。
     * @param appId         应用ID
     * @param posId         广告位ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：取值范围[3000, 5000]，设为0表示使用广点通SDK默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    @Override
    public void onADPresent() {
        Log.i("AD_DEMO", "SplashADPresent");
        splashHolder.setVisibility(View.INVISIBLE); // 广告展示后一定要把预设的开屏图片隐藏起来
    }


    private void next() {

        //防止用户回退看到此页面
        gotoActivity();
        this.finish();
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        next();
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i("AD_DEMO", adError.getErrorMsg() + adError.getErrorCode());
        next();
    }

//    @Override
//    public void onNoAD(int arg0) {
//        Log.i("AD_DEMO", "LoadSplashADFail,ecode=" + arg0);
//        next();
//    }


    @Override
    public void onADClicked() {
        Log.i("AD_DEMO", "SplashADClicked");
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        skipView.setText(String.format(SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADExposure() {

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
