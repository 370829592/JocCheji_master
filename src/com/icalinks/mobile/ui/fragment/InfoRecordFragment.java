package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.obd.vo.ServiceInfo;
import com.provider.common.JocApplication;
import com.provider.model.resources.OBDHelper;

/**
 * 保养记录
 * 
 */

public class InfoRecordFragment extends BaseFragment implements View.OnClickListener{


	private View mContentView;
//	private Button mActionBarButton;
//	private Button mBtnNewServiceInfo;
	private List<ServiceInfo> mServiceInfoList;

//	private ListView m_lst_main;
//	private InfoRecordAdapter mRecordAdapter;

	
	private TextView mcontent,mtuition,mnumofkm,mdate;
	private Button mbtnNew,mbtnEdit,mbtnDelete;
	private View mbtnLayout;
	
	private boolean showBtnLayout;
	
	private AbsSubActivity mActivity;;
	
	public InfoRecordFragment(int resId) {
		super(resId);
	}
	
	public InfoRecordFragment(){
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbsSubActivity) getActivitySafe();
		mContentView = inflater.inflate(R.layout.info_record_item, null);
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
		requestRecordInfo();
		initListener();
	}



	private void initView() {
		mdate = (TextView)mContentView.findViewById(R.id.info_record_item_date);
		mcontent = (TextView)mContentView.findViewById(R.id.info_record_item_content);
		mtuition = (TextView)mContentView.findViewById(R.id.info_record_item_tuition);
		mnumofkm = (TextView)mContentView.findViewById(R.id.info_record_item_numofkm);
		mbtnNew = (Button)mContentView.findViewById(R.id.info_new);
		mbtnEdit = (Button)mContentView.findViewById(R.id.info_edit);
		mbtnDelete = (Button)mContentView.findViewById(R.id.info_delete);
		mbtnLayout = (View) mContentView.findViewById(R.id.info_record_item_btn_layout);
		
		showBtnLayout = false;
		mServiceInfoList = new ArrayList<ServiceInfo>();
	}
	private void initListener(){
		showRightButton("编辑").setOnClickListener(this);
		mbtnNew.setOnClickListener(this);
		mbtnDelete.setOnClickListener(this);
		showBackButton().setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.goback();
			}
		})	;
	}


	private void requestRecordInfo() {
		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();

		if (userinfo != null) {
			ActionBarHelper.startProgress();
			OBDHelper.getVehicleService(userinfo.name, userinfo.pswd, this);
		} 
	}



	@Override
	protected void onHandlerFailure(Object obj) {
		ActionBarHelper.stopProgress();
		super.onHandlerFailure(obj);
	}

	@SuppressWarnings("unchecked")
	protected void onHandlerSuccess(Object obj) {
		ActionBarHelper.stopProgress();

		List<ServiceInfo> lstData = null;
		if (obj != null) {
			lstData = (List<ServiceInfo>) obj;
			if (lstData != null && lstData.size() > 0) {
				mServiceInfoList.clear();
				mServiceInfoList = lstData;
			} 
		} 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.info_new:
			ServiceInfo newInfo = new ServiceInfo();
			if (JocApplication.getVehicleInfo() != null) {
				newInfo.setVid(Integer.valueOf(JocApplication
						.getVehicleInfo().getVid()));
			}
			edit(newInfo);
			break;

		case R.id.actionbar_btn_right:
			if(showBtnLayout){
				showRightButton("编辑");
				mbtnLayout.setVisibility(View.GONE);
				showBtnLayout = false;
			}else{
				mbtnLayout.setVisibility(View.VISIBLE);
				showRightButton("取消");
				showBtnLayout = true;
			}
			break;
			
		case R.id.info_edit:
			edit(null);
			break;
		case R.id.info_delete:
			
			break;
		}
	}
	private void edit(ServiceInfo serviceInfo) {
		Intent intent = new Intent();
		intent.setClass(this.getActivitySafe(),com.icalinks.mobile.ui.EditRecordActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(ServiceInfo.class.getSimpleName(), serviceInfo);
		intent.putExtras(bundle);
		getActivitySafe().startActivity(intent);
	}
}
