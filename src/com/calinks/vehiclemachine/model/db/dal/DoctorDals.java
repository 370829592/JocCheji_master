package com.calinks.vehiclemachine.model.db.dal;

import java.util.ArrayList;

import com.calinks.vehiclemachine.model.db.DBConfig;
import com.calinks.vehiclemachine.model.db.JocDBHelper;
import com.icalinks.obd.vo.ServiceInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @ClassName: 类名称:DoctorDals
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 汽车医生数据处理
 * @version 创建时间：2013-6-6 上午10:28:13
 */
public class DoctorDals {
	private  JocDBHelper mHelper;
	
	private DoctorDals(Context context){
		mHelper = new JocDBHelper(context);
	}
	private static DoctorDals docDals;
	
	public static DoctorDals getInstance(Context context){
		if(docDals == null){
			docDals = new DoctorDals(context);
		}
		return docDals;
	}
	/**
	 * 
	 * @param info
	 * @param context
	 * @return void
	 * @description 插入消费记录数据 
	 * @date 创建时间:  2013-6-6 上午10:32:36
	 */
	public void insertConsumRecord(ServiceInfo info){
		ContentValues values = new ContentValues();
		values.put("date", info.getServiceDate());
		values.put("money", info.getPrice());
		values.put("miles", info.getDistance());
		values.put("content",  info.getTypeName());
		values.put("charge_type",  info.getChargeType());
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.insert(DBConfig.TABLE_NAME_4, null, values);
		db.close();
	}
	/**
	 * @param info
	 * @return void
	 * @description 更新消费记录
	 * @date 创建时间:  2013-6-6 上午10:45:17
	 */
	public void updateConsumRecord(ServiceInfo info){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("date", info.getServiceDate());
		values.put("money", info.getPrice());
		values.put("miles", info.getDistance());
		values.put("content",  info.getTypeName());
		values.put("charge_type",  info.getChargeType());
		db.update(DBConfig.TABLE_NAME_4, values, "_id=?", new String[]{info.getVid()+""});
		db.close();
	}
	/**
	 * @param vid
	 * @return void
	 * @description 删除某条消费记录 
	 * @date 创建时间:  2013-6-6 上午11:01:25
	 */
	public void deleteConsumRecord(int vid){
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.delete(DBConfig.TABLE_NAME_4, "_id=?", new String[]{vid+""});
		db.close();
	}
	/**
	 * @return
	 * @return ArrayList<ServiceInfo>
	 * @description 查询消费记录 
	 * @date 创建时间:  2013-6-6 上午11:27:40
	 */
	public ArrayList<ServiceInfo> selectConsumRecord(){
		ArrayList<ServiceInfo> list = new ArrayList<ServiceInfo>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor c = db.query(DBConfig.TABLE_NAME_4, null, null, null, null, null, "_id desc");
		if(c.getCount()<=0)return list;
		while(c.moveToNext()){
			ServiceInfo newInfo = new ServiceInfo();
			newInfo.setVid(c.getInt(c.getColumnIndex("_id")));
			newInfo.setServiceDate(c.getString(c.getColumnIndex("date")));
			newInfo.setPrice(c.getString(c.getColumnIndex("money")));
			newInfo.setDistance(c.getString(c.getColumnIndex("miles")));
			newInfo.setTypeName(c.getString(c.getColumnIndex("content")));
			newInfo.setChargeType(c.getString(c.getColumnIndex("charge_type")));
			list.add(newInfo);
		}
		c.close();
		db.close();
		return list;
	}
	
}
