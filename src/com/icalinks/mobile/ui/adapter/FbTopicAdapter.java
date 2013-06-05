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
import com.icalinks.obd.vo.FeedbackInfo;

public class FbTopicAdapter extends BaseAdapter {
	public static final int MAX_COUNT = 10;
	private List<FeedbackInfo> mItemList;

	private LayoutInflater mInflater;

	public FbTopicAdapter(Context context) {
		this.mInflater = (LayoutInflater) context
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	public void setItemList(List<FeedbackInfo> lstItem) {
		this.mItemList = lstItem;
	}

	@Override
	public int getCount() {
		return mItemList != null ? (mItemList.size() > MAX_COUNT ? MAX_COUNT
				: mItemList.size()) : 0;// 只显示最后10条
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			mHolder = new Holder();
			convertView = mInflater.inflate(
					R.layout.fbtopic_item, null);
			mHolder.topic = (TextView) convertView
					.findViewById(R.id.umeng_fb_feedbackpreview);
			mHolder.reply = (TextView) convertView
					.findViewById(R.id.umeng_fb_dev_reply);
			mHolder.ldate = (TextView) convertView
					.findViewById(R.id.umeng_fb_state_or_date);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		FeedbackInfo info = mItemList.get(position);
		mHolder.ldate.setText(info.getTime());
		mHolder.topic.setText(info.getComment());
		mHolder.reply.setText(info.isReply() ? "已回复" : "");
		return convertView;
	}

	private static class Holder {
		TextView topic;
		TextView reply;
		TextView ldate;
	}
}
