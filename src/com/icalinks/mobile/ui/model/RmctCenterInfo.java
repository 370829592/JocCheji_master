package com.icalinks.mobile.ui.model;

public class RmctCenterInfo {
	/**
	 * 名称
	 */
	private int nameResId;

	/**
	 * 图标
	 */
	private int iconResId;
	/**
	 * 图标,不可用状态
	 */
	private int iconNonId;

	/**
	 * 当前是否激活态(比如油电是开着的)
	 */
	private boolean activated;

	private boolean enabled;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public int getIconNonId() {
		return iconNonId;
	}

	public void setIconNonId(int iconNonId) {
		this.iconNonId = iconNonId;
	}

	/**
	 * 指令代码
	 */
	private int rmctCmdId;

	public int getNameResId() {
		return nameResId;
	}

	public void setNameResId(int nameResId) {
		this.nameResId = nameResId;
	}

	public RmctCenterInfo(int rmctCmdId, int nameResId, int iconResId,
			int iconNonId) {
		super();
		this.rmctCmdId = rmctCmdId;
		this.nameResId = nameResId;
		this.iconResId = iconResId;
		this.iconNonId = iconNonId;
	}

	public int getIconResId() {
		return iconResId;
	}

	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}

	public int getRmctCmdId() {
		return rmctCmdId;
	}

	public void setRmctCmdId(int rmctCmdId) {
		this.rmctCmdId = rmctCmdId;
	}
}
