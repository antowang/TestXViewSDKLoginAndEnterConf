package com.cinlan.xview.bean.data;

import java.io.Serializable;

public class DataOperation implements Serializable{

	
	private String WBoardID;
	private long groupId;
	private int  bussinesstype;
	private long fromUserID;
	private boolean isBfyn;
	
	public String getWBoardID() {
		return WBoardID;
	}
	public void setWBoardID(String wBoardID) {
		WBoardID = wBoardID;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public int getBussinesstype() {
		return bussinesstype;
	}
	public void setBussinesstype(int bussinesstype) {
		this.bussinesstype = bussinesstype;
	}
	public long getFromUserID() {
		return fromUserID;
	}
	public void setFromUserID(long fromUserID) {
		this.fromUserID = fromUserID;
	}
	public boolean isBfyn() {
		return isBfyn;
	}
	public void setBfyn(boolean isBfyn) {
		this.isBfyn = isBfyn;
	}
	
}
