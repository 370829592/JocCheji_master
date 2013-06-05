package com.icalinks.mobile.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

/***
 * 
 * @author jimmy
 * 
 */
public class MoreLoginActivity extends BaseActivity implements
		OnCallbackListener {
	public static final int RESULT_CODE_SUCCESS = 1;
	private String TAG = "LoginActivity";
	private Button save;
	private Button back;
	private EditText account;
	private EditText password;
	private UserDal userDal;

	private UserInfo userInfo;
	private Handler handler;
	private Context mContext;
	private ActionBar mActionBar;

	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle builder = getIntent().getExtras();
		setContentView(R.layout.more_account_login);
		handler = new Handler();

		mContext = getApplicationContext();
		userInfo = new UserInfo();
		findId();
		// haveInternet();
		if (userDal == null) {
			userDal = UserDal.getInstance(mContext);
		}
		// checkPhoneNumber();
	}

	private void findId() {
		mActionBar = (ActionBar) findViewById(R.id.more_account_login_actionbar);
		mActionBar.setTitle(R.string.more_login_title);
		back = mActionBar.showBackButton();
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MoreLoginActivity.this.finish();
			}
		});

		save = mActionBar.showButton("登录");
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
		// Login = (Button) findViewById(R.id.more_account_login_btn);
		// Login.setOnClickListener(new Lintener());
		// back = (Button) findViewById(R.id.account_login_back_btn);
		// back.setOnClickListener(new Lintener());

		account = (EditText) findViewById(R.id.account_login_num_et);
		account.setFocusable(true);
		password = (EditText) findViewById(R.id.account_login_password_et);
		// password.setOnEditorActionListener(new
		// TextView.OnEditorActionListener() {
		//
		// public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// save();
		// return true;
		// }
		// });
	}

	protected class Lintener implements OnClickListener {

		public void onClick(View v) {
			if (v == save) {
				save();
			} else if (v == back) {
				setResult(RESULT_CANCELED);
				finish();
			}

		}

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

	// private AlertDialog mLoginDialog;

	private void hideLoginDialog() {
		mActionBar.setProgressBarVisibility(View.GONE);
		// if (mLoginDialog != null) {
		// mLoginDialog.dismiss();
		// mLoginDialog = null;
		// }
	}

	private String mUsername;
	private String mPassword;

	public void save() {
		// if (checkPhoneNumber()) {
		if (isUserNameAndPwdValid()) {

			mUsername = account.getText().toString().trim();
			mPassword = password.getText().toString();
			showLoginDialog();

			OBDHelper.validUser(mUsername, mPassword, this);
			// if (!userDal.exists(userInfo.name)) {
			//
			// showLoginDialog();
			// OBDHelper.validUser(mUsername, mPassword, this);
			//
			// } else {
			// Toast.makeText(this, "帐号已经添加", Toast.LENGTH_SHORT).show();
			// // login failed,user does't exist
			// Log.e(TAG, "帐号已经添加");
			// }

		}
	}

	// }

	// private boolean checkPhoneNumber() {
	// String strPhoneNumber = GlobalApplication.getPhoneNumberFromShare(this);
	// if (strPhoneNumber == null || strPhoneNumber.trim().equals("")) {
	// ToastShow.show(this, "请先设置手机号码，再进行添加账号！");
	// return false;
	// }
	// return true;
	// }

	public boolean isUserNameAndPwdValid() {
		account.setText(account.getText().toString().trim().toUpperCase());
		if (account.getText().toString().trim().equals("")) {
			Toast.makeText(this, getString(R.string.account_empty),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// // 判断是否有网络,获得ConnectivityManager来得到激活的网络连接信息:
	// public void haveInternet() {
	// NetworkInfo info = ((ConnectivityManager)
	// getSystemService(Context.CONNECTIVITY_SERVICE))
	// .getActiveNetworkInfo();
	// if (info == null || !info.isConnected()) {
	//
	// Toast.makeText(mContext, "没有网络，请检查设置", 1000);
	//
	// return;
	// }
	// if (info.isConnected()) {
	// Log.e(TAG, "网络已连接");
	// }
	// }

	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onFailure(Result arg0) {
		hideLoginDialog();
		Toast.makeText(MoreLoginActivity.this, arg0.head.resMsg,
				Toast.LENGTH_SHORT).show();
		Log.e(TAG, arg0.head.resMsg);
	}

	@Override
	public void onSuccess(Result arg0) {
		hideLoginDialog();
		VehicleInfo vehicleInfo = GlobalApplication.getVehicleInfo();
		if (vehicleInfo != null) {
			if (!userDal.exists(vehicleInfo.getVid())) {
				userDal.insert(vehicleInfo.getLicensePlate(),
						vehicleInfo.getPassword(), vehicleInfo.getLoginName(),
						vehicleInfo.getVid());
			} else {
				userDal.update(vehicleInfo.getLicensePlate(),
						vehicleInfo.getPassword(), vehicleInfo.getLoginName(),
						vehicleInfo.getVid());
			}
		} else {
			Toast.makeText(MoreLoginActivity.this, "登录失败", Toast.LENGTH_SHORT)
					.show();
		}

		Log.e(TAG, "添加成功！");

		Toast.makeText(MoreLoginActivity.this, R.string.login_sucess,
				Toast.LENGTH_SHORT).show();

		setResult(RESULT_OK);
		finish();
		// GlobalApplication.getApplication().setCurrUser(
		// new UserInfo(mUsername, mPassword));
	}
}
