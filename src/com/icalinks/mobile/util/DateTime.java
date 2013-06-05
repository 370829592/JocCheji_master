package com.icalinks.mobile.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTime {
	private Date mDate;

	private DateTime(long timeMillis) {
		mDate = new Date(timeMillis);
	}

	public DateTime() {
		mDate = new Date(System.currentTimeMillis());
	}

	public static DateTime now() {
		return new DateTime();
	}

	public static DateTime from(DateTime datetime) {
		return new DateTime(datetime.toTimeMillis());
	}

	public static DateTime from(long timeMillis) {
		return new DateTime(timeMillis);
	}

	public static DateTime from(Date date) {
		return new DateTime(date.getTime());
	}

	public static DateTime from(String datetime, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = df.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateTime.from(date);
	}

	public static DateTime from(String datetime, SimpleDateFormat dateformat) {
		Date date = null;
		try {
			date = dateformat.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateTime.from(date);
	}

	// "EEE MMM dd HH:mm:ss zZ yyyy"
	public static DateTime fromEnglish(String datetime) {
		SimpleDateFormat dateformat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy", Locale.ENGLISH);
		Date date = null;
		try {
			date = dateformat.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return DateTime.from(date);
	}

	public static DateTime from(String datetime) {
		return DateTime.from(datetime, mFormat);
	}

	public long toTimeMillis() {
		return mDate.getTime();
	}

	private static String mFormat;
	private static SimpleDateFormat mSimpleDateFormat;
	static {
		// mFormat = "yyyy-MM-dd kk:mm:ss";
		mFormat = "yyyy-MM-dd HH:mm:ss";
		mSimpleDateFormat = new SimpleDateFormat(mFormat);
	}

	public String toString() {
		return mSimpleDateFormat.format(mDate);
	}

	public String toString(String format) {
		return new SimpleDateFormat(format).format(mDate);
	}

	public DateTime addDay(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(mDate);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + day);
		return new DateTime(calendar.getTime().getTime());
	}

	public DateTime setYear(int year) {
		mDate.setYear(year - 1900);
		return this;
	}

	public int getYear() {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(mDate);
		// return calendar.get(Calendar.YEAR);

		return mDate.getYear() + 1900;
	}

	public DateTime setMonth(int month) {
		mDate.setMonth(month);
		return this;
	}

	public int getMonth() {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(mDate);
		// return calendar.get(Calendar.MONTH);
		return mDate.getMonth();
	}

	public DateTime setDay(int day) {
		mDate.setDate(day);
		return this;
	}

	public int getDay() {
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(mDate);
		// return calendar.get(Calendar.DAY_OF_MONTH);
		return mDate.getDate();
	}

	public DateTime setHours(int hours) {
		mDate.setHours(hours);
		return this;
	}

	public int getHours() {
		return mDate.getHours();
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(mDate);
		// return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public DateTime setMinutes(int minutes) {
		mDate.setMinutes(minutes);
		return this;
	}

	public int getMinutes() {
		return mDate.getMinutes();
		// Calendar calendar = Calendar.getInstance();
		// calendar.setTime(mDate);
		// return calendar.get(Calendar.HOUR_OF_DAY);
	}
}
