package com.icalinks.mobile.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.db.dal.UserDal;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/**
 * 
 * @author guogzhao
 * 
 */
public class MoreReActPwdActivity extends BaseActivity implements
		OnCallbackListener {
	private Context mContext;
	private Button back;
	private EditText orEditText;
	private EditText newEditText;
	private EditText reEditText;
	// private EditText saveEditText;
	private Button save;
	private EditText oldEditText;
	// private UserInfo mUserInfo;
	private ActionBar mActionBar;
	// private UserDal userDal;
	private String mUsername;
	private String mPassword;
	private String mNewPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_recarpwd);
		initArgs();
		initView();
	}

	private void initArgs() {
		Bundle bundle = getIntent().getExtras();
		mUsername = bundle.getString(MoreActMgrActivity.ARGS_USERNAME);
		mPassword = bundle.getString(MoreActMgrActivity.ARGS_PASSWORD);
	}

	private void initView() {
		mActionBar = (ActionBar) findViewById(R.id.more_recarpwd_actionbar);
		mActionBar.setTitle(R.string.more_recarpwd_title);
		back = mActionBar.showBackButton();
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MoreReActPwdActivity.this.finish();
			}
		});

		save = mActionBar.showButton("保存");
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});

		orEditText = (EditText) findViewById(R.id.originalpassword_et);

		newEditText = (EditText) findViewById(R.id.input_newpassword_et);
		reEditText = (EditText) findViewById(R.id.input_repasswords_et);
		oldEditText = (EditText) findViewById(R.id.originalpassword_et);
		oldEditText.setFocusable(true);
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
		if (mActionBar.getProgressBarVisibility() == View.GONE) {
			String oldpassword = orEditText.getText().toString();
			mNewPassword = newEditText.getText().toString();
			String renpassword = reEditText.getText().toString();

			if (oldpassword.equals("")) {
				Toast.makeText(MoreReActPwdActivity.this, "原始密码不能为空!", 0)
						.show();
				return;
			}

			if (mNewPassword.length() < 6) {
				Toast.makeText(MoreReActPwdActivity.this, "密码长度至少6位!", 0)
						.show();
				return;
			}
			if (mNewPassword.length() > 16) {
				Toast.makeText(MoreReActPwdActivity.this, "密码长度不超过16位!", 0)
						.show();
				return;
			}

			if (!mNewPassword.equals(renpassword)) {
				Toast.makeText(MoreReActPwdActivity.this, "两次密码不一致，请重新输入!", 0)
						.show();
				return;
			}

			mActionBar.setProgressBarVisibility(View.VISIBLE);
			// 保存新密码
			OBDHelper
					.updatePassword(mUsername, oldpassword, mNewPassword, this);
		}
	}

	private static final int WHAT_FAILURE = 0;
	private static final int WHAT_SUCCESS = 1;
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_FAILURE:
				onHandlerFailure(msg.obj);
				break;
			case WHAT_SUCCESS:
				Object obj = null;
				try {
					if (msg.obj != null) {
						obj = ((Result) msg.obj).object;
					}
				} catch (Exception ex) {
				}
				onHandlerSuccess(obj);
				break;
			}

		};
	};

	protected void onHandlerFailure(Object obj) {
		mActionBar.setProgressBarVisibility(View.GONE);
		Toast.makeText(this, ((Result) obj).head.resMsg, Toast.LENGTH_SHORT)
				.show();
	}

	protected void onHandlerSuccess(Object obj) {
		mActionBar.setProgressBarVisibility(View.GONE);
		Toast.makeText(MoreReActPwdActivity.this, "密码修改成功!", 0).show();
		UserDal.getInstance(this).updatePassword(mUsername, mNewPassword);
		setResult(RESULT_OK);
		finish();
	}

	public void onFailure(Result result) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_FAILURE, result));
	}

	public void onSuccess(Result result) {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_SUCCESS, result));
	}
}
// {
// // 更新内存中的账号密码
// mUserInfo.pswd = mNewPassword;
// // 更新数据库中的账号密码
//
// VehicleInfo vehicleInfo = GlobalApplication.getVehicleInfo();
// if (vehicleInfo != null) {
//
// UserDal.getInstance(this).update(vehicleInfo.getLicensePlate(),
// vehicleInfo.getPassword(), vehicleInfo.getLoginName(),
// vehicleInfo.getVid());
// }
// }
