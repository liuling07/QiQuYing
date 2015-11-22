package com.lling.qiqu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCacheListener;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lling.qiqu.App;
import com.lling.qiqu.R;


/**
 * @ClassName: BitmapUtil
 * @Description: 图片加载工具类
 * @author lling
 * @date 2015-7-10
 */
public class BitmapUtil {
	
	public static BitmapUtils mBitmapUtils;
	
	static {
		mBitmapUtils = new BitmapUtils(App.getInstance());
		mBitmapUtils.configDefaultLoadingImage(R.drawable.default_portrait);
		mBitmapUtils.configDefaultLoadFailedImage(R.drawable.default_portrait);
		mBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		mBitmapUtils.configMemoryCacheEnabled(true);
		mBitmapUtils.configDiskCacheEnabled(true);
	}
	
	/**
	 * 加载图片
	 * @param container
	 * @param uri
	 * @param callBack
	 */
	public static void display(ImageView container, String uri, BitmapLoadCallBack<ImageView> callBack) {
		mBitmapUtils.display(container, uri, callBack);
	}
	
	/**
	 * 加载图片
	 * @param container
	 * @param uri
	 */
	public static void display(ImageView container, String uri) {
		mBitmapUtils.display(container, uri);
	}
	
	/**
	 * 从缓存中获取原图
	 * @param uri
	 * @return
	 */
	public static Bitmap getBitmapFromCache(String uri) {
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setBitmapConfig(Bitmap.Config.ARGB_8888);
		config.setShowOriginal(true);
		Bitmap bitmap = mBitmapUtils.getBitmapFromMemCache(uri, config);
		if(bitmap != null) {
			return bitmap;
		}
		try {
			return BitmapFactory.decodeStream(new FileInputStream(mBitmapUtils.getBitmapFileFromDiskCache(uri)));
		} catch (FileNotFoundException e) {
			Log.e("getBitmapFileFromCache", "get bitmap error", e);
		}
		return null;
	}
	
	/**
	 * 获取缓存中图片的缩略图
	 * @param uri
	 * @return
	 */
	public static Bitmap getThumbBitmapFromCache(String uri, int width, int height) {
		BitmapDisplayConfig config = new BitmapDisplayConfig();
		config.setBitmapConfig(Bitmap.Config.ARGB_8888);
		config.setShowOriginal(false);
		BitmapSize bitmapSize = new BitmapSize(width, height);
		config.setBitmapMaxSize(bitmapSize);
		Bitmap bitmap = mBitmapUtils.getBitmapFromMemCache(uri, config);
		if(bitmap != null) {
			return bitmap;
		}
		try {
//			Bitmap bitmap2 = BitmapFactory.decodeStream(new FileInputStream());
			File file = mBitmapUtils.getBitmapFileFromDiskCache(uri);
	        BitmapFactory.Options options = new BitmapFactory.Options();  
	        options.inJustDecodeBounds = true;  
	        // 获取这个图片的宽和高，注意此处的bitmap为null  
	        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);  
	        options.inJustDecodeBounds = false; // 设为 false  
	        // 计算缩放比  
	        int h = options.outHeight;  
	        int w = options.outWidth;  
	        int beWidth = w / width;  
	        int beHeight = h / height;  
	        int be = 1;  
	        if (beWidth < beHeight) {  
	            be = beWidth;  
	        } else {  
	            be = beHeight;  
	        }  
	        if (be <= 0) {  
	            be = 1;  
	        }  
	        options.inSampleSize = be;  
	        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
	        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);  
	        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
	        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
	                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
	        return bitmap;
		} catch (Exception e) {
			Log.e("getBitmapFileFromCache", "get bitmap error", e);
		}
		return null;
	}
	
	/**
	 * 清除吃盘缓存
	 */
	public static void cleanDiskCache() {
		mBitmapUtils.clearDiskCache();
	}
	
	/**
	 * 清除内存缓存
	 */
	public static void cleanMemCache() {
		mBitmapUtils.clearMemoryCache();
	}
    
}
