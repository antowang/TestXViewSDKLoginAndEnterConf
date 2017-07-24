package com.cinlan.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.cinlan.xview.msg.EventConfMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.ui.p2p.view.PToPActivity;
import com.cinlan.xview.utils.XviewLog;

import org.greenrobot.eventbus.EventBus;

public class ImRequest {
	private static ImRequest mImRequest;

	private List<WeakReference<ImRequestCallback>> callbacks;

	private ImRequest() {
		callbacks = new ArrayList<WeakReference<ImRequestCallback>>();
	};

	public void addCallback(ImRequestCallback callback) {
		this.callbacks.add(new WeakReference<ImRequestCallback>(callback));
	}

	public static synchronized ImRequest getInstance() {
		if (mImRequest == null) {
			mImRequest = new ImRequest();
		}

		return mImRequest;
	}

	public native boolean initialize(ImRequest request);

	public native void unInitialize();

	public native void login(String szId, String szPassword, int status,
							 int type, int isAnonymous, String szName);

	public native void addFriends(long nGroupID, long nDstUserID,
			String sAdditInfo, String sSrcCommentName);

	public native void acceptAddFriends(long nGroupID, long nFromUserID,
			String sDstCommentName);

	public native void refuseAddFriends(long nFromUserID, String sReason);

	public native void delFriends(long nDstUserID);

	public native void updateMyStatus(int nStatus, String szStatusDesc);

	public native void changeMySign(String szSign);

	public native void changeMyNickName(String szNickName);

	public native void changeMyNeedAuth(int nNeedAuth);

	public native void changeMyPrivacy(int snPrivacy);

	public native void modifyCommentName(long nUserId, String sCommentName);

	public native void createFriendGroup(String szGroupName);

	public native void deleteFriendGroup(long nGroupID);

	public native void modifyFriendGroup(long nGroupID, String szGroupName);

	public native void moveFriendsToGroup(long nGroupID, long nDstUserID);

	public native void getPersonalInfo(long nUserID);

	public native void modifyPersonalInfo(String InfoXml);

	public native void changeSystemAvatar(String szAvatarName);

	public native void changeCustomAvatar(String szAvatar, int len,
			String szExtensionName);

	public native void onStartUpdate();

	public native void onStopUpdate();

	public native void searchMember(String szUnsharpName, int nStartNum,
			int nSearchNum);

	public native void createCrowd(String sCrowdName, int nMaxUserSize,
			int nNeedAuth, String sDstUserIDXml);

	public native void destroyCrowd(long nCrowdId);

	public native void modifyCrowd(String sCrowdInfoXml);

	public native void inviteJoinCrowd(String sCrowdInfoXml,
			String sDstUserIDXml);

	public native void acceptInviteJoinCrowd(long nCrowdId);

	public native void refuseInviteJoinCrowd(long nCrowdId, String sReason);

	public native void delCrowdMember(long nCrowdId, String sDstUserIDXml);

	public native void applyJoinCrowd(String sCrowdXml, String sAddInfo);

	public native void acceptApplyJoinCrowd(String sCrowdXml, long nUserId);

	public native void refuseApplyJoinCrowd(String sCrowdXml, long nUserId,
			String sReason);

	public native void exitCrowd(long nCrowdId);

	public native void searchCrowd(String szUnsharpName, int nStartNum,
			int nSearchNum);

	public native void delCrowdFile(long nCrowdId, String sFileID);

	public native void getCrowdFileInfo(long nCrowdId);

	public native void logout();


	/**
	 * 事件接收实在{@link PToPActivity#onNativeCallback(com.cinlan.xview.msg.EventConfMsg)}
	 * @param mStatus self:2,others:1;
	 */
	private void OnLogout(int mStatus) {
		XviewLog.e("ImRequest UI", "OnXviewLogout:---" + mStatus);

		/**
		 * 发送消息到{@link PToPActivity#onNativeCallback(com.cinlan.xview.msg.EventConfMsg)}
		 */
		EventBus.getDefault().post(new EventConfMsg(EventMsgType.ON_USER_LOGOUT));

		for (WeakReference<ImRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ImRequestCallback callback = (ImRequestCallback) obj;
				callback.OnLogoutCallback(mStatus);
			}
		}
	}

	/*
	 */
	private void OnLogin(long nUserID, int nStatus, long servertime, int nResult) {
		XviewLog.e("ImRequest UI", "OnXviewLogin:---" + nUserID + " " + nResult
				+ " " + servertime + " " + nResult);

		for (WeakReference<ImRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ImRequestCallback callback = (ImRequestCallback) obj;
				callback.OnLoginCallback(nUserID, nStatus, nResult);
			}
		}
	}

	public void OnGetSearchMember(String xml) {
		XviewLog.e("ImRequest UI", "OnGetSearchMember:---" + xml);
	}

	private void OnAddFriends(String sxml) {
		XviewLog.e("ImRequest UI", "OnAddFriends:---" + sxml);
	}

	private void OnAcceptAddFriends(String UserName) {
		XviewLog.e("ImRequest UI", "OnAcceptAddFriends:---" + UserName);
	}

	private void OnRefuseAddFriends(String sUserBaseInfo,
			String sUserExtendInfo, String sReason) {
		XviewLog.e("ImRequest UI", "OnRefuseAddFriends:---" + sUserBaseInfo);
	}

	private void OnGetFriends(String sxml) {
		XviewLog.e("ImRequest UI", "OnGetFriends:---" + sxml);
		int start = sxml.indexOf("<friendlist>");
		int end = sxml.indexOf("</xml>");
	}

	private void OnUserStatusUpdated(long nUserID, int nStatus,
			String szStatusDesc) {
		XviewLog.e("ImRequest UI", "OnGetFriends:---" + nUserID + ":" + nStatus
				+ ":" + szStatusDesc);
	}

	private void OnUserSignUpdated(long nUserID, String szNewSign) {
		XviewLog.e("ImRequest UI", "OnUserSignUpdated");
	}

	private void OnUserNickNameUpdated(long nUserID, String szNewNickName) {
		XviewLog.e("ImRequest UI", "OnUserNickNameUpdated");
	}

	private void OnUserPrivacyUpdated(long nUserID, int nPrivacy) {
		XviewLog.e("ImRequest UI", "OnUserPrivacyUpdated");
	}

	private void OnCreateFriendGroup(long nGroupID, String szGroupName) {
		XviewLog.e("ImRequest UI", "OnCreateFriendGroup");
	}

	private void OnDestroyFriendGroup(long nGroupID) {
		XviewLog.e("ImRequest UI", "OnDestroyFriendGroup");
	}

	private void OnModifyFriendGroup(long nGroupID, String szGroupName) {
		XviewLog.e("ImRequest UI", "OnModifyFriendGroup");
	}

	private void OnDelFriends(long nUserID) {
		XviewLog.e("ImRequest UI", "OnDelFriends");
	}

	private void OnMoveFriendsToGroup(long nDstUserID, long nDstGroupID) {
		XviewLog.e("ImRequest UI", "OnMoveFriendsToGroup");
	}

	private void OnGetPersonalInfo(long nUserID, String InfoXml) {
		XviewLog.e("ImRequest UI", "OnGetPersonalInfo" + nUserID + "-->"
				+ InfoXml);
		for (WeakReference<ImRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ImRequestCallback callback = (ImRequestCallback) obj;
				callback.OnGetPersonalInfo(nUserID, InfoXml);
			}
		}
	}

	private void OnChangeAvatar(int nAvatarType, long nUserID, String AvatarName) {
		XviewLog.e("ImRequest UI", "OnChangeAvatar");
	}

	private void OnHaveUpdateNotify(String updatefilepath, String updatetext) {
		XviewLog.e("ImRequest UI", "OnHaveUpdateNotify");
	}

	private void OnUpdateDownloadBegin(long filesize) {
		XviewLog.e("ImRequest UI", "OnUpdateDownloadBegin");
	}

	private void OnUpdateDownloading(long size) {
		XviewLog.e("ImRequest UI", "OnUpdateDownloading");
	}

	private void OnUpdateDownloadEnd(boolean error) {
		XviewLog.e("ImRequest UI", "OnUpdateDownloadEnd");
	}

	private void OnCreateCrowd(String sCrowdXml, int nResult) {
		XviewLog.e("ImRequest UI", "OnCreateCrowd");
	}

	private void OnInviteCrowd(String nAdminUser, String sCrowdXml) {
		XviewLog.e("ImRequest UI", "OnInviteCrowd");
	}

	private void OnAcceptInviteCrowd(long nCrowdId, String nUserInfoXml) {
		XviewLog.e("ImRequest UI", "OnAcceptInviteCrowd");
	}

	private void OnApplyCrowd(long nCrowdId, String nUserInfoXml, String sReason) {
		XviewLog.e("ImRequest UI", "OnApplyCrowd");
	}

	private void OnRefuseInviteCrowd(long nCrowdId, String nUserInfoXml,
			String Reason) {
		XviewLog.e("ImRequest UI", "OnRefuseInviteCrowd");
	}

	private void OnAcceptApplyCrowd(String sCrowdXml, String sAdminInfoXml) {
		XviewLog.e("ImRequest UI", "OnAcceptApplyCrowd");
	}

	private void OnRefuseApplyCrowd(String sCrowdXml, String sAdminInfoXml,
			String sReason) {
		XviewLog.e("ImRequest UI", "OnRefuseApplyCrowd");
	}

	private void OnDestroyCrowd(long nCrowdId, long nAdminId) {
		XviewLog.e("ImRequest UI", "OnDestroyCrowd");
	}

	private void OnKickCrowd(long nCrowdId, long nAdminId) {
		XviewLog.e("ImRequest UI", "OnKickCrowd");
	}

	private void OnExitCrowd(long nCrowdId, long nUserId) {
		XviewLog.e("ImRequest UI", "OnExitCrowd");
	}

	private void OnMemberEnter(long nCrowdId, String sUserInfoXml) {
		XviewLog.e("ImRequest UI", "OnMemberEnter");
	}

	private void OnAcceptInviteJoinCrowd(String sXml) {
	}

	private void OnSearchCrowd(String InfoXml) {
	}

	private void OnModifyCrowd(long nCrowdId, String InfoXml) {
	}

	private void Oncrowdfile(long nCrowdId, String InfoXml) {
	}

	private void OnGetCrowdFileInfo(long nCrowdId, String InfoXml) {
	}

	private void OnDelCrowdFile(long nCrowdId, String sFileID) {
	}

	private void OnDismiss(long nCrowdId) {
	}

	private void OnModifyCommentName(long nUserId, String sCommmentName) {
	}

	private void OnConnectResponse(int nResult) {
		XviewLog.e("ImRequest UI", "OnConnectResponse:---" + nResult);
		for (WeakReference<ImRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ImRequestCallback callback = (ImRequestCallback) obj;
				callback.OnConnectResponseCallback(nResult);
			}
		}
	}

	private void OnServerFaild(String sModuleName) {
		XviewLog.e("ImRequest UI", "OnServerFaild::" + sModuleName);
	}

	private void OnSignalDisconnected() {
		XviewLog.e("ImRequest UI", "OnSignalDisconnected:---");
		for (WeakReference<ImRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				ImRequestCallback callback = (ImRequestCallback) obj;
				callback.OnSignalDisconnected();
			}
		}
	}

}
