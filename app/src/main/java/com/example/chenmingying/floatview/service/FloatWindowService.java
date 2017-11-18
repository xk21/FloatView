package com.example.chenmingying.floatview.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.chenmingying.floatview.bean.GeekEvent;
import com.example.chenmingying.floatview.utils.Constants;
import com.example.chenmingying.floatview.utils.FloatViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class FloatWindowService extends Service  {
    private final String TAG = "FloatWindowService";
    public static boolean isOpen = false;
    private Handler handler = new Handler();
    private Timer timer;
    public static int sGeekShopType = 0;
    private boolean isOpenWindow =true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        isOpen =true;
        Toast.makeText(this, "开启了",Toast.LENGTH_SHORT).show();
        isOpenWindow=getSharedPreferences(Constants.MO_GEEK_FLOAT_SP_GEEK, Context.MODE_PRIVATE)
                .getBoolean(Constants.MO_GEEK_SP_IS_OPEN_WINDOW, true);
        Log.d(TAG,"isOpen 1="+isOpen+" isOpenWindow="+isOpenWindow);
    }

        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isOpenTimer(isOpenWindow);
        return START_STICKY;
    }

    private void isOpenTimer(boolean isOpenWindow) {
        Log.d(TAG,"isOpen 1SHOP"+isOpen+" sGeekShopType="+sGeekShopType+" =");
        if (isOpenWindow) {
            if (timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new RefreshTask(), 0, 4000);
            }
        }else {
            if (timer!=null){
                timer.cancel();
                timer = null;
            }
            FloatViewUtils.getInstance(this).removeWindow();
        }
    }
    @Subscribe
    public void onEventMainThread(GeekEvent event) {
        Log.d(TAG, "MainEvent =" + event.getType());
        switch (event.getType()) {

            case Constants.MO_GEEK_RESIDENT_SHOP:
                Log.d(TAG,"isOpen 1SHOP"+isOpen+" sGeekShopType="+sGeekShopType+" ="
                        +FloatViewUtils.getInstance(this)+" dd=");
                if (sGeekShopType==1) {
                    FloatViewUtils.getInstance(this).updateView();
                }else {
                    FloatViewUtils.getInstance(this).removeWindow();
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onDestroy() {
        Log.d(TAG,"isOpen 2="+isOpen);
        EventBus.getDefault().unregister(this);
        FloatViewUtils.getInstance(this).removeWindow();
        Toast.makeText(this, "关闭了",Toast.LENGTH_SHORT).show();
        if (timer!=null) {
            timer.cancel();
            timer = null;
        }
        isOpen =false;
    }
    /**
     * 判断当前界面是否是桌面
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 1获取信息
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    private class RefreshTask extends TimerTask {
        @Override
        public void run() {
            // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
            Log.d(TAG,"isOpen 111111"+FloatViewUtils.getInstance(FloatWindowService.this).isShowWindow());
            if (isHome() && !FloatViewUtils.getInstance(FloatWindowService.this).isShowWindow()&&
                    sGeekShopType ==0) {
                Log.d(TAG,"isOpen 111111"+isOpen);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FloatViewUtils.getInstance(FloatWindowService.this).addFloatView(40);
                    }
                });
            } else if (!isHome() && FloatViewUtils.getInstance(FloatWindowService.this).isShowWindow()&&sGeekShopType!=1) {
                Log.d(TAG,"isOpen 22222"+isOpen);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FloatViewUtils.getInstance(FloatWindowService.this).removeWindow();
                    }
                });
            }
        }
    }
}
