package com.cinlan.xview.service;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * 注册者
 * @author ChongXue
 *
 */
public class Registrant {

	WeakReference<Handler> refH;
	int what;
	Object userObj;

	public Registrant(Handler h, int what, Object obj) {
		refH = new WeakReference<Handler>(h);
		this.what = what;
		this.userObj = obj;
	}

	public Handler getHandler() {
		if (refH == null)
			return null;

		return (Handler) refH.get();
	}

	public int getWhat() {
		return what;
	}

	public Object getObject() {
		return userObj;
	}
}
