package com.devcheng.toxicgasdetection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.NetworkUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by DevCheng on 2017/8/31.
 */

public class LoginActivity extends BaseActivity {

	@BindView(R.id.et_login_ip)
	EditText etIP;
	@BindView(R.id.et_login_port)
	EditText etPort;
	@BindView(R.id.btn_login)
	Button btnLogin;

	private SPUtils mSpUtils;
	private SocketClient mClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		MyApplication.getInstance().addActivity(this);
		ButterKnife.bind(this);

		mSpUtils=new SPUtils(Const.SP_ADDRESS,this);
		initViews();
	}

	private void initViews() {
		if (isSaved(Const.SP_IP)){
			etIP.setText(mSpUtils.getString(Const.SP_IP));
		}
		if (isSaved(Const.SP_PORT)){
			etPort.setText(String.valueOf((mSpUtils.getInt(Const.SP_PORT))));
		}
	}

	@OnClick(R.id.btn_login)
	public void login(View v) {
		switch (v.getId()) {
			case R.id.btn_login:

				final String ip=etIP.getText().toString();
				final String port=etPort.getText().toString();

				if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
					ToastUtil.showToast(this, "IP地址或IP端口号不能为空",1000);
					return;
				}
				if (!isIPRight(ip)) {
					ToastUtil.showToast(this, "IP地址输入格式错误",1000);
					return;
				}
				if (!isPortRight(port)) {
					ToastUtil.showToast(this, "IP端口号输入格式错误",1000);
					return;
				}

				//保存IP
				mSpUtils.put(Const.SP_IP,ip);
				mSpUtils.put(Const.SP_PORT,Integer.parseInt(port));

				if (!NetworkUtils.isConnected()){
					ToastUtil.showToast(this,"网络连接异常，请检查网络设置",1000);
					return;
				}


				AsyncTask<Void,Void,Boolean> task=new AsyncTask<Void, Void, Boolean>() {
					@Override
					protected Boolean doInBackground(Void... params) {
						mClient=new SocketClient(ip,Integer.parseInt(port));
						return mClient.isServerClose();
					}

					@Override
					protected void onPostExecute(Boolean b) {
						if (b){
							ToastUtil.showToast(LoginActivity.this,"连接异常",1000);
						}else {
							Intent intent=new Intent(LoginActivity.this,MainActivity.class);
							intent.putExtra(Const.SERVER_IP,ip);
							intent.putExtra(Const.SERVER_PORT,Integer.parseInt(port));
							startActivity(intent);
							finish();
						}
					}
				};
				task.execute();
				break;
		}
	}


	private boolean isIPRight(String ip) {
		Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}

	private boolean isPortRight(String port) {
		Pattern pattern = Pattern.compile("\\d{4}");
		Matcher matcher = pattern.matcher(port);
		return matcher.matches();
	}


	/**
	 * 属性是否设置了
	 * @return
	 */
	private boolean isSaved(String keyName) {
		boolean isExist;
		isExist = mSpUtils.contains(keyName);
		return isExist;
	}

	/**
	 * 按两次back键退出
	 */
	private static Boolean isExit = false;
	private Timer tExit = new Timer();
	private TimerTask task;

	@Override
	public void onBackPressed() {

		if (!isExit) {
			isExit = true;
			ToastUtil.showToast(this, "再按一次退出程序", 1000);
			task = new TimerTask() {
				@Override
				public void run() {
					isExit = false;
				}
			};
			tExit.schedule(task, 2000);
		} else {
			ToastUtil.cancelToast();
			if (mClient != null) {
				mClient.disconnect();
			}
//			MyApplication.getInstance().exitApp();//出出现闪黑屏
			finish();
		}
//		super.onBackPressed();//此处不能调用父类的方法，否则直接退出程序
	}
}
