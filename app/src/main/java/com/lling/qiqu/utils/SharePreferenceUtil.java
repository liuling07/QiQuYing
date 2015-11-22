package com.lling.qiqu.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

/**
 * @ClassName: SharePreferenceUtil
 * @Description: SharePreference数据存取工具类
 * @author lling
 * @date 2015-6-7
 */
public class SharePreferenceUtil {
	private static final String TAG  = "SharePreferenceUtil";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
	}
	
	/**
	 * 存取字符串值
	 * @param key
	 * @param value
	 */
	public void putString(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.apply();
	}
	
	/**
	 * 获取字符串值
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}
	
	/**
	 * 设置boolean值
	 * @param key
	 * @param value
	 */
	public void putBoolean(String key, boolean value) {
		editor = sp.edit();
		editor.putBoolean(key, value);
		editor.apply();
	}
	
	/**
	 * 根据key获取boolean类型值
	 * @param key
	 * @param defValue
	 * @return
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * 设置int值
	 * @param key
	 * @param value
	 */
	public void putInt(String key, int value) {
		editor = sp.edit();
		editor.putInt(key, value);
		editor.apply();
	}
	
	/**
	 * 根据key获取int值
	 * @param key
	 * @param defValue
	 * @return
	 */
	public int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	/**
	 * 设置float值
	 * @param key
	 * @param value
	 */
	public void putFloat(String key, float value) {
		editor = sp.edit();
		editor.putFloat(key, value);
		editor.apply();
	}
	
	/**
	 * 根据key获取float值
	 * @param key
	 * @param defValue
	 * @return
	 */
	public float getFloat(String key, float defValue) {
		return sp.getFloat(key, defValue);
	}
	
	/**
	 * 设置long值
	 * @param key
	 * @param value
	 */
	public void putLong(String key, long value) {
		editor = sp.edit();
		editor.putLong(key, value);
		editor.apply();
	}
	
	/**
	 * 根据key获取long值
	 * @param key
	 * @param defValue
	 * @return
	 */
	public long getLong(String key, long defValue) {
		return sp.getLong(key, defValue);
	}
	
	/**
	 * SharedPreferences保存对象
	 * @param key
	 * @param object
	 */
	public void putObject(String key, Object object) {
		String objectBase64 = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			objectBase64 = Base64.encodeToString(baos.toByteArray(),
					Base64.DEFAULT);
			editor = sp.edit();
			editor.putString(key, objectBase64);
			Log.e(TAG, "put object ok~! ");
			editor.apply();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "pub object error", e);
		}
	}

	/**
	 * SharedPreferences保存对象,立马提交
	 * @param key
	 * @param object
	 */
	public void putObjectImmediately(String key, Object object) {
		String objectBase64 = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			objectBase64 = Base64.encodeToString(baos.toByteArray(),
					Base64.DEFAULT);
			editor = sp.edit();
			editor.putString(key, objectBase64);
			Log.e(TAG, "put object ok~! ");
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "pub object error", e);
		}
	}
	
	/**
	 * SharedPreferences取得对象
	 * @param context
	 * @param key
	 * @return
	 */
	public Object getObject(String key, Object defValue) {
		try {
			String objectBase64 = this.getString(key, "");
			if("".equals(objectBase64)) {
				return defValue;
			}
			byte[] base64Bytes = Base64.decode(objectBase64.getBytes(),
					Base64.DEFAULT);
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object object = ois.readObject();
			return object;
		} catch (Exception e) {// 发生异常情况下清空对应缓存
			this.putObject(key, "");
			Log.e(TAG, "getObject error", e);
			return defValue;
		}
	}
	
	/**
	 * SharedPreferences根据键清除对象
	 * @param key
	 * @return
	 */
	public void remove(String key) {
		editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
	
}
