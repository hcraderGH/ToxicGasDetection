package com.devcheng.toxicgasdetection;

/**
 * Created by DevCheng on 2017/7/19.
 */

public class MyApplication extends BaseApplication {

	private static boolean isTest=false;//TODO 当不处于测试的时候应该设置为false

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public static boolean isTest(){
		return isTest;
	}
}
