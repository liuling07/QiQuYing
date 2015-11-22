package com.lling.qiqu.service;

import com.lling.qiqu.interfaces.ApiCallBack;

import android.os.Handler;

/**
 * @ClassName: IUserService
 * @Description: 用户service
 * @author lling
 * @date 2015年6月30日
 */
public interface IUserService {

	/**
	 * 增加腾讯QQ用户
	 * @param handler
	 * @param portraitUrl
	 * @param nickName
	 * @param sex
	 */
	public void addTencentUser(Handler handler, String openId, String portraitUrl, String nickName, int sex);
	
	/**
	 * 增加新浪微博用户
	 * @param handler
	 * @param uid
	 * @param portraitUrl
	 * @param nickName
	 */
	public void addSinaUser(Handler handler, String uid, String portraitUrl, String nickName);
	
	/**
	 * 用户注册
	 * @param handler
	 * @param email
	 * @param password
	 */
	public void regist(Handler handler, String email, String password);
	
	/**
	 * 回调方式注册
	 * @param apiCallBack
	 * @param email
	 * @param password
	 */
	public void regist(ApiCallBack apiCallBack, String email, String password);
	
	/**
	 * 用户登录
	 * @param handler
	 * @param email
	 * @param password
	 */
	public void login(Handler handler, String email, String password);
	
	/**
	 * 获取七牛上传token
	 */
	public void getQiNiuToken(Handler handler);
	
	/**
	 * 设置用户头像
	 * @param handler
	 * @param userId
	 * @param portraitURL
	 */
	public void setPortrait(Handler handler, String userId, String portraitURL);
	
	/**
	 * 设置昵称和性别
	 * @param handler
	 * @param nickName
	 * @param sex
	 */
	public void setNickAndSex(Handler handler, String userId, String nickName, String sex);
	
	/**
	 * 用户反馈
	 * @param handler
	 * @param content
	 * @param contact
	 * @param imgUrl
	 */
	public void feedback(Handler handler, String content, String contact, String imgUrl);
	
	/**
	 * 验证用户是否注册
	 * @param phone
	 * @param apiCallBack
	 */
	public void checkUserExeist(String phone, ApiCallBack apiCallBack);
	
	/**
	 * 重置密码
	 * @param phone
	 * @param password
	 * @param apiCallBack
	 */
	public void reSetPassword(String phone, String password, ApiCallBack apiCallBack);
}
