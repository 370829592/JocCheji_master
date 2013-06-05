package com.calinks.vehiclemachine.port;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.util.Log;
import android_serialport_api.SerialPort;

import com.icalinks.mobile.GlobalApplication;

public class WriteOdbPort {
	private static OutputStream mOutputStream;
	
	private static WriteOdbPort my = null;
	
	private WriteOdbPort(){
		SerialPort port =  GlobalApplication.getApplication().getObdPort();
		if(port != null)
		mOutputStream = port.getOutputStream();
	}
	
	public static WriteOdbPort from(){
		if(my == null){
			my = new WriteOdbPort();
		}
		return my;
	}
	/**
	 * 
	 * @param cmd  16�����ֽ����� 
	 */
	public  void sendCmdToOdbPort(byte[] cmd){
		if(mOutputStream != null){
			try {
				mOutputStream.write(cmd);
				mOutputStream.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
}
