package com.icalinks.mobile.util;

import android.content.Context;
import android.widget.Toast;

public class ToastShow {

	public static void show(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void show(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
}
