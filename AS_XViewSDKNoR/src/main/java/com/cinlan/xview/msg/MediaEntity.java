package com.cinlan.xview.msg;

import java.io.Serializable;

//String mediaId, String name,
//int monitorIndex, int mixerType, long ownerID
public class MediaEntity implements Serializable {

	private String mediaId;
	private String name;
	private int monitorIndex;
	private int mixerType;
	private long ownerID;

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMonitorIndex() {
		return monitorIndex;
	}

	public void setMonitorIndex(int monitorIndex) {
		this.monitorIndex = monitorIndex;
	}

	public int getMixerType() {
		return mixerType;
	}

	public void setMixerType(int mixerType) {
		this.mixerType = mixerType;
	}

	public long getOwnerID() {
		return ownerID;
	}

	public void setOwnerID(long ownerID) {
		this.ownerID = ownerID;
	}

	@Override
	public String toString() {
		return "MediaEntity [mediaId=" + mediaId + ", name=" + name
				+ ", monitorIndex=" + monitorIndex + ", mixerType=" + mixerType
				+ ", ownerID=" + ownerID + "]";
	}

}
