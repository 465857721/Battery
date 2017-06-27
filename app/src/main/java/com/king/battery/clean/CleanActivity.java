package com.king.battery.clean;


import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jaeger.library.StatusBarUtil;
import com.king.battery.clean.event.CleanFinishEvent;
import com.king.battery.home.HomeActivity;
import com.king.batterytest.R;
import com.king.battery.clean.adapter.TaskAdapter;
import com.king.battery.clean.bean.TaskInfo;
import com.king.battery.main.BaseActivity;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.animation.ObjectAnimator.ofFloat;


public class CleanActivity extends BaseActivity implements Handler.Callback {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rcl_task)
    RecyclerView rclTask;
    @Bind(R.id.ll_bg)
    LinearLayout llBg;
    @Bind(R.id.ll_progress)
    View googleProgress;
    @Bind(R.id.card_finish_top)
    CardView cardFinishTop;
    @Bind(R.id.card_finish_ad)
    CardView cardFinishAD;
    private List<TaskInfo> list;
    private Handler mHandler;
    private Context mContext;
    private Timer timer;
    private TaskAdapter adapter;
    private int flag = 0;// 1 为从通知进来

    ViewGroup bannerContainer;
    BannerView bv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        flag = getIntent().getIntExtra("flag",0);
        ButterKnife.bind(this);
        timer = new Timer();
        initViw();
        mContext = this;
        mHandler = new Handler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = TaskUtils.getTaskInfos(getApplicationContext());
                mHandler.sendEmptyMessage(0);
            }
        }).start();
        bannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);


    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.clean_bg_red));
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        super.onBackPressed();
        if(flag==1){
            Intent goHome = new Intent(mContext, HomeActivity.class);
            startActivity(goHome);
        }
    }

    private void initViw() {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("省电优化");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                adapter = new TaskAdapter(list, mContext);
                rclTask.setLayoutManager(new LinearLayoutManager(mContext));
                rclTask.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                clean();
                startRemoveAnim();
                break;
        }

        return true;
    }

    private void clean() {
        for (TaskInfo info : list) {
//                    Process.killProcess(info.getPid());
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(info.getPackageName());  //应用的包名
        }

    }

    private void startRemoveAnim() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (list.size() > 0) {
                            list.remove(0);
                            adapter.notifyItemRemoved(0);
                        } else {
                            timer.cancel();
                            StatusBarUtil.setColor(CleanActivity.this, ContextCompat.getColor(mContext, R.color.colorPrimary));
                            llBg.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));

                            onCleanFinish();
                        }

                    }
                });
            }
        }, 1000, 500);
    }

    private void onCleanFinish() {
        googleProgress.setVisibility(View.GONE);
        cardFinishTop.setVisibility(View.VISIBLE);
        cardFinishAD.setVisibility(View.VISIBLE);
        ObjectAnimator//
                .ofFloat(cardFinishTop, "rotationX", -90F, 0f)//
                .setDuration(1000)//
                .start();
        initBanner();
        bv.loadAD();
        EventBus.getDefault().post(new CleanFinishEvent());
    }
    private void initBanner() {
        this.bv = new BannerView(this, ADSize.BANNER, "1101189414", "4090829316242214");
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
        bannerContainer.addView(bv);
    }
}
