package com.cinlan.jni;


/**
 * 
 * @author tieziqiang
 *
 */
public interface WBRequestCallback {
	
	public void OnWBoardChatInviteCallback(long nGroupID, int nBusinessType, long  nFromUserID, String szWBoardID, 
			int nWhiteIndex,String szFileName, int type);
	
	public void OnWBoardPageListCallback(String szWBoardID, String szPageData,
			int nPageID);
	
	public void OnWBoardActivePageCallback(long nUserID, String szWBoardID, int nPageID);
	
	
	public void OnRecvAddWBoardDataCallback(String szWBoardID, int nPageID,
			String szDataID, String szData);
	
	public void OnRecvAppendWBoardDataCallback(String szWBoardID, int nPageID,
			String szDataID, String szData);
	
	public void OnWBoardChatAcceptedCallback(long  nGroupID, int nBusinessType, long  nFromUserID, String szWBoardID, 
			int nWhiteIndex,String szFileName, int type);
	
	public void OnWBoardChatingCallback(long nGroupID, int nBusinessType,long nFromUserID, String szWBoardID, String szFileName);
	
	public void OnWBoardClosedCallback(long  nGroupID, int nBusinessType, long nUserID, String szWBoardID);
	
	public void OnRecvChangeWBoardDataCallback(String szWBoardID, int nPageID,String szDataID, String szData);
	
	public void OnWBoardDataRemovedCallback(String szWBoardID, int nPageID,String szDataID); 
	
	public void OnWBoardAddPageCallback(String szWBoardID, int nPageID);
	
	public void OnWBoardDocDisplayCallback(String szWBoardID, int nPageID,String szFileName,int result);
	
	public void OnDataBeginCallback(String szWBoardID);
	
	public void OnDataEndCallback(String szWBoardID);
	
}
