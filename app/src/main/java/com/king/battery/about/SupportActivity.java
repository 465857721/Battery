package com.king.battery.about;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.king.battery.main.BaseActivity;
import com.king.battery.utils.APIID;
import com.king.battery.utils.Tools;
import com.king.batterytest.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SupportActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;


    private boolean ready = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        ButterKnife.bind(this);
        initViw();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViw() {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("支持");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @OnClick(R.id.likeview)
    public void onViewClicked() {
        if (ready) {

            Tools.toastInBottom(this, "感谢您的支持！");
        } else {
            Tools.toastInBottom(this, "广告拉取失败");
        }
    }


}
