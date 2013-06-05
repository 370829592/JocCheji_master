package com.icalinks.mobile.ui.adapter;

import java.util.ArrayList;
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
import com.icalinks.mobile.ui.MoreAboutActivity;
import com.icalinks.mobile.ui.MoreActMgrActivity;
import com.icalinks.mobile.ui.MoreFencesActivity;
import com.icalinks.mobile.ui.MoreFourSNumber;
import com.icalinks.mobile.ui.MoreReCtlPwdActivity;
import com.icalinks.mobile.ui.MoreScanActivity;
import com.icalinks.mobile.ui.MoreTelephoneActivity;
import com.icalinks.mobile.ui.MoreYoujiaSetting;
import com.icalinks.mobile.ui.MsgsActivity;
import com.icalinks.mobile.ui.WarmActivity;
import com.icalinks.mobile.ui.model.MoreItem;

public class MoreAdapter extends BaseAdapter {

	private static class Holder {
		ImageView icon;
		TextView text;
	}

	private Holder mHolder;
	private MoreItem mMoreItem;
	private Context mContext;
	private LayoutInflater mInflater;
	private List<MoreItem> mItemList;

	public MoreAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

		mItemList = new ArrayList<MoreItem>();
		{
			mItemList.add(new MoreItem(R.string.more_item_actmgr,
					R.drawable.more_item_actmgr, MoreActMgrActivity.class));
			mItemList.add(new MoreItem(R.string.more_item_youjia,
					R.drawable.more_item_youjia, MoreYoujiaSetting.class));
			mItemList.add(new MoreItem(R.string.more_item_rcpswd,
					R.drawable.more_item_rcpswd, MoreReCtlPwdActivity.class));
			mItemList.add(new MoreItem(R.string.more_item_msgs,
					R.drawable.more_item_msgs, MsgsActivity.class));
			mItemList.add(new MoreItem(R.string.more_item_fences,
					R.drawable.more_item_fences, MoreFencesActivity.class));
			mItemList.add(new MoreItem(R.string.more_item_warm,
					R.drawable.more_item_warm, WarmActivity.class));
			mItemList.add(new MoreItem(R.string.more_item_4stel,
					R.drawable.more_item_4stel, MoreFourSNumber.class));
			mItemList.add(new MoreItem(R.string.more_item_about,
					R.drawable.more_item_about, MoreAboutActivity.class));
			mItemList.add(new MoreItem(R.string.more_item_telnum,
					R.drawable.more_item_telnum, MoreTelephoneActivity.class));
		}
	}

	@Override
	public int getCount() {
		return mItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.more_center_item, null);
			mHolder = new Holder();
			{
				mHolder.icon = (ImageView) convertView
						.findViewById(R.id.more_item_icon);
				mHolder.text = (TextView) convertView
						.findViewById(R.id.more_item_text);
			}
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		mMoreItem = mItemList.get(position);
		{
			mHolder.icon.setBackgroundResource(mMoreItem.getIconResId());
			mHolder.text.setText(mMoreItem.getTextResId());
		}
		return convertView;
	}
}
