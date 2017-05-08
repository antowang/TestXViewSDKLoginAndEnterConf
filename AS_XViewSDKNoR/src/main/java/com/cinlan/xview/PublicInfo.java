package com.cinlan.xview;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.cinlan.jni.ImRequest;
import com.cinlan.xview.utils.XviewLog;
import com.cinlankeji.khb.iphone.R;

public class PublicInfo {
	/**
	 * 服务器地址
	 */
	public static final String XVIEW_SERVER = "video.kaihuibao.cn";
	/**
	 * 服务器端口号
	 */
	public static final String XVIEW_PORT = "18181";
	/**
	 * 向ColumnLayout发送的打开文件柜的信号
	 */
	public static final int OPEN_SHARE_TO_COLUMNLAYOUT = 1002;
	public static final int OPEN_SHARE_TO_VIDEOFRAGMENT = 1010;
	/**
	 * 向ColumnLayout发送的关闭文件柜的信号
	 */
	public static final int CLOSE_SHARE_TO_COLUMNLAYOUT = 1004;
	public static final int CLOSE_SHARE_TO_VIDEOFRAGMENT = 1011;

	/**
	 * 准备匿名登录
	 */
	public static final int FLAG_ANONYMOUS_LOGIN = 1007;
	/**
	 * 通知ConfListActivity自毁.
	 */
	public static final int CLOSE_CONFLISTACTIVITY = 1008;
	/**
	 * 显示或隐藏底边栏
	 */
	public static final int SHOW_OR_HIDE_BOTTOMBAR = 1010;

	/**
	 * 用于滑动文档时翻页
	 */
	public static final int SHAREDOC_FLING_SIGNAL = 1012;
	/**
	 * 尝试再次登录
	 */
	public static final int TRY_LOGIN = 1015;
	public static final int connected = 1016;
	/**
	 * 解除ConfListReceiver和ForceOfflineReceiver注册
	 */
	public static final int UNREGIStER_RECEIVER = 1018;

	/**
	 * 设备方向 1:竖屏 0:横屏
	 */
	public static int DEVICE_ORIENTATION = 1;

	/**
	 * 用于向ColumnLayout发送消息的Handler.
	 */
	public static Handler columnLayoutHandler;
	/**
	 * 用于向ConfActivity发送消息的Handler.
	 */
	public static Handler confActivityHandler;
	public static Handler confListRefreshHandler;
	/**
	 * 用于在Video_fragment中接收消息的Handler
	 */
	public static Handler videoFragmentHandler;
	/**
	 * 用于广播通知LoginActivity网络已连接的Handler.
	 */
	public static Handler loginActivityHandler;
	/**
	 * 是否是匿名登录
	 */
	public static boolean isAnonymousLogin = false;
	/**
	 * 是否已经打开会议列表
	 */
	public static boolean isOpenedConfList = false;
	/**
	 * 是否已经关闭本地视频
	 */
	public static boolean isClosedLocalVideo = false;
	/**
	 * 是否打开了文件柜
	 */
	public static boolean isOpenedShareList = false;
	/**
	 * 是否打开了参会者列表
	 */
	public static boolean isOpenedUserList = false;
	/**
	 * 是否可以执行会议列表的刷新,不判断就刷新会crash
	 */
	public static boolean isExecConfListRefresh = false;
	/**
	 * 是否可以执行参会者列表的刷新,不判断就刷新会crash
	 */
	public static boolean isExecUserListRefresh = false;
	public static boolean isAnonymousLoginSucess = false;
	/**
	 * 是否已经网络断开连接
	 */
	public static boolean isBreakLink = false;
	/**
	 * 程序是否是第一次运行
	 */
	public static boolean isFirstRunning = true;
	/**
	 * 已经打开的视频路数
	 */
	public static int OPENED_VIDEO_COUNT = 0;
	/**
	 * 屏幕宽度
	 */
	public static int screenWidth = 0;
	/**
	 * 屏幕高度
	 */
	public static int screenHeight = 0;
	/**
	 * 底边栏高度
	 */
	public static int heightBottomBar = 0;
	/**
	 * 0:未登录 1:登录失败 2:登录成功
	 */
	public static int AnonymousLoginState = 0;
	/**
	 *  sdk调用logout时，置为1.否则置为0.
	 */
	public static int logoutFlag = 0;
	/**
	 * 超清
	 */
	public static String Support1Level = "";
	/**
	 * 高清
	 */
	public static String Support2Level = "";
	/**
	 * 标清
	 */
	public static String Support3Level = "";
	/**
	 * 流畅
	 */
	public static String Support4Level = "";
	public static String tempConfPassword = "";
	public static String VideoDevInfo = "";
	/**
	 * 当前是哪个Activity
	 */
	public static String currentActivity = "";
	public static String xviewServer = "";
	public static String xviewPort = "";
	/**
	 * 最高分辨率1280X720
	 */
	public static String SUPER_HIGH_SIZE = "1280X720";
	private static java.lang.String XTAG = PublicInfo.class.getSimpleName();

	public static int[] parseDeviceListXml(String info, int selectedindex,
			String tag) {
		info = info.replace("<devicelist>", " ");
		info = info.replace("</devicelist>", " ");
		String s[] = info.trim().split("</device>");
		for (String st : s) {
			if (st.contains(tag)) {
				info = st;
			}
		}

		int index = info.indexOf(">");
		String q = (String) info.subSequence(0, index + 1);
		info = info.replace(q, " ").trim();
		String r[] = info.split("size");

		List<String> datas = new ArrayList<String>();
		for (int i = 0; i < r.length; i++) {
			if (r[i].length() > 5) {
				r[i] = r[i].replace("></", " ").trim();
				datas.add(r[i]);
			}
		}
		if (selectedindex >= datas.size()) {
			selectedindex = datas.size() - 1;
		}
		String temp = datas.get(selectedindex);
		String result[] = temp.split(" ");
		String w = result[0].replace("width=", " ").replace("'", " ").trim();
		String h = result[1].replace("height=", " ").replace("'", " ").trim();
		int width = Integer.parseInt(w);
		int height = Integer.parseInt(h);

		int[] intArray = { width, height };

		return intArray;
	}
	private static ProgressDialog proDialog;
	/**
	 * 注销账号
	 * 
	 * @param context
	 */
	public static void logout(Context context) {
//		proDialog = android.app.ProgressDialog.show(context, "",
//				context.getResources().getString(R.string.logingout_xviewsdk));
//		proDialog.show();
		XviewLog.i(XTAG, "Logout");
		ImRequest.getInstance().logout();
	}

	public static void dismissDialog() {
		XviewLog.i(XTAG, "dismissDialog");
		if (proDialog != null)
			proDialog.dismiss();
	}

	/**
	 * 公用Toast方法
	 * 
	 * @param context
	 * @param resId
	 */
	public static void toast(Context context, int resId) {
		Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
	}
}
