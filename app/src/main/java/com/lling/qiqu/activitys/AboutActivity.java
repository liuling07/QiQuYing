package com.lling.qiqu.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.R;
import com.lling.qiqu.utils.ToolsUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: AboutActivity
 * @Description: 关于界面
 * @author lling
 * @date 2015-7-26
 */
@ContentView(R.layout.activity_about)
public class AboutActivity extends BaseActivity {

	@ViewInject(R.id.about_version)
	private TextView mAboutVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mAboutVersion.setText(new StringBuilder("奇趣营 V").append(ToolsUtils.getVersionName(this)));
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("AboutActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("AboutActivity");
		super.onPause();
	}
	
	@OnClick(R.id.back)
	private void back(View view) {
		finishWithAnimation();
	}

}
