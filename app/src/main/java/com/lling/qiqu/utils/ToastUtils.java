package com.lling.qiqu.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.lling.qiqu.R;

/**
 * @ClassName: ToastUtils
 * @Description: Toast提示工具类 
 * @author lling
 * @date 2015-6-7
 */
public class ToastUtils {  
    private static Handler mHandler = new Handler(Looper.getMainLooper());  
    private static Toast mToast;
    protected static LayoutInflater inflater;
    private static int marginBottem = 48;

    /** 
     * Toast发送消息，默认Toast.LENGTH_SHORT 
     * @param act 
     * @param msg 
     */ 
    public static void showMessage(final Context act, final String msg) {  
        showMessage(act, msg, Toast.LENGTH_SHORT);  
    }
    
    /** 
     * Toast发送消息，默认Toast.LENGTH_SHORT 
     * @param act 
     * @param msg 
     */ 
    public static void showMessageInCenter(final Context act, final String msg) {  
    	/*new Thread(new Runnable() {  
            public void run() {  */
            	mHandler.post(new Runnable() {  
                    @Override 
                    public void run() {  
                    	if (mToast != null) {
                    		mToast.setGravity(Gravity.CENTER, 0, 0);
                    		mToast.setText(msg);
                    	} else {
                    		mToast = Toast.makeText(act, msg, Toast.LENGTH_SHORT);
                    		if(inflater == null) {
                    			inflater = (LayoutInflater) act
                        	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    		}
                    		mToast.setView(inflater.inflate(R.layout.toast, null));
                    		mToast.setGravity(Gravity.CENTER, 0, 0);
                    		mToast.setText(msg);
                    	}
                    	mToast.show();
                    }  
                });  
           /* }  
        }).start();  */
    }  
       
    /** 
     * Toast发送消息，默认Toast.LENGTH_LONG 
     * @param act 
     * @param msg 
     */ 
    public static void showMessageLong(final Context act, final String msg) {  
        showMessage(act, msg, Toast.LENGTH_LONG);  
    }  
   
    /** 
     * Toast发送消息，默认Toast.LENGTH_SHORT 
     * @param act 
     * @param msg 
     */ 
    public static void showMessage(final Context act, final int msg) {  
        showMessage(act, msg, Toast.LENGTH_SHORT);  
    }  
       
    /** 
     * Toast发送消息，默认Toast.LENGTH_LONG 
     * @param act 
     * @param msg 
     */ 
    public static void showMessageLong(final Context act, final int msg) {  
        showMessage(act, msg, Toast.LENGTH_LONG);  
    }  
   
    /** 
     * Toast发送消息 
     * @param act 
     * @param msg 
     * @param len 
     */ 
    public static void showMessage(final Context act, final int msg,  
            final int len) {  
        /*new Thread(new Runnable() {  
            public void run() {  */
            	mHandler.post(new Runnable() {  
                    @Override 
                    public void run() {  
                    	if (mToast != null) {
                    		mToast.setText(msg);
//                    		mToast.setGravity(Gravity.BOTTOM, 0, marginBottem);
                    		mToast.setGravity(Gravity.CENTER, 0, 0);
                    	} else {
                    		mToast = Toast.makeText(act, msg, len);
                    		if(inflater == null) {
                    			inflater = (LayoutInflater) act
                        	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    		}
                    		mToast.setView(inflater.inflate(R.layout.toast, null));
//                    		mToast.setGravity(Gravity.BOTTOM, 0, marginBottem);
                    		mToast.setGravity(Gravity.CENTER, 0, 0);
                    		mToast.setText(msg);
                    	}
                    	mToast.show();
                    }  
                });  
           /* }  
        }).start();*/  
    }  
       
    /** 
     * Toast发送消息 
     * @param act 
     * @param msg 
     * @param len 
     */ 
    public static void showMessage(final Context act, final String msg,  
            final int len) {  
        /*new Thread(new Runnable() {  
            public void run() {*/  
            	mHandler.post(new Runnable() {  
                    @Override 
                    public void run() {  
                    	if (mToast != null) {
                    		mToast.setText(msg);
//                    		mToast.setGravity(Gravity.BOTTOM, 0, marginBottem);
                    		mToast.setGravity(Gravity.CENTER, 0, 0);
                    	} else {
                    		mToast = Toast.makeText(act, msg, len);
                    		if(inflater == null) {
                    			inflater = (LayoutInflater) act
                        	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    		}
                    		mToast.setView(inflater.inflate(R.layout.toast, null));
//                    		mToast.setGravity(Gravity.BOTTOM, 0, marginBottem);
                    		mToast.setGravity(Gravity.CENTER, 0, 0);
                    		mToast.setText(msg);
                    	}
                    	mToast.show();
                    }  
                });  
            /*}  
        }).start();  */
    }  
   
}  
