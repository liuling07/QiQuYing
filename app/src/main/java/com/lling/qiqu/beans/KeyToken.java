package com.lling.qiqu.beans;

import java.io.Serializable;

/**
 * 上传头像所需的token、key类，用于接收从服务器上传回来的值
 */
public class KeyToken implements Serializable {

	private String token;
	private String key;
	
	public KeyToken() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

}
