package com.icalinks.mobile.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.ui.activity.MainActivityGroup;

public class MainActivity extends BaseActivity {// BaseActivity
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		GlobalApplication application = (GlobalApplication) getApplication();
		if (application.isRunning()) {
			redirect();
		} else {
			waitRedirect();
		}
	}

	public void redirect() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, MainActivityGroup.class);
		startActivity(intent);
		finish();
	}

	private void waitRedirect() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1984);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (checkInited()) {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MainActivityGroup.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, InitActivity.class);
					startActivity(intent);
				}
				finish();
			};
		}.start();
	}

	// SharedPreferences sharedPreferences = mContext.getSharedPreferences(
	// RMCT_RADIUS_NAME, MODE_WORLD_READABLE);

	private boolean checkInited() {
		SharedPreferences preferences = getApplicationContext()
				.getSharedPreferences(InitActivity.SP_NAME_INIT,
						MODE_WORLD_READABLE);
		boolean isinit = preferences.getBoolean(InitActivity.SP_KEY_IS_INIT,
				false);
		return isinit;
	}
}
