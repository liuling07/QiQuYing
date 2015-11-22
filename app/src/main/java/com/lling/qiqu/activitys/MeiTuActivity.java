package com.lling.qiqu.activitys;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.R;
import com.lling.qiqu.adapters.QQYFragmentPagerAdapter;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.ui.CategoryTabStrip;
import com.lling.qiqu.ui.fragments.MeiTuFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: MeiTuActivity
 * @Description: 美图activity
 * @author lling
 * @date 2015-6-7
 */
@ContentView(R.layout.activity_meitu)
public class MeiTuActivity extends BaseActivity {
	
	@ViewInject(R.id.mViewPager)
	private ViewPager mViewPager;
	@ViewInject(R.id.refresh)
	private ImageView mRefreshIV;
	
	private QQYFragmentPagerAdapter mAdapetr;
	private ArrayList<Fragment> mFragments;
	
	@ViewInject(R.id.categoryTabStrip)
	private CategoryTabStrip mCategoryTabStrip;
	/** tab栏目（最新、最热）*/
	private ArrayList<String> mCategoryList = new ArrayList<String>();
	private Animation mRefreshAnimation;  //刷新图标动画
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mCategoryList.add("最新");
		mCategoryList.add("最热");
		mFragments = new ArrayList<Fragment>();
		initFragment();
	}
	
	/**
	 * 初始化fragment
	 */
	private void initFragment() {
		Bundle data = new Bundle();
		data.putInt("newOrHotFlag", Joke.SORT_NEW);
		MeiTuFragment meiTuNewFragment = new MeiTuFragment();
		meiTuNewFragment.setArguments(data);
		mFragments.add(meiTuNewFragment);
		
		Bundle data1 = new Bundle();
		data1.putInt("newOrHotFlag", Joke.SORT_HOT);
		MeiTuFragment meiTuHotFragment = new MeiTuFragment();
		meiTuHotFragment.setArguments(data1);
		mFragments.add(meiTuHotFragment);
		
		mAdapetr = new QQYFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
		mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapetr);
        
        mCategoryTabStrip.setViewPager(mViewPager);
        mCategoryTabStrip.setCatalogs(mCategoryList);
	}
	
	@OnClick(R.id.refresh)
	private void refresh(View view) {
		if(mRefreshAnimation == null) {
			mRefreshAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.progress_anim);
		}
		MeiTuFragment meiTuFragment = (MeiTuFragment)mFragments.get(mViewPager.getCurrentItem());
		if(meiTuFragment.isRefresh()) {
			return;
		}
		view.startAnimation(mRefreshAnimation);
		meiTuFragment.refresh();
	}
	
	/**
	 * 刷新完毕，结束刷新动画
	 */
	public void refreshCompete() {
		Log.e("refreshCompete", "refreshCompete");
		mRefreshIV.clearAnimation();
		if(mRefreshAnimation != null) {
			mRefreshAnimation.cancel();
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("MeiTuActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("MeiTuActivity");
		super.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
	}
}
