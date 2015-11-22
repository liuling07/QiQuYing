package com.lling.qiqu.beans;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @ClassName: Praise
 * @Description: 本地顶和踩数据记录
 * @author lling
 * @date 2015-6-28
 */
@Table(name="user_ding_cai")
public class DingOrCai implements Serializable{
	
	private static final long serialVersionUID = -2565933558042038491L;
	//是否上传服务器标记
	public static final int NOT_UPLOAD = 0;
	public static final int UPLOAD = 1;
	
	//顶踩标记
	public static final int CAI = 0;
	public static final int DING = 1;
	
	/**
	 * id
	 */
	@Id(column="id")
	private int id;
	
	/**
	 * 笑话id
	 */
	@Column(column="joke_id")
	private int jokeId;
	
	/**
	 * 用户id，游客为-1
	 */
	@Column(column="user_id")
	private int userId;
	
	/**
	 * 标记顶和踩
	 * 1：顶
	 * 0：踩
	 */
	@Column(column="ding_or_cai")
	private int dingOrCai;
	
	/**
	 * 顶或者踩之后的数量
	 */
	@Column(column="num")
	private int num;
	
	/**
	 * 是否上传数据
	 * 1：是
	 * 0：否
	 */
	@Column(column="is_upload", defaultValue="0")
	private int isUpload;

	public int getJokeId() {
		return jokeId;
	}

	public void setJokeId(int jokeId) {
		this.jokeId = jokeId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDingOrCai() {
		return dingOrCai;
	}

	public void setDingOrCai(int dingOrCai) {
		this.dingOrCai = dingOrCai;
	}
	
	public boolean isDing() {
		return dingOrCai == DING;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	
}
