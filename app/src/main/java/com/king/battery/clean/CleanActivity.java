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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.king.battery.clean.adapter.TaskAdapter;
import com.king.battery.clean.bean.TaskInfo;
import com.king.battery.clean.event.CleanFinishEvent;
import com.king.battery.home.HomeActivity;
import com.king.battery.main.BaseActivity;
import com.king.batterytest.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;


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
    @Bind(R.id.card_native)
    CardView cardNative;
    @Bind(R.id.tv_headtips)
    TextView tvHeadtips;
    @Bind(R.id.tv_tips)
    TextView tvTips;

    private List<TaskInfo> list;
    private Handler mHandler;
    private Context mContext;
    private Timer timer;
    private TaskAdapter adapter;
    private int flag = 0;// 1 为从通知进来

    private ViewGroup bannerContainer;

    private static final String TAG = "AD_DEMO";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);


        flag = getIntent().getIntExtra("flag", 0);
        ButterKnife.bind(this);
        timer = new Timer();
        initViw(getIntent().getIntExtra("type", 0));
        mContext = this;
        mHandler = new Handler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = TaskUtils.getTaskInfos(getApplicationContext());
                mHandler.sendEmptyMessage(0);
            }
        }).start();
        bannerContainer = (ViewGroup) this.findViewById(R.id.bv);


    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.clean_bg_red));
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        super.onBackPressed();
        if (flag == 1) {
            Intent goHome = new Intent(mContext, HomeActivity.class);
            startActivity(goHome);
        }
    }

    /**
     * type 0  清理  1 降温
     *
     * @param type
     */

    private void initViw(int type) {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        if (type == 0) {
            mToolbar.setTitle("省电优化");

        } else {
            mToolbar.setTitle("快速降温");
            tvTips.setText("正在关闭高耗能进程，请稍等...");
            tvHeadtips.setText("高耗能进程已关闭，温度正在下降");
        }
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
        EventBus.getDefault().post(new CleanFinishEvent());
        cardNative.setVisibility(View.VISIBLE);


    }


}
