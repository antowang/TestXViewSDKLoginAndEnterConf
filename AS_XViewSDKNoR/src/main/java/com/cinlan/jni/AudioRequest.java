package com.cinlan.jni;

import android.content.Context;

import com.cinlan.xview.utils.XviewLog;
import com.example.VoiceEngine;

public class AudioRequest {
	private static AudioRequest mAudioRequest;

	private AudioRequest() {
	};

	public static synchronized AudioRequest getInstance() {
		if (mAudioRequest == null) {
			mAudioRequest = new AudioRequest();
		}

		VoiceEngine v = new VoiceEngine();

		return mAudioRequest;
	}

	public native boolean initialize(AudioRequest request);

	public native void unInitialize();

	public native void InviteAudioChat(long nGroupID, long nToUserID);

	public native void AcceptAudioChat(long nGroupID, long nToUserID);

	public native void RefuseAudioChat(long nGroupID, long nToUserID);

	public native void CloseAudioChat(long nGroupID, long nToUserID);

	public native void MuteMic(long nGroupID, long nUserID, boolean bMute);

	private void OnAudioChatInvite(long nGroupID, long nBusinessType,
			long nFromUserID) {
		XviewLog.e("ImRequest UI", "OnAudioChatInvite " + nGroupID + ":"
				+ nBusinessType + ":" + nFromUserID);
	}

	private void OnAudioChatAccepted(long nGroupID, long nBusinessType,
			long nFromUserID) {
		XviewLog.e("ImRequest UI", "OnAudioChatAccepted " + nGroupID + ":"
				+ nBusinessType + ":" + nFromUserID);
	}

	private void OnAudioChatRefused(long nGroupID, long nBusinessType,
			long nFromUserID) {
		XviewLog.e("ImRequest UI", "OnAudioChatRefused " + nGroupID + ":"
				+ nBusinessType + ":" + nFromUserID);
	}

	private void OnAudioChatClosed(long nGroupID, long nBusinessType,
			long nFromUserID) {
		XviewLog.e("ImRequest UI", "OnAudioChatClosed " + nGroupID + ":"
				+ nBusinessType + ":" + nFromUserID);
	}

	private void OnAudioChating(long nGroupID, long nBusinessType,
			long nFromUserID) {
		XviewLog.e("ImRequest UI", "OnAudioChating " + nGroupID + ":"
				+ nBusinessType + ":" + nFromUserID);
	}
}
