package com.icalinks.mobile.db.dal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.icalinks.mobile.db.DBHelper;

public class UserDal {

	protected String $nullColumnHack() {
		return "_id";
	}

	protected String $table() {
		return "user";
	}

	private DBHelper mDBHelper;

	// private SQLiteDatabase mSQLiteDatabase;
	//
	// protected SQLiteDatabase openWrite() {
	// return mSQLiteDatabase = mDBHelper.getWritableDatabase();
	// }
	//
	// protected SQLiteDatabase openRead() {
	// return mSQLiteDatabase = mDBHelper.getReadableDatabase();
	// }

	// public void close() {
	// if (mSQLiteDatabase != null) {// ggz//不行了就分开，只是稍微麻烦一点儿
	// mSQLiteDatabase.close();
	// } else {
	// // mDBHelper.close();
	// }
	// }

	// protected abstract ContentValues getValues(BaseInfo info);
	//
	// protected abstract String[] getColumns();

	public boolean exists(String vid) {
		UserInfo userInfo = new UserInfo();
		userInfo.setVid(vid);
		List<UserInfo> lstInfo = select(userInfo);
		if (lstInfo != null && lstInfo.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	// public boolean exists(String name) {
	// List<UserInfo> lstInfo = select(new UserInfo(name));
	// if (lstInfo != null && lstInfo.size() > 0) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	private static int miCount = 0;

	public void delete(int vid) {

		// mDBHelper.getWritableDatabase().delete($table(), "name=?", new
		// String[] { name });
		// mDBHelper.getWritableDatabase().execSQL("DELETE FROM " + $table() +
		// " WHERE _id=" + Integer.toString(miCount));
		mDBHelper.getReadableDatabase().delete($table(), "vid=?",
				new String[] { "" + vid });

	}

	public long insert(String name, String pswd, String nick, String vid) {
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("pswd", pswd);
		values.put("nick", nick);
		values.put("vid", vid);
		values.put("sysAddDate", System.currentTimeMillis());
		values.put("sysMdfDate", System.currentTimeMillis());
		long result = mDBHelper.getWritableDatabase().insert($table(),
				$nullColumnHack(), values);
		return result;
	}

	public long updateNickname(String name, String nick) {
		ContentValues values = new ContentValues();
		values.put("nick", nick);
		// values.put("sysMdfDate", System.currentTimeMillis());
		long result = mDBHelper.getWritableDatabase().update($table(), values,
				"name=?", new String[] { name });
		return result;
	}

	public long updatePassword(String name, String pswd) {
		ContentValues values = new ContentValues();
		values.put("pswd", pswd);
//		values.put("sysMdfDate", System.currentTimeMillis());
		long result = mDBHelper.getWritableDatabase().update($table(), values,
				"name=?", new String[] { name });
		return result;
	}

	public long update(String name, String pswd, String nick, String vid) {
		ContentValues values = new ContentValues();
		values.put("name", name);
		values.put("pswd", pswd);
		values.put("nick", nick);
		values.put("vid", vid);
		values.put("sysMdfDate", System.currentTimeMillis());
		long result = mDBHelper.getWritableDatabase().update($table(), values,
				"vid=?", new String[] { vid });
		return result;
	}

	protected String[] clumns = { "_id", "name", "pswd", "nick", "vid" };

	// public UserInfo get(String name) {
	// List<UserInfo> lstInfo = select(new UserInfo(name));// , pswd
	// if (lstInfo != null && lstInfo.size() > 0) {
	// return lstInfo.get(0);
	// } else {
	// return null;
	// }
	// }

	public List<UserInfo> select(UserInfo info) {
		List<UserInfo> lstResult = new ArrayList<UserInfo>();
		{
			String selection = null;
			String[] selectionArgs = null;
			if (info != null) {// 密码暂不验证
				// if (info.name != null) {
				// selection = "name=?";
				// selectionArgs = new String[] { info.name };
				// }
				selection = "vid=?";
				selectionArgs = new String[] { info.vid + "" };

			}
			Cursor cur = mDBHelper.getReadableDatabase().query($table(),
					clumns, selection, selectionArgs, null, null,
					"sysMdfDate DESC");
			if (cur != null && cur.moveToFirst()) {

				do {
					UserInfo tmp = new UserInfo();
					{
						tmp._id = cur.getInt(cur.getColumnIndex("_id"));
						tmp.name = cur.getString(cur.getColumnIndex("name"));
						tmp.pswd = cur.getString(cur.getColumnIndex("pswd"));
						tmp.nick = cur.getString(cur.getColumnIndex("nick"));
						tmp.vid = cur.getString(cur.getColumnIndex("vid"));
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
	private static UserDal mUserDal;

	/**
	 * 私有化构造方法
	 */

	private UserDal(Context context) {
		mDBHelper = new DBHelper(context);
	}

	/**
	 * 静态工厂方法
	 */
	public synchronized static UserDal getInstance(Context context) {
		if (mUserDal == null) {
			mUserDal = new UserDal(context);
		}
		return mUserDal;
	}
}
