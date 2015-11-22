package com.lling.qiqu.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import cn.smssdk.SMSSDK;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.beans.User;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.utils.HttpUtil;
import com.qq.e.splash.SplashAd;
import com.qq.e.splash.SplashAdListener;
import com.qq.e.splash.SplashAdView;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.weibo.TencentUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * @ClassName: SplashActivity
 * @Description: 应用开机界面
 * @author lling
 * @date 2015年7月15日
 */
@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
	
	private static final String TAG = "SplashActivity";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//使用StrictMode对耗时操作检测
		if (Constants.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork()
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}
		ViewUtils.inject(this);
		App.isStart = true;
		init();
		if(HttpUtil.isNetworkAvailable(this)) {
			//准备展示开屏广告的容器
		    FrameLayout container = (FrameLayout) this
		            .findViewById(R.id.splashcontainer);        
		    SplashAdView adView = null;
			// 创建开屏广告，广告拉取成功后会自动展示在container中。Container会首先被清空
		    new SplashAd(this, container, Constants.GDT_APPId, Constants.GDT_SPLASHPosId, 
					new SplashAdListener() {
						// 广告拉取成功开始展示时调用
						public void onAdPresent() {
							if(Constants.DEBUG) {
								Log.d(TAG, "present");
							}
						}
						// 广告拉取超时（3s）或者没有广告时调用，errCode参见SplashAd类的常量声明
						public void onAdFailed(int errCode) {
							Log.w(TAG, "onAdFailed" + errCode);
							Intent intent = new Intent(SplashActivity.this, IndexActivity.class);
							startActivityWithAnimation(intent);
							SplashActivity.this.finish();
						}
						// 广告展示时间结束（5s）或者用户点击关闭时调用。
						public void onAdDismissed() {
							if(Constants.DEBUG) Log.i(TAG, "onAdDismissed");
							Intent intent = new Intent(SplashActivity.this, IndexActivity.class);
							startActivityWithAnimation(intent);
							SplashActivity.this.finish();
						}
					});
		} else {
			Handler handler = new Handler();
			handler.postDelayed(startAct, 3000);
		}
	}
	
	/**
	 * 应用程序初始化
	 */
	private void init() {
		initYouMeng();
		App.currentUser = (User)spUtil.getObject("user", null);
		//初始化短信接口
        SMSSDK.initSDK(this, getString(R.string.sina_appid), getString(R.string.youmeng_appid));
        initWeiXin();
        TencentUtils.initTencent(this, spUtil);
	}
	
	private void initYouMeng() {
//		AnalyticsConfig.enableEncrypt(true);
		MobclickAgent.openActivityDurationTrack(false);
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		if(spUtil.getBoolean(Constants.IS_RECEIVE_PUSH, true)) {
			mPushAgent.enable();
		} else {
			mPushAgent.disable();
		}
	}
	
	private void initWeiXin() {
		IWXAPI api = WXAPIFactory.createWXAPI(this, com.lling.qiqu.wxapi.Constants.APP_ID);
		api.registerApp(com.lling.qiqu.wxapi.Constants.APP_ID);
	}
	
	private Runnable startAct = new Runnable() {
		@Override
		public void run() {
			startActivityWithAnimation(new Intent(SplashActivity.this, IndexActivity.class));
			finish();
		}
	};

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 阻止用户在展示过程中点击手机返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onBackPressed() { }
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("SplashActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("SplashActivity");
		super.onPause();
	}
	
}
