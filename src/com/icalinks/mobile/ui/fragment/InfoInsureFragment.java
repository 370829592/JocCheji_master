package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.EditInsureActivity;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.ui.adapter.InfoInsureAdapter;
import com.icalinks.obd.vo.InsureInfo;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 查询保险
 * 
 */

public class InfoInsureFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = InfoInsureFragment.class.getSimpleName();

	private AbsSubActivity mActivity;
	private View mContentView;
	private Button mActionBarButton;

	private ListView m_lst_main;
	private InfoInsureAdapter mInfoInsureAdapter;
	private Button telpButton;
	private List<InsureInfo> mInsureInfoList;

	public InfoInsureFragment(int resId) {
		super(resId);
	}
	public InfoInsureFragment(){}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity =  (AbsSubActivity) this.getActivitySafe();
		mContentView = inflater.inflate(R.layout.info_insure_item, null);
//		initView();
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
		setTitle(mActivity.getString(R.string.ms_cxbx));
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.goback();
			}
		});
		requestInsureInfo();
		mActionBarButton = GlobalApplication.getApplication().getHomeActivity()
				.showActionBarButton("编辑");
		mActionBarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				forwordEdit();
			}
		});
	}

	private void forwordEdit() {
		if (mInsureInfoList != null && mInsureInfoList.size() > 0) {
			Intent intent = new Intent();
			intent.setClass(mActivity, EditInsureActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(InsureInfo.class.getSimpleName(),
					mInsureInfoList.get(0));
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
		} else {
			UserInfo userinfo = GlobalApplication.getApplication()
					.getCurrUser();
			if (userinfo != null) {
				// showLoadingDialog();
				ActionBarHelper.startProgress();
				OBDHelper.getVehicleInsure(userinfo.name, userinfo.pswd,
						new OnCallbackListener() {
							@SuppressWarnings("unchecked")
							@Override
							public void onSuccess(Result result) {
								if (result.head.resCode == 0) {
									List<InsureInfo> infos = (List<InsureInfo>) result.object;
									if (infos != null && infos.size() > 0) {
										onHandlerSuccess(infos);

										Intent intent = new Intent();
										intent.setClass(mActivity,
												EditInsureActivity.class);
										Bundle bundle = new Bundle();
										bundle.putSerializable(InsureInfo.class
												.getSimpleName(), infos.get(0));
										intent.putExtras(bundle);
										mActivity.startActivity(intent);
									} else {
										/*
										 * VehicleInfo vehicleInfo =
										 * JocApplication.getVehicleInfo();
										 * if(vehicleInfo != null){ InsureInfo
										 * insureInfo = new InsureInfo();
										 * insureInfo
										 * .setVid(Integer.valueOf(vehicleInfo
										 * .getVid())); Intent intent = new
										 * Intent(); intent.setClass(mActivity,
										 * EditInsureActivity.class); Bundle
										 * bundle = new Bundle();
										 * bundle.putSerializable
										 * (InsureInfo.class .getSimpleName(),
										 * insureInfo);
										 * intent.putExtras(bundle);
										 * mActivity.startActivity(intent); }
										 */
										Toast.makeText(mActivity, "没有可供编辑的保险",
												Toast.LENGTH_SHORT).show();
									}
								}
								// disLoadingDialog();
								ActionBarHelper.stopProgress();
							}

							@Override
							public void onFailure(Result result) {
								// disLoadingDialog();
								ActionBarHelper.stopProgress();
								Toast.makeText(mActivity, result.head.resMsg,
										Toast.LENGTH_SHORT).show();
							}
						});
			}
		}

	}

	private void initView() {
		m_lst_main = (ListView) mContentView
				.findViewById(R.id.info_insure_lst_main);
		telpButton = (Button) mContentView.findViewById(R.id.insure_telnumber);

		mInsureInfoList = new ArrayList<InsureInfo>();
		mInfoInsureAdapter = new InfoInsureAdapter(this);
		mInfoInsureAdapter.setDataList(mInsureInfoList);
		m_lst_main.setAdapter(mInfoInsureAdapter);
	}

	private void requestInsureInfo() {
		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();
		if (userinfo != null) {
			ActionBarHelper.startProgress();
			OBDHelper.getVehicleInsure(userinfo.name, userinfo.pswd, this);
		} else {
//			clearList();
		}
	}

	private void clearList() {
		telpButton.setVisibility(View.GONE);
		m_lst_main.setVisibility(View.GONE);
		mInsureInfoList.clear();
		mInfoInsureAdapter.setDataList(mInsureInfoList);
		mInfoInsureAdapter.notifyDataSetChanged();
	}

	private void showList() {
		telpButton.setVisibility(View.VISIBLE);
		m_lst_main.setVisibility(View.VISIBLE);
		mInfoInsureAdapter.setDataList(mInsureInfoList);
		mInfoInsureAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onHandlerFailure(Object obj) {
		ActionBarHelper.stopProgress();
		super.onHandlerFailure(obj);
//		clearList();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandlerSuccess(Object obj) {
		ActionBarHelper.stopProgress();
		List<InsureInfo> lstData = null;
		if (obj != null) {
			lstData = (List<InsureInfo>) obj;
			if (lstData != null && lstData.size() > 0) {
				mInsureInfoList.clear();
				// mInsureInfoList = lstData;
				mInsureInfoList.add(lstData.get(0));
				showList();
			} else {
//				clearList();
			}
		} else {
//			clearList();
		}
	}

	public void onClick(View v) {
		Button btnTel = (Button) v;
		String strTel = btnTel.getText().toString().trim();
		if (strTel != "") {
			Uri uriTel = Uri.parse("tel:" + strTel);
			Intent intent = new Intent(Intent.ACTION_CALL, uriTel);
			mActivity.startActivity(intent);
		} else {
			return;
		}
	}
}
