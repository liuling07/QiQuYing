package com.lling.qiqu.commons;

import java.io.Serializable;

/**
 * @ClassName: ResponseInfo
 * @Description: http接口返回数据封装类
 * @author lling
 * @date 2015-5-30
 */
public class ResponseInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String code;
	private String desc;
	private Object data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
}
