package com.cinlan.xview.ui.fragement;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cinlan.xview.bean.data.Beeline;
import com.cinlan.xview.bean.data.DocShare;
import com.cinlan.xview.bean.data.Ellipse;
import com.cinlan.xview.bean.data.FreedomLine;
import com.cinlan.xview.bean.data.HeightLightLine;
import com.cinlan.xview.bean.data.Label;
import com.cinlan.xview.bean.data.Page;
import com.cinlan.xview.bean.data.Rectangle;
import com.cinlan.xview.bean.data.RoundRect;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.widget.photoview.PhotoView;
import com.cinlankeji.khb.iphone.R;


public class Fragment_Doc extends Fragment {

	private FragmentActivity activity;
	private Page page;
	private PhotoView iv_doc_display;
	private TextView tv_doc_name;
	private ImageView ivBackFD;
	private String name;
	private List<Label> labels;
	private Handler handler = new Handler();
	private Bitmap mSrc;
	private Paint mPaint;
	private Bitmap newMap;
	private Canvas mCanvas;
	private int scale;

	public interface OnClickBackListener {
		public void OnBackListener(int flag);
	}

	private OnClickBackListener listener;

	public void removeListener() {
		listener = null;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnClickBackListener) activity;
	}

	public static Fragment_Doc newInstance(Page page, String name) {
		Fragment_Doc fragment = new Fragment_Doc();
		Bundle bundle = new Bundle();
		bundle.putSerializable("page", page);
		bundle.putString("name", name);
		fragment.setArguments(bundle);
		return fragment;
	}

	public int getCurrentPage() {
		if (page != null)
			return page.getIndex();
		return 0;
	}

	public String getCurrentWBid() {
		if (page != null)
			return page.getWbid();
		return "";
	}

	public void valideImage() {
		if (page != null) {
			getDataFromApp();
			if (labels != null && iv_doc_display != null) {
				// 锟侥碉拷锟斤拷刷锟斤拷
				refreshBitamp();
			}
		}
	}

	public void getDataFromApp() {
		if (page != null) {
			DocShare docShare = GlobalHolder.getInstance().findDocShare(
					page.getWbid());
			if (docShare != null) {
				Map<Integer, List<Label>> data = docShare.getData(); // 页锟斤拷锟接abels
				if (data != null)
					labels = data.get(page.getIndex());
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		page = (Page) bundle.getSerializable("page");
		name = (String) bundle.getString("name");
		mPaint = new Paint();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_doc_xviewsdk, container, false);
		iv_doc_display = (PhotoView) view.findViewById(R.id.iv_doc_display_xviewsdk);
		tv_doc_name = (TextView) view.findViewById(R.id.tv_doc_name_xviewsdk);
		tv_doc_name.setTextSize(15);
		ivBackFD = (ImageView) view.findViewById(R.id.ivBackFD_xviewsdk);
		ivBackFD.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.OnBackListener(1);
			}
		});
		new ImageAsyncTask().execute(page.getPaths());
		return view;
	}

	private int srcwidth;
	private int srcheight;

	class ImageAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String path = null;
			if (params != null && params.length > 0)
				path = params[0];
			if (path != null) {
				Options opts = new Options();
				opts.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(path, opts);
				srcwidth = opts.outWidth;
				srcheight = opts.outHeight;
				float m = 1024 * 1024;
				scale = (int) Math
						.ceil(Math.sqrt(srcwidth * srcheight * 4 / m));
				opts.inSampleSize = scale;
				opts.inJustDecodeBounds = false;
				try {
					mSrc = BitmapFactory.decodeFile(path, opts);
				} catch (OutOfMemoryError er) {
					mSrc = null;
					er.printStackTrace();
				} catch (Exception e) {
					mSrc = null;
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (mSrc != null) {
				iv_doc_display.setImageBitmap(mSrc);
				newMap = Bitmap.createBitmap(mSrc.getWidth(), mSrc.getHeight(),
						Bitmap.Config.RGB_565);
				mCanvas = new Canvas(newMap);
				mCanvas.drawColor(Color.TRANSPARENT);
				valideImage();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
	}

	@Override
	public void onResume() {
		super.onResume();
		tv_doc_name.setText(name + "");
	}

	public void refreshBitamp() {
		// 图锟斤拷锟叫∫拷锟斤拷锟斤拷锟街达拷小锟斤拷锟斤拷,锟皆猴拷锟侥憋拷锟斤拷锟饺讹拷应
		if (mCanvas != null && mSrc != null) {
			mCanvas.drawBitmap(mSrc, new Matrix(), mPaint);
			drawMyself(mPaint, mCanvas);
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				if (newMap != null)
					iv_doc_display.setImageBitmap(newMap);
			}
		});
	}

	private void drawMyself(Paint mPaint, Canvas canvas) {
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE); //
		mPaint.setDither(true); // 锟斤拷锟斤拷锟斤拷锟斤拷锟�图锟斤拷锟斤拷锟斤拷锟斤拷

		float XScale = (float) srcwidth / (float) (mSrc.getWidth());
		float Yscale = (float) srcheight / (float) (mSrc.getHeight());


		for (int a = 0; labels != null && a < labels.size(); a++) {
			Label label = labels.get(a);
			if (label != null) {
				mPaint.setStrokeWidth(label.getPen().getWidth());
				if (label instanceof FreedomLine) {
					int color = label.getPen().getColor();
					mPaint.setColor(color);
					FreedomLine fline = (FreedomLine) label;
					List<Point> points = fline.getPoints();
					Path path = new Path();
					path.moveTo(points.get(0).x / XScale, points.get(0).y
							/ Yscale);
					for (Point p : points) {
						path.lineTo(p.x / XScale, p.y / Yscale);
					}
					canvas.drawPath(path, mPaint);
				} else if (label instanceof Beeline) {
					mPaint.setColor(label.getPen().getColor());
					// System.out.println("锟斤拷始锟斤拷:Beeline");
					Beeline fline = (Beeline) label;
					canvas.drawLine(fline.getPoints().get(0).x / XScale, fline
							.getPoints().get(0).y / Yscale, fline.getPoints()
							.get(1).x / XScale, fline.getPoints().get(1).y
							/ Yscale, mPaint);

				} else if (label instanceof RoundRect) {
					mPaint.setColor(label.getPen().getColor());

					RoundRect fline = (RoundRect) label;
					// System.out.println("锟斤拷始锟斤拷:RoundRect");
					RectF r = new RectF(fline.getPoints().get(0).x / XScale,
							fline.getPoints().get(0).y / Yscale, fline
									.getPoints().get(1).x / XScale, fline
									.getPoints().get(1).y / Yscale);
					canvas.drawRoundRect(r, 15, 15, mPaint);

				} else if (label instanceof Rectangle) {
					mPaint.setColor(label.getPen().getColor());

					/*
					 * left锟斤拷锟角撅拷锟轿撅拷锟斤拷锟斤拷叩锟絏锟斤拷 top锟斤拷锟角撅拷锟轿撅拷锟斤拷锟较边碉拷Y锟斤拷
					 * right锟斤拷锟角撅拷锟轿撅拷锟斤拷锟揭边碉拷X锟斤拷
					 * bottom锟斤拷锟角撅拷锟轿撅拷锟斤拷锟铰边碉拷Y锟斤拷
					 */
					Rectangle fline = (Rectangle) label;
					RectF r = new RectF(fline.getPoints().get(0).x / XScale,
							fline.getPoints().get(0).y / Yscale, fline
									.getPoints().get(1).x / XScale, fline
									.getPoints().get(1).y / Yscale);
					canvas.drawRect(r, mPaint);

				} else if (label instanceof Ellipse) {
					mPaint.setColor(label.getPen().getColor());
					Ellipse fline = (Ellipse) label;

					RectF oval = new RectF(fline.getPoints().get(0).x / XScale,
							fline.getPoints().get(0).y / Yscale, fline
									.getPoints().get(1).x / XScale, fline
									.getPoints().get(1).y / Yscale);
					canvas.drawOval(oval, mPaint);

				} else if (label instanceof HeightLightLine) {
					HeightLightLine fline = (HeightLightLine) label;
					mPaint.setColor(fline.getPen().getColor());
					mPaint.setAlpha(50); // 锟斤拷锟斤拷a为透锟斤拷锟饺ｏ拷取值锟斤拷围为0~255锟斤拷锟斤拷值越小越透锟斤拷锟斤拷
					// System.out.println("锟斤拷始锟斤拷:HeightLightLine");

					mPaint.setStrokeWidth(fline.getPen().getWidth());
					List<Point> points = fline.getPoints();
					Path path = new Path();
					path.moveTo(points.get(0).x / XScale, points.get(0).y
							/ Yscale);
					for (Point p : points) {
						path.lineTo(p.x / XScale, p.y / Yscale);
					}
					canvas.drawPath(path, mPaint);
				}
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (mSrc != null && !mSrc.isRecycled()) {
			mSrc.recycle();
			mSrc = null;
		}
		if (newMap != null && !newMap.isRecycled()) {
			newMap.recycle();
			newMap = null;
		}

		System.gc();
	}

}
