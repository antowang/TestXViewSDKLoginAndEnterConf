package com.cinlan.xview.bean.data;

import java.io.Serializable;

public class Brush implements Serializable{

	private String BreushType;
	private int HatchBackColor;
	private int HatchForeColor;
	private String HatchStyle;
	private int SolidColor;
	public String getBreushType() {
		return BreushType;
	}
	public void setBreushType(String breushType) {
		BreushType = breushType;
	}
	public int getHatchBackColor() {
		return HatchBackColor;
	}
	public void setHatchBackColor(int hatchBackColor) {
		HatchBackColor = hatchBackColor;
	}
	public int getHatchForeColor() {
		return HatchForeColor;
	}
	public void setHatchForeColor(int hatchForeColor) {
		HatchForeColor = hatchForeColor;
	}
	public String getHatchStyle() {
		return HatchStyle;
	}
	public void setHatchStyle(String hatchStyle) {
		HatchStyle = hatchStyle;
	}
	public int getSolidColor() {
		return SolidColor;
	}
	public void setSolidColor(int solidColor) {
		SolidColor = solidColor;
	}
	@Override
	public String toString() {
		return "Brush [BreushType=" + BreushType + ", HatchBackColor="
				+ HatchBackColor + ", HatchForeColor=" + HatchForeColor
				+ ", HatchStyle=" + HatchStyle + ", SolidColor=" + SolidColor
				+ "]";
	}
	
	
	
}
