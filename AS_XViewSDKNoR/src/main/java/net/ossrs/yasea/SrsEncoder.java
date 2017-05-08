package net.ossrs.yasea;

import android.content.res.Configuration;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.cinlan.xview.utils.XviewLog;

/**
 * Created by Leo Ma on 4/1/2016.
 */
public class SrsEncoder {
    private static final String TAG = "SrsEncoder";
    private byte[] sps_pps_byte;
    private int sps_pps_len = 0;
    public static final String VCODEC = "video/avc";
    public String x264Preset = "veryfast";
    public static int vOutWidth = 720;   // Note: the stride of resolution must be set as 16x for hard encoding with some chip like MTK
    public static int vOutHeight = 1280;  // Since Y component is quadruple size as U and V component, the stride must be set as 32x
    public static int vBitrate = 500 * 1000;  // 500kbps
    public static int VFPS = 24;
    public static int VGOP = 48;

    private MediaCodecInfo vmci;
    private MediaCodec vencoder = null;
    private MediaCodec.BufferInfo vebi = new MediaCodec.BufferInfo();
    private boolean useSoftEncoder = false;
    private long mPresentTimeUs;
    private int mVideoColorFormat;
    private boolean bstop = true;


    // Y, U (Cb) and V (Cr)
    // yuv420                     yuv yuv yuv yuv
    // yuv420p (planar)   yyyy*2 uu vv
    // yuv420sp(semi-planner)   yyyy*2 uv uv
    // I420 -> YUV420P   yyyy*2 uu vv
    // YV12 -> YUV420P   yyyy*2 vv uu
    // NV12 -> YUV420SP  yyyy*2 uv uv
    // NV21 -> YUV420SP  yyyy*2 vu vu
    // NV16 -> YUV422SP  yyyy uv uv
    // YUY2 -> YUV422SP  yuyv yuyv

    public SrsEncoder() {
        mVideoColorFormat = chooseVideoEncoder();
    }

    public boolean start() {
        bstop = false;
        // the referent PTS for video and audio encoder.
        mPresentTimeUs = System.nanoTime() / 1000;
        setEncoderResolution(vOutWidth, vOutHeight);
        setEncoderFps(VFPS);
        setEncoderGop(VGOP);
        setEncoderBitrate(vBitrate);
        setEncoderPreset(x264Preset);
        if (useSoftEncoder && !openSoftEncoder()) {
            return false;
        }

        // vencoder yuv to 264 es stream.
        // requires sdk level 16+, Android 4.1, 4.1.1, the JELLY_BEAN
        try {
            vencoder = MediaCodec.createByCodecName(vmci.getName());
            XviewLog.e(TAG, " vencoder 对象被创建===============.");

        } catch (IOException e) {
            XviewLog.e(TAG, "create vencoder failed.");
            e.printStackTrace();
            return false;
        }

        // setup the vencoder.
        // Note: landscape to portrait, 90 degree rotation, so we need to switch width and height in configuration
        MediaFormat videoFormat = MediaFormat.createVideoFormat(VCODEC, vOutWidth, vOutHeight);
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, mVideoColorFormat);
        videoFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, vBitrate);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, VFPS);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VGOP / VFPS);
        vencoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        XviewLog.e(TAG, "vencoder is create =========");
        // start device and encoder.
        vencoder.start();
        return true;
    }

    public void stop() {
        bstop = true;
        if (vencoder != null) {
            XviewLog.e(TAG, "stop vencoder=================");
            vencoder.stop();
            vencoder.release();
            vencoder = null;
        }


    }

    public void setResolution(int width, int height) {
        vOutWidth = width;
        vOutHeight = height;
    }

    private void onProcessedYuvFrame(byte[] yuvFrame, long pts) {
        try {
            if (vencoder == null) {
                XviewLog.e("onProcessedYuvFrame", "vencoder is null ==============");
                return;
            }
            ByteBuffer[] inBuffers = vencoder.getInputBuffers();
            ByteBuffer[] outBuffers = vencoder.getOutputBuffers();

            int inBufferIndex = vencoder.dequeueInputBuffer(-1);
            if (inBufferIndex >= 0) {
                ByteBuffer bb = inBuffers[inBufferIndex];
                bb.clear();
                bb.put(yuvFrame, 0, yuvFrame.length);
                vencoder.queueInputBuffer(inBufferIndex, 0, yuvFrame.length, pts, 0);
            }

            for (; ; ) {
                if (bstop) {
                    XviewLog.e("onProcessedYuvFrame", "vencoder is null ====bstop==========");
                    break;
                }
                int outBufferIndex = vencoder.dequeueOutputBuffer(vebi, 0);
                if (outBufferIndex >= 0) {
                    ByteBuffer bb = outBuffers[outBufferIndex];
                    byte[] outData = new byte[vebi.size];
                    bb.get(outData);
                    //operaFileData(outData);
                    int nalkey1 = vebi.flags;
                    int nalkey = outData[4] & 0x1f;
                   // XviewLog.e("h264数据","====vOutWidth=="+vOutWidth+"===vOutHeight==="+vOutHeight+" nalkey="+nalkey);
                    if (nalkey == 5) {
                        if (sps_pps_len > 0) {
                            //XviewLog.e("捕获第一帧1","========width=="+vOutWidth+"===height==="+vOutHeight);
                            byte[] newData = new byte[vebi.size + sps_pps_len];
                            System.arraycopy(sps_pps_byte, 0, newData, 0, sps_pps_len);
                            System.arraycopy(outData, 0, newData, sps_pps_len, vebi.size);
                            // operaFileData(newData);
                            EncodedVideoFrame(newData, newData.length, 1, vOutWidth, vOutHeight);
                            // XviewLog.e("捕获I帧结束","======new==width=="+vOutWidth+"===height==="+vOutHeight);
                            newData = null;
                        }
                    } else if (7 == nalkey || 8 == nalkey) {
                        sps_pps_len = outData.length - 4;
                        sps_pps_byte = new byte[sps_pps_len];
                        System.arraycopy(outData, 4, sps_pps_byte, 0, sps_pps_len);
                    } else {
                        if (sps_pps_len > 0) {
                            byte[] newData = new byte[vebi.size - 4];
                            System.arraycopy(outData, 4, newData, 0, vebi.size - 4);
                            // operaFileData(newData);
                            EncodedVideoFrame(newData, newData.length, 0, vOutWidth, vOutHeight);
                            newData = null;
                        }
                    }
                    vencoder.releaseOutputBuffer(outBufferIndex, false);
                    outData = null;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            int k = 1;
        }

    }

    private void onSoftEncodedData(byte[] es, long pts, boolean isKeyFrame) {
        ByteBuffer bb = ByteBuffer.wrap(es);
        vebi.offset = 0;
        vebi.size = es.length;
        vebi.presentationTimeUs = pts;
        vebi.flags = isKeyFrame ? MediaCodec.BUFFER_FLAG_KEY_FRAME : 0;
        onEncodedAnnexbFrame(bb, vebi);
    }

    // when got encoded h264 es stream.
    private void onEncodedAnnexbFrame(ByteBuffer es, MediaCodec.BufferInfo bi) {
        try {
            ByteBuffer record = es.duplicate();
        } catch (Exception e) {
            XviewLog.e(TAG, "muxer write video sample failed.");
            e.printStackTrace();
        }
    }

    public void onGetRgbaFrame(byte[] data, int width, int height) {
        // Check video frame cache number to judge the networking situation.
        // Just cache GOP / FPS seconds data according to latency.
        long pts = System.nanoTime() / 1000 - mPresentTimeUs;
        if (useSoftEncoder) {
            swRgbaFrame(data, width, height, pts);
        } else {
            byte[] processedData = hwRgbaFrame(data, width, height);
            if (processedData != null) {
                onProcessedYuvFrame(processedData, pts);
            } else {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(),
                        new IllegalArgumentException("libyuv failure"));
            }
        }
    }

    private byte[] hwRgbaFrame(byte[] data, int width, int height) {
        switch (mVideoColorFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                return RGBAToI420(data, width, height, true, 180);
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                return RGBAToNV12(data, width, height, true, 180);
            default:
                throw new IllegalStateException("Unsupported color format!");
        }
    }

    private void swRgbaFrame(byte[] data, int width, int height, long pts) {
        RGBASoftEncode(data, width, height, true, 180, pts);
    }


    // choose the video encoder by name.
    public MediaCodecInfo chooseVideoEncoder(String name) {
        int nbCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < nbCodecs; i++) {
            MediaCodecInfo mci = MediaCodecList.getCodecInfoAt(i);
            if (!mci.isEncoder()) {
                continue;
            }

            String[] types = mci.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(VCODEC)) {
                    XviewLog.i(TAG, String.format("vencoder %s types: %s", mci.getName(), types[j]));
                    if (name == null) {
                        return mci;
                    }

                    if (mci.getName().contains(name)) {
                        return mci;
                    }
                }
            }
        }

        return null;
    }

    // choose the right supported color format. @see below:
    private int chooseVideoEncoder() {
        vmci = chooseVideoEncoder(null);
        int matchedColorFormat = 0;
        MediaCodecInfo.CodecCapabilities cc = vmci.getCapabilitiesForType(VCODEC);
        for (int i = 0; i < cc.colorFormats.length; i++) {
            int cf = cc.colorFormats[i];
            XviewLog.i(TAG, String.format("vencoder %s supports color fomart 0x%x(%d)", vmci.getName(), cf, cf));

            // choose YUV for h.264, prefer the bigger one.
            // corresponding to the color space transform in onPreviewFrame
            if (cf >= cc.COLOR_FormatYUV420Planar && cf <= cc.COLOR_FormatYUV420SemiPlanar) {
                if (cf > matchedColorFormat) {
                    matchedColorFormat = cf;
                }
            }
        }

        for (int i = 0; i < cc.profileLevels.length; i++) {
            MediaCodecInfo.CodecProfileLevel pl = cc.profileLevels[i];
            XviewLog.i(TAG, String.format("vencoder %s support profile %d, level %d", vmci.getName(), pl.profile, pl.level));
        }

        XviewLog.i(TAG, String.format("vencoder %s choose color format 0x%x(%d)", vmci.getName(), matchedColorFormat, matchedColorFormat));
        return matchedColorFormat;
    }

    private native void setEncoderResolution(int outWidth, int outHeight);

    private native void setEncoderFps(int fps);

    private native void setEncoderGop(int gop);

    private native void setEncoderBitrate(int bitrate);

    private native void setEncoderPreset(String preset);

    private native byte[] NV21ToI420(byte[] yuvFrame, int width, int height, boolean flip, int rotate);

    private native byte[] NV21ToNV12(byte[] yuvFrame, int width, int height, boolean flip, int rotate);

    private native byte[] RGBAToI420(byte[] yuvFrame, int width, int height, boolean flip, int rotate);

    private native byte[] RGBAToNV12(byte[] yuvFrame, int width, int height, boolean flip, int rotate);

    private native int NV21SoftEncode(byte[] yuvFrame, int width, int height, boolean flip, int rotate, long pts);

    private native int RGBASoftEncode(byte[] yuvFrame, int width, int height, boolean flip, int rotate, long pts);

    private native boolean openSoftEncoder();

    private native void closeSoftEncoder();

    private native void EncodedVideoFrame(byte[] data, int len, int width, int height, int frameType);

    static {
        System.loadLibrary("yuv");
        System.loadLibrary("enc");
    }
}
