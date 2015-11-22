package com.lling.qiqu.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.beans.User;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.commons.RcpUri;
import com.lling.qiqu.interfaces.ApiCallBack;
import com.lling.qiqu.service.IUserService;
import com.lling.qiqu.utils.FastjsonUtil;
import com.lling.qiqu.utils.HttpUtil;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.Util;

public class UserServiceImpl implements IUserService {
	
	private static final String TAG = "UserServiceImpl";

	private Context mContext;
	
	public UserServiceImpl(Context context) {
		this.mContext = context;
	}
	
	@Override
	public void addTencentUser(final Handler handler, String openId, String portraitUrl, String nickName,
			int sex) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("openId", openId);
		params.addQueryStringParameter("portraitUrl", portraitUrl);
		try {
			params.addQueryStringParameter("nickName", URLEncoder.encode(nickName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		params.addQueryStringParameter("sex", String.valueOf(sex));
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_ADD_TENCENT_USER, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						handler.sendEmptyMessage(Constants.FAILURE);
						Log.e(TAG, "save user failure");
						return;
					}
					Log.e(TAG, "save user success");
					User user = FastjsonUtil.deserialize(
							map.get("data").toString(), User.class);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					message.what = Constants.SUCCESS;
					message.setData(bundle);
					handler.sendMessage(message);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "save user failure", error);
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}
	
	@Override
	public void addSinaUser(final Handler handler, String uid, String portraitUrl,
			String nickName) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("uid", uid);
		params.addQueryStringParameter("portraitUrl", portraitUrl);
		try {
			params.addQueryStringParameter("nickName", URLEncoder.encode(nickName, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_ADD_SINA_USER, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						if(handler != null) {
							handler.sendEmptyMessage(Constants.FAILURE_2);
						}
						Log.e(TAG, "save user failure");
						return;
					}
					Log.e(TAG, "save user success");
					User user = FastjsonUtil.deserialize(
							map.get("data").toString(), User.class);
					if(handler != null) {
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putSerializable("user", user);
						message.what = Constants.SUCCESS_2;
						message.setData(bundle);
						handler.sendMessage(message);
					} else {
						App.currentUser = user;
						App.getInstance().getSpUtil().putObject("user", user);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "save user failure", error);
					if(handler != null) {
						handler.sendEmptyMessage(Constants.FAILURE_2);
					}
				}
			});
	}

	@Override
	public void regist(final Handler handler, String email, String password) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("userName", email);
		params.addQueryStringParameter("password", password);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_REGIST, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						handler.sendEmptyMessage(Constants.FAILURE_1);
						Log.e(TAG, "regist failure");
						if(code == 502) {  //用户名存在
							ToastUtils.showMessage(mContext, R.string.phone_exists);
						}
						if(code == 504) {  //注册失败
							ToastUtils.showMessage(mContext, R.string.regist_fail);
						}
						return;
					}
					Log.e(TAG, "regist success");
					User user = FastjsonUtil.deserialize(
							map.get("data").toString(), User.class);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					message.what = Constants.SUCCESS_1;
					message.setData(bundle);
					handler.sendMessage(message);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "regist failure", error);
					handler.sendEmptyMessage(Constants.FAILURE_1);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						ToastUtils.showMessage(mContext, R.string.regist_fail);
					}
				}
			});
	}
	
	@Override
	public void regist(final ApiCallBack apiCallBack, String email, String password) {
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("userName", email);
		params.addQueryStringParameter("password", password);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_REGIST, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						apiCallBack.onFailure(String.valueOf(map.get("code")));
						Log.e(TAG, "regist failure");
						if(code == 502) {  //用户名存在
							ToastUtils.showMessage(mContext, R.string.phone_exists);
						}
						if(code == 504) {  //注册失败
							ToastUtils.showMessage(mContext, R.string.regist_fail);
						}
						return;
					}
					Log.e(TAG, "regist success");
					User user = FastjsonUtil.deserialize(
							map.get("data").toString(), User.class);
					apiCallBack.onSuccess(user);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "regist failure", error);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						apiCallBack.onError(error, msg);
					}
				}
			});
	}

	@Override
	public void login(final Handler handler, String email, String password) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("userName", email);
		params.addQueryStringParameter("password", password);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_LOGIN, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						handler.sendEmptyMessage(Constants.FAILURE);
						Log.e(TAG, "login failure");
						if(code == 503) {  //用户名不存在
							ToastUtils.showMessage(mContext, R.string.no_account);
							return;
						}
						if(code == 504) {  //登录密码错误
							ToastUtils.showMessage(mContext, R.string.password_error);
							return;
						}
						ToastUtils.showMessage(mContext, R.string.login_fail);
					}
					Log.e(TAG, "login success");
					User user = FastjsonUtil.deserialize(
							map.get("data").toString(), User.class);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("user", user);
					message.what = Constants.SUCCESS;
					message.setData(bundle);
					handler.sendMessage(message);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "login failure", error);
					handler.sendEmptyMessage(Constants.FAILURE);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						ToastUtils.showMessage(mContext, R.string.login_fail);
					}
				}
			});
	}

	@Override
	public void getQiNiuToken(final Handler handler) {
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_GET_QINIU_TOKEN, null,
			new RequestCallBack<String>() {
				@SuppressWarnings("rawtypes")
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if(code == Constants.SUCCESS) {
						String data = map.get("data").toString();
						Message msg = new Message();
						msg.what = Constants.SUCCESS;
						msg.obj = data;
						handler.sendMessage(msg);
					} else {
						Log.e(TAG, "get keyToken fail, fail msg is " + map.get("msg"));
						handler.sendEmptyMessage(Constants.FAILURE);
						ToastUtils.showMessage(mContext,
								R.string.set_portrait_fail);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "get keyToken error", error);
					handler.sendEmptyMessage(Constants.FAILURE);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						ToastUtils.showMessage(mContext,
								R.string.set_portrait_fail);
					}
				}
			});
	}

	@Override
	public void setPortrait(final Handler handler, String userId, String portraitURL) {
		Log.e(TAG, "setPortrait");
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("userId", userId);
		params.addQueryStringParameter("portraitURL", portraitURL);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_SET_PORTRAIT, params,
			new RequestCallBack<String>() {
				@SuppressWarnings("rawtypes")
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if(code == Constants.SUCCESS) {
						Log.e(TAG, "set portrait success");
						handler.sendEmptyMessage(Constants.SUCCESS_1);
					} else {
						Log.e(TAG, "set portrait fail, fail msg is " + map.get("msg"));
						handler.sendEmptyMessage(Constants.FAILURE_1);
						ToastUtils.showMessage(mContext,
								R.string.set_portrait_fail);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "set portrait error", error);
					handler.sendEmptyMessage(Constants.FAILURE_1);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						ToastUtils.showMessage(mContext,
								R.string.set_portrait_fail);
					}
				}
			});
	}

	@Override
	public void setNickAndSex(final Handler handler, String userId, String nickName, String sex) {
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("userId", userId);
		if(nickName != null) {
			params.addQueryStringParameter("nickName", nickName);
		}
		if(sex != null) {
			params.addQueryStringParameter("sex", sex);
		}
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_SET_NICK_SEX, params,
			new RequestCallBack<String>() {
				@SuppressWarnings("rawtypes")
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if(code == Constants.SUCCESS) {
						Log.e(TAG, "set nickName sex success");
						handler.sendEmptyMessage(Constants.SUCCESS_2);
					} else {
						Log.e(TAG, "set nickName sex fail, fail msg is " + map.get("msg"));
						handler.sendEmptyMessage(Constants.FAILURE_2);
						ToastUtils.showMessage(mContext,
								R.string.set_nick_sex_fial);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "set nickName sex error", error);
					handler.sendEmptyMessage(Constants.FAILURE_2);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						ToastUtils.showMessage(mContext,
								R.string.set_nick_sex_fial);
					}
				}
			});
	}

	@Override
	public void feedback(final Handler handler, String content, String contact, String imgUrl) {
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		try {
			params.addQueryStringParameter("content", URLEncoder.encode(content, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(!Util.isNotEmpty(contact)) {
			params.addQueryStringParameter("contactWay", contact);
		}
		if(Util.isNotEmpty(imgUrl)) {
			params.addQueryStringParameter("imgUrl", imgUrl);
		}
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_FEEDBACK, params,
			new RequestCallBack<String>() {
				@SuppressWarnings("rawtypes")
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if(code == Constants.SUCCESS) {
						Log.e(TAG, "feedback success");
						ToastUtils.showMessage(mContext,
								R.string.feedback_success);
						handler.sendEmptyMessage(Constants.SUCCESS);
					} else {
						Log.e(TAG, "feedback fail, fail msg is " + map.get("msg"));
						handler.sendEmptyMessage(Constants.FAILURE);
						ToastUtils.showMessage(mContext,
								R.string.feedback_fail);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "feedback error", error);
					handler.sendEmptyMessage(Constants.FAILURE);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						ToastUtils.showMessage(mContext,
								R.string.feedback_fail);
					}
				}
			});
	}

	@Override
	public void checkUserExeist(String phone, final ApiCallBack apiCallBack) {
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		http.configDefaultHttpCacheExpiry(0); 
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("phone", phone);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_CHECK_EXEIST, params,
			new RequestCallBack<String>() {
				@SuppressWarnings("rawtypes")
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if(code == Constants.SUCCESS) {
						Log.e(TAG, "checkUserExeist success");
						apiCallBack.onSuccess(code);
					} else {
						Log.e(TAG, "checkUserExeist fail, fail msg is " + map.get("msg"));
						apiCallBack.onFailure(String.valueOf(code));
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "checkUserExeist error", error);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						apiCallBack.onError(error, msg);
					}
				}
			});
	}

	@Override
	public void reSetPassword(String phone, String password,
			final ApiCallBack apiCallBack) {
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		http.configDefaultHttpCacheExpiry(0); 
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("phone", phone);
		params.addQueryStringParameter("password", password);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_RESET_PWD, params,
			new RequestCallBack<String>() {
				@SuppressWarnings("rawtypes")
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if(code == Constants.SUCCESS) {
						Log.e(TAG, "reSetPassword success");
						User user = FastjsonUtil.deserialize(
								map.get("data").toString(), User.class);
						apiCallBack.onSuccess(user);
					} else {
						Log.e(TAG, "reSetPassword fail, fail msg is " + map.get("msg"));
						apiCallBack.onFailure(String.valueOf(code));
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "reSetPassword error", error);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						apiCallBack.onError(error, msg);
					}
				}
			});
	}

}
