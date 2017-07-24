package com.cinlan.xview.bean.data;

import java.io.Serializable;

/*
 * <Pen
        Align="PenAlignmentCenter"
        Color="-65536"
        DashStyle="DashStyleSolid"
        EndCap="lcdRound"
        LineJoin="LineJoinMiter"
        StartCap="lcdRound"
        Width="4" />
 */
public class Pen implements Serializable{

	private String Align;
	private int Color;
	private String DashStyle;
	private String EndCap;
	private String LineJoin;
	private String StartCap;
	private int Width;
	public String getAlign() {
		return Align;
	}
	public void setAlign(String align) {
		Align = align;
	}
	public int getColor() {
		return Color;
	}
	public void setColor(int color) {
		Color = color;
	}
	public String getDashStyle() {
		return DashStyle;
	}
	public void setDashStyle(String dashStyle) {
		DashStyle = dashStyle;
	}
	public String getEndCap() {
		return EndCap;
	}
	public void setEndCap(String endCap) {
		EndCap = endCap;
	}
	public String getLineJoin() {
		return LineJoin;
	}
	public void setLineJoin(String lineJoin) {
		LineJoin = lineJoin;
	}
	public String getStartCap() {
		return StartCap;
	}
	public void setStartCap(String startCap) {
		StartCap = startCap;
	}
	public int getWidth() {
		return Width;
	}
	public void setWidth(int width) {
		Width = width;
	}
	@Override
	public String toString() {
		return "Pen [Align=" + Align + ", Color=" + Color + ", DashStyle="
				+ DashStyle + ", EndCap=" + EndCap + ", LineJoin=" + LineJoin
				+ ", StartCap=" + StartCap + ", Width=" + Width + "]";
	}
	
	
	
}
