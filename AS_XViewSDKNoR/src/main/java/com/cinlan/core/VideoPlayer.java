package com.cinlan.core;

import com.cinlan.xview.utils.VideoHelper;

import java.io.FileOutputStream;

public class VideoPlayer {

	private VideoHelper mVideoHelper;

	private FileOutputStream file;

	public void setVideoHeler(VideoHelper videoHeler) {
		mVideoHelper = videoHeler;
	}

	/*
	 * Called by native
	 */
	private void OnPlayVideo(byte[] data, int width, int height, int frameType) {
		if (mVideoHelper != null)
			mVideoHelper.playVideo(data, width, height, frameType);

		try {
			if (file == null) {
				file = new FileOutputStream("/mnt/sdcard/video.h264");
			}

			if (file != null) {
				file.write(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
