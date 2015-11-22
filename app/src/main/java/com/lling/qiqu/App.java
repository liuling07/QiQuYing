package com.lling.qiqu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.util.Log;

import com.lling.qiqu.activitys.UserCenterActivity;
import com.lling.qiqu.beans.User;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.DataCleanManager;
import com.lling.qiqu.utils.SharePreferenceUtil;
import com.tencent.tauth.Tencent;

public class App extends Application {
	private static App mAppApplication;
	public static final String SP_FILE_NAME = "qiquying_msg_sp";
	private static SharePreferenceUtil mSpUtil;
	public static User currentUser = null;
	public static List<Activity> activities = new ArrayList<Activity>(); //用于记录已经开启的activity,便于退出应用时全部关闭
	// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
	// 其中APP_ID是分配给第三方应用的appid，类型为String。
	public static Tencent mTencent;
	public static Map<String, Long> smsCodeRecode;  //注册验证码发送记录
	public static Map<String, Long> smsCodeRecodeFindPwd;  //找回密码验证码发送记录
	public static boolean isStart = false;
	public static boolean isStartOtherActivity = false;
	@Override
	public void onCreate() {
		super.onCreate();
		mAppApplication = this;
		mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
		CrashHandler crashHandler = CrashHandler.getInstance();   
        crashHandler.init(getApplicationContext());
	}
	
	/** 获取Application */
    public static App getInstance() {
        return mAppApplication;
    }
    
    /** 获取mSpUtil */
    public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null)
			mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
		return mSpUtil;
	}


	/*添加activity*/
	public static void addActivity(Activity activity) {
		activities.add(activity);
	}
	
	/*添加activity*/
	public static void removeActivity(Activity activity) {
		if(activities.contains(activity)) {
			activities.remove(activity);
		}
	}
	
	/*关闭所有已打开的activity*/
	public static void clearActivitys() {
		for (Activity ac : activities) {
			if(ac!=null && !ac.isFinishing()) {
				ac.finish();
			}
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		if(Constants.DEBUG) Log.w("App", "onTrimMemory");
		if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
			BitmapUtil.cleanMemCache();  //清除图片内存的中缓存
	    }
	}
}
