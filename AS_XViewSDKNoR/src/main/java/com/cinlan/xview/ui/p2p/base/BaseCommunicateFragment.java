package com.cinlan.xview.ui.p2p.base;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cinlan.core.LocaSurfaceView;
import com.cinlan.core.VideoCaptureDevInfo;
import com.cinlan.jni.ConfRequest;
import com.cinlan.jni.VideoRequest;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.utils.DensityUtil;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sivin on 2017/5/11.
 */

public abstract class BaseCommunicateFragment extends Fragment {

    private static final String TAG = "BaseCommunicateFragment";

    protected FrameLayout mRootView;

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
     * 小窗口放大前的坐标信息
     */
    private Point mLastSmallWindowPoint = new Point();


    /**
     * 本机预览界面的SurfaceView
     */
    private SurfaceView mLocalSurfaceView;


    /**
     * 上一次点击的时间,用于防止动画还未结束之前
     * 重复点击操作
     */
    private long mLastPreformAnimTime;


    /**
     * 当前正在全屏的SurfaceView
     */
    private SurfaceView mCurrentFullScreenView;

    /**
     * 将要放大全屏的SurfaceView
     */
    private SurfaceView mToAmplifySurfaceView;


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

        //TODO:这个会议的Id如果错误会有什么影响?
        mConfId = getConfId();
        startOpenDev();
    }

    protected abstract FrameLayout getContentView(LayoutInflater inflater, ViewGroup container);

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
     * @param surfaceView surfaceView
     * @param isFullScreen 是否是全屏显示
     */
    protected void previewLocalVideo(SurfaceView surfaceView , boolean isFullScreen) {
        mLocalSurfaceView = surfaceView;
        mLocalVideoDevHasOpen = true;
        if (isFullScreen)
            setSurfaceViewFullScreen(surfaceView);
        else
            setPipLocation(surfaceView,mScreenSize.x / 3 * 2 , 0);

        // 监听已打开的本地的surface
        addCallbackForLocalSurface(surfaceView);
    }

    /**
     * 设置surfaceView全屏显示
     * @param surfaceView surfaceView
     */
    private void setSurfaceViewFullScreen(SurfaceView surfaceView) {

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        surfaceView.setLayoutParams(params);

        surfaceView.setZOrderMediaOverlay(true);

        mRootView.addView(surfaceView);
        setClickListener(surfaceView);
        mCurrentFullScreenView = surfaceView;
    }

    protected void setRemoteSurfaceViewFullScreen(SurfaceView surfaceView){

        Log.e(TAG, "setRemoteSurfaceViewFullScreen: " );

        setShrinkSurfaceView(mCurrentFullScreenView,mScreenSize.x / 3 * 2, 0);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        surfaceView.setLayoutParams(params);


        mRootView.addView(surfaceView);

        mCurrentFullScreenView = surfaceView;

        setClickListener(surfaceView);
    }

    /**
     * 缩小view,同时移动到指定的位置
     * @param surfaceView 要缩小的View
     * @param translationX x坐标
     * @param translationY y坐标
     */
    private void setShrinkSurfaceView(SurfaceView surfaceView ,int translationX, int translationY) {
        if(surfaceView == null){
            return;
        }
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        params.height = mScreenSize.y / 3;
        params.width = mScreenSize.x / 3;

        surfaceView.setLayoutParams(params);
        surfaceView.setTranslationX(translationX);
        surfaceView.setTranslationY(translationY);
    }


    /**
     * 设置surfaceView画中画显示
     * @param surfaceView surfaceView
     */
    protected void setPipLocation(SurfaceView surfaceView,int transationX, int transationY) {

        Log.e(TAG, "setPipLocation: " );

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(mScreenSize.x / 3, mScreenSize.y / 3);
        surfaceView.setLayoutParams(params);

        surfaceView.setZOrderOnTop(true);
        surfaceView.setZOrderMediaOverlay(true);
        mRootView.addView(surfaceView);

        surfaceView.setTranslationX(transationX);
        surfaceView.setTranslationY(transationY);
        setClickListener(surfaceView);
    }


    /**
     * 设置放大监听
     *
     * @param surfaceView surfaceView
     */
    protected void setClickListener(final SurfaceView surfaceView) {

        Log.e(TAG, "setClickListener: "+surfaceView);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: "+v );
                magnifySurfaceView((SurfaceView) v);
            }
        });
    }


    /**
     * 本地预览SurfaceView监听
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


    /**
     * 缩小surfaceView,同时
     *
     */
    private void shrinkSurfaceView() {
        if(mCurrentFullScreenView == null){
            return;
        }
        mRootView.removeView(mCurrentFullScreenView);

        mCurrentFullScreenView.setZOrderOnTop(true);

        mRootView.addView(mCurrentFullScreenView);
        setShrinkSurfaceView(mCurrentFullScreenView,mLastSmallWindowPoint.x, mLastSmallWindowPoint.y);
        mCurrentFullScreenView = mToAmplifySurfaceView;
    }


    /**
     * 全屏放大处理
     * @param toAmplifySurfaceView surfaceView
     */
    protected void magnifySurfaceView(SurfaceView toAmplifySurfaceView) {

        if (toAmplifySurfaceView == null)
            return;

        if (toAmplifySurfaceView.getX() == 0) {  //如果这个surfaceView已经全屏,则不用放大处理了
            return;
        }

        if (System.currentTimeMillis() - mLastPreformAnimTime < 1010)
            return;

        mLastPreformAnimTime = System.currentTimeMillis();

        mToAmplifySurfaceView = toAmplifySurfaceView;

        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mToAmplifySurfaceView.getLayoutParams();

        float currentX = mToAmplifySurfaceView.getX();
        float currentY = mToAmplifySurfaceView.getY();

        mLastSmallWindowPoint.x = (int) currentX;
        mLastSmallWindowPoint.y = (int) currentY;

        final ValueAnimator anim = ValueAnimator.ofFloat(mScreenSize.x / 3, mScreenSize.x);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fract = animation.getAnimatedFraction();
                params.width = (int) (mScreenSize.x / 3 * 2 * fract + mScreenSize.x / 3);
                params.height = (int) (mScreenSize.y / 3 * 2 * fract + mScreenSize.y / 3);
                mToAmplifySurfaceView.setLayoutParams(params);
                mToAmplifySurfaceView.setTranslationX(mLastSmallWindowPoint.x * (1 - fract));
                mToAmplifySurfaceView.setTranslationY(mLastSmallWindowPoint.y * (1 - fract));

            }
        });
        final AnimatorSet animSet = new AnimatorSet();

        animSet.play(anim);

        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mToAmplifySurfaceView.setTranslationX(0);
                mToAmplifySurfaceView.setTranslationY(0);
                animSet.removeAllListeners();
                anim.removeAllUpdateListeners();
                shrinkSurfaceView();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animSet.setDuration(800);
        animSet.start();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
