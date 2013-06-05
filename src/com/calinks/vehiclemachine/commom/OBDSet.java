package com.calinks.vehiclemachine.commom;

import android.app.Activity;
import android.util.Log;

import com.calinks.vehiclemachine.port.WriteOdbPort;

public class OBDSet {

		private OBDSet(){
			
		}
	
		// obd����ָ��
		private final static byte[] RESTART_CMD = { 0x55, -0x56, 0x02, 0x38, 0x3a };

		// ��������ָ��
		// private final static String CLEAR_FAULT_CODE = "55aa022426";
		private final static byte[] CLEAR_FAULT_CODE = // "55aa022426"
		{ 0x55, -0x56, 0x02, 0x24, 0x26 };

		// ��ȡ��̬�����
		private final static String READ_DYNAMIC_DATAFLOW = "";

		// �����������
		private final static String SET_DYNAMIC_DATAFLOW_MONITOR = "";
		
		
		
		/**
		 * ���÷�������
		 * 
		 * @param dis
		 * @param ac
		 */
		public static void setDisplacement(Activity ac,float dis) {
			int a = hexStringToInt(dis*10+"");
			int b = 0x03^0x41^a;
			String cs = Integer.toHexString(b);
			if(cs.length()<2){
				cs = "0"+cs;
			}
			WriteOdbPort.from().sendCmdToOdbPort(
					hexStringToBytes("55aa0341"+dis*10+cs));
			// Log.e("info",
			// "��������"+bytesToHexString(hexStringToBytes(SET_DISPLACEMENT_CMD)));
		}
		

		/**
		 * obd����
		 * 
		 * @param ac
		 */
		public static void obdRestart(Activity ac) {
			WriteOdbPort.from().sendCmdToOdbPort(RESTART_CMD);
		}

		/**
		 * ��������
		 * 
		 * @param ac
		 */
		public static void clearFaultCode(Activity ac) {
			WriteOdbPort.from().sendCmdToOdbPort(CLEAR_FAULT_CODE);
			Log.e("info", "�������룺" + bytesToHexString(CLEAR_FAULT_CODE));

		}

		/**
		 * ��ȡ��̬�����
		 * 
		 * @param ac
		 */
		public void readDynamicDataflow(Activity ac) {
			WriteOdbPort.from().sendCmdToOdbPort(
					hexStringToBytes(READ_DYNAMIC_DATAFLOW));
		}

		/**
		 * �����������
		 * 
		 * @param ac
		 */
		public void setDynamicDataflowMonitor(Activity ac) {
			WriteOdbPort.from().sendCmdToOdbPort(
					hexStringToBytes(SET_DYNAMIC_DATAFLOW_MONITOR));
		}

		private static byte[] hexStringToBytes(String hexString) {
			if (hexString == null || hexString.equals("")) {
				return null;
			}

			hexString = hexString.toUpperCase();
			int length = hexString.length() / 2;
			char[] hexChars = hexString.toCharArray();
			byte[] d = new byte[length];
			for (int i = 0; i < length; i++) {
				int pos = i * 2;
				d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
			}
			return d;
		}
		private static byte charToByte(char c) {
			return (byte) "0123456789ABCDEF".indexOf(c);
		}
		private static int hexStringToInt(String str) {
			str = str.toUpperCase();
			int i = charToByte(str.charAt(0)) << 4 | charToByte(str.charAt(1));
			return i;
		}
		private static String bytesToHexString(byte[] src) {
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
}
