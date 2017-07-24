package com.cinlan.xview.agent;

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
import android.util.Log;

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
import com.cinlan.xview.inter.IXVCallback;
import com.cinlan.xview.msg.EnterConfType;
import com.cinlan.xview.msg.MsgType;
import com.cinlan.xview.receiver.ForceOfflineReceiver;
import com.cinlan.xview.service.AbstractHandler;
import com.cinlan.xview.service.JNIResponse;
import com.cinlan.xview.service.JNIService;
import com.cinlan.xview.service.LoginService;
import com.cinlan.xview.service.Registrant;
import com.cinlan.xview.service.RequestLogInResponse;
import com.cinlan.xview.ui.p2p.view.MultiActivity;
import com.cinlan.xview.ui.p2p.view.PToPActivity;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;
import com.cinlan.xview.utils.Utils;
import com.cinlan.xview.utils.XmlParserUtils;
import com.cinlan.xview.utils.XviewLog;

import static com.cinlan.xview.inter.IXVCallback.IXCALLBACK;

public class XViewAgent {
    public enum LoginEnterConfState
    {
        UNKNOWN_STATE,
        LOGINNING_STATE,
        LOGINED_STATE,
        LOGOUTING_STATE,
        LOGOUTED_STATE,
        ENTERING_CONF_STATE,
        ENTERED_CONF_STATE,
    };


    private static String TAG = "xview";

    private static final int LOG_IN_CALL_BACK = 10;

    /**
     * 会议类型,是多人还是点对点
     */
    private ConfType type;

    // 昵称
    private String nickName;

    // 会议id
    private long confId;

    // 会议密码
    private String confPwd;

    // 第三方传来的id
    private String mUserData = "";

    // 底层通讯请求
    private ImRequest imRequest;
    // 底层白板请求
    private WBRequest wbRequest;

    // 底层聊天请求
    private ChatRequest chatRequest;
    // 底层会议请求
    private ConfRequest confRequest;
    // 底层视频请求
    private VideoRequest videoRequest;
    // 底层音频请求
    private AudioRequest audioRequest;
    // 底层配置请求
    private ConfigRequest mConfigRequest = new ConfigRequest();
    // 登录请求接口回调
    private LoginService mLoginService = new LoginService();

    // 服务器地址
    private String ip = PublicInfo.XVIEW_SERVER;
    // 服务器端口
    private String port = PublicInfo.XVIEW_PORT;

    private ParseDNSTask task;

    private IXVCallback mIXViewCallback;


    private ThreadConfList thread;

    private ConfListReceiver mConfListReceiver;

    private LocalBroadcastManager localBroadcastManager;

    private ForceOfflineReceiver receiver;

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


    private static XViewAgent mEnterConf = null;
    public LoginEnterConfState mLoginState = LoginEnterConfState.UNKNOWN_STATE;


    private Handler confListHandler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOG_IN_CALL_BACK:
                    XviewLog.i(TAG, "" + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
                    JNIResponse rlr = (JNIResponse) msg.obj;
                    mLoginState = LoginEnterConfState.LOGOUTING_STATE;
                    /**
                     * 登录反馈
                     */
                    if (rlr.getResult() == JNIResponse.Result.TIME_OUT) {
                        mIXViewCallback.onLoginResultListener(1);
                        XviewLog.i(TAG, " login TIME_OUT");
                        ImRequest.getInstance().logout();
                    } else if (rlr.getResult() == JNIResponse.Result.ENTER_CONF_TIME_OUT) {
                        mIXViewCallback.onEnterConfListener(3);
                        XviewLog.i(TAG, " enter conf TIME_OUT");
                        ImRequest.getInstance().logout();
                    } else if (rlr.getResult() == JNIResponse.Result.FAILED) {
                        mIXViewCallback.onLoginResultListener(2);
                        XviewLog.i(TAG, " login FAILED");
                        ImRequest.getInstance().logout();
                    } else if (rlr.getResult() == JNIResponse.Result.CONNECT_ERROR) {
                        mIXViewCallback.onLoginResultListener(3);
                        XviewLog.i(TAG, " login CONNECT_ERROR");
                        ImRequest.getInstance().logout();
                    } else if (rlr.getResult() == JNIResponse.Result.SERVER_REJECT) {
                        mIXViewCallback.onLoginResultListener(4);
                        XviewLog.i(TAG, " login SERVER_REJECT");
                        ImRequest.getInstance().logout();
                    } else if (rlr.getResult() == JNIResponse.Result.INCORRECT_PAR) {
                        mIXViewCallback.onLoginResultListener(10);
                        XviewLog.i(TAG, " login INCORRECT_PAR");
                        ImRequest.getInstance().logout();
                    } else if (rlr.getResult() == JNIResponse.Result.UNKNOWN) {
                        mIXViewCallback.onLoginResultListener(11);
                        XviewLog.i(TAG, " login UNKNOWN");
                        ImRequest.getInstance().logout();
                    } else {
                        mLoginState = LoginEnterConfState.LOGINED_STATE;
                        mIXViewCallback.onLoginResultListener(5);
                        XviewLog.i(TAG, " login success");

                        User user = ((RequestLogInResponse) rlr).getUser();
                        user.setNickName(nickName);
                        GlobalHolder.getInstance().setLocalUser(user);

                        PublicInfo.isAnonymousLogin = true;
                        if (PublicInfo.isAnonymousLogin) {
                            PublicInfo.confListRefreshHandler = confListHandler;
                            initReceiver();
                            thread = new ThreadConfList();
                            new Thread(thread).start();
                        }
                    }
                    break;
                case PublicInfo.FLAG_ANONYMOUS_LOGIN:
                    mLoginState = LoginEnterConfState.ENTERING_CONF_STATE;
                    XviewLog.i(TAG, "start enter");
                    Log.e("sivin", "start enter");
                    mLoginService.initTimeoutMessage(AbstractHandler.JNI_REQUEST_ENTER_CONF, AbstractHandler.DEFAULT_TIME_OUT_SECS, caller);
                    ConfRequest.getInstance().enterConf(confId, confPwd);
                    break;
                case PublicInfo.UNREGIStER_RECEIVER:
                    unRegister();
                    break;
            }
        }
    };

    private Registrant caller = new Registrant(confListHandler, LOG_IN_CALL_BACK, null);

    private XViewAgent(Context context) {
        if (PublicInfo.isFirstRunning) {
            // 用于只初始化一次底层请求
            initRequest(context);

            PublicInfo.isFirstRunning = false;
        }
    }

    public static XViewAgent getInstance(Context context) {
        XviewLog.i(TAG, "sdk version is 3.2.8.5");

        if (context == null) {
            XviewLog.i(TAG, "context is null");
            throw new NullPointerException("context is null");
        }

        if (mEnterConf == null) {
            mEnterConf = new XViewAgent(context);
        }

        return mEnterConf;
    }


    public void registerXViewCallback(IXVCallback callback) {
        if (callback == null) {
            throw new NullPointerException("mIXViewCallback is null");
        }
        mIXViewCallback = callback;
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
    public void loginXView(Context context, long confNumber, String confPassword, String name, String userData, ConfType t) {

        type = t;

        if (confNumber == 0) {
            XviewLog.i(TAG, "confNumber == 0");
            throw new NullPointerException("confRequest id is null.");
        }
        if (userData == null) {
            XviewLog.i(TAG, "userData == null");
            throw new NullPointerException("userData id is null.");
        }
        this.confId = confNumber;
        this.confPwd = confPassword;
        this.nickName = name;
        this.mUserData = userData;

        XviewLog.i(TAG, "confId=" + confNumber + "confPwd=" + confPassword + "nickName=" + name + "userData=" + userData);


        devInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
        deviceList = devInfo.deviceList;
        if (deviceList.size() == 0) {
            SPUtil.putConfigIntValue(context, "local", 2);
            XviewLog.i(TAG, "No camera, in a state if 2.");
        }


        // 计算支持和选择合适的屏幕分辨率.
        calculateAppSupport();
        // 选择配置文件目录
        String appRootFile = Utils.getAppRootFile();
        mConfigRequest.setExtStoragePath(appRootFile + "");


        //启动服务器,这个服务器无法被杀死
        Intent intent = new Intent(context, JNIService.class);


        context.startService(intent);

        login(context);
    }

    /**
     * 注销接口
     */
    public void logout() {
        PublicInfo.logoutFlag = 1;
        ImRequest.getInstance().logout();
    }

    /**
     * 创建会议
     *
     * @param sSubject           会议主题
     * @param nOrgID             组织id
     * @param sChairPasswd       主席密码
     * @param sParticipantPasswd 普通密码
     * @param nStartTime         开始时间 单位(秒)
     * @param nEndTime           结束时间 单位(秒) 0表示永久会议
     * @param nMaxParticipant    会议容纳最大人数
     */
    public void createConf(Context context, String sSubject, long nOrgID, String sChairPasswd,
                           String sParticipantPasswd, long nStartTime, long nEndTime,
                           int nMaxParticipant) {

        if (sSubject.isEmpty())
            throw new IllegalArgumentException("confRequest subject is empty.");
        if (nOrgID == 0)
            throw new IllegalArgumentException("org id is null.");
        if (sChairPasswd.isEmpty())
            throw new IllegalArgumentException("confRequest chairpwd is empty.");
        if (nStartTime < 0)
            throw new IllegalArgumentException("starttime exception.");
        if (nMaxParticipant <= 0)
            throw new IllegalArgumentException("the nMaxParticipant is illegal.");

        String WEB_SERVER_URL = SPUtil.getConfigStrValue(context, "WEB_SERVER_URL");
        String NAMESPACE = SPUtil.getConfigStrValue(context, "NAMESPACE");

        long nCreateID = 100; // Admin的id
        int nConfType = 0; // 0是本地会议,1是级联会议
        int nParticpantLimited = 0; //0是公开会议 ,1是内部会议

        CreateConfTask task = new CreateConfTask(sSubject, nCreateID, nOrgID,
                sChairPasswd, sParticipantPasswd, nStartTime, nEndTime,
                nConfType, nMaxParticipant, nParticpantLimited);

        task.execute(WEB_SERVER_URL, NAMESPACE, "CreateConf");

    }

    /**
     * 销毁会议
     */
    public void destroyConf(Context context, long nConfId) {
        if (nConfId == 0) {
            throw new IllegalArgumentException("confRequest id null.");
        }
        String WEB_SERVER_URL = SPUtil.getConfigStrValue(context,
                "WEB_SERVER_URL");
        String NAMESPACE = SPUtil.getConfigStrValue(context, "NAMESPACE");
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
            mIXViewCallback.onCreateConfCallback(result);
        }
    }

    private class DestroyConfTask extends AsyncTask<String, Integer, String> {
        private long confId;

        DestroyConfTask(long confId) {
            this.confId = confId;
        }

        @Override
        protected String doInBackground(String... params) {
            return callDestroyConfWebService(params[0], params[1], params[2], this.confId);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mIXViewCallback.onDestroyConfCallback(Integer.parseInt(result));
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
        } catch (IOException | XmlPullParserException e) {
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
    public void setServer(Context context, String ip, String port) {
        if (ip.isEmpty() || port.isEmpty()) {
            throw new IllegalArgumentException("ip or port is null.");
        }
        String WEB_SERVER_URL = "http://" + ip + ":" + port
                + "/ConfWebServiceInterface.wsdl";
        String NAMESPACE = "urn:ConfWebServiceInterface";

        this.ip = ip;
        this.port = port;

        SPUtil.putConfigStrValue(context, "WEB_SERVER_URL", WEB_SERVER_URL);
        SPUtil.putConfigStrValue(context, "NAMESPACE", NAMESPACE);

        SPUtil.putConfigStrValue(context, "ip", ip);
        SPUtil.putConfigStrValue(context, "port", "18181");

        XviewLog.i(TAG, " setServer:" + WEB_SERVER_URL);
    }

    private void login(Context context) {

        ip = SPUtil.getConfigStrValue(context, "ip");
        port = SPUtil.getConfigStrValue(context, "port");

        if ("".equals(ip) || "".equals(port)) {
            XviewLog.i(TAG, " ip or port is null");
            mIXViewCallback.onLoginResultListener(6);
            return;
        }

        if (!com.cinlan.xview.utils.Utils.checkNetworkState(context)) {
            XviewLog.i(TAG, " disnetwork");
            mIXViewCallback.onLoginResultListener(7);
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
        XviewLog.i(TAG, "start login ...");
        // 真正的登录入口
        if (mLoginState != LoginEnterConfState.UNKNOWN_STATE &&
            mLoginState != LoginEnterConfState.LOGOUTED_STATE) {
            //Log.e("sivin", "cannot be login");
            return;
        }
        mLoginState = LoginEnterConfState.LOGINNING_STATE;
        mLoginService.login(mUserData, "", caller, 1, nickName);
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
                mIXViewCallback.onLoginResultListener(8);
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

                    if (getnJoinResult == 0) {
                        mLoginState = LoginEnterConfState.ENTERED_CONF_STATE;
                        mLoginService.removeTimeoutMessage(AbstractHandler.JNI_REQUEST_ENTER_CONF);
                        //TODO:进入会议回调,2代表的是什么?
                        mIXViewCallback.onEnterConfListener(2);
                        XviewLog.i(TAG, " enter confRequest result code = " + getnJoinResult);

                        EnterConfType conftype = (EnterConfType) obj;
                        String szConfData = conftype.getSzConfData();
                        ByteArrayInputStream is = new ByteArrayInputStream(
                                szConfData.getBytes());
                        Conf parserOnEnterConf = XmlParserUtils
                                .parserOnEnterConf(is);
                        //进入的会议的时候添加了自己
                       // GlobalHolder.getInstance().addSelf();
                        GlobalHolder.getInstance().initSelf();
                        GlobalHolder.getInstance().setmCurrentConf(new Conf(confid));

                        Intent confintent = null;
                        if (type == ConfType.MULTI) {
                            confintent = new Intent(context, MultiActivity.class);
                        } else if (type == ConfType.SINGLE) {
                            confintent = new Intent(context, PToPActivity.class);
                        }
                        confintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        confintent.putExtra("conf", parserOnEnterConf);
                        //启动activity
                        context.startActivity(confintent);

                    } else {
                        mIXViewCallback.onEnterConfListener(3);
                        Log.i(TAG, " enter confRequest result code = " + getnJoinResult);


                        if (PublicInfo.isAnonymousLogin) {
                            PublicInfo.AnonymousLoginState = 1;
                        }

                        mLoginState = LoginEnterConfState.LOGOUTING_STATE;
                        ImRequest.getInstance().logout();
                    }
                    break;

                case MsgType.MEMBER_ENTER:  //成员进入会议回调
                    mIXViewCallback.onMemberEnterListener(
                            GlobalHolder.EnterMemberUserId,
                            GlobalHolder.EnterMemberUserNickName,
                            GlobalHolder.EnterMemberUserData);
                    break;

                case MsgType.MEMBER_EXIT: //成员退出回调
                    long userid = intent.getLongExtra("userid", 0);
                    String name = intent.getStringExtra("name");
                    String email = intent.getStringExtra("email");
                    mIXViewCallback.onMemberExitListener(userid, (name.isEmpty() ? "" : name), email);
                    break;

                case MsgType.KICK_CONF:  //被踢出会议回调
                    mIXViewCallback.onConfMsgListener(1);
                    Log.i(TAG, "tick conf");
                    break;

                case MsgType.LOGOUT_SELF:
                    mLoginState = LoginEnterConfState.LOGOUTED_STATE;
                    mIXViewCallback.onLogoutResultListener(1);
                    Log.i(TAG, "self logout");
                    break;
                case MsgType.LOGOUT_OTHER:
                    mLoginState = LoginEnterConfState.LOGOUTED_STATE;
                    Log.i(TAG, "force logout ");
                    mIXViewCallback.onLogoutResultListener(3);
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

    private void initRequest(Context context) {
        XviewLog.i(TAG, " initRequest");
        try {
            System.loadLibrary("audiocore");
            System.loadLibrary("VideoCore");
            System.loadLibrary("Client");
            XviewLog.i(TAG + " load library secuss");
        } catch (UnsatisfiedLinkError ule) {
            mIXViewCallback.onLoginResultListener(9);
            System.out.println("WARNING: Could not load library!");
            XviewLog.i(TAG, " load library fail");
        }

        NativeInitializer.getIntance(context).initialize(context);
        imRequest = ImRequest.getInstance();
        chatRequest = ChatRequest.getInstance();
        confRequest = ConfRequest.getInstance();
        videoRequest = VideoRequest.getInstance();
        wbRequest = WBRequest.getInstance();
        audioRequest = AudioRequest.getInstance();
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
        if (!chatRequest.initialize(chatRequest)) {
            XviewLog.i(TAG, " TestChatRequest=====exception");
            return;
        }
        XviewLog.i(TAG, " TestChatRequest=====end");
    }

    private void TestAudioRequest() {
        XviewLog.i(TAG, " TestAudioRequest=====start");
        if (!audioRequest.initialize(audioRequest)) {
            XviewLog.i(TAG, " TestAudioRequest=====exception");
            return;
        }
        XviewLog.i(TAG, " TestAudioRequest=====end");
    }

    private void TestVideoRequest() {
        XviewLog.i(TAG, " TestVideoRequest=====start");
        if (!videoRequest.initialize(videoRequest)) {
            XviewLog.i(TAG, " TestVideoRequest=====exception");
            return;
        }
        XviewLog.i(TAG, " TestVideoRequest=====end");
    }

    private void TestWBRequest() {
        XviewLog.i(TAG, " TestWBRequest=====start");
        if (!wbRequest.initialize(wbRequest)) {
            XviewLog.i(TAG, " TestWBRequest=====exception");
            return;
        }
        XviewLog.i(TAG, " TestWBRequest=====end");
    }

    private void TestImRequest() {
        XviewLog.i(TAG, " TestImRequest=====start");
        if (!imRequest.initialize(imRequest)) {
            XviewLog.i(TAG, " TestImRequest=====exception");
            return;
        }
        XviewLog.i(TAG, " TestImRequest=====end");
    }

    private void TestConfRequest() {
        XviewLog.i(TAG, " TestConfRequest=====start");
        if (!confRequest.initialize(confRequest)) {
            XviewLog.i(TAG, " TestConfRequest=====exception");
            return;
        }
        XviewLog.i(TAG, " TestConfRequest=====end");
    }

    private class ThreadConfList implements Runnable {
        @Override
        public void run() {
            Conf conf = new Conf();
            conf.setId(confId);
            confListHandler.sendEmptyMessage(PublicInfo.FLAG_ANONYMOUS_LOGIN);
        }
    }
}
