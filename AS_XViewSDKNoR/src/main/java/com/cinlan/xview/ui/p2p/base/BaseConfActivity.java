package com.cinlan.xview.ui.p2p.base;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cinlan.core.CaptureCapability;
import com.cinlan.core.LocaSurfaceView;
import com.cinlan.core.VideoCaptureDevInfo;
import com.cinlan.jni.ConfRequest;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.framework.BaseActivity;
import com.cinlan.xview.msg.EventConfMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.XviewLog;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
     * 本地支持分辨率
     */
    public List<String> mLocalSupportResolutionList = new ArrayList<>();

    /**
     * 最终选择的分辨率集合
     */
    private List<String> mFinalSupportList = new ArrayList<>();


    /**
     * 语音是否可用
     */
    private boolean isSpeak = true;
    /**
     * 静音按钮
     */
    private ImageView mMuteBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(getLayoutId());


        //TODO:是否需要将activity加入堆栈中,这个操作是否有必要
        initAudioManager();
        initVideoDevList();
        initView();
    }

    protected abstract int getLayoutId();

    private int cnt = 0;
    private void initView() {

        mMuteBtn = setMuteBtn();

        Log.e(TAG, "initView: " + mMuteBtn);
        if (mMuteBtn != null) {

            mMuteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //switchSpeak();
                    LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance().getVideoConfig();
                    if (cnt++ % 2 == 0) {
                        config.videoWidth = 720;
                        config.videoHeight = 1280;
                    } else {
                        config.videoWidth = 448;
                        config.videoHeight = 800;
                    }
                    LocaSurfaceView.getInstance().setVideoConfig(config);
                }
            });
        }
    }

    /**
     * 设置静音按钮
     *
     * @return ImageView
     */
    protected abstract ImageView setMuteBtn();


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
        release();
        super.onDestroy();
    }


    /**
     * 静音切换
     */
    protected void switchSpeak() {
        if (isSpeak) {
            ConfRequest.getInstance().releaseControlPermission(3);
            mMuteBtn.setImageResource(R.drawable.conf_micro_selector_xviewsdk);
            isSpeak = false;
        } else {
            ConfRequest.getInstance().applyForControlPermission(3);
            mMuteBtn.setImageResource(R.drawable.conf_micro_fayan_selector_xviewsdk);
            isSpeak = true;
        }
    }


    private void release() {
        GlobalHolder.mOpenUerDevList.clear();
        GlobalHolder.getInstance().mOpenMedia.clear();
        GlobalHolder.getInstance().list.clear();
        GlobalHolder.getInstance().mSpeakUers.clear();
        GlobalHolder.getInstance().mUers.clear();
        GlobalHolder.getInstance().pages.clear();
        GlobalHolder.getInstance().userdevices.clear();
        GlobalHolder.videodevices.clear();
        GlobalHolder.getInstance().mDocShares.clear();
        PublicInfo.confListRefreshHandler
                .sendEmptyMessage(PublicInfo.UNREGIStER_RECEIVER);
    }
}
