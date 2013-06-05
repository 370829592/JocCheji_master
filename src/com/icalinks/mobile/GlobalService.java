//package com.icalinks.mobile;
//
//import java.util.Date;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.util.Log;
//
//import com.provider.common.JocService;
//
//public class GlobalService extends JocService {
//	private static String TAG = GlobalService.class.getSimpleName();
//	private final Timer mTimer = new Timer();
//	private TimerTask mTimerTask = new TimerTask() {
//		@Override
//		public void run() {
//			GlobalApplication.requestPushMessage(GlobalService.this);
//		}
//	};
//
//	private int mHour = 8;
//	private int mMinutes;
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		// // 随机生成一个0~59的数字用来初始化分钟数
//		// mMinutes = (int) (Math.random() * 59);
//		// Log.e(TAG, "mMinutes:" + mMinutes);
//		// mMinutes = 14;
//		// Date date = new Date();
//		// date.setHours(mHour);
//		// date.setMinutes(mMinutes);
//		// // 启动定时器
//		// mTimer.schedule(mTimerTask, date, 3 * 60 * 1000);// 24 * 60 * 60 *
//		mTimer.schedule(mTimerTask, 2 * 60 * 1000); // 1000表示一天的时间(毫秒数)
//	}
//}
