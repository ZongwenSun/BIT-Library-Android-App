package com.android.szw.libbookreminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO<Date Tool class>
 * @author  SunZongwen
 * @data:  2014-7-6 ÏÂÎç10:24:11
 * @version:  V1.0
 */
public class MyDate {
	public static final String format = "yyyy-MM-dd";
	/** 
	 * TODO<get date today>
	 * @return String "yyyy-MM-dd"
	 */
	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat dateformat = new SimpleDateFormat(format);
		return dateformat.format(date);
	}
	
	/** 
	 * TODO<calculate the days between two date>
	 * @param big
	 * @param small
	 * @return int
	 */
	public static int dayBetween(String big, String small) {
		SimpleDateFormat dateformat = new SimpleDateFormat(format);
		Date bigDate;
		Date smallDate;
		try {
			bigDate = dateformat.parse(big);
			smallDate = dateformat.parse(small);
			Calendar bigCalendar = Calendar.getInstance();
			bigCalendar.setTime(bigDate);
			Calendar smallCalendar = Calendar.getInstance();
			smallCalendar.setTime(smallDate);
			return (bigCalendar.get(Calendar.DAY_OF_YEAR) - smallCalendar.get(Calendar.DAY_OF_YEAR));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
		

	}
}
