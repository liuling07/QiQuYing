package com.lling.qiqu.activitys;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.beans.User;
import com.lling.qiqu.interfaces.ApiCallBack;
import com.lling.qiqu.service.IUserService;
import com.lling.qiqu.service.impl.UserServiceImpl;
import com.lling.qiqu.ui.CTDialog;
import com.lling.qiqu.utils.ProgressDialogUtils;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.Util;
import com.sina.weibo.AccessTokenKeeper;
import com.sina.weibo.UsersAPI;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.tencent.weibo.TencentUtils;
import com.umeng.analytics.MobclickAgent;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
	
	private final String TAG = "LoginActivity";
	
	@ViewInject(R.id.login_layout)
	private LinearLayout mLoginLayout;
	@ViewInject(R.id.regist_layout)
	private LinearLayout mRegistLayout;
	@ViewInject(R.id.to_login_btn)
	private TextView mToLoginBtn;
	@ViewInject(R.id.to_regist_btn)
	private TextView mToRegistBtn;
	@ViewInject(R.id.login_phone)
	private EditText mLoginEmail;
	@ViewInject(R.id.login_password)
	private EditText mLoginPassword;
	@ViewInject(R.id.regist_phone)
	private EditText mRegistPhone;
	@ViewInject(R.id.regist_password)
	private EditText mRegistPassword;
	
	private String mAppId;
	private Tencent mTencent;
	private UserInfo mInfo;
	private String mOpenId;
	private ClassHandler mHandler = new ClassHandler(this);
	private IUserService mUserService = new UserServiceImpl(this);
	EventHandler mEventHandler;   //短信发送回调
	String mPhone;
	String mPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mTencent = App.mTencent;
		mEventHandler = new EventHandler(){
			@Override
			public void afterEvent(int event, int result, Object data) {
				ProgressDialogUtils.dismiss();
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 回调完成
					if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						// 获取验证码成功
						if(App.smsCodeRecode == null) {
							App.smsCodeRecode = new HashMap<String, Long>();
						}
						//记录已发送记录
						App.smsCodeRecode.put(mPhone, System.currentTimeMillis());
						Intent intent = new Intent(LoginActivity.this, CheckCodeActivity.class);
						intent.putExtra("phone", mPhone);
						intent.putExtra("password", mPassword);
						startActivityWithAnimation(intent);
					}
				} else {
					ToastUtils.showMessageInCenter(LoginActivity.this, "发送失败, 请重试");
				}
			}
		};
	}
	
	private ApiCallBack checkExeistCallBack = new ApiCallBack() {
		@Override
		public void onSuccess(Object data) {
			SMSSDK.getVerificationCode("86", mPhone.trim());
		}
		@Override
		public void onFailure(String msg) {
			ProgressDialogUtils.dismiss();
			if("501".equals(msg)) {
				ToastUtils.showMessageInCenter(LoginActivity.this, getString(R.string.phone_invalid));
			} else if("502".equals(msg)) {
				ToastUtils.showMessageInCenter(LoginActivity.this, getString(R.string.phone_exists));
			} else {
				ToastUtils.showMessageInCenter(LoginActivity.this, "发送失败, 请重试");
			}
		}
		@Override
		public void onError(Exception e, String msg) {
			ProgressDialogUtils.dismiss();
			ToastUtils.showMessageInCenter(LoginActivity.this, "发送失败, 请重试");
		}
	};
	
	IUiListener loginListener = new IUiListener() {
		@Override
		public void onCancel() {
		}
		@Override
		public void onComplete(Object response) {
			if (null == response) {
                ToastUtils.showMessage(getApplicationContext(), "登录失败,返回为空");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
            	ToastUtils.showMessage(getApplicationContext(), "登录失败,返回为空");
                return;
            }
            initOpenidAndToken(jsonResponse);
            updateUserInfo();
		}
		@Override
		public void onError(UiError arg0) {
		}
    };
    
    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            mOpenId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(mOpenId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(mOpenId);
                App.mTencent = mTencent;
                //存储OpenidAndToken值
                spUtil.putString(Constants.PARAM_OPEN_ID, mOpenId);
                spUtil.putString(Constants.PARAM_ACCESS_TOKEN, token);
                spUtil.putLong(Constants.PARAM_EXPIRES_IN, System.currentTimeMillis() + Long.parseLong(expires) * 1000);
            }
        } catch(Exception e) {
        	Log.e(TAG, "initOpenidAndToken exception", e);
        }
    }
    
    private void updateUserInfo() {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {
				@Override
				public void onError(UiError e) {
					ToastUtils.showMessage(getApplicationContext(), "获取授权信息失败");
				}
				@Override
				public void onComplete(Object response) {
					Log.e(TAG, "get login user info success!");
					try {
						JSONObject json = (JSONObject)response;
						String portraitUrl = json.getString("figureurl_qq_2");
						String nickName = json.getString("nickname");
						String sex = json.getString("gender");
						ProgressDialogUtils.showProgressDialog(LoginActivity.this, "授权成功，正在处理中...");
						mUserService.addTencentUser(mHandler, mOpenId, portraitUrl, nickName, sex!=null&&"男".equals(sex)?1:0);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onCancel() {
				}
			};
			mInfo = new UserInfo(this, mTencent.getQQToken());
			mInfo.getUserInfo(listener);
		} 
	}
	
	@OnClick(R.id.btn_login_qq)
	private void loginByQQ(View view) {
		if(mTencent == null) {
			TencentUtils.initTencent(this, spUtil);
			mTencent = App.mTencent;
		}
		mTencent.login(this, "all", loginListener);
	}
	
	@OnClick(R.id.to_regist_btn)
	private void toRegist(View view) {
		Animation outLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.login_out_from_left);
		outLeftAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				mLoginLayout.setVisibility(View.GONE);
				Animation inRightAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_in_from_right);
				inRightAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						mRegistLayout.setVisibility(View.VISIBLE);
					}
				});
				mRegistLayout.startAnimation(inRightAnimation);
			}
		});
		mLoginLayout.startAnimation(outLeftAnimation);
		mToRegistBtn.setVisibility(View.GONE);
		mToLoginBtn.setVisibility(View.VISIBLE);
	}
	
	@OnClick(R.id.to_login_btn)
	private void toLogin(View view) {
		Animation outRightAnimation = AnimationUtils.loadAnimation(this, R.anim.login_out_from_right);
		outRightAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				mRegistLayout.setVisibility(View.GONE);
				Animation inLeftAnimation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.login_in_from_left);
				inLeftAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						mLoginLayout.setVisibility(View.VISIBLE);
					}
				});
				mLoginLayout.startAnimation(inLeftAnimation);
			}
		});
		mRegistLayout.startAnimation(outRightAnimation);
		mToRegistBtn.setVisibility(View.VISIBLE);
		mToLoginBtn.setVisibility(View.GONE);
	}
	
	@OnClick(R.id.login_btn)
	private void login(View view) {
		String phone = mLoginEmail.getText().toString();
		String password = mLoginPassword.getText().toString();
		if(Util.isEmpty(phone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_null);
			return;
		}
		if(Util.isEmpty(password)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_null);
			return;
		}
		if(password.length() < 6) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_short);
			return;
		}
		if(!Util.isPhone(phone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_invalid);
			return;
		}
		ProgressDialogUtils.showProgressDialog(this, getString(R.string.logining));
		mUserService.login(mHandler, phone, password);
	}
	
	@OnClick(R.id.regist_btn)
	private void regist(View view) {
		mPhone = mRegistPhone.getText().toString();
		mPassword = mRegistPassword.getText().toString();
		if(Util.isEmpty(mPhone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_null);
			return;
		}
		if(Util.isEmpty(mPassword)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_null);
			return;
		}
		if(mPassword.length() < 6) {
			ToastUtils.showMessage(getApplicationContext(), R.string.password_short);
			return;
		}
		if(!Util.isPhone(mPhone)) {
			ToastUtils.showMessage(getApplicationContext(), R.string.phone_invalid);
			return;
		}
		if(App.smsCodeRecode != null && App.smsCodeRecode.containsKey(mPhone)) {
			long time = App.smsCodeRecode.get(mPhone);
			int interval = (int)((System.currentTimeMillis() - time) / 1000);
			int left = 60 - interval;
			if(left > 10) {
				Intent intent = new Intent(LoginActivity.this, CheckCodeActivity.class);
				intent.putExtra("phone", mPhone);
				intent.putExtra("password", mPassword);
				intent.putExtra("left", left);
				startActivityWithAnimation(intent);
				return;
			}
		}
		/*ProgressDialogUtils.showProgressDialog(this, getString(R.string.registing));
		mUserService.regist(mHandler, phone, password);*/
		showSendSmsDialog();
	}
	//取消
	DialogInterface.OnClickListener sendCancelListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	dialog.dismiss();
        }
    };
    //确定发送
    DialogInterface.OnClickListener sendOkListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        	dialog.dismiss();
        	ProgressDialogUtils.showProgressDialog(LoginActivity.this, "正在发送验证短信...");
        	mUserService.checkUserExeist(mPhone, checkExeistCallBack);
        }
    };
	CTDialog.Builder customBuilder = new CTDialog.Builder(this);
	private void showSendSmsDialog() {
        customBuilder.setTitle("温馨提示")
            .setMessage("我们将发送验证码到这个号码\n+86 " + mPhone)
            .setNegativeButton("取消", sendCancelListener).setPositiveButton("确定", 
            		sendOkListener);
        CTDialog sendSmsDialog = customBuilder.create();
        sendSmsDialog.show();
	}
	
	
	/**
	 * 消息处理类
	 * @author lling
	 */
	static class ClassHandler extends Handler {
		WeakReference<LoginActivity> mActivityReference;

		ClassHandler(LoginActivity activity) {
			mActivityReference = new WeakReference<LoginActivity>(activity);
		}

		public void handleMessage(Message msg) {
			final LoginActivity activity = mActivityReference.get();
			if (activity != null) {
				switch (msg.what) {
				case com.lling.qiqu.commons.Constants.FAILURE: // QQ登录失败
//					ToastUtils.showMessage(activity, R.string.user_update_fail);
					break;
				case com.lling.qiqu.commons.Constants.SUCCESS: // QQ登录成功
					MobclickAgent.onEvent(activity, "login_qq");
					// 设置本地用户
					User user = (User)msg.getData().getSerializable("user");
					App.currentUser = user;
					activity.spUtil.putObject("user", user);
					activity.finishWithAnimation();
					break;
				case com.lling.qiqu.commons.Constants.FAILURE_2: // 新浪微博登录失败
//					ToastUtils.showMessage(activity, R.string.user_update_fail);
					break;
				case com.lling.qiqu.commons.Constants.SUCCESS_2: // 新浪微博登录成功
					MobclickAgent.onEvent(activity, "login_sina");
					// 设置本地用户
					User user1 = (User)msg.getData().getSerializable("user");
					App.currentUser = user1;
					activity.spUtil.putObject("user", user1);
					activity.finishWithAnimation();
					break;
				case com.lling.qiqu.commons.Constants.FAILURE_1: // 注册失败
//					ToastUtils.showMessage(activity, R.string.user_update_fail);
					break;
				case com.lling.qiqu.commons.Constants.SUCCESS_1: // 注册成功
					// 设置本地用户
					activity.registSuccess((User)msg.getData().getSerializable("user"));
					break;
				}
				ProgressDialogUtils.dismiss();
			}
		}
	}
	
	private void registSuccess(User user) {
		App.currentUser = user;
		spUtil.putObject("user", user);
		Intent aintent = new Intent(this, CreateUserInfoActivity.class);
		startActivityWithAnimation(aintent);
		finishWithAnimation();
	}
	
	
	@OnClick(R.id.back)
	private void back(View view) {
		finishWithAnimation();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if(requestCode == Constants.REQUEST_API) {
			if(resultCode == Constants.RESULT_LOGIN) {
			    mTencent.handleLoginData(data, loginListener);
		    }
		    super.onActivityResult(requestCode, resultCode, data);
	    }
	    // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
	}
	
	//新浪微博登录
	private AuthInfo mAuthInfo;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    // 创建微博实例
    private void initSinaWeiBo() {
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
    	if(mAuthInfo == null) {
    		mAuthInfo = new AuthInfo(this, com.sina.weibo.Constants.APP_KEY, com.sina.weibo.Constants.REDIRECT_URL, com.sina.weibo.Constants.SCOPE);
    	}
        if(mSsoHandler == null) {
        	mSsoHandler = new SsoHandler(this, mAuthInfo);
        }
    }
    
    @OnClick(R.id.btn_login_sina)
	private void loginBySina(View view) {
    	initSinaWeiBo();
    	mSsoHandler.authorize(new AuthListener());
	}
    
    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                ProgressDialogUtils.showProgressDialog(LoginActivity.this, "授权成功，正在处理中...");
                UsersAPI usersAPI = new UsersAPI(getApplicationContext(), com.sina.weibo.Constants.APP_KEY, mAccessToken);
                usersAPI.show(Long.parseLong(mAccessToken.getUid()), new RequestListener() {
					@Override
					public void onWeiboException(WeiboException arg0) {
						Log.e(TAG, "sina get userInfo fail", arg0);
						ProgressDialogUtils.dismiss();
						ToastUtils.showMessage(LoginActivity.this, R.string.weibosdk_demo_toast_auth_failed);
					}
					@Override
					public void onComplete(String arg0) {
						Log.e(TAG, arg0);
						/** 通过JSON获取到新浪微博用户信息,且解析相关内容信息 */
					    try {
							JSONObject jsonObj = new JSONObject(arg0);
							//uid
							String uid = jsonObj.getString("id");
							// 用户昵称
							String wb_screen_name = jsonObj.getString("screen_name");
							// 用户头像地址，50×50像素
							String wb_profile_image_url = jsonObj.getString("profile_image_url");
							mUserService.addSinaUser(mHandler, uid, wb_profile_image_url, wb_screen_name);
						} catch (JSONException e) {
							Log.e(TAG, "sina get userInfo fail", e);
							ProgressDialogUtils.dismiss();
						}
					}
				});
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                ProgressDialogUtils.dismiss();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, 
                   R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
            ProgressDialogUtils.dismiss();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
            ProgressDialogUtils.dismiss();
        }
    }
    
    @OnClick(R.id.forget_pwd)
    private void findPwd(View view) {
    	Intent intent = new Intent(this, FindPwdActivity1.class);
//		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivityWithAnimation(intent);
    }
    
    @Override
    protected void onPause() {
    	SMSSDK.unregisterEventHandler(mEventHandler);
    	MobclickAgent.onPageEnd("LoginActivity");
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	MobclickAgent.onPageStart("LoginActivity"); //统计页面
    	//初始化短信接口
        SMSSDK.initSDK(this, getString(R.string.sina_appid), getString(R.string.youmeng_appid));
    	SMSSDK.registerEventHandler(mEventHandler);
    	super.onResume();
    }
    
}
