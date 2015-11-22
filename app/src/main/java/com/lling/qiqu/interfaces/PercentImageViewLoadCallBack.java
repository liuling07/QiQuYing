package com.lling.qiqu.interfaces;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lling.qiqu.ui.LoadPercentImageView;

/**
 * @ClassName: PercentImageViewLoadCallBack
 * @Description: 图片加载回调
 * @author lling
 * @date 2015-7-12
 */
public class PercentImageViewLoadCallBack extends BitmapLoadCallBack<ImageView> {
	
	@Override
	public void onLoadFailed(ImageView arg0, String arg1, Drawable arg2) {
		LoadPercentImageView imageView = (LoadPercentImageView)arg0;
		imageView.setProgress(0);
	}
	@Override
	public void onLoadCompleted(ImageView arg0, String arg1, Bitmap arg2,
			BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
		LoadPercentImageView imageView = (LoadPercentImageView)arg0;
		imageView.setProgress(100);
		imageView.setComplete(true);
		imageView.setImageBitmap(arg2);
	}
	@Override
	public void onLoading(ImageView arg0, String uri,
			BitmapDisplayConfig config, long total, long current) {
//		super.onLoading(arg0, uri, config, total, current);
		LoadPercentImageView imageView = (LoadPercentImageView)arg0;
		int progress = (int)(((double)current) / ((double)total) * 100);
		imageView.setProgress(progress);
	}
}
