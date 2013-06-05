package com.icalinks.mobile.ui.adapter;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.model.InfoSsdataItem;

public class InfoSsdataAdapter extends BaseAdapter {
	private Context mContext;

	public InfoSsdataAdapter(Context context) {
		mContext = context;
	}

	private List<InfoSsdataItem> mItemList;

	public void setItems(List<InfoSsdataItem> lstItem) {
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

	// private int[] mBgs = new int[] { R.drawable.list_item_odd_bg,
	// R.drawable.list_item_even_bg };

	// private int[] mBgs = new int[] { R.drawable.shen, R.drawable.qian };

	
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.info_ssdata_item, null);
			mHolder = new Holder();
			convertView.setTag(mHolder);
			mHolder.icon = (ImageView) convertView.findViewById(R.id.ssdata_item_img);
			mHolder.name = (TextView) convertView
					.findViewById(R.id.ssdata_item_lbl_name);
			mHolder.data = (TextView) convertView
					.findViewById(R.id.ssdata_item_lbl_data);
			mHolder.unit = (TextView) convertView
					.findViewById(R.id.ssdata_item_lbl_unit);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		// convertView.setBackgroundResource(mBgs[position % 2]);
		InfoSsdataItem item = mItemList.get(position);
		
		//分钟 转换成小时
		if(item.unit.equals("小时")) {
			item = reviseItem(item);
		}
		
		mHolder.name.setText(item.name);
		if((item.data).length() > 7) {
			mHolder.data.setText((item.data).subSequence(0, 7));
		} else {
			mHolder.data.setText((item.data));
		}
		
		if(item.unit.equals("L/100Km")) {
			mHolder.unit.setTextSize(12);
		}
		mHolder.unit.setText(item.unit);
		
		return convertView;
	}
	
	private InfoSsdataItem reviseItem(InfoSsdataItem item) {
		float data = Float.parseFloat(item.data)/60;
		item.data = String.format("%.2f", data);
		return item;
	}

	static class Holder {
		ImageView icon;
		TextView name;
		TextView data;
		TextView unit;
	}
}
 
