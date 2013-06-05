package com.icalinks.mobile.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.icalinks.common.AppsHelper;
import com.icalinks.mobile.db.dal.NaviDal;
import com.icalinks.mobile.db.dal.NaviInfo;
import com.provider.model.resources.Navigation;

public class MapNaviRouterActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_AUTONAVI_BOOTED);
		filter.addAction(ACTION_NAVI_RESPONSE);
		registerReceiver(mBroadcastReceiver, filter);

		// 检测是否购买(不一定是安装)了高德地图
		if (AppsHelper.exists(this, "com.mapabc.android")) {
			// 有安装，转到高德应用
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName("com.mapabc.android",
					"com.mapabc.android.activity.AutoNaviStartActicity");
			startActivity(intent);
		} else {
			// 没安装，百度地图导航
			Intent intent = new Intent();
			intent.setClass(this, NaviActivity.class);
			intent.putExtra(NaviActivity.ARGS_NAVI_DATA_STRING, getIntent()
					.getStringExtra(ARGS_NAVI_DATA_STRING));
			intent.putExtra(NaviActivity.ARGS_IS_BAIDU_POINT, true);// 标记不用记录入数据库
			startActivity(intent);
			this.finish();
		}
	}

	public static void recordToDb(Context context, NaviInfo info) {
		try {
			// 记录到数据库(不插入重发记录)
			NaviInfo dbNaviInfo = NaviDal.getInstance(context).exists(
					info.getEndAddrs());
			if (dbNaviInfo == null) {
				NaviDal.getInstance(context).insert(info);
			} else {
				NaviDal.getInstance(context).update(dbNaviInfo.get_id());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static final String ARGS_NAVI_DATA_STRING = "Data";
	public static final String ACTION_APP_NAVI_ROUTE_PLAN = "APP_NAVI_ROUTE_PLAN";

	public static final String ACTION_NAVI_REQUEST = "com.icalinks.action.NAVI_DATA";
	public static final String ACTION_NAVI_RESPONSE = "com.icalinks.action.NAVI_RESPONSE";
	public static final String ACTION_AUTONAVI_BOOTED = "com.icalinks.action.AUTONAVI_BOOTED";

	public static final String ARGS_SOURCE_GEOPOINT = "args_source_geopoint";
	public static final String ARGS_TARGET_GEOPOINT = "args_target_geopoint";
	public static final String ARGS_NAVI_MESSAGE_ID = "args_navi_message_id";

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);

	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_AUTONAVI_BOOTED)) {
				Intent xxx = new Intent(ACTION_APP_NAVI_ROUTE_PLAN);
				{
					Bundle bundle = MapNaviRouterActivity.this.getIntent()
							.getExtras();

					// String strData = String.format(
					// "false,%s,%s,%s;false,%s,%s,%s", "0.0", "0.0", "B",
					// "113.969368", "22.602284", "E");
					xxx.putExtra(ARGS_NAVI_DATA_STRING,
							bundle.getString(ARGS_NAVI_DATA_STRING));
				}
				sendBroadcast(xxx);
				// Toast.makeText(this, xxx.toURI(), Toast.LENGTH_SHORT).show();

				// 结束掉当前activity
				mHandler.sendMessage(mHandler
						.obtainMessage(WHAT_FINISH_ACTIVITY));
			} else if (action.equals(ACTION_NAVI_RESPONSE)) {
//				Toast.makeText(MapNaviRouterActivity.this, "高德导航接收成功！",
//						Toast.LENGTH_SHORT).show();

				// 结束掉当前activity
				mHandler.sendMessage(mHandler
						.obtainMessage(WHAT_FINISH_ACTIVITY));
			}
		}
	};
	private static final int WHAT_FINISH_ACTIVITY = 0X0000;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_FINISH_ACTIVITY:
				MapNaviRouterActivity.this.finish();
				break;
			}
		};
	};
}

// private void test(String sRouteDataDst) {
// if (sRouteDataDst != null && !sRouteDataDst.equals("")) {
// String[] dataArr = sRouteDataDst.split(";");
// if (dataArr.length == 2) {
// Navigation naviSrc = handlePointData(dataArr[0]);
// Navigation naviDst = handlePointData(dataArr[1]);
//
// GeoPoint pointSrc = new GeoPoint(
// (int) (Double.parseDouble(naviSrc.latitude) * 1E6),
// (int) (Double.parseDouble(naviSrc.longitude) * 1E6));
//
// GeoPoint pointDst = new GeoPoint(
// (int) (Double.parseDouble(naviDst.latitude) * 1E6),
// (int) (Double.parseDouble(naviDst.longitude) * 1E6));
//
// }
// }
// }

// private Navigation handlePointData(String strData) {
// Navigation navi = new Navigation();
// try {
// String[] dataArr = strData.split(",");
// if (dataArr.length == 4) {
// navi.address = dataArr[3];
// navi.latitude = dataArr[2];// 纬
// navi.longitude = dataArr[1];// 经
// navi.isEncrypt = true;// dataArr[0];
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// return navi;
// }

// String strData = String.format("false,%s,%s,%s;false,%s,%s,%s",
// location.getLongitude(), location.getLatitude(), "X", info
// .getEndPoint().getLongitudeE6() / 1000000.0, info
// .getEndPoint().getLatitudeE6() / 1000000.0, info
// .getEndAddrs());
// intent.putExtra(NaviActivity.ARGS_NAVI_DATA_STRING, strData);
// intent.putExtra(NaviActivity.ARGS_IS_RECORD_REPLAY, true);// 标记不用记录入数据库