package com.lling.qiqu.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
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
import com.lling.qiqu.beans.DingOrCai;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.commons.PageResult;
import com.lling.qiqu.commons.RcpUri;
import com.lling.qiqu.dao.DingCaiDAO;
import com.lling.qiqu.interfaces.ApiCallBack;
import com.lling.qiqu.service.IJokeService;
import com.lling.qiqu.utils.FastjsonUtil;
import com.lling.qiqu.utils.HttpUtil;
import com.lling.qiqu.utils.TaskExecutor;
import com.lling.qiqu.utils.ToastUtils;
import com.tencent.open.utils.Util;

public class JokeServiceImpl implements IJokeService {
	private static final String TAG = "QuShiServiceImpl";
	private Context mContext;
	private DingCaiDAO mDingCaiDAO;
	
	public JokeServiceImpl(Context context) {
		mContext = context;
		mDingCaiDAO = new DingCaiDAO(context);
	}
	
	@Override
	public void getAll(final Handler handler, int newOrHotFlag, int offset, int count) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("newOrHotFlag", String.valueOf(newOrHotFlag));
		params.addQueryStringParameter("offset", String.valueOf(offset));
		params.addQueryStringParameter("count", String.valueOf(count));
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_LIST, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					try {
						String rs = responseInfo.result;
						Map map = FastjsonUtil.json2Map(rs);
						int code = Integer.parseInt(String.valueOf(map.get("code")));
						if (code != 200) {
							//请求失败
							handler.sendEmptyMessage(Constants.FAILURE);
							return;
						}
						PageResult page = FastjsonUtil.deserialize(
								map.get("data").toString(), PageResult.class);
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putSerializable("pageResult", page);
						message.what = Constants.SUCCESS;
						message.setData(bundle);
						handler.sendMessage(message);
					} catch (Exception e) {
						handler.sendEmptyMessage(Constants.FAILURE);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}

	@Override
	public void refushAll(final Handler handler, int newOrHotFlag, int count) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(0); //缓存超期时间0分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("newOrHotFlag", String.valueOf(newOrHotFlag));
		params.addQueryStringParameter("count", String.valueOf(count));
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_LIST, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					try {
						String rs = responseInfo.result;
						Map map = FastjsonUtil.json2Map(rs);
						int code = Integer.parseInt(String.valueOf(map.get("code")));
						if (code != 200) {
							//请求失败
							handler.sendEmptyMessage(Constants.FAILURE);
							return;
						}
						PageResult page = FastjsonUtil.deserialize(
								map.get("data").toString(), PageResult.class);
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putSerializable("pageResult", page);
						message.what = Constants.SUCCESS_1;
						message.setData(bundle);
						handler.sendMessage(message);
					} catch (Exception e) {
						handler.sendEmptyMessage(Constants.FAILURE);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}
	
	@Override
	public void getList(final Handler handler, int type, int newOrHotFlag, int offset,
			int count) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("type", String.valueOf(type));
		params.addQueryStringParameter("newOrHotFlag", String.valueOf(newOrHotFlag));
		params.addQueryStringParameter("offset", String.valueOf(offset));
		params.addQueryStringParameter("count", String.valueOf(count));
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_LIST, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					try {
						String rs = responseInfo.result;
						Map map = FastjsonUtil.json2Map(rs);
						int code = Integer.parseInt(String.valueOf(map.get("code")));
						if (code != 200) {
							//请求失败
							handler.sendEmptyMessage(Constants.FAILURE);
							return;
						}
						PageResult page = FastjsonUtil.deserialize(
								map.get("data").toString(), PageResult.class);
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putSerializable("pageResult", page);
						message.what = Constants.SUCCESS;
						message.setData(bundle);
						handler.sendMessage(message);
					} catch (Exception e) {
						handler.sendEmptyMessage(Constants.FAILURE);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}

	@Override
	public void refush(final Handler handler, int type, int newOrHotFlag, int count) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(0); //缓存超期时间0分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("type", String.valueOf(type));
		params.addQueryStringParameter("newOrHotFlag", String.valueOf(newOrHotFlag));
		params.addQueryStringParameter("count", String.valueOf(count));
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_LIST, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					try {
						String rs = responseInfo.result;
						Map map = FastjsonUtil.json2Map(rs);
						int code = Integer.parseInt(String.valueOf(map.get("code")));
						if (code != 200) {
							//请求失败
							handler.sendEmptyMessage(Constants.FAILURE);
							return;
						}
						PageResult page = FastjsonUtil.deserialize(
								map.get("data").toString(), PageResult.class);
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putSerializable("pageResult", page);
						message.what = Constants.SUCCESS_1;
						message.setData(bundle);
						handler.sendMessage(message);
					} catch (Exception e) {
						handler.sendEmptyMessage(Constants.FAILURE);
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}

	@Override
	public void getCommentByJokeId(final Handler handler, int jokeId, int offset,
			int count) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(60); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("qushiId", String.valueOf(jokeId));
		params.addQueryStringParameter("offset", String.valueOf(offset));
		params.addQueryStringParameter("count", String.valueOf(count));
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_GET_COMMENTS, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						handler.sendEmptyMessage(Constants.FAILURE);
						return;
					}
					PageResult page = FastjsonUtil.deserialize(
							map.get("data").toString(), PageResult.class);
					Message message = new Message();
					Bundle bundle = new Bundle();
					bundle.putSerializable("pageResult", page);
					message.what = Constants.SUCCESS;
					message.setData(bundle);
					handler.sendMessage(message);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}

	@Override
	public void ding(final List<DingOrCai> dingOrCais) {
		if(dingOrCais == null || dingOrCais.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (DingOrCai dingOrCai : dingOrCais) {
			sb.append(dingOrCai.getJokeId()).append(",");
		}
		String ids = sb.toString().substring(0, sb.toString().length() - 1);
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("ids", ids);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_DING, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						Log.e(TAG, "ding failure:" + map.get("desc"));
						return;
					}
					//修改本地数据库
					TaskExecutor.executeTask(new Runnable() {
						@Override
						public void run() {
							mDingCaiDAO.upload(dingOrCais);
						}
					});
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "ding failure", error);
				}
			});
	}

	@Override
	public void cai(final List<DingOrCai> dingOrCais) {
		if(dingOrCais == null || dingOrCais.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (DingOrCai dingOrCai : dingOrCais) {
			sb.append(dingOrCai.getJokeId()).append(",");
		}
		String ids = sb.toString().substring(0, sb.toString().length() - 1);
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("ids", ids);
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_CAI, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						Log.e(TAG, "cai failure:" + map.get("desc"));
						return;
					}
					//修改本地数据库
					TaskExecutor.executeTask(new Runnable() {
						@Override
						public void run() {
							mDingCaiDAO.upload(dingOrCais);
						}
					});
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "cai failure", error);
				}
			});
	}

	@Override
	public void addComment(final Handler handler, Integer jokeId, String content) {
		if(App.currentUser == null || Util.isEmpty(content)) {
			return;
		}
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(1000 * 60 * 10); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("jokeId", String.valueOf(jokeId));
		params.addQueryStringParameter("userId", String.valueOf(App.currentUser.getId()));
		params.addQueryStringParameter("userPortrait", App.currentUser.getPortraitUrl());
		try {
			params.addQueryStringParameter("userNick", 
					URLEncoder.encode(App.currentUser.getUserNike(), "UTF-8"));
			params.addQueryStringParameter("content", 
					URLEncoder.encode(content, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		http.send(HttpRequest.HttpMethod.POST,
			RcpUri.INTERFACE_URI_COMMENT, params,
			new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if (code != 200) {
						//请求失败
						Log.e(TAG, "comment failure");
						ToastUtils.showMessage(mContext, R.string.comment_fail);
						handler.sendEmptyMessage(Constants.FAILURE_1);
						return;
					}
					Log.e(TAG, "comment success");
					handler.sendEmptyMessage(Constants.SUCCESS_1);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "comment failure", error);
					handler.sendEmptyMessage(Constants.FAILURE_1);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						ToastUtils.showMessage(mContext, R.string.comment_fail);
					}
				}
			});
	}

	@Override
	public void getJokeById(final ApiCallBack apiCallBack, Integer jokeId) {
		HttpUtils http = new HttpUtils();
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		http.configDefaultHttpCacheExpiry(0); 
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("jokeId", String.valueOf(jokeId));
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_GET_JOKE, params,
			new RequestCallBack<String>() {
				@SuppressWarnings("rawtypes")
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					String rs = responseInfo.result;
					Map map = FastjsonUtil.json2Map(rs);
					int code = Integer.parseInt(String.valueOf(map.get("code")));
					if(code == Constants.SUCCESS) {
						Log.e(TAG, "getJokeById success");
						Joke joke = FastjsonUtil.deserialize(
								map.get("data").toString(), Joke.class);
						apiCallBack.onSuccess(joke);
					} else {
						Log.e(TAG, "getJokeById fail, fail msg is " + map.get("msg"));
						apiCallBack.onFailure(String.valueOf(code));
					}
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					Log.e(TAG, "getJokeById error", error);
					if(!HttpUtil.isNetworkAvailable(mContext)) {
						ToastUtils.showMessage(mContext, R.string.no_net);
					} else {
						apiCallBack.onError(error, msg);
					}
				}
			});
	}

}
