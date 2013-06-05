package com.icalinks.mobile.db.dal;

import android.database.Cursor;

public abstract class BaseDal {
	protected abstract Object cur2obj(Cursor cur);
}
