package com.lling.qiqu.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lling.qiqu.utils.DensityUtil;

/**
 * @Title: DownloadPercentView.java
 * @Description: 下载进度圆弧显示
 * @author lling
 * @date 2015-5-11
 */
public class LoadPercentImageView extends ImageView {

	// 画圆环的画笔
	private Paint mRingPaint;
	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 绘制进度文字的画笔
	private Paint mTxtPaint;
	// 圆环颜色
	private int mRingColor = 0xffC5C5C5;
	// 圆形颜色
	private int mCircleColor = 0xffDDDDDD;
	// 圆环半径
	private float mRingRadius = 40;
	// 圆环宽度
	private float mStrokeWidth = 8;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	private boolean isComplete = false;
	
	private boolean isLoadImg = true;   //是否加载图片
	
	public LoadPercentImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVariable(context);
	}


	private void initVariable(Context context) {
		
		mRingRadius = DensityUtil.dip2px(context, 28);
		mStrokeWidth = DensityUtil.dip2px(context, 6);
		
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);
		
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.STROKE);
		mCirclePaint.setStrokeWidth(mStrokeWidth);
		
		mTxtPaint = new Paint();
		mTxtPaint.setAntiAlias(true);
		mTxtPaint.setColor(mCircleColor);
		mTxtPaint.setTextAlign(Paint.Align.CENTER);
		mTxtPaint.setTextSize(DensityUtil.sp2px(getContext(), 14));
	}

	/*@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = (int)Math.ceil(mRadius) * 2 + 2 * padding;
		setMeasuredDimension(width, width);
	}*/
	RectF oval = new RectF();
	@Override
	protected void onDraw(Canvas canvas) {
		if(!isLoadImg) {  //如果不加载图片，则只显示背景
			super.onDraw(canvas);
			return;
		}
		if(isComplete) {
			super.onDraw(canvas);
			return;
		}
		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;
		canvas.drawCircle(mXCenter, mYCenter, mRingRadius, mCirclePaint);
		
		if (mProgress >= 0) {
			//设置椭圆上下左右的坐标
			oval.left = mXCenter - mRingRadius;  
			oval.top = mYCenter - mRingRadius;
			oval.right = mXCenter + mRingRadius;
			oval.bottom = mYCenter + mRingRadius;
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint); //
			
			String percentTxt = mProgress + "%";
			//计算文字垂直居中的baseline
			FontMetricsInt fontMetrics = mTxtPaint.getFontMetricsInt();
			float baseline = oval.top + (oval.bottom - oval.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
			canvas.drawText(percentTxt, mXCenter, baseline, mTxtPaint);
		}
	}
	
	public void setProgress(int progress) {
		mProgress = progress;
		isLoadImg = true;
		postInvalidate();
	}
	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
		postInvalidate();
	}
	
	//设置是否加载图片
	public void setLoadImg(boolean isLoadImg) {
		this.isLoadImg = isLoadImg;
	}

}
