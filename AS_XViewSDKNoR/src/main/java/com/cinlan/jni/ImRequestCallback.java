package com.cinlan.jni;

/**
 * 
 * @author 28851274
 * 
 */
public interface ImRequestCallback {

	public void OnLoginCallback(long nUserID, int nStatus, int nResult);

	public void OnLogoutCallback(int status);

	/**
	 * When network connection state changed, this function will be called.<br>
	 * <p>
	 * When call {@link ImRequest#login(String, String, int, int)}, this call
	 * back will before than {@link #OnLoginCallback(long, int, int)}
	 * </p>
	 * 
	 * @param nResult
	 *            301 can't not connect server; 0: succeed
	 */
	public void OnConnectResponseCallback(int nResult);

	public void OnUpdateBaseInfoCallback(long nUserID, String updatexml);

	public void OnUserStatusUpdatedCallback(long nUserID, int nType,
			int nStatus, String szStatusDesc);

	public void OnChangeAvatarCallback(int nAvatarType, long nUserID,
			String AvatarName);

	public void OnModifyCommentNameCallback(long nUserId, String sCommmentName);

	public void OnCreateCrowdCallback(String sCrowdXml, int nResult);

	public void OnConnectResponse(int nResult);

	public void OnSignalDisconnected();

	public void OnServerFaild(String sModuleName);

	public void OnGetPersonalInfo(long nUserID, String InfoXml);
}
