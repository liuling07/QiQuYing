package com.lling.qiqu.activitys;

import java.lang.ref.WeakReference;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.R;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.service.IUserService;
import com.lling.qiqu.service.impl.UserServiceImpl;
import com.lling.qiqu.ui.CTDialog;
import com.lling.qiqu.utils.ProgressDialogUtils;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.ToolsUtils;
import com.lling.qiqu.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: FeedbackActivity
 * @Description: 意见反馈activity
 * @author lling
 * @date 2015年7月10日
 */
@ContentView(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity {
	
	@ViewInject(R.id.feedback_content)
	private EditText mContentET;
	@ViewInject(R.id.feedback_contact)
	private EditText mContactET;
	private IUserService mUserService;
	private ClassHandler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mUserService = new UserServiceImpl(this);
		mHandler = new ClassHandler(this);
	}
	
	@OnClick(R.id.save_tv)
	private void feedback(View view) {
		String content = mContentET.getText().toString().trim();
		if(Util.isEmpty(content)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.content_null);
			return;
		}
		content = ToolsUtils.Html2Text(content);
		if(Util.isEmpty(content)) {
			ToastUtils.showMessage(getApplicationContext(), 
					R.string.input_invalide);
			return;
		}
		String contact = mContactET.getText().toString();
		if(Util.isNotEmpty(contact)) {  //如果填写了联系方式，则验证格式
			if(!Util.isEmail(contact) && !Util.isPhone(contact)) {
				ToastUtils.showMessage(getApplicationContext(), R.string.contact_invalid);
				return;
			}
		}
		contact = ToolsUtils.Html2Text(contact);
		ProgressDialogUtils.showProgressDialog(this, "提交反馈中...");
		mUserService.feedback(mHandler, content, contact, "");
	}
	
	private void feedbackSuccess() {
//		mContentET.setText("");
//		mContactET.setText("");
		finishWithAnimation();
	}
	
	/**
	 * 消息处理类
	 * @author lling
	 */
	static class ClassHandler extends Handler {
		WeakReference<FeedbackActivity> mActivityReference;

		ClassHandler(FeedbackActivity activity) {
			mActivityReference = new WeakReference<FeedbackActivity>(activity);
		}

		public void handleMessage(Message msg) {
			final FeedbackActivity activity = mActivityReference.get();
			if (activity != null) {
				switch (msg.what) {
				case Constants.FAILURE: // 反馈失败
					break;
				case Constants.SUCCESS: // 反馈成功
					activity.feedbackSuccess();
					break;
				}
				ProgressDialogUtils.dismiss();
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		toBack();
	}
	
	private void back() {
		finishWithAnimation();
	}
	
	
	private CTDialog dialogEdited;
	private CTDialog dialogNotEdited;
	
	@OnClick(R.id.back)
	private void back(View view) {
		toBack();
	}
	
	private void toBack() {
		String content = mContentET.getText().toString().trim();
		if(Util.isEmpty(content)) {
			if(dialogNotEdited == null) {
				CTDialog.Builder customBuilder = new CTDialog.Builder(this);
		        customBuilder.setTitle("温馨提示")
		            .setMessage("既然来了，就给点意见呗！")
		            .setNegativeButton("我要反馈", 
		            		new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	dialog.dismiss();
		                }
		            }).setPositiveButton("去意已决", 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	dialog.dismiss();
		                	FeedbackActivity.this.back();
		                }
		            });
		        dialogNotEdited = customBuilder.create();
			}
			dialogNotEdited.show();
		} else {
			if(dialogEdited == null) {
				CTDialog.Builder customBuilder = new CTDialog.Builder(this);
		        customBuilder.setTitle("温馨提示")
		            .setMessage("辛辛苦苦写的意见真的不提交给我们吗？")
		            .setNegativeButton("我要反馈", 
		            		new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	dialog.dismiss();
		                }
		            }).setPositiveButton("去意已决", 
		                    new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	dialog.dismiss();
		                	FeedbackActivity.this.back();
		                }
		            });
		        dialogEdited = customBuilder.create();
			}
	        dialogEdited.show();
		}
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("FeedbackActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("FeedbackActivity");
		super.onPause();
	}

}
