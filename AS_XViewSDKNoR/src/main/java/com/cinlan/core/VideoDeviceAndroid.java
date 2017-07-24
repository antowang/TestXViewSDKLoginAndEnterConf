package com.cinlan.core;

import java.util.ArrayList;

import com.cinlan.core.VideoCaptureDevInfo.VideoCaptureDevice;
import com.cinlan.xview.PublicInfo;

public class VideoDeviceAndroid {

	private VideoCaptureDevInfo mCapDevInfo = VideoCaptureDevInfo
			.CreateVideoCaptureDevInfo();

	private String GetVideoDevInfo() {
		if (mCapDevInfo == null) {
			mCapDevInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
		}

		if (mCapDevInfo == null) {
			return "";
		}

		if (mCapDevInfo.deviceList == null) {
			mCapDevInfo.deviceList = new ArrayList<VideoCaptureDevice>();
		}
		int devNum = mCapDevInfo.deviceList.size();

		StringBuilder sb = new StringBuilder();
		sb.append("<devicelist>");
		for (int i = 0; i < devNum; i++) {
			VideoCaptureDevice dev = mCapDevInfo.deviceList.get(i);

			int frameSize = dev.fps_Ranges.size();
			sb.append("<device devName='");
			sb.append(dev.deviceUniqueName);
			sb.append("' fps='");

			if (frameSize > 0 && dev.fps_Ranges.get(frameSize - 1) != null) {
				sb.append(dev.fps_Ranges.get(frameSize - 1)[0] / 1000);
			} else {
				sb.append(15);
			}

			sb.append("'>");

			for (CaptureCapability cap : dev.capabilites) {
				sb.append("<size width='");
				sb.append(cap.width);
				sb.append("' height='");
				sb.append(cap.height);
				sb.append("'>");
				sb.append("</size>");
			}

			sb.append("</device>");
		}

		sb.append("</devicelist>");
		PublicInfo.VideoDevInfo = sb.toString();
		return sb.toString();
	}
}
