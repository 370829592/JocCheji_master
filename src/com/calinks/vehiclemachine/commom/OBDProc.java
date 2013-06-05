package com.calinks.vehiclemachine.commom;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.calinks.vehiclemachine.listener.OnCheckedFaultInfoListener;
import com.calinks.vehiclemachine.listener.OnClearFaultListener;
import com.calinks.vehiclemachine.listener.OnGetRealDataListener;
import com.calinks.vehiclemachine.listener.OnTravelListener;
import com.calinks.vehiclemachine.model.FaultInfo;
import com.calinks.vehiclemachine.model.PortData;
import com.calinks.vehiclemachine.model.TravelInfo;
import com.calinks.vehiclemachine.model.VTSData;
import com.calinks.vehiclemachine.model.db.DBConfig;
import com.calinks.vehiclemachine.model.db.JocDBHelper;
import com.calinks.vehiclemachine.port.ReadOdbPortThread;
import com.calinks.vehiclemachine.port.WriteOdbPort;
import com.calinks.vehiclemachine.vtssocket.VTSClientThread;
import com.icalinks.mobile.GlobalApplication;
import com.provider.model.resources.RealTimeOBD;

public class OBDProc {

	private OnClearFaultListener mOnClearFaultListener;
	private OnCheckedFaultInfoListener mOnCheckedFaultInfoListener;
	private OnGetRealDataListener onGetRealDataListener;
	private OnTravelListener onTravelListener;

	private ReadOdbPortThread ropt;
	private RealTimeOBD alldata;
	private JocDBHelper myHelper;
	private TravelInfo travelInfo;

	private float last_oil_consum;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	private FaultInfo fInfo = new FaultInfo();
	private long lastReceiverTime = 0;

	private CheckReceiverDateTime ct;


	public OBDProc() {
		alldata = new RealTimeOBD();
		myHelper = new JocDBHelper(GlobalApplication.getApplication()
				.getApplicationContext());
		ropt = new ReadOdbPortThread(handler);
		ct = new CheckReceiverDateTime();
		ropt.start();
		ct.start();
	}

	/**
	 * ��ʼobd
	 */

	public void stopReadOBD() {
		// if (ropt != null) {
		// ropt.stop();
		// }
		// if (ct != null) {
		// ct.stop();
		// }
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			// /星安处理方法
			case 1:

				PortData pData = (PortData) msg.obj;
				conversionData(pData);
				break;
			// 车胜杰obd协议解析
			case 2:
				int size = msg.arg1;
				byte[] buffer = (byte[]) msg.obj;
				conversionDataCheshengjie(size, buffer);
				Log.i("info", "接收到数据:"+new String(buffer,0,size));
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 车胜杰obd协议
	 * 
	 * @param size
	 * @param buffer
	 */
	private void conversionDataCheshengjie(int size, byte[] buffer) {
		String sData = new String(buffer, 0, size);
		parseData(sData);

	}

	private boolean cmdStart = true;
	private String lastData = "";

	private void parseData(String sData) {
		for (int i = 0; i < sData.length(); i++) {
			if (cmdStart) {
				char c = sData.charAt(i);
				if ('\r' == c) {
					Log.i("info", "总算进来了一次！");

					cmdStart = false;
				} else {
					lastData += c;
				}
			} else {
				Log.i("info", lastData);

				if (lastData.contains("VSS")) {
					float value = stringToFloat(lastData);
					alldata.carSpeed = value + "";
					lastReceiverTime = System.currentTimeMillis();
				} else if (lastData.contains("ECT")) {
					lastReceiverTime = System.currentTimeMillis();
					float value = stringToFloat(lastData);
					alldata.coolantTemp = value + "";
				} else if (lastData.contains("RPM")) {
					lastReceiverTime = System.currentTimeMillis();
					float value = stringToFloat(lastData);
					alldata.rotateSpeed = (int) value + "";
					if (value <= 0) {
						alldata.isAccOpen = false;
					} else {
						alldata.isAccOpen = true;
					}
				} else if (lastData.contains("IFE")) {
					lastReceiverTime = System.currentTimeMillis();
					float value = stringToFloat(lastData);
					alldata.oilConsum = value + "";
				} else if (lastData.contains("RDTC")) {
					lastReceiverTime = System.currentTimeMillis();
					fInfo.faultCodes.clear();
					String[] ss = lastData.split("&");
					for (int j = 1; j < ss.length; j++) {
						if (!fInfo.faultCodes.contains(ss[j]))
							fInfo.faultCodes.add(ss[j]);
					}
				} else if (lastData.contains("AD_Mil")) {
					lastReceiverTime = System.currentTimeMillis();
					float value = stringToFloat(lastData);
					myHelper.getWritableDatabase().execSQL(
							DBConfig.updateTable2SQLString("all_total_miles",
									value + ""));
				}

				if (onGetRealDataListener != null) {
					onGetRealDataListener.onGetRealTimeOBD(alldata);
				}
				lastData = "";
				cmdStart = true;
			}

		}
	}

	private float stringToFloat(String runtime) {
		String str2 = "";
		if (runtime != null && !"".equals(runtime)) {
			for (int i = 0; i < runtime.length(); i++) {
				if (runtime.charAt(i) == 46) {
					str2 += runtime.charAt(i);
					continue;
				}
				if (runtime.charAt(i) >= 48 && runtime.charAt(i) <= 57) {
					str2 += runtime.charAt(i);
				}
			}
		}
		if (str2.equals("")) {
			return 0;
		}
		return Float.parseFloat(str2);
	}

	/**
	 * 星安协议
	 * 
	 * @param pData
	 */

	private void conversionData(PortData pData) {

		String params = bytesToHexString(pData.parameters, pData.length + 1);
		if (pData.cmd.equals("41")) {// ���ó��ͺͷ����� Ӧ����
		} else if (pData.cmd.equals("24")) {// �������� Ӧ����
			procFaultCodes(pData);
		} else if (pData.cmd.equals("27")) {// �v�̬����� Ӧ����

		} else if (pData.cmd.equals("40")) {// ����������� Ӧ����

		} else if (pData.cmd.equals("43")) {// �ϴ����������Ϣ
			procDataStream(pData, params);
		} else if (pData.cmd.equals("44")) {// �ϴ�������Ϣ
			procFaultInfo(pData, params);
		} else if (pData.cmd.equals("45")) {// �г̽�����Ϣ
			procTravelEnd(pData, params);
		} else if (pData.cmd.equals("46")) {// �г̿�ʼ��Ϣ
			procTravelStart();
		}

	}

	public void procTravelStart() {
		travelInfo = new TravelInfo();
		travelInfo.this_start_time = System.currentTimeMillis();

		// db.execSQL(DBConfig.insertIntoValueSQLString("this_start_time",
		// value));
		if (onTravelListener != null)
			onTravelListener.onTravelStart(System.currentTimeMillis());
	}

	private void procFaultCodes(PortData pData) {
		if (pData.parameters[2] == 0x00) {
			Log.e("info", "��������ɹ�������11");
			if (mOnClearFaultListener != null)
				mOnClearFaultListener.success();
		} else if (pData.parameters[2] == 0x01) {
			Log.e("info", "��������ʧ�ܡ���00��");
			if (mOnClearFaultListener != null)
				mOnClearFaultListener.fail();
		} else {
			// Log.e("info", "��֪����û�гɹ���");

		}
	}

	private void procDataStream(PortData pData, String params) {
		int count = pData.parameters[2];
		if (count <= 0 || count > (pData.length - 3) / 5) {
			return;
		}

		for (int i = 0; i < count; i++) {
			int pid = pData.parameters[3 + 5 * i];
			String data = params.substring(8 + 10 * i, 16 + 10 * i);
			switch (pid) {
			case 0xCC:// ˲ʱ�ͺ�
				float a = hexStringToInt(data.substring(2, 4));
				float b = hexStringToInt(data.substring(6));
				float oilConsum = getXiaoshuPart(a, b);
				if (oilConsum >= 0) {
					alldata.oilConsum = oilConsum + "";
				}
				break;

			case 0x0C:// ������ת��
				int a1 = hexStringToInt(data.substring(0, 2));
				int b1 = hexStringToInt(data.substring(2, 4));
				int rotateSpeed = (a1 * 256 + b1) * 16384 / 65535;
				if (rotateSpeed >= 0) {
					if (rotateSpeed == 0) {
						alldata.isAccOpen = false;
					} else {
						alldata.isAccOpen = true;
					}
					alldata.rotateSpeed = rotateSpeed + "";
				}
				// Log.w("real_time_data", "params" + params);
				break;

			case 0x0D:// ����
				int carSpeed = hexStringToInt(data.substring(0, 2));
				if (carSpeed >= 0) {
					alldata.carSpeed = carSpeed + "";
				}
				// Log.w("real_time_data", "params" + params);
				// Log.w("real_time_data", data.substring(0, 2));
				break;

			case 0x05:// ��ȴҺ�¶�
				float temp = hexStringToInt(data.substring(0, 2)) - 40;
				alldata.coolantTemp = temp + "";
				// Log.w("real_time_data", "params" + params);
				// Log.w("real_time_data", data);
				break;
			}
		}
		if (onGetRealDataListener != null) {
			onGetRealDataListener.onGetRealTimeOBD(alldata);
		}
	}

	private void procFaultInfo(PortData pData, String params) {
		int count = pData.parameters[2];
		if (count <= 0 || count > (pData.length - 3) / 3) {
			return;
		}
		// FaultInfo fInfo = new FaultInfo();
		fInfo.length = count;
		fInfo.faultCodes = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			String code = params.substring(6 + 6 * i, 12 + 6 * i);
			fInfo.faultCodes.add(code);
		}

		if (mOnCheckedFaultInfoListener != null)
			mOnCheckedFaultInfoListener.onGetInfo(fInfo);
	}

	public void procTravelEnd(PortData pData, String params) {
		if (travelInfo == null) {
			return;
		}
		if (pData.length == 7) {
			float a = pData.parameters[2] * 256 + pData.parameters[3];
			float b = pData.parameters[4];
			float travelMeters = getXiaoshuPart(a, b);
			float haoyouliang = getXiaoshuPart(pData.parameters[5],
					pData.parameters[6]);
			travelInfo.this_end_time = System.currentTimeMillis();
			travelInfo.this_miles = travelMeters;
			travelInfo.this_oil_consum = haoyouliang;
			travelInfo.this_travel_time = travelInfo.this_end_time
					- travelInfo.this_start_time;
			travelInfo.this_oil_consum_havg = haoyouliang * 100 / travelMeters;
			travelInfo.last_oil_consum_havg = last_oil_consum;
			last_oil_consum = travelInfo.this_oil_consum_havg;
			SQLiteDatabase db = myHelper.getWritableDatabase();
			db.execSQL(DBConfig.getInsertIntoTable1(
					sdf.format(new Date(travelInfo.this_end_time)),
					travelInfo.this_miles, travelInfo.this_oil_consum,
					travelInfo.this_oil_consum_havg,
					sdf.format(new Date(travelInfo.this_start_time)),
					travelInfo.this_travel_time,
					travelInfo.last_oil_consum_havg));

			db.execSQL(DBConfig.updateTable2SQLString("all_total_miles",
					"all_total_miles+" + travelInfo.this_miles));
			db.execSQL(DBConfig.updateTable2SQLString("all_travel_time",
					"all_travel_time+" + travelInfo.this_travel_time));
			db.execSQL(DBConfig.updateTable2SQLString("all_oil_consum",
					"all_oil_consum+" + travelInfo.this_oil_consum));

			travelInfo = null;
			if (onTravelListener != null) {
				onTravelListener.onTravelEnd(travelMeters, haoyouliang,
						System.currentTimeMillis());
			}
		}

	}

	private float getXiaoshuPart(float a, float b) {
		float travelMeters = 0;
		if (b >= 0 && b < 10) {
			travelMeters = a + b / 10;
		} else if (b < 100) {
			travelMeters = a + b / 100;
		} else if (b < 1000) {
			travelMeters = a + b / 1000;
		}
		return travelMeters;
	}

	private String bytesToHexString(byte[] src, int length) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= length) {
			return null;
		}
		for (int i = 0; i < length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	private int hexStringToInt(String str) {
		str = str.toUpperCase();
		int i = charToByte(str.charAt(0)) << 4 | charToByte(str.charAt(1));
		return i;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public void setOnClearFaultListener(
			OnClearFaultListener mOnClearFaultListener) {
		this.mOnClearFaultListener = mOnClearFaultListener;
	}

	public void setOnCheckedFaultInfoListener(
			OnCheckedFaultInfoListener mOnCheckedFaultInfoListener) {
		this.mOnCheckedFaultInfoListener = mOnCheckedFaultInfoListener;
	}

	public void setOnGetRealDataListener(
			OnGetRealDataListener onGetRealDataListener) {
		this.onGetRealDataListener = onGetRealDataListener;
	}

	public void setOnTravelListener(OnTravelListener onTravelListener) {
		this.onTravelListener = onTravelListener;
	}

	public RealTimeOBD getRealTimeData() {
		return this.alldata;
	}

	public JocDBHelper getMyHelper() {
		return myHelper;
	}

	public FaultInfo getfInfo() {
		return fInfo;
	}

	class CheckReceiverDateTime extends Thread {

		private static final String CMD_TEMP = "BT+DATA.ECT\r\n";
		private static final String CMD_CARSPEED = "BT+DATA.VSS\r\n";
		private static final String CMD_ROTATE_SPEED = "BT+DATA.RPM\r\n";
		private static final String CMD_OILCONSUM = "BT+DATA.IFE\r\n";

		private static final String CMD_ERROR_CODE = "BT+RDTC\r\n";
		private static final String CMD_TOTAL_MILES = "BT+DATA.AD_Mil\r\n";

		@Override
		public void run() {

			Socket socket = null;
			OutputStream out = null;
			VTSData data = new VTSData();
			try {
				socket = new Socket("61.144.244.100", 5005);
				// this.socket = new Socket("192.168.25.104",6800);
				out = socket.getOutputStream();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
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
				WriteOdbPort.from().sendCmdToOdbPort(CMD_CARSPEED.getBytes());
				try {
					sleep(800);
					WriteOdbPort.from().sendCmdToOdbPort(
							CMD_ROTATE_SPEED.getBytes());
					sleep(800);
					WriteOdbPort.from().sendCmdToOdbPort(CMD_OILCONSUM.getBytes());
					sleep(800);
					WriteOdbPort.from().sendCmdToOdbPort(CMD_TEMP.getBytes());
					sleep(800);
					WriteOdbPort.from()
							.sendCmdToOdbPort(CMD_TOTAL_MILES.getBytes());
					sleep(800);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				WriteOdbPort.from().sendCmdToOdbPort(CMD_ERROR_CODE.getBytes());

				long currentTime = System.currentTimeMillis();
				long dTime = currentTime - lastReceiverTime;
				int second = (int) (dTime / 1000);
				Log.i("info", "second:" + second);
				if (second >= 1800) {
					alldata.carSpeed = "0";
					alldata.oilConsum = "0";
					alldata.coolantTemp = "0";
					alldata.rotateSpeed = "0";
					alldata.isAccOpen = false;
				}
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (out != null) {
					try {
						out.write(data.getUploadString().getBytes());
						Log.w("info", "上传数据一次：" + data.getUploadString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.w("info", "完成一次循环");
			}
		}

	}
}
