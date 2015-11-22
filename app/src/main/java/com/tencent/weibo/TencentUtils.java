package com.tencent.weibo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.lling.qiqu.App;
import com.lling.qiqu.utils.SharePreferenceUtil;
import com.tencent.tauth.Tencent;

/**
 * @ClassName: TencentUtils
 * @Description: 腾讯开放接口工具类
 * @author lling
 * @date 2015-8-22
 */
public class TencentUtils {
	
	/**
	 * 初始化mTencent
	 * @param context
	 * @param spUtil
	 */
	public static void initTencent(Context context, SharePreferenceUtil spUtil) {
		try {
			ApplicationInfo appInfo = context.getPackageManager()
			        .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			int appId = appInfo.metaData.getInt("TECENT_APPID", -1);
			if(appId == -1) {
				return;
			}
			App.mTencent = Tencent.createInstance(String.valueOf(appId), 
					context);
			String openId = spUtil.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID, null);
			String token = spUtil.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN, null);
			long expires = spUtil.getLong(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN, System.currentTimeMillis());
			if(openId != null) {
				App.mTencent.setOpenId(openId);
			}
			if(token != null) {
				App.mTencent.setAccessToken(token, String.valueOf((expires-System.currentTimeMillis())/1000));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
