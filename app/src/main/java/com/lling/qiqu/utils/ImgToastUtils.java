package com.lling.qiqu.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lling.qiqu.R;

/**
 * @ClassName: ToastUtils
 * @Description: Toast提示工具类 
 * @author lling
 * @date 2015-6-7
 */
public class ImgToastUtils {  
    private static Handler mHandler = new Handler(Looper.getMainLooper());  
    private static Toast mToast;
    protected static LayoutInflater inflater;
    private static View view;

    /** 
     * Toast发送消息，默认Toast.LENGTH_SHORT 
     * @param act 
     * @param msg 
     */ 
    public static void showMessage(final Context act, final String msg, int imgRes) {  
        showMessage(act, msg, Toast.LENGTH_SHORT, imgRes);  
    }
    
    /** 
     * Toast发送消息，默认Toast.LENGTH_LONG 
     * @param act 
     * @param msg 
     */ 
    public static void showMessageLong(final Context act, final String msg, int imgRes) {  
        showMessage(act, msg, Toast.LENGTH_LONG, imgRes);  
    }  
   
    /** 
     * Toast发送消息，默认Toast.LENGTH_SHORT 
     * @param act 
     * @param msg 
     */ 
    public static void showMessage(final Context act, final int msg, int imgRes) {  
        showMessage(act, msg, Toast.LENGTH_SHORT, imgRes);  
    }  
       
    /** 
     * Toast发送消息，默认Toast.LENGTH_LONG 
     * @param act 
     * @param msg 
     */ 
    public static void showMessageLong(final Context act, final int msg, int imgRes) {  
        showMessage(act, msg, Toast.LENGTH_LONG, imgRes);  
    }  
   
    /** 
     * Toast发送消息 
     * @param act 
     * @param msg 
     * @param len 
     */ 
    public static void showMessage(final Context act, final int msg,  
            final int len, final int imgRes) {  
    	mHandler.post(new Runnable() {  
            @Override 
            public void run() {  
            	if (mToast != null) {
            		if(inflater == null) {
            			inflater = (LayoutInflater) act
                	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            		}
            		if(view == null) {
            			view = inflater.inflate(R.layout.center_toast, null);
            		}
            		((ImageView)view.findViewById(R.id.image)).setImageResource(imgRes);
            		mToast.setView(view);
            		mToast.setText(msg);
            	} else {
            		mToast = Toast.makeText(act, msg, len);
            		mToast.setGravity(Gravity.CENTER, 0, 0);
            		if(inflater == null) {
            			inflater = (LayoutInflater) act
                	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            		}
            		if(view == null) {
            			view = inflater.inflate(R.layout.center_toast, null);
            		}
            		((ImageView)view.findViewById(R.id.image)).setImageResource(imgRes);
            		mToast.setView(view);
            		mToast.setText(msg);
            	}
            	mToast.show();
            }  
        });  
    }  
       
    public static void showMessage(final Context act, final String msg,  
    		final int len, final int imgRes) {  
    	mHandler.post(new Runnable() {  
            @Override 
            public void run() {  
            	if (mToast != null) {
            		if(inflater == null) {
            			inflater = (LayoutInflater) act
                	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            		}
            		if(view == null) {
            			view = inflater.inflate(R.layout.center_toast, null);
            		}
            		((ImageView)view.findViewById(R.id.image)).setImageResource(imgRes);
            		mToast.setView(view);
            		mToast.setText(msg);
            	} else {
            		mToast = Toast.makeText(act, msg, len);
            		mToast.setGravity(Gravity.CENTER, 0, 0);
            		if(inflater == null) {
            			inflater = (LayoutInflater) act
                	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            		}
            		if(view == null) {
            			view = inflater.inflate(R.layout.center_toast, null);
            		}
            		((ImageView)view.findViewById(R.id.image)).setImageResource(imgRes);
            		mToast.setView(view);
            		mToast.setText(msg);
            	}
            	mToast.show();
            }  
        });  
    }  
   
}  
