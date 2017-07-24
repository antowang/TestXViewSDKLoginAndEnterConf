package com.cinlan.jni;

public class AppShareRequest
{
	public native boolean initialize(AppShareRequest requestObj);
	public native void unInitialize();

	public native void InviteAppShare(long  nGroupID, long  nDstUserID, int nPID, int type); //type=0:driver type=1: hook
	public native void CloseAppShare(long  nGroupID, long  nDstUserID);
	public native void EnableControl(boolean bEnable);
	public native void SetBitWidth(int width);
	public native void SetFrameRate(int rate);
	public native void SetScaleMode(boolean bZoom);
	
	private void OnInviteAppHostShare(int result)
	{
		// TODO
	}
	
	private void OnInviteAppViewerShare(Object hwnd,int result)
	{
		// TODO
	}
	
	private void OnClostAppHostShare(int result)
	{
		// TODO
	}
	
	private void OnCloseAppViewerShare( int result)
	{
		// TODO
	}
}
