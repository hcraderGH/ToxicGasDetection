package com.devcheng.toxicgasdetection;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.mylhyl.superdialog.SuperDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

	@BindView(R.id.tb_toolbar)
	Toolbar mToolbar;
	@BindView(R.id.map_view)
	MapView mMapView;
	private BaiduMap mBaiduMap;

	/**
	 * ==========有毒气体相关===========
	 **/
	@BindView(R.id.tv_latitude)
	TextView tvLatitude;//纬度
	@BindView(R.id.tv_longitude)
	TextView tvLongitude;//经度
	@BindView(R.id.tv_address)
	TextView tvAddress;//地址
	@BindView(R.id.tv_carbon_monoxide)
	TextView tvCarbonMonoxide;//一氧化碳
	@BindView(R.id.tv_carbon_dioxide)
	TextView tvCarbonDioxide;//二氧化碳
	@BindView(R.id.tv_oxygen)
	TextView tvOxygen;//氧气
	@BindView(R.id.tv_hydrothion)
	TextView tvHydrothion;//硫化氢
	@BindView(R.id.tv_methane)
	TextView tvMethane;//甲烷
	@BindView(R.id.tv_sulfur_dioxide)
	TextView tvSulfurDioxide;//二氧化硫
	@BindView(R.id.tv_air_quality)
	TextView tvAirQuality;//空气质量
	@BindView(R.id.tv_temperature)
	TextView tvTemperature;//温度
	@BindView(R.id.tv_humidity)
	TextView tvHumidity;//湿度

	@BindView(R.id.tv_methane_attribute)
	TextView tvMethaneAttribute;//甲烷的属性名称

	@BindView(R.id.btn_model)
	Button btnModel;
	@BindView(R.id.cb_satellite)
	CheckBox cbSatellite;


	private static final String TAG = "测试MainActivity";

	private ToxicGas mToxicGas;
	private double mLatitude;
	private double mLongitude;
	private String mAddress;
	private double mCarbonMonoxide;
	private double mCarbonDioxide;
	private double mOxygen;
	private double mHydrothion;
	private double mMethane;
	private double mSulfurDioxide;
	private double mAirQuality;
	private double mTemperature;
	private double mHumidity;

	private byte[] mResult;
	private SocketClient mClient;
	private boolean isFirstNull =true;
	private boolean isFirstLocate=true;

	private boolean isOriginalLocation=true;
	private boolean isTurnTo2D=false;
	/**
	 * ==========标注覆盖物相关===========
	 **/
	private BitmapDescriptor mOverlayIcon;
	private MyLocationConfiguration.LocationMode mLocationMode;
	private LocationClient mLocationClient;
	private GasLocationListener mGasLocationListener;
	private ResultBroadcast mResultBroadcast;

	private float mCurrentX;
	private MyOrientationListener myOrientationListener;

	private String mServerIP;
	private int mServerPort;
	private boolean isReconnectDialogShowing=false;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyApplication.getInstance().addActivity(this);
		ButterKnife.bind(this);

		mServerIP = getIntent().getStringExtra(Const.SERVER_IP);
		mServerPort = getIntent().getIntExtra(Const.SERVER_PORT, 8990);

		registerBroadcast();

		setupToolbar();
		connectServer();
		initMap();
		initLocation();

//		startDisplay();
	}

	private void registerBroadcast() {
		mResultBroadcast = new ResultBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Const.BROADCAST_RESULT);
		registerReceiver(mResultBroadcast, filter);
	}

	//自定义广播
	class ResultBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			switch (action) {
				case Const.BROADCAST_RESULT:
					mResult = intent.getByteArrayExtra(Const.EXTRA_RESULT);
					Log.i(TAG, "onReceive: mResult=" + Arrays.toString(mResult));
					analysisResult(mResult);
					break;
			}
		}
	}

	@OnClick(R.id.btn_model)
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_model:
				if (isOriginalLocation) {
					setLocationMode();
				}else{
					locate(mLatitude,mLongitude);
					setBtnModelBackground();
				}
				break;
		}
	}

	@OnCheckedChanged(R.id.cb_satellite)
	public void onCheckedChanged(boolean checked) {
		if (checked) {
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		} else {
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		}
	}

	private void setLocationMode() {
		switch (mLocationMode) {
			case COMPASS:
				mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
				setBtnModelBackground();
				isTurnTo2D=true;
				break;
			case NORMAL:
				mLocationMode = MyLocationConfiguration.LocationMode.COMPASS;
				//设置缩放的比例
				MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
				mBaiduMap.setMapStatus(msu);
				setBtnModelBackground();
				break;
		}
	}

	private void setBtnModelBackground(){
		switch (mLocationMode){
			case NORMAL:
				btnModel.setBackgroundResource(R.drawable.ic_model_normal);
				break;
			case COMPASS:
				btnModel.setBackgroundResource(R.drawable.ic_model_compass);
				break;

		}
	}

	/**
	 * 设置俯仰角度
	 *
	 * @param overlook
	 */
	private void setOverlook(int overlook, int rotate) {
		MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(overlook).rotate(rotate).build();
		MapStatusUpdate mu = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.animateMapStatus(mu);
	}

	/**
	 * 解析获取到的数据
	 */
	private void analysisResult(byte[] result) {

		if (result==null){//表示刚刚连接上还没有接受到数据
			return;
		}

		if (result.length==1){
			result[0]=0x00;//表示断开了连接
			if (!isReconnectDialogShowing){
				reconnectDialog();
			}
			addOverlays(mLatitude,mLongitude,R.drawable.ic_gas_gray);
			return;
		}

		//在此表示已经连接上服务器了
		if (mProgressDialog!=null&&mProgressDialog.isShowing()){
			mProgressDialog.cancel();
			ToastUtil.showToast(this,"连接成功!",1000);
		}

		isFirstNull =false;
		isReconnectDialogShowing=false;//当有数据时才设置为false

		final double latitude = CommonUtils.getLatitude(result);
		final double longitude = CommonUtils.getLongitude(result);
		if (latitude != mLatitude || longitude != mLongitude) {
			addOverlays(latitude, longitude,R.drawable.ic_gas);
		}

		mLatitude = latitude;
		mLongitude = longitude;

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				mAddress=searchInfo(latitude,longitude);
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						tvAddress.setText(mAddress+"");
//					}
//				});
//			}
//		}).start();

		mCarbonMonoxide = CommonUtils.getCarbonMonoxide(result);
		mCarbonDioxide = CommonUtils.getCarbonDioxide(result);
		mOxygen = CommonUtils.getOxygen(result);
		mHydrothion = CommonUtils.getHydrothion(result);
		mMethane = CommonUtils.getMethane(result);
		mSulfurDioxide = CommonUtils.getSulfurDioxide(result);
		mAirQuality = CommonUtils.getAirQuality(result);
		mTemperature = CommonUtils.getTemperature(result);
		mHumidity = CommonUtils.getHumidity(result);

		mToxicGas = new ToxicGas(mLatitude, mLongitude, mCarbonMonoxide, mCarbonDioxide,
				mOxygen, mHydrothion, mMethane, mSulfurDioxide, mAirQuality, mTemperature, mHumidity);
		Log.e(TAG, "analysisResult: mToxicGas=" + mToxicGas.toString());

		setData();
	}


	/**
	 * 弹出重连的对话框
	 */
	private void reconnectDialog(){
		isReconnectDialogShowing=true;

		new SuperDialog.Builder(this).setRadius(20)
				.setAlpha(1.0f)
				.setWidth(0.75f)
				.setMessage("断开连接，是否重连？")
				.setPositiveButton("是", new SuperDialog.OnClickPositiveListener() {
					@Override
					public void onClick(View v) {
						connectServer();
						reconnectProgressDialog();
					}
				})
				.setNegativeButton("否", new SuperDialog.OnClickNegativeListener() {
					@Override
					public void onClick(View v) {

					}
				}).build();
	}


	private ProgressDialog mProgressDialog;
	/**
	 * 重连进度对话框
	 */
	private void reconnectProgressDialog(){
		mProgressDialog=new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage("正在重连，请稍等...");
		mProgressDialog.show();

		//超时操作
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mProgressDialog != null&&mProgressDialog.isShowing()) {
					mProgressDialog.cancel();
					ToastUtil.showToast(MainActivity.this,"抱歉，连接失败!",1000);
				}
			}
		},5000);
	}

	//将数值控件设置为空
	private void setValueTextViewNull(){
		tvLatitude.setText("");
		tvLongitude.setText("");
		tvCarbonMonoxide.setText("");
		tvCarbonDioxide.setText("");
		tvOxygen.setText("");
		tvHydrothion.setText("");
		tvMethane.setText("");
		tvSulfurDioxide.setText("");
		tvAirQuality.setText("");
		tvTemperature.setText("");
		tvHumidity.setText("");
	}

//	private Timer mTimer;
//	private TimerTask mTimerTask;
//	private void startDisplay(){
//
//		if (mTimer==null){
//			mTimer=new Timer();
//		}
//		if (mTimerTask == null) {
//			mTimerTask=new TimerTask() {
//				@Override
//				public void run() {
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run(){
//							//在接受到数据时才显示位置
//							addOverlays(mLatitude,mLongitude);
//						}
//					});
//				}
//			};
//		}
//
//		mTimer.schedule(mTimerTask,0,3000);
//	}
//
//	private void stopDisplay(){
//
//		if (mTimer!=null){
//			mTimer.cancel();
//			mTimer=null;
//		}
//
//		if (mTimerTask != null) {
//			mTimerTask.cancel();
//			mTimerTask=null;
//		}
//	}

	private void setData() {

		String carbonMonoxide=CommonUtils.subZeroAndDot(mCarbonMonoxide);
		String carbonDioxide=CommonUtils.subZeroAndDot(mCarbonDioxide);
		String oxygen=CommonUtils.subZeroAndDot(mOxygen);
		String hydrothion=CommonUtils.subZeroAndDot(mHydrothion);
		String methane=CommonUtils.subZeroAndDot(mMethane);
		String sulfurDioxide=CommonUtils.subZeroAndDot(mSulfurDioxide);
		String airQuality=CommonUtils.subZeroAndDot(mAirQuality);
		String temperature=CommonUtils.subZeroAndDot(mTemperature);
		String humidity=CommonUtils.subZeroAndDot(mHumidity);

		SpannableStringBuilder carbonMonoxideSpan;
		SpannableStringBuilder carbonDioxideSpan;
		SpannableStringBuilder oxygenSpan;
		SpannableStringBuilder hydrothionSpan;
		SpannableStringBuilder methaneSpan;
		SpannableStringBuilder sulfurDioxideSpan;
		SpannableStringBuilder airQualitySpan;
		SpannableStringBuilder temperatureSpan;
		SpannableStringBuilder humiditySpan;

		//一氧化碳
		if (mCarbonMonoxide<=10){
			carbonMonoxideSpan= setForegroundColor(carbonMonoxide,R.color.value_color_normal);
		}else if (mCarbonMonoxide>10&&mCarbonMonoxide<=20){
			carbonMonoxideSpan= setForegroundColor(carbonMonoxide,R.color.value_color_reminder);
		}else{
			carbonMonoxideSpan= setForegroundColor(carbonMonoxide,R.color.value_color_warn);
		}
		//二氧化碳
		if (mCarbonDioxide<=4450){
			carbonDioxideSpan= setForegroundColor(carbonDioxide,R.color.value_color_normal);
		}else if (mCarbonDioxide>4450&&mCarbonDioxide<=6450){
			carbonDioxideSpan= setForegroundColor(carbonDioxide,R.color.value_color_reminder);
		}else{
			carbonDioxideSpan= setForegroundColor(carbonDioxide,R.color.value_color_warn);
		}
		//氧气
		if (mOxygen>100){
			oxygenSpan= setForegroundColor("检测异常",R.color.value_color_warn);
		}else if (mOxygen>20&&mOxygen<=100){
			oxygenSpan= setForegroundColor(oxygen,R.color.value_color_normal);
		}else if(mOxygen>18&&mOxygen<=20){
			oxygenSpan= setForegroundColor(oxygen,R.color.value_color_reminder);
		}else{
			oxygenSpan= setForegroundColor(oxygen,R.color.value_color_warn);
		}
		//硫化氢
		if (mHydrothion<=5){
			hydrothionSpan= setForegroundColor(hydrothion,R.color.value_color_normal);
		}else if(mHydrothion>1&&mHydrothion<=5){
			hydrothionSpan= setForegroundColor(hydrothion,R.color.value_color_reminder);
		}else {
			hydrothionSpan= setForegroundColor(hydrothion,R.color.value_color_warn);
		}
		//甲烷
		if (mMethane<10){
			methaneSpan= setForegroundColor(methane,R.color.value_color_normal);
		}else  if(mMethane>10&&mMethane<=20){
			methaneSpan= setForegroundColor(methane,R.color.value_color_reminder);
		}else{
			methaneSpan= setForegroundColor(methane,R.color.value_color_warn);
		}
		//二氧化硫
		if (mSulfurDioxide<=1){
			sulfurDioxideSpan= setForegroundColor(sulfurDioxide,R.color.value_color_normal);
		}else if(mSulfurDioxide>1&&mSulfurDioxide<=5){
			sulfurDioxideSpan= setForegroundColor(sulfurDioxide,R.color.value_color_reminder);
		}else{
			sulfurDioxideSpan= setForegroundColor(sulfurDioxide,R.color.value_color_warn);
		}
		//空气质量
		if (mAirQuality<=0.1){
			airQualitySpan= setForegroundColor(airQuality,R.color.value_color_normal);
		}else if (mAirQuality>0.1&&mAirQuality<=0.3){
			airQualitySpan= setForegroundColor(airQuality,R.color.value_color_reminder);
		}else{
			airQualitySpan= setForegroundColor(airQuality,R.color.value_color_warn);
		}
		//温度
		if (mTemperature<=100){
			temperatureSpan= setForegroundColor(temperature,R.color.value_color_normal);
		}else if (mTemperature>100&&mTemperature<=200){
			temperatureSpan= setForegroundColor(temperature,R.color.value_color_reminder);
		}else{
			temperatureSpan= setForegroundColor(temperature,R.color.value_color_warn);
		}
		//湿度
		if (mHumidity<=60){
			humiditySpan= setForegroundColor(humidity,R.color.value_color_normal);
		}else if(mHumidity>60&&mHumidity<=80){
			humiditySpan= setForegroundColor(humidity,R.color.value_color_reminder);
		}else {
			humiditySpan= setForegroundColor(humidity,R.color.value_color_warn);
		}


		tvLatitude.setText(CommonUtils.subZeroAndDot(mToxicGas.getLatitude()) + "");
		tvLongitude.setText(CommonUtils.subZeroAndDot(mToxicGas.getLongitude()) + "");

		tvCarbonMonoxide.setText(carbonMonoxideSpan);
		tvCarbonMonoxide.append("PPM");

		tvCarbonDioxide.setText(carbonDioxideSpan);
		tvCarbonDioxide.append("PPM");

		tvOxygen.setText( oxygenSpan);
		if (mOxygen<=100){
			tvOxygen.append("%");
		}

		tvHydrothion.setText( hydrothionSpan);
		tvHydrothion.append("PPM");

		tvMethane.setText( methaneSpan);
		tvMethane.append("PPM");

		tvSulfurDioxide.setText(sulfurDioxideSpan);
		tvSulfurDioxide.append("PPM");

		tvAirQuality.setText(airQualitySpan);
		tvAirQuality.append("PPM");

		tvTemperature.setText( temperatureSpan);
		tvTemperature.append("℃");

		tvHumidity.setText(humiditySpan);
		tvHumidity.append("RH%");


	}

	private SpannableStringBuilder setForegroundColor(String valueString, int color){
		return new SpanUtils().append(valueString).setForegroundColor(getResources().getColor(color)).create();
	}

	private void initLocation() {
		mLocationMode = MyLocationConfiguration.LocationMode.NORMAL;
		mLocationClient = new LocationClient(MyApplication.getInstance());
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setCoorType(Const.CurrentCoordinateTypStr);
		option.setIsNeedAddress(true);
		option.setScanSpan(1000);
		option.setOpenGps(true);//是否开启GPS
		mLocationClient.setLocOption(option);

		mGasLocationListener = new GasLocationListener();
		mLocationClient.registerLocationListener(mGasLocationListener);

		myOrientationListener = new MyOrientationListener(this);
		myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
			@Override
			public void onOrientationChanged(float x) {
				mCurrentX = x;
			}
		});

	}

	private void setupToolbar() {
		mToolbar.setTitle(getString(R.string.app_name));
		setSupportActionBar(mToolbar);
	}


	private void connectServer() {
		mClient = new SocketClient(new byte[]{0x24, 0x25, 0x01}, mServerIP, mServerPort, this);
	}

	private void initMap() {
//		mMapView= (MapView) findViewById(R.id.map_view);
		mBaiduMap = mMapView.getMap();
		//设置缩放的比例
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);

		mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				//设置比例尺的位置
				Point point = new Point(mMapView.getMeasuredWidth() - ConvertUtils.dp2px(100), mMapView.getMeasuredHeight() - ConvertUtils.dp2px(30));
				mMapView.setScaleControlPosition(point);
			}
		});

		mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
			@Override
			public void onTouch(MotionEvent motionEvent) {
				mLocationMode= MyLocationConfiguration.LocationMode.NORMAL;//当移动时则改变为普通模式
				isOriginalLocation=false;
				btnModel.setBackground(getResources().getDrawable(R.drawable.ic_model_location));
			}
		});


		//隐藏百度地图Logo
		View child = mMapView.getChildAt(1);
		if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
			child.setVisibility(View.GONE);
		}
	}

	/**
	 * 添加有毒气体覆盖物
	 */
	private void addOverlays(double attitude, double longitude,int draw) {

		mBaiduMap.clear();
		LatLng latLng = new LatLng(attitude, longitude);
		Marker marker = null;
		mOverlayIcon = BitmapDescriptorFactory.fromResource(draw);

		BitmapDescriptor transparentIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_gas_transparent);
		ArrayList<BitmapDescriptor> gifList = new ArrayList<>();
		gifList.add(mOverlayIcon);
		gifList.add(transparentIcon);

		OverlayOptions options = new MarkerOptions()
				.position(latLng)
				.icons(gifList)
				.zIndex(1)
				.period(50)
				.draggable(false);//设置是否手势拖拽
		marker = (Marker) mBaiduMap.addOverlay(options);

	}

	class GasLocationListener extends BDAbstractLocationListener {

		@Override
		public void onReceiveLocation(BDLocation bdLocation) {

			//定位有毒气体的位置数据
			MyLocationData data;
			if (isFirstNull){
				 data= new MyLocationData.Builder()
						.direction(mCurrentX)
						.accuracy(bdLocation.getRadius())
						.latitude(bdLocation.getLatitude())
						.longitude(bdLocation.getLongitude())
						.build();

			}else{
				data= new MyLocationData.Builder()
						.direction(mCurrentX)
						.accuracy(bdLocation.getRadius())
						.latitude(mLatitude)
						.longitude(mLongitude)
						.build();
			}
			Log.i(TAG, "onReceiveLocation: myLatitude="+bdLocation.getLatitude()+" myLongitude="+bdLocation.getLongitude()+"direction="+bdLocation.getDirection());
			Log.i(TAG, "onReceiveLocation: status  overlook="+mBaiduMap.getMapStatus().overlook+"rotate="+mBaiduMap.getMapStatus().rotate);
			mBaiduMap.setMyLocationData(data);

			//设置自定义定位图标
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_gas_transparent);
			MyLocationConfiguration config = new MyLocationConfiguration(mLocationMode, true, bitmapDescriptor);
			mBaiduMap.setMyLocationConfiguration(config);

			if (isFirstNull&&isFirstLocate) {
				locate(bdLocation.getLatitude(),bdLocation.getLongitude());
				isFirstLocate=false;
			}

			if (isTurnTo2D){
				setOverlook(0,0);
				isTurnTo2D=false;
			}
		}
	}

	private void locate(double latitude,double longitude){
		isOriginalLocation=true;
		LatLng latLng=new LatLng(latitude,longitude);
		MapStatusUpdate msu=MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(msu);
	}

	/**
	 * 获取反向地理编码
	 */
	private GeoCoder mSearch = null;

	private String searchInfo(double latitude, double longitude) {
		final String[] address = {null};
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			@Override
			public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
				//正向地理编码
			}

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
				//反向地理编码
				if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
					//没有检索到结果
					ToastUtil.showToast(MainActivity.this, "抱歉，未能找到坐标对应地址", 1000);
					address[0] = null;
				}
				//获取反向地理编码结果
				address[0] = reverseGeoCodeResult.getAddress();
			}
		});
		LatLng latLng = new LatLng(latitude, longitude);
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
		mSearch.destroy();
		return address[0];
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_setting, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.exit_login:
				startActivity(new Intent(MainActivity.this, LoginActivity.class));
				finish();
				break;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		//开启定位
		mBaiduMap.setMyLocationEnabled(true);
		mLocationClient.start();
		//开启方向传感器
		myOrientationListener.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//停止定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		//关闭方向传感器
		myOrientationListener.stop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();

//		stopDisplay();

		if (mClient != null) {
			mClient.disconnect();
		}

		//注销广播
		if (mResultBroadcast != null) {
			unregisterReceiver(mResultBroadcast);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
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
			MyApplication.getInstance().exitApp();
		}
//		super.onBackPressed();//此处不能调用父类的方法，否则直接退出程序
	}
}
