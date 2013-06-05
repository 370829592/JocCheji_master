package com.icalinks.mobile.ui.model;

/**
 * 统计数据详细信息
 * 
 * @author zg_hu@iclinks.com.cn
 * 
 */

public class InfoSsdataItem {
	public String name;
	public String data;
	public String unit;

	public InfoSsdataItem(String name, String data, String unit) {
		this.name = name;
		this.data = data;
		this.unit = unit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
