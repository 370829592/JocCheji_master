package com.icalinks.common;

import com.icalinks.jocyjt.R;

public class UrlConfig {
	// 授权Key
	public static final String BAIDU_ANDROID_SDK_MAP_KEY = "793C83B99F9464176D391AEAFF359CD0C2599687";
	public static final String BAIDU_WEB_SERVICE_API_KEY = "d790e940f4afb54344d0d504598841a5";

	public static final int REQ_TYPE_JYZ = R.id.svcs_center_index_1;
	public static final int REQ_TYPE_TCC = R.id.svcs_center_index_3;

	public static String getPoiUrl(int type, double lat, double lon,
			double radius) {

		switch (type) {
		case REQ_TYPE_JYZ:
			return getPoiUrl("%E5%8A%A0%E6%B2%B9%E7%AB%99", lat, lon, radius);
		case REQ_TYPE_TCC:
			return getPoiUrl("%E5%81%9C%E8%BD%A6%E5%9C%BA", lat, lon, radius);
		}
		return null;
	}

	public static String getPoiUrl(String keyword, double lat, double lon,
			double radius) {
		return "http://api.map.baidu.com/place/search?&query=" + keyword
				+ "&location=" + lat + "," + lon + "&radius=" + radius
				+ "&output=json&key=" + BAIDU_WEB_SERVICE_API_KEY;
	}
}