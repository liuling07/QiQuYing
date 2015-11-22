package com.lling.qiqu.commons;

/**
 * 常量类
 * @author lling
 *
 */
public class Constants {
	
	public static final boolean DEBUG = false;

	//SplashActivity
	public static final String FIRST_USE = "firstUse";
	public static final String IS_RECEIVE_PUSH = "isReceivePush";
	public static final String IS_LOAD_IMG = "isLoadImg";
	
	//http请求超时时间
	public static final int REQUEST_TIME_OUT = 15 * 1000;
	
	public static final int SUCCESS = 200;
	public static final int FAILURE = 500;
	public static final int SUCCESS_1 = 201;
	public static final int FAILURE_1 = 501;
	public static final int SUCCESS_2 = 202;
	public static final int FAILURE_2 = 502;
	
	public static final String SERVER_EXCEPTION = "500";
	public static final String PARAM_INVALUD = "501";
	public static final String SOURCE_EXISTS = "502";
	public static final String SOURCE_NOT_EXISTS = "503";
	public static final String OTHER_ERROR = "504";
	
	public static final String UPLOADFILE_PRE = "http://7xjehg.com1.z0.glb.clouddn.com/";
	public static final String LOGP_URL = "http://7xjehg.com1.z0.glb.clouddn.com/logo.png";
	
	public static final String CACHE_ALL_NEW = "cache_all_new";
	public static final String CACHE_ALL_HOT = "cache_all_hot";
	public static final String CACHE_QUSHI_NEW = "cache_qushi_new";
	public static final String CACHE_QUSHI_HOT = "cache_qushi_hot";
	public static final String CACHE_QUTU_NEW = "cache_qutu_new";
	public static final String CACHE_QUTU_HOT = "cache_qutu_hot";
	public static final String CACHE_MEITU_NEW = "cache_meitu_new";
	public static final String CACHE_MEITU_HOT = "cache_meitu_hot";
	
	//广点通
	public static final boolean TESTAD = false;   //标记测试
	public static final String GDT_APPId = "1104671631";
	public static final String GDT_APPWallPosId = "9050601571008638";
	public static final String GDT_SPLASHPosId = "5040000561500800";
	public static final String GDT_BANNERPosId = "7040104593531094";
	public static final String GDT_MEITU_BANNERPosId = "1030305547810972";
	
	//验证码发送短信
	public static final String SMS_SENDER = "10657120610111";
	
}
