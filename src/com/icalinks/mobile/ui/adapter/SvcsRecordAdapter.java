package com.icalinks.mobile.ui.adapter;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.db.dal.NaviInfo;
import com.icalinks.mobile.exception.JocParameter;

public class SvcsRecordAdapter extends BaseAdapter {
	private List<NaviInfo> mItemList;
	private LayoutInflater mInflater;

	// private Typeface mLcdTypeface;

	public SvcsRecordAdapter(Context context, List<NaviInfo> lstItem) {
		this.mItemList = lstItem;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		// mLcdTypeface= Typeface.createFromAsset(context.getAssets(),
		// "fonts/lcd.ttf");

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private Holder mHolder;
	private NaviInfo mNaviInfo;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new Holder();
			convertView = mInflater.inflate(R.layout.svcs_record_item, null);
			mHolder.rownum = (TextView) convertView
					.findViewById(R.id.svcs_record_item_rownum);
			mHolder.datetime = (TextView) convertView
					.findViewById(R.id.svcs_record_item_datetime);
			// mHolder.datetime.setTypeface(mLcdTypeface);

			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		mNaviInfo = mItemList.get(position);

		mHolder.rownum.setText("" + (position + 1));
		mHolder.datetime.setText(mNaviInfo.getEndAddrs());

		return convertView;
	}

	private static class Holder {
		TextView rownum;
		TextView datetime;
	}
}
