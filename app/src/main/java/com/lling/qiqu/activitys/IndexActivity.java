package com.lling.qiqu.activitys;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.beans.DingOrCai;
import com.lling.qiqu.dao.DingCaiDAO;
import com.lling.qiqu.service.IJokeService;
import com.lling.qiqu.service.impl.JokeServiceImpl;
import com.lling.qiqu.utils.HttpUtil;
import com.lling.qiqu.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName: IndexActivity
 * @Description: 主界面
 * @author lling
 * @date 2015-6-7
 */
@ContentView(R.layout.activity_index)
public class IndexActivity extends TabActivity implements
OnCheckedChangeListener  {
	@ViewInject(R.id.radiogroup)
	private RadioGroup mRadioGroup;
	private TabHost mTabHost;
	private TabSpec mTabSpec;
	private IJokeService mQuShiService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		App.addActivity(this); // 在界面启动栈中加入该界面
		UmengUpdateAgent.update(this);   //友盟检查更新
		init();
		uploadDingOrCai();
	}
	
	private void init() {
		mTabHost = this.getTabHost();
		mTabSpec = mTabHost.newTabSpec("all").setIndicator("all")
				.setContent(new Intent(this, AllJokeActivity.class)); // 进入会首先显示这个activity
		mTabHost.addTab(mTabSpec);
		mTabSpec = mTabHost.newTabSpec("qushi").setIndicator("qushi")
				.setContent(new Intent(this, QuShiActivity.class)); 
		mTabHost.addTab(mTabSpec);
		mTabSpec = mTabHost.newTabSpec("qutu").setIndicator("qutu")
				.setContent(new Intent(this, QuTuActivity.class));
		mTabHost.addTab(mTabSpec);
		mTabSpec = mTabHost.newTabSpec("meitu").setIndicator("meitu")
				.setContent(new Intent(this, MeiTuActivity.class));
		mTabHost.addTab(mTabSpec);
		mTabSpec = mTabHost.newTabSpec("user").setIndicator("user")
				.setContent(new Intent(this, UserCenterActivity.class));
		mTabHost.addTab(mTabSpec);
		mRadioGroup.setOnCheckedChangeListener(this); // 注册tab点击事件
	
	}
	
	/**
	 * 上传顶和踩数据
	 */
	private void uploadDingOrCai() {
		if(!HttpUtil.isNetworkAvailable(this)) {
			return;
		}
		DingCaiDAO dingCaiDAO = new DingCaiDAO(this);
		List<DingOrCai> dingOrCais = dingCaiDAO.getUnUpload();
		if(dingOrCais == null || dingOrCais.size() == 0) {
			return;
		}
		mQuShiService = new JokeServiceImpl(this);
		List<DingOrCai> dings = new ArrayList<DingOrCai>();
		List<DingOrCai> cais = new ArrayList<DingOrCai>();
		for (DingOrCai dingOrCai : dingOrCais) {
			if(dingOrCai.isDing()) {
				dings.add(dingOrCai);
			} else {
				cais.add(dingOrCai);
			}
		}
		mQuShiService.ding(dings);
		mQuShiService.cai(cais);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (mTabHost != null) {
			switch (checkedId) {
			case R.id.all_tab:
				mTabHost.setCurrentTab(0);
				break;
			case R.id.qushi_tab:
				mTabHost.setCurrentTab(1);
				break;
			case R.id.qutu_tab:
				mTabHost.setCurrentTab(2);
				break;
			case R.id.meitu_tab:
				mTabHost.setCurrentTab(3);
				break;
			case R.id.usercenter_tab:
				mTabHost.setCurrentTab(4);
				break;
			}
		}
	}
	
	
	@Override
	protected void onDestroy() {
		App.removeActivity(this);
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		if(App.isStartOtherActivity) {
			super.overridePendingTransition(R.anim.in_from_right,
					R.anim.out_from_left);
			App.isStartOtherActivity = false;
		}
		super.onPause();
	}
	
	/**
	 * 双击退出
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getRepeatCount() == 0) {
			if (mTabHost.getCurrentTab() != 0) { // 如果按后退键时tab不在第一页，则返回第一个tab
				mTabHost.setCurrentTab(0);
				((RadioButton) mRadioGroup.getChildAt(0)).setChecked(true);
			} else {
				exitBy2Click();
			}
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;
			ToastUtils.showMessage(getApplicationContext(), R.string.exit_hint);
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			}, 2000);
		} else {
			this.finish();
			App.clearActivitys(); // 关闭所有已打开的界面
			MobclickAgent.onKillProcess(this); // 保存统计
			System.exit(0);
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
	
}
