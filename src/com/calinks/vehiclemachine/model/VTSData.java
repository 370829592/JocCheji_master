package com.calinks.vehiclemachine.model;

import java.util.ArrayList;
import java.util.Calendar;

import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import com.calinks.vehiclemachine.commom.OBDProc;
import com.calinks.vehiclemachine.model.db.DBConfig;
import com.icalinks.mobile.GlobalApplication;
public class VTSData {
	// 设备id
	public String id = "";

	public String gpsDate = "";// gps日期

	public String gpsTime = "";// gps时间

	// public String latitude = "";// 纬度
	//
	// public String longitude = "";// 经度

	public String gpsInfo = "";

	public String carState = "";// 车状态

	public String carSpeed = "";// 车速

	public String totalMiles = "";// 总里程

	public String errorCodes = "";// 故障码

	public String overplusOil = "";// 剩余油量

	public String rotateSpeed = "";// 发动机转速

	public String coolantTemp = "";// 冷却液温度

	public String obdType = "";

	public String oilConsumEveryHour = "";

	public String oilConsumEveryMile = "";

	public String fuheValue = "0";

	public String inTemp = "0";

	public String airFlowRate = "0";// 空气流量

	public String jieqimenjueduiweizhi = "0";// 节气门绝对位置

	public String carTension = "0";// 电压

	public String envirTemp = "0";

	public String changqiranyouxiuzheng = "0";// 长期燃油修正

	public String qigangyidianhuotiqianjiao = "0";// 气缸1点火提前角

	public String jinqiqiguanjueduiyali = "0";// 进气岐管绝压力

	public String end = "01*";
	private void updateData() {
		// 0
		setAndroidId();
		// 1、2
		setGPSDateAndTime();
		// 3、4
		setLocationInfo();
		// 5
		setAccState();
		// 6 、10、11、13
		setCarSpeed();
		// 7
		setMiles();
		// 8
		setErrorCode("OBD");
		// 9
		setOil();

		// 12
		setOBDType();
		// 14
		setOilConsumMile();

	}

	public String getUploadString() {
		updateData();
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(",").append(gpsDate).append(",").append(gpsTime)
				.append(",").append(gpsInfo).append(",").append(carState)
				.append(",").append(carSpeed).append(",").append(totalMiles)
				.append(",").append(errorCodes).append(",").append(overplusOil)
				.append(",").append(rotateSpeed).append(",")
				.append(coolantTemp).append(",").append(obdType).append(",")
				.append(oilConsumEveryHour).append(",")
				.append(oilConsumEveryMile).append(",").append(fuheValue)
				.append(",").append(inTemp).append(",").append(airFlowRate)
				.append(",").append(jieqimenjueduiweizhi).append(",")
				.append(carTension).append(",").append(envirTemp).append(",")
				.append(changqiranyouxiuzheng).append(",")
				.append(qigangyidianhuotiqianjiao).append(",")
				.append(jinqiqiguanjueduiyali).append(",").append(end);
//		String str = "15013722907,050513,160825,+2307.4788,+11320.6264,1,0,0,[],0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,01*";
		return sb.toString();
//		return str;
	}

	// 0
	private void setAndroidId() {
		if (id.length() == 0) {
			id = GlobalApplication.getApplication().getUserName();
		}
	}

	// 1、2
	private void setGPSDateAndTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, -8);
		gpsDate = getString(c.get(Calendar.MONTH) + 1);
		gpsDate += getString(c.get(Calendar.DAY_OF_MONTH));
		gpsDate += String.valueOf(c.get(Calendar.YEAR)).substring(2);

		gpsTime = getString(c.get(Calendar.HOUR_OF_DAY))
				+ getString(c.get(Calendar.MINUTE))
				+ getString(c.get(Calendar.SECOND));

	}

	// 3、4
	private void setLocationInfo() {
//		gpsInfo = GlobalApplication.getApplication().getGPS().getLocation();
		Location l = GlobalApplication.getLocation();
		gpsInfo = gpsDataToString(l.getLatitude())+","+gpsDataToString(l.getLongitude());
	}
	private String gpsDataToString(double a){
		String s = "+";
		int zhengshu = (int) Math.floor(a);
		double xiaoshu = a-zhengshu;
		
		return s+zhengshu+String.format("%.4f", xiaoshu*60);
	}

	// 5
	private void setAccState() {
		boolean isAccOpen = GlobalApplication.getApplication().getObd()
				.getRealTimeData().isAccOpen;
		if (isAccOpen) {
			carState = "17";
		} else {
			carState = "16";
		}
	}

	// 6 、10、11、13
	private void setCarSpeed() {
		OBDProc obd = GlobalApplication.getApplication().getObd();
		String speed = obd.getRealTimeData().carSpeed;
		String temp = obd.getRealTimeData().coolantTemp;
		String rotateSpeed = obd.getRealTimeData().rotateSpeed;
		String oilConsum = "";
		if (obd.getRealTimeData().oilConsum != null) {
			oilConsum = Float
					.parseFloat(obd.getRealTimeData().oilConsum) * 3.6 + "";
		}
		
		if (speed != null && speed.length() > 0) {
			carSpeed = speed;
		} else {
			// 没有数据时默认值
			carSpeed = "0";
		}
		if (temp != null && temp.length() > 0) {
			coolantTemp = temp;
		} else {
			// 没有数据时默认值
			coolantTemp = "0";
		}
		if (rotateSpeed != null && rotateSpeed.length() > 0) {
			this.rotateSpeed = rotateSpeed;
		} else {
			// 没有数据时默认值
			this.rotateSpeed = "0";
		}
		if (oilConsum != null && oilConsum.length() > 0) {
			this.oilConsumEveryHour = oilConsum;
		} else {
			// 没有数据时默认值
			this.oilConsumEveryHour = "0";
		}

	}

	// 7
	private void setMiles() {
		Cursor c = GlobalApplication.getApplication().getObd().getMyHelper()
				.getReadableDatabase()
				.rawQuery(DBConfig.SELECT_ALL_TOTAL_TABLE, null);
		if (c.getCount() > 0 && c.moveToLast()) {
			totalMiles = c.getFloat(c.getColumnIndex("all_total_miles")) + "";
			// coolantTemp = c.getLong(c.getColumnIndex("all_travel_time"))+"";
			// float allTotalConsum =
			// c.getFloat(c.getColumnIndex("all_oil_consum"));
			// allOilConsumHavg = allTotalConsum*100/allTotalMiles;
		}
		c.close();
	}

	// 8
	private void setErrorCode(String carType) {
		FaultInfo info = GlobalApplication.getApplication().getObd().getfInfo();
		ArrayList<String> list = info.faultCodes;
		if (list != null && list.size() > 0) {
			StringBuilder sbError = new StringBuilder("[");
			for (String string : list) {
				sbError.append(string).append("|");
			}
			sbError.append("]");
			errorCodes = sbError.toString();
		} else {
			errorCodes = "0";
		}

		// if (list != null && list.size() > 0) {
		// StringBuilder sbError = new StringBuilder("[");
		// if (carType.equals("OBD")) {// 车型为OBD
		// for (int i = 0; i < list.size(); i++) {
		//
		// String errorCode = list.get(i);
		// char first = errorCode.charAt(0);
		// switch (first) {
		// case '0':
		// procFirst123(sbError, errorCode);
		// break;
		// case '1':
		// procFirst123(sbError, errorCode);
		// break;
		// case '2':
		// procFirst123(sbError, errorCode);
		// break;
		// case '3':
		// procFirst123(sbError, errorCode);
		// break;
		// case '4':
		// if (isO(errorCode)) {
		// sbError.append("C00")
		// .append(errorCode.substring(2, 4))
		// .append("o");
		// } else {
		// sbError.append("C00")
		// .append(errorCode.substring(2, 4))
		// .append("n");
		// }
		// break;
		// case '8':
		// if (isO(errorCode)) {
		// sbError.append("B00")
		// .append(errorCode.substring(2, 4))
		// .append("o");
		// } else {
		// sbError.append("B00")
		// .append(errorCode.substring(2, 4))
		// .append("n");
		// }
		// break;
		// case 'C':
		// if (isO(errorCode)) {
		// sbError.append("U0")
		// .append(errorCode.substring(1, 4))
		// .append("o");
		// } else {
		// sbError.append("U0")
		// .append(errorCode.substring(1, 4))
		// .append("n");
		// }
		// break;
		// case 'F':
		// if (isO(errorCode)) {
		// sbError.append("U3")
		// .append(errorCode.substring(1, 4))
		// .append("o");
		// } else {
		// sbError.append("U3")
		// .append(errorCode.substring(1, 4))
		// .append("n");
		// }
		// break;
		// }
		// sbError.append("|");
		// }
		// sbError.deleteCharAt(sbError.length()-1);
		// sbError.append("]");
		// sbError.toString();//获取错误码字符串
		// }
		// }
	}

	private void procFirst123(StringBuilder sbError, String errorCode) {
		if (isO(errorCode)) {
			sbError.append("P").append(errorCode.substring(0, 4)).append("o");
		} else {
			sbError.append("P").append(errorCode.substring(0, 4)).append("n");
		}
	}

	// 9
	private void setOil() {
		overplusOil = "0";
	}

	// 12
	private void setOBDType() {
		obdType = "6";
	}

	// 14
	private void setOilConsumMile() {
		oilConsumEveryMile = "0";
	}

	private String getString(int i) {
		if (i >= 0 && i < 10) {
			return "0" + i;
		}
		return "" + i;
	}

	private boolean isO(String errorCode) {
		String end = errorCode.substring(4);
		if (end.equals("01")) {// 表示当前故障o
			return true;
		} else if (end.equals("02")) {// 表示未决故障N
			return false;
		}
		return false;
	}

}
