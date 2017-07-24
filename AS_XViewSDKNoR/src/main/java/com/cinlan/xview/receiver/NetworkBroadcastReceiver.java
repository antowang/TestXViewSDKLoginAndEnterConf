package com.cinlan.xview.receiver;

import com.cinlan.xview.PublicInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (PublicInfo.isBreakLink
				&& PublicInfo.currentActivity.equals("ConfListActivity")) {
			PublicInfo.confListRefreshHandler
					.sendEmptyMessage(PublicInfo.TRY_LOGIN);
			PublicInfo.isBreakLink = false;
		}

		if (!mobileInfo.isConnected() && !wifiInfo.isConnected()
				&& PublicInfo.currentActivity.equals("ConfListActivity")) {
			Toast.makeText(context, "network disconnected", 0).show();
			PublicInfo.isBreakLink = true;
		}

		if ((mobileInfo.isConnected() || wifiInfo.isConnected())
				&& PublicInfo.currentActivity.equals("LoginActivity")
				&& PublicInfo.isBreakLink) {
			PublicInfo.isBreakLink = true;
			PublicInfo.loginActivityHandler
					.sendEmptyMessage(PublicInfo.connected);
		}
	}

}
