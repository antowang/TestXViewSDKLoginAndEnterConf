package com.cinlan.xview.widget;

import com.cinlankeji.khb.iphone.R;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class LogoutAlertDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
	private Button btn_neg;
	private Button btn_pos;
	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	private int btnDialogWidth = 0;

	public LogoutAlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public LogoutAlertDialog builder() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_alertdialog_xviewsdk, null);

		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg_xviewsdk);
		txt_title = (TextView) view.findViewById(R.id.txt_title_xviewsdk);
		txt_title.setVisibility(View.GONE);
		txt_msg = (TextView) view.findViewById(R.id.txt_msg_xviewsdk);
		txt_msg.setVisibility(View.GONE);
		btn_neg = (Button) view.findViewById(R.id.btn_neg_xviewsdk);
		btn_neg.setVisibility(View.GONE);
		btn_pos = (Button) view.findViewById(R.id.btn_pos_xviewsdk);
		btn_pos.setVisibility(View.GONE);
		img_line = (ImageView) view.findViewById(R.id.img_line_xviewsdk);
		img_line.setVisibility(View.GONE);

		dialog = new Dialog(context, R.style.AlertDialogStyle_xviewsdk);
		dialog.setContentView(view);

		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
				.getWidth() * 0.8), LayoutParams.WRAP_CONTENT));
		btnDialogWidth = (int) ((display.getWidth() * 0.8) - 0.5);
		LayoutParams btnPosParams = (LayoutParams) btn_pos.getLayoutParams();
		LayoutParams btnNegParams = (LayoutParams) btn_neg.getLayoutParams();
		btnPosParams.width = btnDialogWidth / 2;
		btnNegParams.width = btnDialogWidth / 2;
		btn_pos.setLayoutParams(btnPosParams);
		btn_neg.setLayoutParams(btnNegParams);
		return this;
	}

	public LogoutAlertDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	public LogoutAlertDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("");
		} else {
			txt_msg.setText(msg);
		}
		txt_msg.setTextColor(context.getResources().getColor(R.color.red_xviewsdk));
		return this;
	}

	public LogoutAlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public LogoutAlertDialog setPositiveButton(String text,
			final OnClickListener listener) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText(context.getResources().getString(R.string.sure_xviewsdk));
		} else {
			btn_pos.setText(text);
		}
		btn_pos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	public LogoutAlertDialog setNegativeButton(String text,
			final OnClickListener listener) {
		showNegBtn = true;
		if ("".equals(text)) {
			btn_neg.setText(context.getResources().getString(R.string.cancel_xviewsdk));
		} else {
			btn_neg.setText(text);
		}
		btn_neg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	public void dismissDialog() {
		dialog.dismiss();
	}

	private void setLayout() {
		if (!showTitle && !showMsg) {
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showTitle) {
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showMsg) {
			txt_msg.setVisibility(View.VISIBLE);
		}

		if (!showPosBtn && !showNegBtn) {
			btn_pos.setText(context.getResources().getString(R.string.sure_xviewsdk));
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector_xviewsdk);
			btn_pos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		if (showPosBtn && showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector_xviewsdk);
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector_xviewsdk);
			img_line.setVisibility(View.VISIBLE);
		}

		if (showPosBtn && !showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector_xviewsdk);
		}

		if (!showPosBtn && showNegBtn) {
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector_xviewsdk);
		}
	}

	public void show() {
		setLayout();
		dialog.show();
	}
}
