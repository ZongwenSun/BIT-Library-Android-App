package com.android.szw.libbookreminder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

/**
 * TODO<Communicate with library web site to Login and download book information>
 * 
 * @author SunZongwen
 * @data: 2014-7-6 ÏÂÎç9:40:15
 * @version: V1.0
 */
public class HttpConnectionHelper {

	public final String TAG = "HttpConnectionHelper";
	private Context mContext = null;
	// the Handler is passed from a activity or service, it's used for communication between background thread and UI thread
	private Handler mHandler = null;
	String mCookieString = null;

	public boolean mHasLogin = false;

	public HttpConnectionHelper(Context context, Handler handler) {
		mContext = context.getApplicationContext();
		mHandler = handler;
	}

	/** 
	 * TODO<judge whether the device is connected to network>
	 * only judge wifi, you can change it to allow using mobile connect
	 * @return boolean
	 */
	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return (networkInfo != null && networkInfo.isConnected());
	}

	public void login(String number, String password) {
		Thread loginTherad = new Thread(new LoginRunnable(number, password));
		loginTherad.start();
	}

	public void getBookInfo() {
		Thread thread = new Thread(new GetBookInfoRunnable());
		thread.start();
	}

	/**
	 * TODO<Login thread, http response code 302 means Login succeed, then get the cookies which is needed for following requests>
	 * @author  SunZongwen
	 * @data:  2014-7-6 ÏÂÎç9:47:27
	 * @version:  V1.0
	 */
	private class LoginRunnable implements Runnable {
		private String mNumber;
		private String mPassword;

		public LoginRunnable(String number, String password) {
			mNumber = number;
			mPassword = password;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String mVerifyUrlString = "http://ico.bit.edu.cn/reader/redr_verify.php";
			URL url = null;
			HttpURLConnection httpConn = null;
			InputStream in = null;
			int trytimes = 0;

			String postString = "?select=cert_no&number=" + mNumber
					+ "&passwd=" + mPassword + "&submit="
					+ URLEncoder.encode("µÇÂ¼") + "&returnUrl=";
			try {
				// if we only try one time, we'll get 200, the second time 302, I don't know why...,
				// it's just out of practice
				while (trytimes < 2) {
					url = new URL(mVerifyUrlString + postString);
					httpConn = (HttpURLConnection) url.openConnection();

					httpConn.setFollowRedirects(false);
					// httpConn.setDoOutput(true);
					// httpConn.setDoInput(true);
					httpConn.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2041.4 Safari/537.36");
					httpConn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					httpConn.setRequestProperty("Referer",
							"http://lib.bit.edu.cn/");
					httpConn.setRequestMethod("HEAD");
					/*
					 * PrintStream send; send = new
					 * PrintStream(httpConn.getOutputStream());
					 * send.print(postString); send.flush(); send.close();
					 */
					// httpConn.getInputStream();
					int responseCode = 0;
					responseCode = httpConn.getResponseCode();
					// 302 means number and password is correct, so we will get the cookie
					if (responseCode == 302) {
						String session_value = httpConn
								.getHeaderField("Set-Cookie");
						String[] sessionId = session_value.split(";");
						mCookieString = sessionId[0];
						httpConn.disconnect();
						mHandler.obtainMessage(Constants.MSG_LOGIN_SUCCEED)
								.sendToTarget();
						break;
					} else if (responseCode == 200) {
						trytimes++;
						httpConn.disconnect();
						// mHandler.obtainMessage(Constants.MSG_USER_PASS_ERR).sendToTarget();
					} else {
						httpConn.disconnect();
						break;
					}

				}
				if (trytimes == 2) {
					mHandler.obtainMessage(Constants.MSG_USER_PASS_ERR)
							.sendToTarget();
				}
			} catch (Exception e) {
				mHandler.obtainMessage(Constants.MSG_INTERNET_ERR)
						.sendToTarget();
			}
		}

	}

	/**
	 * TODO<A runnable for thread to get the information of borrowed books, first get the html, then use regrex to parse it>
	 * @author  SunZongwen
	 * @data:  2014-7-6 ÏÂÎç9:56:13
	 * @version:  V1.0
	 */
	private class GetBookInfoRunnable implements Runnable {
		private String aUrlString = "http://ico.bit.edu.cn/reader/book_lst.php";

		@Override
		public void run() {
			try {
				URL url = new URL(aUrlString);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url
						.openConnection();
				httpURLConnection
						.setRequestProperty(
								"User-Agent",
								"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2041.4 Safari/537.36");
				httpURLConnection.setRequestProperty("Referer",
						"http://lib.bit.edu.cn/");
				httpURLConnection.setRequestMethod("GET");
				httpURLConnection.setRequestProperty("Cookie", mCookieString);

				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(
								httpURLConnection.getInputStream(), "UTF-8"));

				ArrayList<BookInfo> bookInfoList = RegrexTool
						.getBookInfo(bufferedReader);
				bufferedReader.close();
				httpURLConnection.disconnect();
				mHandler.obtainMessage(Constants.MSG_BOOKINFO, bookInfoList)
						.sendToTarget();

			} catch (Exception e) {
				mHandler.obtainMessage(Constants.MSG_BOOKINFO_ERR)
						.sendToTarget();

				e.printStackTrace();
			}

		}

	}
}
