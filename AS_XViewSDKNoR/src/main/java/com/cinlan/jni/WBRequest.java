package com.cinlan.jni;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.cinlan.xview.utils.XviewLog;

public class WBRequest {
	private static WBRequest mWBRequest;

	private List<WeakReference<WBRequestCallback>> callbacks;

	private WBRequest() {
		callbacks = new ArrayList<WeakReference<WBRequestCallback>>();
	};

	public void addCallback(WBRequestCallback callback) {
		this.callbacks.add(new WeakReference<WBRequestCallback>(callback));
	}

	public static synchronized WBRequest getInstance() {

		if (mWBRequest == null) {
			mWBRequest = new WBRequest();
		}
		return mWBRequest;
	}

	public native boolean initialize(WBRequest request);

	public native void unInitialize();

	public native void downLoadPageDoc(String bowardid, int pageid);

	private void OnWBoardChatInvite(long nGroupID, int nBusinessType,
			long nFromUserID, String szWBoardID, int nWhiteIndex,
			String szFileName, int type) {
		XviewLog.e("ImRequest UI", "OnWBoardChatInvite " + nGroupID + " "
				+ nBusinessType + " " + nFromUserID + " " + szWBoardID
				+ "锟斤拷锟叫猴拷锟斤拷:" + nWhiteIndex + "锟侥硷拷锟斤拷:" + szFileName);
		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnWBoardChatInviteCallback(nGroupID, nBusinessType,
						nFromUserID, szWBoardID, nWhiteIndex, szFileName, type);
			}
		}
	}

	private void OnWBoardPageList(String szWBoardID, String szPageData,
			int nPageID) {
		XviewLog.e("ImRequest UI", "OnWBoardPageList " + szWBoardID + " "
				+ szPageData + " " + nPageID);

		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnWBoardPageListCallback(szWBoardID, szPageData,
						nPageID);

			}
		}
	}

	private void OnWBoardActivePage(long nUserID, String szWBoardID, int nPageID) {
		XviewLog.e("ImRequest UI", "OnWBoardActivePage " + szWBoardID + " "
				+ nPageID);

	}

	private void OnRecvAddWBoardData(String szWBoardID, int nPageID,
			String szDataID, String szData) {
		XviewLog.e("ImRequest UI",
				"OnRecvAddWBoardData " + szWBoardID + " " + nPageID + " "
						+ szDataID + " " + szData + " " + szData.length());
		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnRecvAddWBoardDataCallback(szWBoardID, nPageID,
						szDataID, szData);
			}
		}
	}

	// 锟秸碉拷追锟接白帮拷锟斤拷莸幕氐锟�
	private void OnRecvAppendWBoardData(String szWBoardID, int nPageID,
			String szDataID, String szData) {
		XviewLog.e("ImRequest UI",
				"OnRecvAppendWBoardData " + szWBoardID + " " + nPageID + " "
						+ szDataID + " " + szData + " " + szData.length());
		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnRecvAppendWBoardDataCallback(szWBoardID, nPageID,
						szDataID, szData);
			}
		}
	}

	private void OnWBoardChatAccepted(long nGroupID, int nBusinessType,
			long nFromUserID, String szWBoardID, int nWhiteIndex,
			String szFileName, int type) {
		XviewLog.e("ImRequest UI", "OnWBoardChatAccepted " + nGroupID + " "
				+ nBusinessType + " " + nFromUserID + " " + szWBoardID + " "
				+ szFileName + " " + type);
	}

	private void OnWBoardChating(long nGroupID, int nBusinessType,
			long nFromUserID, String szWBoardID, String szFileName) {
		XviewLog.e("ImRequest UI", "OnWBoardChating " + nGroupID + " "
				+ nBusinessType + " " + nFromUserID + " " + szWBoardID + " "
				+ szFileName);
	}

	private void OnWBoardClosed(long nGroupID, int nBusinessType, long nUserID,
			String szWBoardID) {
		XviewLog.e("ImRequest UI", "OnWBoardClosed " + nGroupID + " "
				+ nBusinessType + " " + szWBoardID);
		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnWBoardClosedCallback(nGroupID, nBusinessType,
						nUserID, szWBoardID);
			}
		}
	}

	private void OnRecvChangeWBoardData(String szWBoardID, int nPageID,
			String szDataID, String szData) {
		XviewLog.e("ImRequest UI",
				"OnRecvChangeWBoardData " + szWBoardID + " " + nPageID + " "
						+ szDataID + " " + szData + " " + szData.length());
	}

	private void OnWBoardDataRemoved(String szWBoardID, int nPageID,
			String szDataID) {
		XviewLog.e("ImRequest UI", "OnWBoardDataRemoved " + szWBoardID + " "
				+ nPageID + " " + szDataID);
		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnWBoardDataRemovedCallback(szWBoardID, nPageID,
						szDataID);
			}
		}
	}

	private void OnWBoardAddPage(String szWBoardID, int nPageID) {
		XviewLog.e("ImRequest UI", "OnWBoardAddPage " + szWBoardID + " "
				+ nPageID);
		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnWBoardAddPageCallback(szWBoardID, nPageID);
			}
		}
	}

	//
	private void OnWBoardDeletePage(String szWBoardID, int nPageID) {
		XviewLog.e("ImRequest UI", "OnWBoardDeletePage " + szWBoardID + " "
				+ nPageID);
	}

	// 锟侥碉拷锟斤拷锟斤拷 应锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷一页锟斤拷示一页
	private void OnWBoardDocDisplay(String szWBoardID, int nPageID,
			String szFileName, int result) {
		XviewLog.e("ImRequest UI", "锟侥碉拷锟斤拷示---->OnWBoardDocDisplay "
				+ szWBoardID + " " + nPageID + " " + szFileName + "result"
				+ result);
		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnWBoardDocDisplayCallback(szWBoardID, nPageID,
						szFileName, result);
			}
		}
	}

	private void OnDataBegin(String szWBoardID) {
		XviewLog.e("ImRequest UI", "OnDataBegin " + szWBoardID);
	}

	private void OnDataEnd(String szWBoardID) {
		XviewLog.e("ImRequest UI", "OnDataEnd " + szWBoardID);

		for (WeakReference<WBRequestCallback> wf : this.callbacks) {
			Object obj = wf.get();
			if (obj != null) {
				WBRequestCallback callback = (WBRequestCallback) obj;
				callback.OnDataEndCallback(szWBoardID);
			}
		}
	}
}
