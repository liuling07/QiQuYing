package com.lling.qiqu.dao;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lling.qiqu.beans.Collect;
import com.lling.qiqu.beans.DingOrCai;

/**
 * @ClassName: PraiseDAO
 * @Description: 顶踩功能DAO
 * @author lling
 * @date 2015-6-28
 */
public class CollectDAO {
	
	private static final String TAG = "CollectDAO";
	private Context context;
	public CollectDAO(Context context) {
		this.context = context;
	}
	
	/**
	 * 检查是否收藏过
	 * @param userId
	 * @param jokeId
	 * @return
	 */
	public Collect getCollect(int userId, int jokeId) {
		DbUtils db = DbUtils.create(context);
		Collect collect = null;
		try {
			collect = db.findFirst(Selector.from(Collect.class).where(WhereBuilder.b("user_id", "=", userId).and("joke_id", "=", jokeId)));
			Log.d(TAG, "getDingOrCai success");
		} catch (DbException e) {
			Log.e(TAG, "getDingOrCai failure", e);
		}
		return collect;
	}
	
	/**
	 * 用户收藏
	 * @param userId
	 * @param jokeId
	 * @param jokeContent
	 */
	public void collect(int userId, int jokeId, String jokeContent) {
		DbUtils db = DbUtils.create(context);
		Collect collect = new Collect();
		collect.setUserId(userId);
		collect.setJokeId(jokeId);
		collect.setJokeContent(jokeContent);
		collect.setIsUpload(DingOrCai.NOT_UPLOAD);
		collect.setCreateAt(new Date());
		try {
			db.save(collect);
			Log.d(TAG, "collect success");
		} catch (DbException e) {
			Log.e(TAG, "collect failure", e);
		}
	}
	
	/**
	 * 取消收藏
	 * @param jokeId
	 */
	public void cancelCollect(int jokeId) {
		DbUtils db = DbUtils.create(context);
		try {
			db.delete(Collect.class, WhereBuilder.b("joke_id", "=", jokeId));
			Log.d(TAG, "cancelCollect success");
		} catch (DbException e) {
			Log.e(TAG, "cancelCollect failure", e);
		}
	}
	
	/**
	 * 获取我的收藏
	 * @param userId
	 * @return
	 */
	public List<Collect> getCollects(int userId) {
		DbUtils db = DbUtils.create(context);
		List<Collect> dbModels = null;
		try {
			dbModels = db.findAll(Selector.from(Collect.class).
					where(WhereBuilder.b("user_id", "=", userId))
					.orderBy("create_at", true));
			Log.d(TAG, "getCollects success");
		} catch (DbException e) {
			Log.e(TAG, "getCollects failure", e);
		}
		return dbModels;
	}
	
}
