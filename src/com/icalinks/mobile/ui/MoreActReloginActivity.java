package com.icalinks.mobile.ui;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserDal;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.obd.vo.VehicleInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MoreActReloginActivity extends Activity implements
		OnCallbackListener {
	private Button back;
	private Button save;
	private EditText nameEditText;
	private EditText passwordEditText;
	// private Button cancerButton;
	// private Button okButton;
	private Context mContext;
	private UserDal userDal;

	private UserInfo userInfo;
	private String mUsername;
	private String mPassword;
	private String TAG = "MoreAccountUpDataActivity";
	private VehicleInfo vehicleInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_account_relogin);
		mContext = getApplicationContext();
		vehicleInfo = (VehicleInfo) getIntent().getSerializableExtra("name");

		initView();
		findId();
		if (userDal == null) {
			userDal = UserDal.getInstance(mContext);
		}
		userInfo = new UserInfo();

		checkPhoneNumber();

	}

	private ActionBar mActionBar;

	private void findId() {
		mActionBar = (ActionBar) findViewById(R.id.more_account_relogin_actionbar);
		mActionBar.setTitle("重新登录");
		back = mActionBar.showBackButton();
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MoreActReloginActivity.this.finish();
			}
		});

		save = mActionBar.showButton("保存");
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});
		// Login = (Button) findViewById(R.id.more_account_login_btn);
		// Login.setOnClickListener(new Lintener());
		// back = (Button) findViewById(R.id.account_login_back_btn);
		// back.setOnClickListener(new Lintener());

	}

	private void initView() {
		nameEditText = (EditText) findViewById(R.id.account_updata_login_num_et);
		if (vehicleInfo != null) {
			nameEditText.setText(vehicleInfo.getLicensePlate());
		} else {

		}

		passwordEditText = (EditText) findViewById(R.id.account_updata_login_password_et);

		// cancerButton = (Button)
		// findViewById(R.id.account_updata_login_password_no_btn);
		// okButton = (Button)
		// findViewById(R.id.account_updata_login_password_yes_btn);
		// okButton.setOnClickListener(new Listener());
		// cancerButton.setOnClickListener(new Listener());

		if (vehicleInfo != null) {
			nameEditText.setText(vehicleInfo.getLoginName());

			if (vehicleInfo.getLoginName() == null) {
				nameEditText.setText(vehicleInfo.getLicensePlate());
			}
		}

	}

	// protected class Listener implements OnClickListener {
	//
	// @Override
	// public void onClick(View v) {
	//
	// if (v == okButton) {
	// login();
	//
	// // startActivity(intent);
	// // MoreAccountUpDataActivity.this.finish();
	// }
	// if (v == cancerButton) {
	// MoreAccountUpDataActivity.this.finish();
	// }
	// }
	//
	// }

	private boolean checkPhoneNumber() {
		String strPhoneNumber = GlobalApplication.getPhoneNumberFromShare(this);
		if (strPhoneNumber == null || strPhoneNumber.trim().equals("")) {
			return false;
		}
		return true;
	}

	public boolean isUserNameAndPwdValid() {
		nameEditText.setText(nameEditText.getText().toString().trim()
				.toUpperCase());
		if (nameEditText.getText().toString().trim().equals("")) {
			Toast.makeText(this, getString(R.string.account_empty),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public void login() {
		if (checkPhoneNumber()) {
			if (isUserNameAndPwdValid()) {
				mUsername = nameEditText.getText().toString().trim();
				mPassword = passwordEditText.getText().toString().trim();

				OBDHelper.validUser(mUsername, mPassword, this);

			} else {
				ToastShow.show(this, "请先设置手机号码，再进行相关操作！");
				// 转到设置手机号码页面
				Intent intent = new Intent();
				intent.setClass(this, MoreTelephoneActivity.class);
				this.startActivity(intent);
			}
			// VehicleInfo vehicleInfo = GlobalApplication.getVehicleInfo();
			// mUsername = nameEditText.getText().toString().trim();
			// mPassword = passwordEditText.getText().toString();
			// showLoginDialog();
			// OBDHelper.validUser(mUsername, mPassword, this);

		}
	}

	public void onFailure(Result arg0) {
		hideLoginDialog();

		Toast.makeText(MoreActReloginActivity.this, arg0.head.resMsg,
				Toast.LENGTH_SHORT).show();
		Log.e(TAG, arg0.head.resMsg);
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onSuccess(Result arg0) {

		VehicleInfo vehicleInfo = GlobalApplication.getVehicleInfo();

		if (vehicleInfo != null) {
			if (!userDal.exists(vehicleInfo.getVid())) {

				userDal.insert(mUsername, mPassword,
						vehicleInfo.getLoginName(), vehicleInfo.getVid());

			} else {
				userDal.update(vehicleInfo.getLicensePlate(),
						vehicleInfo.getPassword(), vehicleInfo.getLoginName(),
						vehicleInfo.getVid());
			}
		} else {
			Toast.makeText(MoreActReloginActivity.this, "登录失败",
					Toast.LENGTH_SHORT).show();
		}

		setResult(RESULT_OK);
		finish();
		// GlobalApplication.getApplication().setCurrUser(
		// new UserInfo(mUsername, mPassword));

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// Intent intent = new Intent();
			// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			// intent.setClass(LoginActivity.this,
			// MoreAccountManageActivity.class);
			// startActivity(intent);
			finish();
			return true;
		}
		return false;
	}

	Runnable r = new Runnable() {

		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	private void showLoginDialog() {
		mActionBar.setProgressBarVisibility(View.VISIBLE);
		// // LayoutInflater inflater =
		// // LayoutInflater.from(MoreLoginActivity.this);
		// // View layout = inflater.inflate(R.layout.login_loading, null);
		// // AlertDialog.Builder builder = new AlertDialog.Builder(
		// // MoreLoginActivity.this);
		// //
		// // builder.setView(layout);
		// //
		// // mLoginDialog = builder.create();
		// if (mLoginDialog == null) {
		// mLoginDialog = CustomProgressDialog.show(this);
		// } else if (!mLoginDialog.isShowing()) {
		// mLoginDialog.show();
		// }

	}

	private void hideLoginDialog() {
		mActionBar.setProgressBarVisibility(View.GONE);
		// if (mLoginDialog != null) {
		// mLoginDialog.dismiss();
		// mLoginDialog = null;
		// }
	}

}