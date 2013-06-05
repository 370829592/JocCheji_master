package com.icalinks.mobile.ui.adapter;

import java.util.List;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.fragment.InfoInsureFragment;
import com.icalinks.obd.vo.InsureInfo;
/**
 * 保险列表适配器
 * @author Administrator
 *
 */
public class InfoInsureAdapter extends BaseAdapter {
	
	private Context mContext;
	private InfoInsureFragment mInfoInsureFragment;
	private LayoutInflater mInflater;
	private List<InsureInfo> mItemList;

	public InfoInsureAdapter(InfoInsureFragment fragment) {
		this.mInfoInsureFragment = fragment;
		this.mContext = fragment.getActivitySafe();
		this.mInflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setDataList(List<InsureInfo> dataList) {
		this.mItemList = dataList;
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
			convertView = mInflater.inflate(R.layout.info_insure_item, null);
			convertView.setTag(mHolder = new Holder());
			{
				mHolder.instype = (TextView) convertView.findViewById(R.id.info_insure_item_content);
				mHolder.policyd = (TextView) convertView.findViewById(R.id.info_insure_item_policyd);
				mHolder.insured = (TextView) convertView.findViewById(R.id.info_insure_item_insured);
				mHolder.company = (TextView) convertView.findViewById(R.id.info_insure_item_company);
				mHolder.duetime = (TextView) convertView.findViewById(R.id.info_insure_item_duetime);
				mHolder.caretel = (Button) convertView.findViewById(R.id.info_insure_item_caretel);			
			}
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		InsureInfo item = mItemList.get(position);
		{
			mHolder.instype.setText(item.getTypeName());
			mHolder.policyd.setText(item.getPolicyNo());
			mHolder.insured.setText(item.getOwner());
			mHolder.company.setText(item.getCompanyName());
			mHolder.duetime.setText("从" + item.getStartTime() + "起\n至" + item.getEndTime() + "止");
			mHolder.caretel.setText(item.getCompanyTell());
			
			mHolder.caretel.setOnClickListener(mInfoInsureFragment);
		}
		return convertView;
	}

	private static class Holder {
		TextView instype;
		TextView policyd;
		TextView insured;
		TextView company;
		TextView duetime;
		Button caretel;
	}
}
