package com.lling.qiqu.service.impl;

import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.commons.PageResult;
import com.lling.qiqu.commons.RcpUri;
import com.lling.qiqu.service.IMeiTuService;
import com.lling.qiqu.utils.FastjsonUtil;

public class MeiTuServiceImpl implements IMeiTuService {

	private Context mContext;
	
	public MeiTuServiceImpl(Context context) {
		mContext = context;
	}
	
	@Override
	public void getMeiTu(final Handler handler, int newOrHotFlag, int offset,
			int count) {
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
	public void refresh(final Handler handler, int newOrHotFlag, int count) {
		HttpUtils http = new HttpUtils();
		http.configDefaultHttpCacheExpiry(0); //缓存超期时间10分钟
		http.configTimeout(Constants.REQUEST_TIME_OUT);  //设置超时时间
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("newOrHotFlag", String.valueOf(newOrHotFlag));
		params.addQueryStringParameter("count", String.valueOf(count));
		http.send(HttpRequest.HttpMethod.GET,
			RcpUri.INTERFACE_URI_LIST, params,
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
					message.what = Constants.SUCCESS_1;
					message.setData(bundle);
					handler.sendMessage(message);
				}
				@Override
				public void onFailure(HttpException error, String msg) {
					handler.sendEmptyMessage(Constants.FAILURE);
				}
			});
	}

}
