package com.cinlan.xview.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.cinlan.xview.bean.data.Beeline;
import com.cinlan.xview.bean.data.Ellipse;
import com.cinlan.xview.bean.data.FreedomLine;
import com.cinlan.xview.bean.data.HeightLightLine;
import com.cinlan.xview.bean.data.Label;
import com.cinlan.xview.bean.data.Leaser;
import com.cinlan.xview.bean.data.Rectangle;
import com.cinlan.xview.bean.data.RoundRect;


public class WBImageView extends ImageView {

	private List<Label> labels;
	private Paint mPaint;
	
	private int Cwidth;
	private int Cheight;

	public int getCwidth() {
		return Cwidth;
	}

	public void setCwidth(int cwidth) {
		Cwidth = cwidth;
	}

	public int getCheight() {
		return Cheight;
	}

	public void setCheight(int cheight) {
		Cheight = cheight;
	}

	public Paint getmPaint() {
		return mPaint; 
	}
	
	private Canvas mCanvas;

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public WBImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public WBImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WBImageView(Context context) {
		super(context);
		init();
	}

	
	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	private void init(){
		labels=new ArrayList<Label>();
		mPaint=new Paint();
	}
	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mCanvas=canvas;
		
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE); //
		mPaint.setDither(true);         //���������?ͼ��������
		
		float xtempScale=(float)(Cheight)/600;
		float ytempScale=(float)(Cwidth)/800;
		
		float xScale=(xtempScale<=ytempScale?xtempScale:ytempScale);
		//���
		float yScale=xScale;
		float middle=(Cwidth-800*xScale)/2;
		System.out.println("middle ::"+middle);
		
		for (int a=0; labels!=null&&a<labels.size(); a++) {
			Label label=labels.get(a);
//			System.out.println("���Ķ�����:"+label);
			mPaint.setStrokeWidth(label.getPen().getWidth());
			if(label instanceof FreedomLine){
				
				int color = label.getPen(). getColor();
				if(color==-16777216){
					mPaint.setColor(Color.parseColor("#FFFFFF"));
					mPaint.setStrokeWidth(label.getPen().getWidth()*xScale);
				}else{
					mPaint.setColor(color);
				}
//				System.out.println("��ʼ��:FreedomLine");
				FreedomLine fline=(FreedomLine)label;
				List<Point> points=fline.getPoints();
				Path path = new Path();	
    			path.moveTo(points.get(0).x*xScale ,points.get(0).y*yScale);
    			for(int i=0; i<points.size();i++)
    			{
    				path.lineTo(points.get(i).x*xScale , points.get(i).y*yScale);
    			}
    			canvas.drawPath(path, mPaint);
				
			}else if(label instanceof Leaser){
				
				int color = label.getPen(). getColor();
				if(color==-16777216){
					mPaint.setColor(Color.parseColor("#FFFFFF"));
					mPaint.setStrokeWidth(label.getPen().getWidth()*xScale);
				}else{
					mPaint.setColor(color);
				}
//				System.out.println("��ʼ��:FreedomLine");
				Leaser fline=(Leaser)label;
				List<Point> points=fline.getPoints();
				Path path = new Path();	
    			path.moveTo(points.get(0).x*xScale+middle ,points.get(0).y*yScale);
    			for(int i=0; i<points.size();i++)
    			{
    				path.lineTo(points.get(i).x*xScale+middle , points.get(i).y*yScale);
    			}
    			canvas.drawPath(path, mPaint);
				
			}else if(label instanceof Beeline){
				mPaint.setColor(label.getPen().getColor());
				Beeline fline=(Beeline)label;
				canvas.drawLine(fline.getPoints().get(0).x*xScale, fline.getPoints().get(0).y*yScale, fline.getPoints().get(1).x*xScale, fline.getPoints().get(1).y*yScale, mPaint);
				
			}else if(label instanceof RoundRect){
				mPaint.setColor(label.getPen().getColor());
				
				RoundRect fline=(RoundRect)label;
				RectF r=new RectF(fline.getPoints().get(0).x*xScale, fline.getPoints().get(0).y*yScale, fline.getPoints().get(1).x*xScale, fline.getPoints().get(1).y*yScale);
				canvas.drawRoundRect(r, 15, 15, mPaint);
				
			}else if(label instanceof Rectangle){
				mPaint.setColor(label.getPen().getColor());
				
				/*
				 * left���Ǿ��ξ�����ߵ�X��
					top���Ǿ��ξ����ϱߵ�Y��
					right���Ǿ��ξ����ұߵ�X��
					bottom���Ǿ��ξ����±ߵ�Y��
				 */
				Rectangle fline=(Rectangle)label;
				RectF r=new RectF(fline.getPoints().get(0).x*xScale, fline.getPoints().get(0).y*yScale, fline.getPoints().get(1).x*xScale, fline.getPoints().get(1).y*yScale);
				canvas.drawRect(r, mPaint);
				
			}else if(label instanceof Ellipse){
				mPaint.setColor(label.getPen().getColor());
				Ellipse fline=(Ellipse)label;
				
				RectF oval=new RectF(fline.getPoints().get(0).x*xScale, fline.getPoints().get(0).y*yScale, fline.getPoints().get(1).x*xScale, fline.getPoints().get(1).y*yScale);
				canvas.drawOval(oval, mPaint);
				
			}else if(label instanceof HeightLightLine){
				HeightLightLine fline=(HeightLightLine)label;     
				mPaint.setColor(fline.getPen().getColor());
				mPaint.setAlpha(50);       //����aΪ͸���ȣ�ȡֵ��ΧΪ0~255����ֵԽСԽ͸����
				
				mPaint.setStrokeWidth(fline.getPen().getWidth());
				List<Point> points=fline.getPoints();
				Path path = new Path();	
    			path.moveTo(points.get(0).x*xScale+middle ,points.get(0).y*yScale);
    			int  length=points.size();
    			for(int i=0;i<length;i++){
    				path.lineTo(points.get(i).x*xScale+middle , points.get(i).y*yScale);
    				
    			}
    			canvas.drawPath(path, mPaint);
			}
		}
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;	
		default:
			break;
		}
		return super.onTouchEvent(event);
	}
	
}
