package com.lling.qiqu.service;

import android.os.Handler;

/**
 * @ClassName: IQuShiService
 * @Description: 趣事接口
 * @author lling
 * @date 2015-6-15
 */
public interface IQuTuService {

	/**
	 * 获取趣图列表
	 * @param handler
	 * @param newOrHotFlag
	 * @param offset
	 * @param count
	 */
	public void getQuTu(Handler handler, int newOrHotFlag, int offset, int count);
	
	/**
	 * 刷新趣图列表
	 * @param handler
	 * @param newOrHotFlag
	 * @param count
	 */
	public void refush(Handler handler, int newOrHotFlag, int count);
}
