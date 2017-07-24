package com.cinlan.xview.ui.p2p.view;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.cinlan.jni.ConfRequest;
import com.cinlan.xview.bean.ClientDev;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.msg.EventConfMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.ui.callback.VideoOpenListener;
import com.cinlan.xview.ui.callback.VdOperatorResultCallback;
import com.cinlan.xview.ui.p2p.base.BaseConfActivity;
import com.cinlan.xview.utils.DensityUtil;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.cinlan.xview.ui.callback.VdOperatorResultCallback.OPERATOR_OK;

/**
 * 多人视频聊天界面
 * Created by Sivin on 2017/5/22.
 */
public class MultiActivity extends BaseConfActivity {

    private static final String TAG = "MultiActivity";

    /**
     * 会议实体对象
     */
    private Conf mConf;


    /**
     * 显示视频画面的Fragment
     */
    private MultiFragment mMultiFragment;


    /**
     * 挂断按钮
     */
    private ImageView mHangUpBtn;


    /**
     * 摄像头是否可用按钮
     */
    private ImageView mCameraAbleBtn;

    /**
     * 前后摄像头切换按钮
     */
    private ImageView mSwitchCameraBtn;

    /**
     * 语音是否可用
     */
    private boolean isSpeak = true;

    /**
     * 摄像头是否可用
     */
    private boolean isCameraAble = true;

    /**
     * 用于和fragment 进行交互用的
     */
    public VideoOpenListener mVideoOpenListener;


    private ImageView mUserBtn;


    private PopupWindow mUserPopWindow;


    private ImageView mHallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment();
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_multi_xviewsdk;
    }

    @Override
    protected ImageView setMuteBtn() {
        return (ImageView) findViewById(R.id.id_conf_micro);
    }


    private void addFragment() {
        mMultiFragment = new MultiFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.id_video_container, mMultiFragment);
        ft.commit();
        mVideoOpenListener = mMultiFragment;
    }

    private void init() {
        mConf = (Conf) getIntent().getSerializableExtra("conf");

        mUserBtn = (ImageView) findViewById(R.id.iv_conf_userlist_xviewsdk);

        mHallView = (ImageView) findViewById(R.id.ivEndConf_xviewsdk);

        ImageView changeImageBtn = (ImageView) findViewById(R.id.iv_conf_video_xviewsdk);

        changeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoOpenListener.changeCamera(2);
            }
        });

        mUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });

        mHallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

    }

    private void showPopWindow() {

        Log.e("tag", "showPopWindow");

        if (mUserPopWindow == null) {
            mUserPopWindow = new PlantWindow(this, DensityUtil.getScreenSize(this).x * 4 / 5, new ExpAdapter.PlantControlListener() {
                @Override
                public void onCloseVideo(Long userId, int pos) {
                    ClientDev clientDev = GlobalHolder.getInstance().findClientDev(userId);

                 mVideoOpenListener.closeVideo(userId,clientDev.getVideoDevLists().get(pos), false);
                }

                @Override
                public void onOpenVideo(Long userId, int pos) {
                    ClientDev clientDev = GlobalHolder.getInstance().findClientDev(userId);
                    mVideoOpenListener.openVideo(userId,clientDev,pos,false);
                }
            });
            mMultiFragment.setOpenCallBack((VdOperatorResultCallback) mUserPopWindow);

        }
        mUserPopWindow.showAtLocation(mHallView, Gravity.LEFT, 0, 0);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventCallBack(EventConfMsg msg) {
        switch (msg.getMsgType()) {
            case EventMsgType.ON_MEMBER_EXIT:
                long userId = msg.getExtra();
                ClientDev clientDev = GlobalHolder.getInstance().findClientDev(userId);

                if(clientDev == null) return;

                List<VideoDevice> vdList = clientDev.getAllOpenVdList();

                if(vdList != null) {
                    for(VideoDevice vd : vdList){
                        mVideoOpenListener.closeVideo(userId, vd ,true);
                    }
                }
                GlobalHolder.getInstance().deleteClient(userId);
                if(mUserPopWindow !=null){
                    ((VdOperatorResultCallback) mUserPopWindow).operatorCallback(OPERATOR_OK);
                }


                break;
            case EventMsgType.ON_USER_LOGOUT:
                ConfRequest.getInstance().exitConf(mConf.getId());
                finish();
                break;
        }
    }


    @Override
    public void onBackPressed() {
        showAlertDialog();
    }


}
