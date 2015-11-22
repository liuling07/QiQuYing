package com.lling.qiqu.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.beans.Comment;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.Util;

/**
 * @ClassName: CommentAdapter
 * @Description: 评论Adapter
 * @author lling
 * @date 2015-6-23
 */
public class CommentAdapter extends BaseAdapter{
	
	private List<Comment> mList;
	private Context mContext;
	
    public CommentAdapter(Context context) {
    	mContext = context;
	}
    
    public void setList(List<Comment> lists) {
		this.mList = lists;
	}
    
    public void onDataChange(List<Comment> lists) {
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
		return null;
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
                    R.layout.listitem_comment, null);
            holder.comment_user_img = (ImageView) convertView.findViewById(R.id.comment_user_img);
            holder.comment_user_name = (TextView) convertView.findViewById(R.id.comment_user_nick);
            holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);
//            holder.comment_support_num = (TextView) convertView.findViewById(R.id.comment_support_num);
            holder.comment_content = (TextView) convertView.findViewById(R.id.comment_content);
            convertView.setTag(holder);
        } else {
        	holder = (ViewHolder) convertView.getTag();
        }
        Comment comment = mList.get(position);
        //是自己发的
        if(App.currentUser != null && App.currentUser.getId()==comment.getUserId()) {
        	BitmapUtil.display(holder.comment_user_img, App.currentUser.getPortraitUrl());
            holder.comment_user_name.setText(App.currentUser.getUserNike());
        } else {
        	BitmapUtil.display(holder.comment_user_img, comment.getPortraitUrl());
            holder.comment_user_name.setText(comment.getUserNike());
        }
        
        holder.comment_time.setText(Util.getFormatDate(comment.getCreateDate()));
        holder.comment_content.setText(comment.getContent());
        /*holder.comment_support_num.setText(String.valueOf(comment.getSupportsNum()));
        holder.comment_support_num.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtils.showMessage(mContext, "加1");
			}
		});*/
        
        return convertView;
	}

	
	private class ViewHolder {
		private ImageView comment_user_img;
		private TextView comment_user_name;
		private TextView comment_time;
//		private TextView comment_support_num;
//		private ImageView comment_support_img;
		private TextView comment_content;
    }
	
}
