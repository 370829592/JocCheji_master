package com.icalinks.mobile.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) { 
		super(context, DBConfig.NAME, null, DBConfig.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) { 
		for (int i = 0; i < DBConfig.TABLES.length; i++) {
			db.execSQL(DBConfig.TABLES[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DBConfig.NAME);
		onCreate(db);
	}
}
