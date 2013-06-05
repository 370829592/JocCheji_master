package com.icalinks.mobile.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.db.dal.UserDal;
import com.icalinks.mobile.util.ToastShow;
import com.icalinks.obd.vo.VehicleInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

/***
 * 
 * @author guogzhao
 * 
 */
public class MoreActMdfActivity extends BaseActivity implements
		OnCallbackListener {

	public static final int RESULT_CODE_SUCCESS = 1;
	private String TAG = "LoginActivity";
	private Button save;
	private Button back;
	private EditText newAccount;
	private ActionBar mActionBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_actmdf);
		initArgs();
		initView();
	}

	private String mUsername;
	private String mNackname;
	private String mPassword;
	private String mNewNackname;

	private void initArgs() {
		Bundle bundle = getIntent().getExtras();
		mUsername = bundle.getString(MoreActMgrActivity.ARGS_USERNAME);
		mNackname = bundle.getString(MoreActMgrActivity.ARGS_NICENAME);
		mPassword = bundle.getString(MoreActMgrActivity.ARGS_PASSWORD);
	}

	private void initView() {
		mActionBar = (ActionBar) findViewById(R.id.more_actmdf_actionbar);
		mActionBar.setTitle(R.string.more_actmdf_title);
		back = mActionBar.showBackButton();
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MoreActMdfActivity.this.finish();
			}
		});

		save = mActionBar.showButton("保存");
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});

		TextView lblName = (TextView) findViewById(R.id.more_actmdf_lbl_name);
		EditText oldAccount = (EditText) findViewById(R.id.account_updateaccount_num_et);
		newAccount = (EditText) findViewById(R.id.account_updateaccount_new_et);

		lblName.setText(mUsername);
		oldAccount.setText(mNackname);
		newAccount.setFocusable(true);
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	public void save() {
		if (View.GONE == mActionBar.getProgressBarVisibility()) {
			mNewNackname = newAccount.getText().toString().trim();
			if (mNewNackname != null && !mNewNackname.equals("")) {
				mActionBar.setProgressBarVisibility(View.VISIBLE);
				OBDHelper.updateLoginName(mNewNackname, mUsername, mPassword,
						this);
			} else {
				ToastShow.show(this, "新账号不能为空！");
			}
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
		Toast.makeText(MoreActMdfActivity.this, ((Result) obj).head.resMsg,
				Toast.LENGTH_SHORT).show();
	}

	protected void onHandlerSuccess(Object obj) {
		mActionBar.setProgressBarVisibility(View.GONE);
		Log.e(TAG, "修改成功！");
		VehicleInfo vehicleInfo = GlobalApplication.getVehicleInfo();

		if (vehicleInfo != null) {
			UserDal.getInstance(this).updateNickname(mUsername, mNewNackname);
			Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
		}
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
// UserDal.getInstance(this).update(vehicleInfo.getLicensePlate(),
// vehicleInfo.getPassword(), vehicleInfo.getLoginName(),
// vehicleInfo.getVid());
