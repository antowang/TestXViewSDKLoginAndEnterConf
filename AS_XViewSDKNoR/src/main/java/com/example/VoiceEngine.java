package com.example;

import com.cinlan.xview.utils.XviewLog;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class VoiceEngine
{
    private static final int JNI_VOICE_ENGINE_START_SPEAKING = 1;
    private static final int JNI_VOICE_ENGINE_STOP_SPEAKING = 2;
    private static final int JNI_VOICE_ENGINE_START_PLAYOUT = 3;
    private static final int JNI_VOICE_ENGINE_STOP_PLAYOUT = 4;

    private VoEHandler voehandler;

    public VoiceEngine()
    {
        voehandler = new VoEHandler(Looper.getMainLooper());
        if (!Initialize(this))
        {
            XviewLog.e("NOTIFY", "Initialize Voice Engine Failed");
        }
    }

    private native boolean Initialize(VoiceEngine voe);

    private native void StartSpeaking();
    private native void StopSpeaking();

    private native void StartPlayout();
    private native void StopPlayout();

    private void onStartSpeaking()
    {
        Message.obtain(voehandler, JNI_VOICE_ENGINE_START_SPEAKING)
                .sendToTarget();
    }

    private void onStopSpeaking()
    {
        Message.obtain(voehandler, JNI_VOICE_ENGINE_STOP_SPEAKING)
                .sendToTarget();
    }

    private void onStartPlayout()
    {
        Message.obtain(voehandler, JNI_VOICE_ENGINE_START_PLAYOUT)
                .sendToTarget();
    }

    private void onStopPlayout()
    {
        Message.obtain(voehandler, JNI_VOICE_ENGINE_STOP_PLAYOUT)
                .sendToTarget();
    }

    private class VoEHandler extends Handler
    {
        public VoEHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message nMsg)
        {
            switch (nMsg.what)
            {
                case JNI_VOICE_ENGINE_START_SPEAKING:
                    StartSpeaking();
                    break;
                case JNI_VOICE_ENGINE_STOP_SPEAKING:
                    StopSpeaking();;
                    break;
                case JNI_VOICE_ENGINE_START_PLAYOUT:
                    StartPlayout();
                    break;
                case JNI_VOICE_ENGINE_STOP_PLAYOUT:
                    StopPlayout();
                    break;
                default:
                    break;
            }
        }
    }
}
