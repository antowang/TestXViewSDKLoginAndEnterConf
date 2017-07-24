package com.cinlan.jni;

public abstract class ConfRequestCallbackAdapter implements ConfRequestCallback {

	/**
	 * 进入会议时回掉
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param szConfData
	 * @param nJoinResult
	 */
	@Override
	public void OnEnterConfCallback(long nConfID, long nTime,
			String szConfData, int nJoinResult) {

	}

	/**
	 * 会议成员进入时回掉
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param szUserInfos
	 */
	@Override
	public void OnConfMemberEnterCallback(long nConfID, long nTime,
			String szUserInfos) {

	}

	@Override
	public void OnModifyConfDesc(long nConfID, String szConfDescXml) {

	}

	/**
	 * 会议成员退出时回掉
	 * 
	 * @param nConfID
	 * @param nTime
	 * @param nUserID
	 */
	@Override
	public void OnConfMemberExitCallback(long nConfID, long nTime, long nUserID, int exitcode, String email) {

	}

	/**
	 * 被踢出会议时回掉
	 * 
	 * @param nReason
	 */
	@Override
	public void OnKickConfCallback(int nReason) {

	}

	/**
	 * 授权时回掉
	 * 
	 * @param userid
	 * @param type
	 * @param status
	 */
	@Override
	public void OnGrantPermissionCallback(long userid, int type, int status) {

	}

	/**
	 * 会议被唤醒时回掉
	 * 
	 * @param confXml
	 * @param creatorXml
	 */
	@Override
	public void OnConfNotify(String confXml, String creatorXml) {

	}

	/**
	 * 获取会议列表时回掉
	 * 
	 * @param szConfListXml
	 */
	@Override
	public void OnGetConfList(String szConfListXml) {

	}

	/**
	 * 创建混流时回掉
	 * 
	 * @param mediaId
	 * @param name
	 * @param monitorIndex
	 * @param mixerType
	 * @param ownerID
	 */
	@Override
	public void OnCreateVideoMixer(String mediaId, String name,
			int monitorIndex, int mixerType, long ownerID) {

	}

	/**
	 * 销毁混流时回掉
	 * 
	 * @param mediaId
	 */
	@Override
	public void OnDestroyVideoMixer(String mediaId) {

	}

	/**
	 * 被禁言时回掉
	 */
	@Override
	public void OnConfMute() {

	}

	@Override
	public void OnConfSyncCloseVideoCallback(long nDstUserID,
			String sDstMediaID, boolean bClose) {

	}

	@Override
	public void OnConfSyncOpenVideoCallback(String sDstMediaID) {

	}
}
