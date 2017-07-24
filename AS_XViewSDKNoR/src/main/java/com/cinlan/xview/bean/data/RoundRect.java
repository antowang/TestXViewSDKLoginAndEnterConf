package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.List;

import android.graphics.Point;

/*
 * <TRoundRectMeta ID="{7C6B92B7-1489-4FC5-BD10-3E07ADFFDB1A}" >

    <Points>
		433 115 650 279
    </Points>

    <Pen
        Align="PenAlignmentCenter"
        Color="-65536"
        DashStyle="DashStyleSolid"
        EndCap="lcdRound"
        LineJoin="LineJoinRound"
        StartCap="lcdRound"
        Width="4" />

    <Brush
        BrushType="bteSolid"
        HatchBackColor="-1"
        HatchForeColor="-16777216"
        HatchStyle="HatchStyleHorizontal"
        SolidColor="0" />

</TRoundRectMeta>
 */
public class RoundRect extends Label  implements Serializable{

	
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
		return "RoundRect [pen=" + pen + ", points=" + points + ", brush="
				+ brush + "]";
	}
	
}
