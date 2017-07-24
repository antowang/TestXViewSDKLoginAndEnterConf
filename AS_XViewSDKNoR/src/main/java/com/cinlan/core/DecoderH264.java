package com.cinlan.core;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.cinlan.xview.utils.XviewLog;

/**
 * Created by Leo Ma on 4/1/2016.
 */
public class DecoderH264 {
    String mMimeType="video/avc";
    String TAG="DecoderH264";
    private MediaCodec vdecoder= null;
    private long mPresentTimeUs;
    public boolean isBstart() {
        return bstart;
    }
    private boolean bstart = false;

    public int vInputWidth = 720;
    public int vInputHeight = 1280;  // Since Y com
    public void SetDecodeSize(int width,int height){
        vInputWidth=width;
        vInputHeight=height;
    }

    public void onGetH264Frame(byte[] data, long pts) {
        //long pts = System.nanoTime() / 1000 - mPresentTimeUs;
        onFrame(data, pts);
    }

    public boolean start(Surface surface) {
        XviewLog.e("DecoderH264","start  硬解码==========");
        try {
            mPresentTimeUs = System.nanoTime() / 1000;
            vdecoder = MediaCodec.createDecoderByType(mMimeType);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(mMimeType,vInputWidth, vInputHeight);

            vdecoder.configure(mediaFormat, surface, null, 0);
            vdecoder.setVideoScalingMode(MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            vdecoder.start();
            bstart=true;
            XviewLog.e(TAG, "start vencoder==初始化成功=========width="+vInputWidth+"  height="+vInputHeight);
            return true;

        } catch (IOException e) {
        	XviewLog.e(TAG, "create vencoder failed.");
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        if (vdecoder != null) {
        	XviewLog.e(TAG, "stop vencoder==停止硬件解码===============");
            bstart=false;
            vdecoder.stop();
            vdecoder.release();
            vdecoder = null;
        }
    }

    int mFrameCount = 0;
    private final static int TIME_INTERNAL = 30;
    public boolean onFrame(byte[] buf, long pts) {
        if(vdecoder==null){
        	XviewLog.e(TAG, "vdecoder==is null===============");
            return false;
        }
        ByteBuffer[] inputBuffers = vdecoder.getInputBuffers();
        int inputBufferIndex = vdecoder.dequeueInputBuffer(-1);
       // XviewLog.e("Media", "onFrame index:" + inputBufferIndex);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf, 0, buf.length);
            vdecoder.queueInputBuffer(inputBufferIndex, 0, buf.length,pts, 0);
            mFrameCount++;
        } else {
            return false;
        }
        // Get output buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = vdecoder.dequeueOutputBuffer(bufferInfo, 0);
        while (outputBufferIndex >= 0) {
            vdecoder.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = vdecoder.dequeueOutputBuffer(bufferInfo, 0);
        }
        return true;
    }
}
