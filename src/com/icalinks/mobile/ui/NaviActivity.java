package com.icalinks.mobile.ui;

import java.util.LinkedList;
import java.util.Queue;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.CoordinateConvert;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.RouteOverlay;
import com.icalinks.common.ScreenHelper;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.markupartist.android.widget.ActionBar;
import com.provider.common.util.StrUtils;
import com.provider.common.util.ToolsUtil;
import com.provider.model.resources.Navigation;
import com.provider.net.tcp.GpsWorker;
import com.umeng.analytics.MobclickAgent;

public class NaviActivity extends MapActivity implements MKSearchListener,
		LocationListener {// OnCallbackListener,
	private static String tag = NaviActivity.class.getSimpleName();
	/**
	 * 是否记录回放
	 */
	public static final String ARGS_IS_BAIDU_POINT = "is_baidu_point";
	public static final String ARGS_NAVI_DATA_STRING = "Data";

	private String sRouteDataDst;// 新的导航目标
	private boolean isMKSearchDst = false;// 是否有新的查询目标
	private boolean isInitMKSearchBmap = false;// 是否已经初始化搜索地图
	private int iNaviType = StrUtils.GPS_NAVI_TYPE_DRIVING;// 默认驱车;
	private boolean mIsBaiduPoint;
	private ScreenHelper mScreenHelper;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(tag, "onCreate");
		setContentView(R.layout.navi);
		mScreenHelper = new ScreenHelper();
		mApplication = (GlobalApplication) getApplication();

		checkInitMapActivity();
		checkInitMap();
		checkInitSearch();

		isInitMKSearchBmap = true;
		registerAppRoutePlan(this);

		checkInetntExtrasInQueue();// 检查参数
		Log.e(tag, "onCreate");
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// // TODO Auto-generated method stub
	// super.onConfigurationChanged(newConfig);
	//
	// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	// ToastShow.show(this, "横屏");
	// } else {
	// ToastShow.show(this, "竖屏");
	// }
	// }

	/**
	 * 检查intent中的导航目的地参数，并把目的地加入到导航队列中
	 */
	private void checkInetntExtrasInQueue() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mIsBaiduPoint = getIntent().getBooleanExtra(ARGS_IS_BAIDU_POINT,
					false);

			// if (!mIsRecordReplay) {
			// String strDst = bundle.getString(StrUtils.GPS_NAVI_END_POINT);//
			// 目的地数据
			// iNaviType = bundle.getInt(StrUtils.GPS_NAVI_TYPE,
			// StrUtils.GPS_NAVI_TYPE_DRIVING);// 导航类型
			// if (!mNaviQueue.contains(strDst)) {
			// mNaviQueue.offer(strDst);
			// }
			//
			// } else {
			String naviData = bundle.getString(ARGS_NAVI_DATA_STRING);
			if (!mNaviQueue.contains(naviData)) {
				mNaviQueue.offer(naviData);
			}
			// }
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean checkGpsIsOpen() {
		LocationManager alm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

			return true;
		}
		return false;
	}

	private AlertDialog mGpsPopupDialog;

	private void showGpsPopupDialog() {
		if (mGpsPopupDialog == null) {
			Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_nogps_title);
			builder.setIcon(R.drawable.ic_dialog_alert_holo_light);
			builder.setMessage(R.string.dialog_nogps_message);
			builder.setNegativeButton(R.string.dialog_nogps_negative,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							hideGpsPopupDialog();

							// 转到GPS位置设置界面
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							NaviActivity.this.startActivityForResult(intent, 0);
						}
					});
			builder.setPositiveButton(R.string.dialog_nogps_positive,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {// 不开启GPS，直接导航时
							hideGpsPopupDialog();
							naviQueueDestination();
						}
					});
			mGpsPopupDialog = builder.create();
		}
		if (!mGpsPopupDialog.isShowing()) {
			mGpsPopupDialog.show();
		}
	}

	private void hideGpsPopupDialog() {
		mGpsPopupDialog.hide();
		mGpsPopupDialog.dismiss();
		mGpsPopupDialog = null;
	}

	/**
	 * 导航队列中的目的地
	 */
	private void naviQueueDestination() {
		while (!mNaviQueue.isEmpty()) {
			synchronized (mNaviQueue) {
				String reData = mNaviQueue.poll();
				setMKSearchDst(reData);
			}
		}
	}

	public void onResume() {
		// Log.e(tag, "onResume");

		mMyLocationOverlay.enableCompass();
		mMyLocationOverlay.enableMyLocation();
		mApplication.onResumeMapActivity();
		mApplication.getMapManager().getLocationManager()
				.requestLocationUpdates(this);
		super.onResume();
		MobclickAgent.onResume(this);

		mScreenHelper.acquireWakeLock(this);
		Log.e("NaviActivity", "mScreenHelper.acquireWakeLock(this);");
		// 检查GPS是否开启
		if (checkGpsIsOpen()) {// GPS打开时，直接导航队列中的目的地
			naviQueueDestination();
		} else {
			showGpsPopupDialog();
		}

		// Log.e(tag, "onResume");
	}

	public void onPause() {
		mMyLocationOverlay.disableCompass();
		mMyLocationOverlay.disableMyLocation();
		mApplication.onPauseMapActivity();
		super.onPause();
		MobclickAgent.onPause(this);

		mScreenHelper.releaseWakeLock();
		Log.e("NaviActivity", "mScreenHelper.releaseWakeLock();");
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterAppRoutePlan(this);
	}

	// 设置新的导航目标
	public void setMKSearchDst(String strDst) {
		isMKSearchDst = true;
		sRouteDataDst = strDst;

		String outData = "BMap收到导航目的地=: " + strDst;
		// Toast.makeText(this, outData, Toast.LENGTH_LONG).show();

		if (isInitMKSearchBmap) {
			handleMKSearchDst(StrUtils.GPS_NAVI_TYPE_DRIVING);// 默认驱车;
		}
	}

	private void showLoadingDialog() {
		mActionBar.setProgressBarVisibility(View.VISIBLE);

		// //显示等待对话框
		// if (mLoadingDialog == null) {
		// mLoadingDialog = CustomProgressDialog.show(this);
		// // 把当前Activity隐藏到后台？
		// mLoadingDialog
		// .setOnKeyListener(new DialogInterface.OnKeyListener() {
		//
		// @Override
		// public boolean onKey(DialogInterface dialog,
		// int keyCode, KeyEvent event) {
		// NaviActivity.this.finish();
		// return true;
		// }
		// });
		// } else if (!mLoadingDialog.isShowing()) {
		// mLoadingDialog.show();
		// }
	}

	private AlertDialog mLoadingDialog;

	private void hideLoadingDialog() {
		mActionBar.setProgressBarVisibility(View.GONE);
		// //隐藏等待对话框
		// if (mLoadingDialog != null) {
		// mLoadingDialog.dismiss();
		// mLoadingDialog = null;
		// }
	}

	// 处理导航目标数据
	private void handleMKSearchDst(int naviType) {
		if (!isMKSearchDst)
			return;

		isMKSearchDst = false;
		if (sRouteDataDst != null && !sRouteDataDst.equals("")) {
			String[] dataArr = sRouteDataDst.split(";");
			if (dataArr.length == 2) {
				Navigation naviSrc = handlePointData(dataArr[0]);
				Navigation naviDst = handlePointData(dataArr[1]);
				// // GeoPoint pointSrc =
				// //
				// CoordinateConvert.bundleDecode(CoordinateConvert.fromWgs84ToBaidu(new
				// // GeoPoint(ToolsUtil
				// // .strGPS2Int(naviSrc.latitude) * 10,
				// // ToolsUtil.strGPS2Int(naviSrc.longitude) * 10)));
				//
				// GeoPoint pointSrc = new GeoPoint(
				// ToolsUtil.strGPS2Int(naviSrc.latitude) * 10,
				// ToolsUtil.strGPS2Int(naviSrc.longitude) * 10);
				// 取当前坐标
				Location location = GlobalApplication.getLocation();
				GeoPoint pointSrc = new GeoPoint(
						(int) (location.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6));

				GeoPoint pointDst = null;
				if (mIsBaiduPoint) {
					pointDst = new GeoPoint(
							ToolsUtil.strGPS2Int(naviSrc.latitude) * 10,
							ToolsUtil.strGPS2Int(naviSrc.longitude) * 10);
				} else {
					pointDst = CoordinateConvert
							.bundleDecode(CoordinateConvert.fromWgs84ToBaidu(new GeoPoint(
									ToolsUtil.strGPS2Int(naviDst.latitude) * 10,
									ToolsUtil.strGPS2Int(naviDst.longitude) * 10)));
				}
				// String outData = "正在导航到：" + naviDst.address;
				String outData = "" + naviDst.address;
				Toast.makeText(this, outData, Toast.LENGTH_LONG).show();

				showMapNaviView(naviType, pointSrc, pointDst);
			}
		}
	}

	private Navigation handlePointData(String strData) {
		Navigation navi = new Navigation();
		try {
			String[] dataArr = strData.split(",");
			if (dataArr.length == 4) {
				navi.address = dataArr[3];
				navi.latitude = dataArr[2];// 纬
				navi.longitude = dataArr[1];// 经
				navi.isEncrypt = true;// dataArr[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return navi;
	}

	// 接收目的地
	private ReceiverAppRoutePlan mReceiverAppRoutePlan;

	private void registerAppRoutePlan(Context context) {
		mReceiverAppRoutePlan = new ReceiverAppRoutePlan();
		IntentFilter filter = new IntentFilter();
		filter.addAction(GpsWorker.ACTION_APP_NAVI_ROUTE_PLAN_BMAP);

		context.registerReceiver(mReceiverAppRoutePlan, filter);
	}

	private void unregisterAppRoutePlan(Context context) {
		if (mReceiverAppRoutePlan != null) {
			context.unregisterReceiver(mReceiverAppRoutePlan);
			mReceiverAppRoutePlan = null;
		}
	}

	private Queue<String> mNaviQueue = new LinkedList<String>();

	private class ReceiverAppRoutePlan extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			mIsBaiduPoint = intent.getBooleanExtra(ARGS_IS_BAIDU_POINT, false);
			String reData = intent.getStringExtra(ARGS_NAVI_DATA_STRING);

			// 加入队列
			mNaviQueue.offer(reData);

			// 如果GPS设置对话框没有显示则直接调用导航
			if (!(mGpsPopupDialog != null && mGpsPopupDialog.isShowing())) {
				naviQueueDestination();
			}
		}
	}

	private void showMapNaviView(int naviType, GeoPoint src, GeoPoint dst) {
		switch (naviType) {
		case StrUtils.GPS_NAVI_TYPE_DRIVING:
			startDrivingSearch(src, dst);
			break;
		case StrUtils.GPS_NAVI_TYPE_TRANSIT:
			startTransitSearch(src, dst);
			break;
		case StrUtils.GPS_NAVI_TYPE_WALKING:
			startWalkingSearch(src, dst);
			break;
		default:
			break;
		}
	}

	private GlobalApplication mApplication;

	public void checkInitMapActivity() {
		mApplication.onCreateMapManager();
		mApplication.onCreateMapActivity(this);
	}

	private MapView mMapView;
	private MyLocationOverlay mMyLocationOverlay;
	private MKSearch mMKSearch;

	private ActionBar mActionBar;
	private Button mBtnBack;

	private void checkInitMap() {
		if (mMapView == null) {
			mMapView = (MapView) findViewById(R.id.rmct_routes_baidumap);
			mMapView.setBuiltInZoomControls(true);
			// 设置在缩放动画过程中也显示overlay,默认为不绘制
			mMapView.setDrawOverlayWhenZooming(true);

			mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
			mMapView.getOverlays().add(mMyLocationOverlay);

			// 程序控制手机永远不锁屏或者永远亮光
			mMapView.setKeepScreenOn(true);

			mActionBar = (ActionBar) findViewById(R.id.navi_actionbar);
			mActionBar.setTitle("导航");
			mBtnBack = mActionBar.showBackButton();
			mBtnBack.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					NaviActivity.this.finish();
				}
			});

		}
	}

	// 显示当前位
	private void showLocation() {
		Location location = GlobalApplication.getLocation();
		if (location != null) {
			GeoPoint geopoint = new GeoPoint(
					(int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			mMapView.getController().setCenter(geopoint);
		}
	}

	private void checkInitSearch() {
		if (mMKSearch == null) {
			// 初始化搜索模块，注册事件监听
			mMKSearch = new MKSearch();
			mMKSearch.init(mApplication.getMapManager(), this);
		}
	}

	public void onGetPoiResult(MKPoiResult result, int type, int error) {
		hideLoadingDialog();
		// 错误号可参考MKEvent中的定义
		if (error != 0 || result == null) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
			return;
		}

		// // 将地图移动到第一个POI中心点
		// if (res.getCurrentNumPois() > 0) {
		// // 将poi结果显示到地图上
		// PoiOverlay poiOverlay = new PoiOverlay(this, mMapView);
		// poiOverlay.setData(res.getAllPoi());
		// mMapView.getOverlays().clear();
		// mMapView.getOverlays().add(poiOverlay);
		// mMapView.invalidate();
		// mMapView.getController().animateTo(res.getPoi(0).pt);
		// } else if (res.getCityListNum() > 0) {
		// String strInfo = "在";
		// for (int i = 0; i < res.getCityListNum(); i++) {
		// strInfo += res.getCityListInfo(i).city;
		// strInfo += ",";
		// }
		// strInfo += "找到结果";
		// Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
		// }
		// //////////////////////////////////////////////////////////
		// // 错误号可参考MKEvent中的定义
		// if (error != 0 || res == null) {
		// Toast.makeText(BusLineSearch.this, "抱歉，未找到结果",
		// Toast.LENGTH_LONG).show();
		// return;
		// }
		//
		// // 找到公交路线poi node
		// MKPoiInfo curPoi = null;
		// int totalPoiNum = res.getNumPois();
		// for (int idx = 0; idx < totalPoiNum; idx++) {
		// Log.d("busline", "the busline is " + idx);
		// curPoi = res.getPoi(idx);
		// if (2 == curPoi.ePoiType) {
		// break;
		// }
		// }
		//
		// mSearch.busLineSearch(mCityName, curPoi.uid);
		// }

	}

	public void onGetDrivingRouteResult(MKDrivingRouteResult result, int error) {
		hideLoadingDialog();
		// 错误号可参考MKEvent中的定义
		if (error != 0 || result == null) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			RouteOverlay routeOverlay = new RouteOverlay(this, mMapView);
			routeOverlay.setData(result.getPlan(0).getRoute(0));

			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.getOverlays().add(mMyLocationOverlay);
			mMapView.invalidate();

			mMapView.getController().animateTo(result.getStart().pt);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onGetTransitRouteResult(MKTransitRouteResult result, int error) {
		hideLoadingDialog();
		if (error != 0 || result == null) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		RouteOverlay routeOverlay = new RouteOverlay(this, mMapView);
		routeOverlay.setData(result.getPlan(0).getRoute(0));
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(routeOverlay);
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.invalidate();
		mMapView.getController().animateTo(result.getStart().pt);
	}

	public void onGetWalkingRouteResult(MKWalkingRouteResult result, int error) {
		hideLoadingDialog();
		if (error != 0 || result == null) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		RouteOverlay routeOverlay = new RouteOverlay(this, mMapView);
		routeOverlay.setData(result.getPlan(0).getRoute(0));
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(routeOverlay);
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.invalidate();
		mMapView.getController().animateTo(result.getStart().pt);
	}

	public void onGetAddrResult(MKAddrInfo result, int error) {
		hideLoadingDialog();
	}

	public void onGetBusDetailResult(MKBusLineResult result, int iError) {
		hideLoadingDialog();
		if (iError != 0 || result == null) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
			return;
		}

		RouteOverlay routeOverlay = new RouteOverlay(this, mMapView);
		// 此处仅展示一个方案作为示例
		routeOverlay.setData(result.getBusRoute());
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(routeOverlay);
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.invalidate();
		mMapView.getController().animateTo(result.getBusRoute().getStart());
	}

	/**
	 * 驾车路线
	 * 
	 * @param src
	 *            起始坐标
	 * @param dst
	 *            目的坐标
	 */
	public void startDrivingSearch(GeoPoint src, GeoPoint dst) {

		MKPlanNode srcMkPlanNode = new MKPlanNode();
		srcMkPlanNode.pt = src;

		MKPlanNode dstMkPlanNode = new MKPlanNode();
		dstMkPlanNode.pt = dst;

		mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
		mMKSearch.drivingSearch(null, srcMkPlanNode, null, dstMkPlanNode);
		showLoadingDialog();
	}

	/**
	 * 公交换乘
	 * 
	 * @param src
	 *            起始坐标
	 * @param dst
	 *            目的坐标
	 */
	public void startTransitSearch(GeoPoint src, GeoPoint dst) {
		MKPlanNode srcMkPlanNode = new MKPlanNode();
		srcMkPlanNode.pt = src;

		MKPlanNode dstMkPlanNode = new MKPlanNode();
		dstMkPlanNode.pt = dst;
		mMKSearch.transitSearch(null, srcMkPlanNode, dstMkPlanNode);
		showLoadingDialog();
	}

	/**
	 * 徒步行走
	 * 
	 * @param src
	 *            起始坐标
	 * @param dst
	 *            目的坐标
	 */
	public void startWalkingSearch(GeoPoint src, GeoPoint dst) {
		MKPlanNode srcMkPlanNode = new MKPlanNode();
		srcMkPlanNode.pt = src;

		MKPlanNode dstMkPlanNode = new MKPlanNode();
		dstMkPlanNode.pt = dst;
		mMKSearch.walkingSearch(null, srcMkPlanNode, null, dstMkPlanNode);
		showLoadingDialog();
	}

	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	private Location mLocation;

	public void onLocationChanged(Location location) {
		if (mLocation == null && location != null) {

			mMapView.getController().animateTo(
					new GeoPoint((int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6)));
		}
		mLocation = location;

		// ggz:w 每次位置改变的时候进行判断，需要的话，重新进行导航

	}

	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}
}