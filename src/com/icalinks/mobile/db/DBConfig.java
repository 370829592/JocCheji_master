package com.icalinks.mobile.db;

public class DBConfig {
	public static final int VERSION = 1;
	public static final String NAME = "joc.dbx";
	public static final String[] TABLES;
	// SQL
	static {
		TABLES = new String[3];
		// // user
		{
			String sql = "";
			sql += "CREATE TABLE [user] (";
			sql += "[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,";
			sql += "[name] TEXT NOT NULL,";
			sql += "[pswd] TEXT,";
			sql += "[nick] TEXT,";
			sql += "[vid] TEXT,";
			sql += "[sysAddDate] INTEGER,";
			sql += "[sysMdfDate] INTEGER);";
			TABLES[0] = sql;
		}

		// // navi
		{
			String sql = "";
			sql += "CREATE TABLE [navi] (";
			sql += "[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,";
			sql += "[vid] TEXT,";
			sql += "[navitype] INTEGER NOT NULL,";
			sql += "[datetime] TEXT NOT NULL,";
			sql += "[begpoint] TEXT NOT NULL,";
			sql += "[begaddrs] TEXT NOT NULL,";
			sql += "[endpoint] TEXT NOT NULL,";
			sql += "[endaddrs] TEXT NOT NULL,";
			sql += "[sysAddDate] INTEGER,";
			sql += "[sysMdfDate] INTEGER);";
			TABLES[1] = sql;
		}

		// msgs
		{
			String sql = "";
			sql += "CREATE TABLE [msgs] (";
			sql += "[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,";
			sql += "[vid] TEXT,";
			sql += "[msgsid] TEXT,";
			sql += "[msgstype] INTEGER NOT NULL,";
			sql += "[datetime] TEXT NOT NULL,";
			sql += "[isread] TEXT NOT NULL,";
			sql += "[title] TEXT NOT NULL,";
			sql += "[content] TEXT NOT NULL,";
			sql += "[sysAddDate] INTEGER,";
			sql += "[sysMdfDate] INTEGER);";
			TABLES[2] = sql;
		}
	}
}
