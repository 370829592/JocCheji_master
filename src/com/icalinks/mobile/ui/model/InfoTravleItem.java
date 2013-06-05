package com.icalinks.mobile.ui.model;

import java.io.Serializable;

import com.icalinks.obd.vo.StatisticsInfo;
import com.icalinks.obd.vo.TravelInfo;

/**
 * 
 * @author zg_hu@calinks.com.cn
 * 
 */

public class InfoTravleItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private StatisticsInfo mStatisticsInfo;
	private TravelInfo mTravelInfo;

	private String startPos;
	private String endPos;

	public String thisStartTime;
	public String thisEndTime;
	private String year;
	private String month;
	private String month_;
	private String date;
	private String startTime;
	private String sTime;
	private String endTime;
	private String eTime;
	private String tavelTime;
    private String travelId;
	public String getTravelId() {
		return travelId;
	}



	public void setTravelId(String travelId) {
		this.travelId = travelId;
	}

	//油耗数量
	private String oilCount;
//	private String oilPay;

	private String mile;

	private String speedMax;

	public InfoTravleItem(StatisticsInfo mStatisticsInfo, TravelInfo mTravelInfo) {
		this.mStatisticsInfo = mStatisticsInfo;
		this.mTravelInfo = mTravelInfo;

		spliteBean();
	}

	

	public StatisticsInfo getmStatisticsInfo() {
		return mStatisticsInfo;
	}



	public void setmStatisticsInfo(StatisticsInfo mStatisticsInfo) {
		this.mStatisticsInfo = mStatisticsInfo;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public TravelInfo getmTravelInfo() {
		return mTravelInfo;
	}



	public void setmTravelInfo(TravelInfo mTravelInfo) {
		this.mTravelInfo = mTravelInfo;
	}

	private void spliteBean() {

		setTravelInfo(mTravelInfo);
		thisStartTime = mStatisticsInfo.thisStartTime;
		thisEndTime = mStatisticsInfo.thisEndTime;

		String time[] = spliteTime(mStatisticsInfo.thisStartTime).split("-");
		year = time[0];
		month = time[1];
		month_ = converMonth(time[1]);
		date = time[2];
		startTime = time[3].substring(0, 8);
		sTime = time[3].substring(0, 5);
		time = spliteTime(mStatisticsInfo.thisEndTime).split("-");
		endTime = time[3].substring(0, 8);
		eTime = time[3].substring(0, 5);
		tavelTime = mStatisticsInfo.thisTravelTime;

		mile = mStatisticsInfo.thisMiles;
		
		oilCount = mStatisticsInfo.thisOilConsum;
//		oilPay = mStatisticsInfo.thisOilConsum;

	}

//	public void setTravelInfo() {
//		setTravelInfo(mTravelInfo);
//	}

	public void setTravelInfo(TravelInfo mTravelInfo) {
		this.mTravelInfo = mTravelInfo;

		if (mTravelInfo == null) {
			startPos = "获取失败,请退出重新获取";
			endPos = "获取失败,请退出重新获取";

			speedMax = "0";
		} else {
			startPos = mTravelInfo.getStartAddr();
			endPos = mTravelInfo.getEndAddr();

			speedMax = mTravelInfo.getMaxSpeed() + "";
		}
	}

	private String spliteTime(String time) {
		if(time == null)return null;
		time = (time.trim()).replaceAll(" ", "-");

		return time;
	}

	private String converMonth(String month) {
		String mon[] = { "0", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
				"十一", "十二" };

		int monInt = Integer.parseInt(month);

		return mon[monInt];
	}

	public String getStartPos() {
		return startPos;
	}

	public void setStartPos(String startPos) {
		this.startPos = startPos;
	}

	public String getEndPos() {
		return endPos;
	}

	public void setEndPos(String endPos) {
		this.endPos = endPos;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getTavelTime() {
		return tavelTime;
	}

	public void setTavelTime(String tavelTime) {
		this.tavelTime = tavelTime;
	}

	public String getOilCount() {
		return oilCount;
	}

	public void setOilCount(String oilCount) {
		this.oilCount = oilCount;
	}

	// public String getOilPay() {
	// return oilPay;
	// }
	//
	// public void setOilPay(String oilPay) {
	// this.oilPay = oilPay;
	// }

	public String getSpeedMax() {
		return speedMax;
	}

	public void setSpeedMax(String speedMax) {
		this.speedMax = speedMax;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getThisStartTime() {
		return thisStartTime;
	}

	public void setThisStartTime(String thisStartTime) {
		this.thisStartTime = thisStartTime;
	}

	public String getThisEndTime() {
		return thisEndTime;
	}

	public void setThisEndTime(String thisEndTime) {
		this.thisEndTime = thisEndTime;
	}

	public String getMonth_() {
		return month_;
	}

	public void setMonth_(String month_) {
		this.month_ = month_;
	}

	public String getsTime() {
		return sTime;
	}

	public void setsTime(String sTime) {
		this.sTime = sTime;
	}

	public String geteTime() {
		return eTime;
	}

	public void seteTime(String eTime) {
		this.eTime = eTime;
	}

	public String getMile() {
		return mile;
	}

	public void setMile(String mile) {
		this.mile = mile;
	}

}
