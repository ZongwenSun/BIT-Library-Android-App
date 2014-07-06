package com.android.szw.libbookreminder;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;


/**
 * TODO<Check whether there are books which need to return>
 * @author  SunZongwen
 * @data:  2014-7-6 下午10:21:25
 * @version:  V1.0
 */
public class CheckService extends Service {
	private final String TAG = "CheckService";
	private HttpConnectionHelper httpConnectionHelper = null;
	private AccountManager accountManager = null;
	private String today = null;
	@Override
	public void onCreate() {
		super.onCreate();
		httpConnectionHelper = new HttpConnectionHelper(this, new ServiceHandler(Looper.getMainLooper()));
		accountManager = AccountManager.getInstance(this); 
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!haveChecked()){
			String number = accountManager.getNumber();
			String password = accountManager.getPassword();
			if(number != null && password != null) {
				httpConnectionHelper.login(number, password);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	private boolean haveChecked() {
		String today = MyDate.getDate();
		String latestCheckDate = accountManager.getLatestCheckDate();
		if(latestCheckDate == null){
			return false;
		}
		else {
			return today.equals(latestCheckDate);
		}
	}
	private void checkBookInfoList(ArrayList<BookInfo> bookInfos) {
		String today = MyDate.getDate();
		ArrayList<Integer> warnBookList = new ArrayList<Integer>();
		int preRemindDay = accountManager.getPreRemindDay();
		for (int i = 0; i < bookInfos.size(); i++) {
			if(MyDate.dayBetween(bookInfos.get(i).getReturnTime(), today) < preRemindDay){
				warnBookList.add(i);
			}
		}
		if (warnBookList.size() > 0) {
			int mId = 1;
			String Title = warnBookList.size()+"本书即将过期!";
			String Content = "";
			for(int i = 0; i <warnBookList.size(); i ++) {
				Content += bookInfos.get(warnBookList.get(i)).getName() + "\n";
			}
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle(Title)
			        .setContentText(Content);
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(this, BookInfoActivity.class);
			resultIntent.putExtra("booklist", (ArrayList<BookInfo>)bookInfos);
		

			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(BookInfoActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(mId, mBuilder.build());
			
		}
		
	}
	private class ServiceHandler extends Handler{
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_BOOKINFO:
				ArrayList<BookInfo> bookInfoList = (ArrayList<BookInfo>) msg.obj;
				checkBookInfoList(bookInfoList);
				break;
			case Constants.MSG_LOGIN_SUCCEED:
				httpConnectionHelper.getBookInfo();
				break;
			default:
				break;
			}
		}
		
	}
	
	

}
