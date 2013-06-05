package com.icalinks.mobile.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.ui.MoreActivity;
import com.icalinks.mobile.ui.MsgsActivity;
import com.icalinks.mobile.ui.adapter.MoreAdapter;
import com.icalinks.mobile.ui.model.MoreItem;
import com.icalinks.mobile.util.ToastShow;

@SuppressLint("ValidFragment")
public class MoreCenterFragment extends BaseFragment implements
		OnItemClickListener {
	@SuppressLint("ValidFragment")
	public MoreCenterFragment(int resId) {
		super(resId);
	}
	private View mContentView;

	private MoreActivity mMoreActivity;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMoreActivity = (MoreActivity) getActivitySafe();
		mContentView = inflater.inflate(R.layout.more_center, null);
		initView();
		return mContentView;
	}

	private GridView mGridView;
	private MoreAdapter mMoreAdapter;

	private void initView() {
		mGridView = (GridView) mContentView.findViewById(R.id.more_grid);
		mMoreAdapter = new MoreAdapter(mMoreActivity);
		mGridView.setAdapter(mMoreAdapter);
		mGridView.setOnItemClickListener(this);

	}

	private void showMsgsActivity() {
		UserInfo mUserInfo = GlobalApplication.getApplication().getCurrUser();
		if (mUserInfo != null) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(mMoreActivity, MsgsActivity.class);
			mMoreActivity.startActivity(intent);
		} else {
			ToastShow.show(mMoreActivity,
					R.string.toast_not_logged_operation_alert);
		}
	}


	private void showMoreActivity(Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(getActivitySafe(), cls);
		getActivitySafe().startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		MoreItem item = (MoreItem) mMoreAdapter.getItem(position);
		showMoreActivity(item.getCls());
	}
}
