package com.cinlan.xview.utils;

import android.app.Activity;
import android.view.SurfaceView;

import com.cinlan.core.LocaSurfaceView;
import com.cinlan.core.RemotePlayerManger;
import com.cinlan.core.RemoteSurfaceView;
import com.cinlan.core.VideoPlayer;

/**
 * 视频帮助类<br>
 * 实例化本地或远端SurfaceView.<br>
 * 加载远端视频<br>
 * 
 * @author ChongXue
 * @date 2017-3-14
 */
public class VideoHelper {
	private static final int SCALE_TO_FIT = 0;
	private static final int SCALE_TO_FIT_WITH_CROPPING = 1;

	private SurfaceView view;
	private VideoPlayer videoPlayer;

	// 用户id
	private long userid;
	// 设备id
	private String szDevid;
	// 是否打开(没用)
	private boolean open = false;
	// 是否本地
	private boolean local = false;
	private long last_pts = 0;

	public VideoHelper(Activity ctx, String devId, boolean isLocal) {
		local = isLocal;
		szDevid = devId;

		if (local) {
			// 获取本地视频播放的SurfaceView
			view = LocaSurfaceView.getInstance().getSurfaceView(ctx, SCALE_TO_FIT_WITH_CROPPING);
					
		} else {
			videoPlayer = new VideoPlayer();
			videoPlayer.setVideoHeler(this);

			// 获取远端视频播放的SurfaceView
			view = RemotePlayerManger.getInstance().getRemoteSurfaceView(ctx, szDevid,  SCALE_TO_FIT);
		}
	}

	/**
	 * 播放视频实际入口
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @param frameType
	 */
	public void playVideo(byte[] data, int width, int height, int frameType) {
		if (local) {
			return;
		}

		RemoteSurfaceView rView = (RemoteSurfaceView) view;

		if (rView != null) {
		
				long cur_pts = System.nanoTime() / 1000;
				if (last_pts == 0) {
					last_pts = cur_pts;
				} else {
					if (cur_pts - last_pts < 20) {
						last_pts += 20;
					} else {
						last_pts = cur_pts;
					}
				}
				// 远端视频添加数据
				rView.AddVideoData(data, last_pts, width, height, frameType);
			
		}
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getSzDevid() {
		return szDevid;
	}

	public SurfaceView getView() {
		return view;
	}

	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}
}
