package com.cinlan.core;

import android.view.SurfaceHolder;

public class VideoRecorder {
	@SuppressWarnings("unused")
	private int StartRecordVideo() {
		return 0;
	}

	@SuppressWarnings("unused")
	private int StopRecordVideo() {
		return 0;
	}

	@SuppressWarnings("unused")
	private int StartSend() {
		LocaSurfaceView.getInstance().setBmEncode(true);
		return 0;
	}

	@SuppressWarnings("unused")
	private int StopSend() {
		LocaSurfaceView.getInstance().setBmEncode(false);
		return 0;
	}
}
