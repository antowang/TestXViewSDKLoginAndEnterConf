package com.cinlan.xview.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.cinlan.xview.utils.GlobalHolder;

/*
 * <xml>
 * 	<user id='4611686018427388010'>
 * 			<videolist defaultid='4611686018427388010:Integrated Camera____3505732250'>
 * 				<video bps='512' bps_max='4096' desc='Integrated Camera____3505732250' disable='0' fps='15' id='4611686018427388010:Integrated Camera____3505732250' lost_packet='3' selectedindex='5' trans_policy='0' videotype='vid'>
 * 					<sizelist>
 * 						<size h='1080' w='1920'/>
 * 						<size h='1024' w='1280'/>
 * 						<size h='720' w='1280'/>
 * 						<size h='768' w='1024'/>
 * 						<size h='600' w='800'/>
 * 						<size h='576' w='704'/>
 * 						<size h='480' w='640'/>
 * 						<size h='288' w='352'/>
 * 						<size h='240' w='320'/>
 * 						<size h='144' w='176'/>
 * 					</sizelist>
 * 					<camctrl addr='0' camtype='0' cmds='' comm='0' remoteptz='1'>
 * 						<positions/>
 * 					</camctrl>
 * 			</video>
 * 				<video bps='512' bps_max='4096' desc='e2eSoft VCam____2214096001' disable='0' fps='15' id='4611686018427388010:e2eSoft VCam____2214096001' lost_packet='3' selectedindex='5' trans_policy='0' videotype='vid'>
 * 					<sizelist>
 * 					<size h='1080' w='1920'/><size h='1024' w='1280'/><size h='720' w='1280'/><size h='768' w='1024'/><size h='600' w='800'/><size h='576' w='704'/><size h='480' w='640'/><size h='288' w='352'/><size h='240' w='320'/><size h='144' w='176'/></sizelist><camctrl addr='0' camtype='0' cmds='' comm='0' remoteptz='1'><positions/></camctrl>
 * 				</video>
 * 		</videolist>
 * 	</user>
 * </xml>
 */
public class VideoDevice implements Serializable, Comparable<VideoDevice> {

	private static final long serialVersionUID = 1L;
	private long userid;
	private String id;
	private String bps;
	private String desc;
	private String fbs;
	private Integer selectedindex;
	private String videotype;
	private List<Size> sizelist = new ArrayList<Size>();
	private int disable = 0; // ����ͷ�Ƿ��ܴ򿪹ر�

	public int getDisable() {
		return disable;
	}

	public void setDisable(int disable) {
		this.disable = disable;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBps() {
		return bps;
	}

	public void setBps(String bps) {
		this.bps = bps;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFbs() {
		return fbs;
	}

	public void setFbs(String fbs) {
		this.fbs = fbs;
	}

	public Integer getSelectedindex() {
		return selectedindex;
	}

	public void setSelectedindex(Integer selectedindex) {
		this.selectedindex = selectedindex;
	}

	public String getVideotype() {
		return videotype;
	}

	public void setVideotype(String videotype) {
		this.videotype = videotype;
	}

	public List<Size> getSizelist() {
		return sizelist;
	}

	public void setSizelist(List<Size> sizelist) {
		this.sizelist = sizelist;
	}

	@Override
	public String toString() {
		return "VideoDevice [userid=" + userid + ", id=" + id + ", bps=" + bps
				+ ", desc=" + desc + ", fbs=" + fbs + ", selectedindex="
				+ selectedindex + ", videotype=" + videotype + ", sizelist="
				+ sizelist + ", disable=" + disable + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VideoDevice other = (VideoDevice) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (userid ^ (userid >>> 32));
		return result;
	}

	@Override
	public int compareTo(VideoDevice another) {
		// make sure current user align first position
		if (this.userid == GlobalHolder.getInstance().getLocalUserId()) {
			return -1;
		}
		if (another.getUserid() == GlobalHolder.getInstance()
				.getLocalUserId()) {
			return 1;
		}
		return 0;
	}
}
