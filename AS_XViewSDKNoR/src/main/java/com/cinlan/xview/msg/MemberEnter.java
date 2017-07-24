package com.cinlan.xview.msg;

import java.io.Serializable;

public class MemberEnter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long nConfID;
	private long nTime;
	private String szUserInfos;
	public long getnConfID() {
		return nConfID;
	}
	public void setnConfID(long nConfID) {
		this.nConfID = nConfID;
	}
	public long getnTime() {
		return nTime;
	}
	public void setnTime(long nTime) {
		this.nTime = nTime;
	}
	public String getSzUserInfos() {
		return szUserInfos;
	}
	public void setSzUserInfos(String szUserInfos) {
		this.szUserInfos = szUserInfos;
	}
	
	
	
}
