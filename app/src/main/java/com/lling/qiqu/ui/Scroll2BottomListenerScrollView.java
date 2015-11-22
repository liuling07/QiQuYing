package com.lling.qiqu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * @ClassName: Scroll2BottomListenerScrollView
 * @Description: 监听滚动到底部的scrollview，并且调用listview的onfootloading方法加载更多（适合scrollview与listview嵌套使用）
 * @author lling
 * @date 2015年7月29日
 */
public class Scroll2BottomListenerScrollView extends ScrollView {

	private LoadListView mListView;
	
	public Scroll2BottomListenerScrollView(Context context) {  
        super(context);  
    }  
  
    public Scroll2BottomListenerScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
    
    
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    	if(t + getHeight() >=  computeVerticalScrollRange()) {
    		if(mListView != null) {
    			mListView.prepareFootLoading();
    		}
    	}
    }
    
    
    public void setmListView(LoadListView mListView) {
		this.mListView = mListView;
	}
    

}
