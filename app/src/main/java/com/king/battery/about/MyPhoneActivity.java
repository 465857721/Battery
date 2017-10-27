package com.king.battery.about;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.king.batterytest.R;
import com.king.battery.main.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyPhoneActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_tem)
    TextView tvTem;
    @BindView(R.id.tv_dianya)
    TextView tvDianya;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_time_call)
    TextView tvTimeCall;
    @BindView(R.id.tv_time_3g)
    TextView tvTime3g;
    @BindView(R.id.tv_time_wifi)
    TextView tvTimeWifi;
    @BindView(R.id.tv_time_vadio)
    TextView tvTimeVadio;
    @BindView(R.id.tv_time_game)
    TextView tvTimeGame;
    @BindView(R.id.tv_time_music)
    TextView tvTimeMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphone);
        ButterKnife.bind(this);
        initViw();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViw() {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("我的手机");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvTem.setText(spu.getTem());
        tvDianya.setText(spu.getDianya());
        tvState.setText(spu.getBstate());
        int min = 10 * spu.getNum();
        tvTimeCall.setText((int)Math.floor(min / 60) + "小时" +
                min % 60 + "分钟");
        tvTime3g.setText((int) Math.floor(min * 0.6 / 60) + "小时"
                + (int) Math.floor(min * 0.6 / 60) % 60 + "分钟");
        tvTimeWifi.setText((int) Math.floor(min * 0.8 / 60) + "小时"
                + (int) Math.floor(min * 0.8 / 60) % 60 + "分钟");
        tvTimeVadio.setText((int) Math.floor(min * 0.4 / 60) + "小时"
                + (int) Math.floor(min * 0.4 / 60) % 60 + "分钟");
        tvTimeGame.setText((int) Math.floor(min * 0.3 / 60) + "小时"
                + (int) Math.floor(min * 0.3 / 60) % 60 + "分钟");
        tvTimeMusic.setText((int) Math.floor(min * 1.5 / 60) + "小时"
                + (int) Math.floor(min * 1.5 / 60) % 60 + "分钟");
    }
}
