package com.cinlan.xview.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.cinlan.xview.bean.data.Beeline;
import com.cinlan.xview.bean.data.Ellipse;
import com.cinlan.xview.bean.data.FreedomLine;
import com.cinlan.xview.bean.data.HeightLightLine;
import com.cinlan.xview.bean.data.Label;
import com.cinlan.xview.bean.data.Rectangle;
import com.cinlan.xview.bean.data.RoundRect;

public class DOCImageView extends ImageView {

	private List<Label> labels;
	private Paint mPaint;

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public DOCImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DOCImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DOCImageView(Context context) {
		super(context);
		init();
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	private void init() {
		labels = new ArrayList<Label>();
		mPaint = new Paint();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE); //
		mPaint.setDither(true); // ���������?ͼ��������

		// ���
		float xScale = 800;
		float yScale = 600;

		for (int a = 0; labels != null && a < labels.size(); a++) {
			Label label = labels.get(a);
			// System.out.println("���Ķ�����:"+label);
			if (label != null) {
				mPaint.setStrokeWidth(label.getPen().getWidth());
				if (label instanceof FreedomLine) {

					int color = label.getPen().getColor();
					if (color == -16777216) {
						// System.out.println("��ɫֵ��:"+color);
						// mPaint.setColor(Color.parseColor("#FFFFFFFF"));
						// mPaint.setAlpha(0);
						// mPaint.setStrokeWidth(label.getPen().getWidth()*xScale);
					} else {
						mPaint.setColor(color);
					}

					// System.out.println("��ʼ��:FreedomLine");
					FreedomLine fline = (FreedomLine) label;
					List<Point> points = fline.getPoints();
					Path path = new Path();
					path.moveTo(points.get(0).x, points.get(0).y);
					for (Point p : points) {
						path.lineTo(p.x, p.y);
					}
					canvas.drawPath(path, mPaint);

				} else if (label instanceof Beeline) {
					mPaint.setColor(label.getPen().getColor());
					// System.out.println("��ʼ��:Beeline");
					Beeline fline = (Beeline) label;
					canvas.drawLine(fline.getPoints().get(0).x, fline
							.getPoints().get(0).y, fline.getPoints().get(1).x,
							fline.getPoints().get(1).y, mPaint);

				} else if (label instanceof RoundRect) {
					mPaint.setColor(label.getPen().getColor());

					RoundRect fline = (RoundRect) label;
					// System.out.println("��ʼ��:RoundRect");
					RectF r = new RectF(fline.getPoints().get(0).x, fline
							.getPoints().get(0).y, fline.getPoints().get(1).x,
							fline.getPoints().get(1).y);
					canvas.drawRoundRect(r, 15, 15, mPaint);

				} else if (label instanceof Rectangle) {
					mPaint.setColor(label.getPen().getColor());

					/*
					 * left���Ǿ��ξ�����ߵ�X�� top���Ǿ��ξ����ϱߵ�Y��
					 * right���Ǿ��ξ����ұߵ�X�� bottom���Ǿ��ξ����±ߵ�Y��
					 */
					Rectangle fline = (Rectangle) label;
					RectF r = new RectF(fline.getPoints().get(0).x, fline
							.getPoints().get(0).y, fline.getPoints().get(1).x,
							fline.getPoints().get(1).y);
					canvas.drawRect(r, mPaint);

				} else if (label instanceof Ellipse) {
					mPaint.setColor(label.getPen().getColor());
					Ellipse fline = (Ellipse) label;

					RectF oval = new RectF(fline.getPoints().get(0).x, fline
							.getPoints().get(0).y, fline.getPoints().get(1).x,
							fline.getPoints().get(1).y);
					canvas.drawOval(oval, mPaint);

				} else if (label instanceof HeightLightLine) {
					HeightLightLine fline = (HeightLightLine) label;
					mPaint.setColor(fline.getPen().getColor());
					mPaint.setAlpha(50); // ����aΪ͸���ȣ�ȡֵ��ΧΪ0~255����ֵԽСԽ͸����
					// System.out.println("��ʼ��:HeightLightLine");

					mPaint.setStrokeWidth(fline.getPen().getWidth());
					List<Point> points = fline.getPoints();
					Path path = new Path();
					path.moveTo(points.get(0).x, points.get(0).y);
					for (Point p : points) {
						path.lineTo(p.x, p.y);
					}
					canvas.drawPath(path, mPaint);

				}
			}

		}
	}

}
