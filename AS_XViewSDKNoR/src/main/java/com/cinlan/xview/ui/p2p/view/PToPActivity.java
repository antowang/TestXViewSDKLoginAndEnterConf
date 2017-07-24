package com.cinlan.xview.ui.p2p.view;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cinlan.jni.ConfRequest;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.msg.EventConfMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.ui.callback.VideoOpenListener;
import com.cinlan.xview.ui.p2p.base.BaseConfActivity;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 单聊界面
 * Created by Sivin on 2017/5/11.
 */

public class PToPActivity extends BaseConfActivity implements View.OnClickListener {

    private static final String TAG = "PToPActivity";

    /**
     * 会议实体对象
     */
    private Conf mConf;


    /**
     * 摄像头是否可用
     */
    private boolean isCameraAble = true;

    /**
     * 视频设备监听器
     */
    public VideoOpenListener mVideoOpenListener;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_ptop_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment();
        init();
    }




    @Override
    protected ImageView setMuteBtn() {
        return (ImageView) findViewById(R.id.mute_btn);
    }


    private void addFragment() {
        /*
      显示视频画面的Fragment
     */
        CommunicateFragment mCommunicateFragment = new CommunicateFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.id_video_container, mCommunicateFragment);
        ft.commit();
        mVideoOpenListener = mCommunicateFragment;
    }


    private void init() {
        mConf = (Conf) getIntent().getSerializableExtra("conf");

        /*挂断按钮*/
        ImageView mHangUpBtn = (ImageView) findViewById(R.id.id_hang_up_btn);
        mHangUpBtn.setOnClickListener(this);


        /*前后摄像头切换按钮*/
        ImageView mSwitchCameraBtn = (ImageView) findViewById(R.id.id_capture_change_btn);
        mSwitchCameraBtn.setOnClickListener(this);

        /*摄像头是否可用按钮*/
        ImageView mCameraAbleBtn = (ImageView) findViewById(R.id.id_show_close_video_btn);
        mCameraAbleBtn.setOnClickListener(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNativeCallback(EventConfMsg msg) {
        Log.e(TAG, "eventBus callback: " + msg.getMsgType());

        switch (msg.getMsgType()) {
            case EventMsgType.ON_USER_LOGOUT:
                ConfRequest.getInstance().exitConf(mConf.getId());
                finish();
                break;
            case EventMsgType.ON_MEMBER_EXIT:
                PublicInfo.logout(mContext);
                break;
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.id_hang_up_btn) {
            showAlertDialog();
        } else if (i == R.id.id_capture_change_btn) {
            mVideoOpenListener.changeCamera(2);
        } else if (i == R.id.id_show_close_video_btn) {
            if (isCameraAble) {
                mVideoOpenListener.changeCamera(3);
                isCameraAble = false;
            } else {
                mVideoOpenListener.changeCamera(4);
                isCameraAble = true;
            }
        }
    }


    @Override
    public void onBackPressed() {
        showAlertDialog();
    }


    @Override
    protected void onDestroy() {

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
        super.onDestroy();
    }


}
