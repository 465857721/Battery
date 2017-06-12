package com.king.batterytest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
    private static final String NUM = "num";
    private static final String TEM = "tem";
    private static final String DIANYA = "dianya";
    private static final String BSTATE = "bstate";
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

    public void setTem(String tem) {
        editor.putString(TEM, tem);
        editor.commit();
    }

    public String getTem() {
        return sp.getString(TEM, "");
    }

    public void setDianya(String dianya) {
        editor.putString(DIANYA, dianya);
        editor.commit();
    }

    public String getDianya() {
        return sp.getString(DIANYA, "");
    }

    public void setBstate(String state) {
        editor.putString(BSTATE, state);
        editor.commit();
    }

    public String getBstate() {
        return sp.getString(BSTATE, "");
    }
}
