package com.cinlan.xview.bean;

import java.io.Serializable;

public class Size implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int h;
	private int w;
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	@Override
	public String toString() {
		return "Size [h=" + h + ", w=" + w + "]";
	}
	
	
}
