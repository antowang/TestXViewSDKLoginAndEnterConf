package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.List;

import android.graphics.Point;

public class Rectangle extends Label implements Serializable{

	private Pen pen;
	private List<Point> points;
	private Brush brush;
	
	
	
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



	public Brush getBrush() {
		return brush;
	}



	public void setBrush(Brush brush) {
		this.brush = brush;
	}



	@Override
	public String toString() {
		return "Rectangle [pen=" + pen + ", points=" + points + ", brush="
				+ brush + "]";
	}
	
	
	
}
