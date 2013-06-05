package com.icalinks.mobile.ui.adapter;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.model.MsgsItem;

public class MsgsAdapter extends BaseAdapter {
	private List<MsgsItem> mItemList;// = new ArrayList<MsgsItem>();

	public List<MsgsItem> getItemList() {
		return mItemList;
	}

	public void setItemList(List<MsgsItem> mItemList) {
		this.mItemList = mItemList;
	}

	private LayoutInflater mInflater;

	// private Typeface mLcdTypeface;

	public MsgsAdapter(Context context) {
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
	private MsgsItem mItemTmp;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new Holder();
			convertView = mInflater.inflate(R.layout.msgs_item, null);
			mHolder.icon = (ImageView) convertView
					.findViewById(R.id.msgs_item_icon);
			mHolder.main = (TextView) convertView
					.findViewById(R.id.msgs_item_main);
			mHolder.date = (TextView) convertView
					.findViewById(R.id.msgs_item_date);

			mHolder.left = convertView.findViewById(R.id.msgs_item_left);
			mHolder.right = convertView.findViewById(R.id.msgs_item_right);
			mHolder.bubble = (RelativeLayout) convertView
					.findViewById(R.id.msgs_item_bubble);

			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}

		mItemTmp = mItemList.get(position);

		mHolder.main.setText(mItemTmp.getContent());
		mHolder.date.setText(mItemTmp.getDatetime());
		switch (mItemTmp.getMsgsType()) {
		case MsgsItem.MSGS_TYPE_LOCATION:
			mHolder.icon.setVisibility(View.GONE);
			mHolder.left.setVisibility(View.GONE);
			mHolder.right.setVisibility(View.VISIBLE);
//			mHolder.bubble
//					.setBackgroundResource(R.drawable.umeng_fb_user_bubble);
			break;
		case MsgsItem.MSGS_TYPE_FB_RSP:
			mHolder.icon.setVisibility(View.GONE);
			mHolder.left.setVisibility(View.VISIBLE);
			mHolder.right.setVisibility(View.GONE);
//			mHolder.bubble
//					.setBackgroundResource(R.drawable.umeng_fb_dev_bubble);
			break;
		default:
			mHolder.icon.setVisibility(View.VISIBLE);
			mHolder.left.setVisibility(View.VISIBLE);
			mHolder.right.setVisibility(View.GONE);
//			mHolder.bubble
//					.setBackgroundResource(R.drawable.umeng_fb_dev_bubble);
		}

		return convertView;
	}

	private static class Holder {
		ImageView icon;
		TextView main;
		RelativeLayout bubble;
		TextView date;
		View left;
		View right;
	}
}
