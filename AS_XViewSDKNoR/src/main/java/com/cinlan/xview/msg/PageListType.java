package com.cinlan.xview.msg;

import java.io.Serializable;

/**
 *
 * @author tieziqiang
 *
 */
public class PageListType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8884588193389100295L;
	private String szWBoardID;
	private String szPageData;
	private int count;
	public String getSzWBoardID() {
		return szWBoardID;
	}
	public void setSzWBoardID(String szWBoardID) {
		this.szWBoardID = szWBoardID;
	}
	public String getSzPageData() {
		return szPageData;
	}
	public void setSzPageData(String szPageData) {
		this.szPageData = szPageData;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
