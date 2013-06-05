package com.icalinks.mobile.db.dal;

import com.baidu.mapapi.GeoPoint;

public class NaviInfo {
	public NaviInfo() {
	}
	public NaviInfo(String endAddrs) {
		this.endAddrs = endAddrs;
	}
//	public NaviInfo(String endAddrs, GeoPoint endPoint) {
//		this.endPoint = endPoint;
//		this.endAddrs = endAddrs;
//	}

	private int _id;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	private int naviType;

	public int getNaviType() {
		return naviType;
	}

	public void setNaviType(int naviType) {
		this.naviType = naviType;
	}

	private String datetime;
	private GeoPoint begPoint;
	private String begAddrs;
	private GeoPoint endPoint;
	private String endAddrs;

	public String getBegAddrs() {
		return begAddrs;
	}

	public void setBegAddrs(String begAddrs) {
		this.begAddrs = begAddrs;
	}

	public String getEndAddrs() {
		return endAddrs;
	}

	public void setEndAddrs(String endAddrs) {
		this.endAddrs = endAddrs;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	// public NaviInfo(String datetime, GeoPoint begPoint, GeoPoint endPoint) {
	// super();
	// this.datetime = datetime;
	// this.begPoint = begPoint;
	// this.endPoint = endPoint;
	// }

	public GeoPoint getBegPoint() {
		return begPoint;
	}

	public void setBegPoint(GeoPoint begPoint) {
		this.begPoint = begPoint;
	}

	public GeoPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(GeoPoint endPoint) {
		this.endPoint = endPoint;
	}
}
