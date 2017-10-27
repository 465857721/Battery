package com.king.battery.about;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.king.battery.main.BaseActivity;
import com.king.battery.utils.APIID;
import com.king.battery.utils.Tools;
import com.king.batterytest.R;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.comm.util.AdError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SupportActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private InterstitialAD iad;
    private boolean ready = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        ButterKnife.bind(this);
        initViw();
        //yyb
        iad = new InterstitialAD(this, APIID.ADAPP, APIID.chaping);
        //baidu
//        iad = new InterstitialAD(this, "1106156011", "1010022515748814");
        iad.setADListener(new AbstractInterstitialADListener() {

//            @Override
//            public void onNoAD(int arg0) {
//                Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
//            }

            @Override
            public void onADReceive() {
                Log.i("AD_DEMO", "onADReceive");
                ready = true;
            }

            @Override
            public void onNoAD(AdError adError) {
                Log.i("AD_DEMO", adError.getErrorMsg() + adError.getErrorCode());
            }
        });
        iad.loadAD();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViw() {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("支持");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @OnClick(R.id.likeview)
    public void onViewClicked() {
        if (ready) {
            iad.show();
            Tools.toastInBottom(this, "感谢您的支持！");
        } else {
            Tools.toastInBottom(this, "广告拉取失败");
        }
    }


}
