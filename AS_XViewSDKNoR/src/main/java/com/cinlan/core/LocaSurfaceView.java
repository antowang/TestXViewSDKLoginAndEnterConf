package com.cinlan.core;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import net.ossrs.yasea.SrsEncoder;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.opengles.GL10;

import com.cinlan.xview.utils.SPUtil;
import com.cinlan.xview.utils.XviewLog;

import project.android.imageprocessing.FastImageProcessingPipeline;
import project.android.imageprocessing.FastImageProcessingView;
import project.android.imageprocessing.beauty.YUHighPassSkinSmoothingFilter;
import project.android.imageprocessing.input.CameraPreviewInput;
import project.android.imageprocessing.input.GLTextureOutputRenderer;
import project.android.imageprocessing.output.ScreenEndpoint;

/**
 * 本地视频
 * 
 * @author ChongXue
 * @date 2017-3-13
 */
public class LocaSurfaceView implements SurfaceHolder.Callback {
	private int mIndex = 0;
	private FastImageProcessingView mfastImageProcessingView = null;
	private FastImageProcessingPipeline mPipeline = null;
	private ScreenEndpoint mScreen = null;
	private CameraPreviewInput mPreviewInput = null;

	private boolean bPreview = false;
	private SrsEncoder mEncoder = null;
	private boolean mIsEncoding = false;
	private boolean bsartEncoding = false;
	private boolean bAllocatebuf = false;

	private final ConcurrentLinkedQueue<IntBuffer> mGLIntBufferCache = new ConcurrentLinkedQueue<IntBuffer>();
	private int starX = 0;
	private int startY = 0;
	private int mOutWidth = 0;
	private int mOutHeight = 0;
	private int mCount = 10;

	private IntBuffer[] mArrayGLFboBuffer;
	private ByteBuffer mGlPreviewBuffer;
	private final Object writeLock = new Object();

	private int mActivityDirector;

	private VideoConfig mConfig = new VideoConfig();

	private boolean bcreate = false;
    private int scale_mode = 1;

	public void setbPreview(boolean bPreview) {
		this.bPreview = bPreview;

		if (mScreen != null) {
			mScreen.setbPreView(bPreview);
		}
	}

	private static LocaSurfaceView locaSurfaceView = null;

	public static synchronized LocaSurfaceView getInstance() {
		if (locaSurfaceView == null) {
			synchronized (LocaSurfaceView.class) {
				if (locaSurfaceView == null) {
					locaSurfaceView = new LocaSurfaceView();
				}
			}
		}
		return locaSurfaceView;
	}

	private LocaSurfaceView() {
	};

    public FastImageProcessingView getSurfaceView(Context context, int mode)  {
		 scale_mode = mode;
		/**
		 *  新做法<br>
		 *  或者不这么改,还像原来那样由外边传进来也行,只要值ok就行.
		 */
	    mActivityDirector = SPUtil.getConfigIntValue(context, "viewModePosition", 1) == 1 
	    		? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT 
	    				: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

		if (mfastImageProcessingView == null) {
			mfastImageProcessingView = new FastImageProcessingView(
					context.getApplicationContext());
			mPipeline = new FastImageProcessingPipeline();
			mfastImageProcessingView.setPipeline(mPipeline);
			mfastImageProcessingView.getHolder().addCallback(this);

			  mPipeline.setListen(new FastImageProcessingPipeline.SurfaceListen(){
	                @Override
	                public void onSurfaceCreated(){
	                    Log.e("mPipeline", "onSurfaceCreated---------");
	                    CreateLocalSurfaceView();
	                }
	                @Override
	                public void onSurfaceChanged(GL10 unused, int width, int height){
	                    Log.e("mPipeline", "onSurfaceChanged---------");

	                    if (!bcreate) {
	                        CreateLocalSurfaceView();

	                    }
	                    mScreen.UpdateSize(width, height);
	                }

	                @Override
	                public void onDrawFrame() {

	                }
	            });
		}

		return mfastImageProcessingView;
	}

	public FastImageProcessingView getSurfaceView() {
		return mfastImageProcessingView;
	}

	public void CreateLocalSurfaceView() {
		// 初始化surfaceview
		if (bcreate)
			return;
		bcreate = true;
		mPreviewInput = new CameraPreviewInput(mfastImageProcessingView);
		mPreviewInput.setActivityOrientation(mActivityDirector);
		mScreen = new ScreenEndpoint(mPipeline);
        mScreen.setMode(scale_mode);
		mPreviewInput.addTarget(mScreen);

		mPipeline.addRootRenderer(mPreviewInput);
		mPreviewInput.setCameraCbObj(new CameraPreviewInput.CameraSizeCb() {
			@Override
			public void startPrieview() {
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
		startRendering();
	}

	private void startRendering() {
		if (mPipeline == null)
			return;
		mPipeline.startRendering();
	}

	private void AllocateBuffer() {
		if (mPreviewInput == null)
			return;
		Camera.Size size = mPreviewInput.getClsSize();
		if (size == null)
			return;
		mOutWidth = mPreviewInput.getmOutWidth();
		mOutHeight = mPreviewInput.getmOutHeight();
		mArrayGLFboBuffer = new IntBuffer[mCount];
		for (int i = 0; i < mCount; i++) {
			mArrayGLFboBuffer[i] = IntBuffer.allocate(mOutWidth * mOutHeight);
		}
		if (mPreviewInput.getmPreviewRotation() == 90) {
			starX = (size.height - mOutWidth) / 2;
			startY = (size.width - mOutHeight) / 2;
		} else {
			starX = (size.width - mOutWidth) / 2;
			startY = (size.height - mOutHeight) / 2;
		}
		mGlPreviewBuffer = ByteBuffer.allocate(mOutWidth * mOutHeight * 4);
		bAllocatebuf = true;
	}

	public void setBmEncode(boolean bmEncode) {
		this.mIsEncoding = bmEncode;
	}

	private void StartEncoder() {
		if (bsartEncoding) {
			return;
		}

		Camera.Size size = mPreviewInput.getClsSize();
		if (size == null)
			return;
		bsartEncoding = true;
		AllocateBuffer();
		mEncoder = new SrsEncoder();
		mEncoder.setResolution(mOutWidth, mOutHeight);
		mEncoder.start();
		enableEncoding();
	}

	private void FreeEncoder() {
		if (!bsartEncoding) {
			return;
		}

		disableEncoding();

		bsartEncoding = false;
		if (mEncoder != null)
			mEncoder.stop();
		mEncoder = null;
	}

	public void FreeAll() {
		XviewLog.e("FreeAll", "===========BEGIN===========" + bcreate);
		if (!bcreate)
			return;

		bcreate = false;
		// mIsEncoding=false;
		mPipeline.pauseRendering();
		mPreviewInput.StopCamera();

		mPreviewInput.removeTarget(mScreen);
		mPipeline.removeRootRenderer(mPreviewInput);

		FreeEncoder();

		mScreen = null;
		mPreviewInput = null;
		mArrayGLFboBuffer = null;
		mGlPreviewBuffer = null;
		// mfastImageProcessingView = null;
		// mPipeline = null;
		bAllocatebuf = false;
		// bPreview = false;

		// 否则切换分辨率后不发送数据了
		// mIsEncoding=false;

		XviewLog.e("FreeAll", "===========over===========INDEX = ");
	}

	private Thread worker;

	private void enableEncoding() {
		worker = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					synchronized (mGLIntBufferCache) {
						while (!mGLIntBufferCache.isEmpty()) {
							IntBuffer picture = mGLIntBufferCache.poll();
							// XviewLog.e("enableEncoding ","mindex ="+mIndex+"**********surfaceindex=******"+surfaceindex);
							mGlPreviewBuffer.asIntBuffer().put(picture.array());
							mEncoder.onGetRgbaFrame(mGlPreviewBuffer.array(),
									mOutWidth, mOutHeight);
							mGlPreviewBuffer.clear();
							picture.clear();
						}
					}

					// Waiting for next frame
					synchronized (writeLock) {
						try {
							writeLock.wait(30);
						} catch (InterruptedException ie) {
							worker.interrupt();
						}
					}
				}
			}
		});
		worker.start();
	}

	void disableEncoding() {

		if (worker != null) {
			worker.interrupt();
			try {
				worker.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				worker.interrupt();
			}
			worker = null;
			mGLIntBufferCache.clear();
		}
		// XviewLog.e("disableEncoding","===========end==========="+bcreate);
	}

	private IntBuffer getIntBuffer() {

		if (mIndex > mCount - 1) {
			mIndex = 0;
		}
		return mArrayGLFboBuffer[mIndex++];
	}

	public void putIntBuffer() {
		if (mIsEncoding) {
			StartEncoder();
			if (bAllocatebuf) {
				synchronized (mGLIntBufferCache) {
					IntBuffer mGLFboBuffer = getIntBuffer();
					GLES20.glReadPixels(starX, startY, mOutWidth, mOutHeight,
							GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
							mGLFboBuffer);
					if (mGLIntBufferCache.size() >= mCount) {
						IntBuffer picture = mGLIntBufferCache.poll();
						picture.clear();
					}
					mGLIntBufferCache.add(mGLFboBuffer);
				}
			}
		} else {
			FreeEncoder();
		}
	}

	void switchFlash(boolean open) {
		if (mPreviewInput == null)
			return;
		mPreviewInput.SwitchFlash(open);
	}

	void switchCamera(boolean bfront) {
		if (mPreviewInput == null)
			return;
		int camerid = mPreviewInput.getmCamId();
		if (bfront && camerid == Camera.CameraInfo.CAMERA_FACING_FRONT)
			return;
		if (bfront) {
			camerid = 1;
		} else {
			camerid = 0;
		}
		mPreviewInput.switchCarmera(camerid);
	}

	public Camera.Size getCloseSize() {
		Camera.Size size = mPreviewInput.getClsSize();
		if (size == null)
			return null;
		return size;
	}

	public void reInitialize() {
		mScreen.reInitialize();
	}

	public class VideoConfig implements Cloneable {
		/**
		 * 视频宽
		 */
		public int videoWidth;
		/**
		 * 视频高
		 */
		public int videoHeight;
		/**
		 * 视频帧率
		 */
		public int videoFrameRate;
		/**
		 * I帧间隔，决定一个gop的大小
		 */
		public int videoMaxKeyframeInterval;
		/**
		 * 视频码率
		 */
		public int videoBitRate;
		/**
		 * 是否启用前置摄像头
		 */
		public boolean enabeleFrontCam;
		public boolean openflash;

		public VideoConfig() {
			videoMaxKeyframeInterval = 30;
			enabeleFrontCam = true;
			videoFrameRate = 15;
			videoBitRate = 500 * 1000;
			videoWidth = 352;
			videoHeight = 640;
			openflash = false;
			enabeleFrontCam = true;
		}

		public Object clone() {
			Object o = null;
			try {
				o = super.clone();
			} catch (CloneNotSupportedException e) {
				System.out.println(e.toString());
			}
			return o;
		}
	}

	public void setVideoConfig(VideoConfig config) {

		boolean needReset = false;
		boolean switchCam = false;
		if (mConfig.videoWidth != config.videoWidth
				|| mConfig.videoHeight != config.videoHeight
				|| mConfig.videoBitRate != config.videoBitRate
				|| mConfig.videoFrameRate != config.videoFrameRate) {
			needReset = true;
		}

		if (mConfig.enabeleFrontCam != config.enabeleFrontCam) {
			switchCam = true;
		}

		mConfig = config;

		if (mfastImageProcessingView == null
				|| !mfastImageProcessingView.getHolder().getSurface().isValid()) {
			return;
		}

		if (needReset) {
			FreeAll();
			CreateLocalSurfaceView();
			return;
		}

		if (switchCam) {
			switchCamera(mConfig.enabeleFrontCam);
		}
	}

	public VideoConfig getVideoConfig() {
		Object o = mConfig.clone();
		if (o != null) {
			return (VideoConfig) o;
		} else {
			return null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		FreeAll();
	}
}
