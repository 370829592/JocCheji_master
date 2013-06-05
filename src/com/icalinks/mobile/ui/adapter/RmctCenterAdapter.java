package com.icalinks.mobile.ui.adapter;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.model.RmctCenterInfo;

public class RmctCenterAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;

	public RmctCenterAdapter(Context context, ArrayList<RmctCenterInfo> lstInfo) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

		mRmctCenterInfoList = lstInfo;
	}

	private ArrayList<RmctCenterInfo> mRmctCenterInfoList;

	public int getCount() {
		return mRmctCenterInfoList.size();
	}

	public Object getItem(int position) {
		return mRmctCenterInfoList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private Holder mHolder;

	private int[][] mBackgroundResources = {
			{ R.drawable.rmct_center_item_bg, R.drawable.rmct_center_item_bg_l },
			{ R.drawable.rmct_center_item_bg, R.drawable.rmct_center_item_bg_r } };

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.rmct_center_item, null);
			mHolder = new Holder();
			mHolder.icon = (ImageView) convertView
					.findViewById(R.id.rmct_center_item_icon);
			mHolder.name = (TextView) convertView
					.findViewById(R.id.rmct_center_item_name);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}

		RmctCenterInfo info = mRmctCenterInfoList.get(position);

		mHolder.name.setText(info.getNameResId());
		if (info.isEnabled()) {
			mHolder.icon.setImageResource(info.getIconResId());
			((LinearLayout) convertView)
					.setBackgroundResource(mBackgroundResources[position % 2][info
							.isActivated() ? 1 : 0]);
		} else {
			mHolder.icon.setImageResource(info.getIconNonId());
			((LinearLayout) convertView)
					.setBackgroundResource(mBackgroundResources[position % 2][0]);
		}
		return convertView;
	}

	private static class Holder {
		ImageView icon;
		TextView name;
	}
}
