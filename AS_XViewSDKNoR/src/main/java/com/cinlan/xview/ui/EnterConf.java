package com.cinlan.xview.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import com.XunLiu.jni.NativeInitializer;
import com.cinlan.core.CaptureCapability;
import com.cinlan.core.VideoCaptureDevInfo;
import com.cinlan.core.VideoCaptureDevInfo.VideoCaptureDevice;
import com.cinlan.jni.AudioRequest;
import com.cinlan.jni.ChatRequest;
import com.cinlan.jni.ConfRequest;
import com.cinlan.jni.ConfigRequest;
import com.cinlan.jni.ImRequest;
import com.cinlan.jni.VideoRequest;
import com.cinlan.jni.WBRequest;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.inter.IXVSDKCallback;
import com.cinlan.xview.msg.EnterConfType;
import com.cinlan.xview.msg.MsgType;
import com.cinlan.xview.receiver.ForceOfflineReceiver;
import com.cinlan.xview.service.JNIResponse;
import com.cinlan.xview.service.JNIService;
import com.cinlan.xview.service.LoginService;
import com.cinlan.xview.service.WeakRefHandler;
import com.cinlan.xview.service.RequestLoginResponse;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;
import com.cinlan.xview.utils.Utils;
import com.cinlan.xview.utils.XmlParserUtils;
import com.cinlan.xview.utils.XviewLog;

public class EnterConf {
	private static String TAG = EnterConf.class.getSimpleName();

	private static final int LOG_IN_CALL_BACK = 10;

	/**
	 * 参会者昵称
 	 */
	private String nickName;
	/**
	 * 会议id
	 */
	private long confId;
	/**
	 * 会议密码
	 */
	private String confPwd;
	// 第三方传来的id
	private String mUserData = "";

	// 底层通讯请求
	private ImRequest im;
	// 底层白板请求
	private WBRequest wb;
	// 底层聊天请求
	private ChatRequest chat;
	// 底层会议请求
	private ConfRequest conf;
	// 底层视频请求
	private VideoRequest video;
	// 底层音频请求
	private AudioRequest audio;
	// 底层配置请求
	private ConfigRequest mConfigRequest = new ConfigRequest();
	// 登录请求接口回调
	private LoginService mLoginService = new LoginService();

	// 是否正在用户登录
	private Boolean isLogining = false;
	private  Context mContext;
	private VideoCaptureDevInfo devInfo;
	private List<VideoCaptureDevice> deviceList;
	/**
	 * 指定范围的所有分辨率的集合
	 */
	private List<String> allAppSupport = new ArrayList<String>();
	/**
	 * 最终选择的几个分辨率的集合
	 */
	private List<String> app_support = new ArrayList<String>();
	// 服务器地址
	private String ip = PublicInfo.XVIEW_SERVER;
	// 服务器端口
	private String port = PublicInfo.XVIEW_PORT;
	private ParseDNSTask task;
	public static IXVSDKCallback mIOnXViewCallback;
	private ThreadConfList thread;
	private ConfListReceiver mConfListReceiver;
	private LocalBroadcastManager localBroadcastManager;
	private ForceOfflineReceiver receiver;

	private Handler confListHandler = new Handler(Looper.getMainLooper()) {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case LOG_IN_CALL_BACK:
					isLogining = false;
					XviewLog.i(TAG, "" + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());

					JNIResponse response = (JNIResponse) msg.obj;
					/**
					 * 登录反馈
					 */
					if (response.getResult() == JNIResponse.Result.TIME_OUT) {
						mIOnXViewCallback.onLoginResultListener(1);
						XviewLog.i(TAG, " login TIME_OUT");
						ImRequest.getInstance().logout();
					} else if (response.getResult() == JNIResponse.Result.FAILED) {
						mIOnXViewCallback.onLoginResultListener(2);
						XviewLog.i(TAG, " login FAILED");
						ImRequest.getInstance().logout();
					} else if (response.getResult() == JNIResponse.Result.CONNECT_ERROR) {
						mIOnXViewCallback.onLoginResultListener(3);
						XviewLog.i(TAG, " login CONNECT_ERROR");
						ImRequest.getInstance().logout();
					} else if (response.getResult() == JNIResponse.Result.SERVER_REJECT) {
						mIOnXViewCallback.onLoginResultListener(4);
						XviewLog.i(TAG, " login SERVER_REJECT");
						ImRequest.getInstance().logout();
					} else if (response.getResult() == JNIResponse.Result.INCORRECT_PAR) {
						mIOnXViewCallback.onLoginResultListener(10);
						XviewLog.i(TAG, " login INCORRECT_PAR");
						ImRequest.getInstance().logout();
					} else if (response.getResult() == JNIResponse.Result.UNKNOWN) {
						mIOnXViewCallback.onLoginResultListener(11);
						XviewLog.i(TAG, " login UNKNOWN");
						ImRequest.getInstance().logout();
					} else {
						XviewLog.i(TAG, " login sucess");


						User user = ((RequestLoginResponse) response).getUser();
						user.setNickName(nickName);

                        //TODO:这里不知道是为了做什么?
						GlobalHolder.getInstance().setCurrentUser(user);


                        //TODO:下面的代码要重构
						PublicInfo.isAnonymousLogin = true;
						if (PublicInfo.isAnonymousLogin) {
							PublicInfo.confListRefreshHandler = confListHandler;
							initReceiver();

                            //TODO:下面的代码是干什么的?,目测没有用,可以删除
                            Conf conf = new Conf();
                            conf.setId(confId);


                            //TODO:下面的操作是多余的
                            confListHandler.sendEmptyMessage(PublicInfo.FLAG_ANONYMOUS_LOGIN);
						}
					}

					//TODO:多余
					isLogining = false;


					break;
				case PublicInfo.FLAG_ANONYMOUS_LOGIN:
					XviewLog.i(TAG, "Anonymous enter login");
					ConfRequest.getInstance().enterConf(confId, confPwd);
					break;
				case PublicInfo.UNREGIStER_RECEIVER:
					unRegister();
					break;
			}
		}
	};
	private WeakRefHandler weakRefHandler = new WeakRefHandler(confListHandler, LOG_IN_CALL_BACK, null);

	private static EnterConf mEnterConf = null;

	private EnterConf(Context context) {
        mContext = context;
		if (PublicInfo.isFirstRunning) {
			// 用于只初始化一次底层请求
			initRequest();
			PublicInfo.isFirstRunning = false;
		}
	}


    /**
     * @param context
     * @param iOnXViewCallback
     * @return
     */
	public static EnterConf getInstance(Context context, IXVSDKCallback iOnXViewCallback) {
		XviewLog.i(TAG, "sdk version is 3.2.7.7");
        if (iOnXViewCallback == null) {
            throw new NullPointerException("iOnXViewCallback is null");
        }
        if (context == null) {
            XviewLog.i(TAG, "context is null");
            throw new NullPointerException("context is null");
        }
        mIOnXViewCallback = iOnXViewCallback;

		if (mEnterConf == null) {
			mEnterConf = new EnterConf(context.getApplicationContext());
		}
		return mEnterConf;
	}

	public boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 登录接口
	 */
	public void loginXView(long confNumber, String confPassword, String name, String userData) {
		if (confNumber == 0) {
			XviewLog.i(TAG, "loginXView confNumber == 0");
			throw new NullPointerException("conf id is null.");
		}
		if (userData == null) {
			XviewLog.i(TAG, "loginXView userData == null");
			throw new NullPointerException("userData id is null.");
		}
		this.confId = confNumber;
		this.confPwd = confPassword;
		this.nickName = name;
		this.mUserData = userData;

		XviewLog.i(TAG, "loginXView confId=" + confNumber);
		XviewLog.i(TAG, "loginXView confPwd=" + confPassword);
		XviewLog.i(TAG, "loginXView nickName=" + name);
		XviewLog.i(TAG, "loginXView userData=" + userData);

		devInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();

        //TODO:JNIService这个类有什么功能
        mContext.startService(new Intent(mContext, JNIService.class));


		deviceList = devInfo.deviceList;

		if (deviceList.size() == 0) {
			SPUtil.putConfigIntValue(mContext, "local", 2);
			XviewLog.i(TAG, "No camera, in a state if 2.");
		}
		// 计算支持和选择合适的屏幕分辨率.
		calculateAppSupport();
		// 设置配置文件目录
		String appRootFile = Utils.getAppRootFile();
		mConfigRequest.setExtStoragePath(appRootFile + "");



		login();
	}

    /**
     *  注销接口
     */
    public void logout() {
		PublicInfo.logoutFlag = 1;
		ImRequest.getInstance().logout();
    }

	/**
	 * 创建会议
	 * 
	 * @param sSubject
	 *            会议主题
	 * @param nOrgID
	 *            组织id
	 * @param sChairPasswd
	 *            主席密码
	 * @param sParticipantPasswd
	 *            普通密码
	 * @param nStartTime
	 *            开始时间 单位(秒)
	 * @param nEndTime
	 *            结束时间 单位(秒) 0表示永久会议
	 * @param nMaxParticipant
	 *            会议容纳最大人数
	 */
	public void createConf(String sSubject, long nOrgID, String sChairPasswd,
			String sParticipantPasswd, long nStartTime, long nEndTime,
			int nMaxParticipant) {

		if (sSubject.isEmpty())
			throw new IllegalArgumentException("conf subject is empty.");
		if (nOrgID == 0)
			throw new IllegalArgumentException("org id is null.");
		if (sChairPasswd.isEmpty())
			throw new IllegalArgumentException("conf chairpwd is empty.");
		if (nStartTime < 0)
			throw new IllegalArgumentException("starttime exception.");
		if (nMaxParticipant <= 0)
			throw new IllegalArgumentException(
					"the nMaxParticipant is illegal.");

		String WEB_SERVER_URL = SPUtil.getConfigStrValue(mContext,
				"WEB_SERVER_URL");
		String NAMESPACE = SPUtil.getConfigStrValue(mContext, "NAMESPACE");

		long nCreateID = 100; // Admin的id
		int nConfType = 0; // 0是本地会议,1是级联会议
		int nParticpantLimited = 0; // 1是内部会议,0是公开会议
		CreateConfTask task = new CreateConfTask(sSubject, nCreateID, nOrgID,
				sChairPasswd, sParticipantPasswd, nStartTime, nEndTime,
				nConfType, nMaxParticipant, nParticpantLimited);
		task.execute(WEB_SERVER_URL, NAMESPACE, "CreateConf");
	}

	/**
	 * 销毁会议
	 * 
	 */
	public void destroyConf(long nConfId) {
		if (nConfId == 0) {
			throw new IllegalArgumentException("conf id null.");
		}
		String WEB_SERVER_URL = SPUtil.getConfigStrValue(mContext,
				"WEB_SERVER_URL");
		String NAMESPACE = SPUtil.getConfigStrValue(mContext, "NAMESPACE");
		DestroyConfTask task = new DestroyConfTask(nConfId);
		task.execute(WEB_SERVER_URL, NAMESPACE, "DestroyConf");
	}

	private class CreateConfTask extends AsyncTask<String, Integer, String> {
		private String sSubject;
		private long nCreateID;
		private long nOrgID;
		private String sChairPasswd;
		private String sParticipantPasswd;
		private long nStartTime;
		private long nEndTime;
		private int nConfType;
		private int nMaxParticipant;
		private int nParticpantLimited;

		CreateConfTask(String sSubject, long nCreateID, long nOrgID,
				String sChairPasswd, String sParticipantPasswd,
				long nStartTime, long nEndTime, int nConfType,
				int nMaxParticipant, int nParticpantLimited) {
			this.sSubject = sSubject;
			this.nCreateID = nCreateID;
			this.nOrgID = nOrgID;
			this.sChairPasswd = sChairPasswd;
			this.sParticipantPasswd = sParticipantPasswd;
			this.nStartTime = nStartTime;
			this.nEndTime = nEndTime;
			this.nConfType = nConfType;
			this.nMaxParticipant = nMaxParticipant;
			this.nParticpantLimited = nParticpantLimited;
		}

		@Override
		protected String doInBackground(String... params) {
			return callWebService(params[0], params[1], params[2],
					this.sSubject, this.nCreateID, this.nOrgID,
					this.sChairPasswd, this.sParticipantPasswd,
					this.nStartTime, this.nEndTime, this.nConfType,
					this.nMaxParticipant, this.nParticpantLimited);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mIOnXViewCallback.onCreateConfCallback(result);
		}
	}

	private class DestroyConfTask extends AsyncTask<String, Integer, String> {
		private long confId;

		DestroyConfTask(long confId) {
			this.confId = confId;
		}

		@Override
		protected String doInBackground(String... params) {
			return callDestroyConfWebService(params[0], params[1], params[2],
					this.confId);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mIOnXViewCallback.onDestroyConfCallback(Integer.parseInt(result));
		}
	}

	/**
	 * 调用创建会议WebService
	 * 
	 * @param webServerUrl
	 * @param nameSpace
	 * @param methodName
	 * @param sSubject
	 * @param nCreateID
	 * @param nOrgID
	 * @param sChairPasswd
	 * @param sParticipantPasswd
	 * @param nStartTime
	 * @param nEndTime
	 * @param nConfType
	 * @param nMaxParticipant
	 * @param nParticpantLimited
	 * @return
	 */
	private String callWebService(String webServerUrl, String nameSpace,
			String methodName, String sSubject, long nCreateID, long nOrgID,
			String sChairPasswd, String sParticipantPasswd, long nStartTime,
			long nEndTime, int nConfType, int nMaxParticipant,
			int nParticpantLimited) {

		// 创建HttpTransportSE对象，传递WebService服务器地址
		final HttpTransportSE httpTransportSE = new HttpTransportSE(
				webServerUrl);
		// 创建SoapObject对象
		SoapObject request = new SoapObject(nameSpace, methodName);
		request.addProperty("sSubject", sSubject);
		request.addProperty("nCreateID", nCreateID);
		request.addProperty("nOrgID", nOrgID);
		request.addProperty("sChairPasswd", sChairPasswd);
		request.addProperty("sParticipantPasswd", sParticipantPasswd);
		request.addProperty("nStartTime", nStartTime);
		request.addProperty("nEndTime", nEndTime);
		request.addProperty("nConfType", nConfType);
		request.addProperty("nMaxParticipant", nMaxParticipant);
		request.addProperty("nParticpantLimited", nParticpantLimited);

		// 实例化SoapSerializationEnvelope，传入WebService的SOAP协议的版本号
		final SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		// 设置是否调用的是.Net开发的WebService
		// soapEnvelope.setOutputSoapObject(request);
		soapEnvelope.dotNet = true;
		soapEnvelope.bodyOut = request;
		// httpTransportSE.debug = true;

		// 开启线程去访问WebService
		SoapObject resultSoapObject = null;
		String result = "";
		try {
			httpTransportSE.call(nameSpace + methodName, soapEnvelope);
			if (soapEnvelope.getResponse() != null) {
				// 获取服务器响应返回的SoapObject
				resultSoapObject = (SoapObject) soapEnvelope.bodyIn;
				result = resultSoapObject.getProperty(0).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 调用销毁会议WebService
	 * 
	 * @param webServerUrl
	 * @param nameSpace
	 * @param methodName
	 * @param confId
	 * @return
	 */
	private String callDestroyConfWebService(String webServerUrl,
			String nameSpace, String methodName, long confId) {
		HttpTransportSE se = new HttpTransportSE(webServerUrl);
		SoapObject request = new SoapObject(nameSpace, methodName);
		request.addProperty("nGroupID", confId);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapSerializationEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;
		SoapObject obj = null;
		String result = "";
		try {
			se.call(nameSpace + methodName, envelope);
			if (envelope.getResponse() != null) {
				obj = (SoapObject) envelope.bodyIn;
				result = obj.getProperty(0).toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 设置服务器地址和端口
	 * 
	 * @param ip
	 * @param port
	 */
	public void setServer(String ip, String port) {
		if (ip.isEmpty() || port.isEmpty()) {
			throw new IllegalArgumentException("ip or port is null.");
		}
		String WEB_SERVER_URL = "http://" + ip + ":" + port
				+ "/ConfWebServiceInterface.wsdl";
		String NAMESPACE = "urn:ConfWebServiceInterface";

		this.ip = ip;
		this.port = port;

		SPUtil.putConfigStrValue(mContext, "WEB_SERVER_URL", WEB_SERVER_URL);
		SPUtil.putConfigStrValue(mContext, "NAMESPACE", NAMESPACE);

		SPUtil.putConfigStrValue(mContext, "ip", ip);
		SPUtil.putConfigStrValue(mContext, "port", "18181");

		XviewLog.i(TAG," setServer:" + WEB_SERVER_URL);
	}

	private void login() {
		ip = SPUtil.getConfigStrValue(mContext, "ip");
		port = SPUtil.getConfigStrValue(mContext, "port");

		if ("".equals(ip) || "".equals(port)) {
			XviewLog.i(TAG, " ip or port is null");
			mIOnXViewCallback.onLoginResultListener(6);
			return;
		}

		if (!com.cinlan.xview.utils.Utils.checkNetworkState(mContext)) {
			XviewLog.i(TAG, " disnetwork");
			mIOnXViewCallback.onLoginResultListener(7);
			return;
		}
		if (isDNS(ip)) {
			task = new ParseDNSTask();
			task.execute(ip);
			return;
		} else {
			loginUP();
		}

	}

	private void loginUP() {
		// 真正的设置服务器入口
		mConfigRequest.setServerAddress(ip, Integer.parseInt(port));

		synchronized (isLogining) {
			if (isLogining) {
				XviewLog.i(TAG, " isLogining");
				return;
			}
			isLogining = true;
			XviewLog.i(TAG, " login...");
			// 真正的登录入口
			mLoginService.login(mUserData, "", weakRefHandler, 1, nickName);
		}
	}

	/**
	 * 解析dns线程
	 */
	private class ParseDNSTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String ipAddress = null;
			try {
				InetAddress inetAddress = InetAddress.getByName(params[0]);
				ipAddress = inetAddress.getHostAddress();
			} catch (Exception e) {
				ipAddress = null;
			}
			return ipAddress;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				// dns错误
				XviewLog.i(TAG, " DNS parse error");
				mIOnXViewCallback.onLoginResultListener(8);
				return;
			}
			ip = result;
			loginUP();
		}
	}

	/**
	 * 查询是否是dns域名
	 * 
	 * @param dns
	 * @return
	 */
	private boolean isDNS(String dns) {
		boolean isDns = false;
		for (int i = 0; i < dns.length(); i++) {
			char x = dns.charAt(i);
			if ((x > 'a' && x < 'z') || (x > 'A' && x < 'Z')) {
				isDns = true;
			}
		}

		if (isDns)
			XviewLog.i(TAG, " DNS = " + dns);

		return isDns;
	}

	/**
	 * 计算支持和选择合适的屏幕分辨率.
	 */
	private void calculateAppSupport() {
		allAppSupport.clear();
		if (deviceList == null || deviceList.size() == 0)
			return;
		VideoCaptureDevice video = deviceList.get(0);

		// 选取720以下所有分辨率
		for (CaptureCapability cap : video.capabilites) {
			String one_local = cap.width + "X" + cap.height;
			if (cap.width <= 1280 && cap.height <= 1280)
				allAppSupport.add(one_local);
		}

		if (allAppSupport.size() >= 4) {
			/**
			 * 基本这就是入口.<br>
			 * 将所选的所有分辨率分4个档次,每个级别都是对应档次的最高分辨率.
			 */
			int level = allAppSupport.size() / 4;
			getFourSupport(allAppSupport, level * 1, level * 2, level * 3,
					level * 4);
		} else if (allAppSupport.size() == 3) {
			getFourSupport(allAppSupport, 1, 2, 3, 3);
		} else if (allAppSupport.size() == 2) {
			getFourSupport(allAppSupport, 1, 1, 2, 2);
		} else if (allAppSupport.size() == 1) {
			getFourSupport(allAppSupport, 1, 1, 1, 1);
		} else {
			return;
		}

		// 排序-从低到高
		app_support = sortScreen(app_support);

		// 如果支持1280X720,而又没有选择到就直接加上
		if (allAppSupport.contains(PublicInfo.SUPER_HIGH_SIZE)) {
			if (!app_support.contains(PublicInfo.SUPER_HIGH_SIZE)) {
				app_support.remove(app_support.size() - 1);
				app_support.add(PublicInfo.SUPER_HIGH_SIZE);
			}
		}

		if (app_support.size() == 4) {
			String[] whs0 = app_support.get(0).split("X");
			String[] whs1 = app_support.get(1).split("X");
			int w0 = Integer.parseInt(whs0[0]);
			int w1 = Integer.parseInt(whs1[0]);
			// 将4个分辨率设为公共静态域
			if (w0 < w1) {
				PublicInfo.Support4Level = app_support.get(0);
				PublicInfo.Support3Level = app_support.get(1);
				PublicInfo.Support2Level = app_support.get(2);
				PublicInfo.Support1Level = app_support.get(3);
			} else {
				PublicInfo.Support1Level = app_support.get(0);
				PublicInfo.Support2Level = app_support.get(1);
				PublicInfo.Support3Level = app_support.get(2);
				PublicInfo.Support4Level = app_support.get(3);
			}
		}

	}

	/**
	 * 对集合中的分辨率进行从低到高的排序.
	 */
	private static List<String> sortScreen(List<String> datas) {

		int[] array = new int[datas.size()];
		String[] strings = new String[datas.size()];
		for (int i = 0; i < array.length; i++) {
			String w[] = datas.get(i).split("X");
			array[i] = Integer.parseInt(w[0]);
			strings[i] = datas.get(i);
		}

		int temp;
		String str = "";
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = array.length - 1; j > i; j--) {
				if (array[j - 1] > array[j]) {
					temp = array[j - 1];
					array[j - 1] = array[j];
					array[j] = temp;

					str = strings[j - 1];
					strings[j - 1] = strings[j];
					strings[j] = str;
				}
			}
		}
		for (int i = 0; i < strings.length; i++) {
			for (int j = i + 1; j < strings.length; j++) {
				String w[] = strings[i].split("X");
				int s1 = Integer.parseInt(w[0]);
				int s2 = Integer.parseInt(w[1]);

				String q[] = strings[j].split("X");
				int w1 = Integer.parseInt(q[0]);
				int w2 = Integer.parseInt(q[1]);

				if (s1 == w1) {
					if (s2 > w2) {
						str = strings[i];
						strings[i] = strings[i + 1];
						strings[i + 1] = str;
					}
				}
			}
		}
		datas.clear();
		for (int i = 0; i < strings.length; i++) {
			datas.add(strings[i]);
		}
		/*
		 * 172X1445 176X1440 320X240 176X144 352X288 640X320 1760X144 1760X1445
		 */

		/*
		 * 172X1445 176X1440 176X144 320X240 352X288 640X320 1760X144 1760X1445
		 */

		return datas;
	}

	private void initReceiver() {
		localBroadcastManager = LocalBroadcastManager.getInstance(GlobalHolder.GlobalContext);

		mConfListReceiver = new ConfListReceiver();
		IntentFilter filter = new IntentFilter(JNIService.GET_CONFLIST);
		filter.addCategory(JNIService.XVIEW_JNI_CATA);
		localBroadcastManager.registerReceiver(mConfListReceiver, filter);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.cinlan.xview.broadcast.FORCE_OFFLINE");
		receiver = new ForceOfflineReceiver();
		localBroadcastManager.registerReceiver(receiver, intentFilter);
	}

	/**
	 * 解除注册
	 */
	private void unRegister() {
		if (mConfListReceiver != null) {
			localBroadcastManager.unregisterReceiver(mConfListReceiver);
		}
		if (receiver != null) {
			localBroadcastManager.unregisterReceiver(receiver);
		}

		PublicInfo.columnLayoutHandler = null;
		PublicInfo.confActivityHandler = null;
		PublicInfo.confListRefreshHandler = null;
		PublicInfo.videoFragmentHandler = null;
		PublicInfo.loginActivityHandler = null;

	}

	private class ConfListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int msgtype = intent.getIntExtra("msgtype", -1);
			Object obj = intent.getSerializableExtra("msg");
			switch (msgtype) {
			case MsgType.ENTERCONF_RESULT:

				EnterConfType entertype = (EnterConfType) obj;
				int getnJoinResult = entertype.getnJoinResult();
				long confid = entertype.getnConfID();
				XviewLog.i(TAG, " enter conf result code = " + getnJoinResult);
				if (getnJoinResult == 0) {


					EnterConfType conftype = (EnterConfType) obj;
					String szConfData = conftype.getSzConfData();
					ByteArrayInputStream is = new ByteArrayInputStream(
							szConfData.getBytes());
					Conf parserOnEnterConf = XmlParserUtils
							.parserOnEnterConf(is);
					mIOnXViewCallback.onEnterConfListener(2);
					GlobalHolder.getInstance().addSelf();
					GlobalHolder.getInstance().setmCurrentConf(new Conf(confid));

					Intent confintent = new Intent(context, ConfActivity.class);
					confintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					confintent.putExtra("conf", parserOnEnterConf);
					mContext.startActivity(confintent);


				} else {


					mIOnXViewCallback.onEnterConfListener(3); // 206?
					XviewLog.i(TAG, " enter conf result code = " + getnJoinResult);
					if (PublicInfo.isAnonymousLogin) {
						PublicInfo.AnonymousLoginState = 1;
					}
					ImRequest.getInstance().logout();
				}
				break;
			}

		}
	}

	/**
	 * 选取4个最合适的分辨率
	 */
	private void getFourSupport(List<String> supports, int low, int mid,
			int high, int superHigh) {
		app_support.clear();
		app_support.add(supports.get(low - 1));
		app_support.add(supports.get(mid - 1));
		app_support.add(supports.get(high - 1));
		app_support.add(supports.get(superHigh - 1));
	}

	private void initRequest() {
		XviewLog.i(TAG, " initRequest");
		try {
			System.loadLibrary("audiocore");
			System.loadLibrary("VideoCore");
			System.loadLibrary("Client");
			XviewLog.i(TAG + " load library secuss");
		} catch (UnsatisfiedLinkError ule) {
			mIOnXViewCallback.onLoginResultListener(9);
			System.out.println("WARNING: Could not load library!");
			XviewLog.i(TAG, " load library fail");
		}

		NativeInitializer.getIntance(mContext).initialize(mContext);
		im = ImRequest.getInstance();
		chat = ChatRequest.getInstance();
		conf = ConfRequest.getInstance();
		video = VideoRequest.getInstance();
		wb = WBRequest.getInstance();
		audio = AudioRequest.getInstance();
		TestImRequest();
		TestChatRequest();
		TestConfRequest();
		TestVideoRequest();
		TestWBRequest();
		TestAudioRequest();
		XviewLog.i(TAG, " initRequest sucess");
	}

	private void TestChatRequest() {
		XviewLog.i(TAG, " TestChatRequest=====start");
		if (!chat.initialize(chat)) {
			XviewLog.i(TAG, " TestChatRequest=====exception");
			return;
		}
		XviewLog.i(TAG, " TestChatRequest=====end");
	}

	private void TestAudioRequest() {
		XviewLog.i(TAG, " TestAudioRequest=====start");
		if (!audio.initialize(audio)) {
			XviewLog.i(TAG, " TestAudioRequest=====exception");
			return;
		}
		XviewLog.i(TAG, " TestAudioRequest=====end");
	}

	private void TestVideoRequest() {
		XviewLog.i(TAG, " TestVideoRequest=====start");
		if (!video.initialize(video)) {
			XviewLog.i(TAG, " TestVideoRequest=====exception");
			return;
		}
		XviewLog.i(TAG, " TestVideoRequest=====end");
	}

	private void TestWBRequest() {
		XviewLog.i(TAG, " TestWBRequest=====start");
		if (!wb.initialize(wb)) {
			XviewLog.i(TAG, " TestWBRequest=====exception");
			return;
		}
		XviewLog.i(TAG, " TestWBRequest=====end");
	}

	private void TestImRequest() {
		XviewLog.i(TAG, " TestImRequest=====start");
		if (!im.initialize(im)) {
			XviewLog.i(TAG, " TestImRequest=====exception");
			return;
		}
		XviewLog.i(TAG, " TestImRequest=====end");
	}

	private void TestConfRequest() {
		XviewLog.i(TAG, " TestConfRequest=====start");
		if (!conf.initialize(conf)) {
			XviewLog.i(TAG, " TestConfRequest=====exception");
			return;
		}
		XviewLog.i(TAG, " TestConfRequest=====end");
	}

	private class ThreadConfList implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


		}
	}

}
