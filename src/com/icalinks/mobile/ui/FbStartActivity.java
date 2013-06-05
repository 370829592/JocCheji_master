package com.icalinks.mobile.ui;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.icalinks.common.AppsHelper;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.obd.vo.FeedbackInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

public class FbStartActivity extends BaseActivity implements OnCallbackListener {
	static String TAG = FbStartActivity.class.getName();

	private ActionBar mActionBar;
	private Button mSubmit;
	private Button mViewback;
	private EditText um_fb_content;

	// private Spinner um_fb_age_spinner;
	// private Spinner um_fb_gender_spinner;
	// private TextView um_fb_submit;
	// private TextView umeng_fb_send_feedback_submit;
	// private ImageButton um_fb_see_list_btn;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.fbstart);
		mActionBar = (ActionBar) findViewById(R.id.fbstart_actionbar);
		mActionBar.setTitle("意见反馈");
		mSubmit = mActionBar.showButton("提交");
		mSubmit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {

				if (um_fb_content.getText().toString().trim().equals("")) {
					ToastShow.show(FbStartActivity.this,
							R.string.toast_feedback_null);
					return;
				}

				sendFeedback();
			}
		});

		mViewback = mActionBar.showBackButton();
		mViewback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FbStartActivity.this.finish();
			}
		});

		um_fb_content = (EditText) findViewById(R.id.umeng_fb_content);

		// um_fb_age_spinner = (Spinner)
		// findViewById(R.id.umeng_fb_age_spinner);
		// um_fb_gender_spinner = (Spinner)
		// findViewById(R.id.umeng_fb_gender_spinner);
		// um_fb_submit = (TextView) findViewById(R.id.umeng_fb_submit);
		// umeng_fb_send_feedback_submit = (TextView)
		// findViewById(R.id.umeng_fb_send_feedback_submit);
		//
		// umeng_fb_send_feedback_submit
		// .setOnClickListener(new View.OnClickListener() {
		// public void onClick(View arg0) {
		//
		// if (um_fb_content.getText().toString().trim()
		// .equals("")) {
		// ToastShow.show(FbStartActivity.this,
		// R.string.toast_feedback_null);
		// return;
		// }
		//
		// sendFeedback();
		// }
		// });
		//
		// // um_fb_see_list_btn = (ImageButton)
		// // findViewById(R.id.umeng_fb_see_list_btn);
		// // um_fb_see_list_btn.setOnClickListener(new View.OnClickListener() {
		// // public void onClick(View arg0) {
		// // seeFbTopic();
		// // }
		// // });

	}

	// private void seeFbTopic() {
	//
	// // if (GlobalApplication.isGpsLogin()) {
	// // ToastShow.show(this, R.string.toast_not_logged_operation_alert);
	// // return;
	// // }
	//
	// Intent intent = new Intent();
	// intent.setClass(this, MsgsActivity.class);
	// startActivity(intent);
	// this.finish();
	// }

	private void sendFeedback() {
		FeedbackInfo info = new FeedbackInfo();
		{
			// 反馈内容
			info.setComment("" + um_fb_content.getText());

			// 设备编号
			TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
			info.setDeviceId(tm.getDeviceId());

			// 当前用户
			UserInfo userInfo = GlobalApplication.getApplication()
					.getCurrUser();
			if (userInfo != null) {
				info.setLicenseplate(userInfo.name);
				info.setVid(userInfo.vid);
			}

			// 手机号码
			info.setPhone(GlobalApplication.getPhoneNumber());

			// //* 客户端软件版本号 */
			info.setVersionCode("" + AppsHelper.getVersionCode(this));
			// SDK及系统版本号//* 系统版本 */
			info.setSystemVersion(Build.VERSION.SDK + ","
					+ Build.VERSION.RELEASE);
			// 手机型号
			/* 客户端软件 ID */
			info.setSoftwareId(Build.MODEL);
		}
		mActionBar.setProgressBarVisibility(View.VISIBLE);
		OBDHelper.addFeedback(info, this);
	}


	@Override
	public void onFailure(Result arg0) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE,
				arg0.head.resMsg));
	}

	@Override
	public void onSuccess(Result arg0) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_SUCCESS));
	}

	private static final int WHAT_FAILURE = 0;
	private static final int WHAT_SUCCESS = 1;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_FAILURE:
				ToastShow.show(FbStartActivity.this, "" + msg.obj);
				break;
			case WHAT_SUCCESS:
				ToastShow.show(FbStartActivity.this,
						R.string.toast_feedback_success);
				FbStartActivity.this.finish();
				break;
			}
			mActionBar.setProgressBarVisibility(View.GONE);
		};
	};
}
