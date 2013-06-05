package com.icalinks.common;

import com.baidu.mapapi.GeoPoint;

public class StringHelper {
	public static GeoPoint getGeoPoint(String geopoint) {
		String[] strs = geopoint.split(",");
		return new GeoPoint(Integer.parseInt(strs[0]),
				Integer.parseInt(strs[1]));
	}

	public static String getGeoPoint(GeoPoint geopoint) {
		return geopoint.getLatitudeE6() + "," + geopoint.getLongitudeE6();
	}
}
