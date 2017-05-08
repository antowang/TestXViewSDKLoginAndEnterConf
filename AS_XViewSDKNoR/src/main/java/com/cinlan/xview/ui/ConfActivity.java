package com.cinlan.xview.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cinlan.core.CaptureCapability;
import com.cinlan.core.VideoCaptureDevInfo;
import com.cinlan.core.VideoCaptureDevInfo.VideoCaptureDevice;
import com.cinlan.jni.ConfRequest;
import com.cinlan.jni.ImRequest;
import com.cinlan.jni.VideoRequest;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.XviewApplication.OnsetHomeListener;
import com.cinlan.xview.adapter.ConfDataAdapter;
import com.cinlan.xview.adapter.ConfDeviceAdapter;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.bean.data.DocShare;
import com.cinlan.xview.bean.data.Page;
import com.cinlan.xview.msg.MediaEntity;
import com.cinlan.xview.msg.MsgType;
import com.cinlan.xview.service.JNIService;
import com.cinlan.xview.ui.fragement.Fragment_Doc;
import com.cinlan.xview.ui.fragement.Fragment_wb;
import com.cinlan.xview.ui.fragement.Video_fragement;
import com.cinlan.xview.utils.ActivityHolder;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;
import com.cinlan.xview.utils.Utils;
import com.cinlan.xview.utils.XviewLog;
import com.cinlan.xview.widget.SlidingLayer;
import com.cinlankeji.khb.iphone.R;

/**
 * 会议界面
 * 
 * @author Chong
 */
public class ConfActivity extends FragmentActivity implements
		OnsetHomeListener, Fragment_Doc.OnClickBackListener,
		Fragment_wb.OnClickBackListener2 {
	private static String XTAG = ConfActivity.class.getSimpleName();
	public static ConfActivity mContext;

	private SlidingLayer right_sliding_layer;
	/**
	 * 布局加载器
	 */
	private LayoutInflater inflater;
	/**
	 * 参会者布局
	 */
	private RelativeLayout view_conf_userlist;
	/**
	 * 文件柜布局
	 */
	private RelativeLayout view_conf_sharedata;
	/**
	 * 会议列表底部布局
	 */
	private static RelativeLayout llBottomBar;
	/**
	 * 会议列表顶部布局
	 */
	private static RelativeLayout llTopBar;
	/**
	 * 文档详情布局
	 */
	private RelativeLayout view_conf_doc_detail;

	/**
	 * 文件柜返回图标
	 */
	private ImageView view_confdatalist_back;
	/**
	 * 文档缩略图列表(旧:不用)
	 */
	private ListView lv_conf_imagelist;
	/**
	 * 文件柜的列表
	 */
	private ListView lv_conf_datalist;
	/**
	 * 参会者的列表
	 */
	private ListView lv_conf_userlist;
	/**
	 * 参会者界面返回图标
	 */
	private ImageView ivBackUserList;
	/**
	 * 文档中左箭头,用于返回上一页
	 */
	private static ImageView doc_iv_left;
	/**
	 * 文档中右箭头,用于跳去下一页
	 */
	private static ImageView doc_iv_right;
	/**
	 * 锁定会议
	 */
	private TextView tvLockConfItem;
	/**
	 * 视图模式
	 */
	private TextView tvScreenMode;
	/**
	 * 更多设置界面视频编码字段
	 */
	private TextView tvPop2;
	/**
	 * 更多设置界面视频帧率字段
	 */
	private TextView tvPop3;
	/**
	 * 更多设置界面视频流量字段
	 */
	private TextView tvPop4;
	/**
	 * 参会者列表适配器
	 */
	private static ConfDeviceAdapter mConfUserListAdapter;
	/**
	 * 文档列表适配器
	 */
	private ConfDataAdapter mDocShareAdapter;
	/**
	 * 白板布局
	 */
	private Fragment_wb mFragment_wb;
	/**
	 * 用户设备集合
	 */
	private static List<UserDevice> userdevices;
	/**
	 * 视频流集合
	 */
	private static List<MediaEntity> medias;
	/**
	 * 共享文档实体类集合
	 */
	private List<DocShare> mDocShares;
	/**
	 * 最终选择的分辨率集合
	 */
	private List<String> finalSupportList = new ArrayList<String>();
	/**
	 * 本地支持的所有分辨率集合
	 */
	private List<String> local_support = new ArrayList<String>();
	/**
	 * 视频捕获设备类集合
	 */
	private List<VideoCaptureDevice> deviceList;
	/**
	 * 更多设置父布局弹窗
	 */
	private PopupWindow popuWindow;
	/**
	 * 视频编码弹窗
	 */
	private PopupWindow videoCodePopupWindow;
	/**
	 * 视频帧率弹窗
	 */
	private PopupWindow videoFrameRatePopupWindow;
	/**
	 * 视频流量弹窗
	 */
	private PopupWindow videoFlowPopupWindow;
	/**
	 * 更多设置父布局
	 */
	private View contentView;
	/**
	 * 视频编码布局
	 */
	private View videoCodeContentView;
	/**
	 * 视频帧率布局
	 */
	private View videoFrameRateContentView;
	/**
	 * 视频流量布局
	 */
	private View videoFlowContentView;
	/**
	 * 当前会议,从会议列表界面传来的
	 */
	private Conf mConf;
	/**
	 * 会议广播接收器
	 */
	private InConfReceiver mInConfReceiver;
	/**
	 * 耳机广播接收器
	 */
	private ErjiReceiver mErjiReceiver;
	/**
	 * 聊天图标
	 */
	private ImageView ivChat;
	/**
	 * 麦克风图标
	 */
	private ImageView iv_conf_micro;
	/**
	 * 设置图标
	 */
	private ImageView iv_conf_setting;
	/**
	 * 挂断图标
	 */
	private ImageView ivEndConf;
	/**
	 * 参会者图标
	 */
	private ImageView iv_conf_userlist;
	/**
	 * 转换前后置摄像头的图标
	 */
	private ImageView iv_conf_video;
	/**
	 * 文件柜图标
	 */
	private ImageView iv_conf_share;
	/**
	 * 打开关闭摄像头图标
	 */
	private ImageView iv_conf_closevideo;

	/**
	 * 音频模式
	 */
	private int lastmode;
	/**
	 * 偶数切换前置摄像头<br>
	 * 奇数切换后置摄像头.
	 */
	private int clickVideoCount = 0;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	/**
	 * 屏幕高度
	 */
	private int screenHeight;
	/**
	 * 视频设备监听器
	 */
	public VideoOpenListener mVideoOpenListener;
	/**
	 * 实际操控视频的碎片类
	 */
	private Video_fragement videoFragement;
	/**
	 * 视频捕获设备信息类
	 */
	private VideoCaptureDevInfo devInfo;
	/**
	 * 电源管理
	 */
	private PowerManager pManager;
	/**
	 * 用于唤醒屏幕
	 */
	private WakeLock mWakeLock;
	/**
	 * 用于AudioManager在onCreate和onDestroy时设置.<br>
	 * 暂不用管它.
	 */
	private boolean last_speakerphoneOn;
	/**
	 * true代表关摄像头<br>
	 * false代表开摄像头.
	 */
	private boolean s = true;
	/**
	 * 是否可以发言
	 */
	private boolean isSpeak = false;
	/**
	 * 是否可以开关摄像头
	 */
	private boolean isCanOpenOrCloseCamera = true;
	/**
	 * 是否可以切换摄像头
	 */
	private static boolean isCanReverseCamera = true;
	/**
	 * 更多设置中视频编码布局条目
	 */
	private RelativeLayout videoCode;
	/**
	 * 更多设置中视频帧率布局条目
	 */
	private RelativeLayout videoFrameRate;
	/**
	 * 更多设置中视频流量布局条目
	 */
	private RelativeLayout videoFlow;
	/**
	 * 更多设置中锁定会议布局条目
	 */
	private RelativeLayout rlLockConf;
	/**
	 * 临时存储视频编码
	 */
	private String tempVideoCode;
	/**
	 * 超清分辨率
	 */
	private CheckBox cbSuperHigh;
	/**
	 * 高清分辨率
	 */
	private CheckBox cbHigh;
	/**
	 * 中等分辨率
	 */
	private CheckBox cbMid;
	/**
	 * 低等分辨率
	 */
	private CheckBox cbLow;
	/**
	 * 临时存储视频帧率
	 */
	private int tempVideoFrameRate;
	/**
	 * 视频帧率第一档次
	 */
	private CheckBox cbFRA;
	/**
	 * 视频帧率第二档次
	 */
	private CheckBox cbFRB;
	/**
	 * 视频帧率第三档次
	 */
	private CheckBox cbFRC;
	/**
	 * 视频帧率第四档次
	 */
	private CheckBox cbFRD;
	/**
	 * 临时存储视频流量
	 */
	private int tempVideoFlow;
	/**
	 * 视频流量第一档次
	 */
	private CheckBox cbFLA;
	/**
	 * 视频流量第二档次
	 */
	private CheckBox cbFLB;
	/**
	 * 视频流量第三档次
	 */
	private CheckBox cbFLC;
	/**
	 * 视频流量第四档次
	 */
	private CheckBox cbFLD;
	/**
	 * 音频管理器
	 */
	private AudioManager mAudioManager;
	/**
	 * 断网对话框
	 */
	private Dialog dissconnected_dialog;

	private LocalBroadcastManager lbm;

	private static Handler confActivityHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			/**
			 * 显示或隐藏底边栏
			 */
			case PublicInfo.SHOW_OR_HIDE_BOTTOMBAR:
				showOrHidellBottomBar();
				break;

			/**
			 * 滑动文档时翻页
			 */
			case PublicInfo.SHAREDOC_FLING_SIGNAL:
				if (msg.arg1 == 2) {
					doc_iv_left.performClick();
				} else if (msg.arg1 == 1) {
					doc_iv_right.performClick();
				}
				break;
			default:
				break;
			}
		};
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		setContentView(R.layout.activity_in_conf_xviewsdk);
		ActivityHolder.getInstance().addActivity(this);
		XviewLog.i(XTAG, " onCreate init start==========");
		// 初始化布局加载器
		inflater = LayoutInflater.from(this);
		// 初始化音频管理器
		mAudioManager = (AudioManager) getSystemService(mContext.AUDIO_SERVICE);
		last_speakerphoneOn = mAudioManager.isSpeakerphoneOn();

		// 默认一个视频尺寸
		if (SPUtil.getConfigStrValue(mContext, "chicun").isEmpty()) {
			SPUtil.putConfigStrValue(mContext, "chicun",
					PublicInfo.Support2Level);
		}

		// 初始化屏幕宽高
		initSV();

		// 获取音频模式
		lastmode = mAudioManager.getMode();
		// 获取当前Conf
		mConf = (Conf) getIntent().getSerializableExtra("conf");
		GlobalHolder.CurrentConfId = mConf.getId();
		// 设置麦克风为开
		setSpeakLounder(true);
		// 获取视频捕获设备信息类
		devInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
		// 通过视频捕获设备信息类获取设备列表
		deviceList = devInfo.deviceList;
		if (deviceList.size() == 0) {
			SPUtil.putConfigIntValue(mContext, "local", 2/* 无摄像头 */);
		}
		// 初始化本地的设备的支持的分辨率
		initLocalSupport();
		// 获取共享文档集合
		mDocShares = GlobalHolder.getInstance().getmDocShares();
		// 获取用户设备集合
		userdevices = GlobalHolder.getInstance().getUserDevice();

		List<String> users = new ArrayList<String>();
		for (int i = 0; i < userdevices.size(); i++) {
			users.add(userdevices.get(i).getUser().getmUserId() + "");
		}
		XviewLog.i(XTAG, " onGetUserListListener = " + users.size());
		EnterConf.mIOnXViewCallback.onGetUserListListener(users);

		// 获取视频碎片实例
		videoFragement = Video_fragement.newInstance();
		// 获取视频流集合
		medias = GlobalHolder.getInstance().getMediaDevice();
		// 设置视频设备监听器的接收者
		setmVideoOpenListener(videoFragement);

		// 添加到当前Activity
		FragmentTransaction beginTransaction = getSupportFragmentManager()
				.beginTransaction();
		beginTransaction.replace(R.id.view_conf_videolist_xviewsdk,
				videoFragement);
		beginTransaction.commit();

		lbm = LocalBroadcastManager.getInstance(GlobalHolder.GlobalContext);
		// 初始化广播接收器
		initReceiver();
		// 初始化布局控件
		initView();
		// 初始化用户列表
		initUserListView();
		// 初始化共享数据
		initDataView();
		// 初始化文档详情布局
		initDocDetailView();
		// 初始化白板详情布局
		initWbDetailView();

		XviewLog.i(XTAG, " onCreate init end==========");
	}

	/**
	 * 初始化屏幕宽高
	 */
	private void initSV() {
		XviewLog.i(XTAG, " initSV");
		WindowManager wm = this.getWindowManager();
		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();
		PublicInfo.screenHeight = screenHeight;
		PublicInfo.screenWidth = screenWidth;
	}

	@Override
	protected void onResume() {
		XviewLog.i(XTAG, " onResume start ");
		/**
		 * 横屏就旋转方向
		 */
		int index = SPUtil.getConfigIntValue(mContext, "viewModePosition", 1);
		PublicInfo.DEVICE_ORIENTATION = index;
		XviewLog.i(XTAG, " onResume index = " + index);
		if (index == 1) {
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		} else {
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
		super.onResume();
		PublicInfo.currentActivity = "ConfActivity";

		if (PublicInfo.isAnonymousLogin) {
			// 匿名登录成功
			PublicInfo.AnonymousLoginState = 2;
		}

		PublicInfo.confActivityHandler = confActivityHandler;
		pManager = ((PowerManager) getSystemService(POWER_SERVICE));
		mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, "MeetingRoom");
		mWakeLock.acquire();

		XviewLog.i(XTAG, " onResume end ");
	}

	@Override
	protected void onPause() {
		super.onPause();
		clickVideoCount = 0;
		XviewLog.i(XTAG, " onPause ");
	}

	@Override
	protected void onStop() {
		super.onStop();
		XviewLog.i(XTAG, " onStop ");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		XviewLog.i(XTAG, " onDestroy ");

		mAudioManager.setSpeakerphoneOn(last_speakerphoneOn);
		mAudioManager.setMode(lastmode);
		if (mInConfReceiver != null) {
			lbm.unregisterReceiver(mInConfReceiver);
		}
		if (mErjiReceiver != null) {
			lbm.unregisterReceiver(mErjiReceiver);
		}

		if (mConf != null)
			ConfRequest.getInstance().exitConf(mConf.getId());

		PublicInfo.confListRefreshHandler
				.sendEmptyMessage(PublicInfo.UNREGIStER_RECEIVER);

		XviewLog.i(XTAG, " onDestroy unregisterReceiver ");

		if (docFragment != null) {
			docFragment.removeListener();
		}

		if (mFragment_wb != null) {
			mFragment_wb.removeListener();;
		}
		/**
		 * 如果销毁的时候不加这些,则再次进入会议会崩溃.
		 */
		GlobalHolder.getInstance().mOpenUers.clear();
		GlobalHolder.getInstance().mOpenMedia.clear();
		GlobalHolder.getInstance().list.clear();
		GlobalHolder.getInstance().mSpeakUers.clear();
		GlobalHolder.getInstance().mUers.clear();
		GlobalHolder.getInstance().pages.clear();
		GlobalHolder.getInstance().userdevices.clear();
		GlobalHolder.getInstance().videodevices.clear();
		GlobalHolder.getInstance().mDocShares.clear();
		if (mConfUserListAdapter != null) {
			Collections.sort(userdevices);
			userdevices = GlobalHolder.getInstance().getUserDevice();
			medias = GlobalHolder.getInstance().getMediaDevice();
			mConfUserListAdapter.update(userdevices, medias);
		}

		XviewLog.i(XTAG, " onDestroy clear ");

//		confActivityHandler = null;
//		PublicInfo.confActivityHandler = null;
//		devInfo = null;
//		deviceList = null;
//		doc_iv_left = null;
//		doc_iv_right = null;
//		finalSupportList = null;
//		inflater = null;
//		ivBackUserList = null;
//		ivChat = null;
//		ivEndConf = null;
//		iv_conf_closevideo = null;
//		iv_conf_micro = null;
//		iv_conf_setting = null;
//		iv_conf_share = null;
//		iv_conf_userlist = null;
//		iv_conf_video = null;
//		llBottomBar = null;
//		llTopBar = null;
//		local_support = null;
//		lv_conf_datalist = null;
//		lv_conf_imagelist = null;
//		lv_conf_userlist = null;
//		mAudioManager = null;
//		videoFragement = null;
//		mConf = null;
//		mConfUserListAdapter = null;
//		mDocShareAdapter = null;
//		mDocShares = null;
//		mErjiReceiver = null;
//		mInConfReceiver = null;
//		mVideoOpenListener = null;
//		mWakeLock = null;
//		medias = null;
//		pManager = null;
//		popuWindow = null;
//		right_sliding_layer = null;
//		rlLockConf = null;
//		userdevices = null;
//		view_conf_doc_detail = null;
//		view_conf_sharedata = null;
//		view_conf_userlist = null;
//		view_conf_wb_detail = null;
//		view_confdatalist_back = null;
//		videoFrameRatePopupWindow = null;
//		videoFrameRateContentView = null;
//		videoFrameRate = null;
//		videoFlowPopupWindow = null;
//		videoFlowContentView = null;
//		videoFlow = null;
//		videoCodePopupWindow = null;
//		videoCodeContentView = null;
//		videoCode = null;
//		tvScreenMode = null;
//		tvPop2 = null;
//		tvPop3 = null;
//		tvPop4 = null;
//		tvLockConfItem = null;
//		popuWindow = null;
//		mFragment_wb = null;
//		ptrl = null;
		mContext = null;
//		ActivityHolder.removeActivity(this);
		XviewLog.i(XTAG, " onDestroy null ");
	}

	/**
	 * 设置麦克风为开或关.
	 * 
	 * @param lounder
	 */
	private void setSpeakLounder(boolean lounder) {
		XviewLog.i(XTAG, " setSpeakLounder = " + lounder);
		mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
		mAudioManager.setSpeakerphoneOn(lounder);
	}

	/**
	 * 初始化布局控件
	 */
	private void initView() {
		XviewLog.i(XTAG, " initView");

		if (mConf != null)
			right_sliding_layer = (SlidingLayer) findViewById(R.id.right_sliding_layer_xviewsdk);
		// 设置禁止滑动
		right_sliding_layer.setSlidingEnabled(false);

		// 打开关闭摄像头图标
		iv_conf_closevideo = (ImageView) findViewById(R.id.iv_conf_closevideo_xviewsdk);
		iv_conf_closevideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 打开关闭摄像头
				 */
				if (!isCanOpenOrCloseCamera) {
					return;
				}
				int local = SPUtil.getConfigIntValue(mContext, "local", 1);
				if (local == 1 || local == 0) {
//				if (PublicInfo.isClosedLocalVideo) {
//					Toast.makeText(
//							this,
//							getResources().getString(
//									R.string.first_open_video_xviewsdk),
//							Toast.LENGTH_LONG).show();
//					return;
//				}
					// Close Camera.
					if (mVideoOpenListener != null) {
						PublicInfo.isClosedLocalVideo = true;
						mVideoOpenListener.changeCamera(3);
						UserDevice userdevice = userdevices.get(0);
						if (userdevice != null) {
							closeDevice(userdevice);
							// State:HaveCamera,disable.
							SPUtil.putConfigIntValue(mContext, "local", 4);
						}

						if (mConfUserListAdapter != null)
							mConfUserListAdapter.notifyDataSetChanged();

						clickVideoCount++;
					}
					s = false;
					isCanOpenOrCloseCamera = false;
					iv_conf_closevideo
							.setImageResource(R.drawable.conf_video_selector_xviewsdk);

				}  else if (local == 4) {
					// Open Camera.
//					if (!PublicInfo.isClosedLocalVideo) {
//						Toast.makeText(
//								mContext,
//								getResources().getString(
//										R.string.first_close_video_xviewsdk),
//								Toast.LENGTH_LONG).show();
//						return;
//					}
					if (mVideoOpenListener != null) {
						PublicInfo.isClosedLocalVideo = false;
						int configIntValue = SPUtil.getConfigIntValue(mContext,
								"local", 0);
						if (configIntValue == 4 || configIntValue == 0) {
							UserDevice userdevice = userdevices.get(0);
							if (userdevice != null) {
								User user = userdevice.getUser();
								openDevice(userdevice, user);
							}
							// State:HaveCamera,opened.
							SPUtil.putConfigIntValue(mContext, "local", 1);
							if (mConfUserListAdapter != null)
								mConfUserListAdapter.notifyDataSetChanged();
						}
						mVideoOpenListener.changeCamera(2);
					}
					s = true;
					isCanOpenOrCloseCamera = false;
					iv_conf_closevideo
							.setImageResource(R.drawable.open_camera_selector_xviewsdk);
				}
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						isCanOpenOrCloseCamera = true;
					}
				}, 500);
			}
		});
		// 麦克风图标
		iv_conf_micro = (ImageView) findViewById(R.id.iv_conf_micro_xviewsdk);
		iv_conf_micro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 麦克风
				 */
				if (isSpeak) {
					ConfRequest.getInstance().releaseControlPermission(3);
				} else {
					ConfRequest.getInstance().applyForControlPermission(3);
				}
			}
		});
		// 设置图标
		iv_conf_setting = (ImageView) findViewById(R.id.iv_conf_setting_xviewsdk);
		iv_conf_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 更多设置
				 */
				initPopuWindow(v);
			}
		});
		// 参会者图标
		iv_conf_userlist = (ImageView) findViewById(R.id.iv_conf_userlist_xviewsdk);
		iv_conf_userlist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 用户列表
				 */
				// OpenShareList
				right_sliding_layer.removeAllViews();
				right_sliding_layer.closeLayer(true);
				openUserlist();
				lv_conf_userlist.setSelection(0);
			}
		});
		// 文件柜图标
		iv_conf_share = (ImageView) findViewById(R.id.iv_conf_share_xviewsdk);
		iv_conf_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 共享文档
				 */
				right_sliding_layer.removeAllViews();
				right_sliding_layer.closeLayer(true);
				openShareList();
				lv_conf_datalist.setSelection(0);
			}
		});
		// 挂断图标
		ivEndConf = (ImageView) findViewById(R.id.ivEndConf_xviewsdk);
		ivEndConf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 退出会议
				 */
				final com.cinlan.xview.widget.AlertDialog d = new com.cinlan.xview.widget.AlertDialog(
						ConfActivity.this)
						.builder()
						.setTitle(
								getResources()
										.getString(R.string.hint_xviewsdk))
						.setMsg(getResources().getString(
								R.string.isExit_xviewsdk))
						.setPositiveButton(
								getResources()
										.getString(R.string.sure_xviewsdk),
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										PublicInfo.logout(mContext);
									}
								})
						.setNegativeButton(
								getResources().getString(
										R.string.cancel_xviewsdk),
								new OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								});
				d.show();
			}
		});
		// 转换前后置摄像头图标
		iv_conf_video = (ImageView) findViewById(R.id.iv_conf_video_xviewsdk);
		iv_conf_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 前置后置摄像头
				 */
				if (!isCanReverseCamera) {
//					Toast.makeText(
//							ConfActivity.this,
//							ConfActivity.this.getResources().getString(
//									R.string.change_camera_fast_xviewsdk),
//							Toast.LENGTH_SHORT).show();
					return;
				}
//				if (PublicInfo.isClosedLocalVideo) {
//					Toast.makeText(
//							ConfActivity.this,
//							ConfActivity.this.getResources().getString(
//									R.string.local_camera_closed_xviewsdk),
//							Toast.LENGTH_LONG).show();
//					return;
//				}

				if (SPUtil.getConfigIntValue(mContext, "camera", 1) == 1) {
					// Front camera
					if (mVideoOpenListener != null) {
						int configIntValue = SPUtil.getConfigIntValue(mContext,
								"local", 0);
						/**
						 * 如果摄像头没打开则打开摄像头
						 */
						if (configIntValue == 4 || configIntValue == 0) {
							UserDevice userdevice = userdevices.get(0);
							if (userdevice != null) {
								User user = userdevice.getUser();
								openDevice(userdevice, user);
							}
						}
						SPUtil.putConfigIntValue(mContext, "local", 1);
						SPUtil.putConfigIntValue(mContext, "camera", 0);
						if (mConfUserListAdapter != null)
							mConfUserListAdapter.notifyDataSetChanged();
						// Set to front.
						mVideoOpenListener.changeCamera(1);
					}
					isCanReverseCamera = false;
					clickVideoCount++;
				} else if (SPUtil.getConfigIntValue(mContext, "camera", 0) == 0) {
					// Rear camera
					if (mVideoOpenListener != null) {
						int configIntValue = SPUtil.getConfigIntValue(mContext,
								"local", 0);
						if (configIntValue == 4 || configIntValue == 0) {
							UserDevice userdevice = userdevices.get(0);
							if (userdevice != null) {
								User user = userdevice.getUser();
								openDevice(userdevice, user);
							}
							SPUtil.putConfigIntValue(mContext, "local", 1);
							SPUtil.putConfigIntValue(mContext, "camera", 1);
							if (mConfUserListAdapter != null)
								mConfUserListAdapter.notifyDataSetChanged();
						}
						mVideoOpenListener.changeCamera(2);
					}
					isCanReverseCamera = false;
					clickVideoCount++;
				}
//				new Handler().postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						isCanReverseCamera = true;
//					}
//				}, 500);
				MyHandler h = new MyHandler(mContext);
				h.postDelayed(null, 500);
			}
		});

		llBottomBar = (RelativeLayout) findViewById(R.id.llBottomBar_xviewsdk);
		llTopBar = (RelativeLayout) findViewById(R.id.rlTopBar_xviewsdk);
		PublicInfo.heightBottomBar = llBottomBar.getLayoutParams().height;
	}
	private static class MyHandler extends Handler {
		private final WeakReference<ConfActivity> mActivity;
		public MyHandler(ConfActivity activity) {
			mActivity = new WeakReference<ConfActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1011) {
				isCanReverseCamera = true;
			}
		}


	}
	/**
	 * 初始化用户列表
	 */
	private void initUserListView() {
		XviewLog.i(XTAG, " initUserListView");

		view_conf_userlist = (RelativeLayout) inflater.inflate(
				R.layout.view_conf_userlist_xviewsdk, null);

		// 返回图标
		ivBackUserList = (ImageView) view_conf_userlist
				.findViewById(R.id.ivBackUserList_xviewsdk);
		ivBackUserList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				right_sliding_layer.removeAllViews();
				right_sliding_layer.closeLayer(true);
			}
		});

		// 聊天图标
		ivChat = (ImageView) view_conf_userlist
				.findViewById(R.id.ivChat_xviewsdk);
		ivChat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(mContext, ChatActivity.class));
				right_sliding_layer.closeLayer(true);
				setRequestFoucsAble(true);
			}
		});

		// 参会者列表
		lv_conf_userlist = (ListView) view_conf_userlist
				.findViewById(R.id.lv_conf_userlist_xviewsdk);
		mConfUserListAdapter = new ConfDeviceAdapter(mContext, userdevices,
				medias);
		lv_conf_userlist.setAdapter(mConfUserListAdapter);
		lv_conf_userlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				userdevices = GlobalHolder.getInstance().getUserDevice();
				if (position <= userdevices.size() - 1) {
					UserDevice userDevice = userdevices.get(position);
					// 准备打开用户设备视频
					readyOpenDevice(userDevice);
				} else if (position > userdevices.size() - 1) {
					MediaEntity entity = medias.get(position
							- userdevices.size());
					// 准备打开视频流
					readyOpenMedia(entity);
				}
			}
		});
	}

	/**
	 * 初始化共享数据
	 */
	private void initDataView() {
		XviewLog.i(XTAG, " initDataView");

		// 文件柜布局
		view_conf_sharedata = (RelativeLayout) inflater.inflate(
				R.layout.view_conf_sharedata_xviewsdk, null);
		view_conf_sharedata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				closePlayer();
				openShareList();
			}
		});

		// 文件柜返回图标
		view_confdatalist_back = (ImageView) view_conf_sharedata
				.findViewById(R.id.view_confdatalist_back_xviewsdk);
		view_confdatalist_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 共享数据界面返回事件
				 */
				if (right_sliding_layer.getChildAt(0) == view_conf_sharedata) {
					PublicInfo.isOpenedShareList = false;
					if (PublicInfo.OPENED_VIDEO_COUNT == 2
							&& SPUtil.getConfigIntValue(mContext,
									"viewModePosition", 1) == 0) {
						PublicInfo.videoFragmentHandler
								.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_VIDEOFRAGMENT);
						PublicInfo.columnLayoutHandler
								.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_COLUMNLAYOUT);
					} else {
						if (PublicInfo.OPENED_VIDEO_COUNT != 4
								&& SPUtil.getConfigIntValue(mContext,
										"viewModePosition", 1) == 0)
							PublicInfo.columnLayoutHandler
									.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_COLUMNLAYOUT);
					}
				}
				closePlayer();
				if (right_sliding_layer.getChildAt(0) == view_conf_sharedata) {
					iv_conf_share.requestFocus();
				}
			}
		});

		// 文档列表
		lv_conf_datalist = (ListView) view_conf_sharedata
				.findViewById(R.id.lv_conf_datalist_xviewsdk);
		mDocShareAdapter = new ConfDataAdapter(mContext, mDocShares);
		lv_conf_datalist.setAdapter(mDocShareAdapter);
		lv_conf_datalist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				DocShare docShare = mDocShares.get(position);
				if (docShare.isWb()) {
					// 打开白板详情
					openWbDetail(docShare);
				} else {
					// 打开文档详情
					openDocDetail(docShare);
				}
			}
		});
	}

	/**
	 * 打开白板详情
	 * 
	 * @param docShare
	 */
	protected void openWbDetail(DocShare docShare) {
		XviewLog.i(XTAG, " openWbDetail");
		openWbDetailList();
		final List<Page> pages = docShare.getPages();
		if (pages != null && pages.size() > 0) {
			FragmentTransaction beginTransaction = getSupportFragmentManager()
					.beginTransaction();
			mFragment_wb = Fragment_wb.newInstance(pages.get(0),
					docShare.getFilename());
			beginTransaction.replace(R.id.fl_wb_content_xviewsdk, mFragment_wb);
			beginTransaction.commit();
		}
	}

	/**
	 * 打开白版详情列表
	 */
	private void openWbDetailList() {
		if (right_sliding_layer.isOpened()) {
			right_sliding_layer.removeAllViews();
			right_sliding_layer.addView(view_conf_wb_detail);
			right_sliding_layer.openLayer(false);
		}
	}

	private Fragment_Doc docFragment;
	private FrameLayout view_conf_wb_detail;
	private long lastImageTime;
	private boolean firstView = true;

	/**
	 * 打开文档详情
	 * 
	 * @param mDoc
	 */
	protected void openDocDetail(final DocShare mDoc) {
		XviewLog.i(XTAG, " openDocDetail");
		SharedPreferences p = getSharedPreferences("isFirstOpenDocDetail",
				Activity.MODE_PRIVATE);
		boolean isFirstOpenDocDetail = p.getBoolean("isFirstOpenDoc", false);
		if (!isFirstOpenDocDetail) {
			SharedPreferences.Editor editor = p.edit();
			editor.putBoolean("isFirstOpenDoc", true);
			editor.commit();

			PublicInfo.toast(mContext, R.string.sliding_up_and_down_xviewsdk);
		}

		final List<Page> pages = mDoc.getPages();
		if (pages.size() == 0)
			return;

		FragmentTransaction beginTransaction = getSupportFragmentManager()
				.beginTransaction();
		docFragment = Fragment_Doc
				.newInstance(pages.get(0), mDoc.getFilename());
		beginTransaction.replace(R.id.lin_doc_content_xviewsdk, docFragment);
		beginTransaction.commit();
		openDocDetailList();

		doc_iv_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long timeMillis = System.currentTimeMillis();
				if (!firstView && timeMillis - 5000 > lastImageTime) {
					lastImageTime = timeMillis;
					return;
				}
				firstView = false;
				int currentPage = docFragment.getCurrentPage();
				if (currentPage <= 1)
					return;
				int currenttemp = currentPage - 2;
				FragmentTransaction beginTransaction = getSupportFragmentManager()
						.beginTransaction();
				docFragment = Fragment_Doc.newInstance(pages.get(currenttemp),
						mDoc.getFilename());
				lv_conf_imagelist.setSelection(currentPage);
				beginTransaction.replace(R.id.lin_doc_content_xviewsdk,
						docFragment);
				beginTransaction.commit();
			}
		});

		doc_iv_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long timeMillis = System.currentTimeMillis();
				if (!firstView && timeMillis - 5000 > lastImageTime) {
					lastImageTime = timeMillis;
					return;
				}

				firstView = false;
				int currentPage = docFragment.getCurrentPage();
				if (currentPage >= pages.size())
					return;
				FragmentTransaction beginTransaction = getSupportFragmentManager()
						.beginTransaction();
				int cuu = currentPage++;
				docFragment = Fragment_Doc.newInstance(pages.get(cuu),
						mDoc.getFilename());
				beginTransaction.replace(R.id.lin_doc_content_xviewsdk,
						docFragment);
				beginTransaction.commit();
				lv_conf_imagelist.setSelection(currentPage);
			}
		});
	}

	/**
	 * 初始化文档详情布局
	 */
	private void initDocDetailView() {
		XviewLog.i(XTAG, " initDocDetailView");

		view_conf_doc_detail = (RelativeLayout) inflater.inflate(
				R.layout.view_conf_doc_detail_xviewsdk, null);
		lv_conf_imagelist = (ListView) view_conf_doc_detail
				.findViewById(R.id.lv_conf_imagelist_xviewsdk);
		lv_conf_imagelist.setVisibility(View.GONE);
		doc_iv_left = (ImageView) view_conf_doc_detail
				.findViewById(R.id.doc_iv_left_xviewsdk);
		doc_iv_right = (ImageView) view_conf_doc_detail
				.findViewById(R.id.doc_iv_right_xviewsdk);
	}

	/**
	 * 初始化白板详情布局
	 */
	private void initWbDetailView() {
		XviewLog.i(XTAG, " initWbDetailView");

		view_conf_wb_detail = (FrameLayout) inflater.inflate(
				R.layout.view_conf_wb_detail_xviewsdk, null);
	}

	/**
	 * 准备打开用户设备视频
	 * 
	 * @param userdevice
	 */
	protected void readyOpenDevice(final UserDevice userdevice) {
		final List<UserDevice> openUsers = GlobalHolder.getInstance().mOpenUers;
		if (userdevice == null)
			return;
		final User user = userdevice.getUser();
		final VideoDevice device = userdevice.getDevice();
		if (user == null)
			return;
		if (device == null) {
			// 对方没有摄像头设备
			PublicInfo.toast(mContext, R.string.novideodevice_xviewsdk);
			return;
		}
		if (device.getDisable() == 1) {
			// 对方摄像头设置为禁用
			PublicInfo.toast(mContext, R.string.cannotvideodevice_xviewsdk);
			return;
		}
		// 如果本设备还没有打开,就打开设备
		if (!openUsers.contains(userdevice)) {
			openDevice(userdevice, user);
			closePlayer();
		} else {
			// 如果本设备已经打开,就关闭设备
			closeDevice(userdevice);
			closePlayer();
		}
	}

	/**
	 * 准备打开视频流
	 * 
	 * @param m
	 */
	protected void readyOpenMedia(final MediaEntity m) {
		final List<MediaEntity> openMedias = GlobalHolder.getInstance().mOpenMedia;
		if (m == null)
			return;

		if (!openMedias.contains(m)) {
			// 打开视频流
			openMeida(m);
			closePlayer();
		} else {
			// 关闭视频流
			closeMedia(m);
			closePlayer();
		}
	}

	/**
	 * 因同步自动打开用户视频设备
	 * 
	 * @param userId
	 * @param DstDeviceID
	 */
	private void autoOpenVideo(long userId, String DstDeviceID) {
		for (int i = 0; i < userdevices.size(); i++) {
			UserDevice device = userdevices.get(i);
			User user = userdevices.get(i).getUser();

			if (user.getmUserId() == userId
					&& device.getDevice().getId().equals(DstDeviceID)) {
				if (GlobalHolder.getInstance().mOpenUers.size()
						+ GlobalHolder.getInstance().mOpenMedia.size() >= 4) {
					return;
				}
				openDevice(device, user);
			}
		}
	}

	/**
	 * 因同步自动打开视频流
	 * 
	 * @param mediaId
	 */
	private void autoOpenMedia(String mediaId) {
		final List<MediaEntity> openMedias = GlobalHolder.getInstance().mOpenMedia;
		for (int i = 0; i < medias.size(); i++) {
			MediaEntity m = medias.get(i);
			if (m == null)
				return;
			if (!openMedias.contains(m) && mediaId.equals(m.getMediaId())) {
				openMeida(m);
			}
		}

	}

	/**
	 * 因同步自动关闭用户视频设备
	 * 
	 * @param d
	 */
	private void autoCloseVideo(UserDevice d) {
		for (int i = 0; i < userdevices.size(); i++) {
			UserDevice device = userdevices.get(i);
			String id = userdevices.get(i).getDevice().getId();

			if (id.equals(d.getDevice().getId())) {
				closeDevice(device);
			}
		}
	}

	/**
	 * 因同步自动关闭视频流
	 * 
	 * @param mediaId
	 */
	private void autoCloseMedia(String mediaId) {
		final List<MediaEntity> openMedias = GlobalHolder.getInstance().mOpenMedia;
		for (int i = 0; i < medias.size(); i++) {
			MediaEntity m = medias.get(i);
			if (m == null)
				return;
			if (openMedias.contains(m) && mediaId.equals(m.getMediaId())) {
				closeMedia(m);
			}
		}
	}

	/**
	 * 关闭用户视频设备
	 * 
	 * @param userdevice
	 */
	public void closeDevice(final UserDevice userdevice) {
		XviewLog.i(XTAG, " closeDevice");
		User user = userdevice.getUser();
		if (user == null)
			return;

		if (GlobalHolder.getInstance().mOpenUers.contains(userdevice)) {
			GlobalHolder.getInstance().mOpenUers.remove(userdevice);
			if (mVideoOpenListener != null) {
				PublicInfo.isClosedLocalVideo = true;
				mVideoOpenListener.closeVideo(userdevice);
			}
		}
	}

	/**
	 * 关闭视频流
	 * 
	 * @param m
	 */
	private void closeMedia(MediaEntity m) {
		if (m == null)
			return;

		if (GlobalHolder.getInstance().mOpenMedia.contains(m)) {
			GlobalHolder.getInstance().mOpenMedia.remove(m);
		}

		if (mVideoOpenListener != null) {
			mVideoOpenListener.closeMedia(m);
		}

		medias = GlobalHolder.getInstance().getMediaDevice();

		if (mConfUserListAdapter != null) {
			mConfUserListAdapter.update(userdevices, medias);
		}
	}

	/**
	 * 打开用户视频设备
	 * 
	 * @param userdevice
	 * @param user
	 */
	private void openDevice(final UserDevice userdevice, final User user) {
		if (GlobalHolder.getInstance().mOpenUers.contains(userdevice)) {
			// 已经查看了该路视频
			PublicInfo.toast(mContext, R.string.hasopen_xviewsdk);
			return;
		}

		List<VideoDevice> devicelist = GlobalHolder.getInstance().videodevices
				.get(user.getmUserId());

		if (user.getmUserId() == GlobalHolder.getInstance().getCurrentUserId()
				|| (devicelist != null && devicelist.size() > 0)) {
			if (user.getmUserId() == GlobalHolder.getInstance()
					.getCurrentUserId()) {
				PublicInfo.isClosedLocalVideo = false;
				// 有摄像头,已被打开
				SPUtil.putConfigIntValue(mContext, "local", 1);
			}
			if (GlobalHolder.getInstance().mOpenUers.size()
					+ GlobalHolder.getInstance().mOpenMedia.size() >= 4) {
				// 最多打开4路视频,请先关闭其他参会者视频
				PublicInfo.toast(mContext, R.string.maxfour_xviewsdk);
				return;
			}
			GlobalHolder.getInstance().mOpenUers.add(userdevice);
			if (mVideoOpenListener != null) {
				try {
					mVideoOpenListener.openVideo(userdevice);
				} catch (Exception e) {
					PublicInfo.logout(mContext);
				}
			}
		} else {
			// 对方没有摄像头设备
			PublicInfo.toast(mContext, R.string.novideodevice_xviewsdk);
		}
	}

	/**
	 * 打开视频流
	 * 
	 * @param m
	 */
	public void openMeida(MediaEntity m) {
		if (GlobalHolder.getInstance().mOpenMedia.contains(m)) {
			// 已经查看了该路视频
			PublicInfo.toast(mContext, R.string.hasopen_xviewsdk);
			return;
		}

		if (GlobalHolder.getInstance().mOpenUers.size()
				+ GlobalHolder.getInstance().mOpenMedia.size() >= 4) {
			// 最多打开4路视频,请先关闭其他参会者视频
			PublicInfo.toast(mContext, R.string.maxfour_xviewsdk);
			return;
		}

		GlobalHolder.getInstance().mOpenMedia.add(m);

		if (mVideoOpenListener != null) {
			try {
				mVideoOpenListener.openMedia(m);
			} catch (Exception e) {
				// 注销账号
				PublicInfo.logout(mContext);
			}
		}

		if (mConfUserListAdapter != null) {
			mConfUserListAdapter.update(userdevices, medias);
		}

		closePlayer();
	}

	/**
	 * 初始化更多设置的弹窗
	 * 
	 * @param parent
	 */
	private void initPopuWindow(View parent) {
		if (popuWindow == null) {
			LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
			contentView = mLayoutInflater.inflate(
					R.layout.popupwindow1_xviewsdk, null);
			popuWindow = new PopupWindow(contentView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		// 初始化更多设置界面的字段
		initTextView();

		videoCode = (RelativeLayout) contentView
				.findViewById(R.id.videoCodeRelativeLayout_xviewsdk);
		videoFrameRate = (RelativeLayout) contentView
				.findViewById(R.id.videoFrameRateRelativeLayout_xviewsdk);
		videoFlow = (RelativeLayout) contentView
				.findViewById(R.id.videoFlowRelativeLayout_xviewsdk);
		rlLockConf = (RelativeLayout) contentView
				.findViewById(R.id.rlLockConf_xviewsdk);
		tvLockConfItem = (TextView) contentView
				.findViewById(R.id.tvLockConfItem_xviewsdk);
		tvScreenMode = (TextView) contentView
				.findViewById(R.id.tvScreenMode_xviewsdk);

		videoCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 视频编码
				 */
				popuWindow.dismiss();
				initVideoCodePopupWindow(v);
			}
		});
		videoFrameRate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 视频帧率
				 */
				popuWindow.dismiss();
				initVideoFrameRatePopupWindow(v);
			}
		});
		videoFlow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 视频流量
				 */
				popuWindow.dismiss();
				initVideoFlowPopupWindow(v);
			}
		});
		rlLockConf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 锁定会议
				 */
				// String str = tvLockConfItem.getText().toString().trim();
				// long curConfId = GlobalHolder.getInstance().getmCurrentConf()
				// .getId();
				// if (mContext.getString(
				// MResource.getIdByName(mContext, "string",
				// "unlock_xviewsdk")).equals(str)) {
				// tvLockConfItem.setText(mContext.getString(MResource
				// .getIdByName(mContext, "string", "locked_xviewsdk")));
				// SPUtil.putConfigBoolean(mContext, "islockconf", true);
				// SPUtil.putConfigLong(mContext, "lockconfid", curConfId);
				// SPUtil.putConfigStrValue(mContext, "lockconfpwd",
				// PublicInfo.tempConfPassword);
				// } else {
				// tvLockConfItem.setText(mContext.getString(MResource
				// .getIdByName(mContext, "string", "unlock_xviewsdk")));
				// SPUtil.putConfigBoolean(mContext, "islockconf", false);
				// }
			}
		});

		// 获取设备方向
		int positionViewMode = SPUtil.getConfigIntValue(mContext,
				"viewModePosition", 1);
		switch (positionViewMode) {
		case 1:
			tvScreenMode.setText(getResources().getString(
					R.string.vertical_screen_xviewsdk));
			break;
		case 0:
			tvScreenMode.setText(getResources().getString(
					R.string.horizontal_screen_xviewsdk));
		default:
			break;
		}

		// 获取是否锁定会议
		boolean isLockConf = SPUtil.getConfigBoolean(mContext, "islockconf",
				false);
		if (isLockConf) {
			tvLockConfItem
					.setText(mContext.getString(R.string.locked_xviewsdk));
		} else {
			tvLockConfItem
					.setText(mContext.getString(R.string.unlock_xviewsdk));
		}

		ColorDrawable cd = new ColorDrawable(0x000000);
		popuWindow.setBackgroundDrawable(cd);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.4f;
		getWindow().setAttributes(lp);

		popuWindow.setOutsideTouchable(true);
		popuWindow.setFocusable(true);
		popuWindow.showAtLocation((View) parent.getParent(), Gravity.CENTER
				| Gravity.CENTER_HORIZONTAL, 0, 0);

		popuWindow.update();
		popuWindow.setOnDismissListener(new OnDismissListener() {

			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	/**
	 * 初始化更多设置界面的字段
	 */
	private void initTextView() {
		tvPop2 = (TextView) contentView.findViewById(R.id.tvPop2_xviewsdk);
		tvPop3 = (TextView) contentView.findViewById(R.id.tvPop3_xviewsdk);
		tvPop4 = (TextView) contentView.findViewById(R.id.tvPop4_xviewsdk);

		/**
		 * 初始化视频编码设置字段
		 */
		String tmpVideoCode = SPUtil.getConfigStrValue(mContext, "chicun");
		if (tmpVideoCode.equals(PublicInfo.Support1Level)) {
			tvPop2.setText(getResources().getString(
					R.string.superhigh_setting_xviewsdk));
		} else if (tmpVideoCode.equals(PublicInfo.Support2Level)) {
			tvPop2.setText(getResources().getString(
					R.string.allhigh_setting_xviewsdk));
		} else if (tmpVideoCode.equals(PublicInfo.Support3Level)) {
			tvPop2.setText(getResources().getString(
					R.string.high_setting_xviewsdk));
		} else if (tmpVideoCode.equals(PublicInfo.Support4Level)) {
			tvPop2.setText(getResources().getString(
					R.string.standard_setting_xviewsdk));
		}

		/**
		 * 初始化视频帧率设置字段
		 */
		int positionVideoFrameRate = SPUtil.getConfigIntValue(mContext, "zl",
				20);
		switch (positionVideoFrameRate) {
		case 10:
			tvPop3.setText(getResources().getString(
					R.string.videframe_a_xviewsdk));
			break;
		case 20:
			tvPop3.setText(getResources().getString(
					R.string.videframe_b_xviewsdk));
			break;
		case 30:
			tvPop3.setText(getResources().getString(
					R.string.videframe_c_xviewsdk));
			break;
		case 40:
			tvPop3.setText(getResources().getString(
					R.string.videframe_d_xviewsdk));
			break;
		default:
			break;
		}

		/**
		 * 初始化视频流量设置字段
		 */
		int positionVideoFlow = SPUtil.getConfigIntValue(mContext, "videoFlow",
				256);
		switch (positionVideoFlow) {
		case 128:
			tvPop4.setText(getResources().getString(
					R.string.videflow_a_xviewsdk));
			break;
		case 256:
			tvPop4.setText(getResources().getString(
					R.string.videflow_b_xviewsdk));
			break;
		case 512:
			tvPop4.setText(getResources().getString(
					R.string.videflow_c_xviewsdk));
			break;
		case 1024:
			tvPop4.setText(getResources().getString(
					R.string.videflow_d_xviewsdk));
			break;
		default:
			break;
		}

	}

	/**
	 * 初始化视频编码弹窗-分辨率
	 * 
	 * @param parent
	 */
	private void initVideoCodePopupWindow(View parent) {
		if (videoCodePopupWindow == null) {
			LayoutInflater mLayoutInflater = LayoutInflater.from(this);
			videoCodeContentView = mLayoutInflater.inflate(
					R.layout.popupwindow_videocode_xviewsdk, null);
			videoCodePopupWindow = new PopupWindow(videoCodeContentView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		cbSuperHigh = (CheckBox) videoCodeContentView
				.findViewById(R.id.cbSuperHigh_xviewsdk);
		cbHigh = (CheckBox) videoCodeContentView
				.findViewById(R.id.cbHigh_xviewsdk);
		cbMid = (CheckBox) videoCodeContentView
				.findViewById(R.id.cbMid_xviewsdk);
		cbLow = (CheckBox) videoCodeContentView
				.findViewById(R.id.cbLow_xviewsdk);

		cbSuperHigh.setChecked(false);
		cbHigh.setChecked(false);
		cbMid.setChecked(false);
		cbLow.setChecked(false);

		// 获取保存的分辨率
		tempVideoCode = SPUtil.getConfigStrValue(ConfActivity.this, "chicun");

		if (tempVideoCode.isEmpty())
			tempVideoCode = PublicInfo.Support3Level;

		if (tempVideoCode.equals(PublicInfo.Support1Level)) {
			tvPop2.setText(getResources().getString(
					R.string.superhigh_setting_xviewsdk));
			cbSuperHigh.setChecked(true);
		} else if (tempVideoCode.equals(PublicInfo.Support2Level)) {
			tvPop2.setText(getResources().getString(
					R.string.allhigh_setting_xviewsdk));
			cbHigh.setChecked(true);
		} else if (tempVideoCode.equals(PublicInfo.Support3Level)) {
			tvPop2.setText(getResources().getString(
					R.string.high_setting_xviewsdk));
			cbMid.setChecked(true);
		} else if (tempVideoCode.equals(PublicInfo.Support4Level)) {
			tvPop2.setText(getResources().getString(
					R.string.standard_setting_xviewsdk));
			cbLow.setChecked(true);
		}

		// 超清
		cbSuperHigh.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbSuperHigh.isEnabled()) {
					return;
				}
				if (cbSuperHigh.isChecked()) {
					tvPop2.setText(getResources().getString(
							R.string.superhigh_setting_xviewsdk));
					cbSuperHigh.setChecked(true);
					cbHigh.setChecked(false);
					cbMid.setChecked(false);
					cbLow.setChecked(false);
					tempVideoCode = PublicInfo.Support1Level;
				}
				defaultVideoCodeRate(0);
			}
		});

		// 高清
		cbHigh.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbHigh.isEnabled()) {
					return;
				}
				if (cbHigh.isChecked()) {
					tvPop2.setText(getResources().getString(
							R.string.allhigh_setting_xviewsdk));
					cbSuperHigh.setChecked(false);
					cbMid.setChecked(false);
					cbLow.setChecked(false);
					cbHigh.setChecked(true);
					tempVideoCode = PublicInfo.Support2Level;
				}
				defaultVideoCodeRate(1);
			}
		});

		// 中
		cbMid.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbMid.isEnabled()) {
					return;
				}
				if (cbMid.isChecked()) {
					tvPop2.setText(getResources().getString(
							R.string.high_setting_xviewsdk));
					cbSuperHigh.setChecked(false);
					cbHigh.setChecked(false);
					cbLow.setChecked(false);
					cbMid.setChecked(true);
					tempVideoCode = PublicInfo.Support3Level;
				}
				defaultVideoCodeRate(2);
			}
		});

		// 低
		cbLow.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbLow.isEnabled()) {
					return;
				}
				if (cbLow.isChecked()) {
					tvPop2.setText(getResources().getString(
							R.string.standard_setting_xviewsdk));
					cbSuperHigh.setChecked(false);
					cbHigh.setChecked(false);
					cbMid.setChecked(false);
					cbLow.setChecked(true);
					tempVideoCode = PublicInfo.Support4Level;
				}
				defaultVideoCodeRate(3);
			}
		});

		// 应用Pop的背景设置
		applyPopSetting(parent, videoCodePopupWindow);
		videoCodePopupWindow.setOnDismissListener(new OnDismissListener() {

			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);

				SPUtil.putConfigStrValue(ConfActivity.this, "chicun",
						tempVideoCode);
				if (!tempVideoCode.isEmpty())
					Toast.makeText(
							mContext,
							getResources().getString(
									R.string.apply_apply_xviewsdk)
									+ tempVideoCode, Toast.LENGTH_SHORT).show();

				// 保存并应用设置参数
				saveAndApplySetting();
			}
		});
	}

	private void defaultVideoCodeRate(int position) {
		if (!cbSuperHigh.isChecked() & !cbHigh.isChecked()
				&& !cbMid.isChecked() && !cbLow.isChecked()) {
			switch (position) {
			case 0:
				cbSuperHigh.setChecked(true);
				break;
			case 1:
				cbHigh.setChecked(true);
				break;
			case 2:
				cbMid.setChecked(true);
				break;
			case 3:
				cbLow.setChecked(true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 应用Pop的背景设置
	 * 
	 * @param parent
	 */
	private void applyPopSetting(View parent, PopupWindow pop) {
		ColorDrawable cd = new ColorDrawable(0x000000);
		pop.setBackgroundDrawable(cd);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.4f;
		getWindow().setAttributes(lp);

		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAtLocation((View) parent.getParent(), Gravity.CENTER
				| Gravity.CENTER_HORIZONTAL, 0, 0);

		pop.update();
	}

	/**
	 * 初始化视频帧率弹窗
	 * 
	 * @param parent
	 */
	private void initVideoFrameRatePopupWindow(View parent) {
		if (videoFrameRatePopupWindow == null) {
			LayoutInflater mLayoutInflater = LayoutInflater.from(this);
			videoFrameRateContentView = mLayoutInflater.inflate(
					R.layout.popupwindow_videoframerate_xviewsdk, null);
			videoFrameRatePopupWindow = new PopupWindow(
					videoFrameRateContentView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		cbFRA = (CheckBox) videoFrameRateContentView
				.findViewById(R.id.cbFive_xviewsdk);
		cbFRB = (CheckBox) videoFrameRateContentView
				.findViewById(R.id.cbTen_xviewsdk);
		cbFRC = (CheckBox) videoFrameRateContentView
				.findViewById(R.id.cbFifteen_xviewsdk);
		cbFRD = (CheckBox) videoFrameRateContentView
				.findViewById(R.id.cbTwenty_xviewsdk);

		cbFRA.setChecked(false);
		cbFRB.setChecked(false);
		cbFRC.setChecked(false);
		cbFRD.setChecked(false);

		int vfr = SPUtil.getConfigIntValue(mContext, "zl", 20);

		if (vfr == 10) {
			tvPop3.setText(getResources().getString(
					R.string.videframe_a_xviewsdk));
			cbFRA.setChecked(true);
		} else if (vfr == 20) {
			tvPop3.setText(getResources().getString(
					R.string.videframe_b_xviewsdk));
			cbFRB.setChecked(true);
		} else if (vfr == 30) {
			tvPop3.setText(getResources().getString(
					R.string.videframe_c_xviewsdk));
			cbFRC.setChecked(true);
		} else if (vfr == 40) {
			tvPop3.setText(getResources().getString(
					R.string.videframe_d_xviewsdk));
			cbFRD.setChecked(true);
		}
		cbFRA.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbFRA.isEnabled())
					return;

				if (cbFRA.isChecked()) {
					tvPop3.setText(getResources().getString(
							R.string.videframe_a_xviewsdk));
					cbFRA.setChecked(true);
					cbFRB.setChecked(false);
					cbFRC.setChecked(false);
					cbFRD.setChecked(false);
					tempVideoFrameRate = 10;
				}
				defaultCheckVideoFrameRate(0);
			}
		});
		cbFRB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbFRB.isEnabled())
					return;

				if (cbFRB.isChecked()) {
					tvPop3.setText(getResources().getString(
							R.string.videframe_b_xviewsdk));
					cbFRA.setChecked(false);
					cbFRB.setChecked(true);
					cbFRC.setChecked(false);
					cbFRD.setChecked(false);
					tempVideoFrameRate = 20;
				}
				defaultCheckVideoFrameRate(1);
			}
		});
		cbFRC.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbFRC.isEnabled())
					return;

				if (cbFRC.isChecked()) {
					tvPop3.setText(getResources().getString(
							R.string.videframe_c_xviewsdk));
					cbFRA.setChecked(false);
					cbFRB.setChecked(false);
					cbFRC.setChecked(true);
					cbFRD.setChecked(false);
					tempVideoFrameRate = 30;
				}
				defaultCheckVideoFrameRate(2);
			}
		});
		cbFRD.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (!cbFRD.isEnabled())
					return;

				if (cbFRD.isChecked()) {
					tvPop3.setText(getResources().getString(
							R.string.videframe_d_xviewsdk));
					cbFRA.setChecked(false);
					cbFRB.setChecked(false);
					cbFRC.setChecked(false);
					cbFRD.setChecked(true);
					tempVideoFrameRate = 40;
				}
				defaultCheckVideoFrameRate(3);
			}
		});

		applyPopSetting(parent, videoFrameRatePopupWindow);
		videoFrameRatePopupWindow.setOnDismissListener(new OnDismissListener() {

			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
				SPUtil.putConfigIntValue(mContext, "zl", tempVideoFrameRate);
				if (tempVideoFrameRate != 0)
					Toast.makeText(
							mContext,
							getResources().getString(
									R.string.apply_apply_xviewsdk)
									+ tempVideoFrameRate + "fps",
							Toast.LENGTH_SHORT).show();
				saveAndApplySetting();
			}
		});
	}

	private void defaultCheckVideoFrameRate(int position) {
		if (!cbFRA.isChecked() && !cbFRB.isChecked() && !cbFRC.isChecked()
				&& !cbFRD.isChecked()) {
			switch (position) {
			case 0:
				cbFRA.setChecked(true);
				break;
			case 1:
				cbFRB.setChecked(true);
				break;
			case 2:
				cbFRC.setChecked(true);
				break;
			case 3:
				cbFRD.setChecked(true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 初始化视频流量弹窗
	 * 
	 * @param parent
	 */
	private void initVideoFlowPopupWindow(View parent) {
		if (videoFlowPopupWindow == null) {
			LayoutInflater mLayoutInflater = LayoutInflater.from(this);
			videoFlowContentView = mLayoutInflater.inflate(
					R.layout.popupwindow_videoflow_xviewsdk, null);
			videoFlowPopupWindow = new PopupWindow(videoFlowContentView,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		cbFLA = (CheckBox) videoFlowContentView
				.findViewById(R.id.cbOneNineTwo_xviewsdk);
		cbFLB = (CheckBox) videoFlowContentView
				.findViewById(R.id.cbTwoFiveSix_xviewsdk);
		cbFLC = (CheckBox) videoFlowContentView
				.findViewById(R.id.cbThreeEightFour_xviewsdk);
		cbFLD = (CheckBox) videoFlowContentView
				.findViewById(R.id.cbFourLevelVideoFlow_xviewsdk);

		cbFLA.setChecked(false);
		cbFLB.setChecked(false);
		cbFLC.setChecked(false);
		cbFLD.setChecked(false);

		int position = SPUtil.getConfigIntValue(mContext, "videoFlow", 256);

		if (position == 128) {
			tvPop4.setText(getResources().getString(
					R.string.videflow_a_xviewsdk));
			cbFLA.setChecked(true);
		} else if (position == 256) {
			tvPop4.setText(getResources().getString(
					R.string.videflow_b_xviewsdk));
			cbFLB.setChecked(true);
		} else if (position == 512) {
			tvPop4.setText(getResources().getString(
					R.string.videflow_c_xviewsdk));
			cbFLC.setChecked(true);
		} else if (position == 1024) {
			tvPop4.setText(getResources().getString(
					R.string.videflow_d_xviewsdk));
			cbFLD.setChecked(true);
		}

		cbFLA.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					tvPop4.setText(getResources().getString(
							R.string.videflow_a_xviewsdk));
					cbFLA.setChecked(true);
					cbFLB.setChecked(false);
					cbFLC.setChecked(false);
					cbFLD.setChecked(false);
					tempVideoFlow = 128;
				}
				defaultVideoFlow(0);
			}
		});
		cbFLB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					tvPop4.setText(getResources().getString(
							R.string.videflow_b_xviewsdk));
					cbFLA.setChecked(false);
					cbFLB.setChecked(true);
					cbFLC.setChecked(false);
					cbFLD.setChecked(false);
					tempVideoFlow = 256;
				}
				defaultVideoFlow(1);
			}
		});
		cbFLC.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					tvPop4.setText(getResources().getString(
							R.string.videflow_c_xviewsdk));
					cbFLA.setChecked(false);
					cbFLB.setChecked(false);
					cbFLC.setChecked(true);
					cbFLD.setChecked(false);
					tempVideoFlow = 512;
				}
				defaultVideoFlow(2);
			}
		});
		cbFLD.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					tvPop4.setText(getResources().getString(
							R.string.videflow_d_xviewsdk));
					cbFLA.setChecked(false);
					cbFLB.setChecked(false);
					cbFLC.setChecked(false);
					cbFLD.setChecked(true);
					tempVideoFlow = 1024;
				}
				defaultVideoFlow(3);
			}
		});

		applyPopSetting(parent, videoFlowPopupWindow);
		videoFlowPopupWindow.setOnDismissListener(new OnDismissListener() {

			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
				SPUtil.putConfigIntValue(mContext, "videoFlow", tempVideoFlow);
				if (tempVideoFlow != 0) {
					Toast.makeText(
							mContext,
							getResources().getString(
									R.string.apply_apply_xviewsdk)
									+ tempVideoFlow + "Kbps",
							Toast.LENGTH_SHORT).show();
				}
				saveAndApplySetting();
			}
		});
	}

	private void defaultVideoFlow(int position) {
		if (!cbFLA.isChecked() && !cbFLB.isChecked() && !cbFLC.isChecked()
				&& !cbFLD.isChecked()) {
			switch (position) {
			case 0:
				cbFLA.setChecked(true);
				break;
			case 1:
				cbFLB.setChecked(true);
				break;
			case 2:
				cbFLC.setChecked(true);
				break;
			case 3:
				cbFLD.setChecked(true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 初始化广播接收器
	 */
	private void initReceiver() {
		XviewLog.i(XTAG, " initReceiver");

		mInConfReceiver = new InConfReceiver();
		IntentFilter filter = new IntentFilter(JNIService.GET_CONFLIST);
		filter.addCategory(JNIService.XVIEW_JNI_CATA);
		lbm.registerReceiver(mInConfReceiver, filter);
		XviewLog.i(XTAG, " initReceiver InConfReceiver");
		mErjiReceiver = new ErjiReceiver();
		IntentFilter erjiFilter = new IntentFilter(
				"android.intent.action.HEADSET_PLUG");
		lbm.registerReceiver(mErjiReceiver, erjiFilter);
		XviewLog.i(XTAG, " initReceiver ErjiReceiver");
	}

	@Override
	public void onBackPressed() {
		if (right_sliding_layer != null && right_sliding_layer.isOpened()) {
			PublicInfo.isOpenedShareList = false;
			// 如果是横屏模式画中画视频时关闭文档
			if (PublicInfo.OPENED_VIDEO_COUNT == 2
					&& SPUtil
							.getConfigIntValue(mContext, "viewModePosition", 1) == 0) {
				PublicInfo.videoFragmentHandler
						.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_VIDEOFRAGMENT);
				PublicInfo.columnLayoutHandler
						.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_COLUMNLAYOUT);
			} else {
				// 如果是横屏模式且不是4路视频全都打开的情况下
				if (PublicInfo.OPENED_VIDEO_COUNT != 4
						&& SPUtil.getConfigIntValue(mContext,
								"viewModePosition", 1) == 0)
					PublicInfo.columnLayoutHandler
							.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_COLUMNLAYOUT);
			}

			if (right_sliding_layer.getChildAt(0) == view_conf_userlist) {
				PublicInfo.isOpenedUserList = false;
			}

			right_sliding_layer.closeLayer(true);
			setRequestFoucsAble(true);
			return;
		}

		final com.cinlan.xview.widget.AlertDialog d = new com.cinlan.xview.widget.AlertDialog(
				ConfActivity.this)
				.builder()
				.setTitle(getResources().getString(R.string.hint_xviewsdk))
				.setMsg(getResources().getString(R.string.isExit_xviewsdk))
				.setPositiveButton(
						getResources().getString(R.string.sure_xviewsdk),
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								boolean isEnterConf = SPUtil.getConfigBoolean(
										mContext, "islockconf", false);
//								if (PublicInfo.isAnonymousLogin || isEnterConf) {
									PublicInfo.logout(mContext);
//									ConfActivity.this.finish();
//								} else {
//									ConfActivity.this.finish();
//								}
							}
						})
				.setNegativeButton(
						getResources().getString(R.string.cancel_xviewsdk),
						new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						});
		d.show();
	}



	/**
	 * 会议广播接收器
	 * 
	 * @author Hello
	 * 
	 */
	class InConfReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			int msgtype = intent.getIntExtra("msgtype", -1);
			XviewLog.i(XTAG, "msgtype=" + msgtype);
			userdevices = GlobalHolder.getInstance().getUserDevice();
			medias = GlobalHolder.getInstance().getMediaDevice();
			switch (msgtype) {
			// 当断网时候
			case MsgType.DISCONNECTED:
				XviewLog.i(XTAG, " MsgType.DISCONNECTED  receiveBroadcast success");
				// showDisconneced();
				break;
			// 被踢出会议
			case MsgType.KICK_CONF:
				XviewLog.i(XTAG, " MsgType.KICK_CONF  receiveBroadcast success");
				Toast.makeText(
						context,
						getResources()
								.getString(R.string.kickoff_conf_xviewsdk),
						Toast.LENGTH_SHORT).show();
				EnterConf.mIOnXViewCallback.onConfMsgListener(1);
				PublicInfo.logout(mContext);
				XviewLog.i(XTAG, " MsgType.KICK_CONF  receiveBroadcast end");
				break;
			// 有成员加入
			case MsgType.MEMBER_ENTER:
				XviewLog.i(XTAG, " MsgType.MEMBER_ENTER  receiveBroadcast success");
				// 循环遍历看看谁设置摄像头为禁用
				userdevices = GlobalHolder.getInstance().getUserDevice();
				medias = GlobalHolder.getInstance().getMediaDevice();
				for (UserDevice userdevice : userdevices) {
					VideoDevice device = userdevice.getDevice();
					User user = userdevice.getUser();
					if (device != null && user != null) {
						int disable = device.getDisable();
						if (disable == 1) {
							GlobalHolder.getInstance().mOpenUers
									.remove(userdevice);
							if (mVideoOpenListener != null)
								mVideoOpenListener.closeVideo(userdevice);
						}
					}
				}

				if (mConfUserListAdapter != null) {
					Collections.sort(userdevices);
					mConfUserListAdapter.update(userdevices, medias);
				}
				XviewLog.i(XTAG, " MEMBER_ENTER - userid = " + GlobalHolder.EnterMemberUserId);
				XviewLog.i(XTAG, " MEMBER_ENTER - name = " + GlobalHolder.EnterMemberUserNickName);
				XviewLog.i(XTAG, " MEMBER_ENTER - name = " + GlobalHolder.EnterMemberUserData);
				EnterConf.mIOnXViewCallback.onMemberEnterListener(GlobalHolder.EnterMemberUserId,
						GlobalHolder.EnterMemberUserNickName, GlobalHolder.EnterMemberUserData);
				XviewLog.i(XTAG, " MsgType.MEMBER_ENTER  receiveBroadcast end");
				break;
			case MsgType.VIDEO_LIST:
				XviewLog.i(XTAG, " MsgType.VIDEO_LIST  receiveBroadcast success");
				// 循环遍历看看谁设置摄像头为禁用
				userdevices = GlobalHolder.getInstance().getUserDevice();
				medias = GlobalHolder.getInstance().getMediaDevice();
				XviewLog.i(XTAG, " MsgType.VIDEO_LIST  userdevices.size()=" + userdevices.size());
				XviewLog.i(XTAG, " MsgType.VIDEO_LIST  medias.size()=" + medias.size());
				for (UserDevice userdevice : userdevices) {
					VideoDevice device = userdevice.getDevice();
					User user = userdevice.getUser();
					if (device != null && user != null) {
						int disable = device.getDisable();
						if (disable == 1) {
							XviewLog.i(XTAG, " MsgType.VIDEO_LIST  mOpenUers.remove " + userdevice.getUser().getmUserId());
							GlobalHolder.getInstance().mOpenUers
									.remove(userdevice);
							if (mVideoOpenListener != null) {
								XviewLog.i(XTAG, " MsgType.VIDEO_LIST  closeVideo " + userdevice.getUser().getmUserId());
								mVideoOpenListener.closeVideo(userdevice);
							}
						}
					}
					XviewLog.i(XTAG, " MsgType.VIDEO_LIST  user.getmUserId() = " + user.getmUserId());
					XviewLog.i(XTAG, " MsgType.VIDEO_LIST  GlobalHolder.EnterMemberUserId = " + GlobalHolder.EnterMemberUserId);
					XviewLog.i(XTAG, " MsgType.VIDEO_LIST  GlobalHolder.getInstance().userdevices.size() = " + GlobalHolder.getInstance().userdevices.size());

					if (user.getmUserId() == GlobalHolder.EnterMemberUserId && GlobalHolder.getInstance().userdevices.size() == 2) {
						readyOpenDevice(userdevice);
						ConfRequest.getInstance().applyForControlPermission(3);
						isSpeak = true;
					}
				}

				if (mConfUserListAdapter != null) {
					Collections.sort(userdevices);
					mConfUserListAdapter.update(userdevices, medias);
				}
				XviewLog.i(XTAG, " MsgType.VIDEO_LIST  receiveBroadcast end");
				break;
			case MsgType.MODIFY_CONF_DESC:
				XviewLog.i(XTAG, " MsgType.MODIFY_CONF_DESC  receiveBroadcast success");
				// Bundle d = intent.getBundleExtra("conf_desc");
				// ModifyConfDesc desc = (ModifyConfDesc) d
				// .getSerializable("modify_conf_desc");
				// int syncvideo = Integer.parseInt(desc.getSyncvideo());
				// if (syncvideo == 2) {
				// List<UserDevice> mOpenVideos =
				// GlobalHolder.getInstance().mOpenUers;
				// List<MediaEntity> mOpenMedias =
				// GlobalHolder.getInstance().mOpenMedia;
				// while (mOpenVideos.size() > 0) {
				// for (int i = 0; i < mOpenVideos.size(); i++) {
				// closeDevice(mOpenVideos.get(i));
				// }
				// }
				// while (mOpenMedias.size() > 0) {
				// for (int i = 0; i < mOpenMedias.size(); i++) {
				// closeMedia(mOpenMedias.get(i));
				// }
				// }
				// }
				XviewLog.i(XTAG, " MsgType.MODIFY_CONF_DESC  receiveBroadcast end");
				break;
			case MsgType.SYNC_OPEN_VIDEO:
				XviewLog.i(XTAG, " MsgType.SYNC_OPEN_VIDEO  receiveBroadcast success");
				// Bundle data1 = intent.getExtras();
				// String DstDeviceID = data1.getString("DstDeviceID");
				// String DstUserID = data1.getString("DstUserID");
				//
				// if (DstUserID.length() > 4) {
				// autoOpenVideo(Long.parseLong(DstUserID), DstDeviceID);
				// } else {
				// autoOpenMedia(DstDeviceID);
				// }
				XviewLog.i(XTAG, " MsgType.SYNC_OPEN_VIDEO  receiveBroadcast end");
				break;
			case MsgType.SYNC_CLOSE_VIDEO:
				XviewLog.i(XTAG, " MsgType.SYNC_CLOSE_VIDEO  receiveBroadcast success");
				// Bundle data2 = intent.getExtras();
				// long nDstUserID = data2.getLong("nDstUserID", -1);
				// String sDstMediaID = data2.getString("sDstMediaID", "");
				// boolean bClose = data2.getBoolean("bClose", true);
				//
				// if (nDstUserID > 100) {
				// List<UserDevice> devices2 =
				// GlobalHolder.getInstance().mOpenUers;
				// for (int i = 0; i < devices2.size(); i++) {
				// if (devices2.get(i).getDevice().getId()
				// .equals(sDstMediaID)) {
				// autoCloseVideo(devices2.get(i));
				// }
				// }
				// } else {
				// List<MediaEntity> medias2 =
				// GlobalHolder.getInstance().mOpenMedia;
				// for (int i = 0; i < medias2.size(); i++) {
				// if (medias2.get(i).getMediaId().equals(sDstMediaID)) {
				// autoCloseMedia(sDstMediaID);
				// }
				// }
				// }
				XviewLog.i(XTAG, " MsgType.SYNC_CLOSE_VIDEO  receiveBroadcast end");
				break;
			case MsgType.VIDEOREMOTE_SETTING_COME: // PC远程设置移动端参数
				XviewLog.i(XTAG, " MsgType.VIDEOREMOTE_SETTING_COME  receiveBroadcast success");
				Bundle data = intent.getExtras();
				String dev = data.getString("dev", "");
				int nDisable = data.getInt("nDisable", 1);
				int fps = data.getInt("fps", 20);
				int nSizeIndex = data.getInt("nSizeIndex", 0);
				int bps = data.getInt("bps", 256);

				// 获取设备所有分辨率中的指定分辨率
				String temp = "";
				if (local_support.size() >= nSizeIndex)
					temp = local_support.get(nSizeIndex);

				if (temp.isEmpty())
					return;

				String size = "";
				if (local_support.contains(temp)) {
					if (finalSupportList.contains(temp)) {
						size = temp;
					} else {
						int[] datas = transferSI(finalSupportList);
						int width = Integer.parseInt(temp.split("X")[0]);
						int height = Integer.parseInt(temp.split("X")[1]);
						int t = width * height;
						int position = -1;

						for (int i = 0; i < datas.length; i++) {
							if (datas[i] < t) {
								position = i;
							}
						}
						if (position == -1) {
							if (t < datas[0]) {
								t = datas[0];
								size = PublicInfo.Support4Level;
							}
							if (t > datas[datas.length - 1]) {
								t = datas[datas.length - 1];
								size = PublicInfo.Support1Level;
							}
						} else {
							t = datas[position];
							switch (position) {
							case 0:
								size = PublicInfo.Support4Level;
								break;
							case 1:
								size = PublicInfo.Support3Level;
								break;
							case 2:
								size = PublicInfo.Support2Level;
								break;
							case 3:
								size = PublicInfo.Support1Level;
								break;
							default:
								break;
							}
						}
					}
				} else {
					return;
				}

				SPUtil.putConfigStrValue(context, "chicun", size);

				if (bps <= 128)
					bps = 128;
				if (bps > 128 && bps <= 256) {
					bps = (bps - 128) > (256 - bps) ? 256 : 128;
				}
				if (bps > 256 && bps <= 512) {
					bps = (bps - 256) > (512 - bps) ? 512 : 256;
				}
				if (bps > 512 && bps <= 1024) {
					bps = (bps - 512) > (1024 - bps) ? 1024 : 512;
				}
				if (bps > 1024)
					bps = 1024;
				SPUtil.putConfigIntValue(ConfActivity.this, "videoFlow", bps);
				if (bps == 128) {
					SPUtil.putConfigIntValue(context, "videoFlow", 128);
				} else if (bps == 256) {
					SPUtil.putConfigIntValue(context, "videoFlow", 256);
				} else if (bps == 512) {
					SPUtil.putConfigIntValue(context, "videoFlow", 512);
				} else if (bps == 1024) {
					SPUtil.putConfigIntValue(context, "videoFlow", 1024);
				}

				if (fps < 10)
					fps = 10;
				if (fps > 40)
					fps = 40;

				fps = (fps + 5) / 10 * 10;
				SPUtil.putConfigIntValue(context, "zl", fps);
				if (fps == 10) {
					SPUtil.putConfigIntValue(context, "zl", 10);
				} else if (fps == 20) {
					SPUtil.putConfigIntValue(context, "zl", 20);
				} else if (fps == 30) {
					SPUtil.putConfigIntValue(context, "zl", 30);
				} else if (fps == 40) {
					SPUtil.putConfigIntValue(context, "zl", 40);
				}

				Toast.makeText(
						ConfActivity.this,
						context.getString(R.string.apply_apply_xviewsdk) + size
								+ "\n" + bps + "Kbps\n" + fps + "fps",
						Toast.LENGTH_LONG).show();
				saveAndApplySetting();
				XviewLog.i(XTAG, " MsgType.VIDEOREMOTE_SETTING_COME  receiveBroadcast end");
				break;
			// 申请回应
			case MsgType.PERIMSSTYPE:
				XviewLog.i(XTAG, " MsgType.PERIMSSTYPE  receiveBroadcast success");
				Integer integer = GlobalHolder.getInstance().mSpeakUers
						.get(GlobalHolder.getInstance().getCurrentUserId());
				if (integer != null && integer == 3) {
					iv_conf_micro
							.setImageResource(R.drawable.conf_micro_fayan_selector_xviewsdk);
					isSpeak = true;
				} else {
					iv_conf_micro
							.setImageResource(R.drawable.conf_micro_selector_xviewsdk);
					isSpeak = false;
				}
				if (mConfUserListAdapter != null) {
					Collections.sort(userdevices);
					mConfUserListAdapter.update(userdevices, medias);
				}
				XviewLog.i(XTAG, " MsgType.PERIMSSTYPE  receiveBroadcast end");
				break;
			// 成员退出
			case MsgType.MEMBER_EXIT:
				XviewLog.i(XTAG, " MsgType.MEMBER_EXIT  receiveBroadcast success");
				long userid = intent.getLongExtra("userid", 0);
				String name = intent.getStringExtra("name");
				int exitcode = intent.getIntExtra("exitcode", 1);
				String email = intent.getStringExtra("email");

				XviewLog.i(XTAG, " MEMBER_EXIT - userid = " + userid);
				XviewLog.i(XTAG, " MEMBER_EXIT - name = " + name);
				XviewLog.i(XTAG, " MEMBER_EXIT - email = " + email);

				EnterConf.mIOnXViewCallback.onMemberExitListener(userid, (name.isEmpty() ? "" : name), email);

				if (EnterConf.mIOnXViewCallback == null) {
					XviewLog.i(XTAG, " MEMBER_EXIT callback is null");
				} else {
					XviewLog.i(XTAG, " MEMBER_EXIT callback not null");
				}
				if (name != null)
					Toast.makeText(
							context,
							name
									+ getResources().getString(
											R.string.one_exit_conf_xviewsdk),
							Toast.LENGTH_SHORT).show();
				List<UserDevice> findUserDevices = GlobalHolder.getInstance()
						.findHasOpenUserDevice(userid);
				for (UserDevice findUserDevice : findUserDevices) {
					if (GlobalHolder.getInstance().mOpenUers
							.contains(findUserDevice)) {
						VideoDevice device = findUserDevice.getDevice();
						if (device != null)
							GlobalHolder.getInstance().removeOpendDevice(
									userid, device.getId());
						if (mVideoOpenListener != null) {
							mVideoOpenListener.closeVideo(findUserDevice);
						}
					}
				}
				if (mConfUserListAdapter != null) {
					Collections.sort(userdevices);
					mConfUserListAdapter.update(userdevices, medias);
				}
				XviewLog.i(XTAG, " MsgType.MEMBER_EXIT  receiveBroadcast end");
				break;
			// 页面展示
			case MsgType.PAGE_DISPLAY:
				XviewLog.i(XTAG, " MsgType.PAGE_DISPLAY  receiveBroadcast success");
				if (mDocShareAdapter != null)
					mDocShareAdapter.notifyDataSetChanged();
				XviewLog.i(XTAG, " MsgType.PAGE_DISPLAY  receiveBroadcast end");
				break;
			// 页面添加
			case MsgType.PAGECOUNTCOME:
				XviewLog.i(XTAG, " MsgType.PAGECOUNTCOME  receiveBroadcast success");
				if (mDocShareAdapter != null)
					mDocShareAdapter.notifyDataSetChanged();
				XviewLog.i(XTAG, " MsgType.PAGECOUNTCOME  receiveBroadcast end");
				break;
			case MsgType.DOCCLOSE:
			case MsgType.DOCHASCOME:
				XviewLog.i(XTAG, " MsgType.DOCHASCOME  receiveBroadcast success");
				if (mDocShareAdapter != null)
					mDocShareAdapter.notifyDataSetChanged();
				XviewLog.i(XTAG, " MsgType.DOCHASCOME  receiveBroadcast end");
				break;
			// 有数据进来
			case MsgType.DATACOME:
				XviewLog.i(XTAG, " MsgType.DATACOME  receiveBroadcast success");
				int pageid = intent.getIntExtra("getnPageID", 0);
				String wbid = intent.getStringExtra("szWBoardID2");
				if (docFragment != null && wbid != null
						&& wbid.equals(docFragment.getCurrentWBid())) {
					if (pageid == docFragment.getCurrentPage()) {
						System.out.println("sldjflsjdflj");
						docFragment.valideImage();
					}
					return;
				}
				if (mFragment_wb != null && wbid != null
						&& wbid.equals(mFragment_wb.getCurrentWBid())) {
					if (pageid == mFragment_wb.getCurrentPage()) {
						mFragment_wb.valideImage();
					}
				}
				XviewLog.i(XTAG, " MsgType.DATACOME  receiveBroadcast end");
				break;
			// 创建混合流
			case MsgType.MEDIA_MIXER:
				XviewLog.i(XTAG, " MsgType.MEDIA_MIXER  receiveBroadcast success");
				if (mConfUserListAdapter != null)
					mConfUserListAdapter.update(userdevices, medias);
				XviewLog.i(XTAG, " MsgType.MEDIA_MIXER  receiveBroadcast end");
				break;

			case MsgType.CONF_MUTE:
				XviewLog.i(XTAG, " MsgType.CONF_MUTE  receiveBroadcast success");
				Integer muteid = GlobalHolder.getInstance().mSpeakUers
						.get(GlobalHolder.getInstance().getCurrentUserId());
				if (muteid != null && muteid == 3) {
					ConfRequest.getInstance().releaseControlPermission(3);
				}
				XviewLog.i(XTAG, " MsgType.CONF_MUTE  receiveBroadcast end");
				break;
			case MsgType.MEDIA_REMOVE:
				XviewLog.i(XTAG, " MsgType.MEDIA_REMOVE  receiveBroadcast success");
				String mediaId = intent.getStringExtra("removeMedia");
				for (int i = 0; i < GlobalHolder.getInstance().mOpenMedia
						.size(); i++) {
					if (mediaId != null
							&& GlobalHolder.getInstance().mOpenMedia.get(i)
									.getMediaId().equals(mediaId)) {
						mVideoOpenListener.closeMedia(GlobalHolder
								.getInstance().mOpenMedia.get(i));
						GlobalHolder.getInstance().mOpenMedia.remove(i);
					}
				}
				if (mConfUserListAdapter != null) {
					mConfUserListAdapter.update(userdevices, medias);
				}
				XviewLog.i(XTAG, " MsgType.MEDIA_REMOVE  receiveBroadcast end");
				break;
			}
		}

	}

	/**
	 * 将分辨率的String[]转换成int数组
	 * 
	 * @param local_supports
	 * @return
	 */
	private int[] transferSI(List<String> local_supports) {
		int[] datas = new int[local_supports.size()];
		for (int i = 0; i < local_supports.size(); i++) {
			String temp[] = local_supports.get(i).split("X");
			int w = Integer.parseInt(temp[0]);
			int h = Integer.parseInt(temp[1]);
			datas[i] = w * h;
		}
		return datas;
	}

	/**
	 * 耳机广播接收器
	 * 
	 * @author Hello
	 * 
	 */
	class ErjiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
				if (intent.hasExtra("state")) {
					int state = intent.getIntExtra("state", 0);
					if (state == 1) {
						setSpeakLounder(false);
					} else if (state == 0) {
						setSpeakLounder(true);
					}
				}
			}
		}
	}

	/**
	 * 显示断网对话框
	 */
	public void showDisconneced() {
		XviewLog.i("disconnected show dialog");
		dissconnected_dialog = Utils.createDialog(mContext,
				R.drawable.icon_xviewsdk,
				getResources().getString(R.string.hint_xviewsdk),
				getResources().getString(R.string.netdissconnected_xviewsdk),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						PublicInfo.logout(mContext);
					}
				});
		dissconnected_dialog.setCanceledOnTouchOutside(false);
		dissconnected_dialog.show();
	}

	/**
	 * 视频设备的监听器
	 * 
	 * @author laoyu
	 * 
	 */
	public interface VideoOpenListener {
		/**
		 * 关闭视频设备
		 * 
		 * @param u
		 */
		void closeVideo(UserDevice u);

		/**
		 * 打开视频设备
		 * 
		 * @param u
		 */
		void openVideo(UserDevice u);

		/**
		 * 换成前置 还是后置摄像头
		 * 
		 * @param i
		 */
		void changeCamera(int i);

		/**
		 * 应用设置的分辨率/视频流量/帧率/格式/设备方向/是否前置
		 * 
		 * @param width
		 * @param height
		 * @param videoFlow
		 * @param frameRate
		 * @param format
		 * @param requestedOrientation
		 * @param enabeleFrontCam
		 */
		void applySetting(int width, int height, int videoFlow, int frameRate,
				int format, int requestedOrientation, boolean enabeleFrontCam);

		/**
		 * 打开视频流
		 * 
		 * @param m
		 */
		void openMedia(MediaEntity m);

		/**
		 * 关闭视频流
		 * 
		 * @param m
		 */
		void closeMedia(MediaEntity m);
	}

	/**
	 * 显示或隐藏底边栏
	 */
	private  static void showOrHidellBottomBar() {
		ObjectAnimator alphaHideAnimLBB = ObjectAnimator.ofFloat(llBottomBar,
				"alpha", 1, 0);
		ObjectAnimator alphaShowAnimLBB = ObjectAnimator.ofFloat(llBottomBar,
				"alpha", 0, 1);
		ObjectAnimator alphaHideAnimRCC = ObjectAnimator.ofFloat(llTopBar,
				"alpha", 1, 0);
		ObjectAnimator alphaShowAnimRCC = ObjectAnimator.ofFloat(llTopBar,
				"alpha", 0, 1);

		if (llBottomBar.getVisibility() == View.VISIBLE) {
			alphaHideAnimLBB.start();
			alphaHideAnimRCC.start();
			alphaHideAnimLBB.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					llBottomBar.setVisibility(View.GONE);
					llTopBar.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel(Animator animation) {

				}
			});
		} else {
			llBottomBar.setVisibility(View.VISIBLE);
			llTopBar.setVisibility(View.VISIBLE);
			alphaShowAnimLBB.start();
			alphaShowAnimRCC.start();
		}
	}

	public VideoOpenListener getmVideoOpenListener() {
		return mVideoOpenListener;
	}

	public void setmVideoOpenListener(VideoOpenListener mVideoOpenListener) {
		this.mVideoOpenListener = mVideoOpenListener;
	}

	/**
	 * 初始化本地的设备的支持的分辨率
	 */
	private void initLocalSupport() {
		XviewLog.i(XTAG, " initLocalSupport");
		if (deviceList == null || deviceList.size() == 0)
			return;

		VideoCaptureDevice video = deviceList.get(0);
		for (CaptureCapability cap : video.capabilites) {
			String one_local = cap.width + "X" + cap.height;
			local_support.add(one_local);
		}

		finalSupportList.clear();
		finalSupportList.add(PublicInfo.Support4Level);
		finalSupportList.add(PublicInfo.Support3Level);
		finalSupportList.add(PublicInfo.Support2Level);
		finalSupportList.add(PublicInfo.Support1Level);
	}

	@Override
	public void backHome() {
		// 退到后台,程序将在3秒后退出会议
		if (!ConfActivity.this.isFinishing()) {
			PublicInfo.toast(mContext, R.string.backhome_xviewsdk);
		}

	}

	/**
	 * 按住菜单键 控制视频之外的布局
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if (iv_conf_setting.isFocused()) {
				iv_conf_setting.requestFocus();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 打开用户列表
	 */
	protected void openUserlist() {
		right_sliding_layer.removeAllViews();
		if (!right_sliding_layer.isOpened()) {
			PublicInfo.isOpenedUserList = true;
			PublicInfo.isOpenedShareList = false;
			right_sliding_layer.addView(view_conf_userlist);
			right_sliding_layer.openLayer(false);

			initSV();

			LayoutParams params = right_sliding_layer.getLayoutParams();
			if (SPUtil.getConfigIntValue(mContext, "viewModePosition", 1) == 1) {
				params.height = LayoutParams.MATCH_PARENT;
				params.width = LayoutParams.MATCH_PARENT;
			} else {
				params.height = LayoutParams.MATCH_PARENT;
				params.width = screenWidth / 3 * 2;
			}
			PublicInfo.isOpenedUserList = true;
			right_sliding_layer.setLayoutParams(params);
			setRequestFoucsAble(false);
		}
	}

	private void setRequestFoucsAble(boolean flag) {
		iv_conf_micro.setFocusable(flag);
		iv_conf_share.setFocusable(flag);
		iv_conf_userlist.setFocusable(flag);
		iv_conf_setting.setFocusable(flag);
		ivEndConf.setFocusable(flag);
		iv_conf_closevideo.setFocusable(flag);
	}

	/**
	 * 打开文档列表
	 */
	protected void openShareList() {
		right_sliding_layer.removeAllViews();
		if (!right_sliding_layer.isOpened()) {
			PublicInfo.isOpenedShareList = true;
			right_sliding_layer.addView(view_conf_sharedata);
			view_confdatalist_back.setClickable(true);
			view_confdatalist_back.setFocusable(true);
			view_confdatalist_back.requestFocus();

			initSV();

			LayoutParams params = right_sliding_layer.getLayoutParams();
			if (SPUtil.getConfigIntValue(mContext, "viewModePosition", 1) == 1) {
				params.height = LayoutParams.MATCH_PARENT;
				params.width = LayoutParams.MATCH_PARENT;
			} else {
				params.height = LayoutParams.MATCH_PARENT;
				params.width = screenWidth / 3 * 2;
				if (PublicInfo.OPENED_VIDEO_COUNT == 0) {
					params.width = LayoutParams.MATCH_PARENT;
				}
			}

			right_sliding_layer.setLayoutParams(params);
			right_sliding_layer.openLayer(false);

			if (PublicInfo.OPENED_VIDEO_COUNT == 2
					&& SPUtil
							.getConfigIntValue(mContext, "viewModePosition", 1) == 0) {
				PublicInfo.videoFragmentHandler
						.sendEmptyMessage(PublicInfo.OPEN_SHARE_TO_VIDEOFRAGMENT);
				PublicInfo.columnLayoutHandler
						.sendEmptyMessage(PublicInfo.OPEN_SHARE_TO_COLUMNLAYOUT);
			} else {
				if (PublicInfo.OPENED_VIDEO_COUNT != 4
						&& SPUtil.getConfigIntValue(mContext,
								"viewModePosition", 1) == 0)
					PublicInfo.columnLayoutHandler
							.sendEmptyMessage(PublicInfo.OPEN_SHARE_TO_COLUMNLAYOUT);
			}
		}
	}

	/**
	 * 打开文档详情列表
	 */
	protected void openDocDetailList() {
		if (right_sliding_layer.isOpened()) {
			right_sliding_layer.removeAllViews();
			right_sliding_layer.addView(view_conf_doc_detail);
			doc_iv_left.requestFocus();
			right_sliding_layer.openLayer(false);
			setRequestFoucsAble(false);
		}
	}

	/**
	 * 关闭SlidingLayer播放器
	 */
	private void closePlayer() {
		if (right_sliding_layer != null && right_sliding_layer.isOpened()) {
			right_sliding_layer.closeLayer(false);
			setRequestFoucsAble(true);
		}
	}

	/**
	 * 保存并应用设置参数
	 */
	private void saveAndApplySetting() {
		// 获取设备朝向
		int deviceDirection = SPUtil.getConfigIntValue(mContext,
				"viewModePosition", 1);
		// 获取前置后置
		int camera = SPUtil.getConfigIntValue(this, "camera", 0);
		// 获取分辨率
		String default_video_wh = SPUtil.getConfigStrValue(mContext, "chicun");
		if (default_video_wh.isEmpty()) {
			default_video_wh = PublicInfo.Support2Level;
		}
		String[] whs = default_video_wh.split("X");
		int width = Integer.parseInt(whs[0]);
		int height = Integer.parseInt(whs[1]);
		// 获取帧率
		int frameRate = SPUtil.getConfigIntValue(mContext, "zl", 20);
		if (frameRate < 10) {
			frameRate = 30;
		}
		if (frameRate > 40) {
			frameRate = 40;
		}

		// 视频流量
		int videoFlow = 0;
		try {
			videoFlow = SPUtil.getConfigIntValue(this, "videoFlow", 512);
			videoFlow *= 1024;
		} catch (Exception e) {
			/**
			 * 根据分辨率计算所需的视频流量
			 */
			if (default_video_wh.equals(PublicInfo.Support4Level)) {
				videoFlow = 128 * 1024;
			} else if (default_video_wh.equals(PublicInfo.Support3Level)) {
				videoFlow = 256 * 1024;
			} else if (default_video_wh.equals(PublicInfo.Support2Level)) {
				videoFlow = 512 * 1024;
			} else if (default_video_wh.equals(PublicInfo.Support1Level)) {
				videoFlow = 1024 * 1024;
			} else {
				videoFlow = 512 * 1024;
			}
		}
		// 获取摄像头索引
		int configIntValue = SPUtil.getConfigIntValue(mContext, "ccindex", 0);

		devInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
		deviceList = devInfo.deviceList;
		VideoRequest.getInstance().enumMyVideos(0);
		VideoCaptureDevice device = deviceList.get(configIntValue);

		if (device != null) {
			if (mVideoOpenListener != null) {
				mVideoOpenListener.applySetting(width, height, videoFlow,
						frameRate, ImageFormat.NV21, deviceDirection,
						(camera == 0 ? true : false));
			}
		}
	}

	@Override
	public void OnBackListener(int flag) {
		if (flag == 1) {
			onBackPressed();
			if (PublicInfo.OPENED_VIDEO_COUNT != 4
					&& SPUtil
							.getConfigIntValue(mContext, "viewModePosition", 1) == 0)
				PublicInfo.columnLayoutHandler
						.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_COLUMNLAYOUT);
		}
	}

	@Override
	public void OnBackListener2(int flag) {
		if (flag == 2) {
			onBackPressed();
			if (PublicInfo.OPENED_VIDEO_COUNT != 4
					&& SPUtil
							.getConfigIntValue(mContext, "viewModePosition", 1) == 0)
				PublicInfo.columnLayoutHandler
						.sendEmptyMessage(PublicInfo.CLOSE_SHARE_TO_COLUMNLAYOUT);
		}
	}

}
