package com.king.battery.speed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.king.battery.main.BaseActivity;
import com.king.battery.utils.APIID;
import com.king.batterytest.R;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

import java.lang.reflect.Field;


public class SpeedActivity extends BaseActivity {
    private int REQUEST_CODE = 0;
    private Button showBt;
    private Button closeBt;
    private RadioButton radioBt1;
    private RadioButton radioBt2;
    private RadioButton radioBt3;
    private Toolbar mToolbar;
    public static final String SETTING = "setting";
    public static final String BOTH = "both";
    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String CHANGED = "changed";
    public static final String INIT_X = "init_x";
    public static final String INIT_Y = "init_y";
    public static final String IS_SHOWN = "is_shown";
    private ViewGroup bannerContainer;
    private BannerView bv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        //检查悬浮窗权限
//        if (checkDrawOverlayPermission()) {
            init();
//        }
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("网速悬浮窗");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showBt = (Button) findViewById(R.id.bt_show);
        closeBt = (Button) findViewById(R.id.bt_close);
        radioBt1 = (RadioButton) findViewById(R.id.radio_1);
        radioBt2 = (RadioButton) findViewById(R.id.radio_2);
        radioBt3 = (RadioButton) findViewById(R.id.radio_3);
        showBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(SpeedActivity.this, SpeedCalculationService.class));
            }
        });
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(SpeedActivity.this, SpeedCalculationService.class));
            }
        });
        WindowUtil.statusBarHeight = getStatusBarHeight();
        String setting = (String) SharedPreferencesUtils.getFromSpfs(this, SETTING, BOTH);
        if (setting.equals(BOTH)) {
            radioBt1.setChecked(true);
        } else if (setting.equals(UP)) {
            radioBt2.setChecked(true);
        } else {
            radioBt3.setChecked(true);
        }
        findViewById(R.id.bt_gosetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                applyCommonPermission(SpeedActivity.this);


            }
        });

        bannerContainer = (ViewGroup) this.findViewById(R.id.bv);
        initBanner();
        bv.loadAD();
    }

    private void applyCommonPermission(Context context) {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M)) {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));

            startActivity(localIntent);
        } else {
            try {
                Class clazz = Settings.class;
                Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
                Intent intent = new Intent(field.get(null).toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {

                Intent localIntent = new Intent();
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));

                startActivity(localIntent);
            }
        }

    }

    private void initBanner() {
        //yyb
        // this.bv = new BannerView(this, ADSize.BANNER, "1101189414", "5040624571474334");
        //baidu
        this.bv = new BannerView(this, ADSize.BANNER, APIID.ADAPP, APIID.banner);

        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

//            @Override
//            public void onNoAD(int arg0) {
//                Log.d("zk","onNoAD");
//            }

            @Override
            public void onNoAD(AdError adError) {

            }

            @Override
            public void onADReceiv() {
                Log.d("zk", "onADReceiv");
            }
        });
        bannerContainer.addView(bv);
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    init();
                } else {
                    Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        return statusBarHeight;
    }

    public void onRadioButtonClick(View view) {
        switch (view.getId()) {
            case R.id.radio_1:
                radioBt1.setChecked(true);
                SharedPreferencesUtils.putToSpfs(this, SETTING, BOTH);
                break;
            case R.id.radio_2:
                radioBt2.setChecked(true);
                SharedPreferencesUtils.putToSpfs(this, SETTING, UP);
                break;
            case R.id.radio_3:
                radioBt3.setChecked(true);
                SharedPreferencesUtils.putToSpfs(this, SETTING, DOWN);
                break;
            default:
                break;
        }
        startService(new Intent(this, SpeedCalculationService.class)
                .putExtra(CHANGED, true));
    }

}
