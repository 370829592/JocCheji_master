package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.MoreFencesActivity;
import com.icalinks.mobile.ui.RmctActivity;
import com.icalinks.mobile.ui.adapter.RmctCenterAdapter;
import com.icalinks.mobile.ui.adapter.RmctCenterHelper;
import com.icalinks.mobile.ui.model.RmctCenterInfo;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.mobile.widget.PopupWindowDialog;
import com.icalinks.obd.vo.ControlInfo;
import com.provider.common.config.MessageCenter;
import com.provider.model.Result;
import com.provider.model.resources.NetHelper;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnMessageListener;
/**
 * 远程控制
 * @author Administrator
 *
 */

public class RmctCenterFragment extends BaseFragment implements
		View.OnClickListener, OnMessageListener,
		AdapterView.OnItemClickListener {

	public RmctCenterFragment(int resId) {
		super(resId);
	}

	private RmctActivity mRmctActivity;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContentView = inflater.inflate(R.layout.rmct_center, null);
		mRmctActivity = (RmctActivity) getActivitySafe();
		return mContentView;
	}

	private GlobalApplication mGlobalApplication;
	private View mContentView;
	private RmctCenterAdapter mRmctCenterAdapter;
	private RmctCenterHelper mRmctCenterHelper;
	private ArrayList<RmctCenterInfo> mRmctCenterInfoList;
	private GridView mGrdMain;

	private void checkInit() {
		if (mGrdMain == null) {
			mGrdMain = (GridView) mContentView
					.findViewById(R.id.rmct_center_grd_main);
			mRmctCenterHelper = new RmctCenterHelper(getActivitySafe());
			mRmctCenterAdapter = new RmctCenterAdapter(getActivitySafe(),
					mRmctCenterInfoList = mRmctCenterHelper.getList());
			mGrdMain.setAdapter(mRmctCenterAdapter);
			mGrdMain.setOnItemClickListener(this);
			mGlobalApplication = GlobalApplication.getApplication();

			// 注册回调
			NetHelper.getInstance().register(
					MessageCenter.netId.GPS_CTL_CENTER, this);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:
			hideVerifyDialog();
			break;
		case 1:
			showVerifyDialog();
			break;
		}
	}

	private UserInfo mCurrUserInfo;

	public void onResume() {
		checkInit();

		// // 用户不同时，清空数据
		// if (mCurrUserInfo == null) {
		// mCurrUserInfo = mGlobalApplication.getCurrUser();
		// if (mCurrUserInfo != null) {
		// mRmctCenterHelper.setCurrUserName(mCurrUserInfo.name);
		// mRmctCenterAdapter.notifyDataSetChanged();
		// requestControlInfo(mCurrUserInfo.name);
		// }
		// } else if (!mCurrUserInfo.equals(mGlobalApplication.getCurrUser())) {
		// mCurrUserInfo = mGlobalApplication.getCurrUser();
		// if (mCurrUserInfo != null) {
		// mRmctCenterHelper.setCurrUserName(mCurrUserInfo.name);
		// mRmctCenterAdapter.notifyDataSetChanged();
		// requestControlInfo(mCurrUserInfo.name);
		// } else {
		// mRmctCenterHelper.setCurrUserName(null);
		// }
		// mRmctCenterAdapter.notifyDataSetChanged();
		// }

		mCurrUserInfo = mGlobalApplication.getCurrUser();
		if (mCurrUserInfo != null) {
			mRmctCenterHelper.setCurrUserName(mCurrUserInfo.name);
			mRmctCenterAdapter.notifyDataSetChanged();
			try {
				requestControlInfo(Integer.valueOf(mCurrUserInfo.vid));
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else {// 用户不同时，清空数据
			mRmctCenterHelper.setCurrUserName(null);
			mRmctCenterAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		ActionBarHelper.stopProgress();
	}

	private void requestControlInfo(int vid) {
		ActionBarHelper.startProgress();
		mIsControlRequest = true;
		OBDHelper.getControlInfo("" + vid, this);
	}

	// /** 命令 */
	// private int cmd;
	//
	// /** 名称 */
	// private String name;
	//
	// /** 是否开通 */
	// private boolean isOpen;
	@Override
	protected void onHandlerFailure(Object obj) {
		super.onHandlerFailure(obj);
		ActionBarHelper.stopProgress();
	}

	private boolean mIsControlRequest;

	@Override
	protected void onHandlerSuccess(Object obj) {
		if (obj != null) {
			if (mIsControlRequest) {
				List<ControlInfo> lstCtl = (List<ControlInfo>) obj;
				int rmctSize = lstCtl.size();
				int viewSize = mRmctCenterInfoList.size();
				RmctCenterInfo rmctCenterInfo = null;
				ControlInfo controlInfo = null;
				for (int i = 0; i < viewSize; i++) {
					rmctCenterInfo = mRmctCenterInfoList.get(i);
					for (int j = 0; j < rmctSize; j++) {
						controlInfo = lstCtl.get(j);
						if (rmctCenterInfo.getRmctCmdId() == controlInfo
								.getCmd()) {
							rmctCenterInfo.setEnabled(controlInfo.isOpen());
						}
					}
				}
				mRmctCenterAdapter.notifyDataSetChanged();
				ActionBarHelper.stopProgress();
			} else {
				sendRmctCommand();
			}
		}
	}

	// protected void onHandlerFailure(Object obj) {
	// super.onHandlerFailure(obj);
	// ActionBarHelper.stopProgress();
	// }
	//
	// protected void onHandlerSuccess(Object obj) {
	// ActionBarHelper.stopProgress();
	//
	// // mRmctCenterData.changeButtonStatus(mCurrRmctCenterInfo);
	// // mRmctCenterAdapter.notifyDataSetChanged();
	//
	// ToastShow.show(mContext, R.string.rmct_center_toast_rmctok);
	// }

	private PopupWindowDialog mDialog;
	private RmctCenterInfo mCurrRmctInfo;

	private void hideVerifyDialog() {
		mTxtPwdViewListIndex = -1;
		for (int i = 0; i < mTxtPwdViewList.size(); i++) {
			mTxtPwdViewList.get(i).setText("");
			mTxtPwdViewList.get(i).setHint("");
		}
		mDialog.destroy();
		mDialog = null;
	}

	private List<TextView> mTxtPwdViewList = new ArrayList<TextView>();
	private int mTxtPwdViewListIndex = -1;

	private void addPwdKey(String key) {
		if (mTxtPwdViewListIndex + 1 < mTxtPwdViewList.size()) {
			mTxtPwdViewListIndex++;

			mTxtPwdViewList.get(mTxtPwdViewListIndex).setHint(key);
			mTxtPwdViewList.get(mTxtPwdViewListIndex).setText("*");
		}
	}

	private void delPwdKey() {
		if (mTxtPwdViewListIndex >= 0) {
			mTxtPwdViewList.get(mTxtPwdViewListIndex).setHint("");
			mTxtPwdViewList.get(mTxtPwdViewListIndex).setText("");
			mTxtPwdViewListIndex--;
		}
	}

	private void showVerifyDialog() {
		mDialog = new PopupWindowDialog(mRmctActivity);
		mDialog.setContentView(R.layout.rmct_center_verify);

		View view = mDialog.getContentView();

		((TextView) view.findViewById(R.id.rmct_center_verify_title))
				.setText(mCurrRmctInfo.getNameResId());

		view.findViewById(R.id.rmct_center_verify_close).setOnClickListener(
				this);

		mTxtPwdViewList.clear();

		mTxtPwdViewList.add((TextView) view
				.findViewById(R.id.rmct_center_verify_pwd001));
		mTxtPwdViewList.add((TextView) view
				.findViewById(R.id.rmct_center_verify_pwd002));
		mTxtPwdViewList.add((TextView) view
				.findViewById(R.id.rmct_center_verify_pwd003));
		mTxtPwdViewList.add((TextView) view
				.findViewById(R.id.rmct_center_verify_pwd004));
		mTxtPwdViewList.add((TextView) view
				.findViewById(R.id.rmct_center_verify_pwd005));
		mTxtPwdViewList.add((TextView) view
				.findViewById(R.id.rmct_center_verify_pwd006));

		view.findViewById(R.id.rmct_center_verify_num001).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num002).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num003).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num004).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num005).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num006).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num007).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num008).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num009).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_delete).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_num000).setOnClickListener(
				this);
		view.findViewById(R.id.rmct_center_verify_define).setOnClickListener(
				this);

		mDialog.show();
	}

	/**
	 * 发送远程控制命令
	 */
	private void sendRmctCommand() {
		// // 发送安防指令
		// ActionBarHelper.startProgress();
		NetHelper.getInstance().sendCtlToServerGPS(
				MessageCenter.netId.GPS_CTL_CENTER, // 主命令
				mCurrRmctInfo.getRmctCmdId(), // 子命令
				getRmctRadius());// 数据

		// // 提示 // 发送成功后
		// if (mCurrRmctCenterInfo.getRmctCmdId() ==
		// MessageCenter.netId.GPS_CTR_ON_OILELE
		// || mCurrRmctCenterInfo.getRmctCmdId() ==
		// MessageCenter.netId.GPS_CTR_OFF_OILELE) {
		// ToastShow.show(mContext, "请等待客服回电确认");
		// }
		// // 改变列表状态
		// mRmctCenterHelper.changeButtonStatus(mCurrRmctCenterInfo
		// .getRmctCmdId());
		// mRmctCenterAdapter.notifyDataSetChanged();
	}

	public void onClick(View v) {
		v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
				HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

		Button currButton = (Button) v;

		switch (v.getId()) {
		case R.id.rmct_center_verify_close:
			hideVerifyDialog();
			break;
		case R.id.rmct_center_verify_define:
			// if (checkrmctpwd()) {
			// 验证安防密码
			if (mTxtPwdViewListIndex != 6 - 1) {
				ToastShow.show(mRmctActivity,
						R.string.rmct_center_toast_errpwdfmt);
			} else {
				ActionBarHelper.startProgress();
				UserInfo user = GlobalApplication.getApplication()
						.getCurrUser();
				if (user != null) {
					mIsControlRequest = false;
					OBDHelper.validAFSPass(user.name, getPassword(), this);
				}
				// 隐藏对话框
				hideVerifyDialog();
				// } else {
				// ToastShow.show(mContext, R.string.rmct_center_toast_errpwd);
				// }
			}
			break;
		case R.id.rmct_center_verify_delete:
			delPwdKey();
			break;
		case R.id.rmct_center_verify_num001:
		case R.id.rmct_center_verify_num002:
		case R.id.rmct_center_verify_num003:
		case R.id.rmct_center_verify_num004:
		case R.id.rmct_center_verify_num005:
		case R.id.rmct_center_verify_num006:
		case R.id.rmct_center_verify_num007:
		case R.id.rmct_center_verify_num008:
		case R.id.rmct_center_verify_num009:
		case R.id.rmct_center_verify_num000:
			addPwdKey(currButton.getText().toString());
			break;
		}
	}

	public String getPassword() {
		String password = "";
		for (int i = 0; i < mTxtPwdViewList.size(); i++) {
			password += mTxtPwdViewList.get(i).getHint();
		}
		return password;
	}

	// private boolean checkrmctpwd() {
	// if (mTxtPwdViewListIndex != 6 - 1) {
	// ToastShow.show(mContext, R.string.rmct_center_toast_errpwdfmt);
	// return false;
	// }
	//
	// String password = "";
	// for (int i = 0; i < mTxtPwdViewList.size(); i++) {
	// password += mTxtPwdViewList.get(i).getHint().toString();
	// }
	// // 验证密码正确性return true;
	// return password.equals(getRmctPassword());
	// }

	// public String getRmctPassword() {
	// SharedPreferences sharedPreferences = mContext.getSharedPreferences(
	// MoreReCtlPwdActivity.RMCT_PASSWROD_NAME,
	// MoreReCtlPwdActivity.MODE_WORLD_READABLE);
	// String rmctPassword = sharedPreferences.getString(
	// MoreReCtlPwdActivity.RMCT_PASSWORD_KEY, null);//
	// RMCT_PASSWORD_VALUE_DEFAULT
	// return rmctPassword;
	// }

	public int getRmctRadius() {
		SharedPreferences sharedPreferences = mRmctActivity
				.getSharedPreferences(MoreFencesActivity.RMCT_RADIUS_NAME,
						MoreFencesActivity.MODE_WORLD_READABLE);
		int rmctRadius = sharedPreferences.getInt(
				MoreFencesActivity.RMCT_RADIUS_KEY,
				MoreFencesActivity.RMCT_DEFAULT_RADIUS);
		return rmctRadius;
	}

	private static final int WHAT_ON_MSG_FAILURE = 0x1240;
	private static final int WHAT_ON_MSG_SUCCESS = 0x1241;

	private Handler mOnMsgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_ON_MSG_SUCCESS:

				int iGpsCtlNetId = Integer.parseInt("" + msg.obj);
				Resources res = mRmctActivity.getResources();
				switch (iGpsCtlNetId) {
				/** 远程开车门 */
				case MessageCenter.netId.GPS_CTR_OPEN_DOOR:
					ToastShow.show(mRmctActivity,
							res.getString(R.string.rmct_center_opendoor)
									+ "成功！");
					break;
				/** 远程关车门 */
				case MessageCenter.netId.GPS_CTR_CLOSE_DOOR:
					ToastShow.show(mRmctActivity,
							res.getString(R.string.rmct_center_shutdoor)
									+ "成功！");
					break;
				/** 远程开油电 */
				case MessageCenter.netId.GPS_CTR_ON_OILELE:
					ToastShow.show(mRmctActivity, "请求已发送，请等待客服回电确认");
					// ToastShow.show(mRmctActivity,
					// res.getString(R.string.rmct_center_onoilele)
					// + "成功！请等待客服回电确认.");// ToastShow.show(mContext,
					// "请等待客服回电确认");
					break;
				/** 远程断油电 */
				case MessageCenter.netId.GPS_CTR_OFF_OILELE:
					ToastShow.show(mRmctActivity, "请求已发送，请等待客服回电确认");
					// ToastShow.show(mRmctActivity,
					// res.getString(R.string.rmct_center_ofoilele)
					// + "成功！请等待客服回电确认.");
					break;
				/** 车辆设防 */
				case MessageCenter.netId.GPS_CTR_ON_GARRISON:
					ToastShow.show(mRmctActivity,
							res.getString(R.string.rmct_center_ongarrison)
									+ "成功！");
					break;
				/** 车辆撤防 */
				case MessageCenter.netId.GPS_CTR_OFF_GARRISON:
					ToastShow.show(mRmctActivity,
							res.getString(R.string.rmct_center_ofgarrison)
									+ "成功！");
					break;
				/** 停车位置提示 */
				case MessageCenter.netId.GPS_CTR_SHOW_PARK:
					ToastShow.show(mRmctActivity,
							res.getString(R.string.rmct_center_poiprompt)
									+ "成功！");
					break;
				default:
					ToastShow.show(mRmctActivity, "无法识别的成功指令！！！");
					break;

				}
				mRmctCenterHelper.changeButtonStatus(iGpsCtlNetId);
				mRmctCenterAdapter.notifyDataSetChanged();
				break;
			case WHAT_ON_MSG_FAILURE:
				if (msg.obj != null && !msg.obj.toString().equals("")) {
					ToastShow.show(mRmctActivity, "" + msg.obj);
				} else {
					ToastShow.show(mRmctActivity,
							R.string.toast_rmct_center_error);
				}
				break;
			}
		};
	};

	@Override
	public void onReceived(Result result) {
		ActionBarHelper.stopProgress();
		if (result.head.resCode == 0) {
			mOnMsgHandler.sendMessage(mOnMsgHandler.obtainMessage(
					WHAT_ON_MSG_SUCCESS, result.object));
		} else {
			mOnMsgHandler.sendMessage(mOnMsgHandler.obtainMessage(
					WHAT_ON_MSG_FAILURE, result.head.resMsg));
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!GlobalApplication.isGpsLogin()) {
			ToastShow.show(getActivitySafe(),
					R.string.toast_not_logged_operation_alert);
			return;
		}

		mCurrRmctInfo = (RmctCenterInfo) mRmctCenterAdapter.getItem(position);
		if (mCurrRmctInfo.isEnabled()) {// 可用才执行
			view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
					HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
			showVerifyDialog();
		} else {
			// ToastShow.show(getActivitySafe(),"您的版本不支持此功能!");
		}
	}
}