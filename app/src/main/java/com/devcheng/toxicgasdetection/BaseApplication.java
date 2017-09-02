package com.devcheng.toxicgasdetection;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.location.Geocoder;
import android.os.Process;

import com.baidu.mapapi.SDKInitializer;
import com.blankj.utilcode.util.Utils;

import java.util.Stack;

/**
 * Created by DevCheng on 2017/7/17.
 */

public class BaseApplication extends Application {
	private static BaseApplication sInstance;
	private Stack<Activity> activityStack;

	public static BaseApplication getInstance(){
		return sInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
		Utils.init(this);
		sInstance=this;
	}

	/**
	 * 添加指定Activity到堆栈
	 * @param activity
	 */
	public void addActivity(Activity activity){
		if (activityStack==null){
			activityStack=new Stack<>();
		}else{
			activityStack.add(activity);
		}
	}

	/**
	 * 获取当前的Activity（放在addActivity方法之后）
	 * @return
	 */
	public Activity currentActivity(){
		return activityStack.lastElement();
	}

	public Stack<Activity> getAllActivities(){
		return activityStack;
	}

	/**
	 * 结束当前的Activity
	 * @param activity
	 */
	public void finishActivity(Activity activity){
		if (activity!=null){
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}

	/**
	 * 结束指定Class的Activity
	 * @param cls
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)){
				finishActivity(activity);
				return;
			}
		}
	}

	/**
	 * 结束全部的Activity
	 */
	public void finishAllActivity(){
		for (int i = 0; i < activityStack.size(); i++) {
			if(activityStack.get(i)!=null){
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}


	/**
	 * 退出应用程序
	 */
	public void exitApp(){
		finishAllActivity();
		//杀死该应用进程
		Process.killProcess(Process.myPid());
	}
}
