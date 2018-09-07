package com.king.battery.home;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.battery.main.BaseActivity;
import com.king.battery.utils.APIID;
import com.king.battery.utils.Tools;
import com.king.batterytest.BuildConfig;
import com.king.batterytest.R;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;


public class LoadingActivity extends BaseActivity implements SplashADListener {
    private long timelong = 4000;
    private SplashAD splashAD;
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

        container = findViewById(R.id.splash_container);
        skipView = findViewById(R.id.skip_view);
        splashHolder = findViewById(R.id.splash_holder);


        String[] cArray = getResources().getStringArray(R.array.channel);
        for (String c : cArray) {
            String channel = Tools.getAppMetaData(this, "UMENG_CHANNEL");
            if (c.equals(channel)
                    && (System.currentTimeMillis() - Long.valueOf(BuildConfig.releaseTime) < 18 * 60 * 60 * 1000)) {
                next();
                return;
            }
        }
        fetchSplashAD(this, container, skipView, APIID.ADAPP, APIID.loadingkey, this, 5000);

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
