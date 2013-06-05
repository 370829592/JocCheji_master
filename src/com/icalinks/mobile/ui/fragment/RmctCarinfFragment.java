package com.icalinks.mobile.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.GeoPoint;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.EditVehicleActivity;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.mobile.widget.PopupWindowDialog;
import com.icalinks.obd.vo.VehicleInfo;
import com.provider.common.JocApplication;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 
 * @ClassName: RmctCarinfFragment
 * @Description: TODO(这里用一句话描述这个类的作用) 车辆信息界面
 * @author: wc_zhang@calinks.com.cn
 * @date: 2013-5-24 上午10:49:06
 * 
 */
public class RmctCarinfFragment extends BaseFragment implements
		View.OnClickListener {

	private Typeface lcdTypeface;
	// private boolean sdFlag = false;
	private Button mActionBarButton;
	private TextView m_lbl_gpstime;
	private TextView m_lbl_licenseplate;
	private TextView m_lbl_status;
	private TextView rmct_carinf_lbl_emissions;
	private TextView m_lbl_frameno;
	private TextView m_lbl_engineno;
	private TextView m_lbl_uxtid;
	private TextView m_lbl_uxtsim;
	private TextView m_lbl_license_date;
	private TextView m_lbl_examine_date;
	private GlobalApplication mGlobalApplication;
	@SuppressLint("ValidFragment")
	private AbsSubActivity mActivity;

	public RmctCarinfFragment(int resId) {
		super(resId);
	}

	public RmctCarinfFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (AbsSubActivity) getActivitySafe();
		View v = inflater.inflate(R.layout.rmct_carinf, null);
		// sdFlag = GlobalApplication.sdFlag;
		initView(v);
		
		return v;
	}

	private void initView(View v) {

		m_lbl_uxtsim = (TextView) v.findViewById(R.id.rmct_carinf_lbl_uxtsim);
		m_lbl_uxtid = (TextView) v.findViewById(R.id.rmct_carinf_lbl_uxtid);
		m_lbl_license_date = (TextView) v
				.findViewById(R.id.rmct_carinf_lbl_license_date);
		m_lbl_examine_date = (TextView) v
				.findViewById(R.id.rmct_carinf_lbl_examine_date);

		m_lbl_licenseplate = (TextView) v
				.findViewById(R.id.rmct_carinf_lbl_licenseplate);
		m_lbl_gpstime = (TextView) v.findViewById(R.id.rmct_carinf_lbl_gpstime);

		lcdTypeface = Typeface.createFromAsset(mActivity.getAssets(),
				"fonts/lcd.ttf");
		m_lbl_gpstime.setTypeface(lcdTypeface);
		// if (sdFlag) {
		// Typeface.createFromFile(JocParameter.LCD_FONTPATH);
		// m_lbl_gpstime.setTypeface(lcdTypeface);
		// } else {
		// m_lbl_gpstime.setTextSize(16);
		// }

		m_lbl_status = (TextView) v.findViewById(R.id.rmct_carinf_lbl_status);
		rmct_carinf_lbl_emissions = (TextView) v
				.findViewById(R.id.rmct_carinf_lbl_emissions);
		m_lbl_frameno = (TextView) v.findViewById(R.id.rmct_carinf_lbl_frameno);
		m_lbl_engineno = (TextView) v
				.findViewById(R.id.rmct_carinf_lbl_engineno);

		mGlobalApplication = (GlobalApplication) getActivitySafe()
				.getApplication();
	}

	private UserInfo mUserInfo;

	@Override
	public void onResume() {
		super.onResume();
		setTitle(mActivity.getString(R.string.ms_clxx));
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.goback();
			}
		});
		VehicleInfo vehicleInfo = JocApplication.getVehicleInfo();
		if (JocApplication.isGpsLogin() && vehicleInfo != null) {
			onHandlerSuccess(vehicleInfo);
		}

		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();
		if (userinfo != null) {
			if (mUserInfo != null) {
				if (!userinfo.name.equals(mUserInfo.name)) {
					mIsSafePasswordValid = false;
				}
			}
			mUserInfo = userinfo;
			OBDHelper.getVehicleInfo(userinfo.name, userinfo.pswd, this);
		} else {
			clear();
		}

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
		if (!JocApplication.isGpsLogin()) {
			Toast.makeText(mActivity, "请先登录", Toast.LENGTH_LONG).show();
			return;
		}
		VehicleInfo vehicleInfo = JocApplication.getVehicleInfo();
		if (vehicleInfo != null) {
			Intent intent = new Intent();
			intent.setClass(mActivity, EditVehicleActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable(VehicleInfo.class.getSimpleName(),
					vehicleInfo);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
		} else {
			UserInfo userinfo = GlobalApplication.getApplication()
					.getCurrUser();
			if (userinfo != null) {
				OBDHelper.getVehicleInfo(userinfo.name, userinfo.pswd,
						new OnCallbackListener() {

							@Override
							public void onSuccess(Result result) {
								if (result.head.resCode == 0) {
									VehicleInfo info = (VehicleInfo) result.object;
									if (info != null) {
										Intent intent = new Intent();
										intent.setClass(mActivity,
												EditVehicleActivity.class);
										Bundle bundle = new Bundle();
										bundle.putSerializable(
												VehicleInfo.class
														.getSimpleName(), info);
										intent.putExtras(bundle);
										mActivity.startActivity(intent);
									}
								}
							}

							@Override
							public void onFailure(Result result) {
								Toast.makeText(mActivity,
										result.head.resMsg, Toast.LENGTH_SHORT)
										.show();
							}
						});
			}
		}
	}

	// private boolean checkNetwork() {
	// boolean flag = false;
	// ConnectivityManager cwjManager = (ConnectivityManager) mRmctActivity
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// if (cwjManager.getActiveNetworkInfo() != null) {
	// flag = cwjManager.getActiveNetworkInfo().isAvailable();
	// }
	// return flag;
	// }

	@Override
	public void onPause() {
		super.onPause();
		ActionBarHelper.stopProgress();
		GlobalApplication.getApplication().getHomeActivity()
				.hideActionBarButton();
	}

	private void clear() {
		mIsSafePasswordValid = false;

		m_lbl_uxtsim.setText("");
		m_lbl_uxtid.setText("");
		m_lbl_licenseplate.setText("");
		m_lbl_frameno.setText("");
		m_lbl_engineno.setText("");
		m_lbl_gpstime.setText("");
		m_lbl_status.setText("");
		rmct_carinf_lbl_emissions.setText("");
		m_lbl_license_date.setText("");
		m_lbl_examine_date.setText("");
	}

	private boolean mIsSafePasswordValid = false;

	private VehicleInfo mVehicleInfo;

	@Override
	protected void onHandlerSuccess(Object obj) {
		ActionBarHelper.stopProgress();
		if (obj != null) {
			if (!obj.getClass().equals(VehicleInfo.class)) {
				// 安防密码验证？// 验证安防密码
				mIsSafePasswordValid = !mIsSafePasswordValid;
			} else {
				try {
					mVehicleInfo = (VehicleInfo) obj;
					if (mVehicleInfo.getLicensePlate() != null
							&& !mVehicleInfo.getLicensePlate().trim()
									.equals("")) {
						m_lbl_licenseplate.setText(mVehicleInfo
								.getLicensePlate());
						m_lbl_uxtid.setText(mVehicleInfo.getPriorityNo());
						m_lbl_uxtsim.setText(mVehicleInfo.getSimcard());

						m_lbl_frameno.setText(mVehicleInfo.getFrameNumber());// 车架号
						m_lbl_engineno.setText(mVehicleInfo.getEngineNumber());// 发动机号
						m_lbl_gpstime.setText(mVehicleInfo.getGpsTime());
						m_lbl_status.setText(mVehicleInfo.getStatus());
						rmct_carinf_lbl_emissions.setText(mVehicleInfo
								.getDisplacement());
						m_lbl_license_date.setText(mVehicleInfo
								.getLicenseDate());
						m_lbl_examine_date.setText(mVehicleInfo
								.getExamineDate());

						// m_btn_position.setVisibility(View.VISIBLE);
						// m_lbl_location.setText(mVehicleInfo.getLocation());

						// 记录当前车辆位置
						GeoPoint geopoint = new GeoPoint(
								(int) (mVehicleInfo.getLatitude() * 1e6),
								(int) (mVehicleInfo.getLongitude() * 1e6));

						// 经纬度同时为零的话认为，是无效的坐标，即：没有取到坐标
						if (geopoint.getLatitudeE6() == 0
								&& geopoint.getLongitudeE6() == 0) {
							mGlobalApplication.setCarPosition(null,
									mVehicleInfo.getLocation());
						} else {
							mGlobalApplication.setCarPosition(geopoint,
									mVehicleInfo.getLocation());
						}
					} else {
						clear();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onHandlerFailure(Object obj) {
		super.onHandlerFailure(obj);
		ActionBarHelper.stopProgress();
	}

	public void onClick(View v) {
		v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
				HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

		Button currButton = (Button) v;

		switch (v.getId()) {
		case R.id.rmct_center_verify_define:

			// 验证安防密码
			if (mTxtPwdViewListIndex != 6 - 1) {
				ToastShow.show(mActivity,
						R.string.rmct_center_toast_errpwdfmt);
			} else {
				ActionBarHelper.startProgress();
				UserInfo user = GlobalApplication.getApplication()
						.getCurrUser();
				if (user != null) {
					OBDHelper.validAFSPass(user.name, getPassword(), this);
				}
				// 隐藏对话框
				hideVerifyDialog();
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

	private PopupWindowDialog mDialog;

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

	// private void showVerifyDialog() {
	// mDialog = new PopupWindowDialog(mRmctActivity);
	// mDialog.setContentView(R.layout.rmct_center_verify);
	//
	// View view = mDialog.getContentView();
	//
	// ((TextView) view.findViewById(R.id.rmct_center_verify_title))
	// .setText("显示车辆位置");
	//
	// view.findViewById(R.id.rmct_center_verify_close).setOnClickListener(
	// this);
	//
	// mTxtPwdViewList.clear();
	//
	// mTxtPwdViewList.add((TextView) view
	// .findViewById(R.id.rmct_center_verify_pwd001));
	// mTxtPwdViewList.add((TextView) view
	// .findViewById(R.id.rmct_center_verify_pwd002));
	// mTxtPwdViewList.add((TextView) view
	// .findViewById(R.id.rmct_center_verify_pwd003));
	// mTxtPwdViewList.add((TextView) view
	// .findViewById(R.id.rmct_center_verify_pwd004));
	// mTxtPwdViewList.add((TextView) view
	// .findViewById(R.id.rmct_center_verify_pwd005));
	// mTxtPwdViewList.add((TextView) view
	// .findViewById(R.id.rmct_center_verify_pwd006));
	//
	// view.findViewById(R.id.rmct_center_verify_num001).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num002).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num003).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num004).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num005).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num006).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num007).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num008).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num009).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_delete).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_num000).setOnClickListener(
	// this);
	// view.findViewById(R.id.rmct_center_verify_define).setOnClickListener(
	// this);
	//
	// mDialog.show();
	// }
}
