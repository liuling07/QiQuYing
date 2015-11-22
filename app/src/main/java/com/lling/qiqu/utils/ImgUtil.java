package com.lling.qiqu.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

/**
 * @author zhr
 * @project CloudTeacher
 * @create_date 2015-3-9 下午18:19:18
 */
public class ImgUtil {
	
	/**
	 * 获取网络图片
	   * @author zhr
	   * @create_date 2015-3-10 下午3:52:52
	   * @param url
	   * @return
	 */
	public static Bitmap getBitMapFromNet(String url){ 
        URL myFileUrl = null;   
        Bitmap bitmap = null;  
        try {   
            myFileUrl = new URL(url);   
        } catch (MalformedURLException e) {   
            e.printStackTrace();   
        }   
        try {   
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();   
            conn.setDoInput(true);   
            conn.connect();   
            InputStream is = conn.getInputStream();   
            bitmap = BitmapFactory.decodeStream(is);   
            is.close();   
        } catch (IOException e) {   
              e.printStackTrace();   
        }   
        return bitmap;
    }
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			Log.e("bmpToByteArray", "bmpToByteArray error", e);
		}
		
		return result;
	}
	
	/**
	 * 获取圆角图片
	   * @author zhr
	   * @create_date 2015-3-10 下午3:53:41
	   * @param bitmap
	   * @param pixels
	   * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap,int r) {
		//构建一个bitmap  
        Bitmap backgroundBmp = Bitmap.createBitmap(r,  
                 r, Config.ARGB_8888);  
        //new一个Canvas，在backgroundBmp上画图  
        Canvas canvas = new Canvas(backgroundBmp);  
        Paint paint = new Paint();  
        //设置边缘光滑，去掉锯齿  
        paint.setAntiAlias(true);  
        //宽高相等，即正方形  
        RectF rect = new RectF(0, 0, r, r);  
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形  
        canvas.drawRoundRect(rect, r/2, r/2, paint);  
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        //canvas将bitmap画在backgroundBmp上  
        canvas.drawBitmap(bitmap, null, rect, paint);  
        //返回已经绘画好的backgroundBmp  
        return backgroundBmp;  
    }
	
	public static void CopyStream(InputStream is, OutputStream os)  
    {  
        final int buffer_size=1024;  
        try  
        {  
            byte[] bytes=new byte[buffer_size];  
            for(;;)  
            {  
              int count=is.read(bytes, 0, buffer_size);  
              if(count==-1)  
                  break;  
              os.write(bytes, 0, count);                 
            }
            is.close();  
            os.close(); 
        }  
        catch(Exception ex){
        	ex.printStackTrace();
        }  
    }
	
	/**
	 * 设置图片高度
	 * @param imageView：图片对象
	 * @param whscale：宽高比
	 * @param width：宽度
	 */
	public static void setImageViewHeight(ImageView imageView,float whscale,int width){
		int height = (int) (width/whscale);
		LayoutParams para = imageView.getLayoutParams();
		para.height = height;
		para.width = width;
		imageView.setLayoutParams(para);
	}
	
	
	/**
	 *  高斯模糊
	 * @param bmp          要处理图像
	 * @param hRadius      水平方向模糊度
	 * @param vRadius      竖直方向模糊度
	 * @param iterations   模糊迭代度
	 * @return
	 */
    public static Drawable boxBlurFilter(Bitmap bmp, int hRadius, int vRadius, int iterations) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

	public static void blur(int[] in, int[] out, int width, int height,
            float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];
 
        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;
 
        int inIndex = 0;
 
        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;
 
            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }
 
            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];
 
                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];
 
                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }
 
    public static void blurFractional(int[] in, int[] out, int width,
            int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;
 
        for (int y = 0; y < height; y++) {
            int outIndex = y;
 
            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];
 
                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }
 
    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    //获得圆角图片的方法   
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int width, int height){   
        if(bitmap.getHeight() < height) {
        	bitmap = bitmapZoomByHeight(bitmap, height);
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);   
    
        final int color = 0xff424242;   
        final Paint paint = new Paint();
        paint.setAntiAlias(true);     //防止边缘的锯齿
        paint.setColor(0xff655659); 
        final Rect rect = new Rect(0, 0, width, height);  
        
        //画大圆
        canvas.drawARGB(0, 0, 0, 0);   
        paint.setColor(color); 
        canvas.drawCircle(rect.exactCenterX(), rect.exactCenterY(), rect.width()/2, paint);
        
        //画图片
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));   
        //让图片居中显示在view上
        float left = 0;
        if(bitmap.getWidth() > width) {
        	left = -(bitmap.getWidth() - width) / 2.0f;
        }
        canvas.drawBitmap(bitmap, left, 0, paint);
//        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;   
    }
    
    /**
    * 按照指定长宽压缩
    * @param srcBitmap
    * @param newWidth
    * @param newHeight
    * @return
    */
	public static Bitmap bitmapZoomBySize(Bitmap srcBitmap, int newWidth,
			int newHeight) {
		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();

		float scaleWidth = ((float) newWidth) / srcWidth;
		float scaleHeight = ((float) newHeight) / srcHeight;

		return bitmapZoomByScale(srcBitmap, scaleWidth, scaleHeight);
	}
	/**
	 * 按照宽度的百分比压缩
	 * 
	 * @param srcBitmap
	 * @param newHeight
	 * @return
	 */
	public static Bitmap bitmapZoomByWidth(Bitmap srcBitmap, int newWidth) {
		int srcWidth = srcBitmap.getWidth();
//		int srcHeight = srcBitmap.getHeight();

		float scaleWidth = ((float) newWidth) / srcWidth;
		float scaleHeight = scaleWidth;

		return bitmapZoomByScale(srcBitmap, scaleWidth, scaleHeight);
	}
	
	/**
	 * 按照宽度的百分比压缩
	 * 
	 * @param srcBitmap
	 * @param newHeight
	 * @return
	 */
	public static Bitmap bitmapZoomByHeight(Bitmap srcBitmap, int newHeight) {
//		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();

		float scaleHeight = ((float) newHeight) / srcHeight;
//		float scaleWidth = 1;

		return bitmapZoomByScale(srcBitmap, scaleHeight, scaleHeight);
	}
	
	/**
	 * 使用长宽缩放比缩放
	 * 
	 * @param srcBitmap
	 * @param scaleWidth
	 * @param scaleHeight
	 * @return
	 */
	public static Bitmap bitmapZoomByScale(Bitmap srcBitmap, float scaleWidth,
			float scaleHeight) {
		int srcWidth = srcBitmap.getWidth();
		int srcHeight = srcBitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth,
				srcHeight, matrix, true);
		if (resizedBitmap != null) {
			return resizedBitmap;
		} else {

			return srcBitmap;
		}
	}
	
	/**
	 * 指定的位图画布上绘制类似遮罩效果
	   * @param drawable
	   * @param color：画笔颜色
	   * @return
	 */
	public static Drawable maskDrawable(Drawable drawable,int color) {
	    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
	    Paint paint = new Paint();
	    paint.setColor(color);
	    RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    new Canvas(bitmap).drawRoundRect(rect, 0, 0, paint);
	    return new BitmapDrawable(bitmap);
	}
	
	/**
	 * 获取合适的Bitmap平时获取Bitmap就用这个方法吧.
	 * @param path   路径.
	 * @param data   byte[]数组.
	 * @param context  上下文
	 * @param uri    uri
	 * @param target  模板宽或者高的大小.
	 * @param width   是否是宽度
	 * @return
	 */
	public static Bitmap getResizedBitmap(String path, byte[] data,
			Context context, Uri uri, int target, boolean width) {
		Options options = null;

		if (target > 0) {

			Options info = new Options();
			// 这里设置true的时候，decode时候Bitmap返回的为空，
			// 将图片宽高读取放在Options里.
			info.inJustDecodeBounds = true;

			decode(path, data, context, uri, info);

			int dim = info.outWidth;
			if (!width)
				dim = Math.max(dim, info.outHeight);
			int ssize = sampleSize(dim, target);
			options = new Options();
			options.inSampleSize = ssize;
		}

		Bitmap bm = null;
		try {
			bm = decode(path, data, context, uri, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bm;

	}

	/**
	 * 解析Bitmap的公用方法.
	 * 
	 * @param path
	 * @param data
	 * @param context
	 * @param uri
	 * @param options
	 * @return
	 */
	public static Bitmap decode(String path, byte[] data, Context context,
			Uri uri, BitmapFactory.Options options) {

		Bitmap result = null;
		if (path != null) {
			result = BitmapFactory.decodeFile(path, options);
		} else if (data != null) {
			result = BitmapFactory.decodeByteArray(data, 0, data.length,
					options);
		} else if (uri != null) {
			// uri不为空的时候context也不要为空.
			ContentResolver cr = context.getContentResolver();
			InputStream inputStream = null;

			try {
				inputStream = cr.openInputStream(uri);
				result = BitmapFactory.decodeStream(inputStream, null, options);
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	/**
	 * 获取合适的sampleSize. 这里就简单实现都是2的倍数啦.
	 * 
	 * @param width
	 * @param target
	 * @return
	 */
	private static int sampleSize(int width, int target) {
		int result = 1;
		for (int i = 0; i < 10; i++) {
			if (width < target * 2) {
				break;
			}
			width = width / 2;
			result = result * 2;
		}
		return result;
	}
	
	/**
	 * 保存图片到sd卡中
	 * @param bitmap
	 * @param _file
	 * @throws IOException
	 */
	public static void saveBitmapToFile(Bitmap bitmap, String _file)
			throws IOException {
		BufferedOutputStream os = null;
		try {
			File file = new File(_file);
			// String _filePath_file.replace(File.separatorChar +
			// file.getName(), "");
			int end = _file.lastIndexOf(File.separator);
			String _filePath = _file.substring(0, end);
			File filePath = new File(_filePath);
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			file.createNewFile();
			os = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					Log.e("ImgUtil", e.getMessage(), e);
				}
			}
		}
	}
	
}
