package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WBoard  extends DataOperation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String WBoardID;
	private long groupId;
	private int  bussinesstype;

	private long  fromUserID;
	private int type ;
	private int index;
	
	private List<Page> pages=new ArrayList<Page>();
	private List<Label> labels=new ArrayList<Label>();
	
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	
	
	public List<Page> getPages() {
		return pages;
	}
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	public List<Label> getLabels() {
		return labels;
	}
	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
