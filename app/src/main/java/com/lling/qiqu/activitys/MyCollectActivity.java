package com.lling.qiqu.activitys;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.adapters.JokeAdapter;
import com.lling.qiqu.beans.Collect;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.dao.CollectDAO;
import com.lling.qiqu.utils.FastjsonUtil;
import com.lling.qiqu.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: MyCollectActivity
 * @Description: 我的收藏
 * @author lling
 * @date 2015-7-12
 */
@ContentView(R.layout.activity_mycollect)
public class MyCollectActivity extends BaseActivity {

	@ViewInject(R.id.listview)
	private ListView mListView;
	@ViewInject(R.id.no_data_layout)
	private View mNoDataLayout;
	
	private JokeAdapter mJokeAdapter;
	private CollectDAO mCollectDAO;
	private List<Collect> mCollects;
	private int mUserId = -1;
	private List<Joke> mJokes = new ArrayList<Joke>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		if(App.currentUser != null) {
    		mUserId = App.currentUser.getId();
    	}
		mCollectDAO = new CollectDAO(this);
		init();
	}
	
	private void init() {
		mCollects = mCollectDAO.getCollects(mUserId);
		if(Util.isEmpty(mCollects)) { //暂无收藏，显示无数据布局
			mListView.setVisibility(View.GONE);
			mNoDataLayout.setVisibility(View.VISIBLE);
			return;
		} 
		for (Collect collect : mCollects) {
			Joke joke = FastjsonUtil.deserialize(collect.getJokeContent(), Joke.class);
			mJokes.add(joke);
		}
		mJokeAdapter = new JokeAdapter(this);
		mJokeAdapter.setList(mJokes);
		mListView.setAdapter(mJokeAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Joke joke = mJokes.get(position);
				if(joke.getType() == Joke.TYPE_QUSHI) {
					Intent intent = new Intent(MyCollectActivity.this, QuShiDetailActivity.class);
					intent.putExtra("qushi", joke);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				} else if(joke.getType() == Joke.TYPE_QUTU || joke.getType() == Joke.TYPE_MEITU) {
					Intent intent = new Intent(MyCollectActivity.this, TuDetailActivity.class);
					intent.putExtra("content", joke);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);
				}
			}
		});
	}
	
	/**
	 * 取消搜藏
	 * @param joke
	 */
	public void cancelCollect(Joke joke) {
		mJokes.remove(joke);
		mJokeAdapter.notifyDataSetChanged();
		if(Util.isEmpty(mJokes)) { //显示无数据布局
			mListView.setVisibility(View.GONE);
			mNoDataLayout.setVisibility(View.VISIBLE);
		} 
	}
	
	@OnClick(R.id.back)
	private void back(View view) {
		finishWithAnimation();
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("MyCollectActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("MyCollectActivity");
		super.onPause();
	}

}
