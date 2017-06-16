package com.king.battery.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.jaeger.library.StatusBarUtil;
import com.king.battery.about.AboutActivity;
import com.king.batterytest.R;
import com.king.battery.about.MyPhoneActivity;
import com.king.battery.charge.ChargeActivity;
import com.king.battery.clean.CleanActivity;
import com.king.battery.clean.TaskUtils;
import com.king.battery.clean.bean.TaskInfo;
import com.king.battery.clean.event.CleanFinishEvent;
import com.king.battery.main.BaseActivity;
import com.king.battery.main.event.BatteryInfoEvent;
import com.king.battery.service.BackService;
import com.king.battery.setting.SettingActivity;
import com.king.battery.utils.Tools;
import com.king.battery.utils.WaveHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, Handler.Callback {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout2)
    DrawerLayout drawer;
    @Bind(R.id.tv_leave)
    TextView tvLeave;
    @Bind(R.id.iv_clean_woring)
    ImageView ivCleanWoring;
    @Bind(R.id.tv_clean_state)
    TextView tvCleanState;
    @Bind(R.id.tv_goclean)
    TextView tvGoclean;

    private WaveHelper mWaveHelper;
    private WaveView waveView;
    private int mBorderColor = Color.parseColor("#44FFFFFF");
    private int mBorderWidth = 30;
    private Context mContext;
    private List<TaskInfo> list = new ArrayList<>();
    private Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mContext = this;
        EventBus.getDefault().register(this);
        startService();

        intiView();
        mWaveHelper.start(spu.getNum() / 100);
        mHandler = new Handler(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = TaskUtils.getTaskInfos(getApplicationContext());
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_comment) {
            Tools.goMarket(this);
        } else if (id == R.id.nav_send) {
            joinQQGroup("KfTI5YEmo0GSQmuBQyK8DW32P5BE6COB");
        } else if (id == R.id.nav_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (id == R.id.nav_setting) {
            Intent setIntent = new Intent(this, SettingActivity.class);
            startActivity(setIntent);
        }
//        closeDrawerLayout();
        return true;
    }


    private void intiView() {
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        StatusBarUtil.setColorForDrawerLayout(this, drawer, getResources().getColor(R.color.colorPrimaryDark));

        navigationView.setNavigationItemSelectedListener(this);

        TextView tvPhoneName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_phone_name);
        tvPhoneName.setText(Build.MODEL);

        //电量
        waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);

        mWaveHelper = new WaveHelper(waveView);
        waveView.setShapeType(WaveView.ShapeType.CIRCLE);

        // nva 头部点击
        navigationView.getHeaderView(0).findViewById(R.id.ll_nav_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goPhone = new Intent(mContext, MyPhoneActivity.class);
                startActivity(goPhone);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BatteryInfoEvent event) {
        mWaveHelper.setLevelRatio(event.getNum() / 100f);
        tvLeave.setText(event.getNum() + "%");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.ll_charge, R.id.tv_goclean})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_charge:
                Intent goCharge = new Intent(mContext, ChargeActivity.class);
                startActivity(goCharge);
                break;
            case R.id.tv_goclean:
                Intent goClean = new Intent(mContext, CleanActivity.class);
                startActivity(goClean);
                break;

        }
    }

    private void startService() {
//        editor.putInt("open", 0);// 0为开启
//        editor.commit();

        Intent i = new Intent(this, BackService.class);
        startService(i);
    }

    private void closeDrawerLayout() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                if (list.size() > 5) {
                    initWordingView();
                } else {
                    initNormalView();
                }
                break;
        }

        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CleanFinishEvent event) {
        initNormalView();
    }

    private void initWordingView() {
        ivCleanWoring.setVisibility(View.VISIBLE);
        tvCleanState.setText(getString(R.string.tips_background_woring));
        tvGoclean.setText(getString(R.string.tips_go_clean));
    }

    private void initNormalView() {
        ivCleanWoring.setVisibility(View.GONE);
        tvCleanState.setText(getString(R.string.tips_background_normal));
        tvGoclean.setText(getString(R.string.tips_go_check));
    }

    /****************
     *
     * 发起添加群流程。群号：电量显示反馈群(299046020) 的 key 为： KfTI5YEmo0GSQmuBQyK8DW32P5BE6COB
     * 调用 joinQQGroup(KfTI5YEmo0GSQmuBQyK8DW32P5BE6COB) 即可发起手Q客户端申请加群 电量显示反馈群(299046020)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Tools.toastInBottom(this,"未安装手Q或安装的版本不支持");
            return false;
        }
    }


}
