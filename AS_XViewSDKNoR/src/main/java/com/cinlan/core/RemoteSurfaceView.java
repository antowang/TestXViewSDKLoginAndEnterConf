package com.cinlan.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.opengles.GL10;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.output.ScreenEndpoint;

/**
 *  远端视频
 *  Created by apple on 2017/3/17.
 */

public class RemoteSurfaceView extends GLSurfaceView {

    // 管道过滤器处理
    private FastImageProcessingPipeline mPipeline = null;
    private ScreenEndpoint mScreen = null;
    private VideoDecodeInput mDecodeInput = null;
    private boolean bcreate = false;
    private int decWidth = 0;
    private int decHeight = 0;
    private int scale_mode = 0;

    /**
     * Creates a new view which can be used for fast image processing.
     * @param context The activity context that this view belongs to.
     */
    public RemoteSurfaceView(Context context, int mode) {
        this(context, null, mode);
        setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        setEGLContextClientVersion(2);

        mPipeline = new FastImageProcessingPipeline();
        this.setPipeline(mPipeline);

        mPipeline.setListen(new FastImageProcessingPipeline.SurfaceListen(){
            @Override
            public void onSurfaceCreated(){
                FreeAll();
                CreateRemoteSurfaceView();
            }
            @Override
            public void onSurfaceChanged(GL10 unused, int width, int height){
                //CreateRemoteSurfaceView();
                mScreen.UpdateSize(width, height);
            }

            @Override
            public void onDrawFrame() {
                int i = 0;
            }
        });
    }

    /**
     * Creates a new view which can be used for fast image processing.
     * @param context The activity context that this view belongs to.
     * @param attr The activity attribute set.
     */
    public RemoteSurfaceView(Context context, AttributeSet attr, int mode) {
        super(context, attr);
        setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
        setEGLContextClientVersion(2);
        scale_mode = mode;
    }

    public void CreateRemoteSurfaceView() {
        if (bcreate) {
            return;
        }
        bcreate=true;
        mDecodeInput = new VideoDecodeInput(this);
        mScreen = new ScreenEndpoint(mPipeline);
        mScreen.setbPreView(true);
        mScreen.setMode(scale_mode);
        mDecodeInput.addTarget(mScreen);

        mPipeline.addRootRenderer(mDecodeInput);
        /*
        mPreviewInput.setCameraCbObj(new CameraPreviewInput.CameraSizeCb() {
            @Override
            public void startPrieview(){
                mPreviewInput.StartCamera();
                mScreen.setbPreView(bPreview);
                Camera.Size size = mPreviewInput.getClsSize();
                if (mPreviewInput.getmPreviewRotation() == 90
                        || mPreviewInput.getmPreviewRotation() == 270) {
                    mScreen.SetRawSize(size.height, size.width);
                } else {
                    mScreen.SetRawSize(size.width, size.height);
                }
            }
        });
        */
        startRendering();
    }

    private void startRendering() {
        if (mPipeline==null) {
            return;
        }
        mPipeline.startRendering();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        super.surfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    /**
     * Sets the FastImageProcessingPipeline that will do the rendering for this view.
     * @param pipeline The FastImageProcessingPipeline that will do the rendering for this view.
     */
    public void setPipeline(FastImageProcessingPipeline pipeline) {
        setRenderer(pipeline);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public void AddVideoData(byte[] data, long pts, int width, int height, int frameType) {
        if (mDecodeInput != null) {
            if (decWidth != width || decHeight != height) {
                decWidth = width;
                decHeight = height;
                mDecodeInput.setRenderSize(width, height);
                mScreen.SetRawSize(width, height);
            }

            mDecodeInput.AddVideoData(data, pts, width, height, frameType);
        }
    }

    private void FreeAll() {
        bcreate = false;
        decWidth = 0;
        decHeight = 0;
        mPipeline.pauseRendering();
        if (mDecodeInput != null) {
            mPipeline.removeRootRenderer(mDecodeInput);
            mDecodeInput.removeTarget(mScreen);
            mDecodeInput.destroy();
            mScreen.destroy();
            mDecodeInput = null;
            mScreen = null;
        }
    }
}
