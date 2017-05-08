package com.cinlan.jni;

import com.cinlan.xview.utils.XviewLog;

import android.app.Activity;

public class FileRequest {
	private Activity context;
	private static FileRequest mFileRequest;

	private FileRequest(Activity context) {
		this.context = context;
	}

	public static synchronized FileRequest getInstance(Activity context) {

		if (mFileRequest == null) {
			mFileRequest = new FileRequest(context);
		}
		return mFileRequest;
	}

	public native boolean initialize(FileRequest request);

	public native void unInitialize();

	public native void inviteFileTrans(long nGroupID, String szToUserXml,
			String szFilesXml, int linetype);

	public native void acceptFileTrans(String szFileID, String szSavePath);

	public native void refuseFileTrans(String szFileID);

	public native void cancelFileTrans(String szFileID);

	public native void downLoadFile(long nGroupID, String szFileID,
			String szPathName);

	public native void delFile(String szFileID);

	public native void cancelGroupFile(String szFileID, int type);

	public native void resumeGroupFile(String szFileID, int type);

	public native void pauseGroupFile(String szFileID, int type);

	private void OnFileTransInvite(long nGroupID, int nBusinessType,
			String szFileID, String szFileName, long nFileBytes, int linetype) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransInvite--->" + nGroupID + ":"
				+ nBusinessType + ":" + szFileID + ":" + szFileName + ":"
				+ nFileBytes + ":" + linetype);
	}

	private void OnFileTransAccepted(int nBusinessType, String szFileID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransAccepted--->" + nBusinessType
				+ ":" + szFileID);
	}

	private void OnFileTransRefuse(String szFileID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransRefuse--->" + szFileID);
	}

	private void OnFileTransNotify(long nGroupID, int nBusinessType,
			long nFromUserID, String szFileID, String szFileName,
			long nFileBytes) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransNotify--->" + nGroupID + ":"
				+ nBusinessType + ":" + nFromUserID + ":" + szFileID + ":"
				+ szFileName + ":" + nFileBytes);
	}

	private void OnFileTransNotifyDel(long nGroupID, int nBusinessType,
			String szFileID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransNotifyDel--->" + nGroupID + ":"
				+ nBusinessType + ":" + szFileID);
	}

	private void OnFileTransBegin(String szFileID, int nTransType,
			long nFileSize) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransBegin--->" + szFileID + ":"
				+ nTransType + ":" + nFileSize);
	}

	private void OnFileTransProgress(String szFileID, long nBytesTransed,
			int nTransType) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransProgress--->" + szFileID + ":"
				+ nBytesTransed + ":" + nBytesTransed);
	}

	private void OnFileTransEnd(String szFileID, String szFileName,
			long nFileSize, int nTransType) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransEnd--->" + szFileID + ":"
				+ szFileName + ":" + nFileSize + ":" + nTransType);
	}

	private void OnFileTransCancel(String szFileID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileTransCancel--->" + szFileID);
	}

	private void OnFileDownloadError(String sFileID) {
		// TODO
		XviewLog.e("ImRequest UI", "OnFileDownloadError--->" + sFileID);
	}

}
