package com.cinlan.jni;

public interface ChatRequestCallback {

	/**
	 * 发送文本消息时回掉
	 * 
	 * @param mGroupID
	 * @param nToUserID
	 * @param szText
	 */
	public void OnSendChatTextCallback(long mGroupID, long nToUserID,
			String szText);

	/**
	 * 发送图片消息时回掉
	 * 
	 * @param nGroupID
	 * @param nToUserID
	 * @param pPicData
	 * @param nLength
	 */
	public void OnSendChatPictureCallback(long nGroupID, long nToUserID,
			byte[] pPicData, int nLength);

	/**
	 * 返回文本消息时回掉
	 * 
	 * @param nGroupID
	 * @param nBusinessType
	 * @param nFromUserID
	 * @param nTime
	 * @param szXmlText
	 * @param nLength
	 */
	public void OnRecvChatTextCallback(long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, byte[] szXmlText);

	/**
	 * 返回图片消息时回掉
	 * 
	 * @param nGroupID
	 * @param nBusinessType
	 * @param nFromUserID
	 * @param nTime
	 * @param pPicData
	 * @param nLength
	 */
	public void OnRecvChatPictureCallback(long nGroupID, int nBusinessType,
			long nFromUserID, long nTime, byte[] pPicData, int nLength);
}
