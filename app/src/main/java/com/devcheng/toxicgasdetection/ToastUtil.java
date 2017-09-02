package com.devcheng.toxicgasdetection;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by DevCheng on 2017/7/19.
 */

public class ToastUtil {

	public static final int TIME_SHORT = Toast.LENGTH_SHORT;
	public static final int TIME_LONG = Toast.LENGTH_LONG;

	private static Handler mHandler = new Handler();

	private static Toast mToast;

	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	public static void showToast(Context mContext,int resId) {
		showToast(mContext,mContext.getResources().getString(resId));
	}

	public static void showToast(Context mContext,String text) {
		showToast(mContext,text, TIME_SHORT);
	}

	public static void showToast(Context mContext,int resId, int duration) {
		showToast(mContext,mContext.getResources().getString(resId), duration);
	}

	public static void showToast(Context mContext,String text, int duration) {
		mHandler.removeCallbacks(r);
		if (mToast == null) {
			mToast = Toast.makeText(mContext, text, duration);
		} else {
			mToast.setText(text);
		}
		mHandler.postDelayed(r,duration);
		mToast.show();
	}

	/**
	 * 取消Toast
	 */
	public static void cancelToast(){
		if(mToast!=null){
			mToast.cancel();
		}
	}
}
