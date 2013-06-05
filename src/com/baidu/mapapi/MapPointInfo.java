package com.baidu.mapapi;

import com.baidu.mapapi.GeoPoint;

public class MapPointInfo {

	private GeoPoint gps;
	private String str;

	public GeoPoint getGps() {
		return gps;
	}

	// public void set(GeoPoint gps, String str) {
	// // public void set(double lat, double lan, String str) {
	// // if (this.gps == null) {
	// // this.gps = new GeoPoint(lat, lan);
	// // } else {
	// // this.gps.setLatitudeE6((int) (lat * 1e6));
	// // this.gps.setLongitudeE6((int) (lan * 1e6));
	// // }
	// this.str = str;
	// this.gps = gps;
	// }

	public void setGps(GeoPoint gps) {
		this.gps = gps;
	}

//	public void setGps(double lat, double lon) {
//		this.gps = new GeoPoint(lat, lon);
//	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
}
