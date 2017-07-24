package com.cinlan.xview.ui.p2p.base;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cinlan.core.LocaSurfaceView;
import com.cinlan.core.VideoCaptureDevInfo;
import com.cinlan.jni.ConfRequest;
import com.cinlan.jni.VideoRequest;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.bean.SurfaceWrap;
import com.cinlan.xview.utils.DensityUtil;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频播放fragment基础类
 * Created by Sivin on 2017/5/11.
 */

public abstract class BaseConfFragment extends Fragment {

//    private static final String TAG = "BaseConfFragment";

    protected Long mLocalUserId;

    protected GlobalHolder mHolder;

    protected RelativeLayout mRootView;



    /**
     * 会议的id
     */
    protected long mConfId;

    /**
     * 根据设备智能选取的4个级别的分辨率.
     */
    private List<String> mAppSupportResolutionList = new ArrayList<>();


    protected boolean mLocalHasOpen = false;

    /**
     * 记录屏幕尺寸
     */
    protected Point mScreenSize;

    private SurfaceView mCurrentFullView;
    private SurfaceView mCurrentSmallView;
    private RelativeLayout mFullFrameLayout;
    private RelativeLayout mSmallFrameLayout;


    private VideoCaptureDevInfo mDevInfo;

    /**
     * 记录当前摄像头是前置摄像头还是后置摄像头
     */
    private int mCameraToward;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = getContentView(inflater, container);
        init();
        return mRootView;
    }

    protected void init() {
        EventBus.getDefault().register(this);
        mScreenSize = DensityUtil.getScreenSize(getActivity());
        mConfId = getConfId();
        mHolder = GlobalHolder.getInstance();
        mLocalUserId = mHolder.getLocalUserId();

        mFullFrameLayout = (RelativeLayout) mRootView.findViewById(R.id.full_view);
        mSmallFrameLayout = (RelativeLayout) mRootView.findViewById(R.id.small_view);
        mSmallFrameLayout.setTranslationX(mScreenSize.x / 3 * 2);
        mSmallFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentSmallView != null)
                    switchLayout();
            }
        });

        //初始化本地设备信息
        initLocalClientDev();

        checkHaveUserDevList();
    }

    protected abstract RelativeLayout getContentView(LayoutInflater inflater, ViewGroup container);

    /**
     * 保证这个ConfId一定被子类进行复值处理
     *
     * @return confId
     */
    protected abstract long getConfId();

    /**
     * 打开本地预览视频和远端视频
     */
    private void initLocalClientDev() {
        //// 设备朝向?什么意思
//        PublicInfo.DEVICE_ORIENTATION = SPUtil.getConfigIntValue(getActivity(), "viewModePosition", 1);
        mDevInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();

        List<VideoCaptureDevInfo.VideoCaptureDevice> deviceList = mDevInfo.deviceList;

        if (deviceList == null || deviceList.size() == 0) return;

        initLocalSupport();

        // 摄像头朝向
        mCameraToward = mDevInfo.deviceList.size() >= 2 ? 0 : 1;
        // 码率
        int malv = SPUtil.getConfigIntValue(getActivity(), "ml", 800 * 1024);
        // 帧率
        int zelv = SPUtil.getConfigIntValue(getActivity(), "zl", 15);
        // 分辨率
        String default_video_wh = SPUtil.getConfigStrValue(getActivity(), "chicun");

		//如果分辨率为null或不为4个分辨率之一就默认为高清
        if (default_video_wh.isEmpty() || !mAppSupportResolutionList.contains(default_video_wh)) {
            default_video_wh = "448X800";
        }

        String[] whArray = default_video_wh.split("X");

        //TODO:不知道什么意思?
        VideoRequest.getInstance().enumMyVideos(0);
        if (deviceList.get(0).deviceUniqueName != null) {
            //TODO:这个方法底层调用有什么作用?
            VideoRequest.getInstance().setDefaultVideoDev(deviceList.get(0).deviceUniqueName);
            // 设置默认的设备名称
            mDevInfo.SetDefaultDevName(mDevInfo.deviceList.get(0).deviceUniqueName);
            // 设置采集参数
            LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance().getVideoConfig();
            config.videoWidth = Integer.parseInt(whArray[0]);
            config.videoHeight = Integer.parseInt(whArray[1]);
            config.videoBitRate = malv;
            config.videoFrameRate = zelv;
            config.videoMaxKeyframeInterval = zelv * 2;
            config.enabeleFrontCam = mCameraToward == 0;
            LocaSurfaceView.getInstance().setVideoConfig(config);
            //TODO:保存这个值干啥的?
            SPUtil.putConfigIntValue(getActivity(), "ccindex", 0);
        }
    }



    protected abstract void checkHaveUserDevList();


    /**
     * 初始化本地支持分辨率集合
     */
    private void initLocalSupport() {
        mAppSupportResolutionList.clear();
        mAppSupportResolutionList.add(PublicInfo.Support4Level);
        mAppSupportResolutionList.add(PublicInfo.Support3Level);
        mAppSupportResolutionList.add(PublicInfo.Support2Level);
        mAppSupportResolutionList.add(PublicInfo.Support1Level);
    }


    /**
     * 预览本地视频
     *
     * @param surfaceView  surfaceView
     * @param isFullScreen 是否是全屏显示
     */
    protected void previewLocalVideo(SurfaceView surfaceView, boolean isFullScreen) {
        if (isFullScreen){
            setSurfaceFull(surfaceView);
        }
        else {
            mCurrentSmallView = surfaceView;

            if(mCurrentFullView != null){
                mCurrentSmallView.setZOrderOnTop(true);
                mCurrentSmallView.setZOrderMediaOverlay(true);
                setPipShow(mCurrentSmallView);
            }
        }

        // 监听已打开的本地的surface
        addCallbackForLocalSurface(surfaceView);
    }


    /**
     * 设置surfaceView全屏显示
     *
     * @param surfaceView surfaceView
     */
    private void setSurfaceFull(SurfaceView surfaceView) {
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(params);
        mFullFrameLayout.addView(surfaceView);
        surfaceView.setTranslationX(0);
        surfaceView.setTranslationY(0);
        mCurrentFullView = surfaceView;
    }

    /**
     * 设置surfaceView画中画显示
     *
     * @param surfaceView surfaceView
     */
    private void setPipShow(SurfaceView surfaceView) {
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = mScreenSize.x / 3;
        params.height = mScreenSize.y / 3;
        surfaceView.setLayoutParams(params);
        mSmallFrameLayout.addView(surfaceView);
        mCurrentSmallView = surfaceView;

    }


    protected void setRemoteSurface(SurfaceView surfaceView, boolean isFull) {

        if (isFull) { //本地是否有全屏的视频
            if (mCurrentFullView != null) {
                //从大屏移除掉,到小屏幕里
                switchLayout();
                setSurfaceFull(surfaceView);
            } else {
                //直接添加到大屏里
                setSurfaceFull(surfaceView);
                if(mCurrentSmallView != null){
                    mCurrentSmallView.setZOrderOnTop(true);
                    mCurrentSmallView.setZOrderMediaOverlay(true);
                    setPipShow(mCurrentSmallView);
                }
            }
        }
    }


    /**
     * 大小屏切换
     */
    private void switchLayout() {

        SurfaceView smallView = null;
        if (mSmallFrameLayout.getChildCount() > 0) {
            smallView = (SurfaceView) mSmallFrameLayout.getChildAt(0);
            mSmallFrameLayout.removeViewAt(0);
        }


        if (smallView != null) {
            smallView.setZOrderOnTop(false);
            setSurfaceFull(smallView);
        }


        SurfaceView fullView = null;
        if (mFullFrameLayout.getChildCount() > 0) {
            fullView = (SurfaceView) mFullFrameLayout.getChildAt(0);
            mFullFrameLayout.removeViewAt(0);
        }

        if (fullView != null) {
            fullView.setZOrderOnTop(true);
            fullView.setZOrderMediaOverlay(true);
            setPipShow(fullView);
        }

    }


    /**
     * 本地预览SurfaceView监听
     *
     * @param surfaceView surfaceView
     */
    protected void addCallbackForLocalSurface(SurfaceView surfaceView) {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

                VideoRequest.getInstance().closeVideoDevice(mConfId,
                        GlobalHolder.getInstance().getLocalUserId(), "", null, 1);

                LocaSurfaceView.getInstance().setbPreview(false);
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                VideoRequest.getInstance()
                        .openVideoDevice(mConfId,
                                GlobalHolder.getInstance().getLocalUserId(), "",
                                null, 1);

                LocaSurfaceView.getInstance().setbPreview(true);
                //开启说话权限
                ConfRequest.getInstance().applyForControlPermission(3);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                holder.setFixedSize(width, height);
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mFullFrameLayout.removeAllViews();
        mSmallFrameLayout.removeAllViews();
        mRootView.removeAllViews();
        mLocalHasOpen = false;
        GlobalHolder.getInstance().release();
        super.onDestroy();
    }


    public void changeCamera(int camera) {
        // 后面添加一个 ":Camera"不知道是什么,目前解释是固定格式:
        String deviceId = GlobalHolder.getInstance().getLocalUserId() + ":Camera";
        // 摄像头状态
        int cameraStatus = SPUtil.getConfigIntValue(getActivity(), "local", 0);

        switch (camera) {
            case 2:  //前后摄像头切换
                if (cameraStatus == 1
                        && mDevInfo != null
                        && mDevInfo.deviceList != null
                        && mDevInfo.deviceList.size() == 1) {
                    // 只有一个摄像头，不能切换
                    Toast.makeText(getActivity(), R.string.onecamera_xviewsdk, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mCameraToward == 0) {
                    SPUtil.putConfigIntValue(getActivity(), "camera", 1);
                    mCameraToward = 1;
                } else {
                    SPUtil.putConfigIntValue(getActivity(), "camera", 0);
                    mCameraToward = 0;
                }

                LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance().getVideoConfig();
                config.enabeleFrontCam = !config.enabeleFrontCam;
                LocaSurfaceView.getInstance().setVideoConfig(config);
                break;

            case 3: //禁用摄像头
                VideoRequest.getInstance().setVideoDevDisable(deviceId, true);
                SPUtil.putConfigIntValue(getActivity(), "local", 4);
                break;

            case 4://打开摄像头
                VideoRequest.getInstance().setVideoDevDisable(deviceId, false);
                SPUtil.putConfigIntValue(getActivity(), "local", 1);
                break;
        }
    }


    /**
     * 移除全屏的surfaceView
     *
     * @return surfaceView
     */
    public SurfaceView removeFullSurfaceView() {
        SurfaceView surfaceView = (SurfaceView) mFullFrameLayout.getChildAt(0);
        mFullFrameLayout.removeAllViews();
        return surfaceView;
    }


    /**
     * 移除小的surfaceView
     *
     * @return surfaceView
     */
    public SurfaceView removeSmallSurfaceView() {
        SurfaceView surfaceView = (SurfaceView) mSmallFrameLayout.getChildAt(0);
        mSmallFrameLayout.removeAllViews();
        return surfaceView;
    }


    /**
     * 隐藏画中画视频父布局文件
     */
    public void pipParentLayoutGone() {
        mFullFrameLayout.setVisibility(View.GONE);
        mFullFrameLayout.setVisibility(View.GONE);
    }


    /**
     * 将surfaceView从布局中移除
     *
     * @param wrap 要移除的surfaceView
     * @param num         当前正在显示的surfaceView的个数
     */
    protected void rmSfFromLayout(String vdId, SurfaceWrap wrap, int num) {

        SurfaceView surfaceView = wrap.getSuf();

        if (num == 1) {
            mFullFrameLayout.removeAllViews();
            removeOpenedVd(vdId,surfaceView);

        } else if (num == 2) {

            if (surfaceView == mCurrentSmallView) {
                mSmallFrameLayout.removeView(surfaceView);
                removeOpenedVd(vdId,surfaceView);
                mCurrentSmallView =null;

            } else if (surfaceView == mCurrentFullView) {
                mSmallFrameLayout.removeAllViews();
                mFullFrameLayout.removeAllViews();
                removeOpenedVd(vdId,surfaceView);
                setSurfaceFull(mCurrentSmallView);
                mCurrentSmallView = null;
            }
        }
    }


    /**
     * 删除surfaceViewList的集合
     *
     * @param surfaceView surfaceView
     */
    protected abstract void removeOpenedVd(String vdId, SurfaceView surfaceView);

    /**
     * 设置画中画布局视频
     * @param full  全屏surfaceView
     * @param small 小屏surfaceView
     */
    public void switchToPip(SurfaceView full, SurfaceView small) {
        mRootView.addView(mFullFrameLayout);
        mRootView.addView(mSmallFrameLayout);
        full.setZOrderOnTop(false);
        setSurfaceFull(full);

        small.setZOrderOnTop(true);
        small.setZOrderMediaOverlay(true);

        setPipShow(small);
        switchLayout();
    }

}
