package com.cinlan.xview.receiver;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.ui.EnterConf;
import com.cinlan.xview.utils.ActivityHolder;
import com.cinlan.xview.utils.SPUtil;
import com.cinlan.xview.utils.XviewLog;
import com.cinlankeji.khb.iphone.R;

public class ForceOfflineReceiver extends BroadcastReceiver {

	private static final String XTAG = ForceOfflineReceiver.class.getSimpleName();

	@Override
	public void onReceive(final Context context, Intent intent) {
		int logoutFlag = intent.getIntExtra("logoutFlag", -1);
		XviewLog.i(XTAG, "logoutFlag = " + logoutFlag);
		if (logoutFlag == 1) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setTitle("Warning");
			dialogBuilder.setMessage(context.getResources().getString(
					R.string.force_offline_xviewsdk));
			dialogBuilder.setCancelable(false);
			dialogBuilder.setPositiveButton(
					context.getResources().getString(
							R.string.setting_exit_text_xviewsdk),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SPUtil.putConfigStrValue(context, "account", "");
							SPUtil.putConfigStrValue(context, "password", "");
							ActivityHolder.getInstance().finishAllActivity();
						}
					});
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			alertDialog.show();
			EnterConf.mIOnXViewCallback.onConfMsgListener(2);
		}
		if (logoutFlag == 2) {
			if (EnterConf.mIOnXViewCallback != null)
				EnterConf.mIOnXViewCallback.onConfMsgListener(3);
			PublicInfo.dismissDialog();
			ActivityHolder.getInstance().finishAllActivity();

			if (PublicInfo.logoutFlag == 1) {
				XviewLog.i(XTAG, " OnLogoutCallback logoutFlag = 1");
				EnterConf.mIOnXViewCallback.onLogoutResultListener(1);
				XviewLog.i(XTAG, " OnLogoutCallback callback success");
			}
			XviewLog.i(XTAG, " logoutFlag = 2 over");
		}
	}

}
