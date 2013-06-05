package com.calinks.vehiclemachine.port;

import java.io.IOException;
import java.io.InputStream;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android_serialport_api.SerialPort;

import com.calinks.vehiclemachine.model.PortData;
import com.icalinks.mobile.GlobalApplication;

public class ReadOdbPortThread extends Thread {
	private InputStream mInputStream;

	private boolean cmdStart = false;
	private byte[] data = new byte[128];
	private int dataIndex;

	private Handler mHandler;

	public ReadOdbPortThread(Handler handler) {
		this.mHandler = handler;
		SerialPort port = GlobalApplication.getApplication().getObdPort();
		if (port != null) {
			mInputStream = port.getInputStream();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while (true) {

			if (!GlobalApplication.getApplication().isConn) {
				try {
					sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			byte[] buffer = new byte[1024];
			try {
				if (mInputStream == null) {
					return;
				}
				int size;// = mInputStream.read(buffer);
				while ((size = mInputStream.read(buffer)) != -1) {
					// 星安obd协议
					// analyticalData(buffer, size);
					Message msg = Message.obtain();
					msg.what = 2;
					msg.arg1 = size;
					msg.obj = buffer;
					mHandler.sendMessage(msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	private void getPortData() {
		PortData pData = new PortData();
		pData.length = data[0];
		pData.cmd = Integer.toHexString(data[1]);
		pData.parameters = data;
		Message msg = Message.obtain();
		msg.what = 1;// ��ʾ4�����
		msg.obj = pData;
		this.mHandler.handleMessage(msg);
	}

	private int headFlag = 1;

	/**
	 * 星安obd协议解析方式
	 * 
	 * @param buffer
	 * @param size
	 * @return
	 */
	private void analyticalData(byte[] buffer, int size) {
		for (int i = 0; i < size; i++) {
			if (cmdStart) {
				data[dataIndex] = buffer[i];
				dataIndex++;
				if (data[0] <= 0) {
					cmdStart = false;
				}
				if (dataIndex > data[0]) {
					cmdStart = false;
					getPortData();
				}
				if (dataIndex >= 120) {
					cmdStart = false;
				}
			} else {
				switch (headFlag) {
				case 1:
					if (buffer[i] == (byte) 0x55) {
						headFlag = 2;
					}
					break;
				case 2:
					if (buffer[i] == (byte) 0xaa) {
						cmdStart = true;
						dataIndex = 0;
					}
					headFlag = 1;
					break;
				}
			}
		}

	}
}
