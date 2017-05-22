package com.cinlan.xview.widget;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.ui.ConfActivity;
import com.cinlan.xview.utils.SPUtil;

@SuppressLint("WrongCall")
public class ColumnLayout extends RelativeLayout implements OnClickListener {

	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	/**
	 * 屏幕高度
	 */
	private int screenHeight;
	private int left, top, right, bottom;
	/**
	 * 占领屏幕范围最大的View
	 */
	private View bigView;
	/**
	 * 设备方向
	 */
	private int orientation = 1;
	/**
	 * 保存所有的SurfaceView
	 */
	private Map<Integer, View> views = new HashMap<Integer, View>();

	private Handler handler = new Handler() {
		@SuppressLint("WrongCall")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			/**
			 * 根据打开文档共享所以改变布局
			 */
			case PublicInfo.OPEN_SHARE_TO_COLUMNLAYOUT:
				// 如果时4路视频那么不受影响
				if (getChildCount() != 4) {
					/**
					 * 因为打开文件柜所以重新更改View的测量和布局
					 */
					changeViewMeasureForShare();
					changeViewLayoutForShare();
				}
				break;
			/**
			 * 根据关闭文档共享所以改变布局
			 */
			case PublicInfo.CLOSE_SHARE_TO_COLUMNLAYOUT:
				if (getChildCount() != 4) {
					/**
					 * 关闭文件柜,就恢复原来的样子
					 */
					onMeasure(screenWidth, screenHeight);
					onLayout(true, 0, 0, screenWidth, screenHeight);
				}
				break;
			default:
				break;
			}
		};
	};

	public ColumnLayout(Context context) {
		super(context);
		PublicInfo.columnLayoutHandler = handler;
	}

	public ColumnLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		PublicInfo.columnLayoutHandler = handler;
	}

	public ColumnLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		PublicInfo.columnLayoutHandler = handler;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// 首先获取设备方向
		orientation = SPUtil.getConfigIntValue(getContext(),
				"viewModePosition", 1);

		int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
		int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

		// 然后根据测量宽高判断是竖屏还是横屏
		if (measureWidth > measureHeight) {
			orientation = 0;
		}

		screenWidth = measureWidth;
		screenHeight = measureHeight;

		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);

			LayoutParams params = (LayoutParams) childView.getLayoutParams();

			switch (childCount) {
			/**
			 * 如果只有1路视频肯定是全屏.
			 */
			case 1:
				params.height = screenHeight;
				params.width = screenWidth;
				break;
			/**
			 * 这里没有2路,因为2路是分两个ViewGroup存储.<br>
			 */
			case 3:
			case 4:
				// 第1路要占2/3屏幕范围
				if (i == 0) {
					if (orientation == 0) {
						// 横屏是宽占2/3,高满屏
						params.width = screenWidth / 3 * 2;
						params.height = screenHeight;
					} else {
						// 竖屏是高占2/3,宽满屏
						params.width = screenWidth;
						params.height = screenHeight / 3 * 2;
					}
				} else {
					// 其余视频的宽高都是1/3
					params.width = screenWidth / 3;
					params.height = screenHeight / 3;
				}
				break;
			default:
				break;
			}

			// 已无效
			// if (msetonSizeChangeListener != null) {
			// msetonSizeChangeListener.setVideoSize(childView, params);
			// }
		}

		// 3个或3个以上的视频时才能给SurfaceView添加点击事件
		if (getChildCount() >= 3) {
			for (int i = 0; i < getChildCount(); i++) {
				getChildAt(i).setOnClickListener(this);
			}
		}

		measureChildren(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(measureWidth, measureHeight);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		left = l;
		top = t;
		right = r;
		bottom = b;

		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);

			switch (childCount) {
			/**
			 * 如果只有1路直接满屏显示
			 */
			case 1:
				bigView = childView;
				childView.layout(l, t, r, b);
				break;
			case 3:
			case 4:
				if (orientation == 0) { // 横屏
					if (i == 0) {
						bigView = childView;
						childView.layout(0, 0, screenWidth / 3 * 2,
								screenHeight);
					} else {
						childView.layout(screenWidth / 3 * 2,
								(screenHeight / 3 * (i - 1)), screenWidth,
								(screenHeight / 3 * i));
					}
				} else { // 竖屏
					if (i == 0) {
						bigView = childView;
						childView.layout(0, 0, screenWidth,
								screenHeight / 3 * 2);
					} else {
						childView.layout(screenWidth / 3 * (i - 1),
								(screenHeight / 3 * 2), screenWidth / 3 * i,
								screenHeight);
					}
				}
				break;
			default:
				break;
			}
			views.put(i, childView);
		}

		/**
		 * 如果只有1路视频,那么用户可点击此SurfaceView以<br>
		 * 显示或隐藏顶边或底边栏.
		 */
		if (PublicInfo.OPENED_VIDEO_COUNT == 1) {
			this.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PublicInfo.confActivityHandler
							.sendEmptyMessage(PublicInfo.SHOW_OR_HIDE_BOTTOMBAR);
				}
			});
		}
	}

	public void changeLayout() {
		onMeasure(PublicInfo.screenWidth, PublicInfo.screenHeight);
		onLayout(true, 0, 0, PublicInfo.screenWidth, PublicInfo.screenHeight);
	}

	/**
	 * 重新改变点击的View的onMeasure
	 * 
	 * @param v
	 */
	private void changeViewMeasure(View v) {

		for (int i = 0; i < getChildCount(); i++) {
			// 将点击的View的大小设置为2/3屏幕大小
			if (getChildAt(i) == v) {
				View childView = getChildAt(i);
				LayoutParams params = (LayoutParams) childView
						.getLayoutParams();

				if (orientation == 0) { // 横屏
					params.height = PublicInfo.screenHeight;
					params.width = PublicInfo.screenWidth / 3 * 2;
				} else { // 竖屏
					params.height = PublicInfo.screenHeight / 3 * 2;
					params.width = PublicInfo.screenWidth;
				}

				// 已无用
				// if (msetonSizeChangeListener != null) {
				// msetonSizeChangeListener.setVideoSize(v, params);
				// }

			} else { // 其余的View都设置宽高1/3
				View childView = getChildAt(i);
				LayoutParams params = (LayoutParams) childView.getLayoutParams();
				params.height = screenHeight / 3;
				params.width = screenWidth / 3;

				// 已无用
				// if (msetonSizeChangeListener != null) {
				// msetonSizeChangeListener.setVideoSize(childView, params);
				// }
			}

		}
		measureChildren(screenWidth, screenHeight);
		setMeasuredDimension(screenWidth, screenHeight);
	}

	/**
	 * 重新改变点击的View的onLayout
	 * 
	 * @param v
	 */
	private void changeViewLayout(View v) {
		int count = 0;
		for (int i = 0; i < getChildCount(); i++) {
			// 将点击的View的大小设置为2/3屏幕大小
			if (getChildAt(i) == v) {
				View childView = getChildAt(i);
				bigView = childView;
				if (orientation == 0) { // 横屏
					childView.layout(0, 0, screenWidth / 3 * 2, screenHeight);
				} else { // 竖屏
					childView.layout(0, 0, screenWidth, screenHeight / 3 * 2);
				}
				views.put(0, childView);
			} else { // 设置其余View的位置
				View childView = getChildAt(i);
				if (orientation == 0) { // 横屏
					childView.layout(screenWidth / 3 * 2,
							(screenHeight / 3 * count++), screenWidth,
							(screenHeight / 3 * count));
				} else { // 竖屏
					childView.layout(screenWidth / 3 * count++,
							(screenHeight / 3 * 2), screenWidth / 3 * count,
							screenHeight);
				}
				views.put(count, childView);
			}
		}
	}

	/**
	 * 因为打开文件柜所以重新更改View的测量
	 */
	private void changeViewMeasureForShare() {
		// 因为最多只有3个人,所以全部设为1/3屏幕宽高大小
		for (int i = 0; i < getChildCount(); i++) {
			View childView = getChildAt(i);
			LayoutParams params = (LayoutParams) childView.getLayoutParams();
			params.height = screenHeight / 3;
			params.width = screenWidth / 3;

			// 已无效
			// if (msetonSizeChangeListener != null) {
			// msetonSizeChangeListener.setVideoSize(childView, params);
			// }
		}
		measureChildren(screenWidth, screenHeight);
		setMeasuredDimension(screenWidth, screenHeight);
	}

	/**
	 * 因为打开文件柜所以重新更改View的布局
	 */
	private void changeViewLayoutForShare() {
		// 如果只有1路视频,那么就将这1路视频挤到边上
		if (getChildCount() == 1) {
			View childView = getChildAt(0);
			childView.layout(screenWidth / 3 * 2, 0, screenWidth,
					screenHeight / 3);
		}

		// 如果有3路视频,那么就将这3路视频挤到边上
		if (getChildCount() == 3) {
			View childView = views.get(1);
			childView.layout(screenWidth / 3 * 2, 0, screenWidth,
					screenHeight / 3);

			childView = views.get(2);
			childView.layout(screenWidth / 3 * 2, screenHeight / 3,
					screenWidth, screenHeight / 3 * 2);

			// 这个肯定是大View,挤到最后一个位置
			childView = views.get(0);
			childView.layout(screenWidth / 3 * 2, screenHeight / 3 * 2,
					screenWidth, screenHeight);
		}
	}

	@Override
	public void onClick(View v) {
		// 如果打开了文件柜就不能点击
		if (PublicInfo.isOpenedShareList)
			return;

		// 如果点击了大View就触发
		if (v == bigView) {
			PublicInfo.confActivityHandler
					.sendEmptyMessage(PublicInfo.SHOW_OR_HIDE_BOTTOMBAR);

		}

		// 画中画时点击无效
		if (PublicInfo.OPENED_VIDEO_COUNT == 2) {
			return;
		}

		// 改变点击后的所有View的测量和布局
		changeViewMeasure(v);
		changeViewLayout(v);
	}

	/**
	 * 接口相关[已无用]
	 */
	private setonSizeChangeListener msetonSizeChangeListener;

	public setonSizeChangeListener getMsetonSizeChangeListener() {
		return msetonSizeChangeListener;
	}

	public void setMsetonSizeChangeListener(
			setonSizeChangeListener msetonSizeChangeListener) {
		this.msetonSizeChangeListener = msetonSizeChangeListener;
	}

	/**
	 * 监听视频会议的数目变化导致视频video大小改变
	 * 
	 * @author laoyu
	 * 
	 */
	public interface setonSizeChangeListener {
		void setVideoSize(View tag, LayoutParams params);
	}

}
