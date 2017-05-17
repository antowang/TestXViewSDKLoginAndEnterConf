package com.cinlan.xview.ui.p2p.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.cinlan.jni.VideoRequest;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.msg.EventConfMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.msg.MediaEntity;
import com.cinlan.xview.ui.callback.VideoOpenListener;
import com.cinlan.xview.ui.p2p.base.BaseCommunicateFragment2;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.VideoHelper;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频通话,聊天界面
 * Created by Sivin on 2017/5/11.
 */

public class CommunicateFragment extends BaseCommunicateFragment2 implements VideoOpenListener {

    private static final String TAG = "CommunicateFragment";

    private Map<String, SurfaceView> mRemoteSurfaceViewList = new HashMap<>();


    /**
     * 本地的用户Id;
     */
    private long mLocalUserId;


    /**
     * 用户视频信息设备
     */
    private List<UserDevice> mUserDeviceList;


    /**
     * 记录已经有远端设备全屏了,新来的不用全屏了
     */
    private boolean mNoRemoteDevFull = true;

    @Override
    protected RelativeLayout getContentView(LayoutInflater inflater, ViewGroup container) {
        return (RelativeLayout) inflater.inflate(R.layout.fragment_communicate_layout, container, false);
    }

    @Override
    protected long getConfId() {
        //会议的Id应该是进入这个界面由Intent传递过来的
        Conf mConf = (Conf) getActivity().getIntent().getSerializableExtra("conf");
        return mConf == null ? -1 : mConf.getId();
    }


    @Override
    protected void checkHaveUserDevList() {
         /*
         * 因为,当用户调用底层进入会议接口,
         * 底层就有可能回调返回远程用户设备信息,
         * 此时可能eventBus还没有注册,接收不到有用户进来
         * 因此需要将之前进入的远程用户信息便利一遍,当界面eventBus注册
         * 成功之后,就可以根据eventBus回调触发事件了
         */
        mUserDeviceList = GlobalHolder.getInstance().getUserDevice();
        tryToOpenHaveEnterMemberDev();
    }

    private void tryToOpenHaveEnterMemberDev() {
        if (mUserDeviceList != null && mUserDeviceList.size() != 0) {
            for (UserDevice ud : mUserDeviceList) {
                openVideo(ud);
            }


        }
    }


    @Override
    public void closeVideo(UserDevice u) {

    }


    /**
     * 当有新的用户进入会议时,底层回调给,eventBus转发
     * {@link }
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewUserEnter(EventConfMsg eventMsg) {

        switch (eventMsg.getMsgType()) {

            case EventMsgType.ON_NEW_USER_ENTER:
               /*
                * 当有新的用户加入会议,其实用户的设备信息可以直接传递过来
                * 但是考虑性能,决定从资源缓冲池中去取.将每一个刚来的用户
                * 都放到集合的最后一个
                */
                mUserDeviceList = GlobalHolder.getInstance().getUserDevice();

                Log.e(TAG, "onNewUserEnter: ");

                UserDevice ud = mUserDeviceList.get(mUserDeviceList.size() - 1);
                openVideo(ud);
                break;
        }
    }


    /**
     * 打开视频设备,调用这个方法的地方一共有两个地方,
     * 1:父类打开本地视频设备, 2:当有远程设备接入的时候由eventBus回调
     *
     * @param userDevice userDevice
     */
    @Override
    public void openVideo(UserDevice userDevice) {
        if (userDevice == null)
            return;

        // 获取视频设备
        VideoDevice device = userDevice.getDevice();

        // 获取用户
        User user = userDevice.getUser();
        if (device == null || user == null)
            return;

        // 判断打开的是自己还是远程
        boolean isLocal = user.getmUserId() == GlobalHolder.getInstance().getLocalUserId();


        if (isLocal && mLocalVideoDevHasOpen) {  //集合中存放了本地视频设备,此处判断,避免重复打开
            return;
        }


        // 它会实例化本地或远端的SurfaceView
        VideoHelper videoHelper = new VideoHelper(getActivity(), device.getId(), isLocal);

        // 拿到SurfaceView
        SurfaceView surfaceView = videoHelper.getView();
        videoHelper.setUserid(user.getmUserId());

        /*
         * 判断打开的是本地摄像头视频还是其他路视频.
         */
        if (isLocal) {

            // 预览本地视频,如果只有本地一个视频就全屏显示
            Log.e(TAG, "openVideo: " + mUserDeviceList.size());
            previewLocalVideo(surfaceView, mUserDeviceList.size() == 1);
        } else {

            if (mRemoteSurfaceViewList.get(user.getmUserId() + "") != null) {
                return;
            }
            //当打开远端新的视频设备的时候,默认是第一个远端视频设备全屏显示
            //剩下的都是小视频显示
            setRemoteSurface(surfaceView, mNoRemoteDevFull);
            mNoRemoteDevFull = false;
            addCallbackForOtherSurface(surfaceView, user.getmUserId(), videoHelper);

            //TODO:以后要用到
            mRemoteSurfaceViewList.put(user.getmUserId() + "", surfaceView);
        }
    }


    @Override
    public void changeCamera(int i) {

    }

    @Override
    public void applySetting(int width, int height, int videoFlow, int frameRate, int format, int requestedOrientation, boolean enabeleFrontCam) {

    }

    @Override
    public void openMedia(MediaEntity m) {

    }

    @Override
    public void closeMedia(MediaEntity m) {

    }


    private void addCallbackForOtherSurface(SurfaceView surfaceView,
                                            final long userId,
                                            final VideoHelper videoHelper) {

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                VideoRequest.getInstance().closeVideoDevice(mConfId,
                        userId,
                        videoHelper.getSzDevid(),
                        videoHelper.getVideoPlayer(), 1);
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                VideoRequest.getInstance().openVideoDevice(mConfId,
                        userId,
                        videoHelper.getSzDevid(),
                        videoHelper.getVideoPlayer(),
                        1);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                holder.setFixedSize(width, height);
            }
        });
    }


}
