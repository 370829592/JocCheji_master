package com.icalinks.common;

import java.util.Set;

import com.icalinks.jocyjt.R;
import com.icalinks.mobile.GlobalApplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class TmpsHelper {
	public final static int REQUEST_ENABLE_BT = 111;

	private BluetoothAdapter mBluetoothAdapter;

	private static Activity mAc;


//	private BluetoothChatService mChatService = null;

	public TmpsHelper(Activity ac) {
		this.mAc = ac;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}



	/**
	 * 获取已配对过的设备名�?列表的地址
	 * 
	 * @return
	 */
	public String getPairedDevice() {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals("JETSON TPMS")) {
					return device.getAddress();
				}
			}
		}
		return null;

	}

	/**
	 * 根据地址获取设备对象
	 * 
	 * @param device
	 */
	public BluetoothDevice getDevice(String address) {
		// Attempt to connect to the device

		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		return device;
	}

	// 扫描蓝牙设备
	public void scanBluetoothDevices() {
		// If we're already discovering, stop it
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		mBluetoothAdapter.startDiscovery();
	}

	//本地设备有没有蓝牙模块
	public boolean haveBluetooth() {
		if (mBluetoothAdapter == null) {
			return false;
		}
		return true;
	}

	public void stopScan() {
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
	}



	public boolean isEnabled() {
		return mBluetoothAdapter.isEnabled();
	}

}