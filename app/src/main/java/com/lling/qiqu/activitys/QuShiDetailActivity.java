package com.lling.qiqu.activitys;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.smssdk.SMSSDK;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.adapters.CommentAdapter;
import com.lling.qiqu.beans.Comment;
import com.lling.qiqu.beans.DingOrCai;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.beans.User;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.commons.PageResult;
import com.lling.qiqu.dao.DingCaiDAO;
import com.lling.qiqu.interfaces.ApiCallBack;
import com.lling.qiqu.service.IJokeService;
import com.lling.qiqu.service.impl.JokeServiceImpl;
import com.lling.qiqu.ui.LoadListView;
import com.lling.qiqu.ui.LoadListView.OnFootLoadingListener;
import com.lling.qiqu.ui.Scroll2BottomListenerScrollView;
import com.lling.qiqu.ui.popwindows.SharePopWindow;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.FastjsonUtil;
import com.lling.qiqu.utils.ProgressDialogUtils;
import com.lling.qiqu.utils.TaskExecutor;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.ToolsUtils;
import com.lling.qiqu.utils.Util;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * @ClassName: QuShiDetailActivity
 * @Description: 趣事详情界面
 * @author lling
 * @date 2015-6-22
 */
@ContentView(R.layout.activity_qushi_detail)
public class QuShiDetailActivity extends BaseActivity {

	private final String TAG = "QuShiDetailActivity";
	
	@ViewInject(R.id.user_nick)
	private TextView mUserNickTV;
	@ViewInject(R.id.create_date)
	private TextView mCreateTimeTV;
	@ViewInject(R.id.qushi_content)
	private TextView mContentTV;
	@ViewInject(R.id.ding_tv)
	private TextView mDingTV;
	@ViewInject(R.id.cai_tv)
	private TextView mCaiTV;
	@ViewInject(R.id.comment_tv)
	private TextView mCommentTV;
	@ViewInject(R.id.share_tv)
	private TextView mShareTV;
	@ViewInject(R.id.user_img)
	private ImageView mUserPortraitIV;
	@ViewInject(R.id.listview)
	private LoadListView mListView;
	@ViewInject(R.id.content_et)
	private EditText mCommentContentET;
	@ViewInject(R.id.scrollview)
	private Scroll2BottomListenerScrollView mScrollView;
	@ViewInject(R.id.qushi_list)
	private LinearLayout mQushiLayout;
	@ViewInject(R.id.progress)
	private ProgressBar mLoadProgressBar;
	
	private Joke mJoke;
	private Handler mHandler = new ClassHandler(this);
	private IJokeService mQuShiService;
	private CommentAdapter mCommentAdapter;
	private List<Comment> mCommentLists = new ArrayList<Comment>();
	private int mPageId = 1;
	private int mCount = 5;
	private boolean mHasNext = true;
	private DingCaiDAO mDingOrCaiDAO = new DingCaiDAO(this);
	private int mUserId = -1;
	private boolean isCommnet = false;  //是否点击评论进来的
	private SharePopWindow mSharePopWindow;
	private int mJokeId = 1;
	private boolean mIsOpenFromPush = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		mQuShiService = new JokeServiceImpl(this);
		if(App.currentUser != null) {
    		mUserId = App.currentUser.getId();
    	}
		mSharePopWindow = new SharePopWindow(this);
		mListView.setHaveScrollbar(false);
		mCommentAdapter = new CommentAdapter(this);
		mListView.setAdapter(mCommentAdapter);
		mListView.setOnFootLoadingListener(new OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				loadComments();
			}
		});
		initQuShiContent();
	}
	
	/**
	 * 如果此界面已经打开，再点通知界面打开，则调用此方法
	 */
	protected void onNewIntent(Intent intent) {
		if(Constants.DEBUG) Log.d(TAG, "onNewIntent");
		setIntent(intent);
		reSet();   //重置
		initQuShiContent();  //加载
	}
	
	private void reSet() {
		mPageId = 1;
		mHasNext = true;
		mCommentLists.clear();
		if(mCommentAdapter != null) {
			mCommentAdapter.notifyDataSetChanged();
		}
	}
	
	ApiCallBack getJokeApiCallBack = new ApiCallBack() {
		@Override
		public void onSuccess(Object data) {
			mJoke = (Joke) data;
			if(mJoke != null) {
				setQushiContent();
			}
		}
		@Override
		public void onFailure(String msg) {
			mLoadProgressBar.setVisibility(View.GONE);
		}
		@Override
		public void onError(Exception e, String msg) {
			mLoadProgressBar.setVisibility(View.GONE);
		}
	};

	//初始化笑话内容
	private void initQuShiContent() {
		mJoke = (Joke)getIntent().getSerializableExtra("qushi");
		if(mJoke == null) {  //从push进来的
			try {
				mJokeId = Integer.valueOf(getIntent().getStringExtra("id"));
				mIsOpenFromPush = true;
				init();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			mQuShiService.getJokeById(getJokeApiCallBack, mJokeId);
		} else {   //从界面进来的
			isCommnet = getIntent().getBooleanExtra("isComment", false);
			mJokeId = mJoke.getId();
			setQushiContent();
		}
	}
	
	//push进来的界面要进行应用程序初始化
	private void init() {
		initYouMeng();
		App.currentUser = (User)spUtil.getObject("user", null);
		//初始化短信接口
        SMSSDK.initSDK(this, getString(R.string.sina_appid), getString(R.string.youmeng_appid));
        initWeiXin();
        initTencent();
	}
	
	private void setQushiContent() {
		loadComments();
		mLoadProgressBar.setVisibility(View.GONE);
		mScrollView.setVisibility(View.VISIBLE);
		BitmapUtil.display(mUserPortraitIV, mJoke.getPortraitUrl());
		mUserNickTV.setText(mJoke.getUserNike());
		mCreateTimeTV.setText(Util.getFormatDate(mJoke.getCreateDate()));
		mContentTV.setText(mJoke.getContent());
        mDingTV.setText(String.valueOf(mJoke.getSupportsNum()));
        mCaiTV.setText(String.valueOf(mJoke.getOpposesNum()));
        mCommentTV.setText(String.valueOf(mJoke.getCommentNum()));
        
        final DingOrCai dingOrCai = mDingOrCaiDAO.getDingOrCai(mUserId, mJokeId);
        if(dingOrCai != null) {
        	if(dingOrCai.isDing()) {
        		mDingTV.setSelected(true);
        		mCaiTV.setSelected(false);
        		if(mJoke.getSupportsNum() < dingOrCai.getNum()) {
        			mJoke.setSupportsNum(dingOrCai.getNum());
        		}
        	} else {
        		mDingTV.setSelected(false);
        		mCaiTV.setSelected(true);
        		if(mJoke.getOpposesNum() < dingOrCai.getNum()) {
        			mJoke.setOpposesNum(dingOrCai.getNum());
        		}
        	}
        }
        
        mDingTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dingOrCai != null) {
					if(dingOrCai.isDing()) {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					} else {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					}
					return;
				}
				if(v.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					return;
				}
				View caiView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.cai_tv);
				if(caiView.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					return;
				}
				final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
				Animation addOneAnimation = AnimationUtils.loadAnimation(
						QuShiDetailActivity.this, R.anim.add_one);
				addOneAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						view.setVisibility(View.VISIBLE);
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						view.setVisibility(View.GONE);
					}
				});
				view.startAnimation(addOneAnimation);
				v.setSelected(true);
				mJoke.setSupportsNum(mJoke.getSupportsNum()+1);
				((TextView)v).setText(String.valueOf(mJoke.getSupportsNum()));
				mDingOrCaiDAO.dingOrCai(mUserId, mJokeId, DingOrCai.DING, mJoke.getSupportsNum());
			}
		});
        
        mCaiTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dingOrCai != null) {
					if(dingOrCai.isDing()) {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					} else {
						ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					}
					return;
				}
				if(v.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_cai);
					return;
				}
				View dingView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.ding_tv);
				if(dingView.isSelected()) {
					ToastUtils.showMessage(QuShiDetailActivity.this, R.string.has_ding);
					return;
				}
				final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
				Animation addOneAnimation = AnimationUtils.loadAnimation(
						QuShiDetailActivity.this, R.anim.add_one);
				addOneAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						view.setVisibility(View.VISIBLE);
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						view.setVisibility(View.GONE);
					}
				});
				view.startAnimation(addOneAnimation);
				v.setSelected(true);
				mJoke.setOpposesNum(mJoke.getOpposesNum()+1);
				((TextView)v).setText(String.valueOf(mJoke.getOpposesNum()));
				mDingOrCaiDAO.dingOrCai(mUserId, mJokeId, DingOrCai.CAI, mJoke.getOpposesNum());
			}
		});
        mShareTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharePopWindow.setmJoke(mJoke);
				mSharePopWindow.showAtLocation(v.getRootView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
			}
		});
        mCommentTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mCommentContentET.requestFocus();
				ToolsUtils.openKeybord(mCommentContentET, QuShiDetailActivity.this);
			}
		});
	}
	
	/**
	 * 加载趣事评论
	 */
	private void loadComments() {
		if(!mHasNext){
			mListView.onFootLoadingComplete(true);
			return;
		}
		mQuShiService.getCommentByJokeId(mHandler, mJokeId, mPageId, mCount);
	}
	
	/**
	 * 加载数据成功，更新界面
	 * @param pageResult
	 */
	private void loadSuccess(PageResult pageResult) {
		mScrollView.setmListView(mListView);
		if(pageResult == null || pageResult.isEmpty()) {
			if(Constants.DEBUG) Log.w(TAG, "load qushi success, but no data!");
			mHasNext = false;
			return;
		}
		
		String tmp = FastjsonUtil.serialize(pageResult.getList());
		List<Comment> list = FastjsonUtil.deserializeList(tmp,
				Comment.class);
		mCommentLists.addAll(list);
		
		if(mCommentLists.size() <= 0) {   //无数据，显示无数据布局
		} else {
			mCommentAdapter.onDataChange(mCommentLists);
			mListView.setVisibility(View.VISIBLE);
		}
//		ToolsUtils.setListViewHeightBasedOnChildren(mListView);
		if(isCommnet && mPageId == 1) {   //如果点击评论进来的，则滚动到评论处
			TaskExecutor.scheduleTaskOnUiThread(150, new Runnable() {
				@Override
				public void run() {
					int height = mQushiLayout.getHeight();
					mScrollView.scrollTo(0, height);
				}
			});
		}
		mHasNext = pageResult.getHasNext();
		mListView.setHasMoreData(mHasNext);  //设置是否还有更多数据
		if (pageResult.getHasNext()) {
			mPageId = pageResult.getNext();
		}
	}
	
	@OnClick(R.id.add_comment_btn)
	private void sendComment(View view) {
		ToolsUtils.hideKeyboard(mCommentTV);
		if(App.currentUser == null) {  //用户未登录
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityWithAnimation(intent);
			return;
		}
		String content = mCommentContentET.getText().toString().trim();
		if(Util.isEmpty(content)) {
			ToastUtils.showMessage(getApplicationContext(), 
					R.string.comment_content_null);
			return;
		}
		content = ToolsUtils.Html2Text(content);
		if(Util.isEmpty(content)) {
			ToastUtils.showMessage(getApplicationContext(), 
					R.string.input_invalide);
			return;
		}
		if(mJoke == null) {
			return;
		}
		ProgressDialogUtils.showProgressDialog(this, getString(R.string.commenting));
		mQuShiService.addComment(mHandler, mJokeId, content);
	}
	
	@OnClick(R.id.back)
	private void back(View view) {
		if(mIsOpenFromPush && !App.isStart) {
			Intent intent = new Intent(this, SplashActivity.class);
			startActivityWithAnimation(intent);
		}
		finishWithAnimation();
	}
	
	@Override
	public void onBackPressed() {
		if(mIsOpenFromPush && !App.isStart) {
			Intent intent = new Intent(this, SplashActivity.class);
			startActivityWithAnimation(intent);
		}
		super.onBackPressed();
	}
	
	/**
	 * 消息处理类
	 * @author lling
	 * 
	 */
	static class ClassHandler extends Handler {
		WeakReference<QuShiDetailActivity> mReference;
		ClassHandler(QuShiDetailActivity activity) {
			mReference = new WeakReference<QuShiDetailActivity>(activity);
		}
		public void handleMessage(Message msg) {
			final QuShiDetailActivity activity = mReference.get();
			if(activity == null) {
				return;
			}
			switch (msg.what) {
			case Constants.SUCCESS: // 获取评论数据成功
				PageResult pageResult = (PageResult)msg.getData().getSerializable("pageResult");
				activity.loadSuccess(pageResult);
				activity.mListView.onFootLoadingComplete(true);
				break;
			case Constants.FAILURE:
				activity.mListView.onFootLoadingComplete(true);
				break;
			case Constants.SUCCESS_1: // 发表评论成功
				ProgressDialogUtils.dismiss();
				activity.commentSuccess();
				break;
			case Constants.FAILURE_1:
				ProgressDialogUtils.dismiss();
				break;
			}
		}
	}

	/**
	 * 评论成功
	 */
	private void commentSuccess() {
		Comment comment = new Comment();
		comment.setContent(mCommentContentET.getText().toString().trim());
		comment.setUserId(App.currentUser.getId());
		comment.setPortraitUrl(App.currentUser.getPortraitUrl());
		comment.setUserNike(App.currentUser.getUserNike());
		comment.setCreateDate(new Date());
		mCommentLists.add(0, comment);
		mCommentAdapter.onDataChange(mCommentLists);
		mCommentContentET.setText("");
		
		mJoke.setCommentNum(mJoke.getCommentNum()+1);
		mCommentTV.setText(String.valueOf(mJoke.getCommentNum()));
		
		mListView.setSelectionAfterHeaderView();//选中第一条
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("QuShiDetailActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("QuShiDetailActivity");
		if(ToolsUtils.isKeybordShow(this)) {
			ToolsUtils.hideKeyboard(mCommentContentET);
		}
		super.onPause();
	}
	
	
	private void initYouMeng() {
		AnalyticsConfig.enableEncrypt(true);
		MobclickAgent.openActivityDurationTrack(false);
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		if(spUtil.getBoolean(Constants.IS_RECEIVE_PUSH, true)) {
			mPushAgent.enable();
		} else {
			mPushAgent.disable();
		}
	}
	
	private void initWeiXin() {
		IWXAPI api = WXAPIFactory.createWXAPI(this, com.lling.qiqu.wxapi.Constants.APP_ID);
		api.registerApp(com.lling.qiqu.wxapi.Constants.APP_ID);
	}
	
	private void initTencent() {
		try {
			ApplicationInfo appInfo = this.getPackageManager()
			        .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			int appId = appInfo.metaData.getInt("TECENT_APPID", -1);
			if(appId == -1) {
				return;
			}
			App.mTencent = Tencent.createInstance(String.valueOf(appId), 
					this.getApplicationContext());
			String openId = spUtil.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID, null);
			String token = spUtil.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN, null);
			long expires = spUtil.getLong(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN, System.currentTimeMillis());
			if(openId != null) {
				App.mTencent.setOpenId(openId);
			}
			if(token != null) {
				App.mTencent.setAccessToken(token, String.valueOf((expires-System.currentTimeMillis())/1000));
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
