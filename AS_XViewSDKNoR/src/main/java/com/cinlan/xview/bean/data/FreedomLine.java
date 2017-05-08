package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.List;

import android.graphics.Point;

/*
 * <TFreedomLineMeta ID="{84BD91A8-4872-493B-A177-AAA33588FB7E}" >

    <Points>
		205 99 205 99 205 100 205 105 207 111 208 116 208 122 208 129 207 136 204 144 202 154 195 162 188 170 177 179 164 190 153 196 140 202 122 206 107 206 91 206 78 206 67 204 57 199 48 192 38 184 29 174 24 164 19 152
    <Pen
        Align="PenAlignmentCenter"
        Color="-65536"
        DashStyle="DashStyleSolid"
        EndCap="lcdRound"
        LineJoin="LineJoinRound"
        StartCap="lcdRound"
        Width="4" />

</TFreedomLineMeta>
 * 
 */
public class FreedomLine extends Label implements Serializable{

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
		return "FreedomLine [points=" + points + ", pen=" + pen + "]";
	}
	
	
	
}
