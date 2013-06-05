package com.calinks.vehiclemachine.vtssocket;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.calinks.vehiclemachine.model.VTSData;
import com.icalinks.mobile.GlobalApplication;

public class VTSClientThread extends Thread {
	private Socket socket;
	private VTSData data;
//	private PrintWriter out;
	private OutputStream out;

	public VTSClientThread() {
		this.data = new VTSData();
		try {
			this.socket = new Socket("61.144.244.100", 5005);
			this.socket.setKeepAlive(true);
			out = socket.getOutputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (GlobalApplication.getApplication().isConn) {
			try {
				sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			writeToVTS(data.getUploadString());
			Log.i("info", data.getUploadString());
		}
	}

	public void writeToVTS(String str) {
		if(out != null){	
			try {
				try {
					out.write(str.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
