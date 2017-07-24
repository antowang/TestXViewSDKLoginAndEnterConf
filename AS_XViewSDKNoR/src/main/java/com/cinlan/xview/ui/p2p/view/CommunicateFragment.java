package com.cinlan.xview.ui.p2p.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cinlan.core.RemotePlayerManger;
import com.cinlan.jni.VideoRequest;
import com.cinlan.xview.bean.ClientDev;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.SurfaceWrap;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.msg.ClientEnterMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.msg.MediaEntity;
import com.cinlan.xview.ui.callback.VideoOpenListener;
import com.cinlan.xview.ui.callback.VdOperatorResultCallback;
import com.cinlan.xview.ui.p2p.base.BaseConfFragment;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.VideoHelper;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;

import static com.cinlan.xview.ui.callback.VdOperatorResultCallback.OPERATOR_OK;

/**
 * 视频通话,单聊fragment
 * Created by Sivin on 2017/5/11.
 */

public class CommunicateFragment extends BaseConfFragment implements VideoOpenListener {

    /**
     * 管理已经打开的surfaceView
     */
    protected LinkedList<SurfaceWrap> mOpenedSurfaceList = new LinkedList<>();


    protected VdOperatorResultCallback mOperatorCallBack;

    /**
     * 用户视频信息设备
     */
    protected List<UserDevice> mUserDeviceList;

    protected SurfaceWrap mWantToRmSf;

    protected SurfaceWrap mWantToShowSf;






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
        initScreenLayout();
    }

    private void initScreenLayout() {
        if (GlobalHolder.mClientDevList != null && GlobalHolder.mClientDevList.size() != 0) {
            for (ClientDev dev : GlobalHolder.mClientDevList) {
                openVideo(dev.getUser().getmUserId(), dev, -1, true);
            }
        }
    }

    /**
     * 当有新的用户进入会议时,底层回调给,eventBus转发
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewUserEnter(ClientEnterMsg ClientEnterMsg) {
        switch (ClientEnterMsg.getMsgType()) {
            case EventMsgType.ON_NEW_USER_ENTER:
                ClientDev dev;
                dev = ClientEnterMsg.getmDev();
                if (dev != null) {
                    openVideo(dev.getUser().getmUserId(), dev, -1, false);
                }
        }
    }

    /**
     * 打开视频设备,调用这个方法的地方一共有两个地方,
     * 1:父类打开本地视频设备, 2:当有远程设备接入的时候由eventBus回调
     */
    public void openVideo(long userId, ClientDev clientDev, int pos, boolean init) {

        //判断是否是预览视频
        boolean isLocal = userId == mLocalUserId;

        if (isLocal && mLocalHasOpen) return;

        if (!clientDev.isCanOpen()) return;

        VideoDevice vd;

        if (pos == -1) {
            vd = clientDev.getFirstEnableDev();
        } else {
            vd = clientDev.getVideoDevLists().get(pos);
        }
        if (vd == null) return;
        vd.setOpen(true);

        if(mOperatorCallBack != null){
            mOperatorCallBack.operatorCallback(OPERATOR_OK);
        }

        //它会实例化本地或远端的SurfaceView
        VideoHelper videoHelper = new VideoHelper(getActivity(), vd.getId(), isLocal);
        videoHelper.setUserid(userId);
        SurfaceView surfaceView = videoHelper.getView();

        int num = calcScreenNum(init);
        if (num < 3) {
            binaryLayout(videoHelper, surfaceView, userId, vd.getId(), num, isLocal);
        } else {
            mWantToShowSf = new SurfaceWrap(surfaceView,vd.getId());
            if (isLocal) {
                addCallbackForLocalSurface(surfaceView);
            } else {
                addCallbackForOtherSurface(surfaceView, userId, videoHelper);
            }
        }
    }


    /**
     * 计算分屏数
     *
     * @return 分屏数
     */
    public int calcScreenNum(boolean init) {
        return init ? GlobalHolder.getCanOpenClientNum()
                : mOpenedSurfaceList.size() + 1;
    }


    /**
     * 当人数少于3个人的时候 布局显示
     *
     * @param helper      helper
     * @param surfaceView surfaceView
     * @param userId      userId
     * @param isLocal     isLocal
     */
    private void binaryLayout(VideoHelper helper, SurfaceView surfaceView,
                              long userId, String vdId, int screenNum,
                              boolean isLocal) {
        //判断打开的是本地摄像头视频还是其他路视频.
        if (isLocal) {
            mLocalHasOpen = true;
            if (isVdHaveOpened(vdId)) return;
            previewLocalVideo(surfaceView, screenNum == 1);  // 预览本地视频,如果只有本地一个视频就全屏显示
            addOpenedSvf(vdId,surfaceView);
        } else {
            if (isVdHaveOpened(vdId)) return;
            addCallbackForOtherSurface(surfaceView, userId, helper);
            setRemoteSurface(surfaceView, true);
            addOpenedSvf(vdId,surfaceView);
        }
    }


    @Override
    public void closeVideo(long userId, VideoDevice vd,boolean isExit) {
        boolean isLocal = userId == GlobalHolder.getInstance().getLocalUserId();
        vd.setOpen(false);

        if(!isExit && mOperatorCallBack != null){
            mOperatorCallBack.operatorCallback(OPERATOR_OK);
        }

        VideoHelper videoHelper = new VideoHelper(getActivity(), vd.getId(), isLocal);
        mWantToRmSf =new SurfaceWrap(videoHelper.getView(),vd.getId()) ;
        videoHelper.setUserid(userId);
        if (isLocal) {
            mLocalHasOpen = false;
            VideoRequest.getInstance().closeVideoDevice(mConfId,
                    videoHelper.getUserid(), "", null, 1);
        } else {


            VideoRequest.getInstance().closeVideoDevice(mConfId,
                    videoHelper.getUserid(), videoHelper.getSzDevid(),
                    videoHelper.getVideoPlayer(), 1);
        }

        if (mOpenedSurfaceList.size() < 3) {
            rmSfFromLayout(vd.getId(), mWantToRmSf, mOpenedSurfaceList.size());
        }

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

        Log.e("sivin", "addCallbackForOtherSurface: " + surfaceView);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        mOpenedSurfaceList.clear();
        RemotePlayerManger.getInstance().removeAllRemoteSurfaceView();
    }


    @Override
    protected void removeOpenedVd(String vdId, SurfaceView surfaceView) {
        for (int i = 0; i < mOpenedSurfaceList.size(); i++) {
            if (mOpenedSurfaceList.get(i).getVideoId().equals(vdId)) {
                mOpenedSurfaceList.remove(i);
            }
        }
    }

    /**
     * 判断这个设备是否已经打开
     *
     * @param vdId vdId
     * @return true 存在, false 不存在
     */
    public boolean isVdHaveOpened(String vdId) {
        for (SurfaceWrap surface : mOpenedSurfaceList) {
            if (surface.getVideoId().equals(vdId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加已经打开的设备
     * @param vdId vdId
     * @param sfv sfv
     */
    public void addOpenedSvf(String vdId, SurfaceView sfv) {
        mOpenedSurfaceList.add(new SurfaceWrap(sfv, vdId));
    }
}
