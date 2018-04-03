package com.king.battery.charge;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jaeger.library.StatusBarUtil;
import com.king.battery.main.BaseActivity;
import com.king.battery.main.event.BatteryInfoEvent;
import com.king.battery.setting.SettingActivity;
import com.king.battery.utils.APIID;
import com.king.batterytest.R;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.itangqi.waveloadingview.WaveLoadingView;


public class OutChargActivity extends BaseActivity implements NativeExpressAD.NativeExpressADListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_leave)
    TextView tvLeave;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;
    private static final String TAG = "AD_DEMO";
    @BindView(R.id.card_native)
    CardView cardNative;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_outcharge);
        ButterKnife.bind(this);
        initViw();
        refreshAd();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_outcharge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_outsetting:
                Intent go = new Intent(this, SettingActivity.class);
                startActivity(go);
                break;
        }
        return true;
    }

    private void refreshAd() {
        if (nativeExpressAD == null) {
            nativeExpressAD = new NativeExpressAD(this, new com.qq.e.ads.nativ.ADSize(com.qq.e.ads.nativ.ADSize.FULL_WIDTH,
                    com.qq.e.ads.nativ.ADSize.AUTO_HEIGHT), APIID.ADAPP, APIID.nativeadout, this); // 传入Activity

        }
        nativeExpressAD.loadAD(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this)
                .content("确定要退出充电保护吗？（一小时内将不再保护）")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {
                        // TODO
                        finish();
                    }
                })
                .show();
    }

    private void initViw() {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("健康充电");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        waveLoadingView.setProgressValue(spu.getNum());
        waveLoadingView.setCenterTitle(spu.getNum() + "%");
        initLeave(spu.getNum());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BatteryInfoEvent event) {
        waveLoadingView.setProgressValue(event.getNum());
        waveLoadingView.setCenterTitle(event.getNum() + "%");
        tvLeave.setText(event.getState());
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.black));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initLeave(int num) {
        if (num <= 80) {
            tvLeave.setText("快速充电中");
        } else if (num < 95) {
            tvLeave.setText("持续充电中");
        } else {
            tvLeave.setText("涓流充电中");
        }
    }

    @Override
    public void onNoAD(AdError adError) {

    }


    @Override
    public void onADLoaded(List<NativeExpressADView> adList) {
        Log.i(TAG, "onADLoaded: " + adList.size());
        // 释放前一个NativeExpressADView的资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }

        if (cardNative.getVisibility() != View.VISIBLE) {
            cardNative.setVisibility(View.VISIBLE);
        }

        if (cardNative.getChildCount() > 0) {
            cardNative.removeAllViews();
        }

        nativeExpressADView = adList.get(0);
        // 保证View被绘制的时候是可见的，否则将无法产生曝光和收益。
        cardNative.addView(nativeExpressADView);
        nativeExpressADView.render();
    }

    @Override
    public void onRenderFail(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADExposure(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClicked(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADClosed(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

    }
}
