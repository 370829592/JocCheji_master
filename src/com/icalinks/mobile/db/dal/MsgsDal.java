package com.icalinks.mobile.db.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.icalinks.mobile.db.DBHelper;
import com.icalinks.mobile.ui.model.MsgsItem;

public class MsgsDal {

	protected String $nullColumnHack() {
		return "_id";
	}

	protected String $table() {
		return "msgs";
	}

	private DBHelper mDBHelper;

	public MsgsItem exists(String vid, int msgsType, String msgsId) {
		MsgsItem item = new MsgsItem();
		item.setVid(vid);
		item.setMsgsType(msgsType);
		item.setMsgsId(msgsId);

		List<MsgsItem> lstInfo = select(item);
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
		values.put("isread", 1);
		// (table, values, whereClause, whereArgs)
		long result = mDBHelper.getWritableDatabase().update($table(), values,
				"_id=" + _id, null);
		return result;
	}

	public long insert(MsgsItem item) {
		ContentValues values = new ContentValues();
		values.put("vid", item.getVid());
		values.put("msgstype", item.getMsgsType());
		values.put("msgsid", item.getMsgsId());
		values.put("datetime", item.getDatetime());
		values.put("isread", item.isIsread() ? 1 : 0);
		values.put("title", "" + item.getTitle());
		values.put("content", "" + item.getContent());
		values.put("sysAddDate", System.currentTimeMillis());
		values.put("sysMdfDate", System.currentTimeMillis());
		long result = mDBHelper.getWritableDatabase().insert($table(),
				$nullColumnHack(), values);
		return result;
	}

	protected String[] clumns = { "_id", "vid", "msgstype", "msgsid",
			"datetime", "isread", "title", "content" };

	public List<MsgsItem> select(MsgsItem item) {
		List<MsgsItem> lstResult = new ArrayList<MsgsItem>();
		{
			String selection = null;
			String[] selectionArgs = null;
			if (item != null && item.getVid() != null) {
				if (item.getMsgsId() != null && item.getMsgsType() != 0) {
					selection = "vid=? AND msgstype = ? AND msgsid=?";
					selectionArgs = new String[] { item.getVid(),
							"" + item.getMsgsType(), item.getMsgsId() };
				} else {
					if (!item.isIsread()) {
						selection = "vid=? AND isread=?";
						selectionArgs = new String[] { item.getVid(), "0" };
					} else {
						selection = "vid=?";
						selectionArgs = new String[] { item.getVid() };
					}
				}
			}

			String orderBy = "sysMdfDate DESC ";// LIMIT 20

			Cursor cur = mDBHelper.getReadableDatabase().query($table(),
					clumns, selection, selectionArgs, null, null, orderBy);
			if (cur != null && cur.moveToFirst()) {

				do {
					lstResult.add(cur2obj(cur));
				} while (cur.moveToNext());
			}
			cur.close();
		}
		return lstResult;
	}

	private MsgsItem cur2obj(Cursor cur) {
		MsgsItem item = new MsgsItem();
		{
			item.set_id(cur.getInt(cur.getColumnIndex("_id")));
			item.setVid(cur.getString(cur.getColumnIndex("vid")));
			item.setMsgsId(cur.getString(cur.getColumnIndex("msgsid")));
			item.setMsgsType(cur.getInt(cur.getColumnIndex("msgstype")));
			item.setIsread(cur.getInt(cur.getColumnIndex("isread")) == 1 ? true
					: false);
			item.setTitle(cur.getString(cur.getColumnIndex("title")));
			item.setContent(cur.getString(cur.getColumnIndex("content")));
			item.setDatetime(cur.getString(cur.getColumnIndex("datetime")));
		}
		return item;
	}

	// limit 10,100;
	public List<MsgsItem> select(String userVid, int pageSize, int pageNumber) {
		List<MsgsItem> lstResult = new ArrayList<MsgsItem>();
		{
			String selection = "vid=?";
			String[] selectionArgs = new String[] { userVid };
			String orderBy = "sysMdfDate DESC  limit " + pageSize
					* (pageNumber - 1) + "," + pageSize;// LIMIT
														// 20

			Cursor cur = mDBHelper.getReadableDatabase().query($table(),
					clumns, selection, selectionArgs, null, null, orderBy);
			if (cur != null && cur.moveToFirst()) {
				do {
					lstResult.add(cur2obj(cur));
				} while (cur.moveToNext());
			}
			cur.close();
		}
		return lstResult;
	}

	public int select(String userVid) {
		int count = 0;
		{
			String selection = "vid=?";
			String[] selectionArgs = new String[] { userVid };

			Cursor cur = mDBHelper.getReadableDatabase().query($table(),
					new String[] { "_id" }, selection, selectionArgs, null,
					null, null);
			if (cur != null) {
				count = cur.getCount();
			}

			cur.close();
		}
		return count;
	}

	/**
	 * 私有静态成员
	 */
	private static MsgsDal mMsgsDal;

	/**
	 * 私有化构造方法
	 */

	private MsgsDal(Context context) {
		mDBHelper = new DBHelper(context);
	}

	/**
	 * 静态工厂方法
	 */
	public synchronized static MsgsDal getInstance(Context context) {
		if (mMsgsDal == null) {
			mMsgsDal = new MsgsDal(context);
		}
		return mMsgsDal;
	}
}
