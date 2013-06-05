package com.calinks.vehiclemachine.commom;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.calinks.vehiclemachine.model.db.DBConfig;
import com.calinks.vehiclemachine.model.db.JocDBHelper;
import com.icalinks.obd.vo.ServiceInfo;

/**
 * @ClassName: 类名称:DBUtils
 * @author 作者 E-mail: wc_zhang@calinks.com.cn
 * @Description: TODO 对数据库的增删改查
 * @version 创建时间：2013-5-31 上午11:21:43
 */
public class DBUtils {
	
	
	public static void insertConsumRecord(ServiceInfo info,Context context){
		JocDBHelper db = new JocDBHelper(context);
		ContentValues values = new ContentValues();
		String comStr = info.getTypeName()+"";
		if(comStr.length() > 60){
			comStr = comStr.substring(0, 60) + "......";
		}
		values.put("date", info.getServiceDate());
		values.put("money", info.getPrice());
		values.put("miles", info.getDistance());
		values.put("content", comStr);
		SQLiteDatabase wdb = db.getWritableDatabase();
		wdb.insert(DBConfig.CREATE_TABLE_4, null, values);
		wdb.close();
	}
}
