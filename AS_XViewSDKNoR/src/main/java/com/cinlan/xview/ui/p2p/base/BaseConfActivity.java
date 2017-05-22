package com.cinlan.xview.ui.p2p.base;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import com.cinlan.core.CaptureCapability;
import com.cinlan.core.VideoCaptureDevInfo;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.framework.BaseActivity;
import com.cinlan.xview.utils.XviewLog;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户开会BaseActivity
 * <p>
 * 主要功能是对显示数据流,解码,显示用户预览需要做的必要准备工作
 * <p>
 * Created by sivin on 2017/5/11.
 */

public abstract class BaseConfActivity extends BaseActivity {

    private static final String TAG = BaseConfActivity.class.getSimpleName();

    protected Context mContext;

    private AudioManager mAudioManager;

    private int mAudioManagerLastMode;

    private boolean mAudioManagerSpeakerphoneIsOn;

    public List<VideoCaptureDevInfo.VideoCaptureDevice> mVideoCaptureDevList;

    /**
     *本地支持分辨率
     */
    public List<String> mLocalSupportResolutionList = new ArrayList<>();

    /**
     * 最终选择的分辨率集合
     */
    private List<String> mFinalSupportList = new ArrayList<>();


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        EventBus.getDefault().register(this);

        //TODO:是否需要将activity加入堆栈中,这个操作是否有必要
        initAudioManager();
        initVideoDevList();

    }

    private void initVideoDevList() {
        VideoCaptureDevInfo videoCaptureDevInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
        if (videoCaptureDevInfo != null) {
            mVideoCaptureDevList = videoCaptureDevInfo.deviceList;
        } else {
            XviewLog.e(TAG, "VideoCaptureDevList is null");
            return;
        }

        VideoCaptureDevInfo.VideoCaptureDevice video = mVideoCaptureDevList.get(0);

        mLocalSupportResolutionList.clear();

        for (CaptureCapability cap : video.capabilites) {
            String resolution = cap.width + "X" + cap.height;
            mLocalSupportResolutionList.add(resolution);
        }

        mFinalSupportList.clear();
        mFinalSupportList.add(PublicInfo.Support4Level);
        mFinalSupportList.add(PublicInfo.Support3Level);
        mFinalSupportList.add(PublicInfo.Support2Level);
        mFinalSupportList.add(PublicInfo.Support1Level);
    }



    private void initAudioManager() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManagerLastMode = mAudioManager.getMode();
        mAudioManagerSpeakerphoneIsOn = mAudioManager.isSpeakerphoneOn();
        mAudioManager.setSpeakerphoneOn(true);
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    private void resetAudioManager() {
        mAudioManager.setSpeakerphoneOn(mAudioManagerSpeakerphoneIsOn);
        mAudioManager.setMode(mAudioManagerLastMode);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        resetAudioManager();
        super.onDestroy();
    }
}
