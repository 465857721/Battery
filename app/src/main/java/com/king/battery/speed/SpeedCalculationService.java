package com.king.battery.speed;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import static com.king.battery.speed.SpeedActivity.INIT_X;
import static com.king.battery.speed.SpeedActivity.INIT_Y;

/**
 * Created by mrrobot on 16/12/28.
 */

public class SpeedCalculationService extends Service {
    private WindowUtil windowUtil;
    private boolean changed=false;

    @Override
    public void onCreate() {
        super.onCreate();
        WindowUtil.initX= (int) SharedPreferencesUtils.getFromSpfs(this,INIT_X,0);
        WindowUtil.initY= (int) SharedPreferencesUtils.getFromSpfs(this,INIT_Y,0);
        windowUtil=new WindowUtil(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        changed=intent.getBooleanExtra(SpeedActivity.CHANGED,false);
        if(changed){
            windowUtil.onSettingChanged();
        }else{
            if(!windowUtil.isShowing()){
                windowUtil.showSpeedView();
            }
            SharedPreferencesUtils.putToSpfs(this, SpeedActivity.IS_SHOWN,true);
        }
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WindowManager.LayoutParams params= (WindowManager.LayoutParams) windowUtil.speedView.getLayoutParams();
        SharedPreferencesUtils.putToSpfs(this, INIT_X,params.x);
        SharedPreferencesUtils.putToSpfs(this, INIT_Y,params.y);
        if(windowUtil.isShowing()){
            windowUtil.closeSpeedView();
            SharedPreferencesUtils.putToSpfs(this, SpeedActivity.IS_SHOWN,false);
        }
        Log.d("yjw","service destroy");
    }
}
