package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.calinks.vehiclemachine.model.db.dal.DoctorDals;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.util.AnimationController;
import com.icalinks.obd.vo.ServiceInfo;
import com.provider.common.JocApplication;
import com.provider.common.util.ToolsUtil;

/**
 * 保养记录
 * 
 */

public class InfoRecordFragment extends BaseFragment implements
		View.OnClickListener {

	private View mContentView;
	private List<ServiceInfo> mServiceInfoList;

	private TextView mcontent, mtuition, mnumofkm, mdate;
	private Button mbtnNew, mbtnEdit, mbtnDelete;
	private View mbtnLayout;

	private LinearLayout recordLayout;
	private Button mbtnPre, mbtnNext;
	private TextView mpageView;

	private boolean showBtnLayout;

	private AbsSubActivity mActivity;

	private AnimationController animController;

	private int currentPage;

	public InfoRecordFragment(int resId) {
		super(resId);
	}

	public InfoRecordFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbsSubActivity) getActivitySafe();
		mContentView = inflater.inflate(R.layout.info_record_item, null);
		animController = new AnimationController();
		currentPage = 1;
		initView();
		return mContentView;
	}

	@Override
	public void onPause() {
		super.onPause();
		ActionBarHelper.stopProgress();
		GlobalApplication.getApplication().getHomeActivity()
				.hideActionBarButton();
	}

	public void onResume() {
		super.onResume();
		setTitle(mActivity.getString(R.string.doc_xfjl));
		initListener();
		updateRecordView();
	}

	private void initView() {
		mdate = (TextView) mContentView
				.findViewById(R.id.info_record_item_date);
		mcontent = (TextView) mContentView
				.findViewById(R.id.info_record_item_content);
		mtuition = (TextView) mContentView
				.findViewById(R.id.info_record_item_tuition);
		mnumofkm = (TextView) mContentView
				.findViewById(R.id.info_record_item_numofkm);
		mbtnNew = (Button) mContentView.findViewById(R.id.info_new);
		mbtnEdit = (Button) mContentView.findViewById(R.id.info_edit);
		mbtnDelete = (Button) mContentView.findViewById(R.id.info_delete);
		mbtnLayout = (View) mContentView
				.findViewById(R.id.info_record_item_btn_layout);
		mbtnLayout.setVisibility(View.INVISIBLE);
		showBtnLayout = false;

		recordLayout = (LinearLayout) mContentView
				.findViewById(R.id.record_main_layout);
		mbtnPre = (Button) mContentView.findViewById(R.id.record_pre_btn);
		mbtnNext = (Button) mContentView.findViewById(R.id.record_next_btn);
		mpageView = (TextView) mContentView
				.findViewById(R.id.record_page_textview);

	}

	private void updateRecordView() {
		requestRecordInfo();
		if (mServiceInfoList.size() > 0) {
			ServiceInfo info = mServiceInfoList.get(currentPage - 1);
			mdate.setText("          " + info.getServiceDate() + " 消费记录");
			String comStr = info.getTypeName();
			if (comStr != null && comStr.length() > 60) {
				comStr = comStr.substring(0, 60) + "......";
			}
			mcontent.setText(comStr);
			mtuition.setText(ToolsUtil.getData(info.getPrice()) + " 元");
			mnumofkm.setText(ToolsUtil.getData(info.getDistance()) + " 公里");
		} else {
			currentPage = 0;
		}
		mpageView.setText(currentPage + " / " + mServiceInfoList.size());
		updatePageView();
	}

	private void updatePageView() {
		if (currentPage <= 0) {
			mbtnPre.setBackgroundResource(R.drawable.pre_01);
			mbtnNext.setBackgroundResource(R.drawable.next_01);
		} else if (currentPage == 1) {
			if (mServiceInfoList.size() == 1) {
				mbtnPre.setBackgroundResource(R.drawable.pre_01);
				mbtnNext.setBackgroundResource(R.drawable.next_01);
			} else {
				mbtnPre.setBackgroundResource(R.drawable.pre_01);
				mbtnNext.setBackgroundResource(R.drawable.next_02);
			}
		} else if (currentPage == mServiceInfoList.size()) {
			mbtnPre.setBackgroundResource(R.drawable.pre_02);
			mbtnNext.setBackgroundResource(R.drawable.next_01);
		} else if (currentPage > 1 && currentPage < mServiceInfoList.size()) {
			mbtnPre.setBackgroundResource(R.drawable.pre_02);
			mbtnNext.setBackgroundResource(R.drawable.next_02);
		}

	}

	private void initialize() {
		if (currentPage == 0) {
			currentPage = 1;
		}
	}

	private void initListener() {
		showRightButton("编辑").setOnClickListener(this);
		mbtnNew.setOnClickListener(this);
		mbtnEdit.setOnClickListener(this);
		mbtnDelete.setOnClickListener(this);
		mbtnPre.setOnClickListener(this);
		mbtnNext.setOnClickListener(this);
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.goback();
			}
		});
	}

	private void requestRecordInfo() {
		mServiceInfoList = DoctorDals.getInstance(mActivity)
				.selectConsumRecord();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.info_new:
			ServiceInfo newInfo = new ServiceInfo();
			if (JocApplication.getVehicleInfo() != null) {
				newInfo.setVid(Integer.valueOf(JocApplication.getVehicleInfo()
						.getVid()));
			}
			edit(newInfo);
			initialize();

			break;

		case R.id.actionbar_btn_right:
			if (showBtnLayout) {
				showRightButton("编辑");
				mbtnLayout.setVisibility(View.GONE);
				showBtnLayout = false;
			} else {
				mbtnLayout.setVisibility(View.VISIBLE);
				showRightButton("取消");
				showBtnLayout = true;
			}
			break;

		case R.id.info_edit:
			if (currentPage > 0)
				edit(mServiceInfoList.get(currentPage - 1));
			break;
		case R.id.info_delete:
			if (currentPage > 0) {
				DoctorDals.getInstance(mActivity).deleteConsumRecord(
						mServiceInfoList.get(currentPage - 1).getVid());
				updateRecordView();
			}
			break;
		case R.id.record_pre_btn:
			if (currentPage > 1) {
				currentPage--;
				animController.pre(recordLayout, 300, 0);
				updateRecordView();
			}
			break;
		case R.id.record_next_btn:
			if (currentPage < mServiceInfoList.size()) {
				currentPage++;
				animController.next(recordLayout, 300, 0);
				updateRecordView();
			}
			break;

		}
	}

	private void edit(ServiceInfo serviceInfo) {
		Intent intent = new Intent();
		intent.setClass(this.getActivitySafe(),
				com.icalinks.mobile.ui.EditRecordActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ServiceInfo.class.getSimpleName(), serviceInfo);
		intent.putExtras(bundle);
		getActivitySafe().startActivity(intent);
	}
}
