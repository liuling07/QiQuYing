package com.lling.qiqu.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: Comment
 * @Description: 笑话评论表
 * @author lling
 * @date 2015-5-29
 */
public class Comment implements Serializable {

	private static final long serialVersionUID = -8116039067400484215L;

	/**
	 * 评论id
	 */
	private int id;
	
	/**
	 * 评论笑话id
	 */
	private int jokeId;
	
	/**
	 * 评论内容
	 */
	private String content;
	
	/**
	 * 评论支持数
	 */
	private int supportsNum;
	
	/**
	 * 创建时间
	 */
	private Date createDate;
	
	/**
	 * 评论用户id
	 */
	private int userId;
	
	/**
	 * 评论用户头像
	 */
	private String portraitUrl;

	/**
	 * 评论用户昵称
	 */
	private String userNike;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getSupportsNum() {
		return supportsNum;
	}

	public void setSupportsNum(int supportsNum) {
		this.supportsNum = supportsNum;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPortraitUrl() {
		return portraitUrl;
	}

	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}

	public String getUserNike() {
		return userNike;
	}

	public void setUserNike(String userNike) {
		this.userNike = userNike;
	}
	
}
