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
import com.lling.qiqu.ui.fragments.AllFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: AllJokeActivity
 * @Description: 所有内容界面
 * @author lling
 * @date 2015年7月16日
 */
@ContentView(R.layout.activity_all_joke)
public class AllJokeActivity extends BaseActivity {
	private final String TAG = "AllJokeActivity";
	@ViewInject(R.id.mViewPager)
	private ViewPager mViewPager;
	
	private QQYFragmentPagerAdapter mAdapetr;
	private ArrayList<Fragment> mFragments;
	@ViewInject(R.id.refresh)
	private ImageView mRefreshIV;
	
	@ViewInject(R.id.categoryTabStrip)
	private CategoryTabStrip mCategoryTabStrip;
	/** tab栏目（最新、最热）*/
	private ArrayList<String> mCategoryList = new ArrayList<String>();
	
	private Animation mRefreshAnimation;  //刷新图标动画
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
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
		AllFragment allNewFragment = new AllFragment();
		allNewFragment.setArguments(data);
		mFragments.add(allNewFragment);
		
		Bundle data1 = new Bundle();
		data1.putInt("newOrHotFlag", Joke.SORT_HOT);
		AllFragment allHotFragment = new AllFragment();
		allHotFragment.setArguments(data1);
		mFragments.add(allHotFragment);
		
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
		AllFragment allFragment = (AllFragment)mFragments.get(mViewPager.getCurrentItem());
		if(allFragment.isRefresh()) {
			return;
		}
		view.startAnimation(mRefreshAnimation);
		allFragment.refresh();
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
		MobclickAgent.onPageStart("AllJokeActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("AllJokeActivity");
		super.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
	}

}
