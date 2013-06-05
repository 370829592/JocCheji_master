package com.icalinks.common;

import com.baidu.mapapi.CoordinateConvert;
import com.baidu.mapapi.GeoPoint;

public class MapsHelper {
	/**
	 * 根据地球上两点的经纬坐标计算两点在地球表面上的距离
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static double getDistance(GeoPoint p1, GeoPoint p2) {
		double x1 = p1.getLongitudeE6() / 1e6;
		double y1 = p1.getLatitudeE6() / 1e6;
		double x2 = p2.getLongitudeE6() / 1e6;
		double y2 = p2.getLatitudeE6() / 1e6;

		double d_x1, d_y1, d_x2, d_y2;
		d_x1 = x1;
		d_y1 = y1;
		d_x2 = x2;
		d_y2 = y2;
		double math_pi = 3.1415926535897932384626433832795;
		double mDistance = 0.0;
		double Lon1, Lon2, Lat1, Lat2, LonDist, LatDist;
		double A, C, D, E, U;

		Lon1 = d_x1 * math_pi / 180.0;
		Lon2 = d_x2 * math_pi / 180.0;
		Lat1 = d_y1 * math_pi / 180.0;
		Lat2 = d_y2 * math_pi / 180.0;

		// 计算经差及纬差
		LonDist = Lon1 - Lon2;
		LatDist = Lat1 - Lat2;

		// 以FEET为单位，计算两点之间的距离
		A = Math.sin(LatDist / 2.0) * Math.sin(LatDist / 2.0) + Math.cos(Lat1)
				* Math.cos(Lat2) * Math.sin(LonDist / 2.0)
				* Math.sin(LonDist / 2.0);
		if (Math.sqrt(A) > 1)
			E = 1.0;
		else
			E = Math.sqrt(A);
		C = 2 * Math.atan(E / Math.sqrt(1 - E * E));
		D = (3963 - 13 * Math.sin((Lat1 + Lat2) / 2)) * C;
		mDistance = D * 5280;// 距离，单位为FEET

		// 将单位FEET转换为公里
		mDistance = mDistance * (3048.0 / 10000000.0);
		U = 10.0 * 10.0 * 10.0;
		mDistance = (mDistance * U + (5.0 / 10.0)) / U;

		// ggz:xiuzheng
		mDistance *= U;
		return mDistance;
	}

	/**
	 * 百度坐标近似转换成GPS通用坐标
	 * 
	 * x = 2*x1-x2，y = 2*y1-y2
	 * 
	 * @param baiduGeoPoint
	 * @return
	 */
	public static GeoPoint fromBaiduToWgs84(GeoPoint baiduGeoPoint) {

		GeoPoint g0 = baiduGeoPoint;

		GeoPoint g1 = CoordinateConvert.bundleDecode(CoordinateConvert
				.fromWgs84ToBaidu(g0));
		return new GeoPoint(g0.getLatitudeE6() * 2 - g1.getLatitudeE6(),
				g0.getLongitudeE6() * 2 - g1.getLongitudeE6());
	}
}
