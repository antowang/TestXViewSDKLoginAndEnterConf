package com.cinlan.xview.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;

public class SPUtil {

	private static final String CONFIG_NAME = "config";

	public SPUtil() {
	}

	/**
	 * chicun:[ 分辨率 - PublicInfo.SupportLevel ]<br>
	 * account:[用户名]<br>
	 * password:[密码]<br>
	 * ip:[服务器地址]<br>
	 * port:[服务器端口]<br>
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getConfigStrValue(Context context, String key) {
		if (context == null) {
			throw new NullPointerException(" context is null");
		}
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);

		return sf.getString(key, "");
	}

	/**
	 * local:[ (0-无状态), (1-有摄像头已被打开), (2-无摄像头), (4-有摄像头但被禁用) ]<br>
	 * camera:[ (0-前置), (1-后置) ]<br>
	 * ccindex:[ 摄像头索引 ]<br>
	 * zl:[帧率]<br>
	 * videoFlow:[ 视频流量 ]<br>
	 * viewModePosition:[1-垂直方向,0-水平方向]<br>
	 * 
	 * @param context
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	public static int getConfigIntValue(Context context, String key,
			int defaultVal) {
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sf.getInt(key, defaultVal);
	}

	/**
	 * chicun:[ 分辨率 - PublicInfo.SupportLevel ]<br>
	 * account:[用户名]<br>
	 * password:[密码]<br>
	 * ip:[服务器地址]<br>
	 * port:[服务器端口]<br>
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean putConfigStrValue(Context context, String key,
			String value) {
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		Editor e = sf.edit();
		e.putString(key, value);
		return e.commit();
	}

	/**
	 * local:[ (0-无状态), (1-有摄像头已被打开), (2-无摄像头), (4-有摄像头但被禁用) ]<br>
	 * camera:[ (0-前置), (1-后置) ]<br>
	 * ccindex:[ 摄像头索引 ]<br>
	 * videoFlow:[ 视频流量 ]<br>
	 * viewModePosition:[1-垂直方向,0-水平方向]<br>
	 * zl:[帧率]
	 * 
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean putConfigIntValue(Context context, String key,
			int value) {
		SharedPreferences sf = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		Editor e = sf.edit();
		e.putInt(key, value);
		return e.commit();
	}

	public static void putConfigBoolean(Context context, String key,
			boolean value) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getConfigBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}

	public static void putConfigLong(Context context, String key, long value) {

		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		sp.edit().putLong(key, value).commit();
	}

	public static Long getConfigLong(Context context, String key, long defValue) {
		SharedPreferences sp = context.getSharedPreferences(CONFIG_NAME,
				Context.MODE_PRIVATE);
		return sp.getLong(key, defValue);
	}

	public static String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };

			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {

			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;

	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkCurrentAviNetwork(Context context) {
		if (context == null) {
			throw new NullPointerException("Invalid context object");
		}
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		// need to check mobile !=null, because no mobile network data in PAD
		if (wifi.isConnected() || (mobile != null && mobile.isConnected())) {
			return true;
		} else {
			return false;
		}
	}

}
