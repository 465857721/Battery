package com.king.battery.speed;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.king.battery.home.LoadingActivity;
import com.king.batterytest.R;


/**
 * Created by mrrobot on 16/12/28.
 */

public class SpeedView extends FrameLayout {
    private Context mContext;
    public TextView downText;
    public TextView upText;
    private WindowManager windowManager;
    private int statusBarHeight;
    private float preX, preY, x, y, downX, downY;
    private final int clickdis = 30;

    public SpeedView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        statusBarHeight = WindowUtil.statusBarHeight;
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //a view inflate itself, that's funny
        inflate(mContext, R.layout.speed_layout, this);
        downText = (TextView) findViewById(R.id.speed_down);
        upText = (TextView) findViewById(R.id.speed_up);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getRawX();
                preY = event.getRawY() - statusBarHeight;
                downX = event.getRawX();
                downY = event.getRawY() - statusBarHeight;
                return true;
            case MotionEvent.ACTION_MOVE:
                x = event.getRawX();
                y = event.getRawY() - statusBarHeight;
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();
                params.x += x - preX;
                params.y += y - preY;
                windowManager.updateViewLayout(this, params);
                preX = x;
                preY = y;
                return true;
            case MotionEvent.ACTION_UP:
                if (Math.abs(downX - event.getRawX()) < clickdis &&
                        Math.abs(downY - event.getRawY()) < clickdis) {
                    Intent go = new Intent(mContext, LoadingActivity.class);
                    go.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    go.putExtra("type",1);
                    mContext.startActivity(go);
                }
                return true;
            default:
                break;

        }
        return super.onTouchEvent(event);
    }

}
