package com.icalinks.mobile.ui.adapter;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.obd.vo.FeedbackInfo;
import com.icalinks.obd.vo.FeedbackReplyInfo;

public class FbReplyAdapter extends BaseAdapter {
	// public static final int MAX_COUNT = 10;
	private List<FeedbackReplyInfo> mItemList;

	private LayoutInflater mInflater;

	public FbReplyAdapter(Context context) {
		this.mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	public void setItemList(List<FeedbackReplyInfo> lstItem) {
		this.mItemList = lstItem;
	}

	public List<FeedbackReplyInfo> getItemList() {
		return this.mItemList;
	}

	@Override
	public int getCount() {
		return mItemList != null ? mItemList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mItemList != null ? mItemList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return mItemList != null ? position : position;
	}

	private Holder mHolder;
//	private int[] mBubbleBg = { R.drawable.umeng_fb_user_bubble,
//			R.drawable.umeng_fb_dev_bubble, };

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new Holder();
			convertView = mInflater.inflate(R.layout.fbreply_item, null);

			mHolder.atomt = (TextView) convertView
					.findViewById(R.id.umeng_fb_atomtxt);
			mHolder.state = (TextView) convertView
					.findViewById(R.id.umeng_fb_stateOrTime);

			mHolder.bubble = (RelativeLayout) convertView
					.findViewById(R.id.umeng_fb_bubble);

			mHolder.lview = convertView
					.findViewById(R.id.umeng_fb_atom_left_margin);
			mHolder.rview = convertView
					.findViewById(R.id.umeng_fb_atom_right_margin);

			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		FeedbackReplyInfo info = mItemList.get(position);
		mHolder.atomt.setText(info.getComment());
		mHolder.state.setText(info.getTime());
//		mHolder.bubble.setBackgroundResource(mBubbleBg[true ? 0 : 1]);
		if (true) {
			mHolder.lview.setVisibility(View.GONE);
			mHolder.rview.setVisibility(View.VISIBLE);
		} else {
			mHolder.lview.setVisibility(View.VISIBLE);
			mHolder.rview.setVisibility(View.GONE);
		}
		return convertView;
	}

	private static class Holder {
		TextView atomt;
		TextView state;
		RelativeLayout bubble;
		View lview;
		View rview;
	}
}
