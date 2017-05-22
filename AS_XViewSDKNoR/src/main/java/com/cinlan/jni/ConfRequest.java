package com.cinlan.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.cinlan.xview.utils.XviewLog;

import android.util.Log;

public class ConfRequest {

	private List<WeakReference<ConfRequestCallback>> callbacks;

	public void addCallback(ConfRequestCallback callback) {
		this.callbacks.add(new WeakReference<ConfRequestCallback>(callback));
	}

	private static ConfRequest mConfRequest;

	private ConfRequest() {
		callbacks = new ArrayList<WeakReference<ConfRequestCallback>>();
	};

	public static synchronized ConfRequest getInstance() {
		if (mConfRequest == null) {
			mConfRequest = new ConfRequest();
		}
		return mConfRequest;
	}

	private boolean isInConf = false;

	public native boolean initialize(ConfRequest request);

	public native void unInitialize();

	/**
	 * 打开同步视频 , 手机端调用
	 * 
	 * @param nGroupID
	 * @param szVideosXml
	 */
	public native void ConfOpenSyncVideo(long nGroupID, String szVideosXml);

	/**
	 * 删除同步视频 , 手机端调用
	 * 
	 * @param nGroupID
	 * @param nUserID
	 * @param szDeviceID
	 */
	public native void ConfCancelSyncVideo(long nGroupID, long nUserID,
			String szDeviceID);

	public native void ConfQuickEnter(int i, String j, long k, long m, int z);

	/**
	 * 打开同步混合流
	 * 
	 * @param id
	 * @param cd
	 * @param i
	 * @param j
	 * @param f
	 */
	private void OnSyncOpenVideoMixer(String id, int cd, int i, int j, String f) {
		System.out.println("OnSyncOpenVideoMixer");
	}

	/**
	 * 关闭同步混合流
	 * 
	 * @param szDevice
	 */
	private void OnSyncCloseVideoMixer(String szDevice) {
		System.out.println("");
	}

	/**
	 * 添加几路混合流
	 * 
	 * @param i
	 * @param k
	 * @param j
	 * @param s
	 */
	private void OnAddVideoMixer(String i, long k, String j, int s) {
		System.out.println();
	}

	/**
	 * 删除几路混合流 中的某一路
	 * 
	 * @param i
	 * @param j
	 * @param s
	 */
	private void OnDelVideoMixer(String i, long j, String s) {
		System.out.println();
	}

	// sXmlConfData :
	// <conf canaudio="1" candataop="1" canvideo="1" conftype="0" haskey="0"
	// id="0" key=""
	// layout="1" lockchat="0" lockconf="0" lockfiletrans="0" mode="2"
	// pollingvideo="0"
	// subject="ss" syncdesktop="0" syncdocument="1" syncvideo="0"
	// chairuserid='0' chairnickname=''>
	// </conf>
	// szInviteUsers :
	// <xml>
	// <user id="11760" nickname=""/>
	// <user id="11762" nickname=""/>
	// </xml>

	public native void applyChair(String szDeviceID);

	public native void createConf(String sXmlConfData, String sXmlInviteUsers);

	public native void destroyConf(long nConfID);

	public native void enterConf(long nConfID, String szPassword);

	public native void exitConf(long nConfID);

	public native void leaveConf(long nConfID);

	public native void kickConf(long nUserID);

	public native void inviteJoinConf(long nDstUserID);

	public native void getConfUserList(long nConfID);

	public native void resumeConf(String sXmlConfData, String sXmlInviteUsers);

	public native void muteConf();

	public native void modifyConfDesc(String sXml);

	public native void setConfSync(int syncMode);

	public native void getConfList();

	/**
	 * 申请
	 * @param type
	 */
	public native void applyForControlPermission(int type);

	/**
	 * 没有说话的权限
	 * @param type
	 */
	public native void releaseControlPermission(int type);

	public native void grantPermission(long userid, int type, int status);

	public native void setCapParam(String szDevID, int nSizeIndex,
			int nFrameRate, int nBitRate);

	public native void syncConfOpenVideo(long nGroupID, long nToUserID,
			String szDeviceID, int nPos);

	public native void cancelSyncConfOpenVideo(long nGroupID, long nToUserID,
			String szDeviceID, boolean bCloseVideo);

	public native void pollingConfOpenVideo(long nGroupID, long nToUserID,
			String sToDevID, long nFromUserID, String sFromDevID);

	public native void changeConfChair(long nGroupID, long nUserID);

	public native void createSipH323Call(long nGroupID, String uri,
			int callType, String sipUserName);

	public native void destroySipCall(long nGroupID, String uri, int callType);

	public native void setSipCallInConfID(long nGroupID, String ip);

	public native void sipSecondDial(long nGroupID, long nSipUserID,
			String uri, String dialNum);

	private void OnEnterConf(long nConfID, String pwd, long nTime,
			String szConfData, int nJoinResult) {
		XviewLog.e("ImRequest UI", "OnEnterConf " + nConfID + "  " + pwd + " "
				+ nTime + " " + szConfData + " " + nJoinResult);

		Log.e("sivin", "OnEnterConf: "+callbacks.size());

		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {

			Object obj = wf.get();
			Log.e("sivin", "OnEnterConf: "+obj);
			if (obj != null) {
				Log.e("sivin", "OnEnterConf: kai shi fa song " );
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnEnterConfCallback(nConfID, nTime, szConfData,
						nJoinResult);
			}
		}


	}

	private void OnDestroyConf(long nConfID) {
		XviewLog.e("ImRequest UI", "OnDestroyConf " + nConfID);
	}

	private void OnInviteJoinConf(String confXml, String userXml) {
		XviewLog.e("ImRequest UI", confXml + ";" + userXml);
	}

	// strUserList :
	// <xml>
	// <user id='' nickname=''/>
	// <user id='' nickname=''/>
	// </xml>
	private void OnConfUserListReport(long nConfID, String strUserList) {
		XviewLog.e("ImRequest UI", "OnConfUserListReport " + strUserList);
	}

	private void OnConfMemberEnter(long nConfID, long nTime, String szUserInfos) {
		XviewLog.e("ImRequest UI", "OnConfMemberEnter " + nConfID + " " + nTime
				+ " " + szUserInfos);
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnConfMemberEnterCallback(nConfID, nTime, szUserInfos);
			}
		}
	}

	private void OnConfMemberExit(long nConfID, long nTime, long nUserID, int exitcode, String email)
	{
		XviewLog.e("ImRequest UI", "OnConfMemberExit " + nConfID + " " + nTime
				+ " " + nUserID + " " + exitcode + " " + email);

		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnConfMemberExitCallback(nConfID, nTime, nUserID, exitcode, email);
			}
		}
	}

	private void OnConfMemberLeave(long nConfID, long nUserID) {
		XviewLog.e("ImRequest UI", "OnConfMemberLeave " + nConfID + " "
				+ nUserID);
	}

	private void OnKickConf(int nReason) {
		// TODO
		XviewLog.e("ImRequest UI", "OnKickConf " + nReason);
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnKickConfCallback(nReason);
			}
		}
	}

	private void OnConfNotify(long nSrcUserID, String srcNickName,
			long nConfID, String subject, long nTime) {
		// TODO
		XviewLog.e("ImRequest UI", "OnConfNotify " + nSrcUserID + " "
				+ srcNickName + " " + nConfID + " " + subject + " " + nTime);
	}

	private void OnConfNotifyEnd(long nConfID) {
		XviewLog.e("ImRequest UI", "OnConfNotifyEnd " + nConfID);
	}

	//
	private void OnGetConfList(String szConfListXml) {
		XviewLog.e("ImRequest UI", "OnGetConfList:" + szConfListXml);

		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnGetConfList(szConfListXml);
			}
		}
	}

	/**
	 * 同步视频:syncvideo='2'<br>
	 * 广播视频:syncvideo='1'<br>
	 * 自由视频:syncvideo='0'<br>
	 */
	private void OnModifyConfDesc(long nConfID, String szConfDescXml) {
		XviewLog.e("ImRequest UI", "OnModifyConfDesc " + nConfID + " "
				+ szConfDescXml);
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnModifyConfDesc(nConfID, szConfDescXml);
			}
		}
	}

	private void OnNotifyChair(long userid, int type) {
		XviewLog.e("ImRequest UI", "OnNotifyChair " + userid + " " + type);

	}

	private void OnGrantPermission(long userid, int type, int status) {
		XviewLog.e("ImRequest UI", "OnGrantPermission " + userid + " " + type
				+ " " + status);
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnGrantPermissionCallback(userid, type, status);
			}
		}
	}

	private void OnConfSyncOpenVideo(String sDstMediaID) {
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnConfSyncOpenVideoCallback(sDstMediaID);
			}
		}
	}

	private void OnConfSyncCloseVideo(long nDstUserID, String sDstMediaID,
			boolean bClose) {
		XviewLog.e("ImRequest UI", "OnConfSyncCloseVideo " + nDstUserID + " "
				+ sDstMediaID + " " + bClose);
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnConfSyncCloseVideoCallback(nDstUserID, sDstMediaID,
						bClose);
			}
		}
	}

	private void OnConfPollingOpenVideo(long nToUserID, String sToDevID,
			long nFromUserID, String sFromDevID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnConfPollingOpenVideo " + nToUserID + " "
				+ sToDevID + " " + nFromUserID + " " + sFromDevID);
	}

	private void OnSetConfMode(long confid, int mode) {
		XviewLog.e("ImRequest UI", "OnConfSyncCloseVideo " + confid + " "
				+ mode);
	}

	private void OnConfChairChanged(long nConfID, long nChairID) {
		XviewLog.e("ImRequest UI", "OnConfChairChanged " + nConfID + " "
				+ nChairID);

	}

	private void OnSetCanOper(long nConfID, boolean bCanOper) {
		XviewLog.e("ImRequest UI", "OnSetCanOper " + nConfID + " " + bCanOper);
	}

	private void OnSetCanInviteUser(long nConfID, boolean bInviteUser) {
		XviewLog.e("ImRequest UI", "OnSetCanInviteUser " + nConfID + " "
				+ bInviteUser);
	}

	private void OnConfMute() {
		XviewLog.e("ImRequest UI", "OnConfMute ");
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnConfMute();
			}
		}
	}

	// 这是混合流视频的
	public void OnCreateVideoMixer(String mediaId, String name, int layout,
			int width, int height, int monitorIndex, int mixerType, long ownerID) {
		XviewLog.e("ImRequest UI", "mediaid = " + mediaId + " ownerid="
				+ ownerID);
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnCreateVideoMixer(mediaId, name, monitorIndex,
						mixerType, ownerID);
			}
		}
	}

	// 退出时调用
	public void OnDestroyVideoMixer(String mediaId) {
		XviewLog.e("ImRequest UI", "mediaid = " + mediaId);
		for (WeakReference<ConfRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ConfRequestCallback callback = (ConfRequestCallback) obj;
				callback.OnDestroyVideoMixer(mediaId);
			}
		}
	}

	public void OnGetServerListInfo(String info) {

	}

	private void OnCreateSipH323Call(long nGroupID, String uri, int status) {

	}

	private void OnDestroySipH323Call(long nGroupID, String uri) {

	}

}
