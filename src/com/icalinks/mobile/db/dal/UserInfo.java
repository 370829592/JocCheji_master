package com.icalinks.mobile.db.dal;

import java.lang.reflect.Field;

public class UserInfo {
	public String name;
	public String pswd;
	public String vid;
	public String nick;

	public UserInfo(String name, String pswd, String vid, String nick) {
		super();
		this.name = name;
		this.pswd = pswd;
		this.vid = vid;
		this.nick = nick;
	}

	public UserInfo(String name, String pswd) {
		super();
		this.name = name;
		this.pswd = pswd;
	}

	public UserInfo(String name) {
		super();
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public UserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public int _id;

	/**
	 * 输出当前对象中的所有字段的值
	 */
	public String toString() {
		StringBuffer stbResult = new StringBuffer();
		try {
			Class cls = Class.forName(this.getClass().getName());
			Field[] fields = cls.getFields();
			stbResult.append(this.getClass().getName() + "::");
			for (Field field : fields) {// .getType()
				stbResult.append(field.getName() + "=" + field.get(this) + ";");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stbResult.toString();
	}
}
