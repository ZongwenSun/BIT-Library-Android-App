package com.android.szw.libbookreminder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.conn.ManagedClientConnection;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * TODO<MainActivity is the Login Activity(the first activity)>
 * @author  SunZongwen
 * @data:  2014-7-6 ÏÂÎç10:02:55
 * @version:  V1.0
 */
public class MainActivity extends Activity {
	private final String DEBUG_TAG = "com.android.szw.MainActivity";

	
	private EditText mNumberEditText = null;
	private EditText mPasswordEditText = null;
	private TextView mInfoTextView = null;
	private Button mLoginButton = null;
	private HttpConnectionHelper mHttpConnectionHelper = null;
	private UIHandler mHandler = null;
	// AccountManager manages some setting info. 
	private AccountManager accountManager = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		accountManager = AccountManager.getInstance(this);

		mInfoTextView = (TextView)findViewById(R.id.textViewInfo);
		mLoginButton = (Button)findViewById(R.id.btnLogin);
		mNumberEditText = (EditText)findViewById(R.id.editTextNumber);
		mPasswordEditText = (EditText)findViewById(R.id.editTextPassword);
		mNumberEditText.requestFocus();
		mHandler = new UIHandler(Looper.getMainLooper());
		mHttpConnectionHelper = new HttpConnectionHelper(MainActivity.this, mHandler);
		
		//if number != null, means user has ever login successfully, and we know his password,
		// so no need input again
		String number = accountManager.getNumber();
		if( number != null){
			String password = accountManager.getPassword();
			if(mHttpConnectionHelper.isOnline()) {
				mHttpConnectionHelper.login(number, password);
			}
			else {
				mNumberEditText.setText(number);
				mPasswordEditText.setText(password);
				mInfoTextView.setText(R.string.internet_unavailable);
			}
		}
		mLoginButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View view) {
				
				if(mHttpConnectionHelper.isOnline()) {
					if(mNumberEditText.length() > 0 && mPasswordEditText.length() > 0){
						mHttpConnectionHelper.login(mNumberEditText.getText().toString(), mPasswordEditText.getText().toString());
					}
					else {
						mInfoTextView.setText(R.string.user_password_not_null);
					}
				}
				else mInfoTextView.setText(R.string.internet_unavailable);
			
			}
		});
	}


	private class UIHandler extends Handler{
		public UIHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case Constants.MSG_INTERNET_ERR:
				mInfoTextView.setText(R.string.internet_error);
				break;
			case Constants.MSG_LOGIN_SUCCEED:
				mHttpConnectionHelper.getBookInfo();
				mInfoTextView.setText(R.string.login_succeed);
				break;
			case Constants.MSG_BOOKINFO:	
				if(accountManager.getNumber() == null) {
					accountManager.setAccount(mNumberEditText.getText().toString(), mPasswordEditText.getText().toString());
					accountManager.setLatestCheckDate(MyDate.getDate());
				}
				ArrayList<BookInfo> booklist = (ArrayList<BookInfo>)msg.obj;
				Intent intent = new Intent(MainActivity.this, BookInfoActivity.class);
				intent.putExtra("booklist", (ArrayList<BookInfo>)msg.obj);
				startActivity(intent);
				MainActivity.this.finish();
				break;
			case Constants.MSG_USER_PASS_ERR:
				mInfoTextView.setText(R.string.user_pass_error);
				break;
			case Constants.MSG_BOOKINFO_ERR:
				mInfoTextView.setText("book info download error");
				break;
			default:
				break;
			}
		}
		
	}

	
}
