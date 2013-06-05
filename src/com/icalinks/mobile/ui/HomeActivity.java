package com.icalinks.mobile.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;
import com.icalinks.mobile.UpgradeHelper;
import com.icalinks.mobile.db.dal.NaviDal;
import com.icalinks.mobile.db.dal.NaviInfo;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.recver.TabViewRecver;
import com.icalinks.mobile.ui.model.UpgradeInfo;
import com.markupartist.android.widget.ActionBar;
import com.provider.common.config.MessageCenter;
import com.provider.common.util.StrUtils;
import com.provider.common.util.ThreadPoolHelper;
import com.provider.common.util.ToolsUtil;
import com.provider.model.Result;
import com.provider.model.resources.Navigation;
import com.provider.model.resources.NetHelper;
import com.provider.net.listener.OnMessageListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.api.sns.UMSnsService;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengUpdateAgent;
/**
 * 总界面
 * @author Administrator
 *
 */
public class HomeActivity extends TabActivity implements OnMessageListener,
		OnCheckedChangeListener, LocationListener {//
	public static final int ACTIVITY_RESULT_NETWORK = 0;
	public static final int ACTIVITY_RESULT_LOGIN = 1;
	public static final String JP_UPGRADE = "@@UPGRADE";

	static String TAG = HomeActivity.class.getSimpleName();
	private ActionBar mActionBar;
	private RadioGroup mRadioGroup;
	private RadioButton m_main_btn_svcs;
	private RadioButton m_main_btn_info;
//	private RadioButton m_main_btn_rmct;
	private RadioButton m_main_btn_more;
	private TabHost mTabHost;
	public static final String TAB_SVCS = "svcs";
	public static final String TAB_INFO = "info";
//	public static final String TAB_RMCT = "rmct";
	public static final String TAB_MORE = "more";

	private GlobalApplication mApplication;

	// private void reg() {
	//
	//
	// private final OnMessageListener mOnMessageListener = new
	// OnMessageListener() {
	// @Override
	// public void onReceived(Result result) {
	// handleGPSMsg(result);
	// }
	// };
	// }

	protected void onCreate(Bundle savedInstanceState) {

//		umeng();
		checkUpgrade();

		super.onCreate(savedInstanceState);
		// BaseFragment.setMainActivity(this);
		mApplication = GlobalApplication.getApplication();
//		mApplication.setHomeActivity(this);
		mApplication.setWinWidth(this.getWindowManager().getDefaultDisplay()
				.getWidth());
		mApplication.setWinHeight(this.getWindowManager().getDefaultDisplay()
				.getHeight());
		mApplication.startRunning();
		// if (false) {
		// // Log.e(TAG, "开始初始化!");
		// toInitActivity();
		// } else {
		setContentView(R.layout.home);

		// Debug.startMethodTracing();

		mActionBar = (ActionBar) findViewById(R.id.home_actionbar);
		mRadioGroup = (RadioGroup) findViewById(R.id.home_radio);
		mRadioGroup.setOnCheckedChangeListener(this);

		m_main_btn_svcs = (RadioButton) findViewById(R.id.home_btn_svcs);
		m_main_btn_info = (RadioButton) findViewById(R.id.home_btn_info);
//		m_main_btn_rmct = (RadioButton) findViewById(R.id.home_btn_rmct);
		m_main_btn_more = (RadioButton) findViewById(R.id.home_btn_more);

		View tabContent = (FrameLayout) findViewById(android.R.id.tabcontent);
		mActionBar.setRootView(tabContent);

		mTabHost = this.getTabHost();

		TabSpec infoTab = mTabHost.newTabSpec(TAB_INFO);
//		TabSpec rmctTab = mTabHost.newTabSpec(TAB_RMCT);
		TabSpec svcsTab = mTabHost.newTabSpec(TAB_SVCS);
		TabSpec moreTab = mTabHost.newTabSpec(TAB_MORE);
//
//		svcsTab.setIndicator(TAB_SVCS).setContent(
//				new Intent(this, SecretaryGroupTab.class)
//						.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
//		infoTab.setIndicator(TAB_INFO).setContent(
//				new Intent(this, DoctorGroupTab.class)
//						.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
////		rmctTab.setIndicator(TAB_RMCT).setContent(
////				new Intent(this, RmctActivity.class)
////						.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
//		moreTab.setIndicator(TAB_MORE).setContent(
//				new Intent(this, MoreGroupTab.class)
//						.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

		mTabHost.addTab(infoTab);
//		mTabHost.addTab(rmctTab);
		mTabHost.addTab(svcsTab);
		mTabHost.addTab(moreTab);
		// mTabHost.setCurrentTabByTag(TAB_INFO);
		// m_main_btn_info.setCompoundDrawablesWithIntrinsicBounds(null,
		// getResources().getDrawable(R.drawable.home_btn_info_p), null,
		// null);
		m_main_btn_info.setBackgroundResource(R.drawable.doctor_02);
		registerReceiverAsync();

		// }

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 检测是否已登录
		if (mApplication.getCurrUser() == null) {
//			showLoginConfirmDialog(R.id.home_btn_info);
		}

	}


	private void requestPushMessage() {
		// if (mIsRequestPushMessage) {
		GlobalApplication.requestPushMessage(this);
		// }
	}

	// /**
	// * 在百度地图上显示轨迹
	// */
	// public void showRoute(String begTime, String endTime) {
	// mTabHost.setCurrentTabByTag(TAB_RMCT);
	// m_main_btn_info.setCompoundDrawablesWithIntrinsicBounds(null,
	// getResources().getDrawable(R.drawable.home_btn_info_n), null,
	// null);
	// m_main_btn_rmct.setCompoundDrawablesWithIntrinsicBounds(null,
	// getResources().getDrawable(R.drawable.home_btn_rmct_p), null,
	// null);
	//
	// mRmctActivity.showRoute(begTime, endTime);
	// }

	private RmctActivity mRmctActivity;

	public void setRmctActivity(RmctActivity rmctActivity) {
		mRmctActivity = rmctActivity;
	}

	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Debug.stopMethodTracing();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				checkNetworkInitMapManager();
				break;
			case R.id.home_btn_info:
				if (mApplication.getCurrUser() != null) {
					m_main_btn_info.setChecked(true);
					onCheckedChanged(mRadioGroup, requestCode);
				}
				break;
//			case R.id.home_btn_rmct:
//				if (mApplication.getCurrUser() != null) {
//					m_main_btn_rmct.setChecked(true);
//					onCheckedChanged(mRadioGroup, requestCode);
//				}
//				break;

			case MENU_MSGS:
				showMsgsActivity();
				break;
			}
		}
	}

	// .setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
	public void onCheckedChanged(RadioGroup group, int checkedId) {

		// 验证先登录后才允许访问车管家和安防监控
		if (checkedId == R.id.home_btn_info //|| checkedId == R.id.home_btn_rmct
				) {
			// 如果为空，提示到添加账号
			if (mApplication.getCurrUser() == null) {
				switch (checkedId) {
				case R.id.home_btn_info:
//					showLoginConfirmDialog(R.id.home_btn_info);
					break;
//				case R.id.home_btn_rmct:
//					showLoginConfirmDialog(R.id.home_btn_rmct);
//					break;
				}
			}
		}

		m_main_btn_svcs.setBackgroundResource(R.drawable.mishu_01);
		m_main_btn_info.setBackgroundResource(R.drawable.doctor_01);
//		m_main_btn_rmct.setBackgroundResource(R.drawable.home_btn_rmct_n);
		m_main_btn_more.setBackgroundResource(R.drawable.more_01);

		switch (checkedId) {
		case R.id.home_btn_svcs:
			mTabHost.setCurrentTabByTag(TAB_SVCS);
			m_main_btn_svcs.setBackgroundResource(R.drawable.mishu_02);
			// m_main_btn_svcs.setCompoundDrawablesWithIntrinsicBounds(null,
			// getResources().getDrawable(R.drawable.home_btn_svcs_p),
			// null, null);
			break;
		case R.id.home_btn_info:
			mTabHost.setCurrentTabByTag(TAB_INFO);
			m_main_btn_info.setBackgroundResource(R.drawable.doctor_02);
			// m_main_btn_info.setCompoundDrawablesWithIntrinsicBounds(null,
			// getResources().getDrawable(R.drawable.home_btn_info_p),
			// null, null);

			break;
//		case R.id.home_btn_rmct:
//			mTabHost.setCurrentTabByTag(TAB_RMCT);
//			m_main_btn_rmct.setBackgroundResource(R.drawable.home_btn_rmct_p);
			// m_main_btn_rmct.setCompoundDrawablesWithIntrinsicBounds(null,
			// getResources().getDrawable(R.drawable.home_btn_rmct_p),
			// null, null);
//			break;
		case R.id.home_btn_more:
			mTabHost.setCurrentTabByTag(TAB_MORE);
			m_main_btn_more.setBackgroundResource(R.drawable.more_02);
			// m_main_btn_more.setCompoundDrawablesWithIntrinsicBounds(null,
			// getResources().getDrawable(R.drawable.home_btn_more_p),
			// null, null);
			break;
		default:
			break;
		}
	}

	private void showLoginConfirmDialog(final int requestCode) {
		AlertDialog.Builder builder = new Builder(this);
		// builder.setIcon(R.drawable.ic_dialog_alert_holo_light);
		// builder.setTitle(R.string.dialog_nologin_title);
		builder.setMessage(R.string.dialog_nologin_message);
		builder.setNegativeButton(R.string.dialog_nologin_negative,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						intent.setClass(HomeActivity.this,
								MoreActMgrActivity.class);
						startActivityForResult(intent, requestCode);
					}
				});
		builder.setPositiveButton(R.string.dialog_nologin_positive,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});

		builder.show();
	}

	protected void onDestroy() {
		unregisterReceiverSync();
		NetHelper.getInstance().unregister(this);
		super.onDestroy();
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

	private UserInfo mUserInfo;

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

	protected void onResume() {
		mApplication.getMapManager().getLocationManager()
				.requestLocationUpdates(this);
		NetHelper.getInstance().register(MessageCenter.netId.GPS_NAVIGATION,
				this);
		super.onResume();
		MobclickAgent.onResume(this);

		if (!checkNetworkInitMapManager()) {
			Log.e(TAG, "网络不可用!");
			showNetworkDialog();
			// Toast.makeText(this, "网络不可用!", Toast.LENGTH_SHORT).show();
			// this.finish();
		}

		// 刷推送消息
		requestPushMessage();
	}

	protected void onPause() {
		mApplication.getMapManager().getLocationManager().removeUpdates(this);
		super.onPause();
		MobclickAgent.onPause(this);
		// Log.e(TAG, "HomeActivty pause");
	}

	private boolean mIsActionDown;

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				mIsActionDown = true;
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				if (mIsActionDown) {
					showExitConfirmDialog();
				}
				mIsActionDown = false;
			}
			return true;
		} else {
			return false;
		}
	}

	public void showExitConfirmDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		{
			dialog.setTitle(R.string.dialog_exit_title);
			dialog.setMessage(R.string.dialog_exit_message);
			dialog.setPositiveButton(R.string.dialog_exit_positive,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ToolsUtil.closeGPS(HomeActivity.this);
							exit();
							// HomeActivity.this.finish();//
							// _AppManager.exit(BaseActivity.this);
						}
					});
			dialog.setNegativeButton(R.string.dialog_exit_negative, null);
		}
		dialog.show();
	}

	public Intent getContentIntent(Class cls) {
		Intent i = new Intent(this, cls);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	public void umeng() {
		MobclickAgent.onError(this);
		UMFeedbackService.setGoBackButtonVisible();
		UMFeedbackService.enableNewReplyNotification(this,
				NotificationType.AlertDialog);
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		// UmengUpdateAgent.update(this);//启动就不检查友盟更新了

		// // 添加地理位置信息
		UMSnsService.UseLocation = true;
		UMSnsService.LocationAuto = true;
	}

	// private void toInitActivity() {
	// Intent intent = new Intent();
	// intent.setClass(this, InitActivity.class);
	// startActivity(intent);
	// this.finish();
	// }

	private boolean checkNetworkInitMapManager() {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null) {
			flag = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		if (flag) {
			// checkInitMapManagerStart();
			mApplication.onCreateMapManager();
		}
		return flag;
	}

	private void showNetworkDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_nonet_title);
		builder.setIcon(R.drawable.ic_dialog_alert_holo_light);
		builder.setMessage(R.string.dialog_nonet_message);
		builder.setNegativeButton(R.string.dialog_nonet_negative,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_SETTINGS);// Settings.ACTION_WIRELESS_SETTINGS
						HomeActivity.this.startActivityForResult(intent,
								ACTIVITY_RESULT_NETWORK);

					}
				});
		builder.setPositiveButton(R.string.dialog_nonet_positive,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		builder.show();
	}

	// private boolean checkInited() {
	// SharedPreferences preferences = getPreferences(MODE_PRIVATE);
	// boolean isinit = preferences.getBoolean(
	// InitActivity.PREFERENCES_IS_INIT, false);
	// if (!isinit) {
	// Editor editor = preferences.edit();
	// editor.putBoolean(InitActivity.PREFERENCES_IS_INIT, true);
	// editor.commit();
	// }
	// return isinit;
	// }

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

	
	// public void showActionBarButton(final int btnIconResId) {
	//
	// mActionBar.addAction(new Action() {
	//
	// public void performAction(View view) {
	// // Toast.makeText(MainActivity.this, "Added action.",
	// // Toast.LENGTH_SHORT).show();// 锟铰硷拷锟截碉拷
	// }
	//
	// public int getDrawable() {
	// return btnIconResId;
	// }
	// });
	// }
	//
	// public void hideActionBarButton() {
	// int actionCount = mActionBar.getActionCount();
	// if (actionCount > 0)
	// mActionBar.removeActionAt(actionCount - 1);
	// }

	public ActionBar getmActionBar() {
		return mActionBar;
	}

	private TabViewRecver mTabHostRecver;

	private void registerReceiverAsync() {
		ThreadPoolHelper.getInstance().execute(new Runnable() {

			public void run() {
//				mTabHostRecver = new TabViewRecver(HomeActivity.this);
				IntentFilter filter = new IntentFilter();
				{
					filter.addAction(TabViewRecver.ACTION_SETS_TITLE);
					filter.addAction(TabViewRecver.ACTION_START_PROGRESS);
					filter.addAction(TabViewRecver.ACTION_STOP_PROGRESS);
				}
				registerReceiver(mTabHostRecver, filter);
			}
		});

	}

	private void unregisterReceiverSync() {
		if (mTabHostRecver != null)
			unregisterReceiver(mTabHostRecver);
	}

	public void onLocationChanged(Location location) {
		if (location != null) {
			mApplication.setLocation(location);
			Log.e(TAG,
					String.format("您当前的位置:" + "纬度:%f" + "经度:%f",
							location.getLongitude(), location.getLatitude()));
		}
	}

	private void exit() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	public static final int WHAT_SHOW_UPDATE_INFO_DIALOG = 0X0001;
	public static final int WHAT_SHOW_DOWNLOADING_DIALOG = 0X0011;
	public static final int WHAT_UPDATE_DOWNLOAD_PROGESS = 0X0111;
	public static final int WHAT_UPDATE_ASYNC_NOTIFY = 0X1111;

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
//		mUpgradeHelper = new UpgradeHelper(HomeActivity.this);
		new Thread() {
			public void run() {
				// 获取升级信息
				mUpgradeInfo = mUpgradeHelper.getUpgradeInfo();

				if (mUpgradeInfo != null
						&& mUpgradeHelper.isNeedUpgrade(mUpgradeInfo)) {
					mHandler.sendMessage(mHandler
							.obtainMessage(WHAT_SHOW_UPDATE_INFO_DIALOG));
				} else {
					Log.e(TAG, "已经是最新版！");
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

	@Override
	public void onReceived(Result result) {
		if (result.head.resCode == Result.RES_CODE_SUCCESS) {
			Navigation navi = (Navigation) result.object;
			try {
				NaviInfo info = new NaviInfo();
				{
					info.setNaviType(StrUtils.GPS_NAVI_TYPE_DRIVING);
					info.setBegAddrs("");
					info.setBegPoint(new GeoPoint(0, 0));
					info.setEndAddrs(navi.address);
					info.setEndPoint(new GeoPoint((int) (Double
							.parseDouble(navi.latitude) * 1e6), (int) (Double
							.parseDouble(navi.longitude) * 1e6)));
				}
				// 记录到数据库(不插入重发记录)
				NaviInfo dbNaviInfo = NaviDal.getInstance(this).exists(
						info.getEndAddrs());
				if (dbNaviInfo == null) {
					NaviDal.getInstance(this).insert(info);
				} else {
					NaviDal.getInstance(this).update(dbNaviInfo.get_id());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
