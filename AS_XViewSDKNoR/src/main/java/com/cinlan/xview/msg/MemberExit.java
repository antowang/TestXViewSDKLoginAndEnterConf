package com.cinlan.xview.msg;

import java.io.Serializable;

public class MemberExit implements Serializable{

	private static final long serialVersionUID = 1L;
	private long nConfID;
	private long nTime;
	private long  nUserID;
	private int exitcode;
	private String email;

	public int getExitcode() {
		return exitcode;
	}

	public String getEmail() {
		return email;
	}

	public void setExitcode(int exitcode) {
		this.exitcode = exitcode;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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
	public long getnUserID() {
		return nUserID;
	}
	public void setnUserID(long nUserID) {
		this.nUserID = nUserID;
	}
	
}
