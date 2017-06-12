package com.king.batterytest.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.jaeger.library.StatusBarUtil;
import com.king.batterytest.R;
import com.king.batterytest.about.MyPhoneActivity;
import com.king.batterytest.main.BaseActivity;
import com.king.batterytest.main.event.BatteryInfoEvent;
import com.king.batterytest.service.BackService;
import com.king.batterytest.utils.WaveHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout2)
    DrawerLayout drawer;
    @Bind(R.id.tv_leave)
    TextView tvLeave;

    private WaveHelper mWaveHelper;
    private WaveView waveView;
    private int mBorderColor = Color.parseColor("#44FFFFFF");
    private int mBorderWidth = 10;
    private Context mContext;

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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout2);
        drawer.closeDrawer(GravityCompat.START);
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

    @OnClick({R.id.ll_charge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_charge:
                mWaveHelper.setLevelRatio(0.9f);
                break;

        }
    }

    private void startService() {
//        editor.putInt("open", 0);// 0为开启
//        editor.commit();

        Intent i = new Intent(this, BackService.class);
        startService(i);
    }
}
