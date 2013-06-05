package com.icalinks.mobile.ui;

import android.app.Activity;
import android.app.AlertDialog;

import com.icalinks.mobile.widget.CustomProgressDialog;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	// Loading...
	private AlertDialog mLoadingDialog;

	public void showLoadingDialog() {
		if (mLoadingDialog == null) {
			mLoadingDialog = CustomProgressDialog.show(this);
		} else if (!mLoadingDialog.isShowing()) {
			mLoadingDialog.show();
		}
	}

	public void disLoadingDialog() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}
}
