package com.lling.qiqu.activitys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lling.qiqu.App;
import com.lling.qiqu.R;
import com.lling.qiqu.beans.KeyToken;
import com.lling.qiqu.commons.Constants;
import com.lling.qiqu.service.IUserService;
import com.lling.qiqu.service.impl.UserServiceImpl;
import com.lling.qiqu.ui.CircleImageView;
import com.lling.qiqu.utils.BitmapUtil;
import com.lling.qiqu.utils.FastjsonUtil;
import com.lling.qiqu.utils.ProgressDialogUtils;
import com.lling.qiqu.utils.ToastUtils;
import com.lling.qiqu.utils.ToolsUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.tencent.open.utils.Util;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: CreateUserInfoActivity
 * @Description: 注册后创建个人资料
 * @author lling
 * @date 2015年7月4日
 */
@ContentView(R.layout.activity_create_user_info)
public class CreateUserInfoActivity extends BaseActivity {
	private final String TAG = "CreateUserInfoActivity";
	private final int PIC_SIZE = 200;
	private final int CHOSE_PIC_CODE = 1;
	private final int TAKE_PHOTO_CODE = 2;
	private final int CROP_CODE = 3;
	/* 头像名称 */
	private final String IMAGE_FILE_NAME = "portrait.jpg";
	@ViewInject(R.id.sex_tv)
	private TextView mSexTV;
	@ViewInject(R.id.nick_et)
	private EditText mNickET;
	@ViewInject(R.id.set_portrait_tv)
	private TextView mSetPortraitTV;
	@ViewInject(R.id.user_img)
	private CircleImageView mUserPortraitIV;
	private Dialog mSexDialog;
	private Dialog mPortraitDialog;
	private int mSexFlag = 1;
	private IUserService mUserService = new UserServiceImpl(this);
	private ClassHandler mHandler = new ClassHandler(this);
	private KeyToken mKeyToken = null;
	private String mPortraitURL;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
	}

	/**
	 * 选择性别
	 * @param view
	 */
	@OnClick(R.id.set_sex_layout)
	private void setSex(View view) {
		if (mSexDialog == null) {
			createSexDialog();
		}
		mSexDialog.show();
	}

	private void createSexDialog() {
		mSexDialog = new Dialog(this, R.style.Translucent_NoTitle);
		mSexDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置对话框无title
		mSexDialog.setContentView(R.layout.dialog_userinfo);
		TextView title = (TextView) mSexDialog
				.findViewById(R.id.dialog_userinfo_title);
		title.setText(R.string.sex);
		ListView listview = (ListView) mSexDialog
				.findViewById(R.id.dialog_userinfo_listview);
		String[] strings = new String[] { "男", "女" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.listitem_userinfo_dialog,
				R.id.name, strings);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0) {
					mSexTV.setText(R.string.sex_man);
					mSexFlag = 1;
				} else {
					mSexTV.setText(R.string.sex_women);
					mSexFlag = 0;
				}
				mSexDialog.dismiss();
			}
		});
		ToolsUtils.setDialogBgAndAnim(mSexDialog);
	}

	@OnClick(R.id.set_portrait_tv)
	private void setPortrait(View view) {
		if (mPortraitDialog == null) {
			createPortraitDialog();
		}
		mPortraitDialog.show();
	}

	private void createPortraitDialog() {
		mPortraitDialog = new Dialog(this, R.style.Translucent_NoTitle);
		mPortraitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置对话框无title
		mPortraitDialog.setContentView(R.layout.dialog_userinfo);
		TextView title = (TextView) mPortraitDialog
				.findViewById(R.id.dialog_userinfo_title);
		title.setText(R.string.set_portrait);
		ListView listview = (ListView) mPortraitDialog
				.findViewById(R.id.dialog_userinfo_listview);
		String[] strings = new String[] { "相册", "相机" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.listitem_userinfo_dialog,
				R.id.name, strings);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0: // 选中相册
					Intent intentFromGallery = new Intent();
					intentFromGallery.setType("image/*"); // 设置文件类型
					intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResultWithAnimation(intentFromGallery,
							CHOSE_PIC_CODE);
					break;
				case 1: // 选中照相机
					Intent intentFromCapture = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					// 判断存储卡是否可以用，可用进行存储
					if(ToolsUtils.hasSdcard()) {
						intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(new File(Environment
										.getExternalStorageDirectory(),
										IMAGE_FILE_NAME)));
						startActivityForResultWithAnimation(intentFromCapture,
								TAKE_PHOTO_CODE);
					} else {
						ToastUtils.showMessageLong(getApplicationContext(),
								"未找到存储卡，无法存储照片！");
					}
					break;
				}
				mPortraitDialog.dismiss();
			}
		});
		ToolsUtils.setDialogBgAndAnim(mPortraitDialog);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CHOSE_PIC_CODE:   //从相册选择照片成功
			try {
				startPhotoZoom(data.getData());
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case TAKE_PHOTO_CODE: //从相机拍照成功
			File tempFile = new File(Environment.getExternalStorageDirectory() + "/"
					+ IMAGE_FILE_NAME);
			try {
				startPhotoZoom(Uri.fromFile(tempFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case CROP_CODE:    //裁剪照片成功
			if (data == null) {
				return;	
			}
			getImageToView();
			break;
		}
	}
	
	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public void startPhotoZoom(Uri uri) throws Exception {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME)));

		byte[] mContent;
		ContentResolver resolver = getContentResolver();

		// 将图片内容解析成字节数组
		mContent = ToolsUtils.readStream(resolver
				.openInputStream(Uri.parse(uri.toString())));

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;

		// 将字节数组转换为ImageView可调用的Bitmap对象
		Bitmap myBitmap = ToolsUtils.getPicFromBytes(mContent, opts);

		if (myBitmap.getWidth() >= PIC_SIZE && myBitmap.getHeight() >= PIC_SIZE) {
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
		}
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_CODE);
	}
	
	/**
	 * 保存裁剪之后的图片数据
	 * @param pic data
	 * @throws IOException
	 */
	private void getImageToView() {
		ProgressDialogUtils.showProgressDialog(this, "正在处理中...");
		mUserService.getQiNiuToken(mHandler);
	}
	
	/**
	 * 跳过
	 * @param view
	 */
	@OnClick(R.id.skip_tv)
	private void skip(View view) {
		Intent intent = new Intent(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivityWithAnimation(intent);
		finishWithAnimation();
	}
	
	/**
	 * 保存个人资料
	 * @param view
	 */
	String nickName;
	@OnClick(R.id.save_tv)
	private void save(View view) {
		nickName = mNickET.getText().toString().trim();
		if(Util.isEmpty(nickName)) {
			mNickET.requestFocus();
			ToastUtils.showMessage(getApplicationContext(), R.string.nick_null);
			return;
		}
		nickName = ToolsUtils.Html2Text(nickName);
		if(Util.isEmpty(nickName)) {
			ToastUtils.showMessage(getApplicationContext(), 
					R.string.input_invalide);
			return;
		}
		ToolsUtils.hideKeyboard(mNickET);
		if (App.currentUser == null) {
			finishWithAnimation();
			return;
		}
		try {
			ProgressDialogUtils.showProgressDialog(this, "正在处理中...");
			mUserService.setNickAndSex(mHandler, String.valueOf(App.currentUser.getId()), 
					URLEncoder.encode(nickName, "UTF-8"), String.valueOf(mSexFlag));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 消息处理类
	 * @author lling
	 */
	static class ClassHandler extends Handler {
		WeakReference<CreateUserInfoActivity> mActivityReference;

		ClassHandler(CreateUserInfoActivity activity) {
			mActivityReference = new WeakReference<CreateUserInfoActivity>(activity);
		}

		public void handleMessage(Message msg) {
			final CreateUserInfoActivity activity = mActivityReference.get();
			if(activity == null) {
				return;
			}
			switch (msg.what) {
			case Constants.FAILURE: // 获取七牛token失败
				ProgressDialogUtils.dismiss();
				break;
			case Constants.SUCCESS: // 获取七牛token成功
				if(msg.obj == null) {
					break;
				}
				activity.mKeyToken = FastjsonUtil.deserialize(
						msg.obj.toString(), KeyToken.class);
				activity.uploadPortrait();
				break;
			case Constants.FAILURE_1: //设置头像失败
				ProgressDialogUtils.dismiss();
				break;
			case Constants.SUCCESS_1: //设置头像成功
				activity.setPortraitSuccess();
				ProgressDialogUtils.dismiss();
				break;
			case Constants.FAILURE_2: //设置昵称和性别失败
				ProgressDialogUtils.dismiss();
				break;
			case Constants.SUCCESS_2: //设置昵称和性别成功
				activity.setNickSexSuccess();
				ProgressDialogUtils.dismiss();
				break;
			}
		}
	}
	
	/**
	 * 上传头像
	 */
	private void uploadPortrait() {
		if(mKeyToken == null) {
			ToastUtils.showMessage(getApplicationContext(), R.string.set_portrait_fail);
			return;
		}
		UploadManager uploadManager = new UploadManager();
		File file = new File(Environment.getExternalStorageDirectory()
				+ "/" + IMAGE_FILE_NAME);
		uploadManager.put(file, mKeyToken.getKey(),
				mKeyToken.getToken(), new UpCompletionHandler() {
					@Override
					public void complete(String fileName, ResponseInfo responseInfo,
							JSONObject jsonObject) {
						mPortraitURL = Constants.UPLOADFILE_PRE + fileName;
						if (responseInfo.isOK()) {
							//上传成功，更新用户头像
							mUserService.setPortrait(mHandler, String.valueOf(App.currentUser.getId()), mPortraitURL);
						} else {
							ToastUtils.showMessage(getApplicationContext(),
									R.string.set_portrait_fail);
							ProgressDialogUtils.dismiss();
						}
					}
				}, null);
	}
	
	/**
	 * 设置头像成功
	 */
	private void setPortraitSuccess() {
		mUserPortraitIV.setVisibility(View.VISIBLE);
		mSetPortraitTV.setVisibility(View.GONE);
		BitmapUtil.display(mUserPortraitIV, mPortraitURL);
		if(App.currentUser == null) {
			return;
		}
		App.currentUser.setPortraitUrl(mPortraitURL);
		spUtil.putObject("user", App.currentUser);
	}
	
	/**
	 * 设置昵称和性别成功
	 */
	private void setNickSexSuccess() {
		if(App.currentUser == null) {
			return;
		}
		App.currentUser.setSex(mSexFlag);
		App.currentUser.setUserNike(nickName);
		spUtil.putObject("user", App.currentUser);
		Intent intent = new Intent(this, IndexActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivityWithAnimation(intent);
		finishWithAnimation();
	}
	
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("CreateUserInfoActivity"); //统计页面
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("CreateUserInfoActivity");
		super.onPause();
	}
}
