package com.lling.qiqu.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.ui.CTDialog;
import com.lling.qiqu.ui.CircleImageView;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.DataCleanManager;
import com.lling.qiqu.utils.ToastUtils;
import com.qq.e.appwall.GdtAppwall;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * @ClassName: UserCenterActivity
 * @Description: 个人中心activity
 * @author lling
 * @date 2015-6-7
 */
@ContentView(R.layout.activity_user_center)
public class UserCenterActivity extends BaseActivity {
	
	private final String TAG = "UserCenterActivity";
	private final int LOGIN_CODE = 1;
	
	@ViewInject(R.id.user_img)
	private CircleImageView mUserPortrait;
	@ViewInject(R.id.username)
	private TextView mUserNick;
	@ViewInject(R.id.cache_size_tv)
	private TextView mCacheSizeTV;
	@ViewInject(R.id.img_load_cb)
	private CheckBox isLoadImgOnNotWifi;
	@ViewInject(R.id.content_notify_cb)
	private CheckBox isReceivePush;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		init();
		initCheckBoxStatusAndSetListenner();
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
		    @Override
		    public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
		        switch (updateStatus) {
		        case UpdateStatus.Yes: // has update
		            UmengUpdateAgent.showUpdateDialog(UserCenterActivity.this, updateInfo);
		            break;
		        case UpdateStatus.No: // has no update
		            ToastUtils.showMessageInCenter(UserCenterActivity.this, "已是最新版本");
		            break;
		        case UpdateStatus.Timeout: // time out
		        	ToastUtils.showMessageInCenter(UserCenterActivity.this, "检测超时");
		            break;
		        }
		    }
		});
	}
	
	private void init() {
		if(App.currentUser != null) {
			BitmapUtil.display(mUserPortrait, App.currentUser.getPortraitUrl());
			mUserNick.setText(App.currentUser.getUserNike());
		} else {
			mUserPortrait.setImageResource(R.drawable.default_portrait);
			mUserNick.setText(R.string.click_login);
		}
		try {
			mCacheSizeTV.setText(DataCleanManager.getCacheSize(this));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		init();
		MobclickAgent.onPageStart("UserCenterActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("UserCenterActivity");
		super.onPause();
	}
	
	/**
	 * 个人信息
	 * @param view
	 */
	@OnClick(R.id.user_img)
	private void toUserInfo(View view) {
		if(App.currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityWithAnimation(intent);
			App.isStartOtherActivity = true;
			return;
		}
		//跳转到个人信息界面
		Intent intent = new Intent(this, UserInfoActivity.class);
		startActivityWithAnimation(intent);
		App.isStartOtherActivity = true;
	}

	/**
	 * 我的收藏
	 * @param view
	 */
	@OnClick(R.id.mycollect_layout)
	private void toMyCollect(View view) {
		Intent intent = new Intent(this, MyCollectActivity.class);
		startActivityWithAnimation(intent);
		App.isStartOtherActivity = true;
	}
	
	/**
	 * 反馈
	 * @param view
	 */
	@OnClick(R.id.feedback_layout)
	private void toFeedback(View view) {
		Intent intent = new Intent(this, FeedbackActivity.class);
		startActivityWithAnimation(intent);
		App.isStartOtherActivity = true;
	}
	
	/**
	 * 关于奇趣营
	 * @param view
	 */
	@OnClick(R.id.about_layout)
	private void toAbout(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivityWithAnimation(intent);
		App.isStartOtherActivity = true;
	}
	
	/**
	 * 检查版本更新
	 * @param view
	 */
	@OnClick(R.id.update_layout)
	private void checkUpdate(View view) {
		UmengUpdateAgent.forceUpdate(this);
	}
	
	private CTDialog dialog;
	/**
	 * 清除缓存
	 * @param view
	 */
	@OnClick(R.id.clearcache_layout)
	private void cleanCache(View view) {
		if(dialog == null) {
			CTDialog.Builder customBuilder = new CTDialog.Builder(this);
	        customBuilder.setTitle("温馨提示")
	            .setMessage("确定要清除缓存吗？")
	            .setNegativeButton("取消", 
	            		new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                }
	            }).setPositiveButton("确定", 
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                	dialog.dismiss();
	                	DataCleanManager.CleanCache(UserCenterActivity.this);
	            		mCacheSizeTV.setText("0.0B");
	                }
	            });
	        dialog = customBuilder.create();
		}
		dialog.show();
	}
	
	/**
	 * 广点通应用推荐
	 * @param view
	 */
	@OnClick(R.id.apptuijian_layout)
	private void gdtAppWallAd(View view) {
		GdtAppwall wall =
	            new GdtAppwall(this, Constants.GDT_APPId, Constants.GDT_APPWallPosId, Constants.TESTAD);
	    wall.doShowAppWall();
	}
	
	/**
	 * 初始化checkbox选中状态,并设置监听器
	 */
	private void initCheckBoxStatusAndSetListenner() {
		if(spUtil.getBoolean(Constants.IS_LOAD_IMG, true)) {
			isLoadImgOnNotWifi.setChecked(true);
		} else {
			isLoadImgOnNotWifi.setChecked(false);
		}
		if(spUtil.getBoolean(Constants.IS_RECEIVE_PUSH, true)) {
			isReceivePush.setChecked(true);
		} else {
			isReceivePush.setChecked(false);
		}
		setOnCheckedChangeListenner();
	}
	
	OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(buttonView.getId() == R.id.img_load_cb) {
				if(isChecked) {
					spUtil.putBoolean(Constants.IS_LOAD_IMG, true);
				} else {
					spUtil.putBoolean(Constants.IS_LOAD_IMG, false);
				}
			}
			if(buttonView.getId() == R.id.content_notify_cb) {
				if(isChecked) {
					spUtil.putBoolean(Constants.IS_RECEIVE_PUSH, true);
					PushAgent.getInstance(UserCenterActivity.this).enable();
				} else {
					spUtil.putBoolean(Constants.IS_RECEIVE_PUSH, false);
					PushAgent.getInstance(UserCenterActivity.this).disable();
				}
			}
		}
	};
	
	/**
	 * 注册checkbox状态改变监听器
	 */
	private void setOnCheckedChangeListenner() {
		isLoadImgOnNotWifi.setOnCheckedChangeListener(checkedChangeListener);
		isReceivePush.setOnCheckedChangeListener(checkedChangeListener);
	}
	
}
