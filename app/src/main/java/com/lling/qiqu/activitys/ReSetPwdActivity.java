package com.lling.qiqu.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.beans.User;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.interfaces.ApiCallBack;
import com.lling.qiqu.service.IUserService;
import com.lling.qiqu.service.impl.UserServiceImpl;
import com.lling.qiqu.ui.CTDialog;
import com.lling.qiqu.utils.ProgressDialogUtils;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: ReSetPwdActivity
 * @Description: 重置密码界面
 * @author lling
 * @date 2015-7-22
 */
@ContentView(R.layout.activity_reset_pwd)
public class ReSetPwdActivity extends BaseActivity {

	@ViewInject(R.id.password1)
	private EditText mPassword1;
	@ViewInject(R.id.password2)
	private EditText mPassword2;
	private IUserService mUserService = new UserServiceImpl(this);
	private String mPhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mPhone = getIntent().getStringExtra("phone");
	}
	
	/**
	 * 修改密码
	 * @param view
	 */
	@OnClick(R.id.save_btn)
	private void savePassword(View view) {
		String password1 = mPassword1.getText().toString().trim();
		String password2 = mPassword2.getText().toString().trim();
		if(Util.isEmpty(password1)) {
			ToastUtils.showMessage(this, R.string.input_pwd_hint);
			return;
		}
		if(password1.length() < 6) {
			ToastUtils.showMessage(this, R.string.password_short);
			return;
		}
		if(Util.isEmpty(password2)) {
			ToastUtils.showMessage(this, R.string.input_pwd_hint1);
			return;
		}
		if(!password1.equals(password2)) {
			ToastUtils.showMessage(this, R.string.input_not_same);
			return;
		}
		ProgressDialogUtils.showProgressDialog(ReSetPwdActivity.this, getString(R.string.doing));
		mUserService.reSetPassword(mPhone, password1, apiCallBack);
	}
	
	private ApiCallBack apiCallBack = new ApiCallBack() {
		@Override
		public void onSuccess(Object data) {
			ProgressDialogUtils.dismiss();
			App.currentUser = (User)data;
			spUtil.putObject("user", App.currentUser);
			Intent intent = new Intent(ReSetPwdActivity.this, IndexActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityWithAnimation(intent);
			finishWithAnimation();
		}
		
		@Override
		public void onFailure(String msg) {
			ProgressDialogUtils.dismiss();
			if(Constants.PARAM_INVALUD.equals(msg)) {
				ToastUtils.showMessage(ReSetPwdActivity.this, R.string.phone_invalid);
			} else if(Constants.SOURCE_NOT_EXISTS.equals(msg)) {
				ToastUtils.showMessage(ReSetPwdActivity.this, R.string.phone_not_exists);
			} else {
				ToastUtils.showMessage(ReSetPwdActivity.this, R.string.reset_pwd_fail);
			}
		}
		@Override
		public void onError(Exception e, String msg) {
			ProgressDialogUtils.dismiss();
			ToastUtils.showMessage(ReSetPwdActivity.this, R.string.reset_pwd_fail);
		}
	};
	
	@OnClick(R.id.back)
	private void back(View view) {
		showBackDialog();
	}
	
	@Override
	public void onBackPressed() {
		showBackDialog();
	}
	
	// 取消
	DialogInterface.OnClickListener backCancelListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	};
	// 确定发送
	DialogInterface.OnClickListener backOkListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			finishWithAnimation();
		}
	};
	CTDialog.Builder customBuilder = new CTDialog.Builder(this);
	CTDialog sendSmsDialog;
	private void showBackDialog() {
		if(sendSmsDialog == null) {
			customBuilder.setTitle("温馨提示")
			.setMessage("确定要放弃此次密码重置操作吗？")
			.setNegativeButton("继续", backCancelListener)
			.setPositiveButton("放弃", backOkListener);
			sendSmsDialog = customBuilder.create();
		}
		sendSmsDialog.show();
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ReSetPwdActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("ReSetPwdActivity");
		super.onPause();
	}
	
}
