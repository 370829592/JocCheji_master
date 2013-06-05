package com.icalinks.mobile.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.model.RmctCenterInfo;
import com.provider.common.config.MessageCenter;

public class RmctCenterHelper {
	private Activity mActivity;

	public RmctCenterHelper(Activity activity) {
		mActivity = activity;
	}

	private RmctCenterInfo m_rmct_center_opendoor;
	private RmctCenterInfo m_rmct_center_shutdoor;
	private RmctCenterInfo m_rmct_center_onoilele;
	private RmctCenterInfo m_rmct_center_ofoilele;
	private RmctCenterInfo m_rmct_center_ongarrison;
	private RmctCenterInfo m_rmct_center_ofgarrison;
	// private RmctCenterInfo m_rmct_center_poiprompt;

	private ArrayList<RmctCenterInfo> mRmctCenterInfoList = new ArrayList<RmctCenterInfo>();
	{
		// 远程开车门
		mRmctCenterInfoList.add(m_rmct_center_opendoor = new RmctCenterInfo(
				MessageCenter.netId.GPS_CTR_OPEN_DOOR,
				R.string.rmct_center_opendoor, R.drawable.rmct_center_opendoor,
				R.drawable.rmct_center_opendoor_p));
		// 远程关车门
		mRmctCenterInfoList.add(m_rmct_center_shutdoor = new RmctCenterInfo(
				MessageCenter.netId.GPS_CTR_CLOSE_DOOR,
				R.string.rmct_center_shutdoor, R.drawable.rmct_center_shutdoor,
				R.drawable.rmct_center_shutdoor_p));

		// 远程断油电
		mRmctCenterInfoList.add(m_rmct_center_onoilele = new RmctCenterInfo(
				MessageCenter.netId.GPS_CTR_ON_OILELE,
				R.string.rmct_center_onoilele, R.drawable.rmct_center_onoilele,
				R.drawable.rmct_center_onoilele_p));

		// 车辆设防
		mRmctCenterInfoList.add(m_rmct_center_ongarrison = new RmctCenterInfo(
				MessageCenter.netId.GPS_CTR_ON_GARRISON,
				R.string.rmct_center_ongarrison,
				R.drawable.rmct_center_ongarrison,
				R.drawable.rmct_center_ongarrison_p));
		// 车辆撤防
		mRmctCenterInfoList.add(m_rmct_center_ofgarrison = new RmctCenterInfo(
				MessageCenter.netId.GPS_CTR_OFF_GARRISON,
				R.string.rmct_center_ofgarrison,
				R.drawable.rmct_center_ofgarrison,
				R.drawable.rmct_center_ofgarrison_p));
		// 远程开油电
		mRmctCenterInfoList.add(m_rmct_center_ofoilele = new RmctCenterInfo(
				MessageCenter.netId.GPS_CTR_OFF_OILELE,
				R.string.rmct_center_ofoilele, R.drawable.rmct_center_ofoilele,
				R.drawable.rmct_center_ofoilele_p));

		// // 停车位置提示
		// mRmctCenterInfoList.add(m_rmct_center_poiprompt = new RmctCenterInfo(
		// MessageCenter.netId.GPS_CTR_SHOW_PARK,
		// R.string.rmct_center_poiprompt,
		// R.drawable.rmct_center_poiprompt,
		// R.drawable.rmct_center_poiprompt_p));

		// // // 冷气预开
		// // mRmctCenterInfoList.add(new
		// // RmctCenterInfo(MessageCenter.netId.GPS_CTR_ON_COOLAIR,
		// // R.string.rmct_center_oncoolair,
		// // R.drawable.rmct_center_oncoolair));
		// // // 碰撞报警设置
		// // mRmctCenterInfoList.add(new
		// // RmctCenterInfo(MessageCenter.netId.GPS_CTR_ALARM_SETTING,
		// // R.string.rmct_center_collisionalarm,
		// // R.drawable.rmct_center_collisionalarm));

	}

	public ArrayList<RmctCenterInfo> getList() {
		return mRmctCenterInfoList;
	}

	private void setDoorStatus(boolean activated) {
		m_rmct_center_opendoor.setActivated(activated);
		m_rmct_center_shutdoor.setActivated(!activated);
		saveStatus(DOOR_STATUS, activated);
	}

	private void setOileleStatus(boolean activated) {
		m_rmct_center_onoilele.setActivated(activated);
		m_rmct_center_ofoilele.setActivated(!activated);
		saveStatus(OILELE_STATUS, activated);
	}

	private void setGarrisonStatus(boolean activated) {
		m_rmct_center_ongarrison.setActivated(activated);
		m_rmct_center_ofgarrison.setActivated(!activated);
		saveStatus(GARRISON_STATUS, activated);
	}

	public void changeButtonStatus(int netId) {
		switch (netId) {
		case MessageCenter.netId.GPS_CTR_OPEN_DOOR:
			setDoorStatus(true);
			break;
		case MessageCenter.netId.GPS_CTR_CLOSE_DOOR:
			setDoorStatus(false);
			break;
		case MessageCenter.netId.GPS_CTR_ON_OILELE:
			setOileleStatus(true);
			break;
		case MessageCenter.netId.GPS_CTR_OFF_OILELE:
			setOileleStatus(false);
			break;
		case MessageCenter.netId.GPS_CTR_ON_GARRISON:
			setGarrisonStatus(true);
			break;
		case MessageCenter.netId.GPS_CTR_OFF_GARRISON:
			setGarrisonStatus(false);
			break;
		}
	}

	private String mCurrentUserName;

	public void setCurrUserName(String name) {
		mCurrentUserName = name;
		// 设置激活为不激活
		m_rmct_center_opendoor.setActivated(false);
		m_rmct_center_shutdoor.setActivated(false);
		m_rmct_center_onoilele.setActivated(false);
		m_rmct_center_ofoilele.setActivated(false);
		m_rmct_center_ongarrison.setActivated(false);
		m_rmct_center_ofgarrison.setActivated(false);
		// 设置都不可用
		m_rmct_center_opendoor.setEnabled(false);
		m_rmct_center_shutdoor.setEnabled(false);
		m_rmct_center_onoilele.setEnabled(false);
		m_rmct_center_ofoilele.setEnabled(false);
		m_rmct_center_ongarrison.setEnabled(false);
		m_rmct_center_ofgarrison.setEnabled(false);
		// m_rmct_center_poiprompt.setEnabled(false);

		// 用户不为空时，默认激活油电开，围栏关
		if (mCurrentUserName != null) {

			// m_rmct_center_opendoor.setActivated(readStatus(DOOR_STATUS));
			// m_rmct_center_onoilele.setActivated(readStatus(OILELE_STATUS));
			// m_rmct_center_ongarrison.setActivated(readStatus(GARRISON_STATUS));

			// setDoorStatus(readStatus(DOOR_STATUS));
			setOileleStatus(readStatus(OILELE_STATUS, true));
			setGarrisonStatus(readStatus(GARRISON_STATUS, false));
		}
	}

	private void saveStatus(String name, boolean activated) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				PREFERENCES_NAME, mActivity.MODE_WORLD_READABLE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(mCurrentUserName + name, activated);
		editor.commit();
	}

	private static final String PREFERENCES_NAME = "RmctCenter";
	private static final String DOOR_STATUS = "door_status";
	private static final String OILELE_STATUS = "oilele_status";
	private static final String GARRISON_STATUS = "garrison_status";

	public boolean readStatus(String name, boolean defValue) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				PREFERENCES_NAME, mActivity.MODE_WORLD_READABLE);
		return sharedPreferences.getBoolean(mCurrentUserName + name, defValue);
	}

}
