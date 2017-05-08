package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.List;

import android.graphics.Point;

/*
 * 
 * <TBeelineMeta ID="{A7A76B35-B7C8-4C89-9AB9-EDB5BB407335}" >

    <Points>
		66 24 416 114
    </Points>

    <Pen
        Align="PenAlignmentCenter"
        Color="-65536"
        DashStyle="DashStyleSolid"
        EndCap="lcdRound"
        LineJoin="LineJoinRound"
        StartCap="lcdRound"
        Width="4" />

</TBeelineMeta>
 */
public class Beeline extends Label implements Serializable{

	private List<Point> points;
	private Pen pen;
	public List<Point> getPoints() {
		return points;
	}
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	public Pen getPen() {
		return pen;
	}
	public void setPen(Pen pen) {
		this.pen = pen;
	}
	@Override
	public String toString() {
		return "Beeline [pen=" + pen + "]";
	}
	
	
	
}
