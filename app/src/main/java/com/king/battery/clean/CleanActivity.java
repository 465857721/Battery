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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.king.battery.clean.adapter.TaskAdapter;
import com.king.battery.clean.bean.TaskInfo;
import com.king.battery.clean.event.CleanFinishEvent;
import com.king.battery.home.HomeActivity;
import com.king.battery.main.BaseActivity;
import com.king.battery.utils.APIID;
import com.king.batterytest.R;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.cfg.MultiProcessFlag;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CleanActivity extends BaseActivity implements Handler.Callback, NativeExpressAD.NativeExpressADListener {

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
    private BannerView bv;
    private static final String TAG = "AD_DEMO";

    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        MultiProcessFlag.setMultiProcess(true);

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
        initBanner();
        bv.loadAD();
        EventBus.getDefault().post(new CleanFinishEvent());
        cardNative.setVisibility(View.VISIBLE);
        refreshAd();


    }

    private void initBanner() {
        //yyb
        this.bv = new BannerView(this, ADSize.BANNER, APIID.ADAPP, APIID.banner);
        //baidu
//        this.bv = new BannerView(this, ADSize.BANNER, "1106156011", "3070323555242789");
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

//            @Override
//            public void onNoAD(int arg0) {
//
//            }

            @Override
            public void onNoAD(AdError adError) {
                Log.i("AD_DEMO", adError.getErrorMsg() + adError.getErrorCode());
            }

            @Override
            public void onADReceiv() {

            }
        });
        bannerContainer.addView(bv);
    }


    private void refreshAd() {
        if (nativeExpressAD == null) {
            int adWidth = 340;
            int adHeight = 300;
            com.qq.e.ads.nativ.ADSize adSize = new com.qq.e.ads.nativ.ADSize(adWidth, adHeight); // 不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
            nativeExpressAD = new NativeExpressAD(this, adSize, APIID.ADAPP, APIID.nativead, this);
        }
        nativeExpressAD.loadAD(1);
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i(
                TAG,
                String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
                        adError.getErrorMsg()));
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
    public void onRenderFail(NativeExpressADView adView) {
        Log.i(TAG, "onRenderFail");
    }

    @Override
    public void onRenderSuccess(NativeExpressADView adView) {
        Log.i(TAG, "onRenderSuccess");
    }

    @Override
    public void onADExposure(NativeExpressADView adView) {
        Log.i(TAG, "onADExposure");
    }

    @Override
    public void onADClicked(NativeExpressADView adView) {
        Log.i(TAG, "onADClicked");
    }

    @Override
    public void onADClosed(NativeExpressADView adView) {
        Log.i(TAG, "onADClosed");
        // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，不再可用。
        if (cardNative != null && cardNative.getChildCount() > 0) {
            cardNative.removeAllViews();
            cardNative.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView adView) {
        Log.i(TAG, "onADLeftApplication");
    }

    @Override
    public void onADOpenOverlay(NativeExpressADView adView) {
        Log.i(TAG, "onADOpenOverlay");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 使用完了每一个NativeExpressADView之后都要释放掉资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }
    }
}
