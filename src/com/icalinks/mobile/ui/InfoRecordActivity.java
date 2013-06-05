//
////package com.icalinks.mobile.ui;
////
////import java.util.ArrayList;
////
////import android.content.Intent;
////import android.graphics.Typeface;
////import android.os.Bundle;
////import android.view.View;
////import android.view.View.OnClickListener;
////import android.widget.AdapterView;
////import android.widget.AdapterView.OnItemClickListener;
////import android.widget.Button;
////import android.widget.DatePicker;
////import android.widget.LinearLayout;
////import android.widget.ListView;
////import android.widget.TextView;
////
////import com.icalinks.jocyjt.R;
////import com.icalinks.mobile.GlobalApplication;
////import com.icalinks.mobile.db.dal.UserInfo;
////import com.icalinks.mobile.exception.JocParameter;
////import com.icalinks.mobile.recver.ActionBarHelper;
////import com.icalinks.mobile.ui.adapter.InfoTravleRecordAdapter;
////import com.icalinks.mobile.ui.model.InfoTravleItem;
////import com.icalinks.mobile.util.DateTime;
////import com.icalinks.mobile.widget.PopupWindowDialog;
////import com.icalinks.obd.vo.StatisticsInfo;
////import com.provider.model.Result;
////import com.provider.model.resources.OBDHelper;
////import com.provider.net.listener.OnCallbackListener;
////
/////**
//// * 
//// * 行驶记录列表
//// * 
//// * @author zg_hu@calinks.com.cn
//// * 
//// */
////
////public class InfoRecordActivity extends BaseActivity {
////	private GlobalApplication mApplication;
////	private Typeface lcdTypeface;
////
////	private TextView info_ssdata_record_none;
////	private ListView mRecordListView;
////	private InfoTravleRecordAdapter mRecordAdapter;
////
////	private TextView info_ssdata_record_date;
////
////	private LinearLayout info_ssdata_record_calendar_layout;
////	private TextView info_ssdata_record_calendar_month;
////	private TextView info_ssdata_record_calendar_date;
////
////	private String totalDate;
////
////	private String year, month, month_, date;
////
////	private DateTime mDatetime;
////
////	private ArrayList<StatisticsInfo> infoList;
////
////	private ArrayList<InfoTravleItem> infoTravelList;
////    private Button info_ssdata_delect_btn;
////	private boolean sdFlag = false;
////
////	@SuppressWarnings("unchecked")
////	@Override
////	public void onCreate(Bundle savedInstanceState) {
////		super.onCreate(savedInstanceState);
////		mApplication = GlobalApplication.getApplication();
////		sdFlag = GlobalApplication.sdFlag;
////		infoTravelList = new ArrayList<InfoTravleItem>();
////
////		infoList = (ArrayList<StatisticsInfo>) getIntent()
////				.getSerializableExtra("info_record_list");
////
////		changeInfoList(infoList);
////
////		setContentView(R.layout.info_ssdata_record);
////		setupViews();
////		setupFont();
////	}
////
////	private void changeInfoList(ArrayList<StatisticsInfo> infoList) {
////		// System.out.println("infoList.size = " + infoList.size());
////		if (infoList.size() > 0) {
////			int dayCount = infoList.get(0).getDayTravelCount();
////			if (dayCount > 0) {
////
////				infoTravelList.clear();
////
////				for (int i = 0; i < infoList.size(); i++) {
////					StatisticsInfo si = infoList.get(i);
////					InfoTravleItem it = new InfoTravleItem(si, null);
////
////					infoTravelList.add(it);
////				}
////			}
////		}
////	}
////
////	private String getTotalDate() {
////		return year.trim() + "-" + month.trim() + "-" + date.trim();
////	}
////
////	private void setupViews() {
////		// lcdTypeface = Typeface.createFromAsset(this.getAssets(),
////		// "fonts/lcd.TTF");
////		if (sdFlag) {
////			lcdTypeface = Typeface.createFromFile(JocParameter.LCD_FONTPATH);
////		}
////
////		info_ssdata_record_date = (TextView) findViewById(R.id.info_ssdata_record_date);
////
////		info_ssdata_record_calendar_layout = (LinearLayout) findViewById(R.id.info_ssdata_record_calendar_layout);
////		info_ssdata_record_calendar_month = (TextView) findViewById(R.id.info_ssdata_record_calendar_month);
////		info_ssdata_record_calendar_date = (TextView) findViewById(R.id.info_ssdata_record_calendar_date);
////		info_ssdata_delect_btn=(Button) findViewById(R.id.info_ssdata_delect_btn);
////		info_ssdata_record_none = (TextView) findViewById(R.id.info_ssdata_record_none);
////		mRecordListView = (ListView) findViewById(R.id.info_ssdata_record_listview);
////
////		mDatetime = DateTime.now();
////		year = mDatetime.getYear() + "";
////		month = (mDatetime.getMonth() + 1) + "";
////		month_ = converMonth(month);
////		date = mDatetime.getDay() + "";
////
////		totalDate = getTotalDate();
////
////		info_ssdata_record_date.setText(totalDate + " ");
////
////		info_ssdata_record_calendar_month.setText(month_);
////		info_ssdata_record_calendar_date.setText(date);
////		info_ssdata_record_calendar_layout
////				.setOnClickListener(new ClickListener());
////
////		mRecordAdapter = new InfoTravleRecordAdapter(this);
////
////		if (infoTravelList.size() == 0) {
////			info_ssdata_record_none.setVisibility(View.VISIBLE);
////			mRecordListView.setVisibility(View.GONE);
////
////		} else if (infoTravelList.size() > 0) {
////			info_ssdata_record_none.setVisibility(View.GONE);
////			mRecordListView.setVisibility(View.VISIBLE);
////
////			// mRecordAdapter.setInfoList(infoList);
////			mRecordAdapter.setInfoList(infoTravelList);
////
////			mRecordListView.setAdapter(mRecordAdapter);
////			mRecordListView.setOnItemClickListener(new ItemClickListener());
////		}
////
////	}
////
////	private void setupFont() {
////		if (sdFlag) {
////			info_ssdata_record_date.setTypeface(lcdTypeface);
////		}
////	}
////
////	class ClickListener implements OnClickListener {
////
////		@Override
////		public void onClick(View v) {
////			// TODO Auto-generated method stub
////			switch (v.getId()) {
////			case R.id.info_ssdata_record_calendar_layout:
////				updateDate();
////				break;
////			case R.id.info_record_picker_submit:
////				saveSelectedDateTime();
////				sendQueryRequest();
////			case R.id.info_record_picker_close:
////			case R.id.info_record_picker_cancel:
////				hidePickerDialog();
////				break;
////
////			default:
////				break;
////			}
////		}
////
////	}
////
////	private DatePicker info_travel_record_date;
////	private PopupWindowDialog mDialog;
////
////	private void hidePickerDialog() {
////		// sendQueryRequest();
////		mDialog.destroy();
////	}
////
////	private void saveSelectedDateTime() {
////		// mDateTime.setYear(info_travel_record_date.getYear());
////		// mDateTime.setMonth(info_travel_record_date.getMonth());
////		// mDateTime.setDay(info_travel_record_date.getDayOfMonth());
////		//
////		// totalDate = mDateTime.toString();
////
////		year = info_travel_record_date.getYear() + "";
////		month = (info_travel_record_date.getMonth() + 1) + "";
////		date = info_travel_record_date.getDayOfMonth() + "";
////		//
////		// System.out.println("year = " + year);
////		// System.out.println("month = " + month);
////		// System.out.println("date = " + date);
////
////		// totalDate = year.trim() + "-" + month.trim() + "-" + date.trim();
////		totalDate = getTotalDate();
////
////		// System.out.println("date = " + totalDate);
////	}
////
////	private void sendQueryRequest() {
////		// 查询车辆信息
////		UserInfo userinfo = mApplication.getCurrUser();// 得到当前用户
////		if (userinfo != null) {
////			OBDHelper.getStatisticsInfo(userinfo.name, userinfo.pswd,
////					totalDate, mDateOnCallbackListener);
////			ActionBarHelper.startProgress();
////		}
////
////	}
////
////	private OnCallbackListener mDateOnCallbackListener = new OnCallbackListener() {
////
////		@SuppressWarnings("unchecked")
////		public void onSuccess(Result result) {
////			ActionBarHelper.stopProgress();
////			infoList.clear();
////			infoList = (ArrayList<StatisticsInfo>) result.object;
////
////			info_ssdata_record_date.setText(totalDate + " ");
////			// System.out.println("infoList.size = " + infoList.size());
////
////			// System.out.println("totalDate = " + totalDate);
////
////			info_ssdata_record_calendar_month.setText(converMonth(month));
////			info_ssdata_record_calendar_date.setText(date);
////
////			int travelCount = infoList.get(0).getDayTravelCount();
////
////			// System.out.println("travelCount = " + travelCount);
////
////			if (travelCount == 0) {
////				info_ssdata_record_none.setVisibility(View.VISIBLE);
////				mRecordListView.setVisibility(View.GONE);
////				
////			} else {
////				info_ssdata_record_none.setVisibility(View.GONE);
////				info_ssdata_delect_btn.setVisibility(View.VISIBLE);
////				mRecordListView.setVisibility(View.VISIBLE);
////
////				changeInfoList(infoList);
////
////				// info_ssdata_record_date.setText(totalDate + " ");
////				//
////				// info_ssdata_record_calendar_month.setText(infoTravelList.get(0)
////				// .getMonth_());
////				// info_ssdata_record_calendar_date.setText(infoTravelList.get(0)
////				// .getDate());
////				// System.out.println("infoTravelList.size = "
////				// + infoTravelList.size());
////
////				mRecordAdapter.setInfoList(infoTravelList);
////
////				mRecordAdapter.notifyDataSetChanged();
////				mRecordListView.setAdapter(mRecordAdapter);
////				mRecordListView.setOnItemClickListener(new ItemClickListener());
////			}
////		}
////
////		public void onFailure(Result result) {
////			ActionBarHelper.stopProgress();
////
////			System.out.println("onFailure" + result.head.resMsg);
////		}
////
////	};
////
////	private String converMonth(String month) {
////		String mon[] = { "0", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
////				"十一", "十二" };
////
////		int monInt = Integer.parseInt(month);
////
////		return mon[monInt];
////	}
////
////	/**
////	 * 更改时间
////	 */
////	private void updateDate() {
////		if (mDialog == null) {
////			mDialog = new PopupWindowDialog(this);
////			mDialog.setContentView(R.layout.info_record_travle_picker);
////		}
////
////		View view = mDialog.getContentView();
////		{
////			view.findViewById(R.id.info_record_picker_close)
////					.setOnClickListener(new ClickListener());
////			view.findViewById(R.id.info_record_picker_cancel)
////					.setOnClickListener(new ClickListener());
////			view.findViewById(R.id.info_record_picker_submit)
////					.setOnClickListener(new ClickListener());
////		}
////
////		info_travel_record_date = (DatePicker) view
////				.findViewById(R.id.info_record_picker_date);
////
////		// System.out.println("date = " + totalDate);
////
////		DateTime datetime = DateTime.from(totalDate, "yyyy-MM-dd");
////
////		info_travel_record_date.init(datetime.getYear(), datetime.getMonth(),
////				datetime.getDay(), null);// date.getDay()
////
////		// System.out.println("年为 = " + info_travel_record_date.getYear());
////
////		mDialog.show();
////	}
////
////	class ItemClickListener implements OnItemClickListener {
////
////		@Override
////		public void onItemClick(AdapterView<?> parent, View view, int position,
////				long id) {
////			// TODO Auto-generated method stub
////			// System.out.println("pos = " + position);
////
////			Intent intent = new Intent();
////
////			intent.putExtra("info_num", position + 1);
////			// intent.putExtra("info_record", infoList.get(position));
////			intent.putExtra("info_record", infoTravelList.get(position));
////
////			intent.setClass(InfoRecordActivity.this,
////					InfoRecordDetailActivity.class);
////
////			skip(intent);
////		}
////
////	}
////
////	private void skip(Intent intent) {
////		// this.overridePendingTransition(R.anim.slide_left,
////		// R.anim.slide_right);
////		startActivity(intent);
////
////	}
////}
//=======
//package com.icalinks.mobile.ui;
//
//import java.util.ArrayList;
//
//import android.content.Intent;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.icalinks.jocyjt.R;
//import com.icalinks.mobile.GlobalApplication;
//import com.icalinks.mobile.db.dal.UserInfo;
//import com.icalinks.mobile.exception.JocParameter;
//import com.icalinks.mobile.recver.ActionBarHelper;
//import com.icalinks.mobile.ui.adapter.InfoTravleRecordAdapter;
//import com.icalinks.mobile.ui.model.InfoTravleItem;
//import com.icalinks.mobile.util.DateTime;
//import com.icalinks.mobile.widget.PopupWindowDialog;
//import com.icalinks.obd.vo.StatisticsInfo;
//import com.provider.model.Result;
//import com.provider.model.resources.OBDHelper;
//import com.provider.net.listener.OnCallbackListener;
//
///**
// * 
// * 行驶记录列表
// * 
// * @author zg_hu@calinks.com.cn
// * 
// */
//
//public class InfoRecordActivity extends BaseActivity {
//	private GlobalApplication mApplication;
//	private Typeface lcdTypeface;
//
//	private TextView info_ssdata_record_none;
//	private ListView mRecordListView;
//	private InfoTravleRecordAdapter mRecordAdapter;
//
//	private TextView info_ssdata_record_date;
//
//	private LinearLayout info_ssdata_record_calendar_layout;
//	private TextView info_ssdata_record_calendar_month;
//	private TextView info_ssdata_record_calendar_date;
//
//	private String totalDate;
//
//	private String year, month, month_, date;
//
//	private DateTime mDatetime;
//
//	private ArrayList<StatisticsInfo> infoList;
//
//	private ArrayList<InfoTravleItem> infoTravelList;
//    private Button info_ssdata_delect_btn;
//	private boolean sdFlag = false;
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mApplication = GlobalApplication.getApplication();
//		sdFlag = GlobalApplication.sdFlag;
//		infoTravelList = new ArrayList<InfoTravleItem>();
//
//		infoList = (ArrayList<StatisticsInfo>) getIntent()
//				.getSerializableExtra("info_record_list");
//
//		changeInfoList(infoList);
//
//		setContentView(R.layout.info_ssdata_record);
//		setupViews();
//		setupFont();
//	}
//
//	private void changeInfoList(ArrayList<StatisticsInfo> infoList) {
//		// System.out.println("infoList.size = " + infoList.size());
//		if (infoList.size() > 0) {
//			int dayCount = infoList.get(0).getDayTravelCount();
//			if (dayCount > 0) {
//
//				infoTravelList.clear();
//
//				for (int i = 0; i < infoList.size(); i++) {
//					StatisticsInfo si = infoList.get(i);
//					InfoTravleItem it = new InfoTravleItem(si, null);
//					
//					infoTravelList.add(it);
//				}
//			}
//		}
//	}
//
//	private String getTotalDate() {
//		return year.trim() + "-" + month.trim() + "-" + date.trim();
//	}
//
//	private void setupViews() {
//		// lcdTypeface = Typeface.createFromAsset(this.getAssets(),
//		// "fonts/lcd.TTF");
//		if (sdFlag) {
//			lcdTypeface = Typeface.createFromFile(JocParameter.LCD_FONTPATH);
//		}
//
//		info_ssdata_record_date = (TextView) findViewById(R.id.info_ssdata_record_date);
//
//		info_ssdata_record_calendar_layout = (LinearLayout) findViewById(R.id.info_ssdata_record_calendar_layout);
//		info_ssdata_record_calendar_month = (TextView) findViewById(R.id.info_ssdata_record_calendar_month);
//		info_ssdata_record_calendar_date = (TextView) findViewById(R.id.info_ssdata_record_calendar_date);
//		info_ssdata_delect_btn=(Button) findViewById(R.id.info_ssdata_delect_btn);
//		info_ssdata_record_none = (TextView) findViewById(R.id.info_ssdata_record_none);
//		mRecordListView = (ListView) findViewById(R.id.info_ssdata_record_listview);
//
//		mDatetime = DateTime.now();
//		year = mDatetime.getYear() + "";
//		month = (mDatetime.getMonth() + 1) + "";
//		month_ = converMonth(month);
//		date = mDatetime.getDay() + "";
//
//		totalDate = getTotalDate();
//
//		info_ssdata_record_date.setText(totalDate + " ");
//
//		info_ssdata_record_calendar_month.setText(month_);
//		info_ssdata_record_calendar_date.setText(date);
//		info_ssdata_record_calendar_layout
//				.setOnClickListener(new ClickListener());
//
//		mRecordAdapter = new InfoTravleRecordAdapter(this);
//
//		if (infoTravelList.size() == 0) {
//			info_ssdata_record_none.setVisibility(View.VISIBLE);
//			mRecordListView.setVisibility(View.GONE);
//
//		} else if (infoTravelList.size() > 0) {
//			info_ssdata_record_none.setVisibility(View.GONE);
//			mRecordListView.setVisibility(View.VISIBLE);
//
//			// mRecordAdapter.setInfoList(infoList);
//			mRecordAdapter.setInfoList(infoTravelList);
//
//			mRecordListView.setAdapter(mRecordAdapter);
//			mRecordListView.setOnItemClickListener(new ItemClickListener());
//		}
//
//	}
//
//	private void setupFont() {
//		if (sdFlag) {
//			info_ssdata_record_date.setTypeface(lcdTypeface);
//		}
//	}
//
//	class ClickListener implements OnClickListener {
//
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			switch (v.getId()) {
//			case R.id.info_ssdata_record_calendar_layout:
//				updateDate();
//				break;
//			case R.id.info_record_picker_submit:
//				saveSelectedDateTime();
//				sendQueryRequest();
//			case R.id.info_record_picker_close:
//			case R.id.info_record_picker_cancel:
//				hidePickerDialog();
//				break;
//
//			default:
//				break;
//			}
//		}
//
//	}
//
//	private DatePicker info_travel_record_date;
//	private PopupWindowDialog mDialog;
//
//	private void hidePickerDialog() {
//		// sendQueryRequest();
//		mDialog.destroy();
//	}
//
//	private void saveSelectedDateTime() {
//		// mDateTime.setYear(info_travel_record_date.getYear());
//		// mDateTime.setMonth(info_travel_record_date.getMonth());
//		// mDateTime.setDay(info_travel_record_date.getDayOfMonth());
//		//
//		// totalDate = mDateTime.toString();
//
//		year = info_travel_record_date.getYear() + "";
//		month = (info_travel_record_date.getMonth() + 1) + "";
//		date = info_travel_record_date.getDayOfMonth() + "";
//		//
//		// System.out.println("year = " + year);
//		// System.out.println("month = " + month);
//		// System.out.println("date = " + date);
//
//		// totalDate = year.trim() + "-" + month.trim() + "-" + date.trim();
//		totalDate = getTotalDate();
//
//		// System.out.println("date = " + totalDate);
//	}
//
//	private void sendQueryRequest() {
//		// 查询车辆信息
//		UserInfo userinfo = mApplication.getCurrUser();// 得到当前用户
//		if (userinfo != null) {
//			OBDHelper.getStatisticsInfo(userinfo.name, userinfo.pswd,
//					totalDate, mDateOnCallbackListener);
//			ActionBarHelper.startProgress();
//		}
//
//	}
//
//	private OnCallbackListener mDateOnCallbackListener = new OnCallbackListener() {
//
//		@SuppressWarnings("unchecked")
//		public void onSuccess(Result result) {
//			ActionBarHelper.stopProgress();
//			infoList.clear();
//			infoList = (ArrayList<StatisticsInfo>) result.object;
//
//			info_ssdata_record_date.setText(totalDate + " ");
//			// System.out.println("infoList.size = " + infoList.size());
//
//			// System.out.println("totalDate = " + totalDate);
//
//			info_ssdata_record_calendar_month.setText(converMonth(month));
//			info_ssdata_record_calendar_date.setText(date);
//
//			int travelCount = infoList.get(0).getDayTravelCount();
//
//			// System.out.println("travelCount = " + travelCount);
//
//			if (travelCount == 0) {
//				info_ssdata_record_none.setVisibility(View.VISIBLE);
//				mRecordListView.setVisibility(View.GONE);
//				
//			} else {
//				info_ssdata_record_none.setVisibility(View.GONE);
//				info_ssdata_delect_btn.setVisibility(View.VISIBLE);
//				mRecordListView.setVisibility(View.VISIBLE);
//
//				changeInfoList(infoList);
//
//				// info_ssdata_record_date.setText(totalDate + " ");
//				//
//				// info_ssdata_record_calendar_month.setText(infoTravelList.get(0)
//				// .getMonth_());
//				// info_ssdata_record_calendar_date.setText(infoTravelList.get(0)
//				// .getDate());
//				// System.out.println("infoTravelList.size = "
//				// + infoTravelList.size());
//
//				mRecordAdapter.setInfoList(infoTravelList);
//
//				mRecordAdapter.notifyDataSetChanged();
//				mRecordListView.setAdapter(mRecordAdapter);
//				mRecordListView.setOnItemClickListener(new ItemClickListener());
//			}
//		}
//
//		public void onFailure(Result result) {
//			ActionBarHelper.stopProgress();
//
//			System.out.println("onFailure" + result.head.resMsg);
//		}
//
//	};
//
//	private String converMonth(String month) {
//		String mon[] = { "0", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
//				"十一", "十二" };
//
//		int monInt = Integer.parseInt(month);
//
//		return mon[monInt];
//	}
//
//	/**
//	 * 更改时间
//	 */
//	private void updateDate() {
//		if (mDialog == null) {
//			mDialog = new PopupWindowDialog(this);
//			mDialog.setContentView(R.layout.info_record_travle_picker);
//		}
//
//		View view = mDialog.getContentView();
//		{
//			view.findViewById(R.id.info_record_picker_close)
//					.setOnClickListener(new ClickListener());
//			view.findViewById(R.id.info_record_picker_cancel)
//					.setOnClickListener(new ClickListener());
//			view.findViewById(R.id.info_record_picker_submit)
//					.setOnClickListener(new ClickListener());
//		}
//
//		info_travel_record_date = (DatePicker) view
//				.findViewById(R.id.info_record_picker_date);
//
//		// System.out.println("date = " + totalDate);
//
//		DateTime datetime = DateTime.from(totalDate, "yyyy-MM-dd");
//
//		info_travel_record_date.init(datetime.getYear(), datetime.getMonth(),
//				datetime.getDay(), null);// date.getDay()
//
//		// System.out.println("年为 = " + info_travel_record_date.getYear());
//
//		mDialog.show();
//	}
//
//	class ItemClickListener implements OnItemClickListener {
//
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position,
//				long id) {
//			// TODO Auto-generated method stub
//			// System.out.println("pos = " + position);
//
//			Intent intent = new Intent();
//
//			intent.putExtra("info_num", position + 1);
//			// intent.putExtra("info_record", infoList.get(position));
//			intent.putExtra("info_record", infoTravelList.get(position));
//
//			intent.setClass(InfoRecordActivity.this,
//					InfoRecordDetailActivity.class);
//
//			skip(intent);
//		}
//
//	}
//
//	private void skip(Intent intent) {
//		// this.overridePendingTransition(R.anim.slide_left,
//		// R.anim.slide_right);
//		startActivity(intent);
//
//	}
//}
