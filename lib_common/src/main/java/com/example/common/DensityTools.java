package com.example.common;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;


/**
 * 
 * @author hxl
 * @date 2014-11-22 下午2:40:14
 * @todo TODO 计算公式 pixels = dips * (density / 160)
 */
public class DensityTools {
	private static final String TAG = DensityTools.class.getSimpleName();

	// 当前屏幕的densityDpi
	private static float dmDensityDpi = 0.0f;
	private static DisplayMetrics dm;
	private static float scale = 0.0f;

	private int mScreenWidth;
	private int mScreenHeight;

	/**
	 * 
	 * 根据构造函数获得当前手机的屏幕系数
	 * 
	 * */
	public DensityTools(Context context) {
		// 获取当前屏幕
		dm = new DisplayMetrics();
		dm = context.getApplicationContext().getResources().getDisplayMetrics();
		// 设置DensityDpi
		setDmDensityDpi(dm.densityDpi);
		// 密度因子
		scale = getDmDensityDpi() / 160;
		EdLog.d(TAG, "DensityTools...densityDpi-->" + dm.densityDpi
				+ " density---->" + dm.density + "scaledDensity----->"
				+ dm.scaledDensity + " scale--->" + scale);
		EdLog.i(TAG, toString());
	}

	/**
	 * 当前屏幕的density因子
	 * 
	 * @param
	 * @retrun DmDensity Getter
	 * */
	public static float getDmDensityDpi() {
		return dmDensityDpi;
	}

	/**
	 * 当前屏幕的density因子
	 * 
	 * @param
	 * @retrun DmDensity Setter
	 * */
	public static void setDmDensityDpi(float dmDensityDpi) {
		DensityTools.dmDensityDpi = dmDensityDpi;
	}

	/**
	 * 密度转换像素
	 * */
	public int dip2px(float dipValue) {

		return (int) (dipValue * scale + 0.5f);

	}

	/**
	 * 像素转换密度
	 * */
	public int px2dip(float pxValue) {
		return (int) (pxValue / scale + 0.5f);
	}

	@Override
	public String toString() {
		return " dmDensityDpi:" + dmDensityDpi;
	}

	public int getmScreenWidth() {
		return dm.widthPixels;
	}

	public void setmScreenWidth(int mScreenWidth) {
		this.mScreenWidth = mScreenWidth;
	}

	public int getmScreenHeight() {
		return dm.heightPixels;
	}

	public void setmScreenHeight(int mScreenHeight) {
		this.mScreenHeight = mScreenHeight;
	}
	
/**
	 * 像素转换密度
	 * */
	public static int px2dp(Context contxt, float pxValue) {
		DisplayMetrics dm=contxt.getResources().getDisplayMetrics();
		return (int) (pxValue * dm.density + 0.5f);
	}
	public static int dp2px(Context context,float value){
	    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
	}
	/**
	 * 获取屏幕的高
	 *
	 * @param activity
	 * @return
	 */
	public static int getScreenHight(Activity activity) {
		if (activity == null) {
			return 0;
		}
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}


	/**
	 * 获取屏幕的宽
	 *
	 * @param activity
	 * @return
	 */
	public static int getScreenWeight(Activity activity) {
		if (activity == null) {
			return 0;
		}
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
}