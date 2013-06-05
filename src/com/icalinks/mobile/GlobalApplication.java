package com.icalinks.mobile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;
import android_serialport_api.SerialPort;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapPointInfo;
import com.calinks.vehiclemachine.commom.OBDProc;
import com.calinks.vehiclemachine.vtssocket.VTSClientThread;
import com.icalinks.jocyjt.R;
import com.icalinks.mobile.db.dal.UserInfo;
import com.icalinks.mobile.exception.JocCrashHandler;
import com.icalinks.mobile.exception.JocParameter;
import com.icalinks.mobile.ui.HomeActivity;
import com.icalinks.mobile.ui.PathActivity;
import com.icalinks.mobile.ui.activity.MainActivityGroup;
import com.icalinks.mobile.ui.adapter.MsgsHelper;
import com.icalinks.obd.vo.VehicleInfo;
import com.provider.common.JocApplication;
import com.provider.model.Result;
import com.provider.model.resources.OBDHelper;
import com.provider.net.listener.OnCallbackListener;

public class GlobalApplication extends JocApplication implements
		MKGeneralListener {
	public static final String TAG = GlobalApplication.class.getSimpleName();

	private static GlobalApplication mApplication;
	private JocCrashHandler mJocCrashHandler;

	public static GlobalApplication getApplication() {
		return mApplication;
	}

	private final Timer mTimer = new Timer();
	private TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			requestPushMessage(getHomeActivity());
		}
	};

	private int mHour = 8;
	private int mMinutes;

	private void initMsgsPush() {
		// 随机生成一个0~59的数字用来初始化分钟数
		mMinutes = (int) (Math.random() * 59);
		Date date = new Date();
		date.setHours(mHour);
		date.setMinutes(mMinutes);
		// 启动定时器
		mTimer.schedule(mTimerTask, 24 * 60 * 60 * 1000); // 1000表示一天的时间(毫秒数)
	}

	public static void requestPushMessage(Context context) {
		if (GlobalApplication.isGpsLogin()) {
			UserInfo user = mApplication.getCurrUser();
			if (user != null) {
				MsgsHelper.requestPushMessage(context, user);
			}
		}
	}

	private BMapManager mMapManager = null;// 百度MapAPI的管理类
	private static final String mMapKey = "793C83B99F9464176D391AEAFF359CD0C2599687"; // 授权Key

	@Override
	public void onCreate() {
		initJgPush();
		initMsgsPush();
		mApplication = this;


		mMapManager = new BMapManager(this);
		mMapManager.init(mMapKey, this);
		mMapManager.getLocationManager().setNotifyInternal(10, 5);

		// 根据渠道(测试/发布)设置服务器地址地址
		setDebug(false);
		setSoundId(R.raw.alert618);
		setDialogLayoutId(R.layout.dialog_act);

		String strServerName = new ConfigHelper().getServerHost().getName();
		Log.e(TAG, "server_name:" + strServerName);
		super.setCurGpsServerIp(strServerName);

		initParam();
		
		
		
		//登陆
//		login();
		
		super.onCreate();
	}

	@Override
	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
		if (mMapManager != null) {
			mMapManager.destroy();
			mMapManager = null;
		}
		super.onTerminate();
	}


	public BMapManager getMapManager() {
		return mMapManager;
	}

	public void onPauseMapActivity() {
		mApplication.getMapManager().stop();
	}

	public void onResumeMapActivity() {
		mApplication.getMapManager().start();
	}

	public void onCreateMapManager() {
		if (mMapManager == null) {
			mMapManager = new BMapManager(getApplication());
			mMapManager.init(mMapKey, this);
		}
		mApplication.getMapManager().start();
	}

	public void onCreateMapActivity(MapActivity mapActivity) {
		// 如果使用地图SDK，请初始化地图Activity
		long iTime = System.nanoTime();
		mapActivity.initMapActivity(mMapManager);
		iTime = System.nanoTime() - iTime;
		Log.d(TAG, "initMapActivity time:" + iTime);
	}

	// private UserInfo mPrevUserInfo;
	private UserInfo mCurrUserInfo;

	/**
	 * 获取当前用户
	 * 
	 * @return
	 */
	public UserInfo getCurrUser() {
		if (isGpsLogin()) {
			setCurrUser(getVehicleInfo());
			return mCurrUserInfo;
		} else {
			return mCurrUserInfo = null;
		}
	}

	public void setCurrUser(VehicleInfo vehicleInfo) {
		if (vehicleInfo != null) {
			mCurrUserInfo = new UserInfo(vehicleInfo.getLicensePlate(),
					vehicleInfo.getPassword(), vehicleInfo.getVid(),
					vehicleInfo.getLoginName());
		} else {
			mCurrUserInfo = null;
		}
	}

	private boolean mIsRunning;

	public boolean isRunning() {
		return mIsRunning;
	}

	public void startRunning() {
		this.mIsRunning = true;
	}

	private int winWidth;
	private int winHeight;

	public void setWinWidth(int winWidth) {
		this.winWidth = winWidth;
	}

	public void setWinHeight(int winHeight) {
		this.winHeight = winHeight;
	}

	public int getWinWidth() {
		return winWidth;
	}

	public int getWinHeight() {
		return winHeight;
	}

	private MainActivityGroup mHomeActivity;

	public MainActivityGroup getHomeActivity() {
		return mHomeActivity;
	}

	public void setHomeActivity(MainActivityGroup homeActivity) {
		this.mHomeActivity = homeActivity;
	}

	public void showRoutes(String begTime, String endTime, String title) {
		Intent intent = new Intent();
		{
			intent.setClass(mHomeActivity, PathActivity.class);
			intent.putExtra(PathActivity.ARGS_BEG_DATE, begTime);
			intent.putExtra(PathActivity.ARGS_END_DATE, endTime);
			intent.putExtra(PathActivity.ARGS_TOP_NAME, title);
		}
		mHomeActivity.startActivity(intent);
	}

	private MapPointInfo mCarMapPointInfo = new MapPointInfo();


	public void setCarPosition(GeoPoint gps, String str) {
		mCarMapPointInfo.setGps(gps);
		mCarMapPointInfo.setStr(str);
	}

	public MapPointInfo getCarPosition() {
		return mCarMapPointInfo;
	}

	public void onGetNetworkState(int iError) {
		Log.d("MapKeyGeneralListener", "onGetNetworkState error is " + iError);
		// Toast.makeText(GlobalApplication.getApplication().getApplicationContext(),
		// "您的网络出错啦！", Toast.LENGTH_LONG).show();
	}

	public void onGetPermissionState(int iError) {
		Log.d("MapKeyGeneralListener", "onGetPermissionState error is "
				+ iError);
		if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
			// 授权Key错误:
			Toast.makeText(
					GlobalApplication.getApplication().getApplicationContext(),
					"请在BMapApiDemoApp.java文件输入正确的授权Key！", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void initJgPush() {
		JPushInterface.setDebugMode(false);// true
		JPushInterface.init(this);
	}

	/**
	 * 初始化一些参数，如：油价
	 */
	private void initParam() {
		SharedPreferences sharedPreferences = getSharedPreferences(
				JocParameter.FILENAME_OIL, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putString(JocParameter.PAY_OIL, "8");

		editor.commit();
	}

	private String mVerifyCode;

	public String getVerifyCode() {
		return mVerifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.mVerifyCode = verifyCode;
	}

	private String mVerifyPnum;

	public String getVerifyPnum() {
		return mVerifyPnum;
	}

	public void setVerifyPnum(String mVerifyPnum) {
		this.mVerifyPnum = mVerifyPnum;
	}
	//新加的
	private SerialPort mSerialPort;

	private OBDProc obd;
	public SerialPort getObdPort() {
		if (mSerialPort == null) {
			try {
				mSerialPort = new SerialPort(new File("/dev/ttyS2"), 9600, 0);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mSerialPort;
	}

	public void closeObdPort() {
		if(obd !=null){
			obd.stopReadOBD();
		}
		
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
	public void startObd(){
		if (obd == null) {
			obd = new OBDProc();
		}
//		obd.startReadOBD();
		isConn = true;
		//准备gps
	}
	public void stopObd(){
		isConn = false;
	}
	
	
	
	public OBDProc getObd(){
		return obd;
	}
	
	public String getUserName(){
		String name = Secure.getString(this
				.getContentResolver(), Secure.ANDROID_ID);
		name = "15013722907";
		return name;
	}
	
	private void login(){
		
		
		OBDHelper.validUser( getUserName(), "123456",
				new OnCallbackListener() {
					
					@Override
					public void onSuccess(Result arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "登录成功",Toast.LENGTH_LONG).show();

					}
					
					@Override
					public void onFailure(Result arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(), "自动登录失败", Toast.LENGTH_LONG).show();
					}
				});
	}
	
	public boolean isConn = false;
	
	
}
