package com.icalinks.mobile.ui.adapter;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.model.RmctRecordInfo;

public class RmctRecordAdapter extends BaseAdapter {
	private Context mContext;

	public RmctRecordAdapter(Context context) {
		mContext = context;
	}

	private List<RmctRecordInfo> mItemList;

	public void setItems(List<RmctRecordInfo> lstItem) {
		mItemList = lstItem;
	}

	
	public int getCount() {
		return mItemList.size();
	}

	
	public Object getItem(int position) {
		return mItemList.get(position);
	}

	
	public long getItemId(int position) {
		return position;
	}

	private Holder mHolder;

	
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.rmct_record_item, null);
			mHolder = new Holder();
			convertView.setTag(mHolder);
			mHolder.name = (TextView) convertView.findViewById(R.id.rmct_record_item_lbl_name);
			mHolder.time = (TextView) convertView.findViewById(R.id.rmct_record_item_lbl_time);
			mHolder.flag = (TextView) convertView.findViewById(R.id.rmct_record_item_lbl_flag);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		RmctRecordInfo item = mItemList.get(position);
		mHolder.name.setText(item.getName());
		mHolder.time.setText(item.getTime());
		mHolder.flag.setText(item.getFlag());
		return convertView;
	}

	private static class Holder {
		TextView name;
		TextView time;
		TextView flag;
	}
}
