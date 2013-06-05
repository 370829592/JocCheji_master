package com.calinks.vehiclemachine.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.database.Cursor;

import com.calinks.vehiclemachine.commom.OBDProc;
import com.calinks.vehiclemachine.model.db.DBConfig;
import com.calinks.vehiclemachine.model.db.JocDBHelper;
/**
 * 统计数据存储
 * @author Administrator
 *
 */

public class StatisticsInfo implements Serializable{
	//��ƽ��ٹ����ͺ�
	public float allOilConsumHavg;//ok
	//�����
	public float allTotalMiles; //ok
	
	//�ܼ�ʻʱ��
	public long allTravelTime; //ok
	//�����ͺ�
	public float dayOilConsum; //ok
	//�հٹ���ƽ���ͺ�
	public float dayOilConsumHAvg;
	//���������
	public float dayTotalMiles;
	//���г̴���
	public int dayTravelCount;
	//�ռ�ʻʱ��
	public long dayTravelTime;
	//�ϴ���ʻ�ͺ�
	public float lastOilConsumHAvg;
	//������ʻ����ʱ��
	public String thisEndTime;
	//������ʻ�������
	public float thisMiles;
	//������ʻ���ͺ�
	public float thisOilConsum;
	//������ʻ��ƽ���ͺ�
	public float thisOilConsumHAvg;
	//������ʻ��ʼʱ��
	public String thisStartTime;
	//������ʻʱ��
	public long thisTravelTime;
//	�г�id
	public String travelId;
	
	public void register(OBDProc obd){
		JocDBHelper myhelper = obd.getMyHelper();
		if(myhelper ==null){
			return;
		}
		Cursor c = myhelper.getReadableDatabase().rawQuery(DBConfig.SELECT_THIS_TRAVEL, null);
		if(c.getCount()==1 && c.moveToFirst()){
			lastOilConsumHAvg = c.getFloat(c.getColumnIndex("last_oil_consum_havg"));
			thisStartTime = c.getString(c.getColumnIndex("this_start_time"));
			thisEndTime = c.getString(c.getColumnIndex("this_end_time"));
			thisMiles = c.getFloat(c.getColumnIndex("this_miles"));
			thisOilConsum = c.getFloat(c.getColumnIndex("this_oil_consum"));
			thisOilConsumHAvg = c.getFloat(c.getColumnIndex("this_oil_consum_havg"));
			thisTravelTime = c.getLong(c.getColumnIndex("this_travel_time"));
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date nowData = new Date();
		String key = sdf.format(nowData);
		c = myhelper.getReadableDatabase().rawQuery(DBConfig.selectDayTravelInfo(key), null);
		dayTravelCount = c.getCount();
		while(c.getCount()>0 && c.moveToNext()){
			dayOilConsum += c.getFloat(c.getColumnIndex("this_oil_consum"));
			dayTotalMiles += c.getFloat(c.getColumnIndex("this_miles"));
			dayTravelTime += c.getLong(c.getColumnIndex("this_travel_time"));
		}
		dayOilConsumHAvg = dayOilConsum*100/dayTotalMiles;
		
		c = myhelper.getReadableDatabase().rawQuery(DBConfig.SELECT_ALL_TOTAL_TABLE, null);
		if(c.getCount() >0 && c.moveToLast()){
			allTotalMiles = c.getFloat(c.getColumnIndex("all_total_miles"));
			allTravelTime = c.getLong(c.getColumnIndex("all_travel_time"));
			float allTotalConsum = c.getFloat(c.getColumnIndex("all_oil_consum"));
			allOilConsumHavg = allTotalConsum*100/allTotalMiles; 
		}
		c.close();
		
	}
}
