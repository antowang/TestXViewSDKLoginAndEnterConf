package com.cinlan.xview.msg;

public class MsgType {

	public static final int ENTERCONF_RESULT = 1;
	public static final int CONFLIST = 2;
	public static final int MEMBER_ENTER = 3;
	public static final int MEMBER_EXIT = 4;
	public static final int PERIMSSTYPE = 5;
	public static final int DOCHASCOME = 6;
	public static final int PAGECOUNTCOME = 7;
	public static final int PAGE_DISPLAY = 8;
	public static final int DOCCLOSE = 9;
	public static final int DATACOME = 10;
	public static final int KICK_CONF = 11;
	public static final int DISCONNECTED = 12;
	public static final int MEDIA_MIXER = 13;
	public static final int MEDIA_REMOVE = 14;
	public static final int VIDEO_LIST = 15;
	public static final int CONF_MUTE = 16;
	public static final int MESSAGE_LIST = 17;

	@Deprecated
	public static final int LOGOUT_MSG = 18;

	public static final int VIDEOREMOTE_SETTING_COME = 19;
	public static final int SYNC_OPEN_VIDEO = 21;
	public static final int SYNC_CLOSE_VIDEO = 22;
	public static final int MODIFY_CONF_DESC = 23;


	public static final int LOGOUT_SELF = 24;

	public static final int LOGOUT_OTHER = 25;

}
