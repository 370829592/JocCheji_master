package com.icalinks.mobile;

public class UpgradeStatus {

	/**
	 * 没有新版本
	 */
	public static final int NON_NEW = 0x0000; // 没有更新

	/**
	 * 已有新版本
	 */
	public static final int HAS_NEW = 0x1001;

	/**
	 * 请求超时
	 */
	public static final int TIME_OUT = 0xFFFF;// 获取更新超时，请检查网络连接！

	/**
	 * 没有连接WIFI
	 */
	public static final int NON_WIFI = 0x1000;// 没有wifi连接， 只在wifi下更新
}