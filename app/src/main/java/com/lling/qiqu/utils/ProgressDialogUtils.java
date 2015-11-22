package com.lling.qiqu.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lling.qiqu.R;

public class ProgressDialogUtils {
	
	private static Dialog dialog;
	
	public static void showProgressDialog(Context context, String text) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.ct_progressdialog, null);
		TextView content = (TextView) v.findViewById(R.id.dialog_content);
		content.setText(text);
		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.dialog_img);
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context,
				R.anim.progress_anim);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		dialog = new Dialog(context, R.style.CustomDialog);
		dialog.setCanceledOnTouchOutside(false); //触摸外面不消失
		dialog.setContentView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		dialog.getWindow().setWindowAnimations(R.style.dialogAnim); 
		dialog.show();
		/*//设置progressdilog的高度和宽度
		int sreenWidth = DensityUtil.getWidthInPx(context);
		dialog.getWindow().setLayout(sreenWidth - 64, DensityUtil.dip2px(context, 48));*/
	}
	
	public static void dismiss() {
		if(dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}
	
}
