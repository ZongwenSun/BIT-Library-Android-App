package com.android.szw.libbookreminder;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * TODO<manage some setting info, such as user's number,psssword,latest_check_date, the days before return Date to remind the user
 * * these info is saved in xml file>
 * @author  SunZongwen
 * @data:  2014-7-6 ÏÂÎç10:13:37
 * @version:  V1.0
 */
public class AccountManager {
	/*
	 * manage user's number and psssword, save them in xml file and retrieve them from xml file
	 */
	private final String XMLFILE = "library.xml";
	private Context context = null;
	SharedPreferences sharedPref = null;
	private static AccountManager mInstance = null;
	public static AccountManager getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new AccountManager(context);		
		}
		return mInstance;
	}
	private AccountManager(Context context){
		this.context = context.getApplicationContext();
		sharedPref = context.getSharedPreferences(XMLFILE, Context.MODE_PRIVATE);

	}
	public String getNumber() {
		return sharedPref.getString("number", null);
	}
	public String getPassword() {
		return sharedPref.getString("password", null);
	}
	public void setAccount(String number, String pass) {
		sharedPref.edit().putString("number", number).putString("password", pass).commit();
	}
	public void clearAccount() {
		sharedPref.edit().remove("number").remove("password").commit();
	}
	public String getLatestCheckDate(){
		return sharedPref.getString("latest_check_date", null);
	}
	
	public void setLatestCheckDate(String date) {
		sharedPref.edit().putString("latest_check_date", date).commit();
	}
	
	public int getPreRemindDay() {
		return sharedPref.getInt("pre_remind_day", 3);
	}
	public void setPreRemindDay(int day){
		sharedPref.edit().putInt("pre_remind_day", day).commit();
	}
	
	public void setAutoLogin(boolean choice){
		sharedPref.edit().putBoolean("auto_login", choice);
	}
	public boolean getAutoLogin() {
		return sharedPref.getBoolean("auto_login", false);
	}
}
