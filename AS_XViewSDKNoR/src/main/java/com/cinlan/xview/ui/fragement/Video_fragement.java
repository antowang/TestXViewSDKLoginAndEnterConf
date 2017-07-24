//package com.cinlan.xview.ui.fragement;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceHolder.Callback;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.view.WindowManager;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.cinlan.core.LocaSurfaceView;
//import com.cinlan.core.VideoCaptureDevInfo;
//import com.cinlan.core.VideoCaptureDevInfo.VideoCaptureDevice;
//import com.cinlan.jni.VideoRequest;
//import com.cinlan.xview.PublicInfo;
//import com.cinlan.xview.bean.User;
//import com.cinlan.xview.bean.UserDevice;
//import com.cinlan.xview.bean.VideoDevice;
//import com.cinlan.xview.msg.MediaEntity;
//import com.cinlan.xview.msg.MsgType;
//import com.cinlan.xview.ui.callback.VideoOpenListener;
//import com.cinlan.xview.utils.GlobalHolder;
//import com.cinlan.xview.utils.SPUtil;
//import com.cinlan.xview.utils.VideoHelper;
//import com.cinlan.xview.widget.ColumnLayout;
//import com.cinlan.xview.widget.ColumnLayout.setonSizeChangeListener;
//import com.cinlankeji.khb.iphone.R;
//
//public class Video_fragement extends Fragment implements VideoOpenListener,
//		setonSizeChangeListener, OnTouchListener {
//
//	/**
//	 * 显示视频画面的SurfaceView
//	 */
//	private ArrayList<SurfaceView> mSurfaceViewList = new ArrayList<SurfaceView>();
//	/**
//	 * 中间大布局
//	 */
//	private ColumnLayout videoLargeLayout;
//	/**
//	 * 右上角的小布局
//	 */
//	private RelativeLayout videoSmallLayout;
//	/**
//	 * 整体的父布局
//	 */
//	private RelativeLayout videoRootView;
//	/**
//	 * 环境变量
//	 */
//	private FragmentActivity activity;
//	/**
//	 * 当前的会议id
//	 */
//	private long confid;
//	/**
//	 * 根据设备智能选取的4个级别的分辨率.
//	 */
//	private List<String> app_support = new ArrayList<>();
//	/**
//	 * 是否可以切换画中画视频,设置了2秒延迟
//	 */
//	private boolean isCanSwitchTwoSurfaceView = true;
//	private VideoCaptureDevInfo devInfo;
//	/**
//	 * 视频捕获设备类的集合
//	 */
//	private List<VideoCaptureDevice> deviceList;
//	/**
//	 * 用于存放用户设备视频id和对应的视频帮助类集合
//	 */
//	private Map<String, VideoHelper> opencache = new HashMap<String, VideoHelper>();
//	/**
//	 * 用于存放视频流id和对应的视频帮助类集合
//	 */
//	private Map<String, VideoHelper> mediacache = new HashMap<String, VideoHelper>();
//
//	private Handler handler = new Handler() {
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case PublicInfo.OPEN_SHARE_TO_VIDEOFRAGMENT:
//				// 根据打开文档调整画中画布局的位置
//				changeLayoutForShare();
//				break;
//			case PublicInfo.CLOSE_SHARE_TO_VIDEOFRAGMENT:
//				// 设置小布局的位置为右上角
//				calcSmallLayoutSize(true);
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	public static Video_fragement newInstance() {
//		Video_fragement fragment = new Video_fragement();
//		return fragment;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		PublicInfo.videoFragmentHandler = handler;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.fragement_video_xviewsdk, container, false);
//
//		videoLargeLayout = (ColumnLayout) view.findViewById(R.id.video_content_main_xviewsdk);
////		videoLargeLayout.setMsetonSizeChangeListener(this);
//		videoSmallLayout = (RelativeLayout) view
//				.findViewById(R.id.video_content_main2_xviewsdk);
//		videoRootView = (RelativeLayout) view.findViewById(R.id.video_content_main3_xviewsdk);
//
//		return view;
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		activity = getActivity();
//		try {
//			confid = GlobalHolder.getInstance().getmCurrentConf().getId();
//		} catch (Exception e) {
//			confid = GlobalHolder.CurrentConfId;
//		}
//		getScreenWH();
//
//		devInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
//		deviceList = devInfo.deviceList;
//		// HaveCamera,opened.
//		SPUtil.putConfigIntValue(activity, "local", 1);
//		initLocalSupport();
//		openLocal();
//		getScreenWH();
//	}
//
//	private void initLocalSupport() {
//		if (deviceList == null || deviceList.size() == 0)
//			return;
//		app_support.clear();
//		app_support.add(PublicInfo.Support4Level);
//		app_support.add(PublicInfo.Support3Level);
//		app_support.add(PublicInfo.Support2Level);
//		app_support.add(PublicInfo.Support1Level);
//	}
//
//	/**
//	 * 打开本地视频
//	 */
//	private void openLocal() {
//		// 设备朝向
//		int ori = SPUtil.getConfigIntValue(getActivity(), "viewModePosition", 1);
//
//
//		PublicInfo.DEVICE_ORIENTATION = ori;
//		// 摄像头朝向
//		int camera = SPUtil.getConfigIntValue(activity, "camera", 0);
//		// 码率
//		int malv = SPUtil.getConfigIntValue(activity, "ml", 70 * 1024);
//		// 帧率
//		int zelv = SPUtil.getConfigIntValue(activity, "zl", 15);
//		// 分辨率
//		String default_video_wh = SPUtil.getConfigStrValue(activity, "chicun");
//
//		/* 如果分辨率为null或不为4个分辨率之一就默认为高清 */
//		if (default_video_wh.isEmpty() || !app_support.contains(default_video_wh)) {
//
//			default_video_wh = PublicInfo.Support2Level;
//		}
//
//		String[] whs = default_video_wh.split("X");
//		devInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
//		deviceList = devInfo.deviceList;
//		VideoRequest.getInstance().enumMyVideos(0);
//
//		if (deviceList != null && deviceList.size() >= 1) {
//			if (deviceList.get(0).deviceUniqueName != null) {
//				// 设置默认的视频设备
//				VideoRequest.getInstance().setDefaultVideoDev(
//						deviceList.get(0).deviceUniqueName);
//				// 设置默认的设备名称
//				devInfo.SetDefaultDevName(devInfo.deviceList.get(0).deviceUniqueName);
//				// 设置采集参数
//
//				LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance().getVideoConfig();
//
//				config.videoWidth = Integer.parseInt(whs[0]);
//				config.videoHeight = Integer.parseInt(whs[1]);
//				config.videoBitRate = malv;
//				config.videoFrameRate = zelv;
//				config.videoMaxKeyframeInterval = zelv * 2;
//				config.enabeleFrontCam = camera == 0 ? true : false;
//				LocaSurfaceView.getInstance().setVideoConfig(config);
//
//				SPUtil.putConfigIntValue(activity, "ccindex", 0);
//
//				UserDevice userDevice = GlobalHolder.getInstance().currendUserDevice;
//				GlobalHolder.getInstance().mOpenUerDevList.add(userDevice);
//				openVideo(userDevice,false);
//			}
//		}
//
//	}
//
//	/**
//	 * 打开视频的User
//	 */
//	private User user;
//
//	public void closeVideo(UserDevice userDevice) {
//		if (userDevice == null)
//			return;
//
//		// 获得用户
//		User user = userDevice.getUser();
//		// 获得视频设备
//		VideoDevice device = userDevice.getDevice();
//		if (user == null || device == null)
//			return;
//
//		VideoHelper videoHelper = opencache.get(device.getId());
//		if (videoHelper == null)
//			return;
//
//		// 关闭视频
//		if (user.getmUserId() == GlobalHolder.getInstance().getLocalUserId()) {
//			VideoRequest.getInstance().closeVideoDevice(confid,
//					videoHelper.getUserid(), "", null, 1);
//		} else {
//			VideoRequest.getInstance().closeVideoDevice(confid,
//					videoHelper.getUserid(), videoHelper.getSzDevid(),
//					videoHelper.getVideoPlayer(), 1);
//		}
//
//		// 释放SurfaceView
//		SurfaceView view = videoHelper.getView();
//
//		// 从集合和布局中移除SurfaceView
//		mSurfaceViewList.remove(view);
//		opencache.remove(user.getmUserId());
//		videoLargeLayout.removeView(view);
//		videoSmallLayout.removeView(view);
//		view.getHolder().getSurface().release();
//
//		if (mSurfaceViewList.size() == 1) {
//			PublicInfo.OPENED_VIDEO_COUNT = 1;
//
//			// 根据横竖屏设置父布局尺寸
//			setLayoutMatchParent(videoRootView);
//
//			// 如果小布局里还有surface,移到大布局里
//			if (videoSmallLayout.getChildCount() > 0) {
//				// 将小布局的surface取出放到大布局中并隐藏小布局
//				movePipToBigLayout();
//			}
//
//			// 设置大布局宽高为填充父布局
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 设置小布局宽高各为屏幕1/3大小,隐藏小布局
//			setPipLocation(true);
//
//		} else if (mSurfaceViewList.size() == 2) {
//			PublicInfo.OPENED_VIDEO_COUNT = 2;
//
//			// 如果打开过4路那么现在就要把布局2添加进来
//			if (videoRootView.getChildCount() != 2) {
//				videoRootView.addView(videoSmallLayout);
//			}
//
//			// 设置父布局和大布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 设置小布局宽高为屏幕1/3大小,位置是右上角
//			setPipLocation(false);
//
//			// 从大布局取出一个surface放到小布局中
//			movePipToSmallLayout();
//
//			// 画中画布局里的surface不可点击
//			setSurfaceClickable(false);
//
//			// 设置大小布局可点击
//			setLayoutClickable(true);
//
//			// 还是要手动调用一次点击切换画中画视频
//			videoSmallLayout.performClick();
//
//		} else if (mSurfaceViewList.size() == 3) {
//
//			PublicInfo.OPENED_VIDEO_COUNT = 3;
//
//			// 设置父布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//
//			// 画中画布局里的surface可点击
//			setSurfaceClickable(true);
//
//			// 设置大小布局不可点击
//			setLayoutClickable(false);
//
//		} else if (mSurfaceViewList.size() == 4) {
//
//			PublicInfo.OPENED_VIDEO_COUNT = 4;
//
//			// 设置父布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//
//			// 画中画布局里的surface可点击
//			setSurfaceClickable(true);
//
//			// 设置大小布局不可点击
//			setLayoutClickable(false);
//
//		} else if (mSurfaceViewList.size() == 0) {
//
//			PublicInfo.OPENED_VIDEO_COUNT = 0;
//
//		}
//	}
//
//	@Override
//	public void closeVideo(long userId,int pos) {
//
//	}
//
//	@Override
//	public void openVideo(long userId, VideoDevice vd, boolean isInit) {
//
//	}
//
//	/**
//	 * 设置大小布局是否可点击
//	 *
//	 * @param isClick
//	 */
//	private void setLayoutClickable(boolean isClick) {
//		videoSmallLayout.setClickable(isClick);
//		videoLargeLayout.setClickable(isClick);
//		videoSmallLayout.setFocusable(isClick);
//		videoLargeLayout.setFocusable(isClick);
//	}
//
//	/**
//	 * 获取屏幕宽高
//	 */
//	private void getScreenWH() {
//		WindowManager wm = (WindowManager) getActivity().getSystemService(
//				Context.WINDOW_SERVICE);
//
//		// 提供全局使用
//		PublicInfo.screenHeight = wm.getDefaultDisplay().getHeight();
//		PublicInfo.screenWidth = wm.getDefaultDisplay().getWidth();
//
//		// 每获取一次屏幕宽高就会重置子View
//		videoLargeLayout.changeLayout();
//	}
//
//	/**
//	 * 打开用户视频设备
//	 */
//	public void openVideo(UserDevice userDevice,boolean isInit) {
//		if (userDevice == null)
//			return;
//
//		// 获取视频设备
//		VideoDevice device = userDevice.getDevice();
//		// 获取用户
//		user = userDevice.getUser();
//
//		if (device == null || user == null)
//			return;
//
//		// 判断是否是本地
//		boolean isLocal = user.getmUserId() == GlobalHolder.getInstance().getLocalUserId();
//
//		// 它会实例化本地或远端的SurfaceView
//		final VideoHelper mVideoHelper = new VideoHelper(activity, device.getId(), isLocal);
//
//
//		// 再返回SurfaceView
//		SurfaceView view = mVideoHelper.getView();
//
//		mVideoHelper.setUserid(user.getmUserId());
//
//		/**
//		 * 判断打开的是本地摄像头视频还是其他路视频.
//		 */
//		if (isLocal) {
//
//			// 设置本地surface大小为填充父布局
//			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
//					LayoutParams.MATCH_PARENT));
//
//			// 隐藏小布局，将本地摄像头视频添加到大布局中
//			if (videoSmallLayout != null)
//				videoSmallLayout.setVisibility(View.GONE);
//			videoLargeLayout.addView(view);
//
//			// 监听已打开的本地的surface
//			addCallbackForLocalSurface(view);
//
//			// view.setId(0x10001001);
//
//		} else {
//
//			// 设置小布局宽高各为1/3屏幕大小
//			setPipLocation(false);
//
//			// 监听已打开的远端视频
//			addCallbackForOtherSurface(mVideoHelper, (SurfaceView) view);
//
//			// 先将远端视频添加到大布局中
//			videoLargeLayout.addView(view);
//
//		}
//
//		mSurfaceViewList.add(view);
//		getScreenWH();
//		PublicInfo.DEVICE_ORIENTATION = SPUtil.getConfigIntValue(getActivity(),
//				"viewModePosition", 1);
//
//		if (mSurfaceViewList.size() == 1) { // 当前只有1路视频的情况下
//			PublicInfo.OPENED_VIDEO_COUNT = 1;
//
//			// 设置父布局和大布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//
//		} else if (mSurfaceViewList.size() == 2) { // 当前有2路视频的情况下
//			PublicInfo.OPENED_VIDEO_COUNT = 2;
//
//			// 如果没有布局2就要把布局2添加进来
//			if (videoRootView.getChildCount() != 2) {
//				videoRootView.addView(videoSmallLayout);
//			}
//
//			// 设置父布局和大布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 显示并设置小布局的位置为右上角,大小为屏幕宽高的1/3
//			setPipLocation(false);
//
//			// 从大布局取出一路视频放到小布局中
//			movePipToSmallLayout();
//
//			// 设置所有视频为不可点击
//			setSurfaceClickable(false);
//			// 设置所有布局为可点击
//			setLayoutClickable(true);
//
//		} else if (mSurfaceViewList.size() == 3) { // 当前有3路视频的情况下
//			PublicInfo.OPENED_VIDEO_COUNT = 3;
//
//			// 设置父布局和大布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 将视频从小布局取出来放到大布局里
//			movePipToBigLayout();
//
//			// 设置所有视频为可点击
//			setSurfaceClickable(true);
//			// 设置所有布局为不可点击
//			setLayoutClickable(false);
//
//		} else if (mSurfaceViewList.size() == 4) { // 当前有4路视频的情况下
//			PublicInfo.OPENED_VIDEO_COUNT = 4;
//
//			// 设置父布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//
//			// 设置所有视频为可点击
//			setSurfaceClickable(true);
//			// 设置所有布局为不可点击
//			setLayoutClickable(false);
//
//			/**
//			 * 4路视频时,video_content_main2会在右上角显示,隐藏不掉.<br>
//			 * 所以只能移除掉.
//			 */
//			videoRootView.removeView(videoSmallLayout);
//		}
//
//		videoSmallLayout.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (!isCanSwitchTwoSurfaceView) {
//					// 画中画切换需2s以上一次,否则频率过高会导致崩溃
//					PublicInfo.toast(getActivity(),
//							R.string.change_camera_fast_xviewsdk);
//					return;
//				}
//
//				if (mSurfaceViewList.size() == 2) {
//					// 如果用户打开了文件柜则禁止切换画中画
//					if (PublicInfo.isOpenedShareList) {
//						return;
//					}
//
//					/**
//					 * 大布局和小布局交换视频,实际上就是关->开.
//					 */
//					View s2 = videoSmallLayout.getChildAt(0);
//					View s1 = videoLargeLayout.getChildAt(0);
//					videoSmallLayout.removeAllViews();
//					videoLargeLayout.removeAllViews();
//					videoSmallLayout.addView(s1);
//					videoLargeLayout.addView(s2);
//
//					// 设置父布局和大布局为填充屏幕
//					setLayoutMatchParent(videoRootView);
//					setLayoutMatchParent(videoLargeLayout);
//
//					isCanSwitchTwoSurfaceView = false;
//				}
//
//				/**
//				 * 倒计时2s
//				 */
//				new Handler().postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						isCanSwitchTwoSurfaceView = true;
//					}
//				}, 2000);
//
//			}
//		});
//
//		/**
//		 * 打开两路视频时必须要代码执行一次点击切换.<br>
//		 * 否则新打开的视频会被覆盖看不见.
//		 */
//		if (mSurfaceViewList.size() == 2) {
//			videoSmallLayout.performClick();
//		}
//
//		opencache.put(device.getId(), mVideoHelper);
//		videoLargeLayout.requestFocus();
//
//	}
//
//	/**
//	 * 将小布局的surface取出放到大布局中并隐藏小布局
//	 */
//	private void movePipToBigLayout() {
//		View tempSV = videoSmallLayout.getChildAt(0);
//
//		videoSmallLayout.removeAllViews();
//		videoSmallLayout.setVisibility(View.GONE);
//		videoLargeLayout.addView(tempSV);
//	}
//
//	/**
//	 * 从大布局取出一路视频放到小布局中.
//	 */
//	private void movePipToSmallLayout() {
//		View s = videoLargeLayout.getChildAt(1);
//		videoLargeLayout.removeViewAt(1);
//		videoSmallLayout.addView(s);
//	}
//
//	/**
//	 * 设置画中画的小布局的位置<br>
//	 * true隐藏小布局,false显示小布局
//	 */
//	private void setPipLocation(boolean isHide) {
//		if (isHide)
//			videoSmallLayout.setVisibility(View.GONE);
//		else
//			videoSmallLayout.setVisibility(View.VISIBLE);
//
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//				PublicInfo.screenWidth / 3, PublicInfo.screenHeight / 3);
//		params.setMargins(PublicInfo.screenWidth / 3 * 2, 0, 0, 0);
//
//		params.width = PublicInfo.screenWidth / 3;
//		params.height = PublicInfo.screenHeight / 3;
//
//		videoSmallLayout.setLayoutParams(params);
//	}
//
//	/**
//	 * 根据打开文档调整画中画布局的位置
//	 */
//	private void changeLayoutForShare() {
//		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
//				PublicInfo.screenWidth / 3, PublicInfo.screenHeight / 3);
//		lp2.setMargins(PublicInfo.screenWidth / 3 * 2,
//				PublicInfo.screenHeight / 3, 0, 0);
//		videoSmallLayout.setLayoutParams(lp2);
//	}
//
//	/**
//	 * 监听已打开的本地的Surface
//	 *
//	 * @param surface
//	 */
//	private void addCallbackForLocalSurface(final SurfaceView surface) {
//		surface.getHolder().addCallback(new Callback() {
//
//			@Override
//			public void surfaceDestroyed(SurfaceHolder holder) {
//				VideoRequest.getInstance().closeVideoDevice(confid,
//						GlobalHolder.getInstance().getLocalUserId(), "",
//						null, 1);
//
//				LocaSurfaceView.getInstance().setbPreview(false);
//			}
//
//			@Override
//			public void surfaceCreated(SurfaceHolder holder) {
//				VideoRequest.getInstance().openVideoDevice(confid,
//						GlobalHolder.getInstance().getLocalUserId(), "",
//						null, 1);
//
//				LocaSurfaceView.getInstance().setbPreview(true);
//			}
//
//			@Override
//			public void surfaceChanged(SurfaceHolder holder, int format,
//					int width, int height) {
//
//			}
//		});
//	}
//
//	/**
//	 * 设置小布局的位置的大小
//	 *
//	 * @param flag
//	 *            是否宽大于高
//	 */
//	private void calcSmallLayoutSize(boolean flag) {
//
//		LayoutParams params = videoSmallLayout.getLayoutParams();
//		// 获取是竖屏还是横屏
//		PublicInfo.DEVICE_ORIENTATION = SPUtil.getConfigIntValue(getActivity(),
//				"viewModePosition", 1);
//		SurfaceView s1 = (SurfaceView) videoSmallLayout.getChildAt(0);
//
//		getScreenWH();
//
//		// if (s1.getId() == 0x10001001) { // 本地摄像头
//		// params.height = PublicInfo.screenHeight / 3;
//		// params.width = PublicInfo.screenWidth / 3;
//		// videoSmallLayout.setLayoutParams(params);
//		// setPipShow(false);
//		// return;
//		// }
//		if (PublicInfo.DEVICE_ORIENTATION == 0) {
//			if (flag) {
//				params.height = PublicInfo.screenHeight / 3;
//				params.width = PublicInfo.screenWidth / 3;
//			} else {
//				params.height = PublicInfo.screenWidth / 3;
//				params.width = PublicInfo.screenHeight / 3;
//			}
//		} else { // vertical
//			if (flag) {
//				params.height = PublicInfo.screenWidth / 3;
//				params.width = PublicInfo.screenHeight / 3;
//			} else {
//				params.height = PublicInfo.screenHeight / 3;
//				params.width = PublicInfo.screenWidth / 3;
//			}
//		}
//		videoSmallLayout.setLayoutParams(params);
//
//		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
//				params.width, params.height);
//		lp2.setMargins(PublicInfo.screenWidth - params.width, 0,
//				PublicInfo.screenWidth, params.height);
//		videoSmallLayout.setLayoutParams(lp2);
//
//		Canvas canvas = s1.getHolder().lockCanvas();
//		if (canvas != null) {
//			canvas.drawColor(Color.BLACK); // 涂黑两次是为了防止图像闪屏
//			canvas.drawColor(Color.BLACK);
//
//			s1.getHolder().unlockCanvasAndPost(canvas);
//		}
//		videoSmallLayout.setGravity(Gravity.CENTER);
//	}
//
//	private void addCallbackForOtherSurface(final VideoHelper mVideoHelper,
//			final SurfaceView surface) {
//
//		surface.getHolder().addCallback(new Callback() {
//
//			@Override
//			public void surfaceDestroyed(SurfaceHolder holder) {
//				VideoRequest.getInstance().closeVideoDevice(confid,
//						user.getmUserId(), mVideoHelper.getSzDevid(),
//						mVideoHelper.getVideoPlayer(), 1);
//			}
//
//			@Override
//			public void surfaceCreated(SurfaceHolder holder) {
//				VideoRequest.getInstance().openVideoDevice(confid,
//						user.getmUserId(), mVideoHelper.getSzDevid(),
//						mVideoHelper.getVideoPlayer(), 1);
//			}
//
//			@Override
//			public void surfaceChanged(SurfaceHolder holder, int format,
//					int width, int height) {
//			}
//		});
//	}
//
//	/**
//	 * 父布局宽高为填充屏幕
//	 */
//	private void setLayoutMatchParent(ViewGroup viewGroup) {
//
//		LayoutParams params = viewGroup.getLayoutParams();
//		params.height = LayoutParams.MATCH_PARENT;
//		params.width = LayoutParams.MATCH_PARENT;
//		viewGroup.setLayoutParams(params);
//	}
//
//	/**
//	 * 获得当前分辨率
//	 *
//	 * @return
//	 */
//	private String[] getCurChiCun() {
//		String default_video_wh = SPUtil.getConfigStrValue(activity, "chicun");
//		if ("".equals(default_video_wh)) {
//			default_video_wh = app_support.get(0);
//		}
//		String[] whs = default_video_wh.split("X");
//		return whs;
//	}
//
//	/**
//	 * 设置所有视频是否可点击
//	 * @param isClick
//	 */
//	private void setSurfaceClickable(boolean isClick) {
//		for (int i = 0; i < mSurfaceViewList.size(); i++) {
//			mSurfaceViewList.get(i).setClickable(isClick);
//			mSurfaceViewList.get(i).setFocusable(isClick);
//		}
//	}
//
//	/**
//	 * 改变摄像头的状态
//	 */
//	@Override
//	public void changeCamera(int camera) {
//		// 设备id
//		String deviceid = GlobalHolder.getInstance().getLocalUserId()
//				+ ":Camera";
//		// 摄像头状态
//		int cameraStatus = SPUtil.getConfigIntValue(activity, "local", 0);
//
//		// 获取分辨率
//		String default_video_wh = SPUtil.getConfigStrValue(getActivity(),
//				"chicun");
//		// 获取视频流量
//		int videoFlow = SPUtil.getConfigIntValue(getActivity(), "videoFlow",
//				512);
//		// 获取帧率
//		int frameRate = SPUtil.getConfigIntValue(getActivity(), "zl", 10);
//		// 获取设备方向
//		int deviceOrientation = SPUtil.getConfigIntValue(getActivity(),
//				"viewModePosition", 1);
//		PublicInfo.DEVICE_ORIENTATION = deviceOrientation;
//
//		if (default_video_wh.isEmpty()) {
//			default_video_wh = PublicInfo.Support2Level;
//		}
//		String[] whs = default_video_wh.split("X");
//		int width = Integer.parseInt(whs[0]);
//		int height = Integer.parseInt(whs[1]);
//
//		switch (camera) {
//		/**
//		 * 前置摄像头
//		 */
//		case 1:
//		case 2:
//
//			if (cameraStatus == 1 && devInfo != null
//					&& devInfo.deviceList != null
//					&& devInfo.deviceList.size() == 1) {
//				// 只有一个摄像头，不能切换
//				PublicInfo.toast(getActivity(), R.string.onecamera_xviewsdk);
//				return;
//			}
//			VideoRequest.getInstance().setVideoDevDisable(deviceid, false);
//			SPUtil.putConfigIntValue(getActivity(), "camera", 0);
//
//			LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance()
//					.getVideoConfig();
//			config.enabeleFrontCam = !config.enabeleFrontCam;
//			LocaSurfaceView.getInstance().setVideoConfig(config);
//			break;
//		/**
//		 * 有摄像头,但被禁用
//		 */
//		case 3:
//
//			VideoRequest.getInstance().setVideoDevDisable(deviceid, true);
//			SPUtil.putConfigIntValue(activity, "local", 4);
//			Intent enter2Intent = new Intent("com.cinlan.xview.conflist");
//			enter2Intent.putExtra("msgtype", MsgType.MEMBER_ENTER);
//			enter2Intent.addCategory("com.cinlan.xview.catagary");
//			activity.sendBroadcast(enter2Intent);
//			break;
//		}
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		System.out.println("videofragment stop");
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		/**
//		 * 如果销毁的时候不加这些,则再次进入会议会崩溃.
//		 */
//		for (View view : mSurfaceViewList) {
//			if (view instanceof SurfaceView) {
//				((SurfaceView) view).getHolder().getSurface().release();
//			}
//		}
//		mSurfaceViewList.clear();
//		activity = null;
//		opencache.clear();
//		mediacache.clear();
//
//		videoLargeLayout.removeAllViews();
//		videoSmallLayout.removeAllViews();
//		videoRootView.removeAllViews();
//
//		videoLargeLayout = null;
//		videoSmallLayout = null;
//		videoRootView = null;
//
//		mSurfaceViewList = null;
//		devInfo = null;
//		deviceList = null;
//		opencache = null;
//		mediacache = null;
//		app_support = null;
//		handler = null;
//
//	}
//
//	/**
//	 * 打开视频流
//	 */
//	@Override
//	public void openMedia(final MediaEntity m) {
//		if (m == null)
//			return;
//		final VideoHelper mMediaHelper = new VideoHelper(activity, m.getMediaId(), false);
//
//		SurfaceView view = mMediaHelper.getView();
//
//		mMediaHelper.setUserid(m.getOwnerID());
//
//		setPipLocation(false);
//
//		VideoRequest.getInstance().openVideoMixer(0, m.getMediaId(), mMediaHelper.getVideoPlayer(), false, m.getMixerType());
//
//		mSurfaceViewList.add(view);
//		videoLargeLayout.addView(view);
//
//		if (mSurfaceViewList.size() == 1) {
//			PublicInfo.OPENED_VIDEO_COUNT = 1;
//
//			// 设置父布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//		}
//		if (mSurfaceViewList.size() == 2) {
//			// 设置一些状态
//			PublicInfo.OPENED_VIDEO_COUNT = 2;
//
//			// 如果没有布局2就要把布局2添加进来
//			if (videoRootView.getChildCount() != 2) {
//				videoRootView.addView(videoSmallLayout);
//			}
//
//			// 设置父布局和大布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 设置小布局的位置为右上角,宽高各为屏幕1/3大小
//			setPipLocation(false);
//
//			// 从大布局取出一个surface放到小布局中
//			movePipToSmallLayout();
//
//			// 设置所有视频为不可点击
//			setSurfaceClickable(false);
//			// 设置所有布局为可点击
//			setLayoutClickable(true);
//
//		}
//		if (mSurfaceViewList.size() == 3) {
//			PublicInfo.OPENED_VIDEO_COUNT = 3;
//
//			// 设置父布局和大布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 将小布局的surface取出放到大布局中并隐藏小布局
//			movePipToBigLayout();
//
//			// 设置所有视频为可点击
//			setSurfaceClickable(true);
//			// 设置所有布局为不可点击
//			setLayoutClickable(false);
//		}
//		if (mSurfaceViewList.size() == 4) {
//			PublicInfo.OPENED_VIDEO_COUNT = 4;
//
//			// 设置父布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//
//			// 设置已有的surface可点击
//			setSurfaceClickable(true);
//
//			// 设置所有视频为可点击
//			setSurfaceClickable(true);
//			// 设置所有布局为不可点击
//			setLayoutClickable(false);
//
//			/**
//			 * 4路视频时,video_content_main2会在右上角显示,隐藏不掉.<br>
//			 * 所以只能移除掉.
//			 */
//			videoRootView.removeView(videoSmallLayout);
//		}
//		videoSmallLayout.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (!isCanSwitchTwoSurfaceView) {
//					Toast.makeText(
//							getActivity(),
//							getActivity().getResources().getString(R.string.change_camera_fast_xviewsdk),
//							Toast.LENGTH_SHORT).show();
//					return;
//				}
//
//				if (mSurfaceViewList.size() == 2) {
//					if (PublicInfo.isOpenedShareList) {
//						return;
//					}
//					SurfaceView s2 = (SurfaceView) videoSmallLayout.getChildAt(0);
//					SurfaceView s1 = (SurfaceView) videoLargeLayout.getChildAt(0);
//
//					videoSmallLayout.removeAllViews();
//					videoLargeLayout.removeAllViews();
//					videoSmallLayout.addView(s1);
//					videoLargeLayout.addView(s2);
//
//					// 设置父布局和大布局为填充屏幕
//					setLayoutMatchParent(videoRootView);
//					setLayoutMatchParent(videoLargeLayout);
//
//					isCanSwitchTwoSurfaceView = false;
//				}
//
//				new Handler().postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						isCanSwitchTwoSurfaceView = true;
//					}
//				}, 2000);
//			}
//		});
//		if (mSurfaceViewList.size() == 2) {
//			videoSmallLayout.performClick();
//		}
//		mediacache.put(m.getMediaId(), mMediaHelper);
//	}
//
//	@Override
//	public void closeMedia(MediaEntity m) {
//		String mediaId = m.getMediaId();
//		if (mediaId == null)
//			return;
//		VideoHelper mediaHelper = mediacache.get(mediaId);
//		if (mediaHelper == null)
//			return;
//
//		String mediaID = mediaId.split(":")[0];
//		int mediaid = new Integer(mediaID);
//
//		VideoRequest.getInstance().closeVideoMixer(mediaid, mediaId,
//				mediaHelper.getVideoPlayer());
//
//		SurfaceView view = mediaHelper.getView();
//		mSurfaceViewList.remove(view);
//		mediacache.remove(mediaId);
//		videoLargeLayout.removeView(view);
//		videoSmallLayout.removeView(view);
//		view.getHolder().getSurface().release();
//
//		if (mSurfaceViewList.size() == 1) {
//			PublicInfo.OPENED_VIDEO_COUNT = 1;
//
//			// 设置父布局和大布局为填充屏幕
//			setLayoutMatchParent(videoRootView);
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 如果小布局里还有视频就移到大布局里
//			if (videoSmallLayout.getChildCount() > 0) {
//				movePipToBigLayout();
//			}
//
//			// 设置小布局宽高各为屏幕1/3大小,隐藏小布局
//			setPipLocation(true);
//
//		} else if (mSurfaceViewList.size() == 2) {
//			PublicInfo.OPENED_VIDEO_COUNT = 2;
//
//			// 如果打开过4路那么现在就要把布局2添加进来
//			if (videoRootView.getChildCount() != 2) {
//				videoRootView.addView(videoSmallLayout);
//			}
//
//			// 根据横竖屏设置父布局尺寸
//			setLayoutMatchParent(videoRootView);
//
//			// 设置大布局宽高为填充父布局
//			setLayoutMatchParent(videoLargeLayout);
//
//			// 设置小布局宽高各为屏幕1/3大小,位置为右上角
//			setPipLocation(false);
//
//			// 从大布局取出一个surface放到小布局中
//			movePipToSmallLayout();
//
//			// 画中画布局里的surface不可点击
//			setSurfaceClickable(false);
//
//			// 设置大小布局可点击
//			setLayoutClickable(true);
//
//			videoSmallLayout.performClick();
//		} else if (mSurfaceViewList.size() == 3) {
//
//			PublicInfo.OPENED_VIDEO_COUNT = 3;
//
//			// 根据横竖屏设置父布局尺寸
//			setLayoutMatchParent(videoRootView);
//
//			// 画中画布局里的surface可点击
//			setSurfaceClickable(true);
//
//			// 设置大小布局不可点击
//			setLayoutClickable(false);
//
//		} else if (mSurfaceViewList.size() == 4) {
//
//			PublicInfo.OPENED_VIDEO_COUNT = 4;
//
//			// 根据横竖屏设置父布局尺寸
//			setLayoutMatchParent(videoRootView);
//
//			// 画中画布局里的surface可点击
//			setSurfaceClickable(true);
//
//			// 设置大小布局不可点击
//			setLayoutClickable(false);
//
//		} else if (mSurfaceViewList.size() == 0) {
//			PublicInfo.OPENED_VIDEO_COUNT = 0;
//		}
//	}
//
//	/**
//	 * 给视频会议的视频设置大小
//	 */
//	@Override
//	public void setVideoSize(View tag,
//			android.widget.RelativeLayout.LayoutParams params) {
//	}
//
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		return false;
//	}
//
//	/**
//	 * 应用设置的分辨率/视频流量/帧率/格式/设备方向/是否前置
//	 */
//	@Override
//	public void applySetting(int width, int height, int videoFlow,
//			int frameRate, int format, int requestedOrientation,
//			boolean enabeleFrontCam) {
//
//		LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance()
//				.getVideoConfig();
//		config.videoWidth = width;
//		config.videoHeight = height;
//		config.videoBitRate = videoFlow;
//		config.videoFrameRate = frameRate;
//		config.videoMaxKeyframeInterval = frameRate * 2;
//		config.enabeleFrontCam = enabeleFrontCam;
//		LocaSurfaceView.getInstance().setVideoConfig(config);
//	}
//}
