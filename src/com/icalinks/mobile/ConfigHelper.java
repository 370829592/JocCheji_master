package com.icalinks.mobile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.ui.model.HostInfo;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author guogzhao
 * 
 */
public class ConfigHelper {

	private Context mContext;
	private Bundle mBundle;
	private static Resources mResources;

	public ConfigHelper() {
		mContext = GlobalApplication.getApplication();
		mResources = mContext.getResources();
		ApplicationInfo appinfo = null;
		try {
			appinfo = mContext.getPackageManager().getApplicationInfo(
					mContext.getPackageName(), PackageManager.GET_META_DATA);
			mBundle = appinfo.metaData;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static final String CP_SERVER_NAME_D = "server_name_d";
	private static final String CP_SERVER_NAME_R = "server_name_r";
	private static final String CP_SERVER_PORT_D = "server_port_d";
	private static final String CP_SERVER_PORT_R = "server_port_r";
	private static final String CP_HESSIAN_URL_D = "hessian_url_d";
	private static final String CP_HESSIAN_URL_R = "hessian_url_r";
	private static final String CP_UPGRADE_URL_D = "upgrade_url_d";
	private static final String CP_UPGRADE_URL_R = "upgrade_url_r";

	private static final String UMENG_CHANNEL = "UMENG_CHANNEL";
	private static final String UMENG_CHANNEL_DEBUG = "debug";

	private String mUmengChannel;

	private boolean isUmengChannelDebug() {
		if (mUmengChannel == null) {
			mUmengChannel = getMetaData(UMENG_CHANNEL);
		}
		return UMENG_CHANNEL_DEBUG.equals(mUmengChannel);
	}

	private static HostInfo mServerHost;

	/**
	 * 获取Socket服务器地址
	 * 
	 * @param activity
	 * @return
	 */
	public HostInfo getServerHost() {
		if (mServerHost == null) {
			mServerHost = new HostInfo();
			if (isUmengChannelDebug()) {
				mServerHost.setName(MobclickAgent.getConfigParams(mContext,
						CP_SERVER_NAME_D));
				mServerHost.setPort(MobclickAgent.getConfigParams(mContext,
						CP_SERVER_PORT_D));
				if (!isValid(mServerHost.getName())
						|| !isValid(mServerHost.getPort())) {
					mServerHost.setName(getString(R.string.cfg_server_name_d));
					mServerHost.setPort(getString(R.string.cfg_server_port_d));
				}
			} else {
				mServerHost.setName(MobclickAgent.getConfigParams(mContext,
						CP_SERVER_NAME_R));
				mServerHost.setPort(MobclickAgent.getConfigParams(mContext,
						CP_SERVER_PORT_R));
				if (!isValid(mServerHost.getName())
						|| !isValid(mServerHost.getPort())) {
					mServerHost.setName(getString(R.string.cfg_server_name_r));
					mServerHost.setPort(getString(R.string.cfg_server_port_r));
				}
			}
		}
		return mServerHost;
	}

	private static String mHessianUrl = null;

	/**
	 * 获取HessianUrl地址
	 * 
	 * @param activity
	 * @return
	 */
	public String getHessianUrl() {
		if (mHessianUrl == null) {
			if (isUmengChannelDebug()) {
				mHessianUrl = MobclickAgent.getConfigParams(mContext,
						CP_HESSIAN_URL_D);
				if (!isValid(mHessianUrl)) {
					mHessianUrl = getString(R.string.cfg_hessian_url_d);
				}
			} else {
				mHessianUrl = MobclickAgent.getConfigParams(mContext,
						CP_HESSIAN_URL_R);
				if (!isValid(mHessianUrl)) {
					mHessianUrl = getString(R.string.cfg_hessian_url_r);
				}
			}
		}
		return mHessianUrl;
	}

	private static String mUpgradeUrl;

	/**
	 * 获取升级服务器地址
	 * 
	 * @param activity
	 * @return
	 */
	public String getUpgradeUrl() {
		if (mUpgradeUrl == null) {
			if (isUmengChannelDebug()) {
				mUpgradeUrl = MobclickAgent.getConfigParams(mContext,
						CP_UPGRADE_URL_D);
				if (!isValid(mUpgradeUrl)) {
					mUpgradeUrl = getString(R.string.cfg_upgrade_url_d);
				}
			} else {
				mUpgradeUrl = MobclickAgent.getConfigParams(mContext,
						CP_UPGRADE_URL_R);
				if (!isValid(mUpgradeUrl)) {
					mUpgradeUrl = getString(R.string.cfg_upgrade_url_r);
				}
			}
		}
		return mUpgradeUrl;
	}

	private boolean isValid(String value) {
		if (value == null || value.trim().equals(""))
			return false;
		else
			return true;
	}

	public String getMetaData(String metaName) {
		if (mBundle != null) {
			Object metaValue = mBundle.get(metaName);
			if (metaValue != null) {
				return metaValue.toString();
			}
		}
		return null;
	}

	public String getString(int resId) {
		return mResources.getString(resId);
	}

}
