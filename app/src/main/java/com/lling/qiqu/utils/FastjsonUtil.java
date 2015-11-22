/**  
* Filename:    FastjsonUtil.java  
* Description:   
* Copyright:   Copyright (c)2014  
* Company:    bbk 
* @author:     Issac 
* @version:    1.0  
* Create at:   2014-8-15 下午06:52:56  
*  
* Modification History:  
* Date           Author       Version      Description  
* ------------------------------------------------------------------  
* 2014-8-15    Issac       1.0        1.0 Version  
*/

package com.lling.qiqu.utils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

/** 
 * @ClassName: FastjsonUtil 
 * @Description: TODO
 * @author Issac
 * @date 2014-8-15 下午06:52:56 
 *  
 */

public class FastjsonUtil {
	
	private static SerializeConfig mapping = new SerializeConfig(); static { 
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss")); 
	} 

	/** 
	 * @Title: main 
	 * @Description: TODO 
	 * @param @param args  
	 * @return void 
	 */

	public static void main(String[] args) {
	}
	
	/**
      * 将java类型的对象转换为JSON格式的字符串
      * @param object java类型的对象
      * @return JSON格式的字符串
    */
    public static <T> String serialize(T object) {
        return JSON.toJSONString(object);
    }
    
    /**
     * 将JSON格式的字符串转换成任意Java类型的对象或者java数组类型的对象，不包括java集合类型
     * @param json JSON格式的字符串
     * @param clz java类型或者java数组类型，不包括java集合类型
     * @return java类型的对象或者java数组类型的对象，不包括java集合类型的对象
     */
    public static <T> T deserialize(String json, Class<T> clz) {
       return JSON.parseObject(json, clz);
    }
	
    /**
    * 将JSON格式的字符串转换成任意Java类型的对象
    * @param json JSON格式的字符串
    * @param type 任意Java类型
    * @return 任意Java类型的对象
    */
    public static <T> T deserializeAny(String json, TypeReference<T> type) {
       return JSON.parseObject(json, type);
    }
   
    /**
    * 将JSON格式的字符串转换成任意Java类型的对象
    * @param json JSON格式的字符串
    * @param type 任意Java类型
    * @return 任意Java类型的对象
    */
    public static <T> List<T> deserializeList(String json, Class<T> clz) {
       return JSON.parseArray(json, clz);
    }
	
    /**
    * 将JSON格式的字符串转换为List<T>类型的对象
    * @param json JSON格式的字符串
    * @param clz 指定泛型集合里面的T类型
    * @return List<T>类型的对象
    */
    public static <T> List<T> json2List(String json, Class<T> clz){ 
        //JSON array -> List 
	   return JSON.parseArray(json, clz); 
    } 
	
    /**
     * 将JSON格式的字符串转换为Map类型的对象
     * @param json JSON格式的字符串
     * @return Map类型的对象
     */
	public static Map json2Map(String json){ 
		//JSON -> Map 
		return (Map)JSON.parse(json); 
	} 
	
	/**
     * 将数组格式的转换为String类型的对象
     * @param 数组格式的字符串
     * @return String类型的对象
     */
	public static String array2json(Object[] obj){ 
		return JSON.toJSONString(obj,true); 
	} 
	
	/**
     * 将Map格式的转换为String类型的对象
     * @param Map格式的字符串
     * @return String类型的对象
     */
	public static String map2json(Map map){ 
	    return JSON.toJSONString(map); 
	} 
	
	/**
     * 将List格式的转换为String类型的对象
     * @param List格式的字符串
     * @return String类型的对象
     */
	public static String list2json(List list){ 
	    return JSON.toJSONString(list); 
	} 

}
