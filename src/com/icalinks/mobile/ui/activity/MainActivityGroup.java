package com.icalinks.mobile.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mapapi.LocationListener;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.UpgradeHelper;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.TabViewRecver;
import com.icalinks.mobile.ui.HomeActivity;
import com.icalinks.mobile.ui.MoreAboutActivity;
import com.icalinks.mobile.ui.MsgsActivity;
import com.icalinks.mobile.ui.model.UpgradeInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.common.config.MessageCenter;
import com.provider.common.util.ThreadPoolHelper;
import com.provider.model.Result;
import com.provider.model.resources.NetHelper;
import com.provider.net.listener.OnMessageListener;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: 类名称:MainActivityGroup
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 主界面
 * @version 创建时间：2013-6-4 下午3:12:43
 */
public class MainActivityGroup extends AbsActivityGroup implements OnMessageListener,
 LocationListener{

	private GlobalApplication mApplication;
	private TabViewRecver mTabHostRecver;
	
	private UserInfo mUserInfo;
	private ActionBar mActionBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mApplication = GlobalApplication.getApplication();
		mApplication.setHomeActivity(this);
		mApplication.setWinWidth(this.getWindowManager().getDefaultDisplay()
				.getWidth());
		mApplication.setWinHeight(this.getWindowManager().getDefaultDisplay()
				.getHeight());
		mApplication.startRunning();
		super.onCreate(savedInstanceState);
		mActionBar = (ActionBar) findViewById(R.id.home_actionbar);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		GlobalApplication.requestPushMessage(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiverSync();
		NetHelper.getInstance().unregister(this);
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mApplication.getMapManager().getLocationManager().removeUpdates(this);
		super.onPause();
	}
	
	protected static final int MENU_ABOUT = Menu.FIRST;
	protected static final int MENU_MSGS = Menu.FIRST + 1;
	private static final int MENU_EXIT = Menu.FIRST + 2;

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ABOUT, 0, R.string.menu_about);
		menu.add(0, MENU_MSGS, 0, R.string.menu_msgs);
		menu.add(0, MENU_EXIT, 0, R.string.menu_exit);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ABOUT: {
			Intent intent = new Intent();
			intent.setClass(this, MoreAboutActivity.class);
			startActivity(intent);
		}
			break;
		case MENU_MSGS: {
			showMsgsActivity();
		}
			break;
		case MENU_EXIT:
			exit();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showMsgsActivity() {
		mUserInfo = GlobalApplication.getApplication().getCurrUser();
		if (mUserInfo != null) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.setClass(this, MsgsActivity.class);
			startActivity(intent);
		} else {
//			showLoginConfirmDialog(MENU_MSGS);
		}
	}
	
	@Override
	protected int getLayoutResourceId() {
		// TODO Auto-generated method stub
		return R.layout.home;
	}

	@Override
	protected int[] getRadioButtonIds() {
		// TODO Auto-generated method stub
		return new int[]{
				R.id.home_btn_info,
				R.id.home_btn_svcs,
				R.id.home_btn_more
		};
	}

	@Override
	public Class<? extends Activity>[] getClasses() {
		// TODO Auto-generated method stub
		Class<? extends Activity>[] classes = new Class[]{
				DoctorMenu.class,
				SecretaryMenu.class,
				MoreMenu.class
		};
		return classes;
	}
	private void registerReceiverAsync() {
		ThreadPoolHelper.getInstance().execute(new Runnable() {

			public void run() {
				mTabHostRecver = new TabViewRecver(MainActivityGroup.this);
				IntentFilter filter = new IntentFilter();
				{
					filter.addAction(TabViewRecver.ACTION_SETS_TITLE);
					filter.addAction(TabViewRecver.ACTION_START_PROGRESS);
					filter.addAction(TabViewRecver.ACTION_STOP_PROGRESS);
					// filter.addAction(TabViewRecver.ACTION_SHOW_BUTTON);
					// filter.addAction(TabViewRecver.ACTION_HIDE_BUTTON);
				}
				registerReceiver(mTabHostRecver, filter);
			}
		});
	}
	private void unregisterReceiverSync() {
		if (mTabHostRecver != null)
			unregisterReceiver(mTabHostRecver);
	}


	private void exit() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
	@Override
	public void onReceived(Result arg0) {
		// TODO Auto-generated method stub
		
	}
	public Button showBackButton(){
		return mActionBar.showBackButton();
	}
	
	public void setsActionBarTitle(CharSequence title) {
		mActionBar.setTitle(title);
	}

	public void showActionBarProcess() {
		mActionBar.setProgressBarVisibility(View.VISIBLE);
	}

	public void hideActionBarProcess() {
		mActionBar.setProgressBarVisibility(View.GONE);
	}

	public Button showActionBarButton(String text) {
		return mActionBar.showButton(text);
	}

	public void hideActionBarButton() {
		mActionBar.hideButton();
	}
	
	public void hideBackButton(){
		mActionBar.hideBackButton();
	}
	
	
	public ActionBar getmActionBar() {
		return mActionBar;
	}
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (location != null) {
			mApplication.setLocation(location);
			Log.e("info",
					String.format("您当前的位置:" + "纬度:%f" + "经度:%f",
							location.getLongitude(), location.getLatitude()));
		}
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_SHOW_UPDATE_INFO_DIALOG:
				showUpgradeInfoDialog();
				break;
			case WHAT_SHOW_DOWNLOADING_DIALOG:

				break;
			case WHAT_UPDATE_DOWNLOAD_PROGESS:
				mDownloadingDialog.setProgress(Integer.parseInt("" + msg.obj));
				break;
			case WHAT_UPDATE_ASYNC_NOTIFY:
				checkUpgrade();
				break;
			}
		};
	};

	public void updateDownloadingProgress(int progress) {
		mHandler.sendMessage(mHandler.obtainMessage(
				WHAT_UPDATE_DOWNLOAD_PROGESS, progress));
	}

	private UpgradeHelper mUpgradeHelper;
	private UpgradeInfo mUpgradeInfo;

	private AlertDialog mUpgradeInfoDialog;
	private ProgressDialog mDownloadingDialog;
	private Thread mDownloadingThread;

	private void checkUpgrade() {
		mUpgradeHelper = new UpgradeHelper(MainActivityGroup.this);
		new Thread() {
			public void run() {
				// 获取升级信息
				mUpgradeInfo = mUpgradeHelper.getUpgradeInfo();

				if (mUpgradeInfo != null
						&& mUpgradeHelper.isNeedUpgrade(mUpgradeInfo)) {
					mHandler.sendMessage(mHandler
							.obtainMessage(WHAT_SHOW_UPDATE_INFO_DIALOG));
				} else {
					Log.e("info", "已经是最新版！");
				}
			}
		}.start();
	}

	public void checkUpgradeAsync() {
		mHandler.sendMessage(mHandler.obtainMessage(WHAT_UPDATE_ASYNC_NOTIFY));
	}

	/**
	 * 弹出升级提示对话框
	 */
	private void showUpgradeInfoDialog() {
		AlertDialog.Builder builder = new Builder(this);
		{
			builder.setTitle(mUpgradeInfo.appName + " " + mUpgradeInfo.verName);
			builder.setMessage(mUpgradeInfo.summary);
			builder.setNegativeButton(R.string.dialog_upgrade_negative,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							mUpgradeInfoDialog.dismiss();
							showDownloadingDialog();

							mDownloadingThread = new Thread() {
								@Override
								public void run() {
									mUpgradeHelper.startUpgrade(mUpgradeInfo);
								}
							};
							mDownloadingThread.start();

						}
					});
			if (mUpgradeInfo.upgType == UpgradeInfo.UPGRADE_TYPE_FALSE) {
				builder.setPositiveButton(R.string.dialog_upgrade_positive,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								mUpgradeInfoDialog.dismiss();
							}
						});

			}
		}
		mUpgradeInfoDialog = builder.create();
		mUpgradeInfoDialog
				.setOnKeyListener(new DialogInterface.OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						// 不处理任何键盘事件了
						return true;
					}
				});
		mUpgradeInfoDialog.show();
	}

	/**
	 * 弹出升级下载进度条
	 */
	private void showDownloadingDialog() {

		mDownloadingDialog = new ProgressDialog(this);
		mDownloadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDownloadingDialog.setMax((int) mUpgradeInfo.apkSize);
		mDownloadingDialog.setTitle(R.string.dialog_upgrade_title);
		// mDownloadingDialog.setMessage(R.string.dialog_upgrade_message);

		mDownloadingDialog
				.setOnKeyListener(new DialogInterface.OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						// 不处理任何键盘事件了
						return true;
					}
				});
		mDownloadingDialog.show();
	}
	public static final int WHAT_SHOW_UPDATE_INFO_DIALOG = 0X0001;
	public static final int WHAT_SHOW_DOWNLOADING_DIALOG = 0X0011;
	public static final int WHAT_UPDATE_DOWNLOAD_PROGESS = 0X0111;
	public static final int WHAT_UPDATE_ASYNC_NOTIFY = 0X1111;

}
