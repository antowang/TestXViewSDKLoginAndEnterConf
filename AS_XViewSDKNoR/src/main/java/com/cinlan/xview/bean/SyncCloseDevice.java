package com.cinlan.xview.bean;

public class SyncCloseDevice {
	private long nDstUserID;
	private String sDstMediaID;
	private boolean bClose;

	public SyncCloseDevice() {
		super();
	}

	public SyncCloseDevice(long nDstUserID, String sDstMediaID, boolean bClose) {
		super();
		this.nDstUserID = nDstUserID;
		this.sDstMediaID = sDstMediaID;
		this.bClose = bClose;
	}

	public long getnDstUserID() {
		return nDstUserID;
	}

	public void setnDstUserID(long nDstUserID) {
		this.nDstUserID = nDstUserID;
	}

	public String getsDstMediaID() {
		return sDstMediaID;
	}

	public void setsDstMediaID(String sDstMediaID) {
		this.sDstMediaID = sDstMediaID;
	}

	public boolean isbClose() {
		return bClose;
	}

	public void setbClose(boolean bClose) {
		this.bClose = bClose;
	}

	@Override
	public String toString() {
		return "SyncCloseDevice [nDstUserID=" + nDstUserID + ", sDstMediaID="
				+ sDstMediaID + ", bClose=" + bClose + "]";
	}

}
