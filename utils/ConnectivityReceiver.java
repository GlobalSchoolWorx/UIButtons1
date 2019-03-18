package com.edu.worx.global.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectivityReceiver extends BroadcastReceiver {
	
	private static Boolean mIsNetworkConnectivityAvaliable = null;
	private static ArrayList<OnConnectivityChangeListener> mListeners = null;
	private static HashMap<String, OnConnectivityChangeListener> mListenersMap = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(ConnectivityReceiver.class.getSimpleName(), "action: "
				+ intent.getAction());
		mIsNetworkConnectivityAvaliable = hasNetworkConnection(context);
		if(null != mListeners){
			for(OnConnectivityChangeListener l: mListeners){
				if(null != l){
					l.onConnectivityChanged(mIsNetworkConnectivityAvaliable);
				}
			}
		}
	} 

	private static boolean hasNetworkConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {		        
			if (ni.isConnected())
				return true;		        
		}
		return false;
	}

	public static boolean isConnected(Context context){
		if(null == mIsNetworkConnectivityAvaliable){
			mIsNetworkConnectivityAvaliable = hasNetworkConnection(context);
		}
		
		return mIsNetworkConnectivityAvaliable;
	}
	
	public static void setOnConnectivityChangedListener(OnConnectivityChangeListener l){
		if(null == mListeners){
			mListeners = new ArrayList<>();
		}

		if(null == mListenersMap){
			mListenersMap = new HashMap<>();
		}

		if(!mListeners.contains(l)){
			mListeners.add(l);
			String key = l.getConnectivityListenerTag();
			if(null != key){
				mListenersMap.put(key, l);
			}
		}
	}

	public static void removeOnConnectivityChangedListener(OnConnectivityChangeListener l){
		if(null == mListeners){
			mListeners = new ArrayList<>();
		}

		if(null == mListenersMap){
			mListenersMap = new HashMap<>();
		}

		mListeners.remove(l);
		String key = l.getConnectivityListenerTag();
		if(null != key) {
			mListenersMap.remove(key);
		}
	}

	public static void removeOnConnectivityChangedListener(String tag){
		if(null == mListeners){
			mListeners = new ArrayList<>();
		}

		if(null == mListenersMap){
			mListenersMap = new HashMap<>();
		}

		OnConnectivityChangeListener l = mListenersMap.remove(tag);
		mListeners.remove(l);
	}

	public interface OnConnectivityChangeListener{
		void onConnectivityChanged(boolean isConnected);
		String getConnectivityListenerTag();
	}
}
