package com.lling.qiqu.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lling.qiqu.R;
import com.lling.qiqu.utils.ImgToastUtils;
import com.lling.qiqu.utils.ToastUtils;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: WXEntryActivity
 * @Description: 微信回调activity
 * @author lling
 * @date 2015年7月14日
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private final String TAG = "WXEntryActivity";
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxentry);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
    	api.handleIntent(getIntent(), this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			Toast.makeText(this, getString(result), Toast.LENGTH_LONG).show();
			ImgToastUtils.showMessage(this, getString(result), R.drawable.center_ok_tip);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			ToastUtils.showMessageInCenter(this, getString(result));
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			ToastUtils.showMessageInCenter(this, getString(result));
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		Log.e(TAG, resp.errStr+""+resp.errCode);
		finish();
	}

	
}
