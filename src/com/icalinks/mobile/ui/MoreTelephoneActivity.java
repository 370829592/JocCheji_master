package com.icalinks.mobile.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

public class MoreTelephoneActivity extends AbsSubActivity {
	private EditText edtPhone;
	private EditText edtVerifyCode;
	private Context mContext;
	private Button mReqVerifyCodeButton;
	// private String mPhoneNumber;
	private boolean isVerify;
	private long mSleepTime = 0;
	private long mSleepDiff;
	private boolean mIsRequest;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_telephone);
		mContext = getApplicationContext();
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setTitle(getString(R.string.gd_bjhmsz));	
		initView();
		initData();
	}

	private void initView() {

		
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});
		showRightButton("保存").setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});

		mReqVerifyCodeButton = (Button) findViewById(R.id.more_btn_get_verify_code);
		mReqVerifyCodeButton.setOnClickListener(mOnClickListener);
		edtVerifyCode = (EditText) findViewById(R.id.more_edt_verify_code);
		edtPhone = (EditText) findViewById(R.id.more_telephone_num_et);
		// edtPhone.setOnEditorActionListener(new
		// TextView.OnEditorActionListener() {
		//
		// public boolean onEditorAction(TextView v, int actionId, KeyEvent
		// event) {
		// //save();
		// return true;
		// }
		// });
	}

	private String mYZGDPhoneNumber;

	private void initData() {
		mYZGDPhoneNumber = GlobalApplication.getPhoneNumber();
		if (mYZGDPhoneNumber != null && !mYZGDPhoneNumber.trim().equals("")) {
			edtPhone.setText(GlobalApplication.getPhoneNumber());
		} else {
			edtPhone.setText(GlobalApplication.getApplication().getVerifyPnum());
		}
		edtPhone.setFocusable(true);
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.more_btn_get_verify_code:
				String mobile = edtPhone.getText() + "";
				requestVerifyCode(mobile);
				break;

			default:
				break;
			}
		}
	};

	private void requestVerifyCode(String mobile) {
		if (mobile == null || mobile.equals("")) {
			Toast.makeText(mContext, "请输入电话号码", Toast.LENGTH_LONG).show();
			return;
		}
		if (mobile.length() != 11) {
			Toast.makeText(mContext, "电话号码为11数字", Toast.LENGTH_LONG).show();
			return;
		}
		mReqVerifyCodeButton.setEnabled(false);

		// long nowTime = System.currentTimeMillis();
		// Log.e("joc", (nowTime - mSleepTime) + "");
		// if (nowTime - mSleepTime < 30 * 1000) {
		// Toast.makeText(mContext, "两次获取验证码间隔时间至少30秒", Toast.LENGTH_LONG)
		// .show();
		// return;
		// }
		// mSleepTime = nowTime;
		// mSleepTime = System.currentTimeMillis();
		// mPhoneNumber = mobile;
		GlobalApplication.getApplication().setVerifyPnum(mobile);
		OBDHelper.getVerifyCodes(mobile, new OnCallbackListener() {

			@Override
			public void onSuccess(Result result) {
				GlobalApplication.getApplication().setVerifyCode(
						"" + result.object);
				Toast.makeText(mContext, "请注意查收短信", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFailure(Result result) {
				Toast.makeText(mContext, result.head.resMsg, Toast.LENGTH_LONG)
						.show();
				mHandler.sendMessage(mHandler.obtainMessage(WHAT_ENABLED_BTN));
			}
		});

		mSleepTime = System.currentTimeMillis();
		mIsRequest = true;
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				do {
					if (mIsRequest) {
						mHandler.sendMessage(mHandler
								.obtainMessage(WHAT_UPDATE_TIME));

						mSleepDiff = System.currentTimeMillis() - mSleepTime;
					}
				} while (mIsRequest && (mSleepDiff < 30 * 1000));
				mHandler.sendMessage(mHandler.obtainMessage(WHAT_ENABLED_BTN));
			};
		}.start();
		// new Timer().schedule(new TimerTask() {
		// @Override
		// public void run() {
		// mHandler.sendMessage(mHandler.obtainMessage());
		// }
		// }, 30 * 1000);
	}

	private void save() {
		String mobile = edtPhone.getText() + "";
		if (mobile.equals("")) {
			Toast.makeText(mContext, "请输入电话号码", Toast.LENGTH_LONG).show();
			return;
		}
		if (mobile.length() != 11) {
			Toast.makeText(mContext, "电话号码为11数字", Toast.LENGTH_LONG).show();
			return;
		}

		String verifyCode = edtVerifyCode.getText() + "";
		if (verifyCode.equals("")) {
			Toast.makeText(mContext, "请输入验证码", Toast.LENGTH_LONG).show();
			return;
		}
		if (!mobile.equals(GlobalApplication.getApplication().getVerifyPnum())) {
			Toast.makeText(mContext, "请重新获取验证码", Toast.LENGTH_LONG).show();
			return;
		}
		// Log.e("joc", verifyCode.toUpperCase() +"=？"+ mVerifyCode);
		if (!verifyCode.toUpperCase().equals(
				GlobalApplication.getApplication().getVerifyCode())) {
			Toast.makeText(mContext, "验证码不正确，请重新输入", Toast.LENGTH_LONG).show();
			return;
		}

		GlobalApplication.savePhoneNumber(mContext, mobile);
		Toast.makeText(mContext, "保存成功", Toast.LENGTH_LONG).show();
		setResult(RESULT_OK);
		MoreTelephoneActivity.this.finish();
	}

	// private void save() {
	// String number = edtPhone.getText().toString().trim() + "";
	// if (number.length() == 11) {
	// if ("".equals(number)) {
	// Toast.makeText(MoreTelephoneActivity.this, "请设置电话号码",
	// Toast.LENGTH_LONG).show();
	// } else {
	// Toast.makeText(MoreTelephoneActivity.this, "保存成功",
	// Toast.LENGTH_LONG).show();
	// MoreTelephoneActivity.this.finish();
	// }
	// GlobalApplication.savePhoneNumber(mContext, number);
	// } else {
	// Toast.makeText(MoreTelephoneActivity.this, "电话号码必须是11数字",
	// Toast.LENGTH_LONG).show();
	// }
	// }
	private final static int WHAT_ENABLED_BTN = 0x0001;
	private final static int WHAT_UPDATE_TIME = 0x0002;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (mReqVerifyCodeButton != null) {
				switch (msg.what) {
				case WHAT_ENABLED_BTN:
					mIsRequest = false;
					mReqVerifyCodeButton.setText("获取验证码");
					mReqVerifyCodeButton.setEnabled(true);
					break;
				case WHAT_UPDATE_TIME:
					mReqVerifyCodeButton.setText("获取验证码" + "("
							+ (30 - mSleepDiff / 1000) + ")");
					break;
				}
			}
		};
	};
}
