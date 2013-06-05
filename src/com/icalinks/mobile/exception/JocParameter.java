package com.icalinks.mobile.exception;

import java.io.File;

import android.os.Environment;

/**
 * 
 * @author zg_hu@calinks.com.cn
 * 
 *         version2.1: share preference file name
 * 
 */
public class JocParameter {
	//
	public static final String FILENAME_OIL = "youjia";
	public static final String PAY_OIL = "youjia";
	public static final String FILENAME_4S = "4Snumber";
	public static final String FORTH_S = "4s";

	public static boolean exitsSd() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)){
			return true;
		} else {
			return false;
		}
	}
	
	// sd
	public static final String getSDPath() {
		File sdcardDir = Environment.getExternalStorageDirectory();
		return sdcardDir.getPath();
	}

	// font check
	public static final String JOCPATH = "/joc";
	public static final String FONTS = "/fonts";
	public static final String LOG = "/log";
	public static final String FONT_LV = "lvshu.ttf";
	public static final String FONT_LCD = "lcd.ttf";
	
	public static final String LV_FONTPATH = getSDPath() + JOCPATH + FONTS + "/" + FONT_LV;
	public static final String LCD_FONTPATH = getSDPath() + JOCPATH + FONTS + "/" + FONT_LCD;
	public static final String LOG_PATH = getSDPath() + JOCPATH + LOG + "/";
}
