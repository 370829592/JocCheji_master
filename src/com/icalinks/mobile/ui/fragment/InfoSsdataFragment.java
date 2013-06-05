package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.exception.JocParameter;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.InfoActivity;
import com.icalinks.obd.vo.StatisticsInfo;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 统计数据
 * 
 */

public class InfoSsdataFragment extends BaseFragment {
	private static final String TAG = InfoSsdataFragment.class.getSimpleName();

	public InfoSsdataFragment(int resId) {
		super(resId);
	}

	private Typeface lcdTypeface;// LCD字体
	private InfoActivity mInfoActivity;
	private View mView;

	private String totalPay = "0";
	private TextView info_ssdata_daily_travel_num;
	private TextView info_ssdata_daily_oil_num;

	private TextView info_ssdata_mile_bc_num;
	private TextView info_ssdata_mile_daily_num;
	private TextView info_ssdata_mile_total_num;


	private SeekBar info_ssdata_scoil_bar;
	private TextView info_ssdata_scoil_num;

	private SeekBar info_ssdata_bcoil_bar;
	private TextView info_ssdata_bcoil_num;

	private SeekBar info_ssdata_bloil_bar;
	private TextView info_ssdata_bloil_num;

	private SeekBar info_ssdata_totaloil_bar;
	private TextView info_ssdata_totaloil_num;

	private String oil_sc_value = "0";
	private String oil_bc_value = "0";
	private String oil_bl_value = "0";
	private String oil_total_value = "0";

	private TextView info_ssdata_time_bc_hour;
	private TextView info_ssdata_time_bc_mintue;
	private TextView info_ssdata_time_daily_hour;
	private TextView info_ssdata_time_daily_mintue;
	private TextView info_ssdata_time_total_hour;
	private TextView info_ssdata_time_total_mintue;

	private String time[] = { "00", "00" };

	// private StatisticsOBD mSsdata;
	private ArrayList<StatisticsInfo> infoList;
	private StatisticsInfo latestSsdata;
	// private StatisticsInfo lastSsdata;// 前一数据

	private int travelCount;// 当日行驶次数

	private static final int INFO_SSDATA = 0x00;

	private final int ZOOM = 100;

	private float oilPay = 0f;

	private float oilCount = 0f;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.info_ssdata, null);
		mInfoActivity = (InfoActivity) this.getActivitySafe();

		lcdTypeface = Typeface.createFromAsset(mInfoActivity.getAssets(),
				"fonts/lcd.ttf");

		return mView;
	}

	public void onResume() {
		setupViews();
		conn(null);

		super.onResume();
	}

	private void setupViews() {
		setupDailyViews();
		setupMileViews();
		setupOilViews();
		setupTimeViews();

		setupFont();

		setListener();
	}

	private void setupDailyViews() {

		info_ssdata_daily_travel_num = (TextView) mView
				.findViewById(R.id.info_ssdata_daily_travel_num);

		info_ssdata_daily_travel_num.setText("0" + " ");

		info_ssdata_daily_oil_num = (TextView) mView
				.findViewById(R.id.info_ssdata_daily_oil_num);
		info_ssdata_daily_oil_num.setText("0" + " ");

	}

	private void setupMileViews() {


		info_ssdata_mile_bc_num = (TextView) mView
				.findViewById(R.id.info_ssdata_mile_bc_num);

		info_ssdata_mile_daily_num = (TextView) mView
				.findViewById(R.id.info_ssdata_mile_daily_num);

		info_ssdata_mile_total_num = (TextView) mView
				.findViewById(R.id.info_ssdata_mile_total_num);

		info_ssdata_mile_bc_num.setText(" " + "0" + " ");
		info_ssdata_mile_daily_num.setText(" " + "0" + " ");
		info_ssdata_mile_total_num.setText(" " + "0" + " ");
	}

	private void setupOilViews() {

		info_ssdata_scoil_bar = (SeekBar) mView
				.findViewById(R.id.info_ssdata_oil_sc);
		info_ssdata_scoil_num = (TextView) mView
				.findViewById(R.id.info_ssdata_oil_sc_num);


		info_ssdata_bcoil_bar = (SeekBar) mView
				.findViewById(R.id.info_ssdata_oil_bc);
		info_ssdata_bcoil_num = (TextView) mView
				.findViewById(R.id.info_ssdata_oil_bc_num);


		info_ssdata_bloil_bar = (SeekBar) mView
				.findViewById(R.id.info_ssdata_oil_bl);
		info_ssdata_bloil_num = (TextView) mView
				.findViewById(R.id.info_ssdata_oil_bl_num);

		info_ssdata_totaloil_bar = (SeekBar) mView
				.findViewById(R.id.info_ssdata_oil_total);
		info_ssdata_totaloil_num = (TextView) mView
				.findViewById(R.id.info_ssdata_oil_total_num);

		info_ssdata_scoil_num.setText("0.00" + " ");
		info_ssdata_bcoil_num.setText("0.00" + " ");
		info_ssdata_bloil_num.setText("0.00" + " ");
		info_ssdata_totaloil_num.setText("0.00" + " ");
	}

	private void setupTimeViews() {

		info_ssdata_time_bc_hour = (TextView) mView
				.findViewById(R.id.info_ssdata_time_bc_num_hours);

		info_ssdata_time_bc_hour.setText("00" + " ");
		info_ssdata_time_bc_mintue = (TextView) mView
				.findViewById(R.id.info_ssdata_time_bc_num_mintues);

		info_ssdata_time_bc_mintue.setText("00" + " ");

		info_ssdata_time_daily_hour = (TextView) mView
				.findViewById(R.id.info_ssdata_time_daily_num_hours);

		info_ssdata_time_daily_hour.setText("00" + " ");
		info_ssdata_time_daily_mintue = (TextView) mView
				.findViewById(R.id.info_ssdata_time_daily_num_mintues);

		info_ssdata_time_daily_mintue.setText("00" + " ");

		info_ssdata_time_total_hour = (TextView) mView
				.findViewById(R.id.info_ssdata_time_total_num_hours);

		info_ssdata_time_total_hour.setText("00000");
		info_ssdata_time_total_mintue = (TextView) mView
				.findViewById(R.id.info_ssdata_time_total_num_mintues);

		info_ssdata_time_total_mintue.setText("00" + " ");
	}

	private void setupFont() {
		
			info_ssdata_daily_travel_num.setTypeface(lcdTypeface);
			info_ssdata_daily_oil_num.setTypeface(lcdTypeface);
			info_ssdata_mile_bc_num.setTypeface(lcdTypeface);
			info_ssdata_mile_daily_num.setTypeface(lcdTypeface);
			info_ssdata_mile_total_num.setTypeface(lcdTypeface);
		
			info_ssdata_scoil_num.setTypeface(lcdTypeface);
			info_ssdata_bcoil_num.setTypeface(lcdTypeface);
			info_ssdata_bloil_num.setTypeface(lcdTypeface);
			info_ssdata_totaloil_num.setTypeface(lcdTypeface);

			info_ssdata_time_bc_hour.setTypeface(lcdTypeface);
			info_ssdata_time_bc_mintue.setTypeface(lcdTypeface);
			info_ssdata_time_daily_hour.setTypeface(lcdTypeface);
			info_ssdata_time_daily_mintue.setTypeface(lcdTypeface);
			info_ssdata_time_total_hour.setTypeface(lcdTypeface);
			info_ssdata_time_total_mintue.setTypeface(lcdTypeface);
	}

	private void setListener() {
		info_ssdata_scoil_bar
				.setOnSeekBarChangeListener(new SeekBarChangeListener());
		info_ssdata_bcoil_bar
				.setOnSeekBarChangeListener(new SeekBarChangeListener());
		info_ssdata_bloil_bar
				.setOnSeekBarChangeListener(new SeekBarChangeListener());
		info_ssdata_totaloil_bar
				.setOnSeekBarChangeListener(new SeekBarChangeListener());
	}

	private void conn(String date) {
		infoList = new ArrayList<StatisticsInfo>();
		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();// 得到当前用户
		if (userinfo != null) {
			OBDHelper.getStatisticsInfo(userinfo.name, userinfo.pswd, date,
					mSsdataOnCallbackListener);
		} else {
			clear();
		}
	}

	private void clear() {
		// daily
		{
			info_ssdata_daily_travel_num.setText("0" + " ");
			info_ssdata_daily_oil_num.setText("0" + " ");
		}
		// mile
		{
			info_ssdata_mile_bc_num.setText(" " + "0" + " ");
			info_ssdata_mile_daily_num.setText(" " + "0" + " ");
			info_ssdata_mile_total_num.setText(" " + "0" + " ");
		}
		// oil
		{
			oil_sc_value = "0.00";
			oil_bc_value = "0.00";
			oil_bl_value = "0.00";
			oil_total_value = "0.00";

			info_ssdata_scoil_num.setText(oil_sc_value + " ");
			info_ssdata_bcoil_num.setText(oil_bc_value + " ");
			info_ssdata_bloil_num.setText(oil_bl_value + " ");
			info_ssdata_totaloil_num.setText(oil_total_value + " ");

			info_ssdata_scoil_bar.setProgress((int) (Float
					.parseFloat(oil_sc_value) * ZOOM));
			info_ssdata_bcoil_bar.setProgress((int) (Float
					.parseFloat(oil_bc_value) * ZOOM));
			info_ssdata_bloil_bar.setProgress((int) (Float
					.parseFloat(oil_bl_value) * ZOOM));
			info_ssdata_totaloil_bar.setProgress((int) (Float
					.parseFloat(oil_total_value) * ZOOM));
		}
		// time
		{
			info_ssdata_time_bc_hour.setText("00" + " ");
			info_ssdata_time_bc_mintue.setText("00" + " ");
			info_ssdata_time_daily_hour.setText("00" + " ");
			info_ssdata_time_daily_mintue.setText("00" + " ");
			info_ssdata_time_total_hour.setText("00000" + " ");
			info_ssdata_time_total_mintue.setText("00" + " ");
		}
	}

	private OnCallbackListener mSsdataOnCallbackListener = new OnCallbackListener() {

		public void onSuccess(Result result) {
			ActionBarHelper.stopProgress();
			mHandler.sendMessage(mHandler.obtainMessage(INFO_SSDATA,
					result.object));
		}

		public void onFailure(Result result) {
			ActionBarHelper.stopProgress();
			clear();
		}

	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INFO_SSDATA:
				update(msg.obj);
			}

		};
	};

	@SuppressWarnings("unchecked")
	private void update(Object obj) {
		infoList.clear();
		infoList = (ArrayList<StatisticsInfo>) obj;
		Log.e(TAG, "size = " + infoList.size());
		if (infoList.size() >= 1) {
			latestSsdata = infoList.get(infoList.size() - 1);

			travelCount = infoList.get(0).getDayTravelCount();

			if (travelCount == 0) {
				oil_sc_value = latestSsdata.lastOilConsum;

			} else if (travelCount == 1) {
				oil_sc_value = latestSsdata.lastOilConsum;

			} else if (travelCount >= 2) {
				oil_sc_value = latestSsdata.lastOilConsum;
			}
			// daily
			{
				info_ssdata_daily_travel_num
						.setText(latestSsdata.dayTravelCount + " ");
				if (latestSsdata.thisOilConsum == null) {
					info_ssdata_daily_oil_num.setText(0 + " ");
				} else {

					oilPay = getOilPay();

					oilCount = stringToFloat(latestSsdata.dayOilConsum);

					totalPay = String.format("%.2f", oilPay * oilCount);

					info_ssdata_daily_oil_num.setText(totalPay + " ");
				}

			}
			// mile
			{
				info_ssdata_mile_bc_num.setText(" " + latestSsdata.thisMiles
						+ " ");
				info_ssdata_mile_daily_num.setText(" "
						+ latestSsdata.dayTotalMiles + " ");
				info_ssdata_mile_total_num.setText(" "
						+ latestSsdata.allTotalMiles + " ");
			}
			// oil
			{
				oil_bc_value = latestSsdata.thisOilConsumHAvg;
				oil_bl_value = latestSsdata.dayOilConsumHAvg;
				oil_total_value = latestSsdata.allOilConsumHAvg;

				if (oil_sc_value == null || oil_sc_value.equals("")) {
					oil_sc_value = "0";
				}
				info_ssdata_scoil_num.setText(oil_sc_value + " ");
				info_ssdata_bcoil_num.setText(oil_bc_value + " ");
				info_ssdata_bloil_num.setText(oil_bl_value + " ");
				info_ssdata_totaloil_num.setText(oil_total_value + " ");

				info_ssdata_scoil_bar.setProgress((int) (Float
						.parseFloat(oil_sc_value) * ZOOM));
				info_ssdata_bcoil_bar.setProgress((int) (Float
						.parseFloat(oil_bc_value) * ZOOM));
				info_ssdata_bloil_bar.setProgress((int) (Float
						.parseFloat(oil_bl_value) * ZOOM));
				info_ssdata_totaloil_bar.setProgress((int) (Float
						.parseFloat(oil_total_value) * ZOOM));
			}
			// time
			{
				time = reviseItem(latestSsdata.thisTravelTime);
				checkTime(time);
				info_ssdata_time_bc_hour.setText(time[0] + " ");
				info_ssdata_time_bc_mintue.setText(time[1] + " ");

				time = reviseItem(latestSsdata.dayTravelTime);
				checkTime(time);
				info_ssdata_time_daily_hour.setText(time[0] + " ");
				info_ssdata_time_daily_mintue.setText(time[1] + " ");

				time = reviseItem(latestSsdata.allTravelTime);
				checkTime(time);
				info_ssdata_time_total_hour.setText(time[0] + " ");
				info_ssdata_time_total_mintue.setText(time[1] + " ");
			}
		} else {
			clear();
		}
	}

	private String[] reviseItem(String mintues) {
		String time1[] = { "00", "00" };
		int allMin = (int) Float.parseFloat(mintues);
		int hourData = 0;
		int minData = 0;
		if (allMin >= 60) {
			hourData = allMin / 60;
		} else {
			hourData = 0;
		}

		minData = allMin % 60;

		time1[0] = Integer.toString(hourData);
		time1[1] = Integer.toString(minData);

		return time1;
	}

	private void checkTime(String time[]) {
		for (int i = 0; i < time.length; i++) {
			if (time[i].length() == 1) {
				time[i] = "0" + time[i];
			}
		}
	}

	public void onPause() {
		super.onPause();
		ActionBarHelper.stopProgress();
	}


	class SeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			switch (seekBar.getId()) {
			case R.id.info_ssdata_oil_sc:
				info_ssdata_scoil_bar.setProgress((int) (Float
						.parseFloat(oil_sc_value) * ZOOM));
				break;
			case R.id.info_ssdata_oil_bc:
				info_ssdata_bcoil_bar.setProgress((int) (Float
						.parseFloat(oil_bc_value) * ZOOM));
				break;

			case R.id.info_ssdata_oil_bl:
				info_ssdata_bloil_bar.setProgress((int) (Float
						.parseFloat(oil_bl_value) * ZOOM));
				break;

			case R.id.info_ssdata_oil_total:
				info_ssdata_totaloil_bar.setProgress((int) (Float
						.parseFloat(oil_total_value) * ZOOM));
				break;

			default:
				break;
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

	}

	private float getOilPay() {
		SharedPreferences preferences = mInfoActivity.getSharedPreferences(
				JocParameter.FILENAME_OIL, Activity.MODE_PRIVATE);

		String tempOil = preferences.getString(JocParameter.FILENAME_OIL, "0");
		if (tempOil == null || tempOil.equals("")) {
			tempOil = "0";
		}
		oilPay = stringToFloat(tempOil);

		return oilPay;
	}

	private float stringToFloat(String str) {
		return Float.parseFloat(str);
	}
}
