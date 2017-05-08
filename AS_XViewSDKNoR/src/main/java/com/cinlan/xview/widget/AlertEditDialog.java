package com.cinlan.xview.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.bean.Conf;
import com.cinlankeji.khb.iphone.R;

public class AlertEditDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private EditText txt_msg;
	private Button btn_neg;
	private Button btn_pos;
	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
	private Conf conf;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;
	private boolean isConfHasKey = true;

	public AlertEditDialog(Context context, Conf conf) {
		this.context = context;
		this.conf = conf;

		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public AlertEditDialog builder() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_alerteditdialog_xviewsdk, null);

		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg_xviewsdk);
		txt_title = (TextView) view.findViewById(R.id.txt_title_xviewsdk);
		txt_title.setVisibility(View.GONE);
		txt_msg = (EditText) view.findViewById(R.id.txt_msg_xviewsdk);
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

		return this;
	}

	public AlertEditDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	private String confId;

	public AlertEditDialog setConfInfo(String confId) {
		this.confId = confId;
		return this;
	}

	public AlertEditDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("");
		} else {
			txt_msg.setText(msg);
		}
		return this;
	}

	public AlertEditDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public AlertEditDialog setPositiveButton(String text) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText(context.getResources().getString(R.string.sure_xviewsdk));
		} else {
			btn_pos.setText(text);
		}
		btn_pos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return this;
	}

	/**
	 * -1:don't know<br>
	 * 1:has key<br>
	 * 0:no key
	 * 
	 * @param flag
	 * @return
	 */
	public AlertEditDialog setConfHasKey(int flag) {
		switch (flag) {
		case 1:
			isConfHasKey = true;
			break;
		case 0:
			isConfHasKey = false;
		case -1:
			isConfHasKey = false;
			break;
		default:
			break;
		}
		return this;
	}

	public AlertEditDialog setNegativeButton(String text) {
		showNegBtn = true;
		if ("".equals(text)) {
			btn_neg.setText(context.getResources().getString(R.string.cancel_xviewsdk));
		} else {
			btn_neg.setText(text);
		}
		btn_neg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if (PublicInfo.isAnonymousLogin) {
					/**
					 * if not input confPwd,dismissDialog and Exit
					 * ConfListActivity.
					 */
					PublicInfo.confListRefreshHandler
							.sendEmptyMessage(PublicInfo.CLOSE_CONFLISTACTIVITY);
				}
			}
		});
		return this;
	}

	private void setLayout() {
		if (!showTitle && !showMsg) {
			txt_title.setText("");
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showTitle) {
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showMsg) {
			txt_msg.setVisibility(View.VISIBLE);
		}

		if (!showPosBtn && !showNegBtn) {
			btn_pos.setText("");
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

	private ProgressDialog mProDialog;

	private void dismissProgress() {
		if (mProDialog != null && mProDialog.isShowing()) {
			mProDialog.dismiss();
			mProDialog = null;
		}
	}

	private void showWaitDialog() {
		mProDialog = new ProgressDialog(context);
		mProDialog
				.setMessage(context.getResources().getString(R.string.enting_xviewsdk));
		mProDialog.setIndeterminate(true);
		mProDialog.setCancelable(false);
		mProDialog.show();
	}

}
