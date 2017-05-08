package com.cinlan.xview.ui.fragement;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cinlan.xview.bean.data.DocShare;
import com.cinlan.xview.bean.data.Label;
import com.cinlan.xview.bean.data.Page;
import com.cinlan.xview.ui.fragement.Fragment_Doc.OnClickBackListener;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.Utils;
import com.cinlan.xview.widget.WBImageView;
import com.cinlankeji.khb.iphone.R;


public class Fragment_wb extends Fragment {

	private FragmentActivity activity;
	private Page page;
	private WBImageView iv_wb_display;
	private TextView tv_wb_name;
	private ImageView ivBackFB;
	private String name;
	private List<Label> labels;

	private Handler handler = new Handler();
	private Paint mPaint;
	private Canvas mCanvas;

	public static Fragment_wb newInstance(Page page, String name) {
		Fragment_wb fragment = new Fragment_wb();
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

	public void getDataFromApp() {
		if (page != null) {
			DocShare docShare = GlobalHolder.getInstance().findDocShare(
					page.getWbid());
			if (docShare != null) {
				Map<Integer, List<Label>> data = docShare.getData(); // ҳ���ӦLabels
				if (data != null) {
					labels = data.get(page.getIndex());
					iv_wb_display.setLabels(labels);
				}
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

	public interface OnClickBackListener2 {
		public void OnBackListener2(int flag);
	}

	private OnClickBackListener2 listener;
	public void removeListener() {
		listener = null;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		listener = (OnClickBackListener2) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wb_xviewsdk, container, false);
		iv_wb_display = (WBImageView) view.findViewById(R.id.iv_wb_display_xviewsdk);
		tv_wb_name = (TextView) view.findViewById(R.id.tv_wb_name_xviewsdk);
		ivBackFB = (ImageView) view.findViewById(R.id.ivBackFB_xviewsdk);
		ivBackFB.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.OnBackListener2(2);
			}
		});
		mCanvas = new Canvas();
		mCanvas.drawColor(Color.TRANSPARENT);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int heightPixels = metrics.heightPixels;
		int widthPixels = metrics.widthPixels;
		System.out.println(widthPixels + ":::" + heightPixels);
		GlobalHolder.getInstance().setMetrics(widthPixels, heightPixels);
		iv_wb_display.setCwidth(widthPixels);
		iv_wb_display.setCheight(heightPixels - Utils.dip2px(activity, 60));
		tv_wb_name.setText(name);
	}

	@Override
	public void onResume() {
		super.onResume();
		valideImage();
	}

	public void valideImage() {
		if (page != null) {
			getDataFromApp();
			if (labels != null && iv_wb_display != null) {
				// �ĵ���ˢ��
				iv_wb_display.postInvalidate();
			}
		}
	}

}
