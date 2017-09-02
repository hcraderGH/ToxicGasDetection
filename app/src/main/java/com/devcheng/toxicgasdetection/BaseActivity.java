package com.devcheng.toxicgasdetection;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.blankj.utilcode.util.Utils;

/**
 * Created by DevCheng on 2017/8/29.
 */

public class BaseActivity extends AppCompatActivity {

	private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.ACCESS_FINE_LOCATION};
	private static final int authBaseRequestCode = 1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		StatusBar.setImmersiveStatusBar(this,R.color.colorPrimary);//沉浸式状态栏

		requestLocationPermissions();
	}

	private void requestLocationPermissions() {
		//申请权限
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			if (!hasBaseAuth()) {
				this.requestPermissions(authBaseArr, authBaseRequestCode);
				return;
			}
		}

	}

	private boolean hasBaseAuth() {
		PackageManager pm = this.getPackageManager();
		for (String auth : authBaseArr) {
			if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode==authBaseRequestCode){

			for (int ret : grantResults) {
				if (ret==0){
					continue;
				}else{
					ToastUtil.showToast(BaseActivity.this,"缺少定位的基本权限!", Toast.LENGTH_LONG);
				}
			}
		}
	}
}
