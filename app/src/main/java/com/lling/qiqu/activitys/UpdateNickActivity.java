package com.lling.qiqu.activitys;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.service.IUserService;
import com.lling.qiqu.service.impl.UserServiceImpl;
import com.lling.qiqu.utils.ProgressDialogUtils;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.ToolsUtils;
import com.lling.qiqu.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: UpdateNickActivity
 * @Description: 修改用户昵称
 * @author lling
 * @date 2015-7-5
 */
@ContentView(R.layout.activity_update_nick)
public class UpdateNickActivity extends BaseActivity {

	@ViewInject(R.id.update_nickname)
	private EditText mNickET;
	private IUserService mUserService = new UserServiceImpl(this);
	private ClassHandler mHandler = new ClassHandler(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
	}
	
	private void init() {
		if(App.currentUser != null) {
			mNickET.setText(App.currentUser.getUserNike());
			mNickET.setSelection(App.currentUser.getUserNike().length());
		}
	}
	
	/* 提交修改昵称 */
	String nickName;
	@OnClick(R.id.save_tv)
	private void updateNick(View view) {
		nickName = mNickET.getText().toString().trim();
		if(Util.isEmpty(nickName)) {
			ToastUtils.showMessage(this, R.string.user_update_nickname_null);
			return;
		}
		nickName = ToolsUtils.Html2Text(nickName);
		if(Util.isEmpty(nickName)) {
			ToastUtils.showMessage(getApplicationContext(), 
					R.string.input_invalide);
			return;
		}
		ToolsUtils.hideKeyboard(mNickET);
		if (App.currentUser != null) {
			if(nickName.equals(App.currentUser.getUserNike())) {
				finishWithAnimation();
				return;
			}
			try {
				ProgressDialogUtils.showProgressDialog(this, "正在处理中...");
				mUserService.setNickAndSex(mHandler, String.valueOf(App.currentUser.getId()), 
						URLEncoder.encode(nickName, "UTF-8"), null);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 消息处理类
	 * @author lling
	 */
	static class ClassHandler extends Handler {
		WeakReference<UpdateNickActivity> mActivityReference;

		ClassHandler(UpdateNickActivity activity) {
			mActivityReference = new WeakReference<UpdateNickActivity>(activity);
		}

		public void handleMessage(Message msg) {
			final UpdateNickActivity activity = mActivityReference.get();
			if(activity == null) {
				return;
			}
			switch (msg.what) {
			case Constants.FAILURE_2: //设置性别失败
				ProgressDialogUtils.dismiss();
				break;
			case Constants.SUCCESS_2: //设置性别成功
				activity.setNickSuccess();
				ProgressDialogUtils.dismiss();
				break;
			}
		}
	}
	
	/**
	 * 设置昵称和性别成功
	 */
	private void setNickSuccess() {
		if(App.currentUser == null) {
			return;
		}
		App.currentUser.setUserNike(nickName);
		spUtil.putObject("user", App.currentUser);
		finishWithAnimation();
	}
	
	@OnClick(R.id.clear)
	private void clear(View view) {
		mNickET.setText("");
	}
	
	@OnClick(R.id.back)
	private void back(View view) {
		finishWithAnimation();
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("UpdateNickActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("UpdateNickActivity");
		super.onPause();
	}

}
