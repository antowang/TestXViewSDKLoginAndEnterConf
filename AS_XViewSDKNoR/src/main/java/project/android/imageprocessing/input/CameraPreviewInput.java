package project.android.imageprocessing.input;import java.io.PrintWriter;import java.io.StringWriter;import java.util.ArrayList;import java.util.List;import javax.microedition.khronos.opengles.GL10;import android.annotation.TargetApi;import android.content.pm.ActivityInfo;import android.graphics.ImageFormat;import android.graphics.SurfaceTexture;import android.graphics.SurfaceTexture.OnFrameAvailableListener;import android.hardware.Camera;import android.hardware.Camera.Parameters;import android.hardware.Camera.Size;import android.media.MediaCodecInfo;import android.opengl.GLES11Ext;import android.opengl.GLES20;import android.opengl.GLSurfaceView;import android.util.Log;import android.view.SurfaceHolder;import com.cinlan.core.LocaSurfaceView;import net.ossrs.yasea.SrsEncoder;import project.android.imageprocessing.filter.MultiInputFilter;/** * A Camera input extension of CameraPreviewInput.   * This class takes advantage of the android camera preview to produce new textures for processing. <p> * Note: This class requires an API level of at least 14.  To change camera parameters or get access to the * camera directly before it is used by this class, createCamera() can be override. * @author Chris Batt */@TargetApi(value = 14)public class CameraPreviewInput extends GLTextureOutputRenderer implements OnFrameAvailableListener {	private static final String UNIFORM_CAM_MATRIX = "u_Matrix";	public int getmPrevWidth() {		return mPrevWidth;	}	private int mPrevWidth=640;	public int getmPrevHeight() {		return mPrevHeight;	}	private int mPrevHeight=480;	private CameraSizeCb _cb=null;	public int getmOutWidth() {		return mOutWidth;	}	private boolean bMtk = true;	private int mOutWidth=0;	private int mOutHeight=0;	public int getmOutHeight() {		return mOutHeight;	}	public void setActivityOrientation(int activityOrientation) {		this.activityOrientation = activityOrientation;	}	private int activityOrientation=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;	public int getmPreviewRotation() {		return mPreviewRotation;	}	public void setmPreviewRotation(int mPreviewRotation) {		this.mPreviewRotation = mPreviewRotation;	}	private int mPreviewRotation=0;	private boolean bfisrt = true;	public Camera getCamera() {		return camera;	}	private Camera camera=null;	public Size getClsSize() {		return clsSize;	}	private Camera.Size clsSize;	private int mCamId = Camera.CameraInfo.CAMERA_FACING_FRONT;	private SurfaceTexture camTex;	private int matrixHandle;	private float[] matrix = new float[16];	private GLSurfaceView view;	/**	 * Creates a CameraPreviewInput which captures the camera preview with all the default camera parameters and settings.	 */	public CameraPreviewInput(GLSurfaceView view) {		super();		if(view==null){			int t = 1;		}else {			this.view = view;			view.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);		}		setIsLocal(true);	}	private void bindTexture() {		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);	    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture_in);	}	public void SetCameraParam(LocaSurfaceView.VideoConfig config) {		if(camera==null)			return;		Camera.Parameters params = camera.getParameters();		SrsEncoder.VFPS=config.videoFrameRate;		SrsEncoder.VGOP=config.videoMaxKeyframeInterval;//		mPrevWidth=config.videoWidth;//		mPrevHeight=config.videoHeight;		mPrevWidth=1280;		mPrevHeight=720;		//mPreviewRotation=config.previewRotation;		if( activityOrientation== ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){			mPreviewRotation=0;		}		else			mPreviewRotation=90;		if(config.enabeleFrontCam) {			mCamId = Camera.CameraInfo.CAMERA_FACING_FRONT;			params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);			config.openflash=false;		}		else {			mCamId = Camera.CameraInfo.CAMERA_FACING_BACK;			if(config.openflash){				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);			}else{				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);			}		}		List<Camera.Size> lsuport= getPreSiez(params);		clsSize= getCloselyPreSize(lsuport);		if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);		}		//params.setPictureSize(clsSize.width, clsSize.height);		params.setPreviewSize(clsSize.width, clsSize.height);		int[] range = findClosestFpsRange(SrsEncoder.VFPS, params.getSupportedPreviewFpsRange());		params.setPreviewFpsRange(range[0], range[1]);		params.setPreviewFormat(ImageFormat.NV21);//		params.setWhiteBalance(Camera.Parameters.FOCUS_MODE_AUTO);//		params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);		if (!params.getSupportedFocusModes().isEmpty()) {			params.setFocusMode(params.getSupportedFocusModes().get(0));		}		camera.setParameters(params);		camera.setDisplayOrientation(mPreviewRotation);		calOutSize();		if(_cb!=null) {			_cb.startPrieview();		}	}	public int  getmCamId(){		return  mCamId;	}	public void switchCarmera(int camid){		mCamId=camid;		onResume();	}	public void SwitchFlash(boolean open) {		if(camera==null||mCamId==1)			return;		Camera.Parameters params = camera.getParameters();		String flash = params.getFlashMode();		if(open&&flash.equals(Camera.Parameters.FLASH_MODE_OFF)){			params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);		}else{			params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);		}		camera.setParameters(params);	}	public void StartCamera(){		if(createCamera()!=null) {			reInitialize();			bfisrt=false;		}	}	public void StopCamera() {		if(camera != null) {			camera.setPreviewCallback(null);			camera.stopPreview();			camera.release();			camera = null;			Log.e("CameraPreviewInput","========StopCamera============");		}	}	protected Camera createCamera() {		if(camera == null) {			Log.e("CameraPreviewInput","========createCamera======is null======");			LocaSurfaceView.VideoConfig  config = LocaSurfaceView.getInstance().getVideoConfig();			if(!config.enabeleFrontCam)				mCamId=0;			else				mCamId=1;			camera=Camera.open(mCamId);			SetCameraParam(LocaSurfaceView.getInstance().getVideoConfig());		}else{		}		return camera;	}	/* (non-Javadoc)	 * @see project.android.imageprocessing.input.GLTextureOutputRenderer#destroy()	 */	@Override	public void destroy() {		super.destroy();		StopCamera();		if(camTex != null) {			camTex.release();			camTex = null;		}		if(texture_in != 0) {			int[] tex = new int[1];			tex[0] = texture_in;			GLES20.glDeleteTextures(1, tex, 0);			texture_in = 0;		}	}	@Override	protected void drawFrame() {		try {			camTex.updateTexImage();			super.drawFrame();		}catch (Exception e){		}	}	@Override	protected String getFragmentShader() {		return 					 "#extension GL_OES_EGL_image_external : require\n"					+"precision mediump float;\n"                         					+"uniform samplerExternalOES "+UNIFORM_TEXTURE0+";\n"  					+"varying vec2 "+VARYING_TEXCOORD+";\n"						 			+ "void main() {\n"		 			+ "   gl_FragColor = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+");\n"		 			+ "}\n";	}	@Override	protected String getVertexShader() {		return 					"uniform mat4 "+UNIFORM_CAM_MATRIX+";\n"				  + "attribute vec4 "+ATTRIBUTE_POSITION+";\n"				  + "attribute vec2 "+ATTRIBUTE_TEXCOORD+";\n"					  + "varying vec2 "+VARYING_TEXCOORD+";\n"					  + "void main() {\n"					  + "   vec4 texPos = "+UNIFORM_CAM_MATRIX+" * vec4("+ATTRIBUTE_TEXCOORD+", 1, 1);\n"				  + "   "+VARYING_TEXCOORD+" = texPos.xy;\n"				  + "   gl_Position = "+ATTRIBUTE_POSITION+";\n"		                                            			 				  + "}\n";			}	@Override	protected void initShaderHandles() {		super.initShaderHandles();        matrixHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_CAM_MATRIX);    	}	protected void initPreviewGLContext(){		int[] textures = new int[1];		GLES20.glGenTextures(1, textures, 0);		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);		texture_in = textures[0];		camTex = new SurfaceTexture(texture_in);		camTex.setOnFrameAvailableListener(this);		boolean failed = true;		int trycount = 0 ;		while(failed) {			if(trycount>5)				break;			try {				StopCamera();				if(createCamera()!=null) {					camera.setPreviewTexture(camTex);					camera.startPreview();				}				setRenderSizeToCameraSize();				failed = false;			} catch (Exception e) {				trycount++;				StringWriter sw = new StringWriter();				PrintWriter pw = new PrintWriter(sw);				e.printStackTrace(pw);				Log.e("CameraInput", sw.toString());				StopCamera();				try {					Thread.sleep(100);				} catch (InterruptedException e1) {					e1.printStackTrace();				}			}		}	}	@Override	protected void initWithGLContext() {		super.initWithGLContext();		initPreviewGLContext();	}	/* (non-Javadoc)	 * @see android.graphics.SurfaceTexture.OnFrameAvailableListener#onFrameAvailable(android.graphics.SurfaceTexture)	 */	@Override	public void onFrameAvailable(SurfaceTexture arg0) {		markAsDirty();		view.requestRender();	}	/**	 * Closes and releases the camera for other applications to use.	 * Should be called when the pause is called in the activity. 	 */	public void onPause() {		if(camera != null) {			camera.stopPreview();			camera.release();			camera = null;		}	}	/**	 * Re-initializes the camera and starts the preview again.	 * Should be called when resume is called in the activity.	 */	public void onResume() {		reInitialize();	}		@Override	protected void passShaderValues() {		renderVertices.position(0);		GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 8, renderVertices);  		GLES20.glEnableVertexAttribArray(positionHandle); 		textureVertices[curRotation].position(0);		GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureVertices[curRotation]);  		GLES20.glEnableVertexAttribArray(texCoordHandle);		bindTexture();	    GLES20.glUniform1i(textureHandle, 0);	    camTex.getTransformMatrix(matrix);		GLES20.glUniformMatrix4fv(matrixHandle, 1, false, matrix, 0);	}		private void setRenderSizeToCameraSize() {		Parameters params = camera.getParameters();		Size previewSize = params.getPreviewSize();		LocaSurfaceView.VideoConfig config = LocaSurfaceView.getInstance().getVideoConfig();		if(mPreviewRotation==0)			setRenderSize(previewSize.width, previewSize.height);		else			setRenderSize(previewSize.height, previewSize.width);	}	//查找出预览和picture都支持的尺寸	private List<Size> getPreSiez(Camera.Parameters params) {		int suport = 0;		List<Camera.Size> lprecam=params.getSupportedPreviewSizes();		List<Camera.Size> lpicsize=params.getSupportedPictureSizes();		List<Camera.Size> lprecam1= new ArrayList<Size>();		List<Camera.Size> lpicsize1= new ArrayList<Camera.Size>();		int mode = 32;		if(isMTk()){			mode=32;		}else			mode=16;		float  dip =1.0f;		int with ;		int height ;		if(mPreviewRotation==90) {			with=mPrevHeight;			height=mPrevWidth;			dip = (float) mPrevHeight / mPrevWidth;		}		else {			dip = (float) mPrevWidth / mPrevHeight;			with=mPrevWidth;			height=mPrevHeight;		}		for (Camera.Size tmp : lprecam) {//能被mode的			int modw = tmp.width % mode;			int modh = tmp.height % mode;			//Log.e("lprecam"," width = "+tmp.width+" height="+tmp.height+" Math.abs(newdip-dip)="+Math.abs(newdip-dip)+" modw="+modw+" modh="+modh);			if (modw == 0 && modh == 0) {				lprecam1.add(tmp);			}		}		lprecam.clear();		return lprecam1;	}	/**	 * 通过对比得到与宽高比最接近的预览尺寸（如果有相同尺寸，优先选择）	 *	 * @param preSizeList 需要对比的预览尺寸列表	 * @return 得到与原宽高比例最接近的尺寸	 */	private Camera.Size getCloselyPreSize(List<Camera.Size> preSizeList) {		int sumsize = mPrevWidth + mPrevHeight;		float reqRatio = 1.0f;		if(mPreviewRotation==90){			for(Camera.Size size : preSizeList){				if((size.width == mPrevHeight) && (size.height == mPrevWidth)){					return size;				}			}			reqRatio = ((float)mPrevHeight) / mPrevWidth;		}else{			for(Camera.Size size : preSizeList){				if(((size.width == mPrevWidth) && (size.height == mPrevHeight))){					return size;				}			}			reqRatio = ((float)mPrevWidth) /mPrevHeight ;		}		boolean breturn =false;		Camera.Size retSize = preSizeList.get(0);		int mindis = Math.abs(retSize.width+retSize.height-sumsize);		for (Camera.Size tmp : preSizeList) {//能被mode的			float newdip  =(float)tmp.width/tmp.height;			int nowsum = Math.abs(tmp.width+tmp.height-sumsize);			//Log.e("lprecam"," width = "+tmp.width+" height="+tmp.height+" Math.abs(newdip-dip)="+Math.abs(newdip-dip)+" modw="+modw+" modh="+modh);			if(Math.abs(newdip-reqRatio) <=0.00001f) {//比例相同，插值最小的				if(mindis>nowsum) {					retSize = tmp;					breturn=true;					mindis=nowsum;				}			}		}		if(breturn)			return retSize;		//查找最接近的		for (Camera.Size tmp : preSizeList) {//能被mode的			int nowsum = Math.abs(tmp.width+tmp.height-sumsize);			//Log.e("lprecam"," width = "+tmp.width+" height="+tmp.height+" nowsum="+nowsum+" sumsize="+sumsize);				if(mindis>nowsum) {					mindis=nowsum;					retSize = tmp;				}		}		return retSize;	}	private boolean isMTk(){		SrsEncoder encoder = new SrsEncoder();		MediaCodecInfo vmci=encoder.chooseVideoEncoder(null);		if (vmci.getName().contains("MTK")) {			bMtk=true;			return true;		}		return false;	}	private void calOutSize() {		if (mPreviewRotation == 90) {			int wmax = Math.min(clsSize.width, mPrevHeight);			int hmax = Math.min(clsSize.height, mPrevWidth);			if(mPrevWidth==clsSize.height&&mPrevHeight==clsSize.width){				mOutHeight=clsSize.width;				mOutWidth=clsSize.height;			}else {				mOutHeight = wmax;				mOutWidth = hmax;			}		} else {			int wmax = Math.min(clsSize.width,  mPrevWidth);			int hmax = Math.min(clsSize.height,mPrevHeight);			if(mPrevWidth==clsSize.width&&mPrevHeight==clsSize.height){				mOutWidth=clsSize.width;				mOutHeight=clsSize.height;			}else {				mOutWidth = wmax;				mOutHeight = hmax;			}		}		if (bMtk) {			int w = mOutWidth %  32;			int h= mOutHeight%32;			if ( w!= 0 ) {				mOutWidth=mOutWidth-w;			}			if ( h!= 0 ) {				mOutHeight=mOutHeight-h;			}		}else{			int w = mOutWidth %  16;			int h= mOutHeight%16;			if ( w!= 0 ) {				mOutWidth=mOutWidth-w;			}			if ( h!= 0 ) {				mOutHeight=mOutHeight-h;			}		}	}	private int[] findClosestFpsRange(int expectedFps, List<int[]> fpsRanges) {		expectedFps *= 1000;		int[] closestRange = fpsRanges.get(0);		int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);		for (int[] range : fpsRanges) {			if (range[0] <= expectedFps && range[1] >= expectedFps) {				int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);				if (curMeasure < measure) {					closestRange = range;					measure = curMeasure;				}			}		}		return closestRange;	}	public void setCameraCbObj( CameraSizeCb cb){		_cb=cb;	}	public  interface CameraSizeCb{		void startPrieview();	}}