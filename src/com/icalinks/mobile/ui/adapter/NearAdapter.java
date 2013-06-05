package com.icalinks.mobile.ui.adapter;

import java.text.DecimalFormat;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.model.NearItem;

public class NearAdapter extends BaseAdapter {
	private List<NearItem> mItemList;
	private LayoutInflater mInflater;

	// private Typeface mLcdTypeface;

	public NearAdapter(Context context, List<NearItem> lstItem) {
		this.mItemList = lstItem;
		this.mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
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
	private NearItem mNearItem;

	private DecimalFormat mDecimalFormat = new DecimalFormat(".0");

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new Holder();
			convertView = mInflater.inflate(R.layout.near_item, null);
			mHolder.rownum = (TextView) convertView
					.findViewById(R.id.near_item_rownum);
			mHolder.name = (TextView) convertView
					.findViewById(R.id.near_item_name);

			mHolder.address = (TextView) convertView
					.findViewById(R.id.near_item_address);
			mHolder.distance = (TextView) convertView
					.findViewById(R.id.near_item_distance);

			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		mNearItem = mItemList.get(position);

		mHolder.rownum.setText("" + (position + 1));
		mHolder.name.setText(mNearItem.getName());
		mHolder.address.setText(mNearItem.getAddress());

		if (mNearItem.getDistance() < 1000) {

			mHolder.distance.setText(mNearItem.getDistance() + "米");
		} else {
			mHolder.distance.setText(mDecimalFormat.format(mNearItem
					.getDistance()/1000.0) + "公里");
		}
		return convertView;
	}

	private static class Holder {
		TextView rownum;
		TextView name;
		TextView address;
		TextView distance;
	}
}
