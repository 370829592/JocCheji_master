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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.MoreActMgrActivity;

public class MoreActMgrAdapter extends BaseAdapter implements
		View.OnClickListener {
	private MoreActMgrActivity mMoreActMgrActivity;
	private LayoutInflater mInflater;
	private List<UserInfo> mItemList = new ArrayList<UserInfo>();

	public void setItemList(List<UserInfo> lstUser) {
		this.mItemList = lstUser;
		this.notifyDataSetChanged();
	}

	private UserInfo mItem;
	private Holder mHolder;

	public MoreActMgrAdapter(MoreActMgrActivity moreactmgr) {
		mMoreActMgrActivity = moreactmgr;
		mInflater = (LayoutInflater) moreactmgr
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

	}

	private boolean mIsEdit;

	public boolean isEdit() {
		return mIsEdit;
	}

	public void setEdit(boolean isEdit) {
		this.mIsEdit = isEdit;
	}

	public int getCount() {
		return mItemList.size();
	}

	public Object getItem(int arg0) {
		return mItemList.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LinearLayout linearLayout = (LinearLayout) mInflater.inflate(
					R.layout.more_actmgr_item, null);

			mHolder = new Holder();
			mHolder.title = (TextView) linearLayout
					.findViewById(R.id.more_actmgr_item_title);

			mHolder.online = (ImageView) linearLayout
					.findViewById(R.id.more_actmgr_item_online);

			mHolder.delete = (ImageView) linearLayout
					.findViewById(R.id.more_actmgr_item_delete);
			mHolder.modify = (ImageView) linearLayout
					.findViewById(R.id.more_actmgr_item_modify);

			convertView = linearLayout;
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		mItem = mItemList.get(position);
		if (mItem.nick != null && !mItem.nick.trim().equals("")) {
			mHolder.title.setText(mItem.nick + "");
		} else {
			mHolder.title.setText(mItem.name + "");
		}
		try {
			if (GlobalApplication.isGpsLogin()
					&& mItem != null
					&& mItem.name.toUpperCase()
							.equals(GlobalApplication.getApplication()
									.getCurrUser().name.toUpperCase())) {
				mHolder.online
						.setBackgroundResource(R.drawable.account_online_o);
			} else {
				mHolder.online
						.setBackgroundResource(R.drawable.account_online_n);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (mIsEdit) {
			mHolder.modify.setFocusable(true);
			mHolder.modify.setOnClickListener(this);
			mHolder.modify.setTag("" + position);
			mHolder.modify.setVisibility(View.VISIBLE);
			mHolder.delete.setFocusable(true);
			mHolder.delete.setOnClickListener(this);
			mHolder.delete.setTag("" + position);
			mHolder.delete.setVisibility(View.VISIBLE);
		} else {
			mHolder.modify.setVisibility(View.GONE);
			mHolder.delete.setVisibility(View.GONE);
		}
		convertView.requestFocus();
		return convertView;
	}

	private static class Holder {
		TextView title;
		ImageView online;
		ImageView delete;
		ImageView modify;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_actmgr_item_delete:
			// 调用Activity删除方法
			mMoreActMgrActivity.delete(Integer.parseInt("" + v.getTag()));
			break;
		case R.id.more_actmgr_item_modify:// 调用Activity修改方法
			mMoreActMgrActivity.modify(Integer.parseInt("" + v.getTag()));
			break;
		}

	}
}
