package com.lling.qiqu.activitys;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

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
import com.lling.qiqu.utils.TaskExecutor;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: CheckCodeActivity
 * @Description: 注册验证短信验证码
 * @author lling
 * @date 2015-7-20
 */
@ContentView(R.layout.activity_check_code)
public class CheckCodeActivity extends BaseActivity {
	private final String TAG = "CheckCodeActivity";
	private final int RETRY_INTERVAL = 60;
	private int mTime = RETRY_INTERVAL;
	private String mPhone;
	private String mPassword;
	@ViewInject(R.id.sms_hint_tv)
	private TextView mSmsHintTV;
	@ViewInject(R.id.resend_btn)
	private Button mResendBtn;
	@ViewInject(R.id.code)
	private EditText mCodeEt;
	private IUserService mUserService = new UserServiceImpl(this);
	String mHint;
	SmsContent mSmsContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mSmsContent = new SmsContent(new Handler()); 
		//注册短信变化监听 
		this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, mSmsContent);
		init();
	}
	
	private void init() {
		mHint = getResources().getString(R.string.sms_timer);
		mPhone = getIntent().getStringExtra("phone");
		mPassword = getIntent().getStringExtra("password");
		int left = getIntent().getIntExtra("left", RETRY_INTERVAL);
		mTime = left;
		String hintTest = String.format(mHint, mTime);
        mResendBtn.setText(hintTest);
		String phone = getResources().getString(R.string.sms_hint);    
        String phoneTest = String.format(phone, mPhone);
        mSmsHintTV.setText(Html.fromHtml(phoneTest));
		SMSSDK.registerEventHandler(mEventHandler);
		countDown();
	}
	
	/** 倒数计时 */
	private void countDown() {
		TaskExecutor.scheduleTaskOnUiThread(1000, new Runnable() {
			public void run() {
				mTime--;
				if (mTime == 0) {
			        String hintTest = String.format(mHint, "");
			        mResendBtn.setText(hintTest);
			        mResendBtn.setEnabled(true);
					mTime = RETRY_INTERVAL;
				} else {
			        String hintTest = String.format(mHint, mTime);
			        mResendBtn.setText(hintTest);
			        mResendBtn.setEnabled(false);
					TaskExecutor.scheduleTaskOnUiThread(1000, this);
				}
			}
		});
	}
	
	@OnClick(R.id.resend_btn)
	private void reSend(View view) {
		SMSSDK.getVerificationCode("86", mPhone);
		mTime = RETRY_INTERVAL;
		countDown();
	}
	
	EventHandler mEventHandler = new EventHandler(){
		@Override
		public void afterEvent(int event, int result, Object data) {
			Log.e(TAG, event + "," + result);
			if (result == SMSSDK.RESULT_COMPLETE) {
				// 回调完成
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
					mUserService.regist(mRegistCallBack, mPhone, mPassword);
					ProgressDialogUtils.dismiss();
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					if(App.smsCodeRecode == null) {
						App.smsCodeRecode = new HashMap<String, Long>();
					}
					//记录已发送记录
					App.smsCodeRecode.put(mPhone, System.currentTimeMillis());
				}
			} else {
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//验证成功
					ProgressDialogUtils.dismiss();
					ToastUtils.showMessageInCenter(CheckCodeActivity.this, "验证码输入错误");
				} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					ToastUtils.showMessageInCenter(CheckCodeActivity.this, "发送失败");
				}
			}
		}
	};
	
	//注册接口回调
	ApiCallBack mRegistCallBack = new ApiCallBack() {
		@Override
		public void onSuccess(Object data) {
			ProgressDialogUtils.dismiss();
			User user = (User)data;
			App.currentUser = user;
			spUtil.putObject("user", user);
			Intent aintent = new Intent(CheckCodeActivity.this, CreateUserInfoActivity.class);
			startActivityWithAnimation(aintent);
			finishWithAnimation();
		}
		@Override
		public void onFailure(String msg) {
			ProgressDialogUtils.dismiss();
			ToastUtils.showMessage(CheckCodeActivity.this, R.string.regist_fail);
		}
		@Override
		public void onError(Exception e, String msg) {
			ProgressDialogUtils.dismiss();
			ToastUtils.showMessage(CheckCodeActivity.this, R.string.regist_fail);
		}
	};
	
	@OnClick(R.id.clear)
	private void clean(View view) {
		mCodeEt.setText("");
	}
	
	@OnClick(R.id.check_code_btn)
	private void check(View view) {
		String code = mCodeEt.getText().toString().trim();
		if(Util.isEmpty(code)) {
			ToastUtils.showMessageInCenter(this, "请输入验证码");
			return;
		}
		ProgressDialogUtils.showProgressDialog(CheckCodeActivity.this, getString(R.string.registing));
		SMSSDK.submitVerificationCode("86", mPhone, code);
	}
	
	@Override
	protected void onDestroy() {
		SMSSDK.unregisterEventHandler(mEventHandler);
		this.getContentResolver().unregisterContentObserver(mSmsContent);
		super.onDestroy();
	}
	
	@OnClick(R.id.back)
	private void back(View view) {
		showBackDialog();
	}
	
	@Override
	public void onBackPressed() {
		showBackDialog();
	}
	
	private CTDialog dialog;
	private void showBackDialog() {
		if(dialog == null) {
			CTDialog.Builder customBuilder = new CTDialog.Builder(this);
	        customBuilder.setTitle("温馨提示")
	            .setMessage(getString(R.string.sms_back))
	            .setNegativeButton("继续等待", 
	            		new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                }
	            }).setPositiveButton("取消验证", 
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                	CheckCodeActivity.this.finishWithAnimation();
	                }
	            });
	        dialog = customBuilder.create();
		}
		dialog.show();
	}
	
	/*
     * 监听短信数据库
     */
    class SmsContent extends ContentObserver {
    	private Cursor cursor = null;
    	public SmsContent(Handler handler) {
    		super(handler);
    	}
    	@SuppressWarnings("deprecation")
    	@Override
    	public void onChange(boolean selfChange) {
    		super.onChange(selfChange);
    		// 读取收件箱中指定号码的短信
    		cursor = managedQuery(Uri.parse("content://sms/inbox"),
    				new String[] { "_id", "address", "read", "body" },
    				" address=? and read=?",
    				new String[] { Constants.SMS_SENDER, "0" }, "_id desc");
    		// 按id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
    		if (cursor != null && cursor.getCount() > 0) {
    			ContentValues values = new ContentValues();
    			values.put("read", "1"); // 修改短信为已读模式
    			cursor.moveToNext();
    			int smsbodyColumn = cursor.getColumnIndex("body");
    			String smsBody = cursor.getString(smsbodyColumn);
    			mCodeEt.setText(Util.getDynamicPassword(smsBody));
    		}
    		// 在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上， 会发生崩溃
    		if (Build.VERSION.SDK_INT < 14) {
    			cursor.close();
    		}
    	}
    }
    
    @Override
	protected void onResume() {
		MobclickAgent.onPageStart("CheckCodeActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("CheckCodeActivity");
		super.onPause();
	}
	
}
