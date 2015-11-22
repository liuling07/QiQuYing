package com.lling.qiqu.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.activitys.MeiTuActivity;
import com.lling.qiqu.activitys.TuDetailActivity;
import com.lling.qiqu.adapters.QuTuAdapter;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.commons.PageResult;
import com.lling.qiqu.service.IJokeService;
import com.lling.qiqu.service.impl.JokeServiceImpl;
import com.lling.qiqu.ui.LoadListView;
import com.lling.qiqu.ui.LoadListView.OnFootLoadingListener;
import com.lling.qiqu.utils.FastjsonUtil;
import com.lling.qiqu.utils.HttpUtil;
import com.lling.qiqu.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName: QuTuFragment
 * @Description: 趣图Fragment
 * @author lling
 * @date 2015-6-15
 */
public class MeiTuFragment extends Fragment implements
			SwipeRefreshLayout.OnRefreshListener {
	private final static String TAG = "QuTuFragment";
	
	private SwipeRefreshLayout mSwipeLayout;
	private LoadListView mListView;
	private MeiTuActivity mMeiTuActivity;
	private List<Joke> mJokeLists = new ArrayList<Joke>();
	private Handler mHandler = new ClassHandler(this);
	private IJokeService mJokeService;
	private QuTuAdapter mQuTuAdapter;
	private int mPageId;
	private int mCount;
	private boolean mHasNext;
	
	private int mNewOrHotFlag = Joke.SORT_NEW;
	private String mKey = Constants.CACHE_MEITU_NEW;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mNewOrHotFlag = args != null ? args.getInt("newOrHotFlag") : Joke.SORT_NEW;
		if(mNewOrHotFlag == Joke.SORT_HOT) {
			mKey = Constants.CACHE_MEITU_HOT;
		}
		mPageId = 1;
		mCount = 10;
		mHasNext = true;
		mJokeService = new JokeServiceImpl(mMeiTuActivity);
		mQuTuAdapter = new QuTuAdapter(mMeiTuActivity);
	}

	@Override
	public void onResume() {
		if(mQuTuAdapter != null) {
			mQuTuAdapter.notifyDataSetChanged();
		}
		MobclickAgent.onPageStart("MeiTuFragment"); //统计页面
		super.onResume();
	}
	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MeiTuFragment"); //统计页面
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		this.mMeiTuActivity = (MeiTuActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_qushi, null);
		mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
		mListView = (LoadListView) view.findViewById(R.id.listview);
		// 顶部刷新的样式
		mSwipeLayout.setColorSchemeResources(R.color.holo_red_light,
			R.color.holo_green_light, R.color.holo_blue_bright,
			R.color.holo_orange_light);
		mSwipeLayout.setOnRefreshListener(this);
		
		mListView.setAdapter(mQuTuAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mMeiTuActivity, TuDetailActivity.class);
				intent.putExtra("content", mJokeLists.get(position));
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				App.isStartOtherActivity = true;
			}
		});
		mListView.setOnFootLoadingListener(new OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				loadQuTu();
			}
		});
		clearListsAndInitPageInfo();
		getData();
		return view;
	}
	
	/**
	 * 加载数据
	 */
	private void getData() {
		//有网络
		if(HttpUtil.isNetworkAvailable(mMeiTuActivity)) {
			loadQuTu();
		} else {
			HashMap<String, Object> map = (HashMap<String, Object>)App.getInstance().getSpUtil().getObject(mKey, null);
			if(map == null || map.get("data") == null) {
				//无网络无缓存 ,显示无数据布局
				ToastUtils.showMessageInCenter(mMeiTuActivity, "暂无数据");
				return;   
			} 
			mJokeLists = (ArrayList<Joke>)map.get("data");
			mHasNext = (Boolean)map.get("hasNext");
			mPageId = (Integer)map.get("pageId");
			mQuTuAdapter.setList(mJokeLists);
			mQuTuAdapter.notifyDataSetChanged();
			mListView.setHasMoreData(mHasNext);
		}
	}

	private void clearListsAndInitPageInfo() {
		mPageId = 1;
		mHasNext = true;   //重新加载保证从第一页加载
	}
	
	/**
	 * 加载趣事
	 */
	private void loadQuTu() {
		//没有网络
		if(!HttpUtil.isNetworkAvailable(mMeiTuActivity)) {
			ToastUtils.showMessage(mMeiTuActivity, R.string.no_net);
			return;
		}
		if(!mHasNext){
			mListView.onFootLoadingComplete(true);
			return;
		}
		if(mPageId != 1) {
			mListView.setMoreDataMsg(getString(R.string.more_meitu));
		}
		mJokeService.getList(mHandler, Joke.TYPE_MEITU, mNewOrHotFlag, mPageId, mCount);
	}
	
	/**
	 * 加载数据成功，更新界面
	 * @param pageResult
	 */
	private void loadSuccess(PageResult pageResult) {
		if(pageResult == null || pageResult.isEmpty()) {
			Log.e(TAG, "load data success, but no data!");
			return;
		}
		
		mHasNext = pageResult.getHasNext();
		mListView.setHasMoreData(mHasNext);  //设置是否还有更多数据
		if (pageResult.getHasNext()) {
			mPageId = pageResult.getNext();
		}
		String tmp = FastjsonUtil.serialize(pageResult.getList());
		List<Joke> list = FastjsonUtil.deserializeList(tmp,
				Joke.class);
		mJokeLists.addAll(list);
		
		if(mJokeLists.size() <= 0) {   //无数据，显示无数据布局
			
		} else {
			mQuTuAdapter.onDataChange(mJokeLists);
			mListView.setVisibility(View.VISIBLE);
		}
		saveCache();
	}

	/**
	 * 消息处理类
	 * @author lling
	 * 
	 */
	static class ClassHandler extends Handler {
		WeakReference<MeiTuFragment> mReference;
		ClassHandler(MeiTuFragment fragment) {
			mReference = new WeakReference<MeiTuFragment>(fragment);
		}
		public void handleMessage(Message msg) {
			final MeiTuFragment fragment = mReference.get();
			if(fragment == null) {
				return;
			}
			switch (msg.what) {
			case Constants.SUCCESS: // 获取美图数据成功
				PageResult pageResult = (PageResult)msg.getData().getSerializable("pageResult");
				loadComplete(fragment);
				fragment.loadSuccess(pageResult);
				break;
			case Constants.SUCCESS_1:  //刷新成功
				PageResult pageResult1 = (PageResult)msg.getData().getSerializable("pageResult");
				loadComplete(fragment);
				fragment.mJokeLists.clear();
				fragment.loadSuccess(pageResult1);
				break;
			case Constants.FAILURE:
				loadComplete(fragment);
				break;
			}
		}
		
		private void loadComplete(MeiTuFragment fragment) {
			fragment.mListView.onFootLoadingComplete(true);
			fragment.mSwipeLayout.setRefreshing(false);
			fragment.mMeiTuActivity.refreshCompete();
		}
	}

	/**
	 * 刷新美图
	 */
	@Override
	public void onRefresh() {
		//没有网络
		if(!HttpUtil.isNetworkAvailable(mMeiTuActivity)) {
			ToastUtils.showMessage(mMeiTuActivity, R.string.no_net);
			mSwipeLayout.setRefreshing(false);
			return;
		}
		clearListsAndInitPageInfo();
		mJokeService.refush(mHandler, Joke.TYPE_MEITU, mNewOrHotFlag, mCount);
	}
	
	public boolean isRefresh() {
		if(mSwipeLayout == null) {
			mSwipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh_layout);
		}
		return mSwipeLayout.isRefreshing();
	}
	
	/**
	 * 刷新
	 */
	public void refresh() {
		//没有网络
		if(!HttpUtil.isNetworkAvailable(mMeiTuActivity)) {
			ToastUtils.showMessage(mMeiTuActivity, R.string.no_net);
			mMeiTuActivity.refreshCompete();
			return;
		}
		if(mSwipeLayout.isRefreshing()) {
			return;
		}
		mSwipeLayout.setRefreshing(true);
		clearListsAndInitPageInfo();
		mListView.setSelection(0);
		mJokeService.refush(mHandler, Joke.TYPE_MEITU, mNewOrHotFlag, mCount);
	}
	
	/**
	 * 对内容进行缓存
	 */
	private void saveCache() {
		HashMap<String, Object> cache = new HashMap<String, Object>();
		cache.put("data", mJokeLists);
		cache.put("hasNext", mHasNext);
		cache.put("pageId", mPageId);
		App.getInstance().getSpUtil().putObject(mKey, cache);
	}

}
