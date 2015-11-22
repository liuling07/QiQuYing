package com.lling.qiqu.service;

import android.os.Handler;

/**
 * @ClassName: IMeiTuService
 * @Description: 美图接口
 * @author lling
 * @date 2015-6-27
 */
public interface IMeiTuService {

	/**
	 * 获取美图列表
	 * @param handler
	 * @param newOrHotFlag
	 * @param offset
	 * @param count
	 */
	public void getMeiTu(Handler handler, int newOrHotFlag, int offset, int count);
	
	/**
	 * 刷新美图
	 * @param handler
	 * @param newOrHotFlag
	 * @param offset
	 * @param count
	 */
	public void refresh(Handler handler, int newOrHotFlag, int count);
	
}
