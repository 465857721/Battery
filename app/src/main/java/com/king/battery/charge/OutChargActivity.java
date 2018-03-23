package com.king.battery.charge;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.king.battery.main.BaseActivity;
import com.king.batterytest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.itangqi.waveloadingview.WaveLoadingView;


public class OutChargActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_leave)
    TextView tvLeave;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outcharge);
        ButterKnife.bind(this);
        initViw();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        waveLoadingView.setProgressValue(40);

    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.black));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mWaveHelper.cancel();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(BatteryInfoEvent event) {
//        mWaveHelper.setLevelRatio(event.getNum() / 100f);
//        tvLeave.setText(event.getNum() + "%");
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
