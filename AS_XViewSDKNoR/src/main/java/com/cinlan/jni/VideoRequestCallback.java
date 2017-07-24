package com.cinlan.jni;

public interface VideoRequestCallback {

	public void OnRemoteUserVideoDevice(String szXmlData);

	public void OnRemoteUpdateVideoSetting(String dev, int nDisable, int nSizeIndex,
			int fps, int bps);

}
