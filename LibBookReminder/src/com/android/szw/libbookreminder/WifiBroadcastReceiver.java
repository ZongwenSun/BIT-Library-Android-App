package com.android.szw.libbookreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * TODO<listen for the state of wifi, when wifi is connected, start the CheckService>
 * @author  SunZongwen
 * @data:  2014-7-6 ÏÂÎç10:19:56
 * @version:  V1.0
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "WifiBroadcastReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
	    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
	    NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    if(wifi != null && wifi.isConnected()) {
	    	Intent serviceIntent = new Intent(context, CheckService.class);
	    	context.startService(serviceIntent);
	    
	    }
	}

}
