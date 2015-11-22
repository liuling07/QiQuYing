package com.lling.qiqu.activitys;

import java.io.IOException;
import java.util.UUID;

import uk.co.senab.photoview.PhotoView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.R;
import com.lling.qiqu.utils.ImgUtil;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.ToolsUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: ImageShowActivity
 * @Description: 图片预览
 * @author lling
 * @date 2015-6-27
 */
@ContentView(R.layout.activity_image_show)
public class ImageShowActivity extends BaseActivity {
	private final String SAVE_PATH = "/qiquying/images/" ;
	@ViewInject(R.id.photoView)
	private PhotoView mPhotoView;
	private BitmapUtils mBitmapUtils;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		initBitmapUtils();
		String url = getIntent().getStringExtra("url");
		mBitmapUtils.display(mPhotoView, url);
	}
	
	/**
	 * 初始化BitmapUtils
	 */
	private void initBitmapUtils() {
		mBitmapUtils = new BitmapUtils(this);
//		mBitmapUtils.configDefaultLoadingImage(R.drawable.p_ic_imgloading);
//		mBitmapUtils.configDefaultLoadFailedImage(R.drawable.p_ic_imgloading_fail);
		mBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		mBitmapUtils.configMemoryCacheEnabled(true);
		mBitmapUtils.configDiskCacheEnabled(true);
	}
	
	@OnClick(R.id.save)
	private void save(View view) {
		mPhotoView.setDrawingCacheEnabled(true);
		Bitmap bitmap = mPhotoView.getDrawingCache();
		if(bitmap == null) {
			mPhotoView.setDrawingCacheEnabled(false);
			return;
		}
		if (!ToolsUtils.hasSdcard()) {
			ToastUtils.showMessage(getApplicationContext(), "未找到存储卡");
		}
		view.setEnabled(false);
		ToastUtils.showMessage(getApplicationContext(), "图片已保存到sd卡" + SAVE_PATH);
		try {
			ImgUtil.saveBitmapToFile(bitmap, Environment.getExternalStorageDirectory() 
					+ SAVE_PATH + UUID.randomUUID() + ".jpg");
		} catch (IOException e) {
			ToastUtils.showMessage(getApplicationContext(), "保存失败");
		}
		mPhotoView.setDrawingCacheEnabled(false);
	}
	
	@OnClick(R.id.back)
	private void back(View view) {
		finishWithAnimation();
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ImageShowActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("ImageShowActivity");
		super.onPause();
	}

}
