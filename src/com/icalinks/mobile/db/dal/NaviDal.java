package com.icalinks.mobile.db.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.baidu.mapapi.GeoPoint;
import com.icalinks.common.StringHelper;
import com.icalinks.mobile.db.DBHelper;
import com.icalinks.mobile.util.DateTime;

public class NaviDal {

	protected String $nullColumnHack() {
		return "_id";
	}

	protected String $table() {
		return "navi";
	}

	private DBHelper mDBHelper;

	public NaviInfo exists(String dstAddrs) {
		List<NaviInfo> lstInfo = select(new NaviInfo(dstAddrs));
		if (lstInfo != null && lstInfo.size() > 0) {
			return lstInfo.get(0);
		} else {
			return null;
		}
	}

	// private static int miCount = 0;
	//
	// public void delete(String name) {
	//
	// // mDBHelper.getWritableDatabase().delete($table(), "name=?", new
	// // String[] { name });
	// // mDBHelper.getWritableDatabase().execSQL("DELETE FROM " + $table() +
	// // " WHERE _id=" + Integer.toString(miCount));
	// mDBHelper.getReadableDatabase().delete($table(), "name=?",
	// new String[] { name });
	//
	// }

	public long delete(int _id) {
		long result = mDBHelper.getWritableDatabase().delete($table(),
				"_id=" + _id, null);
		return result;
	}

	public long update(int _id) {
		ContentValues values = new ContentValues();
		values.put("sysMdfDate", System.currentTimeMillis());
		// (table, values, whereClause, whereArgs)
		long result = mDBHelper.getWritableDatabase().update($table(), values,
				"_id=" + _id, null);
		return result;
	}

	public long insert(NaviInfo info) {
		ContentValues values = new ContentValues();
		values.put("navitype", info.getNaviType());
		values.put("datetime", new DateTime().toString());
		values.put("begpoint", StringHelper.getGeoPoint(info.getBegPoint()));
		values.put("begaddrs", "" + info.getBegAddrs());
		values.put("endpoint", StringHelper.getGeoPoint(info.getEndPoint()));
		values.put("endaddrs", "" + info.getEndAddrs());
		values.put("sysAddDate", System.currentTimeMillis());
		values.put("sysMdfDate", System.currentTimeMillis());
		long result = mDBHelper.getWritableDatabase().insert($table(),
				$nullColumnHack(), values);
		return result;
	}

	protected String[] clumns = { "_id", "navitype", "datetime", "begpoint",
			"begaddrs", "endpoint", "endaddrs" };

	public List<NaviInfo> select(NaviInfo info) {
		List<NaviInfo> lstResult = new ArrayList<NaviInfo>();
		{
			String selection = null;
			String[] selectionArgs = null;
			if (info != null) {
				selection = "endaddrs = ?";
				selectionArgs = new String[] { info.getEndAddrs() };
			}

			String orderBy = "sysMdfDate DESC  LIMIT 20";

			Cursor cur = mDBHelper.getReadableDatabase().query($table(),
					clumns, selection, selectionArgs, null, null, orderBy);
			if (cur != null && cur.moveToFirst()) {

				do {
					NaviInfo tmp = new NaviInfo();
					{
						tmp.set_id(cur.getInt(cur.getColumnIndex("_id")));
						tmp.setNaviType(cur.getInt(cur
								.getColumnIndex("navitype")));
						tmp.setDatetime(cur.getString(cur
								.getColumnIndex("datetime")));

						tmp.setBegPoint(StringHelper.getGeoPoint(cur
								.getString(cur.getColumnIndex("begpoint"))));
						tmp.setBegAddrs(cur.getString(cur
								.getColumnIndex("begaddrs")));

						tmp.setEndPoint(StringHelper.getGeoPoint(cur
								.getString(cur.getColumnIndex("endpoint"))));
						tmp.setEndAddrs(cur.getString(cur
								.getColumnIndex("endaddrs")));
					}
					lstResult.add(tmp);
				} while (cur.moveToNext());
			}
			cur.close();
		}
		return lstResult;
	}

	/**
	 * 私有静态成员
	 */
	private static NaviDal mNaviDal;

	/**
	 * 私有化构造方法
	 */

	private NaviDal(Context context) {
		mDBHelper = new DBHelper(context);
	}

	/**
	 * 静态工厂方法
	 */
	public synchronized static NaviDal getInstance(Context context) {
		if (mNaviDal == null) {
			mNaviDal = new NaviDal(context);
		}
		return mNaviDal;
	}
}
