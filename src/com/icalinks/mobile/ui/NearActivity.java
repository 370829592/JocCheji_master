package com.icalinks.mobile.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.baidu.mapapi.GeoPoint;
import com.icalinks.common.HttpHelper;
import com.icalinks.common.MapsHelper;
import com.icalinks.common.UrlConfig;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.ui.adapter.NearAdapter;
import com.icalinks.mobile.ui.model.NearItem;
import com.icalinks.mobile.ui.model.NearItemDistanceComparator;
import com.icalinks.mobile.util.ToastShow;
import com.markupartist.android.widget.ActionBar;

public class NearActivity extends AbsSubActivity implements LocationListener {

	private ActionBar mActionBar;
	private Button mBtnBack;
	// private TextView mMainTextView;

	private ListView mListView;
	private NearAdapter mNearAdapter;
	private int mCurrentSearchRadius = 1;

	// private GlobalApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.near);
		// initList();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initView();
		setTitle(getString(R.string.ms_czb));
		loadCurrentLocation();
	}
	
	private void initView() {
		mListView = (ListView)findViewById(R.id.near_listview);
		mActionBar = (ActionBar) findViewById(R.id.near_actionbar);
		mBtnBack = mActionBar.showBackButton();
		GlobalApplication.getApplication().getHomeActivity().showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});

		// mMainTextView = (TextView) findViewById(R.id.near_none);

//		Bundle bundle = getIntent().getExtras();
//		if (bundle != null) {
//			mOptionId = bundle.getInt(ARGS_OPTION_ID);
//			switch (mOptionId) {
//			case UrlConfig.REQ_TYPE_JYZ:
//				mActionBar.setTitle("附近加油站");
//				break;
//			case UrlConfig.REQ_TYPE_TCC:
//				mActionBar.setTitle("附近停车场");
//				break;
//			}
//		} else {
//			ToastShow.show(this, "intent参数有误！！！！！！！");
//		}
	}

	private LocationManager mLocationManager;
	private boolean mIsHaveAccessLocation = false;

	private void loadCurrentLocation() {
		mActionBar.setProgressBarVisibility(View.VISIBLE);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 0, this);

		// 启动定时器，在5秒钟没有订到位置的情况下，直接使用百度坐标查询
		mTimer.schedule(mTimerTask, 5 * 1000);// 5s
	}

	private void clearCurrentLocation() {
		// synchronized (this) {
		if (!mIsHaveAccessLocation) {
			try {
				mLocationManager.removeUpdates(this);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			searchNearby(mOptionId, mCurrentSearchRadius);
		}
		// }
	}

	private final Timer mTimer = new Timer();
	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			// 取消定时器
			mTimer.cancel();
			mTimerTask.cancel();
			clearCurrentLocation();
		}
	};

	private int mOptionId;
	public static final String ARGS_OPTION_ID = "args_option_id";
	private Location mCurrentLocation;

	private void searchNearby(int optionId, int searchRadius) {

		// mActionBar.setProgressBarVisibility(View.VISIBLE);
		// new Thread() {
		// public void run() {
		if (mCurrentLocation == null) {
			mCurrentLocation = GlobalApplication.getLocation();
		}
		String strResult = null;
		switch (optionId) {
		case UrlConfig.REQ_TYPE_JYZ:
			strResult = HttpHelper
					.getHttpResponse(UrlConfig.getPoiUrl(optionId,
							mCurrentLocation.getLatitude(),
							mCurrentLocation.getLongitude(),
							searchRadius * 1000 + 2000));// 5000mCurrentSearchRadius

			Log.e("NearActivity", "searchRadius:" + searchRadius + ",radius:"
					+ (searchRadius * 1000 + 2000));
			break;
		case UrlConfig.REQ_TYPE_TCC:
			strResult = HttpHelper.getHttpResponse(UrlConfig.getPoiUrl(
					optionId, mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude(), searchRadius * 500));// 1000mCurrentSearchRadius

			Log.e("NearActivity", "searchRadius:" + searchRadius + ",radius:"
					+ (searchRadius * 500));
			break;
		}

		mHandler.sendMessage(mHandler.obtainMessage(0, strResult));
		// }
		// }.start();
	}

	private static final String REQ_RLT_STATUS_OK = "OK";
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			// 生成 JSON 对象
			JSONObject json = null;
			try {
				json = new JSONObject("" + msg.obj);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (json == null) {
				ToastShow.show(NearActivity.this, "附近查询什么都没有返回！！！");
				return;
			}
			String status = null;
			try {
				status = json.getString("status");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			JSONArray results = null;
			try {
				results = json.getJSONArray("results");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (status.equals(REQ_RLT_STATUS_OK)) {
				int size = results.length();
				Log.e("NearActivity", "results count:" + size);
				if (size >= 5) {
					// mMainTextView.setVisibility(View.GONE);
					Location refloca = GlobalApplication.getLocation();
					GeoPoint refGeoPoint = new GeoPoint(
							(int) (refloca.getLatitude() * 1e6),
							(int) (refloca.getLongitude() * 1e6));

					List<NearItem> lstItem = new ArrayList<NearItem>(size);
					try {
						for (int i = 0; i < size; i++) {

							lstItem.add(new NearItem(results.getJSONObject(i),
									refGeoPoint));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					Collections.sort(lstItem, new NearItemDistanceComparator());

					mNearAdapter = new NearAdapter(NearActivity.this, lstItem);
					mListView.setAdapter(mNearAdapter);
					mActionBar.setProgressBarVisibility(View.GONE);
				} else {
					mCurrentSearchRadius++;
					searchNearby(mOptionId, mCurrentSearchRadius);
				}
			} else {
				// mMainTextView.setVisibility(View.VISIBLE);
				// switch (optionId) {
				// case UrlConfig.REQ_TYPE_JYZ:
				// mMainTextView.setText("附近没有加油站");
				// break;
				// case UrlConfig.REQ_TYPE_TCC:
				// mMainTextView.setText("附近没有停车场");
				// break;
				// }
			}
		};
	};

	protected void onListItemClick(android.widget.ListView l, View v,
			int position, long id) {
		Location location = GlobalApplication.getLocation();
		if (location == null) {
			ToastShow.show(this, "无法获取当前位置，请稍后再试！");
			return;
		}

		NearItem item = (NearItem) mNearAdapter.getItem(position);
		GeoPoint wgs84GeoPoint = MapsHelper
				.fromBaiduToWgs84(item.getLocation());
		Intent intent = new Intent();
		{
			intent.setClass(this, MapNaviRouterActivity.class);
			String strData = String.format("false,%s,%s,%s;false,%s,%s,%s",
					item.getLocation().getLongitudeE6() / 1000000.0, item
							.getLocation().getLatitudeE6() / 1000000.0, item
							.getName(),
					wgs84GeoPoint.getLongitudeE6() / 1000000.0, wgs84GeoPoint
							.getLatitudeE6() / 1000000.0, item.getName());// +
																			// " \n "
																			// +
																			// item.getAddress()
			intent.putExtra(NaviActivity.ARGS_NAVI_DATA_STRING, strData);
			intent.putExtra(NaviActivity.ARGS_IS_BAIDU_POINT, true);
		}
		this.startActivity(intent);
	}

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
		clearCurrentLocation();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	};
}
