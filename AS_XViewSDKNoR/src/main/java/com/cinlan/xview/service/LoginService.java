package com.cinlan.xview.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.cinlan.jni.ImRequest;
import com.cinlan.jni.ImRequestCallbackAdapter;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.utils.GlobalConfig;

public class LoginService extends AbstractHandler {

	private ImRequestCB imCB = null;

	public LoginService() {
		imCB = new ImRequestCB(this);
		ImRequest.getInstance().addCallback(imCB);
	}

	public void login(String userId, String passwd, Registrant caller, int flag, String mail) {
		initTimeoutMessage(JNI_REQUEST_LOG_IN, DEFAULT_TIME_OUT_SECS, caller);
		ImRequest.getInstance().login(userId, passwd,
				GlobalConfig.USER_STATUS_ONLINE, GlobalConfig.ANDROID, flag, mail);
	}

	class ImRequestCB extends ImRequestCallbackAdapter {

		private Handler handler;

		public ImRequestCB(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void OnLoginCallback(long nUserID, int nStatus, int nResult) {
			RequestLogInResponse.Result res = RequestLogInResponse.Result
					.fromInt(nResult);
			Message m = Message.obtain(handler, JNI_REQUEST_LOG_IN,
					new RequestLogInResponse(new User(nUserID), res));
			handler.dispatchMessage(m);
		}

		@Override
		public void OnConnectResponseCallback(int nResult) {
			RequestLogInResponse.Result res = RequestLogInResponse.Result
					.fromInt(nResult);
			if (res != RequestLogInResponse.Result.SUCCESS) {
				Message m = Message.obtain(handler, JNI_REQUEST_LOG_IN,
						new RequestLogInResponse(null, res));
				handler.dispatchMessage(m);
			}
		}

	}
}
