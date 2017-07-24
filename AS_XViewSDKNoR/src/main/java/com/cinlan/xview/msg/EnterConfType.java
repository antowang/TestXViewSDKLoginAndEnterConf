package com.cinlan.xview.msg;

import java.io.Serializable;

/**
 * long nConfID, long nTime, String szConfData, int nJoinResult
 * 
 * @author tieziqiang
 * 
 */
public class EnterConfType implements Serializable {

	private static final long serialVersionUID = 1L;
	private long nConfID;
	private long ntime;
	private String szConfData;
	private int nJoinResult;

	public long getnConfID() {
		return nConfID;
	}

	public int getnJoinResult() {
		return nJoinResult;
	}

	public void setnJoinResult(int nJoinResult) {
		this.nJoinResult = nJoinResult;
	}

	public void setnConfID(long nConfID) {
		this.nConfID = nConfID;
	}

	public long getNtime() {
		return ntime;
	}

	public void setNtime(long ntime) {
		this.ntime = ntime;
	}

	public String getSzConfData() {
		return szConfData;
	}

	public void setSzConfData(String szConfData) {
		this.szConfData = szConfData;
	}

}
