package com.cinlan.xview.utils;

import android.util.Log;

public class XviewLog {

	public static final String TAG = "XViewSDK";

	public static boolean isDebuggable = true;

	public static void i(String tag, String msg) {
		if (isDebuggable)
			Log.i(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (isDebuggable)
			Log.e(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (isDebuggable)
			Log.w(tag, msg);
	}

	public static void d(String tag, String msg) {
		if (isDebuggable)
			Log.d(tag, msg);
	}

	public static void i(String msg) {
		if (isDebuggable)
			Log.i(TAG, msg);
	}

	public static void e(String msg) {
		if (isDebuggable)
			Log.e(TAG, msg);
	}

	public static void w(String msg) {
		if (isDebuggable)
			Log.w(TAG, msg);
	}

	public static void d(String msg) {
		if (isDebuggable)
			Log.d(TAG, msg);

	}

}
