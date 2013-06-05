package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.mapapi.BitmapOverlay;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MovingOverlay;
import com.baidu.mapapi.MxLocationOverlay;
import com.baidu.mapapi.OnOverlayEventListener;
import com.baidu.mapapi.RoutesHelper;
import com.baidu.mapapi.RoutesOverlay;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.util.DateTime;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.mobile.widget.PopupWindowDialog;
import com.icalinks.obd.vo.GPSInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;
import com.umeng.analytics.MobclickAgent;

public class PathActivity extends MapActivity implements OnCallbackListener,
		View.OnClickListener, OnOverlayEventListener, MKSearchListener {
	public static final String ARGS_TOP_NAME = "args_top_name";
	public static final String ARGS_BEG_DATE = "args_beg_date";
	public static final String ARGS_END_DATE = "args_end_date";

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.path);
		mPathActivity = this;
		mApplication = GlobalApplication.getApplication();
		checkInitMapActivity();
		checkInitMap();
		checkInitSearch();
		checkInitRoutes();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	private GlobalApplication mApplication;
	private MapView mMapView;
	private MKSearch mMKSearch;

	private PathActivity mPathActivity;
	private ActionBar mActionBar;
	private Button mViewback;

	public void checkInitMapActivity() {
		mApplication.onCreateMapManager();
		mApplication.onCreateMapActivity(this);
	}

	// private MyLocationOverlay mMyLocationOverlay;

	private void checkInitMap() {
		if (mMapView == null) {
			mMapView = (MapView) findViewById(R.id.path_mapview);
			// 设置是否启用内置的缩放控件
			mMapView.setBuiltInZoomControls(true);
			// 设置在缩放动画过程中也显示overlay,默认为不绘制
			mMapView.setDrawOverlayWhenZooming(true);
			// mMapView.getOverlays().clear();

			// 添加定位图层
			mMxLocationOverlay = new MxLocationOverlay(mPathActivity, mMapView);
			mMxLocationOverlay.setOnOverlayEventListener(this);
			mMapView.getOverlays().add(mMxLocationOverlay);

			// 添加汽车图层
			mCaricoOverlay = new BitmapOverlay(mPathActivity, mMapView);
			mCaricoOverlay.setBitmap("rmct_routes_car.png");
			mMapView.getOverlays().add(mCaricoOverlay);

			mBtnBegTime = (Button) findViewById(R.id.path_begtime);
			mBtnEndTime = (Button) findViewById(R.id.path_endtime);
			mBtnBegTime.setOnClickListener(this);
			mBtnEndTime.setOnClickListener(this);

			mActionBar = (ActionBar) findViewById(R.id.path_actionbar);
			mActionBar.setRootView(findViewById(R.id.path_main));
			mViewback = mActionBar.showBackButton();
			mViewback.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PathActivity.this.finish();
				}
			});
		}
	}

	private Button mBtnBegTime;
	private Button mBtnEndTime;

	private void checkInitSearch() {
		if (mMKSearch == null) {
			// 初始化搜索模块，注册事件监听
			mMKSearch = new MKSearch();
			mMKSearch.init(mApplication.getMapManager(), this);
		}
	}

	private String mDateTimeFormat = "yyyy-MM-dd HH:mm";
	private DateTime mBegDateTime;
	private DateTime mEndDateTime;
	private DatePicker datetime_picker_date;
	private TimePicker datetime_picker_time;

	private void checkInitRoutes() {
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			toPointCenter();
			ToastShow.show(this, "参数错误，没有轨迹的起始和结束时间！");
			return;
		}
		try {
			String topName = bundle.getString(PathActivity.ARGS_TOP_NAME);
			String begTime = bundle.getString(PathActivity.ARGS_BEG_DATE);
			String endTime = bundle.getString(PathActivity.ARGS_END_DATE);

			mActionBar.setTitle(topName);
			if (begTime != null && endTime != null) {
				mBegDateTime = DateTime.from(begTime);
				mBtnBegTime.setText(mBegDateTime.toString(mDateTimeFormat));
				mEndDateTime = DateTime.from(endTime);
				mBtnEndTime.setText(mEndDateTime.toString(mDateTimeFormat));
				sendRoutesRequest(begTime, endTime);
			} else {
				{
					mEndDateTime = DateTime.now();
					mBegDateTime = DateTime.from(mEndDateTime);
					// 默认当天6点开始
					mBegDateTime.setHours(6);
					mBegDateTime.setMinutes(0);
					// mEndDateTime.addDay(-3);// 默认查最近三天的记录
				}
				mBtnBegTime.setText(mBegDateTime.toString(mDateTimeFormat));
				mBtnEndTime.setText(mEndDateTime.toString(mDateTimeFormat));

				toCaricoCenter();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// ToastShow.show(this, "Intent参数不正确!");
		}
	}

	// 居中显示车辆位置或当前位
	private void toPointCenter() {
		Location location = GlobalApplication.getLocation();
		if (location != null) {
			GeoPoint geopoint = new GeoPoint(
					(int) (location.getLatitude() * 1e6),
					(int) (location.getLongitude() * 1e6));
			mMapView.getController().setCenter(geopoint);
		}
	}

	private void toCaricoCenter() {
		GeoPoint carGeoPoint = mApplication.getCarPosition().getGps();
		if (carGeoPoint != null) {
			mMapView.getController().setCenter(carGeoPoint);
		} else {
			toPointCenter();
		}
	}

	private MxLocationOverlay mMxLocationOverlay;
	private BitmapOverlay mCaricoOverlay;

	// private UserInfo mCurrUserInfo;

	public void onResume() {
		mApplication.onResumeMapActivity();
		mMxLocationOverlay.enableMyLocation();
		mMxLocationOverlay.enableCompass();
		mCaricoOverlay.setData(mApplication.getCarPosition());
		// if (mMovingOverlay != null) {
		// mMovingOverlay.onResume();
		// }
		super.onResume();
		MobclickAgent.onResume(this);
	}

	// public void showPosition() {
	// // mBitmapOverlay.setData(mRmctActivity.getPosition());
	// mMapView.getController().animateTo(
	// mMapBaiActivity.getMapPointInfo().getGps());
	// }

	private void sendRoutesRequest(String begTime, String endTime) {
		// 查询车辆信息
		UserInfo userinfo = mApplication.getCurrUser();// 得到当前用户
		if (userinfo != null) {
			OBDHelper.getVehicleHistory(userinfo.name, userinfo.pswd, begTime,
					endTime, this);
			mActionBar.setProgressBarVisibility(View.VISIBLE);

		}
		// else {
		// ToastShow.show(mRmctActivity, R.string.toast_no_login);
		// }

	}

	public void onPause() {
		mApplication.onPauseMapActivity();
		mMxLocationOverlay.disableMyLocation();
		mMxLocationOverlay.disableCompass(); // 关闭指南针
		// if (mMovingOverlay != null) {
		// mMovingOverlay.onPause();
		// }
		super.onPause();
		MobclickAgent.onPause(this);
		mActionBar.setProgressBarVisibility(View.GONE);
	}

	private RoutesOverlay mRoutesOverlay;

	// private MovingOverlay mMovingOverlay;

	private void showMapViewRoutes(List<GPSInfo> lstWgs84HashMap) {
		ArrayList<GeoPoint> lstWgs84GeoPoint = RoutesHelper
				.getWgs84GeoPoint(lstWgs84HashMap);

		// mRoutesOverlay
		if (mMapView != null) {
			try {
				if (mRoutesOverlay != null) {
					if (mMapView.getOverlays().contains(mRoutesOverlay)) {
						mMapView.getOverlays().remove(mRoutesOverlay);
						mRoutesOverlay = null;
					}
				}
				mRoutesOverlay = new RoutesOverlay(mPathActivity, mMapView);
				{
					mRoutesOverlay.setOnOverlayEventListener(this);

					mRoutesOverlay.setData(lstWgs84GeoPoint);
				}
				mMapView.getOverlays().add(0, mRoutesOverlay);
				mMapView.invalidate();
				mMapView.getController().animateTo(lstWgs84GeoPoint.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Button mCurrButton;

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.datetime_picker_submit:
			saveSelectedDateTime();
			sendRoutesRequest(mBegDateTime.toString(), mEndDateTime.toString());
		case R.id.datetime_picker_cancel:
		case R.id.datetime_picker_close:
			hidePickerDialog();
			break;
		case R.id.path_endtime:
		case R.id.path_begtime:
			mCurrButton = (Button) v;
			showPickerDialog(mCurrButton.getHint().toString(), mCurrButton
					.getText().toString());
			break;
		}
	}

	private PopupWindowDialog mDialog;

	private void hidePickerDialog() {
		mDialog.destroy();
	}

	private void saveSelectedDateTime() {

		// save beg date
		mBegDateTime.setYear(datetime_picker_date.getYear());
		mBegDateTime.setMonth(datetime_picker_date.getMonth());
		mBegDateTime.setDay(datetime_picker_date.getDayOfMonth());

		// save end date
		mEndDateTime.setYear(datetime_picker_date.getYear());
		mEndDateTime.setMonth(datetime_picker_date.getMonth());
		mEndDateTime.setDay(datetime_picker_date.getDayOfMonth());

		switch (mCurrButton.getId()) {
		case R.id.path_begtime:
			// // date
			// mBegDateTime.setYear(datetime_picker_date.getYear());
			// mBegDateTime.setMonth(datetime_picker_date.getMonth());
			// mBegDateTime.setDay(datetime_picker_date.getDayOfMonth());
			// time
			mBegDateTime.setHours(datetime_picker_time.getCurrentHour());
			mBegDateTime.setMinutes(datetime_picker_time.getCurrentMinute());
			// // set
			// mCurrButton.setText(mBegDateTime.toString(mDateTimeFormat));
			break;
		case R.id.path_endtime:
			// // date
			// mEndDateTime.setYear(datetime_picker_date.getYear());
			// mEndDateTime.setMonth(datetime_picker_date.getMonth());
			// mEndDateTime.setDay(datetime_picker_date.getDayOfMonth());
			// time
			mEndDateTime.setHours(datetime_picker_time.getCurrentHour());
			mEndDateTime.setMinutes(datetime_picker_time.getCurrentMinute());
			// // set
			// mCurrButton.setText(mEndDateTime.toString(mDateTimeFormat));
			break;
		}

		// set
		mBtnBegTime.setText(mBegDateTime.toString(mDateTimeFormat));

		// set
		mBtnEndTime.setText(mEndDateTime.toString(mDateTimeFormat));
	}

	private void showPickerDialog(String title, String strDatetime) {
		if (mDialog == null) {
			mDialog = new PopupWindowDialog(mPathActivity);
			mDialog.setContentView(R.layout.datetime_picker);
		}

		View view = mDialog.getContentView();
		{
			view.findViewById(R.id.datetime_picker_close).setOnClickListener(
					this);
			view.findViewById(R.id.datetime_picker_cancel).setOnClickListener(
					this);
			view.findViewById(R.id.datetime_picker_submit).setOnClickListener(
					this);
		}

		TextView lblTitle = (TextView) view
				.findViewById(R.id.datetime_picker_title);
		datetime_picker_date = (DatePicker) view
				.findViewById(R.id.datetime_picker_date);
		datetime_picker_time = (TimePicker) view
				.findViewById(R.id.datetime_picker_time);
		lblTitle.setText(title);

		DateTime datetime = DateTime.from(strDatetime, "yyyy-MM-dd HH:mm");

		datetime_picker_date.init(datetime.getYear(), datetime.getMonth(),
				datetime.getDay(), null);// date.getDay()

		// pkrTime.setIs24HourView(true);// 24小时制
		datetime_picker_time.setCurrentHour(datetime.getHours());// 设置当前小时。
		datetime_picker_time.setCurrentMinute(datetime.getMinutes());// 当前分钟
		// datetime_picker_time.setOnTimeChangedListener(null);
		mDialog.show();
	}

	private Queue<Integer> mReqCodeQueue = new LinkedList<Integer>();

	public void reverseGeocode(GeoPoint location, int reqCode) {
		mReqCodeQueue.offer(reqCode);
		mMKSearch.reverseGeocode(location);
	}

	public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
		// arg0.geoPt
		if (arg1 != 0 || arg0 == null) {
			// 异常了
			ToastShow.show(mPathActivity, "地址翻译出错");
			return;
		}
		synchronized (mReqCodeQueue) {
			Integer reqCode = mReqCodeQueue.poll();
			if (reqCode != null) {
				switch (reqCode.intValue()) {
				case RoutesOverlay.REVERSE_GEOCODE:
					if (mRoutesOverlay != null)
						mRoutesOverlay.onGetAddrResult(arg0);
					break;
				case MxLocationOverlay.REVERSE_GEOCODE:
					if (mMxLocationOverlay != null)
						mMxLocationOverlay.onGetAddrResult(arg0);
					break;
				}
			}
		}
	}

	public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}

	public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	public void onDestroy() {
		super.onDestroy();
		// if (mMovingOverlay != null) {
		// mMovingOverlay.onDestroy();
		// }
	}

	public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	protected void onHandlerFailure(Object obj) {
		mActionBar.setProgressBarVisibility(View.GONE);
		String err = null;
		try {
			err = ((Result) obj).head.resMsg;
		} catch (Exception ex) {
		}
		Toast.makeText(this, "" + err, Toast.LENGTH_SHORT).show();
		toPointCenter();
	}

	protected void onHandlerSuccess(Object obj) {
		mActionBar.setProgressBarVisibility(View.GONE);
		if (obj != null) {
			@SuppressWarnings("unchecked")
			List<GPSInfo> lstWgs84HashMap = (List<GPSInfo>) obj;
			if (lstWgs84HashMap.size() > 0) {// 至少得有一个点嘛(第一个，最后一个有没有可能一样呢?有的)
				showMapViewRoutes(lstWgs84HashMap);
			} else {
				ToastShow.show(mPathActivity, R.string.datetime_toast_nodata);

				if (mMapView.getOverlays().contains(mRoutesOverlay)) {
					mMapView.getOverlays().remove(mRoutesOverlay);
					// mRoutesOverlay.onDestroy();
					mRoutesOverlay = null;
				}
				// if (mMapView.getOverlays().contains(mMovingOverlay)) {
				// mMapView.getOverlays().remove(mMovingOverlay);
				// mMovingOverlay.onDestroy();
				// mMovingOverlay = null;
				// }
			}
		}
	}

	private static final int WHAT_FAILURE = 0;
	private static final int WHAT_SUCCESS = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_FAILURE:
				onHandlerFailure(msg.obj);
				break;
			case WHAT_SUCCESS:
				Object obj = null;
				try {
					if (msg.obj != null) {
						obj = ((Result) msg.obj).object;
					}
				} catch (Exception ex) {
				}
				onHandlerSuccess(obj);
				break;
			}

		};
	};

	public void onFailure(Result result) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE, result));
	}

	public void onSuccess(Result result) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_SUCCESS, result));
	}
}
