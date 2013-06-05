package com.icalinks.mobile.ui.fragment;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.ui.MoreTelephoneActivity;
import com.icalinks.mobile.ui.NearActivity;
import com.icalinks.mobile.ui.SvcsActivity;
import com.icalinks.mobile.util.ToastShow;
import com.provider.common.util.JocData;
import com.provider.model.resources.CallCenterHelper;
/**
 * 服务中心
 * @author Administrator
 *
 */
@SuppressLint("ValidFragment")
public class SvcsCenterFragment extends BaseFragment {

	public static final String TAG = SvcsCenterFragment.class.getSimpleName();

	private int pageIndex;
	private CallCenterHelper mCallCenterHelper;
	private View mViewSvcsCenterCenter;
	private SvcsActivity mSvcsActivity;

	// private static ArrayList<JocData.CallInfo> mCallInfoList;

	@SuppressLint("ValidFragment")
	public SvcsCenterFragment(int resId) {
		super(resId);
	}
	@SuppressLint("ValidFragment")
	public SvcsCenterFragment(Activity activity, int resId) {
		super(resId);
		setActivity(activity);
		mSvcsActivity = (SvcsActivity) activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.svcs_center, null);
		init(v);
		return v;
	}

	private View.OnTouchListener mOnCallTouchListener = new View.OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			int eAction = event.getAction();
			// System.out.println("eAction=: " + eAction + " vId=: " +
			// v.getId());
			if (eAction == MotionEvent.ACTION_DOWN) {
				mViewSvcsCenterCenter.setBackgroundResource(bgData.get(v
						.getId()));
				v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
						HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);

				// v.playSoundEffect(SoundEffectConstants.CLICK);
			}
			mViewSvcsCenterCenter.setPressed(true);
			if (eAction == MotionEvent.ACTION_UP
					|| eAction == MotionEvent.ACTION_CANCEL) {
				if (eAction == MotionEvent.ACTION_UP) {
					int viewId = v.getId();
					if (viewId != R.id.svcs_center_index_1
							&& viewId != R.id.svcs_center_index_3) {
						// 检测手机号码//
						if (checkPhoneNumber()) {
							JocData.CallInfo callInfo = (JocData.CallInfo) v
									.getTag();
							mCallCenterHelper.onCalling(callInfo.callType,
									callInfo.callNumber, null);
						} else {
							ToastShow.show(mSvcsActivity, "请先设置手机号码，再进行相关操作！");
							// 转到设置手机号码页面
							Intent intent = new Intent();
							intent.setClass(mSvcsActivity,
									MoreTelephoneActivity.class);
							mSvcsActivity.startActivity(intent);
						}
					} else
					// if (viewId == R.id.svcs_center_index_1) {
					// } else if (viewId == R.id.svcs_center_index_3)
					{
						// mSvcsActivity.showRecordFragment();
						Intent intent = new Intent();
						intent.setClass(mSvcsActivity, NearActivity.class);
						intent.putExtra(NearActivity.ARGS_OPTION_ID, viewId);
						mSvcsActivity.startActivity(intent);
					}
				}
				mViewSvcsCenterCenter.setPressed(false);
			}
			return false;
		}
	};

	private boolean checkPhoneNumber() {
		String strPhoneNumber = GlobalApplication
				.getPhoneNumberFromShare(mSvcsActivity);
		if (strPhoneNumber == null || strPhoneNumber.trim().equals("")) {
			return false;
		}
		return true;
	}

	private void init(View v) {

		mCallCenterHelper = new CallCenterHelper(v.getContext());

		mViewSvcsCenterCenter = v.findViewById(R.id.svcs_center_center);
		RelativeLayout centerLayout = (RelativeLayout) v
				.findViewById(R.id.svcs_center_layout);

		// centerLayout.getc
		for (int i = 0; i < 5; i++) {
			int index = i + pageIndex * 5;
			JocData.CallInfo callInfo = JocData.getCallInfoList().get(index);
			LinearLayout llayout = (LinearLayout) centerLayout
					.getChildAt(i + 1);
			ImageView iv = (ImageView) llayout.getChildAt(0);
			TextView tv = (TextView) llayout.getChildAt(1);

			llayout.setTag(callInfo);
			llayout.setOnTouchListener(mOnCallTouchListener);
			iv.setBackgroundResource(imgArr[callInfo.iconId]);
			tv.setText(callInfo.name);
		}
	}

	public void setPageIndex(int index) {
		this.pageIndex = index;
	}

	static Integer[] imgArr = new Integer[12];
	static {
		imgArr[0] = R.drawable.svcs_center_navi;
		imgArr[1] = R.drawable.svcs_center_ticket;
		imgArr[2] = R.drawable.svcs_center_hostel;
		imgArr[3] = R.drawable.svcs_center_food;
		imgArr[4] = R.drawable.svcs_center_traffic;
		imgArr[5] = R.drawable.svcs_center_navi;
		imgArr[6] = R.drawable.svcs_center_upkeep;
		imgArr[7] = R.drawable.svcs_center_repair;
		imgArr[8] = R.drawable.svcs_center_drive;
		imgArr[9] = R.drawable.svcs_center_rescue;
		imgArr[10] = R.drawable.svcs_center_oil;
		imgArr[11] = R.drawable.svcs_center_stop;

	}

	private static HashMap<Integer, Integer> bgData;
	static {
		bgData = new HashMap<Integer, Integer>();
		bgData.put(R.id.svcs_center_index_0, R.drawable.svcs_center_center_0);
		bgData.put(R.id.svcs_center_index_1, R.drawable.svcs_center_center_1);
		bgData.put(R.id.svcs_center_index_2, R.drawable.svcs_center_center_2);
		bgData.put(R.id.svcs_center_index_3, R.drawable.svcs_center_center_3);
		bgData.put(R.id.svcs_center_index_4, R.drawable.svcs_center_center_4);
	}

}
