package com.cinlan.jni;

public abstract class ImRequestCallbackAdapter implements ImRequestCallback {

	@Override
	public void OnLoginCallback(long nUserID, int nStatus, int nResult) {

	}

	@Override
	public void OnLogoutCallback(int status) {

	}

	@Override
	public void OnConnectResponseCallback(int nResult) {

	}

	@Override
	public void OnUpdateBaseInfoCallback(long nUserID, String updatexml) {

	}

	@Override
	public void OnUserStatusUpdatedCallback(long nUserID, int nType,
			int nStatus, String szStatusDesc) {

	}

	@Override
	public void OnChangeAvatarCallback(int nAvatarType, long nUserID,
			String AvatarName) {

	}

	@Override
	public void OnModifyCommentNameCallback(long nUserId, String sCommmentName) {

	}

	@Override
	public void OnCreateCrowdCallback(String sCrowdXml, int nResult) {

	}

	@Override
	public void OnConnectResponse(int nResult) {

	}

	@Override
	public void OnServerFaild(String sModuleName) {

	}

	@Override
	public void OnSignalDisconnected() {

	}

	@Override
	public void OnGetPersonalInfo(long nUserID, String InfoXml) {

	}
}
