package com.lling.qiqu.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lidroid.xutils.BitmapUtils;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.utils.SharePreferenceUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: BaseActivity
 * @Description: Activity基类
 * @author lling
 * @date 2015-6-7
 */
public class BaseActivity extends FragmentActivity {
	protected SharePreferenceUtil spUtil;
	protected BitmapUtils bitmapUtils;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this); //在界面启动栈中加入该界面
		spUtil = App.getInstance().getSpUtil();
		initBitmapUtils();
	}
	//初始化bitmapUtils
	private void initBitmapUtils() {
		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		bitmapUtils.configMemoryCacheEnabled(true);
		bitmapUtils.configDiskCacheEnabled(true);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.removeActivity(this); //在界面启动栈中删除该界面
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}
	
	//带动画打开界面
	protected void startActivityForResultWithAnimation(Intent intent, int requestCode) {
		startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
	}
	//带动画打开界面
	protected void startActivityWithAnimation(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
	}
	
	//带动画关闭界面
	protected void finishWithAnimation() {
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
	}
	@Override
	public void onBackPressed() {
		finishWithAnimation();
		super.onBackPressed();
	}
	
}
