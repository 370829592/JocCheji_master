package com.icalinks.mobile.ui.model;

public class RmctRecordInfo {
	private String name;
	private String time;
	private String flag;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public RmctRecordInfo(String name, String time, String flag) {
		super();
		this.name = name;
		this.time = time;
		this.flag = flag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

}
