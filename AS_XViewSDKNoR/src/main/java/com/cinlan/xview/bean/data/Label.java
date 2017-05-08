package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.List;

import android.graphics.Point;

public abstract class Label implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pen pen;
	private List<Point> points;
	private String pageid;
	private Brush brush;
	
	
	
	public Brush getBrush() {
		return brush;
	}

	public void setBrush(Brush brush) {
		this.brush = brush;
	}



	public String getPageid() {
		return pageid;
	}



	public void setPageid(String pageid) {
		this.pageid = pageid;
	}



	public Pen getPen() {
		return pen;
	}



	public void setPen(Pen pen) {
		this.pen = pen;
	}



	public List<Point> getPoints() {
		return points;
	}



	public void setPoints(List<Point> points) {
		this.points = points;
	}



	@Override
	public String toString() {
		return "Label [pen=" + pen + ", points=" + points + ", pageid="
				+ pageid + ", brush=" + brush + "]";
	}







	
}
