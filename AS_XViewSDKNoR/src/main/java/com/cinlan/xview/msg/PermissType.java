package com.cinlan.xview.msg;

import java.io.Serializable;

/**
 * long userid, int type, int status
 * @author tieziqiang
 *
 */
public class PermissType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5541495971170131393L;
	private long userid;
	private int type;
	private int status;
	public long getUserid() {
		return userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
