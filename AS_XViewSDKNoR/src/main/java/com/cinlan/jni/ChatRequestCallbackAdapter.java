package com.cinlan.jni;

public abstract class ChatRequestCallbackAdapter implements ChatRequestCallback {

	@Override
	public void OnSendChatTextCallback(long mGroupID, long nToUserID,
			String szText) {

	}

	@Override
	public void OnSendChatPictureCallback(long nGroupID, long nToUserID,
			byte[] pPicData, int nLength) {

	}

	@Override
	public void OnRecvChatTextCallback(long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, byte[] szXmlText) {

	}

	@Override
	public void OnRecvChatPictureCallback(long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, byte[] pPicData, int nLength) {

	}

}
