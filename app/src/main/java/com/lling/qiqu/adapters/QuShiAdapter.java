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
import com.lling.qiqu.beans.DingOrCai;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.dao.DingCaiDAO;
import com.lling.qiqu.ui.popwindows.SharePopWindow;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.Util;

/**
 * @ClassName: QuShiAdapter
 * @Description: 趣事item适配器
 * @author lling
 * @date 2015-6-15
 */
public class QuShiAdapter extends BaseAdapter{
	
	private List<Joke> mList;
	private Context mContext;
	private DingCaiDAO mDingOrCaiDAO;
	private SharePopWindow mSharePopWindow;
	int mUserId = -1;
 	
    public QuShiAdapter(Context context) {
    	mContext = context;
    	mDingOrCaiDAO = new DingCaiDAO(context);
    	mSharePopWindow = new SharePopWindow(context);
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
        if (convertView == null) {
        	holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.listitem_qushi, null);
            holder.userPortraitIV = (ImageView) convertView.findViewById(R.id.user_img);
            holder.userNickTV = (TextView) convertView.findViewById(R.id.user_nick);
            holder.createTimeTV = (TextView) convertView.findViewById(R.id.create_date);
            holder.contentTV = (TextView) convertView.findViewById(R.id.qushi_content);
            holder.dingTV = (TextView) convertView.findViewById(R.id.ding_tv);
            holder.caiTV = (TextView) convertView.findViewById(R.id.cai_tv);
            holder.commentTV = (TextView) convertView.findViewById(R.id.comment_tv);
            holder.shareTV = (TextView) convertView.findViewById(R.id.share_tv);
            convertView.setTag(holder);
        } else {
        	holder = (ViewHolder) convertView.getTag();
        }
        final Joke joke = mList.get(position);
        if(App.currentUser != null) {
        	mUserId = App.currentUser.getId();
        }
        final DingOrCai dingOrCai = mDingOrCaiDAO.getDingOrCai(mUserId, joke.getId());
        if(dingOrCai != null) {
        	if(dingOrCai.isDing()) {
        		holder.dingTV.setSelected(true);
        		holder.caiTV.setSelected(false);
        		if(joke.getSupportsNum() < dingOrCai.getNum()) {
        			joke.setSupportsNum(dingOrCai.getNum());
        		}
        	} else {
        		holder.dingTV.setSelected(false);
        		holder.caiTV.setSelected(true);
        		if(joke.getOpposesNum() < dingOrCai.getNum()) {
        			joke.setOpposesNum(dingOrCai.getNum());
        		}
        	}
        } else {
        	holder.dingTV.setSelected(false);
    		holder.caiTV.setSelected(false);
        }
        
        holder.createTimeTV.setText(Util.getFormatDate(joke.getCreateDate()));
        holder.dingTV.setText(String.valueOf(joke.getSupportsNum()));
        holder.caiTV.setText(String.valueOf(joke.getOpposesNum()));
        holder.contentTV.setText(joke.getContent());
        holder.commentTV.setText(String.valueOf(joke.getCommentNum()));
        holder.dingTV.setOnClickListener(new OnClickListener() {
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
		});
        holder.caiTV.setOnClickListener(new OnClickListener() {
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
		});
        holder.commentTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, QuShiDetailActivity.class);
				intent.putExtra("qushi", joke);
				intent.putExtra("isComment", true);
				mContext.startActivity(intent);
				App.isStartOtherActivity = true;
			}
		});
        holder.shareTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSharePopWindow.setmJoke(joke);
				mSharePopWindow.showAtLocation(v.getRootView(), Gravity.LEFT | Gravity.BOTTOM, 0, 0);
			}
		});
        //是自己发的
        if(App.currentUser != null && App.currentUser.getId()==joke.getUserId()) {
            holder.userNickTV.setText(App.currentUser.getUserNike());
            BitmapUtil.display(holder.userPortraitIV, App.currentUser.getPortraitUrl());
        } else {
            holder.userNickTV.setText(joke.getUserNike());
            BitmapUtil.display(holder.userPortraitIV, joke.getPortraitUrl());
        }
        return convertView;
	}

	private class ViewHolder {
		private ImageView userPortraitIV;
		private TextView userNickTV;
		private TextView createTimeTV;
		private TextView contentTV;
		private TextView dingTV;
		private TextView caiTV;
		private TextView commentTV;
		private TextView shareTV;
    }
	
}
