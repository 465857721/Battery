package com.king.battery.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.king.battery.clean.CleanActivity;
import com.king.batterytest.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by alvis on 2017/4/12.
 */

public class Tools {
    private static Toast toast;
    private static Dialog loadingDialog;

    public static SharePreferenceUtil getSpu(Context mContext) {
        return new SharePreferenceUtil(mContext.getApplicationContext(), "battery");
    }

    public static int getStatusBarHeight(Context mContext) {
        int resId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            return mContext.getResources().getDimensionPixelSize(resId);
        }
        return 0;
    }

    // 底部弹出Toast
    public static void toastInBottom(Context context, String msg) {
        if (context != null) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 300);
            toast.show();
        }
    }
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    public static void hideSoftInput(Activity mActivity) {
        ((InputMethodManager) mActivity.getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static boolean checkNetwork(Context context) {
        if (context == null)
            return false;

        ConnectivityManager conMan = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState();

        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState();
        if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
            return true;

        if (wifi == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
            return true;

        // Out.toast(context, R.string.network_not_connected);
        return false;
    }

    public static void hideWaitDialog() {
        if (loadingDialog != null)
            loadingDialog.dismiss();
    }


    /* * 设置状态栏字体图标为深色，需要MIUIV6以上
 * @param window 需要设置的窗口
 * @param dark 是否把状态栏字体及图标颜色设置为深色
 * @return boolean 成功执行返回true
 * */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);  //状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);   //清除黑色字体
                }
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {

        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {

        }

        return verName;
    }

    /**
     * 去市场更新
     *
     * @param mContext
     */
    public static void goMarket(Context mContext) {
        try {

            Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());// id为包名
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(it);

        } catch (Exception e) {
            toastInBottom(mContext, "请打开应用市场，搜索 " + mContext.getString(R.string.app_name));
            e.printStackTrace();
        }
    }

    public static boolean isAppInstalled(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenW(Activity mActivity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

        return mDisplayMetrics.widthPixels;
    }


    /**
     * 格式化数据
     *
     * @param data
     * @return
     */
    public static String formatByte(long data) {
        DecimalFormat format = new DecimalFormat("##.##");
        if (data < 1024) {
            return data + "bytes";
        } else if (data < 1024 * 1024) {
            return format.format(data / 1024f) + "KB";
        } else if (data < 1024 * 1024 * 1024) {
            return format.format(data / 1024f / 1024f) + "MB";
        } else if (data < 1024 * 1024 * 1024 * 1024) {
            return format.format(data / 1024f / 1024f / 1024f) + "GB";
        } else {
            return "超出统计范围";
        }
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isWorked(Context context, String className) {
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(1000);
        for (int i = 0; i < runningService.size(); i++) {
//            Log.d("zk",runningService.get(i).service.getClassName().toString());
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static void createShortcut(Context context) {
        String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
        // 快捷方式要启动的包
        Intent intent = new Intent(context, CleanActivity.class);

        // 设置快捷方式的参数
        Intent shortcutIntent = new Intent(ACTION_INSTALL_SHORTCUT);
        // 设置名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键省电"); // 设置启动 Intent
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        // 设置图标
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context,
                        R.drawable.short_cut_superpower));
        // 只创建一次快捷方式
        shortcutIntent.putExtra("duplicate", false);
        // 创建
        context.sendBroadcast(shortcutIntent);
        toastInBottom(context, "创建成功");
    }

    public static boolean hasShortcut(Context context) {
        boolean isInstallShortcut = false;
        final ContentResolver cr = context.getContentResolver();
        final String AUTHORITY = getAuthorityFromPermission(context, "com.android.launcher.permission.READ_SETTINGS");
        if (TextUtils.isEmpty(AUTHORITY)) {
            return false;
        }

        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI,
                new String[]{"title", "iconResource"}, "title=?", new String[]{"一键省电"}, null);
        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        if (c != null) {
            c.close();
        }

        return isInstallShortcut;
    }

    private static String getAuthorityFromPermission(Context context, String permission) {
        if (permission == null || permission.length() == 0) {
            return null;
        }
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packs == null) {
            return null;
        }
        for (PackageInfo pack : packs) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission) || permission.equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }

    public static void goSplash(Context mContent) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://a.app.qq.com/o/simple.jsp?pkgname=com.android11.wallpager");
        intent.setData(content_url);
        mContent.startActivity(intent);
    }
}
