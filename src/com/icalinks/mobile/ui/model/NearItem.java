package com.icalinks.mobile.ui.model;

import java.text.DecimalFormat;

import org.json.JSONObject;

import com.baidu.mapapi.CoordinateConvert;
import com.baidu.mapapi.GeoPoint;
import com.icalinks.common.MapsHelper;

public class NearItem {
	// public NearItem(JSONObject json) {
	// super();
	// init(json, null);
	// }

	public NearItem(JSONObject json, GeoPoint reflocation) {
		super();
		init(json, reflocation);
	}

	private void init(JSONObject json, GeoPoint reflocation) {
		try {
			setName(json.getString("name"));
			setAddress(json.getString("address"));
			setLocation(json.getJSONObject("location"));
			if (reflocation != null) {
				setDistance(reflocation);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public NearItem() {
		super();
	}

	private int distance;

	public int getDistance() {
		return distance;
	}

	public void setDistance(GeoPoint reflocation) {
		distance = (int) MapsHelper.getDistance(reflocation, location);
	}

	private String name;
	private String address;
	private GeoPoint location;

	// private int

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(JSONObject location) {
		try {
			this.location = new GeoPoint(
					(int) (location.getDouble("lat") * 1e6),
					(int) (location.getDouble("lng") * 1e6));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// private String uid;
	// private String detail_url;
	// private String tag;

	// public String getUid() {
	// return uid;
	// }
	//
	// public void setUid(String uid) {
	// this.uid = uid;
	// }
	//
	// public String getDetail_url() {
	// return detail_url;
	// }
	//
	// public void setDetail_url(String detail_url) {
	// this.detail_url = detail_url;
	// }
	//
	// public String getTag() {
	// return tag;
	// }
	//
	// public void setTag(String tag) {
	// this.tag = tag;
	// }
}
// "uid": "361b31bf3b030b213586f49d",
// "detail_url":
// "http://api.map.baidu.com/place/detail?uid=361b31bf3b030b213586f49d&output=html&source=placeapi",
// "address": "北环大道",
// "location": {
// "lng": 113.97018,
// "lat": 22.558952
// },
// "tag": "",
// "name": "加德士加油站"