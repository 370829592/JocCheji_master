package com.icalinks.mobile.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.util.ToastShow;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

public class MoreReCtlPwdActivity extends BaseActivity implements
		OnCallbackListener {// OnClickListener,

	private final static String XMLNAME = "contorl_password";
	private Button back;
	private EditText orEditText;
	private EditText newEditText;
	private EditText reEditText;
	// private EditText saveEditText;
	private Button save;
	private SharedPreferences sp;
	// private String mOldRmctPassword;
	private EditText oldEditText;
	// private PreferceHelper mPreferceHelper;
	private LinearLayout oldpasswordLinearLayout;
	private LinearLayout newpasswordLinearLayout;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_rectlpwd);

		findId();

		// mSharedPreferences = getSharedPreferences(RMCT_PASSWROD_NAME,
		// MODE_WORLD_READABLE);

		// mOldRmctPassword = getRmctPassword();
		// if (mOldRmctPassword != null) {
		//
		oldpasswordLinearLayout.setVisibility(View.VISIBLE);

		oldEditText.setFocusable(true);
		// } else {
		// newEditText.setFocusable(true);
		// oldpasswordLinearLayout.setVisibility(View.GONE);
		// newpasswordLinearLayout
		// .setBackgroundResource(R.drawable.more_car_loginpassword_up_bg);
		// }
	}

	private ActionBar mActionBar;

	private void findId() {
		mActionBar = (ActionBar) findViewById(R.id.more_rectlpwd_actionbar);
		mActionBar.setTitle(R.string.more_item_rcpswd);
		back = mActionBar.showBackButton();
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MoreReCtlPwdActivity.this.finish();
			}
		});

		save = mActionBar.showButton("保存");
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});

		// back = (Button) findViewById(R.id.car_passwordcon_back_btn);
		// save = (Button) findViewById(R.id.car_password_save_btn);
		// save.setOnClickListener(this);
		// back.setOnClickListener(this);

		orEditText = (EditText) findViewById(R.id.originalpassword_et);
		oldpasswordLinearLayout = (LinearLayout) findViewById(R.id.originalpassword_layout);
		newpasswordLinearLayout = (LinearLayout) findViewById(R.id.newpassword_layout);
		newEditText = (EditText) findViewById(R.id.input_newpassword_et);
		// newEditText.setText(sp.getString("new", null));
		reEditText = (EditText) findViewById(R.id.input_repasswords_et);
		// reEditText.setText(sp.getString("res", null));

		oldEditText = (EditText) findViewById(R.id.originalpassword_et);
		// orLinearLayout.set
		// saveEditText = (EditText) findViewById(R.id.save_passwords_et);

		reEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						save();
						return true;
					}
				});
	}

	public void save() {

		if (!GlobalApplication.isGpsLogin()) {
			ToastShow.show(this, R.string.toast_not_logged_operation_alert);
			return;
		}
		String oldpassword = orEditText.getText().toString();
		String newpassword = newEditText.getText().toString();
		String renpassword = reEditText.getText().toString();

		// if (mOldRmctPassword != null) {
		if (oldpassword.equals("")) {
			Toast.makeText(MoreReCtlPwdActivity.this, "原始密码不能为空!", 0).show();
			return;
		}
		// else if (!oldpassword.equals(mOldRmctPassword)) {
		// Toast.makeText(MoreReCtlPwdActivity.this, "原始密码输入不正确!", 0)
		// .show();
		// return;
		// }
		// }

		if (newpassword.length() == 0 | renpassword.length() == 0) {
			Toast.makeText(MoreReCtlPwdActivity.this, "输入密码输入不正确!", 0).show();

		}

		if (newpassword.length() != 6) {
			Toast.makeText(MoreReCtlPwdActivity.this, "密码必须为6位数字!", 0).show();
			return;
		}
		// if (newpassword.length() > 6) {
		// Toast.makeText(MoreReCtlPwdActivity.this, "密码长度不超过16位!", 0).show();
		// return;
		// }

		// "密码必须为6位数字!"
		try {
			Integer.parseInt(newpassword);
		} catch (Exception e) {
			Toast.makeText(MoreReCtlPwdActivity.this, "密码必须为6位数字!", 0).show();
			return;
		}

		if (!newpassword.equals(renpassword)) {
			Toast.makeText(MoreReCtlPwdActivity.this, "两次密码不一致，请重新输入!", 0)
					.show();
			return;
		}

		// // 保存新密码
		// setRmctPassword(newpassword);
		// Toast.makeText(MoreReCtlPwdActivity.this, "密码设置成功!", 0).show();
		// setResult(1);
		// this.finish();

		UserInfo user = GlobalApplication.getApplication().getCurrUser();
		if (user != null) {// 更改安全卫士密码
			mActionBar.setProgressBarVisibility(View.VISIBLE);
			OBDHelper.updateAFSPassword(user.name, oldpassword, newpassword,
					this);
		} else {
			ToastShow.show(this, R.string.toast_not_logged_operation_alert);
		}
	}

	// private String getString() {
	// String PATH =
	// "/data/data/com.icalinks.jocyjt/shared_prefs/contorl_password.xml";
	// StringBuffer buff = new StringBuffer();
	// try {
	// BufferedReader reader = new BufferedReader(new InputStreamReader(
	// new FileInputStream(PATH)));
	// String str;
	// while ((str = reader.readLine()) != null) {
	// buff.append(str + "\n");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return buff.toString();
	// }

	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.car_passwordcon_back_btn:
	// finish();
	// break;
	// case R.id.car_password_save_btn:
	// save();
	// break;
	// }
	// }

	// public static final String RMCT_PASSWORD_KEY = "rmct_password_key";
	// public static final String RMCT_PASSWROD_NAME = "rmct_passwrod_name";
	//
	// public String getRmctPassword() {
	// String rmctPassword = mSharedPreferences.getString(RMCT_PASSWORD_KEY,
	// null);// RMCT_PASSWORD_VALUE_DEFAULT
	// return rmctPassword;
	// }
	//
	// private void setRmctPassword(String password) {
	// Editor editor = mSharedPreferences.edit();
	// editor.putString(RMCT_PASSWORD_KEY, password);
	// editor.commit();
	// }
	private final static int WHAT_SUCCESS = 0x0001;
	private final static int WHAT_FAILURE = 0x0000;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_SUCCESS:
				mActionBar.setProgressBarVisibility(View.GONE);
				ToastShow.show(MoreReCtlPwdActivity.this, "安防密码设置成功!");
				break;
			case WHAT_FAILURE:
				mActionBar.setProgressBarVisibility(View.GONE);
				ToastShow.show(MoreReCtlPwdActivity.this, "" + msg.obj);
				break;
			}
		};
	};

	@Override
	public void onFailure(Result arg0) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE,
				arg0.head.resMsg));
	}

	@Override
	public void onSuccess(Result arg0) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_SUCCESS, arg0.object));
	}
}
