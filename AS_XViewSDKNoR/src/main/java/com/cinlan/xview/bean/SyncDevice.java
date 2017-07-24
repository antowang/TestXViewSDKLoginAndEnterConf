package com.cinlan.xview.bean;

public class SyncDevice {

	private String DstDeviceID;
	private String DstUserID;
	private int Full;
	private int IsSyncing;
	private int PageId;
	private int Pos;

	public SyncDevice(String dstDeviceID, String dstUserID, int full,
			int isSyncing, int pageId, int pos) {
		super();
		DstDeviceID = dstDeviceID;
		DstUserID = dstUserID;
		Full = full;
		IsSyncing = isSyncing;
		PageId = pageId;
		Pos = pos;
	}

	public SyncDevice() {
		super();
	}

	public String getDstDeviceID() {
		return DstDeviceID;
	}

	public void setDstDeviceID(String dstDeviceID) {
		DstDeviceID = dstDeviceID;
	}

	public String getDstUserID() {
		return DstUserID;
	}

	public void setDstUserID(String dstUserID) {
		DstUserID = dstUserID;
	}

	public int getFull() {
		return Full;
	}

	public void setFull(int full) {
		Full = full;
	}

	public int getIsSyncing() {
		return IsSyncing;
	}

	public void setIsSyncing(int isSyncing) {
		IsSyncing = isSyncing;
	}

	public int getPageId() {
		return PageId;
	}

	public void setPageId(int pageId) {
		PageId = pageId;
	}

	public int getPos() {
		return Pos;
	}

	public void setPos(int pos) {
		Pos = pos;
	}

	@Override
	public String toString() {
		return "SyncDevice [DstDeviceID=" + DstDeviceID + ", DstUserID="
				+ DstUserID + ", Full=" + Full + ", IsSyncing=" + IsSyncing
				+ ", PageId=" + PageId + ", Pos=" + Pos + "]";
	}

}
