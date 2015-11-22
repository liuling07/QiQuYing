package com.lling.qiqu.service;

import java.util.List;

import com.lling.qiqu.beans.DingOrCai;
import com.lling.qiqu.interfaces.ApiCallBack;

import android.os.Handler;

/**
 * @ClassName: IQuShiService
 * @Description: 趣事接口
 * @author lling
 * @date 2015-6-15
 */
public interface IJokeService {

	/**
	 * 获取趣事列表
	 * @param handler
	 * @param newOrHotFlag
	 * @param offset
	 * @param count
	 */
	public void getList(Handler handler, int type, int newOrHotFlag, int offset, int count);
	
	/**
	 * 刷新趣事列表
	 * @param handler
	 * @param newOrHotFlag
	 * @param count
	 */
	public void refush(Handler handler, int type, int newOrHotFlag, int count);
	
	/**
	 * 获取所有内容列表
	 * @param handler
	 * @param newOrHotFlag
	 * @param offset
	 * @param count
	 */
	public void getAll(Handler handler, int newOrHotFlag, int offset, int count);
	
	/**
	 * 刷新所有内容列表
	 * @param handler
	 * @param newOrHotFlag
	 * @param count
	 */
	public void refushAll(Handler handler, int newOrHotFlag, int count);
	
	/**
	 * 根据jokeid获取评论列表
	 * @param handler
	 * @param jokeId
	 * @param offset
	 * @param count
	 */
	public void getCommentByJokeId(Handler handler, int jokeId, int offset, int count);
	
	/**
	 * 顶joke
	 * @param dingOrCais
	 */
	public void ding(List<DingOrCai> dingOrCais);
	
	/**
	 * 踩joke
	 * @param dingOrCais
	 */
	public void cai(List<DingOrCai> dingOrCais);
	
	/**
	 * 用户评论joke
	 * @param handler
	 * @param jokeId
	 * @param userId
	 * @param userPortrait
	 * @param userNick
	 * @param content
	 */
	public void addComment(Handler handler, Integer jokeId, String content);
	
	/**
	 * 根据jokeId获取joke对象
	 * @param apiCallBack
	 * @param jokeId
	 */
	public void getJokeById(ApiCallBack apiCallBack, Integer jokeId);
}
