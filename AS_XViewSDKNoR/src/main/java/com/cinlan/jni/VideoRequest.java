package com.cinlan.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

import com.cinlan.core.VideoPlayer;
import com.cinlan.xview.utils.XviewLog;

public class VideoRequest {
	private static VideoRequest mVideoRequest;
	private Activity context;

	private List<WeakReference<VideoRequestCallback>> callbacks;

	public void addCallback(VideoRequestCallback callback) {
		this.callbacks.add(new WeakReference<VideoRequestCallback>(callback));
	}

	private VideoRequest() {
		callbacks = new ArrayList<WeakReference<VideoRequestCallback>>();
	};

	public static synchronized VideoRequest getInstance() {
		if (mVideoRequest == null) {
			mVideoRequest = new VideoRequest();
		}

		return mVideoRequest;
	}

	public native boolean initialize(VideoRequest request);

	public native void unInitialize();

	public native void enumMyVideos(int p);

	public native void setVideoDevDisable(String deviceid, boolean disable);

	public native void setDefaultVideoDev(String szDeviceID);

	public native void inviteVideoChat(long nGroupID, long nToUserID,
			String szDeviceID, int businessType);

	public native void acceptVideoChat(long nGroupID, long nToUserID,
			String szDeviceID, int businessType);

	public native void refuseVideoChat(long nGroupID, long nToUserID,
			String szDeviceID, int businessType);

	public native void cancelVideoChat(long nGroupID, long nToUserID,
			String szDeviceID, int businessType);

	public native void closeVideoChat(long nGroupID, long nToUserID,
			String szDeviceID, int businessType);

	public native void openVideoDevice(long nGroupID, long nUserID,
			String szDeviceID, VideoPlayer vp, int businessType);

	public native void closeVideoDevice(long nGroupID, long nUserID,
			String szDeviceID, VideoPlayer vp, int businessType);

	public native void setCapParam(String szDevID, int nSizeIndex,
			int nFrameRate, int nBitRate);

	public native void openVideoMixer(long nUserID, String szMediaID,
			VideoPlayer vp, boolean i, int j);

	public native void closeVideoMixer(long nUserID, String szMediaID,
			VideoPlayer vp);

	private void OnRemoteUserVideoDevice(String szXmlData) {
		XviewLog.e("ImRequest UI", "OnRemoteUserVideoDevice:---" + szXmlData);
		for (WeakReference<VideoRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				VideoRequestCallback callback = (VideoRequestCallback) obj;
				callback.OnRemoteUserVideoDevice(szXmlData);
			}
		}
	}

	/**
	 * OnRemoteUpdateVideoSetting 4611686018427388018:Camera, 0, 7, 15, 128
	 * 
	 * @param dev
	 *            设备名称
	 * @param nDisable
	 *            0表示禁用,1表示不禁用
	 * @param nSizeIndex
	 *            7
	 * @param fps
	 * @param bps
	 */
	private void OnRemoteUpdateVideoSetting(String dev, int nDisable,
			int nSizeIndex, int fps, int bps) {
		XviewLog.e("ImRequest UI", "OnRemoteUpdateVideoSetting " + dev + ", "
				+ nDisable + ", " + nSizeIndex + ", " + fps + ", " + bps);
		for (WeakReference<VideoRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				VideoRequestCallback callback = (VideoRequestCallback) obj;
				callback.OnRemoteUpdateVideoSetting(dev, nDisable, nSizeIndex,
						fps, bps);
			}
		}
	}

	private void OnVideoChatInvite(long nGroupID, int nBusinessType,
			long nFromUserID, String szDeviceID) {
		XviewLog.e("ImRequest UI", "OnVideoChatInvite " + nGroupID + " "
				+ nBusinessType + " " + nFromUserID + " " + szDeviceID);
	}

	private void OnVideoChatAccepted(long nGroupID, int nBusinessType,
			long nFromuserID, String szDeviceID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnVideoChatAccepted " + nGroupID + " "
				+ nBusinessType + " " + nFromuserID + " " + szDeviceID);
	}

	private void OnVideoChatRefused(long nGroupID, int nBusinessType,
			long nFromUserID, String szDeviceID) {
	}

	private void OnVideoChating(long nGroupID, int nBusinessType,
			long nFromUserID, String szDeviceID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnVideoChating " + nGroupID + " "
				+ nBusinessType + " " + nFromUserID + " " + szDeviceID);
	}

	private void OnVideoChatClosed(long nGroupID, int nBusinessType,
			long nFromUserID, String szDeviceID) {
		XviewLog.e("ImRequest UI", +nGroupID + " " + nBusinessType + " "
				+ nFromUserID + " " + szDeviceID);
	}

	private void OnVideoWindowSet(String sDevId, Object hwnd) {
		XviewLog.e("ImRequest UI", " OnVideoWindowSet " + sDevId + " " + hwnd);
	}

	private void OnVideoWindowClosed(String sDevId, Object hwnd) {
		XviewLog.e("ImRequest UI", " OnVideoWindowClosed " + sDevId + " "
				+ hwnd);
	}

	private void OnSetCapParamDone(String szDevID, int nSizeIndex,
			int nFrameRate, int nBitRate) {
		XviewLog.e("ImRequest UI", " OnSetCapParamDone " + szDevID + " "
				+ nSizeIndex + " " + nFrameRate + " " + nBitRate);
	}

	private void OnVideoBitRate(Object hwnd, int bps) {
		XviewLog.e("ImRequest UI", " OnVideoBitRate " + bps + " " + hwnd);
	}

	private void OnVideoCaptureError(String szDevID, int nErr) {
		XviewLog.e("ImRequest UI", " OnVideoCaptureError " + szDevID + " "
				+ nErr);
	}

	private void OnVideoPlayerClosed(String szDeviceID) {
		XviewLog.e("ImRequest UI", " OnVideoPlayerClosed " + szDeviceID);
	}

}
