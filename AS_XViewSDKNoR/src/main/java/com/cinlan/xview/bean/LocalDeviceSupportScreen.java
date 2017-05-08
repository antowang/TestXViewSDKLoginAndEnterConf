package com.cinlan.xview.bean;

public class LocalDeviceSupportScreen {

	private String width;
	private String height;
	private String devName;
	private String fps;

	public LocalDeviceSupportScreen() {
		super();
	}

	public LocalDeviceSupportScreen(String width, String height,
			String devName, String fps) {
		super();
		this.width = width;
		this.height = height;
		this.devName = devName;
		this.fps = fps;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getFps() {
		return fps;
	}

	public void setFps(String fps) {
		this.fps = fps;
	}

	@Override
	public String toString() {
		return "LocalDeviceSupportScreen [width=" + width + ", height="
				+ height + ", devName=" + devName + ", fps=" + fps + "]";
	}

}
