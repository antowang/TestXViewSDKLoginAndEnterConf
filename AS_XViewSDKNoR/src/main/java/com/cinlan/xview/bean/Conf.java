package com.cinlan.xview.bean;

import java.io.Serializable;

public class Conf implements Serializable{

	private static final long serialVersionUID = 1L;
	private String chairnickname;
	private long chairuserid;
	private long id;         //会议ID
	private String subject;    //会议主题
	private boolean canaudio;  //会议是否能音频 
	private boolean candataop; //会议是否能数据操作
	private boolean canvideo;  //会议是否能视频
	private int conftype;      //会议类型   0：    1：即使会议     2：预约会议
	private long endtime;      //会议结束时间
	private long starttime;    //会议开始时间
	private boolean haskey;    //会议是否有密码
	private String key;			//会议密码						
	private boolean layout;    //会议是否同步布局
	private boolean lockchat;  //会议能否聊天    
	private boolean lockconf;  //是否锁定会议
	private boolean lockfiletrans;   //会议是否能传输文件
	private int mode;           //会议模式
	private boolean pollingvideo;     //是否在轮询视频
	private boolean syncdesktop;      //是否同步桌面
	private boolean syncdocument;     //会议是否同步文档
	private boolean syncvideo;        //是否同步视频
	private long createuserid;
	private String createusernickname;
	
	public Conf(){};
	public Conf(long confid) {
		this.id=confid;
	}
	public String getChairnickname() {
		return chairnickname;
	}
	public void setChairnickname(String chairnickname) {
		this.chairnickname = chairnickname;
	}
	public long getChairuserid() {
		return chairuserid;
	}
	public void setChairuserid(long chairuserid) {
		this.chairuserid = chairuserid;
	}
	
	
	public long getCreateuserid() {
		return createuserid;
	}
	public void setCreateuserid(long createuserid) {
		this.createuserid = createuserid;
	}
	public String getCreateusernickname() {
		return createusernickname;
	}
	public void setCreateusernickname(String createusernickname) {
		this.createusernickname = createusernickname;
	}
	public boolean isCanaudio() {
		return canaudio;
	}
	public void setCanaudio(boolean canaudio) {
		this.canaudio = canaudio;
	}
	public boolean isCandataop() {
		return candataop;
	}
	public void setCandataop(boolean candataop) {
		this.candataop = candataop;
	}
	public boolean isCanvideo() {
		return canvideo;
	}
	public void setCanvideo(boolean canvideo) {
		this.canvideo = canvideo;
	}
	public int getConftype() {
		return conftype;
	}
	public void setConftype(int conftype) {
		this.conftype = conftype;
	}
	public long getEndtime() {
		return endtime;
	}
	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}
	public long getStarttime() {
		return starttime;
	}
	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}
	public boolean isHaskey() {
		return haskey;
	}
	public void setHaskey(boolean haskey) {
		this.haskey = haskey;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isLayout() {
		return layout;
	}
	public void setLayout(boolean layout) {
		this.layout = layout;
	}
	public boolean isLockchat() {
		return lockchat;
	}
	public void setLockchat(boolean lockchat) {
		this.lockchat = lockchat;
	}
	public boolean isLockconf() {
		return lockconf;
	}
	public void setLockconf(boolean lockconf) {
		this.lockconf = lockconf;
	}
	public boolean isLockfiletrans() {
		return lockfiletrans;
	}
	public void setLockfiletrans(boolean lockfiletrans) {
		this.lockfiletrans = lockfiletrans;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public boolean isPollingvideo() {
		return pollingvideo;
	}
	public void setPollingvideo(boolean pollingvideo) {
		this.pollingvideo = pollingvideo;
	}
	public boolean isSyncdesktop() {
		return syncdesktop;
	}
	public void setSyncdesktop(boolean syncdesktop) {
		this.syncdesktop = syncdesktop;
	}
	public boolean isSyncdocument() {
		return syncdocument;
	}
	public void setSyncdocument(boolean syncdocument) {
		this.syncdocument = syncdocument;
	}
	public boolean isSyncvideo() {
		return syncvideo;
	}
	public void setSyncvideo(boolean syncvideo) {
		this.syncvideo = syncvideo;
	}

	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Conf other = (Conf) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	@Override
	public String toString() {
		return "Conf [chairnickname=" + chairnickname + ", chairuserid="
				+ chairuserid + ", id=" + id + ", subject=" + subject
				+ ", canaudio=" + canaudio + ", candataop=" + candataop
				+ ", canvideo=" + canvideo + ", conftype=" + conftype
				+ ", endtime=" + endtime + ", starttime=" + starttime
				+ ", haskey=" + haskey + ", key=" + key + ", layout=" + layout
				+ ", lockchat=" + lockchat + ", lockconf=" + lockconf
				+ ", lockfiletrans=" + lockfiletrans + ", mode=" + mode
				+ ", pollingvideo=" + pollingvideo + ", syncdesktop="
				+ syncdesktop + ", syncdocument=" + syncdocument
				+ ", syncvideo=" + syncvideo + ", createuserid=" + createuserid
				+ ", createusernickname=" + createusernickname + "]";
	}

}
