package com.lling.qiqu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lling.qiqu.R;
import com.lling.qiqu.utils.BitmapUtil;

public class LoadListView extends ListView implements
		OnScrollListener {

    private LayoutInflater inflater;  
  
    //上啦加载布局
    private View footView;
    
    private ImageView footerProgressImg;
    private Animation progressAnimation;
    
    private boolean haveScrollbar = true;  
  
    public LoadListView(Context context) {  
        super(context);  
        init(context);  
    }  
  
    public LoadListView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
    }  
  
    private void init(Context context) { 
    	progressAnimation = AnimationUtils.loadAnimation(context,
				R.anim.progress_anim);
    	inflater = LayoutInflater.from(context);
        footView = inflater.inflate(R.layout.footer, null);
//        footView.findViewById(R.id.load_layout).setVisibility(View.GONE);
        footerProgressImg = (ImageView) footView.findViewById(R.id.progress);
        footerProgressImg.startAnimation(progressAnimation);
        addFooterView(footView, null, false);
        // 设置滚动监听事件  
//        setOnScrollListener(this);  
        //设置listview加载时不滚动
		setOnScrollListener(new PauseOnScrollListener(BitmapUtil.mBitmapUtils, false, true, this));
  
    }  
	private boolean isLoadingable = false; //是否可以加载更多
	private boolean hasMoreData = false;  //有更多数据
	//正在加载底部数据
	private boolean isFootLoading = false;
	
	public void setHasMoreData(boolean hasMoreData) {
		this.hasMoreData = hasMoreData;
		if(!hasMoreData) {
			footView.findViewById(R.id.load_layout).setVisibility(View.GONE);
		}
	}
    @Override  
    public void onScrollStateChanged(AbsListView view, int scrollState) {  
    	if(isFootLoading)
			return;		
		if(isLoadingable && scrollState==SCROLL_STATE_IDLE && hasMoreData){
			prepareFootLoading();
		}
    }  
    
    public void prepareFootLoading() {
    	if(isFootLoading) {
    		return;
    	}
    	footerProgressImg.startAnimation(progressAnimation);
		footView.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
		isFootLoading = true;
		setSelectionFromTop(getCount()-1, 0);  //滚动到最底端
		onFootLoading();
    }
  
    @Override  
    public void onScroll(AbsListView view, int firstVisibleItem,  
        int visibleItemCount, int totalItemCount) {  
		//因为刚进入的时候,lastPos=-1,count=0,这个时候不能让它执行onAddFoot方法
		if(getLastVisiblePosition()==totalItemCount-1 && !isLoadingable && getLastVisiblePosition() != -1){
			isLoadingable = true;
		}	
    }  
  
    /**
	 * 上拉刷新监听器
	 *
	 */
	public interface OnFootLoadingListener{
		/**
		 * 这里是执行后台获取数据的过程
		 */
		void onFootLoading();
	}
	private OnFootLoadingListener onFootLoadingListener;
	public void setOnFootLoadingListener(
			OnFootLoadingListener onFootLoadingListener) {
		this.onFootLoadingListener = onFootLoadingListener;
	}
	
	//加载更多
	private void onFootLoading() {
		if(onFootLoadingListener != null) {
			onFootLoadingListener.onFootLoading();
		}
	}
	
	/**
	 * 底部数据加载完成,用户需要加入一个removeFootView的操作
	 */
	public void onFootLoadingComplete(boolean goneFooter){
		isLoadingable = false;
		isFootLoading = false;
		if(goneFooter){
			if(progressAnimation != null) {
				progressAnimation.cancel();
			}
			footView.findViewById(R.id.load_layout).setVisibility(View.GONE);
		}		
	}
  
    public void setMoreDataMsg(String moreDataMsg) {
    	if(footView != null) {
    		((TextView) footView.findViewById(R.id.more_data_msg)).setText(moreDataMsg);
    	}
	}
    
    public void setHaveScrollbar(boolean haveScrollbar) {   
        this.haveScrollbar = haveScrollbar;   
    }  
	
	@Override   
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
        if (haveScrollbar == false) {   
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
            super.onMeasure(widthMeasureSpec, expandSpec);   
        } else {   
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);   
        }   
    } 
}  
