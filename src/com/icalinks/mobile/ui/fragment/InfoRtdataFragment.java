package com.icalinks.mobile.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calinks.vehiclemachine.commom.OBDProc;
import com.calinks.vehiclemachine.listener.OnGetRealDataListener;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.ActionBarHelper;
import com.icalinks.mobile.ui.InfoActivity;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.mobile.widget.DashboardView;
import com.provider.model.Result;
import com.provider.model.resources.RealTimeOBD;
import com.provider.net.listener.OnCallbackListener;

/**
 * 实时数据
 * 
 * @author zg_hu@calinks.com.cn
 * 
 */

@SuppressLint("ValidFragment")
public class InfoRtdataFragment extends BaseFragment {
	private static final String TAG = InfoRtdataFragment.class.getSimpleName();

	public InfoRtdataFragment(int resId) {
		super(resId);
	}

	private final static int MAX = 4;

	private InfoActivity mInfoActivity;
	private View mView;

	public static final int INFO_RTDATA = 0x00;
	public static final int INFO_RTDATA_ITEM = 0x01;

	private RealTimeOBD mRtdata;
	private RealTimeOBD rtdata;

	private static int winWidth;
	private static int winHeight;

	private DashboardView mOilDialView;// 瞬时油�?
	private DashboardView mRTDialView;// 实时速度
	private DashboardView mSpeedDialView;// 引擎速度
	private DashboardView mEngDialView;// 引擎水温

	private int resOil[] = { R.drawable.info_rt_ins_oil,
			R.drawable.info_rt_ins_oil_needle, R.drawable.info_rt_ins_oil_boot };
	private int resOil_[] = { R.drawable.info_rt_ins_oil_,
			R.drawable.info_rt_ins_oil_needle_, R.drawable.info_rt_ins_oil_boot };

	private int resRT[] = { R.drawable.info_rt_rt,
			R.drawable.info_rt_rt_needle, R.drawable.info_rt_rt_needle_boot };
	private int resRT_[] = { R.drawable.info_rt_rt_,
			R.drawable.info_rt_rt_needle_, R.drawable.info_rt_rt_needle_boot };

	private int resSpeed[] = { R.drawable.info_rt_eng_speed,
			R.drawable.info_rt_eng_speed_needle,
			R.drawable.info_rt_eng_speed_boot };
	private int resSpeed_[] = { R.drawable.info_rt_eng_speed_,
			R.drawable.info_rt_eng_speed_needle_,
			R.drawable.info_rt_eng_speed_boot };

	private int resEng[] = { R.drawable.info_rt_eng_temp,
			R.drawable.info_rt_eng_temp_needle,
			R.drawable.info_rt_eng_temp_boot };
	private int resEng_[] = { R.drawable.info_rt_eng_temp_,
			R.drawable.info_rt_eng_temp_needle_,
			R.drawable.info_rt_eng_temp_boot };

	private boolean accFlag = false;

	// private int openAcc = 1;

	private float minValue[] = { -0.08f, -1.0f, -20.0f, -0.4f };
	private float maxValue[] = { 0.08f, 1.0f, 20f, 0.4f };
	private float[] rotation;

	private boolean swayFlag = false;
	private Thread swayThread;

	private boolean refresh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Log.e(TAG, "create");
		mView = inflater.inflate(R.layout.info_rtdata, null);
		mInfoActivity = (InfoActivity) this.getActivitySafe();
		rotation = new float[MAX];
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		checkInitViews();
		checkReqRtdata();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void checkInitViews() {
		if (mOilDialView == null) {
			mOilDialView = (DashboardView) mView
					.findViewById(R.id.info_rt_ins_oil);
			mRTDialView = (DashboardView) mView.findViewById(R.id.info_rt_rt);
			mSpeedDialView = (DashboardView) mView
					.findViewById(R.id.info_rt_eng_speed);
			mEngDialView = (DashboardView) mView
					.findViewById(R.id.info_rt_eng_temp);

			
		}
	}


	private void checkReqRtdata() {
		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();//
		// 得到当前用户
		if (userinfo != null) {
			mRtdata = new RealTimeOBD();
			ActionBarHelper.startProgress();
//			 mLicenseplate.setText(userinfo.name);
//			mRtdata.register(userinfo.name, userinfo.pswd,
//					mRtdataOnCallbackListener);
			OBDProc obd = GlobalApplication.getApplication().getObd();
			mRtdata = obd.getRealTimeData();
			mHandler.sendMessage(mHandler.obtainMessage(INFO_RTDATA,
					mRtdata));
			obd.setOnGetRealDataListener(new OnGetRealDataListener() {

				@Override
				public void onGetRealTimeOBD(RealTimeOBD rtdata) {
					// TODO Auto-generated method stub
					ActionBarHelper.stopProgress();
					mHandler.sendMessage(mHandler.obtainMessage(INFO_RTDATA,
							rtdata));
				}
				
			});
			swayThread = new Thread(new SwayRunnable());
			swayFlag = true;
			swayThread.start();
			swayThread = new Thread(new SwayRunnable());
			swayFlag = true;
			swayThread.start();
		} else {
			clear();
		}

	}
	/**
	 * 用 的是网络接口
	 */
//	private void checkReqRtdata() {
//		UserInfo userinfo = GlobalApplication.getApplication().getCurrUser();// 得到当前用户
//		if (userinfo != null) {
//			mRtdata = new RealTimeOBD();
//			ActionBarHelper.startProgress();
//			mRtdata.register(userinfo.name, userinfo.pswd,
//					mRtdataOnCallbackListener);
//			swayThread = new Thread(new SwayRunnable());
//			swayFlag = true;
//			swayThread.start();
//			swayThread = new Thread(new SwayRunnable());
//			swayFlag = true;
//			swayThread.start();
//		} else {
//			clear();
//		}
//	}

	private OnCallbackListener mRtdataOnCallbackListener = new OnCallbackListener() {

		@Override
		public void onSuccess(Result result) {
			ActionBarHelper.stopProgress();
			mHandler.sendMessage(mHandler.obtainMessage(INFO_RTDATA,
					result.object));
		}

		@Override
		public void onFailure(Result result) {
			ActionBarHelper.stopProgress();
			if (result.head.resMsg != null && !result.head.resMsg.equals("")) {
				ToastShow.show(getActivitySafe(), result.head.resMsg);
				GlobalApplication.getApplication().getObd().stopReadOBD();
			}
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INFO_RTDATA:
				update(msg.obj);
				break;
			case INFO_RTDATA_ITEM:
				setRotation();
				break;
			}

		};
	};

	public void update(Object obj) {
		rtdata = (RealTimeOBD) obj;

		// acc开或关，如果发生改变，需要更�?
		// Log.e(TAG, "accFlag = " + accFlag);

		accFlag = rtdata.isAccOpen;
		// Log.e(TAG, "accFlag = " + accFlag);

		if (accFlag) {

			// if (openAcc == 1) {
			mOilDialView.setDailImage(mInfoActivity, resOil_);
			mRTDialView.setDailImage(mInfoActivity, resRT_);
			mSpeedDialView.setDailImage(mInfoActivity, resSpeed_);
			mEngDialView.setDailImage(mInfoActivity, resEng_);

			swayFlag = true;
			// }

			// Log.e(TAG, "瞬时油耗： " + rtdata.oilConsum);
			// Log.e(TAG, "实时转速： " + rtdata.carSpeed);
			// Log.e(TAG, "引擎转速： " + rtdata.rotateSpeed);
			// Log.e(TAG, "引擎水温�? + rtdata.coolantTemp);

			rotation[0] = stringToFloat(rtdata.oilConsum);// 瞬时油�?

			// // ggz:瞬时油耗大于20时最大显示20
			// rotation[0] = rotation[0] > 20 ? 20 : rotation[0];

			rotation[1] = stringToFloat(rtdata.carSpeed);// 实时速度
			rotation[2] = stringToFloat(rtdata.rotateSpeed);// 引擎转�?
			rotation[3] = stringToFloat(rtdata.coolantTemp);// 引擎水温

			// // ggz:引擎水温大于120时最大显示120
			// rotation[3] = rotation[3] > 120 ? 120 : rotation[3];

			setRotation();

			// openAcc = 0;

		} else {
			// if (openAcc == 0) {
			mOilDialView.setDailImage(mInfoActivity, resOil);
			mRTDialView.setDailImage(mInfoActivity, resRT);
			mSpeedDialView.setDailImage(mInfoActivity, resSpeed);
			mEngDialView.setDailImage(mInfoActivity, resEng);

			swayFlag = false;
		}

		// clear();
		rtdata = null;
		accFlag = false;
		for (int i = 0; i < MAX; i++) {
			rotation[i] = 0.0f;
		}
		// openAcc = 1;

		// }

	}

	private float stringToFloat(String str) {
		if(str == null){
			return 0;
		}
		return Float.parseFloat(str);
	}

	private void setRotation() {
		// ggz:瞬时油耗大于20时最大显示20
		rotation[0] = rotation[0] > 20 ? 20 : rotation[0];
		rotation[1] = rotation[1] > 240?240:rotation[1];
		rotation[2] = rotation[2] > 8 ? 8 :rotation[2];
		// ggz:引擎水温大于120时最大显示120
		rotation[3] = rotation[3] > 120 ? 120 : rotation[3];

		mOilDialView.setRotation(rotation[0], 0);// 瞬时油�?
		mRTDialView.setRotation(rotation[1], 1); // 实时速度
		mSpeedDialView.setRotation(rotation[2], 2);// 引擎转�?
		mEngDialView.setRotation(rotation[3], 3);// 引擎水温
	}

	private void clear() {
		rtdata = null;
		accFlag = false;
		for (int i = 0; i < MAX; i++) {
			rotation[i] = 0.0f;
		}

		setRotation();

		mOilDialView.setDailImage(mInfoActivity, resOil);
		mRTDialView.setDailImage(mInfoActivity, resRT);
		mSpeedDialView.setDailImage(mInfoActivity, resSpeed);
		mEngDialView.setDailImage(mInfoActivity, resEng);
		swayFlag = false;
	}

	public static int getWidth() {
		return winWidth;
	}

	public static int getHeight() {
		return winHeight;
	}

	@Override
	public void onPause() {
		if (mRtdata != null) {
			mRtdata.unregister();
		}
		super.onPause();
		ActionBarHelper.stopProgress();
	}

	private float swayFloat(int i) {
		float swayF = 0.0f;
		swayF = (float) ((Math.random()) * (maxValue[i] - minValue[i]))
				+ minValue[i];

		return swayF;
	}

	private class SwayRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (swayFlag) {
				try {
					Thread.sleep(200);
					if (rtdata != null) {
						for (int i = 0; i < MAX; i++) {
							if (rotation[i] > 0.1) {
								rotation[i] += swayFloat(i);
								mHandler.sendMessage(mHandler.obtainMessage(
										INFO_RTDATA_ITEM, null));

							} else {
								continue;
							}
						}
					} else {
						continue;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
