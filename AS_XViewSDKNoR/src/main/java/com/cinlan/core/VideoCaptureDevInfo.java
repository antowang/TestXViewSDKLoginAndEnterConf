package com.cinlan.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;

import com.cinlan.xview.utils.XviewLog;

/**
 * 视频捕获设备信息相关类
 * 包含Camera参数信息和视频捕获相关信息.
 * 
 */
public class VideoCaptureDevInfo {
	private final static String TAG = "VideoCaptureDevInfo";
	private final static String CAMERA_FACE_FRONT = "Camera Facing front";
	public final static String CAMERA_FACE_BACK = "Camera Facing back";

	private String mDefaultDevName = "";

	private CapParams mCapParams = new CapParams();

	/**
	 * 相机捕获参数类
	 */
	public class CapParams {
		public int width = 320;
		public int height = 240;
		public int bitrate = 256000;
		public int fps = 20;
		public int format = ImageFormat.NV21;
		/**
		 * 设备方向:[ (0-横), (1-竖) ]
		 */
		public int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		/**
		 * 是否用前置摄像头
		 */
		public boolean enabeleFrontCam = true;
	}

	public void SetDefaultDevName(String devName) {
		mDefaultDevName = devName;
	}

	public String GetDefaultDevName() {
		return mDefaultDevName;
	}

	/**
	 * 设置CapParams
	 * 
	 * @param 宽
	 * @param 高
	 * @param 比特率
	 *            /码率
	 * @param 帧率
	 * @param 图片格式
	 * @param 设备方向
	 * @param 是否用前置摄像头
	 */
	public void SetCapParams(int width, int height, int bitrate, int fps,
			int format, int requestedOrientation, boolean enabeleFrontCam) {
		mCapParams.width = width;
		mCapParams.height = height;
		mCapParams.bitrate = bitrate;
		mCapParams.fps = fps;
		mCapParams.format = format;
		mCapParams.requestedOrientation = requestedOrientation;
		mCapParams.enabeleFrontCam = enabeleFrontCam;

		LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance().getVideoConfig();
		config.videoWidth = width;
		config.videoHeight = height;
		config.videoBitRate = bitrate;
		config.videoFrameRate = fps;
		config.videoMaxKeyframeInterval = fps*2;
		config.enabeleFrontCam = enabeleFrontCam;
		LocaSurfaceView.getInstance().setVideoConfig(config);
	}

	public CapParams GetCapParams() {
		return mCapParams;
	}

	/**
	 * 视频捕获设备类<br>
	 * 包含所有可用的摄像头和尺寸容量
	 */
	public class VideoCaptureDevice {
		VideoCaptureDevice() {
			frontCameraType = FrontFacingCameraType.None;
			index = 0;
		}

		/**
		 * 设备名称
		 */
		public String deviceUniqueName;
		/**
		 * 前置摄像头类型
		 */
		public FrontFacingCameraType frontCameraType;
		/**
		 * 包含设备宽高的类的集合
		 */
		public LinkedList<CaptureCapability> capabilites = new LinkedList<CaptureCapability>();
		/**
		 * 包含fps范围的集合
		 */
		public List<int[]> fps_Ranges;
		/**
		 * 摄像机的设备朝向 in<br>
		 * android.hardware.Camera.CameraInfo.Orientation
		 */
		public int orientation;
		/**
		 * 相机中使用的摄像头索引.
		 */
		public int index;
		/**
		 * 获取支持的预览格式集合
		 */
		public List<Integer> previewformats;

		/**
		 * 通过编码尺寸得到最终尺寸<br>
		 * 编码过程已删.
		 * 
		 * @param width
		 * @param height
		 * @return
		 */
		public VideoSize GetSrcSizeByEncSize(int width, int height) {
			VideoSize size = new VideoSize();

			int length = capabilites.size();
			if (length <= 0) {
				return null;
			}

			int tempWidth = capabilites.get(0).width;
			int tempHeight = capabilites.get(0).height;
			size.width = tempWidth;
			size.height = tempHeight;
			return size;
		}
	}

	/**
	 * 前置摄像头类型
	 */
	public enum FrontFacingCameraType {
		None, // This is not a front facing camera
		GalaxyS, // Galaxy S front facing camera.
		HTCEvo, // HTC Evo front facing camera
		Android23, // Android 2.3 front facing camera.
	}

	public List<VideoCaptureDevice> deviceList;

	private static VideoCaptureDevInfo s_self = null;

	public static VideoCaptureDevInfo CreateVideoCaptureDevInfo() {
		if (s_self == null) {
			s_self = new VideoCaptureDevInfo();
			if (s_self.Init() != 0) {
				s_self = null;
				XviewLog.d(TAG, "Failed to create VideoCaptureDevInfo.");
			}
		}
		return s_self;
	}

	/**
	 * 根据名称获取一个设备
	 * 
	 * @param devName
	 * @return
	 */
	public VideoCaptureDevice GetDevice(String devName) {
		VideoCaptureDevice device = null;
		for (VideoCaptureDevice dev : deviceList) {
			if (dev.deviceUniqueName.equals(devName)) {
				device = dev;
				break;
			}
		}

		return device;
	}

	public VideoCaptureDevice GetCurrDevice() {
		return GetDevice(mDefaultDevName);
	}

	private VideoCaptureDevInfo() {
		deviceList = new ArrayList<VideoCaptureDevice>();
	}

	/**
	 * 在做此选项之前,请先关闭本地设备.调用此函数后,再打开本地设备.
	 */
	public boolean reverseCamera() {
		// 如果只有1个摄像头,不能做切换
		if (Camera.getNumberOfCameras() <= 1) {
			return false;
		}
		if (CAMERA_FACE_FRONT.equals(mDefaultDevName)) {
			mDefaultDevName = CAMERA_FACE_BACK;
			return true;
		} else if (CAMERA_FACE_BACK.equals(mDefaultDevName)) {
			mDefaultDevName = CAMERA_FACE_FRONT;
			return true;
		} else {
			XviewLog.e("VideoCaptureDevInfo", " Unknow camera default name:"
					+ mDefaultDevName);
			return false;
		}
	}

	private int Init() {
		// Populate the deviceList with available cameras and their
		// capabilities.
		Camera camera = null;
		try {
			// From Android 2.3 and onwards
			int camNums = 0;
			if (Camera.getNumberOfCameras() > 2) {
				camNums = 2;
			} else {
				camNums = Camera.getNumberOfCameras();
			}
			for (int i = 0; i < camNums; ++i) {

				VideoCaptureDevice newDevice = new VideoCaptureDevice();
				// Set first camera
				if (this.mDefaultDevName == null
						|| this.mDefaultDevName.isEmpty()) {
					this.mDefaultDevName = newDevice.deviceUniqueName;
				}

				Camera.CameraInfo info = new Camera.CameraInfo();

				Camera.getCameraInfo(i, info);
				// 摄像头索引
				newDevice.index = i;
				// 设备朝向
				newDevice.orientation = info.orientation;

				if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					newDevice.deviceUniqueName = CAMERA_FACE_BACK;
					newDevice.frontCameraType = FrontFacingCameraType.None;
					XviewLog.d(TAG, "Camera " + i
							+ ", Camera Facing back, Orientation "
							+ info.orientation);
				} else {
					newDevice.deviceUniqueName = CAMERA_FACE_FRONT;
					newDevice.frontCameraType = FrontFacingCameraType.Android23;
					XviewLog.d(TAG, "Camera " + i
							+ ", Camera Facing front, Orientation "
							+ info.orientation);
					/**
					 * 如果有前置摄像头,使用前置摄像头为默认.
					 */
					this.mDefaultDevName = newDevice.deviceUniqueName;
				}
				camera = Camera.open(i);
				Camera.Parameters parameters = camera.getParameters();
				AddDeviceInfo(newDevice, parameters);
				// 断开并释放相机的对象资源
				camera.release();
				camera = null;
				deviceList.add(newDevice);
			}

			// VerifyCapabilities();
		} catch (Exception ex) {
			XviewLog.e(
					TAG,
					"Failed to init VideoCaptureDeviceInfo ex"
							+ ex.getLocalizedMessage());
			return -1;
		}

		return 0;
	}

	/**
	 * 添加当前打开设备的捕获功能
	 * 
	 * @param newDevice
	 * @param parameters
	 */
	private void AddDeviceInfo(VideoCaptureDevice newDevice,
			Camera.Parameters parameters) {
		newDevice.previewformats = parameters.getSupportedPreviewFormats();

		List<Size> sizes = parameters.getSupportedPreviewSizes();
		for (Size s : sizes) {
			XviewLog.d(TAG, "VideoCaptureDeviceInfo " + "CaptureCapability:"
					+ s.width + " " + s.height);
			newDevice.capabilites.add(new CaptureCapability(s.width, s.height));
		}

		newDevice.fps_Ranges = parameters.getSupportedPreviewFpsRange();
		for (int[] range : newDevice.fps_Ranges) {
			String strRange = "range is : ";
			for (int val : range) {
				strRange += val + " ";
			}

			XviewLog.e("FpsRange", strRange);
		}

	}

}
