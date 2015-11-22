package com.lling.qiqu.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.activitys.QuShiDetailActivity;
import com.lling.qiqu.activitys.TuDetailActivity;
import com.lling.qiqu.beans.DingOrCai;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.dao.DingCaiDAO;
import com.lling.qiqu.interfaces.PercentImageViewLoadCallBack;
import com.lling.qiqu.ui.LoadPercentImageView;
import com.lling.qiqu.ui.popwindows.SharePopWindow;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.DensityUtil;
import com.lling.qiqu.utils.HttpUtil;
import com.lling.qiqu.utils.SharePreferenceUtil;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.Util;

/**
 * @ClassName: JokeAdapter
 * @Description: 
 * @author lling
 * @date 2015年7月13日
 */
public class JokeAdapter extends BaseAdapter{
	
	private final static int TYPE_COUNT = 2;  //类型种类
	private final static int TYPE_TEXT = 0;  //文本类型
	private final static int TYPE_IMG = 1;   //图片类型
	
	private List<Joke> mList;
	private Context mContext;
	private DingCaiDAO mDingOrCaiDAO;
	private int mUserId = -1;
	private int mMaxWidth;
	private int mMaxHeight;
	
	private SharePopWindow mSharePopWindow;
	private SharePreferenceUtil mSpUtil;
 	
    public JokeAdapter(Context context) {
    	mContext = context;
    	mDingOrCaiDAO = new DingCaiDAO(context);
    	mMaxWidth = DensityUtil.getWidthInPx(context) - DensityUtil.dip2px(context, 32);
    	mMaxHeight = DensityUtil.getHeightInPx(context) - DensityUtil.getStatusHeight(mContext)
    			- DensityUtil.dip2px(context, 96);
    	mSharePopWindow = new SharePopWindow(context);
    	mSpUtil = App.getInstance().getSpUtil();
	}
    
    public void setList(List<Joke> lists) {
		this.mList = lists;
	}
    
    public void onDataChange(List<Joke> lists) {
		this.mList = lists;
		notifyDataSetChanged();
	}
    
	@Override
	public int getCount() {
		if(mList == null) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		if(mList == null) {
			return null;
		}
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	// 每个convert view都会调用此方法，获得当前所需要的view样式
	@Override
	public int getItemViewType(int position) {
		int type = mList.get(position).getType();
		if(type == Joke.TYPE_QUSHI) {
			return TYPE_TEXT;
		} else if(type == Joke.TYPE_QUTU || type == Joke.TYPE_MEITU) {
			return TYPE_IMG;
		}
		return TYPE_TEXT;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		TextHolder textHolder = null;
		ImgHolder imgHolder = null;
		int type = getItemViewType(position);
		if(convertView == null) {
			switch (type) {
			case TYPE_TEXT:
				textHolder = new TextHolder();
	            convertView = LayoutInflater.from(mContext).inflate(
	                    R.layout.listitem_qushi, null);
	            textHolder.userPortraitIV = (ImageView) convertView.findViewById(R.id.user_img);
	            textHolder.userNickTV = (TextView) convertView.findViewById(R.id.user_nick);
	            textHolder.createTimeTV = (TextView) convertView.findViewById(R.id.create_date);
	            textHolder.contentTV = (TextView) convertView.findViewById(R.id.qushi_content);
	            textHolder.dingTV = (TextView) convertView.findViewById(R.id.ding_tv);
	            textHolder.caiTV = (TextView) convertView.findViewById(R.id.cai_tv);
	            textHolder.commentTV = (TextView) convertView.findViewById(R.id.comment_tv);
	            textHolder.shareTV = (TextView) convertView.findViewById(R.id.share_tv);
	            convertView.setTag(textHolder);
				break;
			case TYPE_IMG:
				imgHolder = new ImgHolder();
	            convertView = LayoutInflater.from(mContext).inflate(
	                    R.layout.listitem_qutu, null);
	            imgHolder.userPortraitIV = (ImageView) convertView.findViewById(R.id.user_img);
	            imgHolder.userNickTV = (TextView) convertView.findViewById(R.id.user_nick);
	            imgHolder.createTimeTV = (TextView) convertView.findViewById(R.id.create_date);
	            imgHolder.titleTV = (TextView) convertView.findViewById(R.id.qutu_title);
	            imgHolder.contentIV = (LoadPercentImageView) convertView.findViewById(R.id.qutu_content);
	            imgHolder.dingTV = (TextView) convertView.findViewById(R.id.ding_tv);
	            imgHolder.caiTV = (TextView) convertView.findViewById(R.id.cai_tv);
	            imgHolder.commentTV = (TextView) convertView.findViewById(R.id.comment_tv);
	            imgHolder.shareTV = (TextView) convertView.findViewById(R.id.share_tv);
	            imgHolder.contentIV.setMaxWidth(mMaxWidth);
	            imgHolder.contentIV.setMaxHeight(mMaxHeight);
	            convertView.setTag(imgHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_TEXT:
				textHolder = (TextHolder) convertView.getTag();
				break;
			case TYPE_IMG:
				imgHolder = (ImgHolder) convertView.getTag();
				break;
			}
		}
		if(App.currentUser != null) {
    		mUserId = App.currentUser.getId();
    	}
		final Joke joke = mList.get(position);
	    final DingOrCai dingOrCai = mDingOrCaiDAO.getDingOrCai(mUserId, joke.getId());
	    OnClickListener dingListener = new DingClickListenner(dingOrCai, joke);
		OnClickListener caiListener = new CaiClickListenner(dingOrCai, joke);
	    switch (type) {
		case TYPE_TEXT:
			if(dingOrCai != null) {
	        	if(dingOrCai.isDing()) {
	        		textHolder.dingTV.setSelected(true);
	        		textHolder.caiTV.setSelected(false);
	        		if(joke.getSupportsNum() < dingOrCai.getNum()) {
	        			joke.setSupportsNum(dingOrCai.getNum());
	        		}
	        	} else {
	        		textHolder.dingTV.setSelected(false);
	        		textHolder.caiTV.setSelected(true);
	        		if(joke.getOpposesNum() < dingOrCai.getNum()) {
	        			joke.setOpposesNum(dingOrCai.getNum());
	        		}
	        	}
	        } else {
	        	textHolder.dingTV.setSelected(false);
	        	textHolder.caiTV.setSelected(false);
	        }
	        textHolder.createTimeTV.setText(Util.getFormatDate(joke.getCreateDate()));
	        textHolder.dingTV.setText(String.valueOf(joke.getSupportsNum()));
	        textHolder.caiTV.setText(String.valueOf(joke.getOpposesNum()));
	        textHolder.contentTV.setText(joke.getContent());
	        textHolder.commentTV.setText(String.valueOf(joke.getCommentNum()));
	        textHolder.dingTV.setOnClickListener(dingListener);
	        textHolder.caiTV.setOnClickListener(caiListener);
	        textHolder.commentTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, QuShiDetailActivity.class);
					intent.putExtra("qushi", joke);
					intent.putExtra("isComment", true);
					mContext.startActivity(intent);
					App.isStartOtherActivity = true;
				}
			});
	        textHolder.shareTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSharePopWindow.setmJoke(joke);
					mSharePopWindow.showAtLocation(v.getRootView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
				}
			});
	        //是自己发的
	        if(App.currentUser != null && App.currentUser.getId()==joke.getUserId()) {
	        	textHolder.userNickTV.setText(App.currentUser.getUserNike());
	        	BitmapUtil.display(textHolder.userPortraitIV, App.currentUser.getPortraitUrl());
	        } else {
	        	textHolder.userNickTV.setText(joke.getUserNike());
	        	BitmapUtil.display(textHolder.userPortraitIV, joke.getPortraitUrl());
	        }
			break;
		case TYPE_IMG:
			if(dingOrCai != null) {
	        	if(dingOrCai.isDing()) {
	        		imgHolder.dingTV.setSelected(true);
	        		imgHolder.caiTV.setSelected(false);
	        		if(joke.getSupportsNum() < dingOrCai.getNum()) {
	        			joke.setSupportsNum(dingOrCai.getNum());
	        		}
	        	} else {
	        		imgHolder.dingTV.setSelected(false);
	        		imgHolder.caiTV.setSelected(true);
	        		if(joke.getOpposesNum() < dingOrCai.getNum()) {
	        			joke.setOpposesNum(dingOrCai.getNum());
	        		}
	        	}
	        } else {
	        	imgHolder.dingTV.setSelected(false);
	        	imgHolder.caiTV.setSelected(false);
	        }
			int width = joke.getImgWidth();
	        int height = joke.getImgHeight();
	        if(width != 0 && width != 0) {
	        	float scale = (float)width / (float)mMaxWidth;
	            height = (int)(height / scale);
	            if(height > mMaxHeight) {
	            	height = mMaxHeight;
	            }
	            imgHolder.contentIV.setLayoutParams(new LinearLayout.LayoutParams(mMaxWidth, height));
	        }
	        imgHolder.contentIV.setProgress(0);
	        imgHolder.contentIV.setComplete(false);
	        imgHolder.createTimeTV.setText(Util.getFormatDate(joke.getCreateDate()));
	        if(Util.isNotEmpty(joke.getTitle())) {
	        	imgHolder.titleTV.setText(joke.getTitle());
	        	imgHolder.titleTV.setVisibility(View.VISIBLE);
	        } else {
	        	imgHolder.titleTV.setVisibility(View.GONE);
	        }
	        imgHolder.dingTV.setText(String.valueOf(joke.getSupportsNum()));
	        imgHolder.caiTV.setText(String.valueOf(joke.getOpposesNum()));
	        imgHolder.commentTV.setText(String.valueOf(joke.getCommentNum()));
	        imgHolder.dingTV.setOnClickListener(dingListener);
	        imgHolder.caiTV.setOnClickListener(caiListener);
	        imgHolder.commentTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, TuDetailActivity.class);
					intent.putExtra("content", joke);
					intent.putExtra("isComment", true);
					mContext.startActivity(intent);
					App.isStartOtherActivity = true;
				}
			});
	        imgHolder.shareTV.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mSharePopWindow.setmJoke(joke);
					mSharePopWindow.showAtLocation(v.getRootView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
				}
			});
	        //图片的加载放后面
	        //是自己发的
	        if(App.currentUser != null && App.currentUser.getId()==joke.getUserId()) {
	        	imgHolder.userNickTV.setText(App.currentUser.getUserNike());
	        	BitmapUtil.display(imgHolder.userPortraitIV, App.currentUser.getPortraitUrl());
	        } else {
	        	imgHolder.userNickTV.setText(joke.getUserNike());
	        	BitmapUtil.display(imgHolder.userPortraitIV, joke.getPortraitUrl());
	        }
	        if(HttpUtil.isWifi(mContext) || mSpUtil.getBoolean(Constants.IS_LOAD_IMG, true)) {  //仅wifi状态下加载网络
	        	imgHolder.contentIV.setLoadImg(true);
	        	BitmapUtil.display(imgHolder.contentIV, joke.getImgUrl(), new PercentImageViewLoadCallBack());
	        } else {
	        	imgHolder.contentIV.setLoadImg(false);
	        }
			break;
		}
	    
        return convertView;
	}

	private class TextHolder {
		private ImageView userPortraitIV;
		private TextView userNickTV;
		private TextView createTimeTV;
		private TextView contentTV;
		private TextView dingTV;
		private TextView caiTV;
		private TextView commentTV;
		private TextView shareTV;
    }
	
	private class ImgHolder {
		private ImageView userPortraitIV;
		private TextView userNickTV;
		private TextView createTimeTV;
		private LoadPercentImageView contentIV;
		private TextView titleTV;
		private TextView dingTV;
		private TextView caiTV;
		private TextView commentTV;
		private TextView shareTV;
    }
	
	class DingClickListenner implements OnClickListener {
		private DingOrCai dingOrCai;
		private Joke joke;
		public DingClickListenner(DingOrCai dingOrCai, Joke joke) {
			this.dingOrCai = dingOrCai;
			this.joke = joke;
		}
		
		@Override
		public void onClick(View v) {
			if(dingOrCai != null) {
				if(dingOrCai.isDing()) {
					ToastUtils.showMessage(mContext, R.string.has_ding);
				} else {
					ToastUtils.showMessage(mContext, R.string.has_cai);
				}
				return;
			}
			if(v.isSelected()) {
				ToastUtils.showMessage(mContext, R.string.has_ding);
				return;
			}
			View caiView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.cai_tv);
			if(caiView.isSelected()) {
				ToastUtils.showMessage(mContext, R.string.has_cai);
				return;
			}
			final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
			Animation addOneAnimation = AnimationUtils.loadAnimation(mContext, R.anim.add_one);
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
			joke.setSupportsNum(joke.getSupportsNum()+1);
			((TextView)v).setText(String.valueOf(joke.getSupportsNum()));
			mDingOrCaiDAO.dingOrCai(mUserId, joke.getId(), DingOrCai.DING, joke.getSupportsNum());
		}
	}
	
	class CaiClickListenner implements OnClickListener {
		private DingOrCai dingOrCai;
		private Joke joke;
		public CaiClickListenner(DingOrCai dingOrCai, Joke joke) {
			this.dingOrCai = dingOrCai;
			this.joke = joke;
		}
		
		@Override
		public void onClick(View v) {
			if(dingOrCai != null) {
				if(dingOrCai.isDing()) {
					ToastUtils.showMessage(mContext, R.string.has_ding);
				} else {
					ToastUtils.showMessage(mContext, R.string.has_cai);
				}
				return;
			}
			if(v.isSelected()) {
				ToastUtils.showMessage(mContext, R.string.has_cai);
				return;
			}
			View dingView = ((LinearLayout)v.getParent().getParent()).findViewById(R.id.ding_tv);
			if(dingView.isSelected()) {
				ToastUtils.showMessage(mContext, R.string.has_ding);
				return;
			}
			final View view = ((RelativeLayout)v.getParent()).getChildAt(1);
			Animation addOneAnimation = AnimationUtils.loadAnimation(mContext, R.anim.add_one);
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
			joke.setOpposesNum(joke.getOpposesNum()+1);
			((TextView)v).setText(String.valueOf(joke.getOpposesNum()));
			mDingOrCaiDAO.dingOrCai(mUserId, joke.getId(), DingOrCai.CAI, joke.getOpposesNum());
		}
	}
	
}
