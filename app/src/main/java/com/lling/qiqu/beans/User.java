package com.lling.qiqu.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: User
 * @Description: 用户实体类
 * @author lling
 * @date 2015-5-29
 */
public class User implements Serializable {

	private static final long serialVersionUID = -1371659645448329097L;

	/**
	 * 用户id
	 */
	private int id;
	
	/**
	 * 用户名,手机号码
	 */
	private String userName;
	
	/**
	 * 用户登陆密码
	 * 加上transient关键字让改字段不参与序列化
	 */
	private transient String password;
	
	/**
	 * 用户昵称
	 */
	private String userNike;
	
	/**
	 * 用户性别(0：女、1：男)
	 */
	private int sex;
	
	/**
	 * 用户头像URL
	 */
	private String portraitUrl;
	
	/**
	 * 是否接受推送消息（0：否、1：是）
	 */
	private int isReceivePush;
	
	/**
	 * 是否禁用（0：否、1：是）
	 */
	private int isForbid;

	/**
	 * 创建日期
	 */
	private Date createDate;
	
	/**
	 * 腾讯openId
	 */
	private String tecentOpenId;
	
	/**
	 * 新浪uid
	 */
	private String sinaUid;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserNike() {
		return userNike;
	}

	public void setUserNike(String userNike) {
		this.userNike = userNike;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPortraitUrl() {
		return portraitUrl;
	}

	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}

	public int getIsReceivePush() {
		return isReceivePush;
	}

	public void setIsReceivePush(int isReceivePush) {
		this.isReceivePush = isReceivePush;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getIsForbid() {
		return isForbid;
	}

	public void setIsForbid(int isForbid) {
		this.isForbid = isForbid;
	}
	
	public String getTecentOpenId() {
		return tecentOpenId;
	}

	public void setTecentOpenId(String tecentOpenId) {
		this.tecentOpenId = tecentOpenId;
	}

	public String getSinaUid() {
		return sinaUid;
	}

	public void setSinaUid(String sinaUid) {
		this.sinaUid = sinaUid;
	}
	
}
