package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.InfoRecordDetailActivity;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.ui.adapter.InfoTravleRecordAdapter;
import com.icalinks.mobile.ui.model.InfoTravleItem;
import com.icalinks.mobile.util.DateTime;
import com.icalinks.mobile.widget.PopupWindowDialog;
import com.icalinks.obd.vo.StatisticsInfo;
import com.provider.model.resources.OBDHelper;

/**
 * 单词行驶记录
 * 
 */

public class InfoSingleFragment extends BaseFragment {

	private static final String TAG = InfoSingleFragment.class.getSimpleName();

	private View mContentView;
	private GlobalApplication mApplication;
	private TextView info_ssdata_record_none;
	private ListView mRecordListView;
	private InfoTravleRecordAdapter mRecordAdapter;
	private TextView info_ssdata_record_date;
	private LinearLayout info_ssdata_record_calendar_layout;
	private TextView info_ssdata_record_calendar_month;
	private TextView info_ssdata_record_calendar_date;
	private String mTotalDate;
	private String year, month, month_, date;
	private DateTime mDateTime;
	private ArrayList<StatisticsInfo> mStatisticsInfoList = new ArrayList<StatisticsInfo>();
	private ArrayList<InfoTravleItem> infoTravelList;
	private Button info_ssdata_delect_btn;
	private DatePicker info_travel_record_date;
	private PopupWindowDialog mDialog;

	private AbsSubActivity mActivity;

	public InfoSingleFragment(int resId) {
		super(resId);
	}

	public InfoSingleFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbsSubActivity) getActivitySafe();
		mContentView = inflater.inflate(R.layout.info_ssdata_record, null);
		mApplication = GlobalApplication.getApplication();
		infoTravelList = new ArrayList<InfoTravleItem>();
		setupViews();
		return mContentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		setTitle(mActivity.getString(R.string.doc_dcxsjl));
		onEditAble(false);
		requestStatisticData();
		info_ssdata_delect_btn = GlobalApplication.getApplication()
				.getHomeActivity().showActionBarButton("编辑");

		info_ssdata_delect_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onEditAble(!mRecordAdapter.isKejian());
			}
		});
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.goback();
			}
		});

	}

	public void onEditAble(boolean isShowBtn) {
		if (isShowBtn) {
			info_ssdata_delect_btn.setText("取消");
		} else {
			info_ssdata_delect_btn.setText("编辑");
		}
		mRecordAdapter.setIsKejian(isShowBtn);
		mRecordAdapter.notifyDataSetChanged();
	}

	private void requestStatisticData() {
		UserInfo userinfo = mApplication.getCurrUser();// 得到当前用户
		if (userinfo != null) {
			ActionBarHelper.startProgress();
			OBDHelper.getStatisticsInfo(userinfo.name, userinfo.pswd,
					mTotalDate, this);
		} else {
			clear();
		}
	}

	// private void clear() {
	//
	// }

	@Override
	public void onPause() {
		super.onPause();
		ActionBarHelper.stopProgress();
		GlobalApplication.getApplication().getHomeActivity()
				.hideActionBarButton();
	}

	@Override
	protected void onHandlerSuccess(Object obj) {
		if (obj != null) {
			mStatisticsInfoList.clear();
			mStatisticsInfoList = (ArrayList<StatisticsInfo>) obj;

			int travelCount = mStatisticsInfoList.get(0).getDayTravelCount();

			if (travelCount == 0) {
				info_ssdata_record_none.setVisibility(View.VISIBLE);
				mRecordListView.setVisibility(View.GONE);

			} else {
				info_ssdata_record_none.setVisibility(View.GONE);
				info_ssdata_delect_btn.setVisibility(View.VISIBLE);
				mRecordListView.setVisibility(View.VISIBLE);

				changeInfoList(mStatisticsInfoList);
				mRecordAdapter.setInfoList(infoTravelList);
				mRecordListView.setAdapter(mRecordAdapter);
				mRecordListView.setOnItemClickListener(new ItemClickListener());
				// mRecordAdapter.notifyDataSetChanged();
			}
		}
		ActionBarHelper.stopProgress();
	}

	@Override
	protected void onHandlerFailure(Object obj) {
		super.onHandlerFailure(obj);
		ActionBarHelper.stopProgress();
		clear();
	}

	private void clear() {
		infoTravelList.clear();
		mRecordAdapter.setInfoList(infoTravelList);
		mRecordListView.setAdapter(mRecordAdapter);
		mRecordAdapter.notifyDataSetChanged();
	}

	private void changeInfoList(ArrayList<StatisticsInfo> infoList) {
		if (infoList.size() > 0) {
			int dayCount = infoList.get(0).getDayTravelCount();
			if (dayCount > 0) {

				infoTravelList.clear();

				for (int i = 0; i < infoList.size(); i++) {
					StatisticsInfo si = infoList.get(i);
					InfoTravleItem it = new InfoTravleItem(si, null);

					infoTravelList.add(it);
				}
			}
		}
	}

	private String getTotalDate() {
		return year.trim() + "-" + month.trim() + "-" + date.trim();
	}

	private void setupViews() {
		info_ssdata_record_date = (TextView) mContentView
				.findViewById(R.id.info_ssdata_record_date);
		info_ssdata_record_calendar_layout = (LinearLayout) mContentView
				.findViewById(R.id.info_ssdata_record_calendar_layout);
		info_ssdata_record_calendar_month = (TextView) mContentView
				.findViewById(R.id.info_ssdata_record_calendar_month);
		info_ssdata_record_calendar_date = (TextView) mContentView
				.findViewById(R.id.info_ssdata_record_calendar_date);
		info_ssdata_delect_btn = (Button) mContentView

		.findViewById(R.id.info_ssdata_delect_btn);

		info_ssdata_record_none = (TextView) mContentView
				.findViewById(R.id.info_ssdata_record_none);
		mRecordListView = (ListView) mContentView
				.findViewById(R.id.info_ssdata_record_listview);

		mDateTime = DateTime.now();
		year = mDateTime.getYear() + "";
		month = (mDateTime.getMonth() + 1) + "";
		month_ = converMonth(month);
		date = mDateTime.getDay() + "";

		mTotalDate = getTotalDate();

		info_ssdata_record_date.setText(mTotalDate + " ");

		info_ssdata_record_calendar_month.setText(month_);
		info_ssdata_record_calendar_date.setText(date);
		info_ssdata_record_calendar_layout
				.setOnClickListener(new ClickListener());

		mRecordAdapter = new InfoTravleRecordAdapter(mActivity);

		if (infoTravelList.size() == 0) {
			info_ssdata_record_none.setVisibility(View.VISIBLE);
			mRecordListView.setVisibility(View.GONE);

		} else if (infoTravelList.size() > 0) {
			info_ssdata_record_none.setVisibility(View.GONE);
			mRecordListView.setVisibility(View.VISIBLE);

			// mRecordAdapter.setInfoList(infoList);
			mRecordAdapter.setInfoList(infoTravelList);

			mRecordListView.setAdapter(mRecordAdapter);
			mRecordListView.setOnItemClickListener(new ItemClickListener());
		}

	}

	class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.info_ssdata_record_calendar_layout:
				showDataPicker();
				break;
			case R.id.info_record_picker_submit:
				saveSelectedDateTime();
				requestStatisticData();
			case R.id.info_ssdata_delect_btn:
				//
				// InfoTravleRecordAdapter.delect_per.setVisibility(View.VISIBLE);
				// System.out.println("	InfoTravleRecordAdapter.delect_per"+
				// InfoTravleRecordAdapter.delect_per);

			case R.id.info_record_picker_close:
			case R.id.info_record_picker_cancel:

				hidePickerDialog();
				break;

			default:
				break;
			}
		}

	}

	private void hidePickerDialog() {
		mDialog.destroy();
		mDialog = null;
	}

	private void saveSelectedDateTime() {
		mDateTime.setYear(info_travel_record_date.getYear());
		mDateTime.setMonth(info_travel_record_date.getMonth());
		mDateTime.setDay(info_travel_record_date.getDayOfMonth());

		mTotalDate = mDateTime.toString();

		year = info_travel_record_date.getYear() + "";
		month = (info_travel_record_date.getMonth() + 1) + "";
		date = info_travel_record_date.getDayOfMonth() + "";
		mTotalDate = getTotalDate();

		info_ssdata_record_date.setText(mTotalDate + " ");
		info_ssdata_record_calendar_month.setText(converMonth(month));
		info_ssdata_record_calendar_date.setText(date);
	}

	private String converMonth(String month) {
		String mon[] = { "0", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
				"十一", "十二" };

		int monInt = Integer.parseInt(month);

		return mon[monInt];
	}

	/**
	 * 更改时间
	 */
	private void showDataPicker() {
		if (mDialog == null) {
			mDialog = new PopupWindowDialog(mActivity);
			mDialog.setContentView(R.layout.info_record_travle_picker);
		}

		View view = mDialog.getContentView();
		{
			view.findViewById(R.id.info_record_picker_close)
					.setOnClickListener(new ClickListener());
			view.findViewById(R.id.info_record_picker_cancel)
					.setOnClickListener(new ClickListener());
			view.findViewById(R.id.info_record_picker_submit)
					.setOnClickListener(new ClickListener());
		}

		info_travel_record_date = (DatePicker) view
				.findViewById(R.id.info_record_picker_date);

		DateTime datetime = DateTime.from(mTotalDate, "yyyy-MM-dd");

		info_travel_record_date.init(datetime.getYear(), datetime.getMonth(),
				datetime.getDay(), null);// date.getDay()

		mDialog.show();
	}

	class ItemClickListener implements OnItemClickListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// Intent intent = new Intent();
			// intent.putExtra("info_num", position + 1);
			// // intent.putExtra("info_record", infoList.get(position));
			// intent.putExtra("info_record", infoTravelList.get(position));
			// intent.setClass(mInfoActivity, InfoRecordDetailActivity.class);
			// mInfoActivity.startActivity(intent);

			Intent intent = new Intent(mActivity,
					InfoRecordDetailActivity.class);
			intent.putExtra("info_num", position + 1);
			intent.putExtra("info_record", infoTravelList.get(position));
			setTitle(mTotalDate + " 第" + (position + 1) + "次行驶记录详情");
			showBackButton();
			hideRightButton();
			mActivity.startActivity(intent);
		}

	}

}
