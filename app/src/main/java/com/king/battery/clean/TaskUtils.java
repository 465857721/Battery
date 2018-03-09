package com.king.battery.clean;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.Stat;
import com.jaredrummler.android.processes.models.Statm;
import com.king.battery.clean.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoukang on 2017/6/12.
 */

public class TaskUtils {

    /**
     * 获取当前正在进行的进程数
     *
     * @param context
     * @return
     */
    public static int getRunningAppProcessInfoSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses().size();
    }

    /**
     * 获取系统可用内存
     *
     * @param context
     * @return
     */
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //得到可用内存
        MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem; //单位是byte
        return availMem;
    }

    /**
     * 获取系统所有的进程信息列表
     *
     * @param context
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context) {
        List<TaskInfo> TaskInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            // Get a list of running apps
            List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();

            for (AndroidAppProcess process : processes) {
                // Get some information about the process
                try {
                    String processName = process.name;
                    Stat stat = process.stat();
                    int pid = stat.getPid();
                    int parentProcessId = stat.ppid();
                    long startTime = stat.stime();
                    int policy = stat.policy();
                    char state = stat.state();

                    Statm statm = process.statm();
                    long totalSizeOfProcess = statm.getSize();
                    long residentSetSize = statm.getResidentSetSize();
                    PackageInfo packageInfo = process.getPackageInfo(context, 0);
                    TaskInfo info = new TaskInfo();
                    String packageName;
                    packageName = packageInfo.packageName;
                    if (processName.contains(":")) {
                        packageName = packageName.split(":")[0];
                    }
                    info.setPackageName(packageName);

                    ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                    //图标
                    Drawable task_icon = applicationInfo.loadIcon(pm);

                    info.setTask_icon(task_icon);

                    String task_name = applicationInfo.loadLabel(pm).toString();

                    Log.d("zk task_name = ", task_name);
                    if (TextUtils.isEmpty(task_name)) {
                        info.setTask_name(packageName);
                    } else {
                        info.setTask_name(task_name);
                    }

                    if (!TextUtils.equals(packageName, context.getPackageName()))
                        TaskInfos.add(info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            }

        } else {

            List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            for (RunningAppProcessInfo info : runningAppProcesses) {
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                //IMPORTANCE_VISIBLE
                if (info.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                    TaskInfo Info = new TaskInfo();
                    //进程名称
                    String packageName;
                    if (info.processName.contains(":")) {
                        packageName = info.processName.split(":")[0];
                    } else {
                        packageName = info.processName;
                    }

                    Log.d("zk packageName = ", packageName);
                    Info.setPackageName(packageName);

                    try {
                        ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
                        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
//                        abAppProcessInfo.isSystem = true;
//                       break;
                        }
                        //图标
                        Drawable task_icon = applicationInfo.loadIcon(pm);
                        Info.setTask_icon(task_icon);

                        String task_name = applicationInfo.loadLabel(pm).toString();

                        Log.d("zk task_name = ", task_name);
                        if (TextUtils.isEmpty(task_name)) {
                            Info.setTask_name(packageName);
                        } else {
                            Info.setTask_name(task_name);
                        }

                    } catch (NameNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Info.setTask_name(packageName);
//                    break;
                    }

                    //进程id
                    int pid = info.pid;
                    Info.setPid(pid);
                    //获取进程占用的内存
                    android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{pid});
                    android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
                    long totalPrivateDirty = memoryInfo.getTotalPrivateDirty(); //KB
                    Info.setTask_memory(totalPrivateDirty);

                    if (!TextUtils.equals(packageName, context.getPackageName()))
                        TaskInfos.add(Info);
                }
            }
        }


        return TaskInfos;
    }

}
