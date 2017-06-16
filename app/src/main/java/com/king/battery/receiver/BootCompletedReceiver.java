package com.king.battery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.king.battery.service.BackService;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences prefs = context.getSharedPreferences("myapp", 0);// 默认值为0
		// ，0为关闭状态~
		int cki = prefs.getInt("ck", 0);
		int open = prefs.getInt("open", 1);
		if (cki == 0 && open == 0) {
			Intent i = new Intent(context, BackService.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("kaiji", 0);
			context.startService(i);
		}
		Log.d("zk onrecive ", "Action = " + intent.getAction());
	}

}
