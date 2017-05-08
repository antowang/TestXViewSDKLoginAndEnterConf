package com.cinlan.xview.bean;

/**
 * PC远程设置手机端的远程设置参数的实体类.
 * 
 * @author ChongXue
 * @date 2017-3-17
 */
public class RemoteSettingBean {

	// 设备名称
	private String dev;
	// 是否禁用,0表示禁用,1表示可用
	private int nDisable;
	// 本地支持的尺寸列表索引[从高到底排序]
	private int nSizeIndex;
	// 帧率
	private int fps;
	// 码率
	private int bps;

	public RemoteSettingBean() {
		super();
	}

	public RemoteSettingBean(String dev, int nDisable, int nSizeIndex, int fps,
			int bps) {
		super();
		this.dev = dev;
		this.nDisable = nDisable;
		this.nSizeIndex = nSizeIndex;
		this.fps = fps;
		this.bps = bps;
	}

	public String getDev() {
		return dev;
	}

	public void setDev(String dev) {
		this.dev = dev;
	}

	public int getnDisable() {
		return nDisable;
	}

	public void setnDisable(int nDisable) {
		this.nDisable = nDisable;
	}

	public int getnSizeIndex() {
		return nSizeIndex;
	}

	public void setnSizeIndex(int nSizeIndex) {
		this.nSizeIndex = nSizeIndex;
	}

	public int getFps() {
		return fps;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	public int getBps() {
		return bps;
	}

	public void setBps(int bps) {
		this.bps = bps;
	}

	@Override
	public String toString() {
		return "RemoteSettingBean [dev=" + dev + ", nDisable=" + nDisable
				+ ", nSizeIndex=" + nSizeIndex + ", fps=" + fps + ", bps="
				+ bps + "]";
	}

}
