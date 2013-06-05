package com.icalinks.common;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class ScreenHelper {
	private WakeLock mWakeLock;

	/**
	 * 取得锁
	 */
	public void acquireWakeLock(Context context) {
		if (mWakeLock == null) {
			// Logger.d("Acquiring wake lock");
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this
					.getClass().getCanonicalName());
			mWakeLock.acquire();
		}
	}

	/**
	 * 释放锁
	 */
	public void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}

	}

}
