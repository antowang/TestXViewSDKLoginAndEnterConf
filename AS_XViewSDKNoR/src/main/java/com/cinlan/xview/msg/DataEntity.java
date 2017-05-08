package com.cinlan.xview.msg;

import java.io.Serializable;

import com.cinlan.xview.bean.data.Label;

/**
 * 
 * @author wenqiang
 * 
 */
public class DataEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6160205047037900684L;
	private String szWBoardID;
	private int nPageID;
	private Label label;
	private String szDataID;

	public String getSzDataID() {
		return szDataID;
	}

	public void setSzDataID(String szDataID) {
		this.szDataID = szDataID;
	}

	public String getSzWBoardID() {
		return szWBoardID;
	}

	public void setSzWBoardID(String szWBoardID) {
		this.szWBoardID = szWBoardID;
	}

	public int getnPageID() {
		return nPageID;
	}

	public void setnPageID(int nPageID) {
		this.nPageID = nPageID;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

}
