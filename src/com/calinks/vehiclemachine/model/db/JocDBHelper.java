package com.calinks.vehiclemachine.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JocDBHelper extends SQLiteOpenHelper {

	public JocDBHelper(Context context) {
		super(context, "joc2.db", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(DBConfig.CREATE_TABLE_1);
		db.execSQL(DBConfig.CREATE_TABLE_2);
		db.execSQL(DBConfig.THE_FIRST_INSERT_INTO_TABLE_2);
		db.execSQL(DBConfig.CREATE_TABLE_4);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + DBConfig.TABLE_NAME_1);
		db.execSQL("DROP TABLE IF EXISTS " + DBConfig.TABLE_NAME_2);
		db.execSQL("DROP TABLE IF EXISTS " + DBConfig.TABLE_NAME_4);
		onCreate(db);
	}
	
}
