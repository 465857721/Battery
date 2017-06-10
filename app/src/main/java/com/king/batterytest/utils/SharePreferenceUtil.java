package com.king.batterytest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private static final String NUM = "num";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String file) {
        sp = context.getApplicationContext().getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void setNum(int num) {
        editor.putInt(NUM, num);
        editor.commit();
    }

    public int getNum() {
        return sp.getInt(NUM, 100);
    }
}
