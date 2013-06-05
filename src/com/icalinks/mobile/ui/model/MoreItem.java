package com.icalinks.mobile.ui.model;

public class MoreItem {
	private int iconResId;
	private int textResId;
	private Class<?> cls;

	public Class<?> getCls() {
		return cls;
	}

	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public int getTextResId() {
		return textResId;
	}

	public MoreItem(int textResId, int iconResId, Class<?> cls) {
		super();
		this.textResId = textResId;
		this.iconResId = iconResId;
		this.cls = cls;
	}

	public void setTextResId(int textResId) {
		this.textResId = textResId;
	}
}
