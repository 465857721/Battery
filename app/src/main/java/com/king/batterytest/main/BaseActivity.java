package com.king.batterytest.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.FitWindowsViewGroup;

import com.jaeger.library.StatusBarUtil;
import com.king.batterytest.R;
import com.king.batterytest.utils.SharePreferenceUtil;
import com.king.batterytest.utils.Tools;
import com.umeng.analytics.MobclickAgent;


public class BaseActivity extends AppCompatActivity {
    public SharePreferenceUtil spu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spu = Tools.getSpu(this);

//        // 需要设置 状态栏 颜色为 非白色否则 其他主题在 5.0以上无法使用。tagetsdk 必须在20以下，只能通过这这种方式来设置状态栏颜色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            ViewGroup firstChildAtDecorView = ((ViewGroup) ((ViewGroup) getWindow().getDecorView()).getChildAt(0));
//            View statusView = new View(this);
//            ViewGroup.LayoutParams statusViewLp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    Tools.getStatusBarHeight(this));
//            //颜色的设置可抽取出来让子类实现之
//            statusView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
//            firstChildAtDecorView.addView(statusView, 0, statusViewLp);
//        }


        setStatusBar();
    }

    protected void setStatusBar() {


        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }


}
