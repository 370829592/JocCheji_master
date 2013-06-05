package com.icalinks.mobile.recver;

import android.content.Intent;

import com.icalinks.mobile.GlobalApplication;

public class ActionBarHelper {

	public static void setTitle(CharSequence title) {
		Intent intent = new Intent(TabViewRecver.ACTION_SETS_TITLE);
		intent.putExtra(TabViewRecver.ARGS_TITLE, title);
		sendBroadcast(intent);
	}

//	public static void showProgress() {
//		Intent intent = new Intent(TabViewRecver.ACTION_SHOW_PROGRESS);
//		sendBroadcast(intent);
//	}

//	public static void hideProgress() {
//		Intent intent = new Intent(TabViewRecver.ACTION_HIDE_PROGRESS);
//		sendBroadcast(intent);
//	}

	public static void startProgress() {
		Intent intent = new Intent(TabViewRecver.ACTION_START_PROGRESS);
		sendBroadcast(intent);
	}

	public static void stopProgress() {
		Intent intent = new Intent(TabViewRecver.ACTION_STOP_PROGRESS);
		sendBroadcast(intent);
	}

//	public static void showButton(int btnIconResId) {
//		Intent intent = new Intent(TabViewRecver.ACTION_SHOW_BUTTON);
//		intent.putExtra(TabViewRecver.ARGS_ICON_RES_ID, btnIconResId);
//		sendBroadcast(intent);
//	}
//
//	public static void hideButton() {
//		Intent intent = new Intent(TabViewRecver.ACTION_HIDE_BUTTON);
//		sendBroadcast(intent);
//	}

	private static void sendBroadcast(Intent intent) {
		GlobalApplication.getApplication().sendBroadcast(intent);
	}
}
