package com.cinlan.jni;

/**
 * 会议请求回掉接口
 * 
 * @author Chong
 * 
 */
public interface ConfRequestCallback {

	/**
	 * 进入会议时回掉
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param szConfData
	 * @param nJoinResult
	 */
	public void OnEnterConfCallback(long nConfID, long nTime,
			String szConfData, int nJoinResult);

	/**
	 * 会议成员进入时回掉
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param szUserInfos
	 */
	public void OnConfMemberEnterCallback(long nConfID, long nTime,
			String szUserInfos);

	public void OnConfSyncOpenVideoCallback(String sDstMediaID);

	public void OnConfSyncCloseVideoCallback(long nDstUserID,
			String sDstMediaID, boolean bClose);

	/**
	 * 会议成员退出时回掉
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param nUserID
	 */
	public void OnConfMemberExitCallback(long nConfID, long nTime, long nUserID, int exitcode, String email);

	/**
	 * 修改会议描述时收到此回调
	 * 
	 * @param nConfID
	 * @param szConfDescXml
	 */
	public void OnModifyConfDesc(long nConfID, String szConfDescXml);

	/**
	 * 被踢出会议时回掉
	 * 
	 * @param nReason
	 */
	public void OnKickConfCallback(int nReason);

	/**
	 * 授权时回掉
	 * 
	 * @param userid
	 * @param type
	 * @param status
	 */
	public void OnGrantPermissionCallback(long userid, int type, int status);

	/**
	 * 会议被唤醒时回掉
	 * 
	 * @param confXml
	 * @param creatorXml
	 */
	public void OnConfNotify(String confXml, String creatorXml);

	/**
	 * 获取会议列表时回掉
	 * 
	 * @param szConfListXml
	 */
	public void OnGetConfList(String szConfListXml);

	/**
	 * 创建混流时回掉
	 * 
	 * @param mediaId
	 * @param name
	 * @param monitorIndex
	 * @param mixerType
	 * @param ownerID
	 */
	public void OnCreateVideoMixer(String mediaId, String name,
			int monitorIndex, int mixerType, long ownerID);

	/**
	 * 销毁混流时回掉
	 * 
	 * @param mediaId
	 */
	public void OnDestroyVideoMixer(String mediaId);

	/**
	 * 被禁言时回掉
	 */
	public void OnConfMute();

}
