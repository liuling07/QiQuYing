package com.lling.qiqu.ui.popwindows;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.activitys.MyCollectActivity;
import com.lling.qiqu.beans.Collect;
import com.lling.qiqu.beans.Joke;
import com.lling.qiqu.commons.RcpUri;
import com.lling.qiqu.dao.CollectDAO;
import com.lling.qiqu.service.IUserService;
import com.lling.qiqu.service.impl.UserServiceImpl;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.FastjsonUtil;
import com.lling.qiqu.utils.ImgToastUtils;
import com.lling.qiqu.utils.ImgUtil;
import com.lling.qiqu.utils.ToastUtils;
import com.sina.weibo.AccessTokenKeeper;
import com.sina.weibo.UsersAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.tencent.weibo.Constants;
import com.tencent.weibo.sdk.android.api.WeiboAPI;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.Authorize;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;
import com.tencent.weibo.sdk.android.model.AccountModel;
import com.tencent.weibo.sdk.android.model.BaseVO;
import com.tencent.weibo.sdk.android.model.ModelResult;
import com.tencent.weibo.sdk.android.network.HttpCallback;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: SharePopWindow
 * @Description: 分享弹出窗
 * @author lling
 * @date 2015年7月13日
 */
public class SharePopWindow extends PopupWindow implements OnClickListener, IWeiboHandler.Response {
	private final String TAG = "SharePopWindow";
	private Context context;
	private Joke mJoke;  //要分享的内容
	private CollectDAO collectDAO;
	private Collect collect;
	IWXAPI api;   //微信api
	private WeiboAPI weiBoAPI;//qq微博API
	private String accessToken = null;  //qq微博token
	
	/** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;
	
	//分享时app Logo地址
	private String appLogoUrl = com.lling.qiqu.commons.Constants.LOGP_URL;
	//分享时点击链接地址
	private String targetUrl = RcpUri.INTERFACE_URI_SHARE;
	private int userId = -1;
	private String title;
	private String description; 
	
	public SharePopWindow(Context context) {
		this.context = context;
		collectDAO = new CollectDAO(context);
		api = WXAPIFactory.createWXAPI(context, com.lling.qiqu.wxapi.Constants.APP_ID);
		LayoutInflater inflater = (LayoutInflater) context
	                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflater.inflate(R.layout.popup_share, null);
	    view.findViewById(R.id.share_weixin).setOnClickListener(this);
	    view.findViewById(R.id.share_pengyouquan).setOnClickListener(this);
	    view.findViewById(R.id.share_qqfriends).setOnClickListener(this);
	    view.findViewById(R.id.share_qqzone).setOnClickListener(this);
	    view.findViewById(R.id.share_qqweibo).setOnClickListener(this);
	    view.findViewById(R.id.share_sinaweibo).setOnClickListener(this);
	    view.findViewById(R.id.share_sms).setOnClickListener(this);
	    view.findViewById(R.id.share_copy).setOnClickListener(this);
	    view.findViewById(R.id.share_collect).setOnClickListener(this);
		initWindow();
		this.setContentView(view);
	}
	
	/**
     * 初始化窗口
     */
    private void initWindow() {
    	this.setClippingEnabled(false);
    	this.setWidth(LayoutParams.MATCH_PARENT);
        //设置弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置弹出窗体可点击
        this.setFocusable(true);
        //设置弹出窗体动画效果
        this.setAnimationStyle(R.style.inoutformbottom);
        //实例化一个ColorDrawable颜色为半透明   (在此设置背景后此窗口不会出现黑色边框)
        ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        
		//popupwindow消失的时候让窗口背景恢复
		this.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				Activity activity = ((Activity) context).getParent();
				if (activity == null) {
					activity = ((Activity) context);
				}
				WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
				lp.alpha = 1f;
				activity.getWindow().setAttributes(lp);
			}
		});
    }
    
    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
    	if(App.currentUser != null) {
			userId = App.currentUser.getId();
    	}
    	collect = collectDAO.getCollect(userId, mJoke.getId());
    	if(collect != null) {  //收藏过
    		getContentView().findViewById(R.id.share_collect).setSelected(true);
    	} else {
    		getContentView().findViewById(R.id.share_collect).setSelected(false);
    	}
    	Activity activity = ((Activity)context).getParent();
    	if(activity == null) {
    		activity = ((Activity)context);
    	}
    	WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = 0.4f;
		activity.getWindow().setAttributes(lp);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		super.showAtLocation(parent, gravity, x, y);
    }

	@Override
	public void onClick(View v) {
		if(mJoke == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.share_weixin:   //微信
			share2WeiXin(SendMessageToWX.Req.WXSceneSession);
			MobclickAgent.onEvent(context, "share_weixin");
			saveShareTimes();
			break;
		case R.id.share_pengyouquan:  //朋友圈
			share2WeiXin(SendMessageToWX.Req.WXSceneTimeline);
			MobclickAgent.onEvent(context, "share_pengyouquan");
			saveShareTimes();
			break;
		case R.id.share_qqfriends:  //QQ好友
			share2QQFriends();
			MobclickAgent.onEvent(context, "share_qq");
			saveShareTimes();
			break;
		case R.id.share_qqzone:  //QQ空间
			share2QZone();
			MobclickAgent.onEvent(context, "share_qqzone");
			saveShareTimes();
			break;
		case R.id.share_qqweibo:  //QQ微博
			share2QQWeibo();
			MobclickAgent.onEvent(context, "share_qqweibo");
			saveShareTimes();
			break;
		case R.id.share_sinaweibo:  //新浪微博
			share2SinaWeibo();
			MobclickAgent.onEvent(context, "share_sina_weibo");
			saveShareTimes();
			break;
		case R.id.share_sms:  //短信
			sendSms();
			MobclickAgent.onEvent(context, "share_sms");
			break;
		case R.id.share_copy:  //复制
			copy();
			MobclickAgent.onEvent(context, "share_copy");
			break;
		case R.id.share_collect:  //收藏
			if(collect == null) {  
				v.setSelected(true);
				collect();
			} else {   //取消收藏
				v.setSelected(false);
				cancelCollect();
			}
			break;
		}
		this.dismiss();
	}
	
	/**
	 * 分享到微信朋友或者朋友圈
	 * @param scene 标记是发送朋友圈还是微信好友
	 */
	private void share2WeiXin(int scene) {
		if(!api.isWXAppInstalled()) {
			if(scene == SendMessageToWX.Req.WXSceneSession) {
				ToastUtils.showMessageInCenter(context, "您没有安装【微信】\n安装后才能分享到微信好友");
			} else {
				ToastUtils.showMessageInCenter(context, "您没有安装【微信】\n安装后才能分享到朋友圈");
			}
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = targetUrl + mJoke.getId();
		WXMediaMessage msg = new WXMediaMessage(webpage);
		Bitmap thumb = null;
		switch (mJoke.getType()) {
		case Joke.TYPE_QUSHI:
			msg.title = context.getString(R.string.share_title_qushi);
			msg.description = mJoke.getContent();
			thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
			break;
		case Joke.TYPE_QUTU:
			msg.title = context.getString(R.string.share_title_qutu);
			msg.description = mJoke.getTitle();
			//微信分享缩略图最大不能超过32K,否则无法调开微信界面
			thumb = BitmapUtil.getThumbBitmapFromCache(mJoke.getImgUrl(), 100, 100);
			break;
		case Joke.TYPE_MEITU:
			msg.title = context.getString(R.string.share_title_meitu);
			msg.description = mJoke.getTitle();
			thumb = BitmapUtil.getThumbBitmapFromCache(mJoke.getImgUrl(), 100, 100);
			break;
		}
		msg.thumbData = ImgUtil.bmpToByteArray(thumb, true);
		
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = scene;
		api.sendReq(req);
		thumb.recycle();
	}
	
	/**
	 * 分享到QQ好友
	 */
	private void share2QQFriends() {
		final Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
	    switch (mJoke.getType()) {
		case Joke.TYPE_QUSHI:
			params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.share_title_qushi));
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  mJoke.getContent().trim());
			//这里应该填写应用图标的图片
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, appLogoUrl);
			break;
		case Joke.TYPE_QUTU:
			params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.share_title_qutu));
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mJoke.getTitle());
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mJoke.getImgUrl());
			break;
		case Joke.TYPE_MEITU:
			params.putString(QQShare.SHARE_TO_QQ_TITLE, context.getString(R.string.share_title_meitu));
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mJoke.getTitle());
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mJoke.getImgUrl());
			break;
		}
	    //加入点击链接
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl + mJoke.getId());
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getString(R.string.app_name));
	    App.mTencent.shareToQQ((Activity)context, params, iUiListener);
	}
	
	/**
	 * 分享到QQ空间
	 */
	private void share2QZone() {
		ArrayList<String> imgList = new ArrayList<String>();
		//分享类型
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		switch (mJoke.getType()) {
		case Joke.TYPE_QUSHI:
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, context.getString(R.string.share_title_qushi));
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,  mJoke.getContent().trim());
			//这里应该填写应用图标的图片
			imgList.add(appLogoUrl);
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgList);
			break;
		case Joke.TYPE_QUTU:
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, context.getString(R.string.share_title_qutu));
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,  mJoke.getTitle());
			imgList.add(mJoke.getImgUrl());
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgList);
			break;
		case Joke.TYPE_MEITU:
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, context.getString(R.string.share_title_meitu));
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mJoke.getTitle());
			imgList.add(mJoke.getImgUrl());
			params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgList);
			break;
		}
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl + mJoke.getId());//必填
		App.mTencent.shareToQzone((Activity)context, params, iUiListener);
	}
	
	private IUiListener iUiListener = new IUiListener() {
		@Override
		public void onError(UiError arg0) {
			ToastUtils.showMessageInCenter(context, "分享失败，请重试");
			Log.e("IUiListener", "qq share error" + arg0.errorCode + arg0.errorMessage + arg0.errorDetail);
		}
		@Override
		public void onComplete(Object arg0) {
			ImgToastUtils.showMessage(context, "分享成功", R.drawable.center_ok_tip);
		}
		@Override
		public void onCancel() {
		}
	};
	
	/**
	 * 分享到新浪微博
	 */
	private void share2SinaWeibo() {
		Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
		if(accessToken != null && accessToken.isSessionValid()) {
			initShareWeiboAPI();
	        share2weibo();
		} else {   //需要授权登录
			initSinaWeiBo();
			initShareWeiboAPI();
			mSsoHandler.authorize(new AuthListener());   //授权后再分享
		}
	}
	
	private void initShareWeiboAPI() {
		if(mWeiboShareAPI == null) {
			mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, com.sina.weibo.Constants.APP_KEY);
		}
		// 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();
        mWeiboShareAPI.handleWeiboResponse(((Activity)context).getIntent(), this);
	}
	
	//新浪微博登录
	private AuthInfo mAuthInfo;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
	// 创建微博实例
    private void initSinaWeiBo() {
        //mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
    	if(mAuthInfo == null) {
    		mAuthInfo = new AuthInfo(context, com.sina.weibo.Constants.APP_KEY, com.sina.weibo.Constants.REDIRECT_URL, com.sina.weibo.Constants.SCOPE);
    	}
        if(mSsoHandler == null) {
        	mSsoHandler = new SsoHandler((Activity)context, mAuthInfo);
        }
    }
	
	private void share2weibo() {
		setShareContent();
		// 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.mediaObject = getWebpageObj();
        weiboMessage.textObject = getTextObj();    //这里可以设置文本
        if(mJoke.getType() != Joke.TYPE_QUSHI) {
        	weiboMessage.imageObject = getImageObj();
        }
       // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        AuthInfo authInfo = new AuthInfo(context, com.sina.weibo.Constants.APP_KEY, com.sina.weibo.Constants.REDIRECT_URL, com.sina.weibo.Constants.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest((Activity)context, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onWeiboException( WeiboException arg0 ) {
            	Log.e(TAG, "share sina weibo error", arg0);
            	ToastUtils.showMessage(context, "分享失败");
            }
            @Override
            public void onComplete( Bundle bundle ) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(context, newToken);
                ImgToastUtils.showMessage(context, "分享成功", R.drawable.center_ok_tip);
            }
            @Override
            public void onCancel() {
            }
        });
	}
	
	/**
     * 创建多媒体（网页）消息对象。
     * 
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "";
        mediaObject.description = "";
        // 设置 Bitmap 类型的图片到视频对象里
        mediaObject.setThumbImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        mediaObject.actionUrl = targetUrl + mJoke.getId();
        mediaObject.schema = "";
        return mediaObject;
    }
    /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = "分享自#奇趣营#" + description;
        return textObject;
    }
    /**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
    	Bitmap bitmap = BitmapUtil.getBitmapFromCache(mJoke.getImgUrl());
    	if(bitmap == null) {
    		return null;
    	}
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        imageObject.title = title;
        imageObject.description = description;
        imageObject.actionUrl = targetUrl + mJoke.getId();
        return imageObject;
    }
    
    private void setShareContent() {
    	switch (mJoke.getType()) {
		case Joke.TYPE_QUSHI:
			title = context.getString(R.string.share_title_qushi);
			description = mJoke.getContent().trim();
			break;
		case Joke.TYPE_QUTU:
			title = context.getString(R.string.share_title_qutu);
			description = mJoke.getTitle();
			break;
		case Joke.TYPE_MEITU:
			title = context.getString(R.string.share_title_meitu);
			description = mJoke.getTitle();
			break;
		}
    }
	
	/**
	 * 收藏
	 */
	private void collect() {
		if(mJoke == null) {
			return;
		}
		String jokeContent = FastjsonUtil.serialize(mJoke);
		collectDAO.collect(userId, mJoke.getId(), jokeContent);
		ImgToastUtils.showMessage(context, "收藏成功", R.drawable.ic_toast_fav);
		MobclickAgent.onEvent(context, "collect");
	}
	
	/**
	 * 取消收藏
	 */
	private void cancelCollect() {
		if(mJoke == null) {
			return;
		}
		collectDAO.cancelCollect(mJoke.getId());
		ImgToastUtils.showMessage(context, "已取消收藏", R.drawable.ic_toast_unfav);
		if(context instanceof MyCollectActivity) {
			((MyCollectActivity)context).cancelCollect(mJoke);
		}
	}
	
	/**
	 * 调用短消息
	 */
	private void sendSms() {
		StringBuffer sb = new StringBuffer();
		sb.append("分享自#奇趣营#");
		if(mJoke.getType() == Joke.TYPE_QUSHI) {
			sb.append(mJoke.getContent());
		} else {
			sb.append(mJoke.getTitle());
		}
		sb.append(targetUrl + mJoke.getId());
		String content = sb.toString().trim();
		Uri smsToUri = Uri.parse("smsto:");// 联系人地址 
        Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO, 
                smsToUri); 
        mIntent.putExtra("sms_body", 
        		content);// 短信内容 
        context.startActivity(mIntent); 
	}
	
	/**
	 * 复制内容
	 * 分享自#奇趣营#+内容+url
	 */
	@SuppressLint("NewApi")
	private void copy() {
		StringBuffer sb = new StringBuffer();
		sb.append("分享自#奇趣营#");
		if(mJoke.getType() == Joke.TYPE_QUSHI) {
			sb.append(mJoke.getContent());
		} else {
			sb.append(mJoke.getTitle());
		}
		sb.append(targetUrl + mJoke.getId());
		String content = sb.toString().trim();
		if (android.os.Build.VERSION.SDK_INT > 11) {
			android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setPrimaryClip(ClipData.newPlainText(content, content));
		} else {
			android.text.ClipboardManager c = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			c.setText(content);
		}
		ToastUtils.showMessageInCenter(context, "已复制到剪贴板");
	}
	
	/**
	 * 分享到QQ微博
	 */
	private void share2QQWeibo() {
		accessToken = Util.getSharePersistent(context, "ACCESS_TOKEN");
		if(com.lling.qiqu.utils.Util.isEmpty(accessToken)) {
			auth(Constants.APP_KEY, Constants.APP_KEY_SEC);
		} else {
			qqWeiboReAdd();
		}
	}
	
	/**
	 * qq微博授权
	 * @param appid
	 * @param app_secket
	 */
	private void auth(final long appid, final String app_secket) {
		//注册当前应用的appid和appkeysec，并指定一个OnAuthListener
		//OnAuthListener在授权过程中实施监听
		AuthHelper.register(context, appid, app_secket, new OnAuthListener() {
			//如果当前设备没有安装腾讯微博客户端，走这里
			@Override
			public void onWeiBoNotInstalled() {
				AuthHelper.unregister(context);
				Intent i = new Intent(context, Authorize.class);
				context.startActivity(i);
			}
			//如果当前设备没安装指定版本的微博客户端，走这里
			@Override
			public void onWeiboVersionMisMatch() {
				AuthHelper.unregister(context);
				Intent i = new Intent(context, Authorize.class);
				context.startActivity(i);
			}
			//如果授权失败，走这里
			@Override
			public void onAuthFail(int result, String err) {
				ToastUtils.showMessage(context, "授权失败");
				AuthHelper.unregister(context);
			}
			//授权成功，走这里
			//授权成功后，所有的授权信息是存放在WeiboToken对象里面的，可以根据具体的使用场景，将授权信息存放到自己期望的位置，
			//在这里，存放到了applicationcontext中
			@Override
			public void onAuthPassed(String name, WeiboToken token) {
				Util.saveSharePersistent(context, "ACCESS_TOKEN", token.accessToken);
				Util.saveSharePersistent(context, "EXPIRES_IN", String.valueOf(token.expiresIn));
				Log.e("String.valueOf(token.expiresIn)", String.valueOf(token.expiresIn));
				Util.saveSharePersistent(context, "OPEN_ID", token.openID);
//				Util.saveSharePersistent(context, "OPEN_KEY", token.omasKey);
				Util.saveSharePersistent(context, "REFRESH_TOKEN", "");
//				Util.saveSharePersistent(context, "NAME", name);
//				Util.saveSharePersistent(context, "NICK", name);
				Util.saveSharePersistent(context, "CLIENT_ID", appid+"");
				Util.saveSharePersistent(context, "AUTHORIZETIME",
						String.valueOf(System.currentTimeMillis() / 1000l));
				AuthHelper.unregister(context);
				accessToken = token.accessToken;
				qqWeiboReAdd();
			}
		});

		AuthHelper.auth(context, "");
	}
	
	/**
	 * 分享到qq微博
	 */
	private void qqWeiboReAdd() {
		AccountModel account = new AccountModel(accessToken);
		weiBoAPI = new WeiboAPI(account);
		StringBuffer sb = new StringBuffer();
		sb.append("分享自#奇趣营#");
		String imgPath = null;
		switch (mJoke.getType()) {
		case Joke.TYPE_QUSHI:
			sb.append(mJoke.getContent());
			break;
		case Joke.TYPE_QUTU:
			sb.append(mJoke.getTitle());
			imgPath = mJoke.getImgUrl();
			break;
		case Joke.TYPE_MEITU:
			sb.append(mJoke.getTitle());
			imgPath = mJoke.getImgUrl();
			break;
		case Joke.TYPE_GIF:
			sb.append(mJoke.getTitle());
			imgPath = mJoke.getGifUrl();
			break;
		}
		sb.append(targetUrl + mJoke.getId());
		weiBoAPI.reAddWeibo(context, sb.toString(), imgPath, null ,null, null, null, mCallBack, null, BaseVO.TYPE_JSON);
	}
	
	/**
	 * 分享到qq微博回调
	 */
	private HttpCallback mCallBack = new HttpCallback() {
   		@Override
   		public void onResult(Object object) {
   			ModelResult result = (ModelResult) object;
   			if(result.isExpires()){  //授权超期了，重新进行授权
   				auth(Constants.APP_KEY, Constants.APP_KEY_SEC);
   			}else{
   				if(result.isSuccess()){
   	   				ImgToastUtils.showMessage(context, "分享成功", R.drawable.center_ok_tip);
   	   			}else{
   	   				ToastUtils.showMessageInCenter(context, "分享失败");
   	   			}
   			}
   		}
	};
	
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
	 * 设置分享内容
	 * @param mJoke
	 */
	public void setmJoke(Joke mJoke) {
		this.mJoke = mJoke;
	}

	/**
	 * 新浪微博分享回调
	 */
	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(context, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(context, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(context, 
            		context.getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
	}
	
	/**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(context, mAccessToken);
                UsersAPI usersAPI = new UsersAPI(context, com.sina.weibo.Constants.APP_KEY, mAccessToken);
                share2weibo();
                usersAPI.show(Long.parseLong(mAccessToken.getUid()), new RequestListener() {
					@Override
					public void onWeiboException(WeiboException arg0) {
						Log.e(TAG, "sina get userInfo fail", arg0);
						ToastUtils.showMessage(context, R.string.weibosdk_demo_toast_auth_failed);
					}
					@Override
					public void onComplete(String arg0) {
						Log.e(TAG, arg0);
						/** 通过JSON获取到新浪微博用户信息,且解析相关内容信息 */
					    try {
							JSONObject jsonObj = new JSONObject(arg0);
							//uid
							String uid = jsonObj.getString("id");
							// 用户昵称
							String wb_screen_name = jsonObj.getString("screen_name");
							// 用户头像地址，50×50像素
							String wb_profile_image_url = jsonObj.getString("profile_image_url");
							IUserService mUserService = new UserServiceImpl(context);
							mUserService.addSinaUser(null, uid, wb_profile_image_url, wb_screen_name);
						} catch (JSONException e) {
							Log.e(TAG, "sina get userInfo fail", e);
						}
					}
				});
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = context.getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(context, 
                   R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(context, 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * 统计趣事、趣图、美图分享次数
     * @param joke
     */
    private void saveShareTimes() {
    	if(mJoke == null) {
    		return;
    	}
    	switch (mJoke.getType()) {
		case Joke.TYPE_QUSHI:
			MobclickAgent.onEvent(context, "qushi_share");
			break;
		case Joke.TYPE_QUTU:
			MobclickAgent.onEvent(context, "qutu_share");
			break;
		case Joke.TYPE_MEITU:
			MobclickAgent.onEvent(context, "meitu_share");
			break;
		}
    }
	
}
