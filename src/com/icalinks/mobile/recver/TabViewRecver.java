package com.icalinks.mobile.recver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.icalinks.mobile.ui.HomeActivity;
import com.icalinks.mobile.ui.activity.MainActivityGroup;

public class TabViewRecver extends BroadcastReceiver {
	public static final String ACTION_SETS_TITLE = "com.icalinks.mobile.recver.TabViewRecver.SetsTitle";
//	public static final String ACTION_SHOW_BUTTON = "com.icalinks.mobile.recver.TabViewRecver.ShowButton";
//	public static final String ACTION_HIDE_BUTTON = "com.icalinks.mobile.recver.TabViewRecver.HideButton";
//	public static final String ACTION_SHOW_PROGRESS = "com.icalinks.mobile.recver.TabViewRecver.ShowProgress";
//	public static final String ACTION_HIDE_PROGRESS = "com.icalinks.mobile.recver.TabViewRecver.HideProgress";
	public static final String ACTION_START_PROGRESS = "com.icalinks.mobile.recver.TabViewRecver.StartProgress";
	public static final String ACTION_STOP_PROGRESS = "com.icalinks.mobile.recver.TabViewRecver.StopProgress";

	// @SuppressLint("ParserError")
	public static final String ARGS_TITLE = "args_title";
	public static final String ARGS_ICON_RES_ID = "btnIconResId";

	private MainActivityGroup mMainActivity;

	public TabViewRecver(MainActivityGroup mainActivity) {
		mMainActivity = mainActivity;
	}

	// @SuppressLint("ParserError")
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle bundle = intent.getExtras();
		if (action.equals(ACTION_SETS_TITLE)) {
			String title = bundle.getString(ARGS_TITLE);
			mMainActivity.setsActionBarTitle(title);
		}
		// else if (action.equals(ACTION_SHOW_PROGRESS)) {
		// mMainActivity.showActionBarProcess();
		// } else if (action.equals(ACTION_HIDE_PROGRESS)) {
		// mMainActivity.hideActionBarProcess();
		// }
		else if (action.equals(ACTION_START_PROGRESS)) {
			mMainActivity.showActionBarProcess();
		} else if (action.equals(ACTION_STOP_PROGRESS)) {
			mMainActivity.hideActionBarProcess();
		}

		// else if (action.equals(ACTION_SHOW_BUTTON)) {
		// int btnIconResId = bundle.getInt(ARGS_ICON_RES_ID);
		// mMainActivity.showActionBarButton(btnIconResId);
		// } else if (action.equals(ACTION_HIDE_BUTTON)) {
		// mMainActivity.hideActionBarButton();
		// }
		else {
			Toast.makeText(context, "收到奇怪的广播", Toast.LENGTH_SHORT).show();
		}
	}
}
