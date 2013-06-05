package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.icalinks.common.JocParameter;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.InfoActivity;
import com.icalinks.mobile.widget.PopupWindowDialog;
import com.icalinks.obd.vo.FaultInfo;
import com.provider.common.config.MessageCenter;
import com.provider.model.Result;
import com.provider.model.resources.NetHelper;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;
import com.provider.net.listener.OnMessageListener;

/**
 * 汽车故障检查
 * 
 * @author zg_hu@calinks.com.cn
 * 
 *         update: lh_ji@calinks.com.cn version:2.0 update ui
 *         zg_hu@calinks.com.cn version:2.0 add 4s tel setting
 * 
 */

@SuppressLint("ValidFragment")
public class InfoStatusFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = InfoStatusFragment.class.getSimpleName();

	private InfoActivity mInfoActivity;
	private View mContentView;
//	private TextView mNookStatus;
	private Button mBtnIsokYuyue;
	private Button mBtnNookYuyue;
	private Button mBtnNookClear;
	private RelativeLayout mRltNook;
	private RelativeLayout mRltIsok;

	private ListView mLsvFaultCode;
	private String m4sCallNumber = "";
	private List<Map<String, String>> mFaultCodeList = new ArrayList<Map<String, String>>();
	private SimpleAdapter mSimpleAdapter;
	private List<FaultInfo> mArrayList = new ArrayList<FaultInfo>();

	public InfoStatusFragment(int resId) {
		super(resId);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInfoActivity = (InfoActivity) this.getActivitySafe();
		mContentView = inflater.inflate(R.layout.info_status, null);

//		mNookStatus = (TextView) mContentView
//				.findViewById(R.id.info_status_nook_status);
		mBtnIsokYuyue = (Button) mContentView
				.findViewById(R.id.info_status_isok_yuyue);
		mBtnNookYuyue = (Button) mContentView
				.findViewById(R.id.info_status_nook_yuyue);
		mBtnNookClear = (Button) mContentView
				.findViewById(R.id.info_status_nook_clear);
		mRltNook = (RelativeLayout) mContentView
				.findViewById(R.id.info_status_rlt_onok);
		mRltIsok = (RelativeLayout) mContentView
				.findViewById(R.id.info_status_rlt_isok);
		mLsvFaultCode = (ListView) mContentView
				.findViewById(R.id.info_status_lst_faultcode);

		mFaultCodeList = new ArrayList<Map<String, String>>();

		mBtnIsokYuyue.setOnClickListener(this);
		mBtnNookYuyue.setOnClickListener(this);
		mBtnNookClear.setOnClickListener(this);

		NetHelper.getInstance().register(MessageCenter.netId.GPS_CLEAR_FAULT,
				mOnMessageListener);
		return mContentView;
	}

	private OnMessageListener mOnMessageListener = new OnMessageListener() {

		@Override
		public void onReceived(Result result) {
			if (result.head.resCode == 0) {
				if (result.object != null) {
					boolean isSuc = (Boolean) result.object;
					if (isSuc) {

					} else {

					}

				}
			} else {
				// result.head.resMsg;
			}
		}
	};

	// 获取故障码
	public void requestFaultCodeList() {
		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();// 得到当前用户
		if (userinfo != null) {
			ActionBarHelper.startProgress();
			OBDHelper.getVehicleFaultCode(userinfo.name, userinfo.pswd, this);
		}
	}

	public void onResume() {
		super.onResume();
		// checkReqNetDatas();
		requestFaultCodeList();
	}

	@Override
	public void onPause() {
		super.onPause();
		ActionBarHelper.stopProgress();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NetHelper.getInstance().unregister(MessageCenter.netId.GPS_CLEAR_FAULT,
				mOnMessageListener);
	}

	// private UserInfo mUserInfo;

	// private void checkReqNetDatas() {
	//
	// UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();//
	// 得到当前用户
	//
	// if (userinfo != null) {
	// if (mUserInfo == null) {
	// mUserInfo = userinfo;
	// }
	//
	// if (!userinfo.equals(mUserInfo)) {
	// clear();
	// mUserInfo = userinfo;
	// }
	// ActionBarHelper.startProgress();
	// OBDHelper.getVehicleFaultCode(userinfo.name, userinfo.pswd, this);
	//
	// } else {
	// clear();
	// }
	// }

	// private void clear() {
	// mRltNook.setVisibility(View.GONE);
	// mRltIsok.setVisibility(View.VISIBLE);
	// }

	@Override
	protected void onHandlerFailure(Object obj) {
		ActionBarHelper.stopProgress();
//		mFaultCodeList.clear();
//		mSimpleAdapter.notifyDataSetChanged();
//		mRltNook.setVisibility(View.GONE);
//		mRltIsok.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onHandlerSuccess(Object obj) {
		ActionBarHelper.stopProgress();
		mArrayList = (ArrayList<FaultInfo>) obj;

		mFaultCodeList = new ArrayList<Map<String, String>>();
		mFaultCodeList.clear();
		if (mArrayList != null && mArrayList.size() > 0) {

			for (FaultInfo faultInfo : mArrayList) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("name", faultInfo.getCode());
				mFaultCodeList.add(map);
			}

			if (mArrayList.size() < 4) {
				int size = 4 - mArrayList.size();
				for (int i = 0; i < size; i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("name", "");
					mFaultCodeList.add(map);
				}
			}

			mSimpleAdapter = new SimpleAdapter(mInfoActivity, mFaultCodeList,
					R.layout.info_status_item, new String[] { "name" },
					new int[] { R.id.info_status_item_text });

			mLsvFaultCode.setAdapter(mSimpleAdapter);
			mRltNook.setVisibility(View.VISIBLE);
			mRltIsok.setVisibility(View.GONE);
		} else {
			mRltNook.setVisibility(View.GONE);
			mRltIsok.setVisibility(View.VISIBLE);
		}
	}

	// /**
	// * 更新数据
	// *
	// * @param obj
	// */
	// private void showData(Object obj) {
	// if (obj != null) {
	// List<FaultInfo> listFaultInfo = (List) obj;
	// if (listFaultInfo != null && listFaultInfo.size() > 0) {
	// mNookStatus.setText(R.string.info_status_yesdata);
	//
	// mRltNook.setVisibility(View.VISIBLE);
	// mRltIsok.setVisibility(View.GONE);
	// } else {
	// mRltNook.setVisibility(View.GONE);
	// mRltIsok.setVisibility(View.VISIBLE);
	// }
	// }
	// }

	private boolean exists4sCallNumber() {
		SharedPreferences preferences = mInfoActivity.getSharedPreferences(
				JocParameter.FILENAME_4S, Activity.MODE_PRIVATE);
		m4sCallNumber = preferences.getString(JocParameter.FORTH_S, "");
		if (m4sCallNumber.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	private Dialog m4sSettingDialog;
	private EditText mTxt4sNumber;

	private void show4sSettingDialog() {
		if (m4sSettingDialog == null) {
			m4sSettingDialog = new Dialog(mInfoActivity, R.style.myDialogTheme);
			LayoutInflater inflater = (LayoutInflater) mInfoActivity
					.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			View contentView = inflater.inflate(R.layout.dialog_4stel, null);
			mTxt4sNumber = (EditText) contentView
					.findViewById(R.id.dialog_4stel_number);
			contentView.findViewById(R.id.dialog_4stel_save)
					.setOnClickListener(this);
			contentView.findViewById(R.id.dialog_4stel_call)
					.setOnClickListener(this);
			m4sSettingDialog.setContentView(contentView);
		}
		m4sSettingDialog.show();
	}

	private void hide4sSettingDialog() {
		m4sSettingDialog.dismiss();
		m4sSettingDialog = null;
	}

	// 删除故障码
	private void clearFaultCode() {
		NetHelper.getInstance().sendCmdToServerGPS(
				MessageCenter.netId.GPS_CLEAR_FAULT);
		requestFaultCodeList();
	}

	private void save4sCallNumber() {
		m4sCallNumber = mTxt4sNumber.getText().toString().trim();
		if (!m4sCallNumber.equals("")) {
			SharedPreferences sharedPreferences = mInfoActivity
					.getSharedPreferences(JocParameter.FILENAME_4S,
							Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(JocParameter.FORTH_S, m4sCallNumber);
			editor.commit();

			// 隐藏对话框
			hide4sSettingDialog();
			// 打电话
			call4sNumber();
		}
	}

	private void call4sNumber() {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ m4sCallNumber));
		mInfoActivity.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info_status_isok_yuyue:
			if (exists4sCallNumber()) {
				call4sNumber();
			} else {
				show4sSettingDialog();
			}
			break;
		case R.id.info_status_nook_yuyue:
			if (exists4sCallNumber()) {
				call4sNumber();
			} else {
				show4sSettingDialog();
			}
			break;
		case R.id.info_status_nook_clear:
			clearFaultCode();
			break;

		case R.id.dialog_4stel_save:
			save4sCallNumber();
			break;
		case R.id.dialog_4stel_call:
			m4sCallNumber = mTxt4sNumber.getText().toString().trim();
			if (!m4sCallNumber.equals("")) {
				call4sNumber();
			}
			break;
		}
	}

}