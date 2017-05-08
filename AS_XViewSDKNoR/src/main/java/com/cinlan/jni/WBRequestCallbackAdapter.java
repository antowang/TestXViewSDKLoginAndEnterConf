package com.cinlan.jni;


public class WBRequestCallbackAdapter implements WBRequestCallback{

	@Override
	public void OnWBoardChatInviteCallback(long nGroupID, int nBusinessType,
			long nFromUserID, String szWBoardID, int nWhiteIndex,
			String szFileName, int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardPageListCallback(String szWBoardID, String szPageData,
			int nPageID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardActivePageCallback(long nUserID, String szWBoardID,
			int nPageID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnRecvAddWBoardDataCallback(String szWBoardID, int nPageID,
			String szDataID, String szData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnRecvAppendWBoardDataCallback(String szWBoardID, int nPageID,
			String szDataID, String szData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardChatAcceptedCallback(long nGroupID, int nBusinessType,
			long nFromUserID, String szWBoardID, int nWhiteIndex,
			String szFileName, int type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardChatingCallback(long nGroupID, int nBusinessType,
			long nFromUserID, String szWBoardID, String szFileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardClosedCallback(long nGroupID, int nBusinessType,
			long nUserID, String szWBoardID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnRecvChangeWBoardDataCallback(String szWBoardID, int nPageID,
			String szDataID, String szData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardDataRemovedCallback(String szWBoardID, int nPageID,
			String szDataID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardAddPageCallback(String szWBoardID, int nPageID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnWBoardDocDisplayCallback(String szWBoardID, int nPageID,
			String szFileName, int result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnDataBeginCallback(String szWBoardID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnDataEndCallback(String szWBoardID) {
		// TODO Auto-generated method stub
		
	}

}
