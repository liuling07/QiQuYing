package com.lling.qiqu.commons;

/**
 * @ClassName: RcpUri
 * @Description: 接口文件
 * @author lling
 * @date 2015-6-15
 */
public class RcpUri {
	public static final String BASEPATH = "http://192.168.0.104/QiQuYingServer/";
	//获取列表
	public static final String INTERFACE_URI_LIST = BASEPATH+"api/list";
	//获取趣事评论列表
	public static final String INTERFACE_URI_GET_COMMENTS = BASEPATH+"api/comments";
	
	public static final String INTERFACE_URI_GET_JOKE = BASEPATH+"api/get_joke";
	//顶
	public static final String INTERFACE_URI_DING = BASEPATH+"api/ding";
	//顶
	public static final String INTERFACE_URI_CAI = BASEPATH+"api/cai";
	//评论
	public static final String INTERFACE_URI_COMMENT = BASEPATH+"api/comment";
	
	//添加用户
	public static final String INTERFACE_URI_ADD_TENCENT_USER = BASEPATH+"user/tencent/add";
	public static final String INTERFACE_URI_ADD_SINA_USER = BASEPATH+"user/sina/add";
	//注册
	public static final String INTERFACE_URI_REGIST = BASEPATH+"user/regist";
	//登录
	public static final String INTERFACE_URI_LOGIN = BASEPATH+"user/login";
	//获取七牛上传token
	public static final String INTERFACE_URI_GET_QINIU_TOKEN = BASEPATH+"user/get_key_token";
	//设置用户头像
	public static final String INTERFACE_URI_SET_PORTRAIT = BASEPATH+"user/set_portrait";
	//设置用户昵称和性别
	public static final String INTERFACE_URI_SET_NICK_SEX = BASEPATH+"user/set_nick_sex";
	//用户反馈
	public static final String INTERFACE_URI_FEEDBACK = BASEPATH+"user/feedback";
	//验证用户是否已经注册
	public static final String INTERFACE_URI_CHECK_EXEIST = BASEPATH+"user/exeist";
	//重置密码
	public static final String INTERFACE_URI_RESET_PWD = BASEPATH+"user/reset_password";
	
	//分享链接
	public static final String INTERFACE_URI_SHARE = BASEPATH+"qushi/detail/";
}
