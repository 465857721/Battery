package com.king.battery.main;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.king.batterytest.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class EmptyActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ButterKnife.bind(this);
        initViw();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViw() {
        mToolbar.setNavigationIcon(R.drawable.icon_head_back);
        mToolbar.setTitle("关于");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
