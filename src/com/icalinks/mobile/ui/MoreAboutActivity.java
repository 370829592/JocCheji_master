package com.icalinks.mobile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.activity.AbsSubActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class MoreAboutActivity extends AbsSubActivity implements OnClickListener {

	private Button m_btn_back;
	private TextView m_lbl_version;
	private TextView m_lbl_verstatus;
	private TextView m_lbl_update;
	private TextView m_lbl_link;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_about);
	}

	private void init() {
		m_btn_back = showBackButton();
		showBackButton().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goback();
			}
		});

		m_lbl_version = (TextView) findViewById(R.id.more_about_version);
		m_lbl_version.setText(getVersion(getApplicationContext()));

		m_lbl_verstatus = (TextView) findViewById(R.id.more_about_verstatus);
		m_lbl_update = (TextView) findViewById(R.id.more_about_update);
		m_lbl_update.setOnClickListener(this);

		m_lbl_link = (TextView) findViewById(R.id.more_about_link);
		m_lbl_link.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setTitle("更多 - 关于");
		init();
		checkUpdate();
	}

	private UpdateResponse mUpdateResponse;

	private void checkUpdate() {
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					mUpdateResponse = updateInfo;
					mHandler.sendMessage(mHandler
							.obtainMessage(WHAT_HAS_UPDATE));
					break;
				case 1: // has no update
					// Toast.makeText(MoreAboutActivity.this, "没有更新",
					// Toast.LENGTH_SHORT).show();
					mHandler.sendMessage(mHandler
							.obtainMessage(WHAT_NON_UPDATE));
					break;
				case 2: // none wifi
					// Toast.makeText(MoreAboutActivity.this,
					// "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
					mHandler.sendMessage(mHandler
							.obtainMessage(WHAT_NON_UPDATE));
					break;
				case 3: // time out
					Toast.makeText(MoreAboutActivity.this, "获取更新超时，请检查网络连接！",
							Toast.LENGTH_SHORT).show();
					mHandler.sendMessage(mHandler
							.obtainMessage(WHAT_NON_UPDATE));
					break;
				}
			}
		});
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.update(this);
		showProgress();
	}

	// UpgradeInfo mUpgradeInfo;
	//
	// private void checkUpdate2() {
	// Location location = GlobalApplication.getLocation();
	// // new Thread() {
	// // public void run() {
	// // // 获取升级信息
	// // mUpgradeInfo = mUpgradeHelper.getUpgradeInfo();
	// //
	// // if (mUpgradeInfo != null
	// // && mUpgradeHelper.isNeedUpgrade(mUpgradeInfo)) {
	// // mHandler.sendMessage(mHandler
	// // .obtainMessage(WHAT_SHOW_UPDATE_INFO_DIALOG));
	// // }
	// // }
	// // }.start();
	// }

	private static final int WHAT_NON_UPDATE = 0;
	private static final int WHAT_HAS_UPDATE = 1;
	// 验证是不是最新版，如果是就不做操作，如果不是就下载
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			hideProgress();
			switch (msg.what) {
			case WHAT_NON_UPDATE:
				m_lbl_verstatus.setText(R.string.more_about_verstatus_lasted);
				m_lbl_verstatus.setVisibility(View.VISIBLE);
				m_lbl_update.setVisibility(View.GONE);
				break;
			case WHAT_HAS_UPDATE:
				Resources res = MoreAboutActivity.this.getResources();
				m_lbl_verstatus.setText(res
						.getString(R.string.more_about_verstatus_update));
				m_lbl_update.setVisibility(View.VISIBLE);
				break;
			}
		};
	};

	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.more_about_back:
		// this.finish();
		// break;
		case R.id.more_about_link:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://www.calinks.com.cn"));
			startActivity(intent);
			break;
		case R.id.more_about_update:
			// 调用升级
			UmengUpdateAgent.showUpdateDialog(this, mUpdateResponse);
			break;
		}

	}

	public static String getVersion(Context context) {
		String versionNum = "";

		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo pi = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			versionNum = pi.versionName;
			if (versionNum == null || versionNum.length() <= 0) {
				return "程序没有设置版本号";
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionNum;

	}


}
