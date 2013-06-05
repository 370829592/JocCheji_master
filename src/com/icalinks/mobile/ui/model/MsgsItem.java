package com.icalinks.mobile.ui.model;

public class MsgsItem {
	// 车管家消息：1326
	// 反馈回复99
	public static final int MSGS_TYPE_LOCATION = 0x0000;
	public static final int MSGS_TYPE_FB_RSP = 99;

	public MsgsItem() {
		super();
	}

	// public MsgsItem(String vid,FeedbackInfo info) {
	// super();
	//
	// {
	// this.setVid(vid);
	// this.setDatetime(info.getTime());
	// this.setTitle(info.getTitle());
	// this.setContent(info.getContent());
	// this.setMsgsId("" + info.getId());
	// this.setMsgsType(info.getType());
	// this.setIsread(false);
	// }
	// }

	private int _id;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	private String vid;

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	private int msgsType;
	private String msgsId;
	private boolean isread;
	private String title;
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String datetime;

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public int getMsgsType() {
		return msgsType;
	}

	public void setMsgsType(int type) {
		this.msgsType = type;
	}

	public String getMsgsId() {
		return msgsId;
	}

	public void setMsgsId(String id) {
		this.msgsId = id;
	}

	public boolean isIsread() {
		return isread;
	}

	public void setIsread(boolean isread) {
		this.isread = isread;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
