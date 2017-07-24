package com.cinlan.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ChatRequest {

	private List<WeakReference<ChatRequestCallback>> callbacks;

	public void addCallback(ChatRequestCallback callback) {
		this.callbacks.add(new WeakReference<ChatRequestCallback>(callback));
	}

	private ChatRequest() {
		callbacks = new ArrayList<WeakReference<ChatRequestCallback>>();
	}

	private static ChatRequest chatRequest;

	public static synchronized ChatRequest getInstance() {
		if (chatRequest == null) {
			chatRequest = new ChatRequest();
		}
		return chatRequest;
	}

	public native boolean initialize(ChatRequest request);

	public native void unInitialize();

	public native void sendChatText(long nGroupID, long nToUserID, byte[] data, int length);

	public native void sendChatPicture(long nGroupID, long nToUserID,
			byte[] pPicData, int nLength);

	public void OnRecvChatText(long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, byte[] szXmlText) {
		for (WeakReference<ChatRequestCallback> wr : this.callbacks) {
			Object obj = wr.get();
			if (obj != null) {
				ChatRequestCallback callback = (ChatRequestCallback) obj;
				callback.OnRecvChatTextCallback(nGroupID, nBusinessType,
						nFromUserID, nTime, szXmlText);
			}
		}
	}

	public void OnRecvChatPicture(long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, byte[] pPicData, int nLength) {
		for (WeakReference<ChatRequestCallback> wr : this.callbacks) {
			Object obj = wr.get();
			if (obj != null) {
				ChatRequestCallback callback = (ChatRequestCallback) obj;
				callback.OnRecvChatPictureCallback(nGroupID, nBusinessType,
						nFromUserID, nTime, pPicData, nLength);
			}
		}
	}

}
