package com.king.battery.charge;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.battery.main.BaseActivity;
import com.king.battery.main.event.BatteryInfoEvent;
import com.king.battery.utils.Tools;
import com.king.batterytest.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChargeActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_charge)
    TextView tvCharge;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.iv_1)
    ImageView iv1;
    @BindView(R.id.tv_state1)
    TextView tvState1;
    @BindView(R.id.iv_2)
    ImageView iv2;
    @BindView(R.id.tv_state2)
    TextView tvState2;
    @BindView(R.id.iv_3)
    ImageView iv3;
    @BindView(R.id.tv_state3)
    TextView tvState3;
    @BindView(R.id.iv_4)
    ImageView iv4;
    @BindView(R.id.tv_state4)
    TextView tvState4;

    private ObjectAnimator scaleYAnimB;
    private ViewWrapper bt;
    private AnimatorSet set1;
    private AnimatorSet set2;
    private AnimatorSet set3;
    private AnimatorSet set4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_charge);
        ButterKnife.bind(this);
        initAimi();
        initViw();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        tvNum.setText(spu.getNum() + "");

        bt = new ViewWrapper(tvCharge);

        scaleYAnimB = ObjectAnimator.ofInt(
                bt, "width", 0, Tools.dip2px(this, 230 - 6 - 10));
        scaleYAnimB.setDuration(5000);
        scaleYAnimB.setRepeatCount(-1);
        scaleYAnimB.setRepeatMode(ValueAnimator.RESTART);

//        if (spu.getBstate().equals("充电状态")) {
//            scaleYAnimB.start();
//        } else {
//            bt.setWidth(Tools.dip2px(this, 230 - 6 - 10) * (spu.getNum() / 100));
//        }

        initChargeView(spu.getNum(), spu.getBstate());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BatteryInfoEvent event) {
        tvNum.setText(event.getNum() + "");
        initChargeView(event.getNum(), event.getState());
    }

    private void initChargeView(int num, String state) {
        tvNum.setText(num + "");
        if (state.equals("充电状态")) {
            scaleYAnimB.start();
        } else {
            if (scaleYAnimB.isStarted())
                scaleYAnimB.end();
            bt.setWidth(Tools.dip2px(this, 230 - 6 - 10) * (spu.getNum() / 100));
        }

        if (state.equals("充电状态")) {
            // 快充
            //cancel动画立即停止，停在当前的位置；end动画直接到最终状态。
            if (num <= 80) {
                if (!set1.isRunning())
                    set1.start();
                set2.cancel();
                set3.cancel();
                set4.cancel();

                tvState1.setText("进行中");
                tvState2.setText("等待中");
                tvState3.setText("等待中");
                tvState4.  setText("等待中");
                tvState1.setTextColor(ContextCompat.getColor(this, R.color.red));
                tvState2.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvState3.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvState4.setTextColor(ContextCompat.getColor(this, R.color.white));

            } else if (num > 80 && num <= 95) {//连续
                set1.end();
                if (!set2.isRunning())
                    set2.start();
                set3.cancel();
                set4.cancel();
                tvState1.setText("已完成");
                tvState2.setText("进行中");
                tvState3.setText("等待中");
                tvState4.setText("等待中");
                tvState1.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvState2.setTextColor(ContextCompat.getColor(this, R.color.red));
                tvState3.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvState4.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else if (num > 95 && num <= 100) {//涓流
                set1.cancel();
                set2.cancel();
                if (!set3.isRunning())
                    set3.start();
                set4.cancel();
                tvState1.setText("已完成");
                tvState2.setText("已完成");
                tvState3.setText("进行中");
                tvState4.setText("等待中");
                tvState1.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvState2.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvState3.setTextColor(ContextCompat.getColor(this, R.color.red));
                tvState4.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
        } else if (state.equals("充满电")) {
            tvState1.setText("已完成");
            tvState2.setText("已完成");
            tvState3.setText("已完成");
            tvState4.setText("已完成");
            tvState1.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvState2.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvState3.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvState4.setTextColor(ContextCompat.getColor(this, R.color.white));
            scaleYAnimB.end();
            bt.setWidth(Tools.dip2px(this, 230 - 6 - 10) * (spu.getNum() / 100));
        } else {
            tvState1.setText("等待中");
            tvState2.setText("等待中");
            tvState3.setText("等待中");
            tvState4.setText("等待中");
            tvState1.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvState2.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvState3.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvState4.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void initAimi() {
        ObjectAnimator scaleXAnim1 = ObjectAnimator.ofFloat(
                iv1, "scaleX", 0.5f, 1f);
        scaleXAnim1.setRepeatCount(-1);
        scaleXAnim1.setRepeatMode(ValueAnimator.REVERSE);

        ObjectAnimator scaleYAnim1 = ObjectAnimator.ofFloat(
                iv1, "scaleY", 0.5f, 1f);
        scaleYAnim1.setRepeatCount(-1);
        scaleYAnim1.setRepeatMode(ValueAnimator.REVERSE);
        set1 = new AnimatorSet();

        set1.playTogether(scaleXAnim1, scaleYAnim1);
        set1.setDuration(1000);


        ObjectAnimator scaleXAnim2 = ObjectAnimator.ofFloat(
                iv2, "scaleX", 0.5f, 1f);
        scaleXAnim2.setRepeatCount(-1);
        scaleXAnim2.setRepeatMode(ValueAnimator.REVERSE);

        ObjectAnimator scaleYAnim2 = ObjectAnimator.ofFloat(
                iv2, "scaleY", 0.5f, 1f);
        scaleYAnim2.setRepeatCount(-1);
        scaleYAnim2.setRepeatMode(ValueAnimator.REVERSE);
        set2 = new AnimatorSet();

        set2.playTogether(scaleXAnim2, scaleYAnim2);
        set2.setDuration(1000);

        ObjectAnimator scaleXAnim3 = ObjectAnimator.ofFloat(
                iv3, "scaleX", 0.5f, 1f);
        scaleXAnim3.setRepeatCount(-1);
        scaleXAnim3.setRepeatMode(ValueAnimator.REVERSE);

        ObjectAnimator scaleYAnim3 = ObjectAnimator.ofFloat(
                iv3, "scaleY", 0.5f, 1f);
        scaleYAnim3.setRepeatCount(-1);
        scaleYAnim3.setRepeatMode(ValueAnimator.REVERSE);
        set3 = new AnimatorSet();

        set3.playTogether(scaleXAnim3, scaleYAnim3);
        set3.setDuration(1000);

        ObjectAnimator scaleXAnim4 = ObjectAnimator.ofFloat(
                iv4, "scaleX", 0.5f, 1f);
        scaleXAnim4.setRepeatCount(-1);
        scaleXAnim4.setRepeatMode(ValueAnimator.REVERSE);

        ObjectAnimator scaleYAnim4 = ObjectAnimator.ofFloat(
                iv4, "scaleY", 0.5f, 1f);
        scaleYAnim4.setRepeatCount(-1);
        scaleYAnim4.setRepeatMode(ValueAnimator.REVERSE);
        set4 = new AnimatorSet();

        set4.playTogether(scaleXAnim4, scaleYAnim4);
        set4.setDuration(1000);
    }
}
