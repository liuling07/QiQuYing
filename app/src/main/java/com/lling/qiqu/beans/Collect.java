package com.lling.qiqu.beans;

import java.io.Serializable;
import java.util.Date;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @ClassName: Collect
 * @Description: 用户搜藏
 * @author lling
 * @date 2015年7月13日
 */
@Table(name="user_collect")
public class Collect implements Serializable{
	
	private static final long serialVersionUID = -6295764401529594309L;
	//是否上传服务器标记
	public static final int NOT_UPLOAD = 0;
	public static final int UPLOAD = 1;
	
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
	 * 是否上传数据
	 * 1：是
	 * 0：否
	 */
	@Column(column="is_upload", defaultValue="0")
	private int isUpload;

	/**
	 * 收藏的joke的序列化对象
	 */
	@Column(column="joke_content")
	private String jokeContent;
	
	@Column(column="create_at")
    protected Date createAt;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getJokeContent() {
		return jokeContent;
	}

	public void setJokeContent(String jokeContent) {
		this.jokeContent = jokeContent;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
}
