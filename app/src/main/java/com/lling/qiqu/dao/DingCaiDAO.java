package com.lling.qiqu.dao;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lling.qiqu.beans.DingOrCai;

/**
 * @ClassName: PraiseDAO
 * @Description: 顶踩功能DAO
 * @author lling
 * @date 2015-6-28
 */
public class DingCaiDAO {
	
	private static final String TAG = "PraiseDAO";
	private Context context;
	public DingCaiDAO(Context context) {
		this.context = context;
	}
	
	/**
	 * 检查是否点过赞
	 * @param userId
	 * @param jokeId
	 * @return
	 */
	public DingOrCai getDingOrCai(int userId, int jokeId) {
		DbUtils db = DbUtils.create(context);
		DingOrCai dingOrCai = null;
		try {
			dingOrCai = db.findFirst(Selector.from(DingOrCai.class).where(WhereBuilder.b("user_id", "=", userId).and("joke_id", "=", jokeId)));
			Log.d(TAG, "getDingOrCai success");
		} catch (DbException e) {
			Log.d(TAG, "getDingOrCai failure", e);
		}
		return dingOrCai;
	}
	
	/**
	 * 用户顶或者踩
	 * @param userId
	 * @param jokeId
	 * @param dingOrCai  标记是顶是踩
	 */
	public void dingOrCai(int userId, int jokeId, int dingOrCai, int num) {
		DbUtils db = DbUtils.create(context);
		DingOrCai praise = new DingOrCai();
		praise.setJokeId(jokeId);
		praise.setUserId(userId);
		praise.setDingOrCai(dingOrCai);
		praise.setNum(num);
		praise.setIsUpload(DingOrCai.NOT_UPLOAD);
		try {
			db.save(praise);
			Log.d(TAG, "dingOrCai success");
		} catch (DbException e) {
			Log.d(TAG, "dingOrCai failure", e);
		}
	}
	
	/**
	 * 查找未同步到服务器的点赞数据
	 * @return
	 */
	public List<DingOrCai> getUnUpload() {
		DbUtils db = DbUtils.create(context);
		List<DingOrCai> dbModels = null;
		try {
			dbModels = db.findAll(Selector.from(DingOrCai.class).where(WhereBuilder.b("is_upload", "=", DingOrCai.NOT_UPLOAD)));
			Log.d(TAG, "getUnUpload success");
		} catch (DbException e) {
			Log.d(TAG, "getUnUpload failure", e);
		}
		return dbModels;
	}
	
	/**
	 * 修改同步后的数据
	 * @param praises
	 */
	public void upload(List<DingOrCai> praises) {
		for (DingOrCai praise : praises) {
			praise.setIsUpload(DingOrCai.UPLOAD);
		}
		DbUtils db = DbUtils.create(context);
		try {
			db.updateAll(praises, "is_upload");
			Log.d(TAG, "upload praise success");
		} catch (DbException e) {
			Log.d(TAG, "upload praise failure", e);
		}
	}
	
}
