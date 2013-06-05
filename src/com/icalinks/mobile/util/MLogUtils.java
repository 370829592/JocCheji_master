package com.icalinks.mobile.util;

import android.util.Log;

/**
 * @ClassName: �����:MLogUtils
 * @author ���� E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO
 * @version ����ʱ�䣺2013-5-17 ����1:56:18
 */
public class MLogUtils {
	public static void printIMsg(String msg){
		Log.i("info", msg);
	}
	public static void printWMsg(String msg){
		Log.w("info", msg);
	}
	public static void printEMsg(String msg){
		Log.e("info", msg);
	}
	
}
