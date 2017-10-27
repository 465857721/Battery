package com.king.battery.setting;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.king.battery.main.BaseActivity;
import com.king.battery.service.BackService;
import com.king.battery.speed.SpeedActivity;
import com.king.battery.utils.Tools;
import com.king.batterytest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sw_notice)
    CheckBox swNotice;
    @BindView(R.id.sw_tem)
    CheckBox swNoticeTem;

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mContext = this;
        initViw();
        boolean c = Tools.isWorked(this, "com.king.battery.service.BackService");
        Log.d("zk", "a" + c);
        swNotice.setChecked(c);
        swNoticeTem.setChecked(spu.getIsNoticeTem());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViw() {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        swNotice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent i = new Intent(SettingActivity.this, BackService.class);
                if (isChecked) {
                    startService(i);
                } else {
                    stopService(i);
                }
            }
        });
        swNoticeTem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spu.setNoticTem(isChecked);

            }
        });
    }

    @OnClick({R.id.rl_speed, R.id.rl_shortcut, R.id.rl_theme})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_theme:
//                Intent themeIntent = new Intent(this, MainActivity.class);
//                startActivity(themeIntent);
                showPrintDialog();
                break;
            case R.id.rl_shortcut:
                Tools.createShortcut(this);
                break;
            case R.id.rl_speed:
                Intent speedIntent = new Intent(mContext, SpeedActivity.class);
                startActivity(speedIntent);
                break;
        }
    }

    private void showPrintDialog() {
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(
                R.layout.dialog_chose_theme, null);
        final Dialog tel_dialog = new AlertDialog.Builder(this).create();
        final Intent i = new Intent(this, BackService.class);
        final SharedPreferences.Editor editor = getSharedPreferences("myapp", 0).edit();
        layout.findViewById(R.id.ll_theme_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("index", 0);
                editor.commit();
                startService(i);
                tel_dialog.dismiss();
                Tools.toastInBottom(mContext, "开启成功，请在通知栏查看");
            }
        });
        layout.findViewById(R.id.ll_theme_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("index", 1);
                editor.commit();
                startService(i);
                tel_dialog.dismiss();
                Tools.toastInBottom(mContext, "开启成功，请在通知栏查看");
            }
        });
        layout.findViewById(R.id.ll_theme_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("index", 2);
                editor.commit();
                startService(i);
                tel_dialog.dismiss();
                Tools.toastInBottom(mContext, "开启成功，请在通知栏查看");
            }
        });
        layout.findViewById(R.id.ll_theme_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("index", 3);
                editor.commit();
                startService(i);
                Tools.toastInBottom(mContext, "开启成功，请在通知栏查看");
                tel_dialog.dismiss();
            }
        });

        tel_dialog.show();
        tel_dialog.getWindow().setContentView(layout);


    }
}
