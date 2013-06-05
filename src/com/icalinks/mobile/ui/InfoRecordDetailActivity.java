package com.icalinks.mobile.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.icalinks.common.MParams;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.exception.JocParameter;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.ui.activity.PublicActivity;
import com.icalinks.mobile.ui.model.InfoTravleItem;
import com.icalinks.mobile.util.MLogUtils;
import com.icalinks.obd.vo.TravelInfo;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 
 * 单次行驶记录
 * 
 * @author huzg@calinks.com.cn
 * 
 */

public class InfoRecordDetailActivity extends
		AbsSubActivity implements View.OnClickListener{

	// @+id/info_record_item_time_month //月份
	// @+id/info_record_item_time_date //日期
	// @+id/info_record_item_pos_start_ //开始地点
	// @+id/info_recored_item_pos_end_ //结束地点
	// @+id/info_record_item_time_s //开始时间
	// @+id/info_record_item_time_e //结束时间
	// @+id/info_record_item_oil_count //油耗数量
	// @+id/info_record_item_oil_pay //油耗费用
	// @+id/info_ssdata_record_mile_value //本次行驶里程数
	// @+id/info_record_item_map_speed //最高时速
	// @+id/info_record_item_time 耗时
//	info_ssdata_record_map_nav  点击进入轨迹界面

	private TextView info_recored_item_pos_start_; // 开始地点
	private TextView info_recored_item_pos_end_;// 结束地点

	private TextView info_record_item_time_month; // 月份
	private TextView info_record_item_time_date; // 日期

	private TextView info_record_item_time_start; // 开始时间
	private TextView info_record_item_time_end; // 结束时间

	private TextView info_record_item_time_;// 耗时

	private TextView info_record_item_oil_count;// 油耗数量
	private TextView info_record_item_oil_pay;// 油耗费用

	private TextView info_ssdata_record_mile_value;// 本次行驶里程数

	private TextView info_record_item_map_speed;// 最高时速

	private TravelInfo mTravelInfo;
	private InfoTravleItem mInfoTravleItem;

	private float oilPay = 0f;
	private float oilCount = 0f;
	private String totalPay = "0";
	private Typeface lcdTypeface;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mInfoTravleItem = (InfoTravleItem) getIntent().getSerializableExtra(
				"info_record");

		setContentView(R.layout.info_ssdata_record_item);

		lcdTypeface = Typeface.createFromAsset(this.getAssets(),
				"fonts/lcd.ttf");

		setupViews();
		setupFonts();

		conn();

		setupValues();
		findViewById(R.id.info_ssdata_record_map_nav).setOnClickListener(this);
		
		MLogUtils.printEMsg("onCreate;");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showBackButton().setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InfoRecordDetailActivity.this,PublicActivity.class);
				intent.putExtra(MParams.FLAG_INTENT, MParams.QCYS_DCXSJL);
				startActivity(intent);
			}
		});
		hideRightButton();
	}

	private void setupViews() {
		info_recored_item_pos_start_ = (TextView) findViewById(R.id.info_record_item_pos_start_);
		info_recored_item_pos_end_ = (TextView) findViewById(R.id.info_recored_item_pos_end_);

		info_record_item_time_month = (TextView) findViewById(R.id.info_record_item_time_month);
		info_record_item_time_date = (TextView) findViewById(R.id.info_record_item_time_date);
		info_record_item_time_start = (TextView) findViewById(R.id.info_record_item_time_s);
		info_record_item_time_end = (TextView) findViewById(R.id.info_record_item_time_e);
		info_record_item_time_ = (TextView) findViewById(R.id.info_record_item_time);

		info_record_item_oil_count = (TextView) findViewById(R.id.info_record_item_oil_count);
		info_record_item_oil_pay = (TextView) findViewById(R.id.info_record_item_oil_pay);

		info_ssdata_record_mile_value = (TextView) findViewById(R.id.info_ssdata_record_mile_value);

		info_record_item_map_speed = (TextView) findViewById(R.id.info_record_item_map_speed);
	}

	// 设置lcd字体
	private void setupFonts() {

		info_record_item_time_start.setTypeface(lcdTypeface);
		info_record_item_time_end.setTypeface(lcdTypeface);
		info_record_item_time_.setTypeface(lcdTypeface);
		info_record_item_oil_count.setTypeface(lcdTypeface);
		info_record_item_oil_pay.setTypeface(lcdTypeface);
		info_ssdata_record_mile_value.setTypeface(lcdTypeface);
		info_record_item_map_speed.setTypeface(lcdTypeface);
	}

	private void conn() {
		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();
		if (userinfo != null) {
			OBDHelper.getTravelInfo(userinfo.name, userinfo.pswd,
					mInfoTravleItem.thisStartTime, mInfoTravleItem.thisEndTime,
					new OnCallbackListener() {

						@Override
						public void onSuccess(Result arg0) {
							// TODO Auto-generated method stub
							mTravelInfo = (TravelInfo) arg0.object;

							mInfoTravleItem.setTravelInfo(mTravelInfo);

							setupValues();
						}

						@Override
						public void onFailure(Result arg0) {
							// TODO Auto-generated method stub
							mTravelInfo = null;
						}
					});
		}
	}

	private void setupValues() {
		info_recored_item_pos_start_.setText(mInfoTravleItem.getStartPos());
		info_recored_item_pos_end_.setText(mInfoTravleItem.getEndPos());

		info_record_item_time_month.setText(mInfoTravleItem.getMonth_());
		info_record_item_time_date.setText(mInfoTravleItem.getDate());
		info_record_item_time_start.setText(mInfoTravleItem.getsTime() + " ");
		info_record_item_time_end.setText(mInfoTravleItem.geteTime() + " ");
		info_record_item_time_.setText(mInfoTravleItem.getTavelTime() + " ");

		info_record_item_oil_count.setText(mInfoTravleItem.getOilCount() + " ");

		oilCount = stringToFloat(mInfoTravleItem.getOilCount());
		totalPay = String.format("%.2f", getOilPay() * oilCount);

		info_record_item_oil_pay.setText(totalPay + " ");

		info_ssdata_record_mile_value.setText(mInfoTravleItem.getMile() + " ");

		info_record_item_map_speed.setText(mInfoTravleItem.getSpeedMax() + " ");
	}

	/**
	 * 轨迹回放
	 */
	private void nav() {
		// System.out.println("播放转迹");
		GlobalApplication.getApplication().showRoutes(
				mInfoTravleItem.getThisStartTime(),
				mInfoTravleItem.getThisEndTime(), "单次行车轨迹");
	}

	private float getOilPay() {
		SharedPreferences preferences = getSharedPreferences(
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		nav();
	}
}
