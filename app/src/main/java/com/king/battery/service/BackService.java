package com.king.battery.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.king.battery.clean.CleanActivity;
import com.king.battery.clean.CoolActivity;
import com.king.battery.home.LoadingActivity;
import com.king.battery.main.event.BatteryInfoEvent;
import com.king.battery.utils.SharePreferenceUtil;
import com.king.battery.utils.Tools;
import com.king.batterytest.R;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;


public class BackService extends Service {
    private Notification baseNF;
    private SharePreferenceUtil spu;
    private int level, scale, BatteryV, BatteryT;
    private Context mContext;
    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        spu = Tools.getSpu(getApplicationContext());
        // 定义电池电量更新广播的过滤器,只接受带有ACTION_BATTERRY_CHANGED事件的Intent
        IntentFilter batteryChangedReceiverFilter = new IntentFilter();
        batteryChangedReceiverFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        // 向系统注册batteryChangedReceiver接收器，本接收器的实现见代码字段处
        registerReceiver(batteryChangedReceiver, batteryChangedReceiverFilter);
        // 实例化Notification通知的管理器，即字段notification manager
        baseNF = new Notification();
        // 由于初始化本服务时系统可能没有发出ACTION_BATTERY_CHANGED广播，那么刚才注册的那个接收器将不会在本服务启动时被激活，这种情况下就无法显示当前电量，因此在这里添加一个匿名广播接收器。
        new BroadcastReceiver() {
            @Override
            public void onReceive(Context mContext, Intent intent) {
                startNoti(mContext, intent);
            }
        };

        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
//        使用startForeground()之后，给出的Notification对象会发布，
//        使用stopForeground()之后，通知会被撤销，当Service销毁（比如stopService()被调用）之后，
//        通知也会被撤销。stopForeground()仅仅只是去掉service的foreground属性，并不会让service停止。
//        stopForeground(true);
    }

    // 接受电池信息更新的广播
    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        public void onReceive(Context mContext, Intent intent) {
            startNoti(mContext, intent);
        }
    };

    private void startNoti(Context context, Intent intent) {
        mContext = context;

        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        level = intent.getIntExtra("level", 0);
        scale = intent.getIntExtra("scale", 100);
//        int status = intent.getIntExtra("status", 0);
        BatteryV = intent.getIntExtra("voltage", 0);  //电池电压
        BatteryT = intent.getIntExtra("temperature", 0);  //电池温度


        Intent pintent = new Intent(mContext, LoadingActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        mContext, 1, pintent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.iconnew))
                .setContentIntent(pendingIntent) //设置通知栏点击意图
//                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//                .setAutoCancel(false)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(true);//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
        int num = level * 100 / scale;


        Log.d("zk", "battery num = " + num);
        SharedPreferences prefs = getSharedPreferences("myapp", 0);// 默认值为0
        // ，0为关闭状态~
        int flags = prefs.getInt("index", 0);
        String theme;
        String name = "";
        switch (flags) {
            case 0:
                theme = "1";
                name = "battery_" + theme + "_bg_" + num;
                break;
            case 1:
                theme = "2";
                name = "battery_" + theme + "_bg_" + num;
                break;
            case 2:
                name = "battery_" + num;
                break;
            case 3:
                name = "battery_" + "green_" + num;
                break;
        }
//        mBuilder.setContentTitle("电量显示");//设置通知栏标题
        String BatteryStatus = "正常使用";
        Log.d("zk action = ", intent.getAction().toString());
        switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                BatteryStatus = "充电状态";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                BatteryStatus = "放电状态";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                BatteryStatus = "未充电";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                BatteryStatus = "充满电";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                BatteryStatus = "未知道状态";
                break;
        }
        EventBus.getDefault().post(new BatteryInfoEvent(num, BatteryV, BatteryT, BatteryStatus));

        int id = getResources().getIdentifier(name, "drawable", getPackageName());
        mBuilder.setSmallIcon(id);
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.rv_notif_allstate);

        Intent goClean = new Intent(mContext, CleanActivity.class);
        goClean.putExtra("flag", 1);
        PendingIntent goCleanpendingIntent =
                PendingIntent.getActivity(
                        mContext, 1, goClean, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.ll_goclean, goCleanpendingIntent);
        mBuilder.setContent(rv);

//        DecimalFormat df = new DecimalFormat(".##");
        DecimalFormat df = new DecimalFormat("######0.0");
        rv.setImageViewResource(R.id.iv_icon, id);
        rv.setTextViewText(R.id.tv_num, "电量: " + num + " %");
        rv.setTextViewText(R.id.tv_tem, "温度: " + df.format(BatteryT * 0.1) + " ℃");
        rv.setTextViewText(R.id.tv_dianya, "电压: " + df.format(BatteryV * 0.001) + " V");
        rv.setTextViewText(R.id.tv_state, "状态: " + BatteryStatus);
        spu.setNum(num);
        spu.setTem(df.format(BatteryT * 0.1) + " ℃");
        spu.setDianya(df.format(BatteryV * 0.001) + " V");
        spu.setBstate(BatteryStatus);
        startForeground(1989, mBuilder.build());
        noticeTem();
    }

    private void noticeTem() {
        if (!spu.getIsNoticeTem() || BatteryT < 400)
            return;
        Intent pintent = new Intent(mContext, LoadingActivity.class);
        //  两次间隔30分钟以上
        Log.d("zktem", "System = " + System.currentTimeMillis());
        Log.d("zktem", "TemLong = " + spu.getTemLongTime());
        Log.d("zktem", "---- = " + (System.currentTimeMillis() - spu.getTemLongTime()));
        if (System.currentTimeMillis() - spu.getTemLongTime() < 30 * 60 * 1000)
            return;
        spu.setTemLongTime(System.currentTimeMillis());

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        mContext, 1, pintent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.iconnew))
                .setContentIntent(pendingIntent) //设置通知栏点击意图
//                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setSmallIcon(R.drawable.woring).setOngoing(true);
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.rv_notif_hightem);
        DecimalFormat df = new DecimalFormat("######0.0");
        rv.setTextViewText(R.id.tv_tem, "电池温度已达到:" + df.format(BatteryT * 0.1) + " ℃");

        Intent goCool = new Intent(mContext, CoolActivity.class);
        goCool.putExtra("flag", 1);
        PendingIntent goCoolIntent =
                PendingIntent.getActivity(
                        mContext, 1, goCool, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.ll_goclean, goCoolIntent);


        mBuilder.setContent(rv);
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_ONLY_ALERT_ONCE;

        mNotificationManager.notify(1990, notify);

    }

}