package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档 实体
 */
public class DocShare implements Serializable {

	private static final long serialVersionUID = 1L;
	private String WBoardID;
	private String filename;
	private long groupId;
	private int bussinesstype;
	private long fromUserID;
	private int type;
	private int pagenums;
	private int currentpage;
	private List<Page> pages = new ArrayList<Page>();
	private boolean wb; // 是否是白板

	public boolean isWb() {
		return wb;
	}

	public void setWb(boolean wb) {
		this.wb = wb;
	}

	public DocShare() {
		Collections.synchronizedList(pages);
	}

	private Map<Integer, List<Label>> data = new HashMap<Integer, List<Label>>();

	public Map<Integer, List<Label>> getData() {
		return data;
	}

	public void setData(Map<Integer, List<Label>> data) {
		this.data = data;
	}

	public int getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}

	public int getPagenums() {
		return pagenums;
	}

	public void setPagenums(int pagenums) {
		this.pagenums = pagenums;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	public String getWBoardID() {
		return WBoardID;
	}

	public void setWBoardID(String wBoardID) {
		WBoardID = wBoardID;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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
