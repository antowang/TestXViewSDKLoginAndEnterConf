package com.cinlan.xview.utils;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;


public class GlobalConfig {
	
	public static final int CONF = 1;
	
	public static final int ANDROID = 2;
	
	public static final String DATA_PATH = Environment.getExternalStorageDirectory().getPath() + "/Xview/";
	public static final String IMAGE_CACHE_PATH = DATA_PATH + "cache/pics/";
	
	/**
	 * Global request type for conference
	 */
	public static final int REQUEST_TYPE_CONF = 1;
	
	/**
	 * Global request type for IM
	 */
	public static final int REQUEST_TYPE_IM = 2;
	
	
	/**
	 * User state for on line
	 */
	public static final int USER_STATUS_ONLINE = 1;

	/**
	 * User state for leaved
	 */
	public static final int USER_STATUS_LEAVING = 2;

	/**
	 * User state for busy
	 */
	public static final int USER_STATUS_BUSY = 3;

	/**
	 * User state for do not disturb
	 */
	public static final int USER_STATUS_DO_NOT_DISTURB = 4;
	
	/**
	 * User state for hidden
	 */
	public static final int USER_STATUS_HIDDEN = 5;

	/**
	 * User state for off line
	 */
	public static final int USER_STATUS_OFFLINE = 0;
	
	
	/**
	 * error conference code for user deleted conference  
	 */
	public static final int CONF_CODE_DELETED = 204;
	
	
	
	/**
	 * Indicate send on line file
	 */
	public static final int FILE_TYPE_ONLINE = 1;
	
	/**
	 * Indicate send off line file
	 */
	public static final int FILE_TYPE_OFFLINE = 2;
	
	
	
	
	
	

	public static final String KEY_LOGGED_IN = "LoggedIn";

	public static int GLOBAL_DPI = DisplayMetrics.DENSITY_XHIGH;

	public static int GLOBAL_VERSION_CODE = 1;

	public static String GLOBAL_VERSION_NAME = "1.3.0.1";

	public static double SCREEN_INCHES = 0;
	
	public static boolean isConversationOpen = false;

	
	

	public static void saveLogoutFlag(Context context) {
		SPUtil.putConfigIntValue(context, KEY_LOGGED_IN, 0);
	}
	
	
	
	
	
	
	
	
	public static String getGlobalPath() {
		return StorageUtil.getAbsoluteSdcardPath()+"/.xview/";
	}
	
	public static String getGlobalUserAvatarPath() {
		return StorageUtil.getAbsoluteSdcardPath()+"/.xview/Users";
	}
	
	
	public static String getGlobalPicsPath() {
		return StorageUtil.getAbsoluteSdcardPath()+"/.xview/pics";
	}
	
	
	public static String getGlobalAudioPath() {
		return StorageUtil.getAbsoluteSdcardPath()+"/.xview/audio";
	}
	
	public static String getGlobalFilePath() {
		return StorageUtil.getAbsoluteSdcardPath()+"/xview/file";
	}
	
	
	
	static class EmojiWraper {
		String emojiStr;
		int id;
		
		public EmojiWraper(String emojiStr, int id) {
			super();
			this.emojiStr = emojiStr;
			this.id = id;
		}
		
	}
}
