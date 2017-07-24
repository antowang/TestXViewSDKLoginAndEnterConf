package com.cinlan.xview.ui.callback;

import com.cinlan.xview.bean.ClientDev;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.msg.MediaEntity;

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
		 * @param userId
		 */
		void closeVideo(long userId, VideoDevice vd , boolean isExit);

		/**
		 * 打开视频设备
		 * 
		 */
		void openVideo(long userId, ClientDev clientDev ,int pos, boolean isInit);

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
