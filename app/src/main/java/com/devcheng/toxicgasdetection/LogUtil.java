package com.devcheng.toxicgasdetection;

import android.util.Log;

/**
 * Created by Administrator on 2016/11/19.
 * 日志工具
 * 当LEVEL等于VERBOSE时就可以把所有的日志都打印出来
 * 当LEVEL等于NOTHING时就可以将所有的日志都屏蔽掉
 */

public class LogUtil {

	public static final int VERBOSE=1;
	public static final int DEBUG=2;
	public static final int INFO=3;
	public static final int WARN=4;
	public static final int ERROR=5;
	public static final int NOTHING=6;

	public static final int LEVEL= MyApplication.isTest()?VERBOSE:NOTHING;

	public static void v(String tag,String msg){
		if (LEVEL<=VERBOSE){
			Log.v(tag,msg);
		}
	}

	public static void d(String tag,String msg){
		if (LEVEL<=DEBUG){
			Log.d(tag,msg);
		}
	}

	public static void i(String tag,String msg){
		if (LEVEL<=INFO){
			Log.i(tag,msg);
		}
	}

	public static void w(String tag,String msg){
		if (LEVEL<=WARN){
			Log.w(tag,msg);
		}
	}

	public static void e(String tag,String msg){
		if (LEVEL<=ERROR){
			Log.e(tag,msg);
		}
	}

}
