package com.cinlan.xview.ui.p2p.base;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
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
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.utils.DensityUtil;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sivin on 2017/5/11.
 */

public abstract class BaseCommunicateFragment2 extends Fragment {

    private static final String TAG = "BaseCommunicateFragment";

    protected RelativeLayout mRootView;

    /**
     * 会议的id
     */
    protected long mConfId;

    /**
     * 根据设备智能选取的4个级别的分辨率.
     */
    private List<String> mAppSupportResolutionList = new ArrayList<>();


    protected boolean mLocalVideoDevHasOpen = false;

    /**
     * 记录屏幕尺寸
     */
    protected Point mScreenSize;


    /**
     * 上一次点击的时间,用于防止动画还未结束之前
     * 重复点击操作
     */
    private long mLastPreformAnimTime;


    /**
     * 当前正在全屏的SurfaceView
     */
    private SurfaceView mCurrentFullView;

    /**
     * 当前小屏的surfaceView
     */
    private SurfaceView mCurrentSamllView;

    private RelativeLayout mFullFrameLayout;

    private RelativeLayout mSmallFrameLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = getContentView(inflater, container);
        init();
        return mRootView;
    }

    protected void init() {
        EventBus.getDefault().register(this);
        mScreenSize = DensityUtil.getScreenSize(getActivity());

        mFullFrameLayout = (RelativeLayout) mRootView.findViewById(R.id.full_view);
        mSmallFrameLayout = (RelativeLayout) mRootView.findViewById(R.id.small_view);

        mSmallFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "开始切换视频", Toast.LENGTH_SHORT).show();
                switchLayout();
            }
        });

        //TODO:这个会议的Id如果错误会有什么影响?
        mConfId = getConfId();
        startOpenDev();
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
    private void startOpenDev() {
        //// 设备朝向?什么意思
        //TODO:这个值在什么时候保存的
        int orientation = SPUtil.getConfigIntValue(getActivity(), "viewModePosition", 1);

        PublicInfo.DEVICE_ORIENTATION = orientation;

        VideoCaptureDevInfo devInfo = VideoCaptureDevInfo.CreateVideoCaptureDevInfo();
        List<VideoCaptureDevInfo.VideoCaptureDevice> deviceList = devInfo.deviceList;

        if (deviceList == null || deviceList.size() == 0) return;

        initLocalSupport();

        // 摄像头朝向
        int camera = SPUtil.getConfigIntValue(getActivity(), "camera", 0);
        // 码率
        int malv = SPUtil.getConfigIntValue(getActivity(), "ml", 70 * 1024);
        // 帧率
        int zelv = SPUtil.getConfigIntValue(getActivity(), "zl", 15);
        // 分辨率
        String default_video_wh = SPUtil.getConfigStrValue(getActivity(), "chicun");

		/* 如果分辨率为null或不为4个分辨率之一就默认为高清 */
        if (default_video_wh.isEmpty() || !mAppSupportResolutionList.contains(default_video_wh)) {
            default_video_wh = PublicInfo.Support2Level;
        }

        String[] whArray = default_video_wh.split("X");

        //TODO:不知道什么意思?
        VideoRequest.getInstance().enumMyVideos(0);


        if (deviceList.get(0).deviceUniqueName != null) {

            //TODO:这个方法底层调用有什么作用?
            VideoRequest.getInstance().setDefaultVideoDev(deviceList.get(0).deviceUniqueName);

            // 设置默认的设备名称
            devInfo.SetDefaultDevName(devInfo.deviceList.get(0).deviceUniqueName);
            // 设置采集参数
            LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance().getVideoConfig();

            config.videoWidth = Integer.parseInt(whArray[0]);
            config.videoHeight = Integer.parseInt(whArray[1]);
            config.videoBitRate = malv;
            config.videoFrameRate = zelv;
            config.videoMaxKeyframeInterval = zelv * 2;
            config.enabeleFrontCam = camera == 0;
            LocaSurfaceView.getInstance().setVideoConfig(config);

            //TODO:保存这个值干啥的?
            SPUtil.putConfigIntValue(getActivity(), "ccindex", 0);

            //获取到当前用户的视频设备
            UserDevice userDevice = GlobalHolder.currendUserDevice;
            //同时将打开的用户视频设备添加到集合中
            GlobalHolder.mOpenUerDevList.add(userDevice);
            checkHaveUserDevList();
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

        Log.e(TAG, "previewLocalVideo: " + isFullScreen);

        mLocalVideoDevHasOpen = true;
        if (isFullScreen)
            setSurfaceFull(surfaceView);
        else
            setPipShow(surfaceView);

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
        mCurrentFullView = surfaceView;
    }

    /**
     * 设置surfaceView画中画显示
     *
     * @param surfaceView surfaceView
     */
    protected void setPipShow(SurfaceView surfaceView) {
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = mScreenSize.x / 3;
        params.height = mScreenSize.y / 3;
        surfaceView.setLayoutParams(params);

        mSmallFrameLayout.addView(surfaceView);
        mSmallFrameLayout.setTranslationX(mScreenSize.x / 3 * 2);
        mCurrentSamllView = surfaceView;
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
            }
        } else { //远端视频添加到小屏容器里

        }
    }


    /**
     * 大小屏切换
     */
    private void switchLayout() {
        SurfaceView fullView = null;


        if (mFullFrameLayout.getChildCount() > 0) {
            fullView = (SurfaceView) mFullFrameLayout.getChildAt(0);
            mFullFrameLayout.removeViewAt(0);
        }
        SurfaceView smallView = null;
        if (mSmallFrameLayout.getChildCount() > 0) {
            smallView = (SurfaceView) mSmallFrameLayout.getChildAt(0);
            mSmallFrameLayout.removeViewAt(0);
        }
        if (fullView != null)
            setPipShow(fullView);
        if (smallView != null)
            setSurfaceFull(smallView);
    }


    /**
     * 本地预览SurfaceView监听
     *
     * @param surfaceView surfaceView
     */
    private void addCallbackForLocalSurface(SurfaceView surfaceView) {
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
        super.onDestroy();
    }
}
