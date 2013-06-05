package com.icalinks.mobile.ui.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.NaviDal;
import com.icalinks.mobile.db.dal.NaviInfo;
import com.icalinks.mobile.ui.NaviActivity;
import com.icalinks.mobile.ui.adapter.SvcsRecordAdapter;
import com.icalinks.mobile.util.ToastShow;
import com.provider.net.tcp.GpsWorker;

public class SvcsRecordFragment extends BaseFragment implements
		OnItemClickListener {

	public static final String TAG = SvcsRecordFragment.class.getSimpleName();

	public SvcsRecordFragment(int resId) {
		super(resId);
	}

	public SvcsRecordFragment(Activity activity, int resId) {
		super(resId);
		setActivity(activity);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.svcs_record, null);
		init(v);
		return v;
	}

	private TextView mMainTextView;
	private ListView mMainListView;
	private SvcsRecordAdapter mSvcsRecordAdapter;

	private void init(View v) {
		mMainTextView = (TextView) v.findViewById(R.id.svcs_record_item_none);
		mMainListView = (ListView) v.findViewById(R.id.svcs_record_item_list);

		mMainListView.setOnItemClickListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		check();
	}

	private void check() {
		List<NaviInfo> lstItem = NaviDal.getInstance(getActivitySafe()).select(
				null);

		if (lstItem != null && lstItem.size() > 0) {
			mMainListView.setVisibility(View.VISIBLE);
			mMainTextView.setVisibility(View.GONE);

			mSvcsRecordAdapter = new SvcsRecordAdapter(getActivitySafe(),
					lstItem);
			mMainListView.setAdapter(mSvcsRecordAdapter);
		} else {
			mMainTextView.setVisibility(View.VISIBLE);
			mMainListView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Location location = GlobalApplication.getLocation();
		if (location == null) {
			ToastShow.show(getActivitySafe(), "无法获取当前位置，请稍后再试！");
			return;
		}

		NaviInfo info = (NaviInfo) mSvcsRecordAdapter.getItem(position);
		NaviDal.getInstance(getActivitySafe()).update(info.get_id());// 更新最后时间，影响排序

		Intent intent = new Intent();
		{
			intent.setClass(getActivitySafe(), NaviActivity.class);
			String strData = String.format("false,%s,%s,%s;false,%s,%s,%s", 0,
					0, "X", info.getEndPoint().getLongitudeE6() / 1000000.0,
					info.getEndPoint().getLatitudeE6() / 1000000.0,
					info.getEndAddrs());
			intent.putExtra(NaviActivity.ARGS_NAVI_DATA_STRING, strData);
			intent.putExtra(NaviActivity.ARGS_IS_BAIDU_POINT, false);// 不是百度坐标
		}
		getActivitySafe().startActivity(intent);
	}
}
