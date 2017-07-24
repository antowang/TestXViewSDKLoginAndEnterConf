package com.cinlan.core;

/**
 * 只用于包含宽高的类<br>
 * 比较的宽高.
 * 
 */
public class CaptureCapability implements Comparable<CaptureCapability> {
	public CaptureCapability(int w, int h) {
		width = w;
		height = h;
	}

	public int width = 0;
	public int height = 0;

	public int compareTo(CaptureCapability obj) {
		return new Integer(this.width * this.height).compareTo(obj.width
				* obj.height);
	}
}
