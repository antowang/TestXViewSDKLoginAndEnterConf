package com.cinlan.xview.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.cinlan.xview.utils.XviewLog;


public abstract class AbstractHandler extends Handler {

    private static final int REQUEST_TIME_OUT = 0;

    protected static final int DEFAULT_TIME_OUT_SECS = 10;


    private SparseArray<Meta> metaHolder = new SparseArray<Meta>();

    public AbstractHandler() {
        super(Looper.getMainLooper());
    }

    protected Message initTimeoutMessage(int mointorMessageID, long timeOutSec,
                                         Registrant caller) {
        // Create unique message object
        Message msg = Message.obtain(this, REQUEST_TIME_OUT, mointorMessageID,
                0, new Object());
        metaHolder.put(Integer.valueOf(mointorMessageID), new Meta(
                mointorMessageID, caller, msg));
        this.sendMessageDelayed(msg, timeOutSec * 1000);
        return msg;
    }

    /**
     * 移除延时消息
     * @param mointorMessageID
     * @return
     */
    protected Registrant removeTimeoutMessage(int mointorMessageID) {
        Meta meta = metaHolder.get(Integer.valueOf(mointorMessageID));
        metaHolder.remove(Integer.valueOf(mointorMessageID));
        if (meta != null) {
            this.removeMessages(REQUEST_TIME_OUT, meta.timeoutMessage.obj);
            return meta.caller;
        } else {
            return null;
        }
    }

    /**
     * 元数据
     */
    class Meta {
        int mointorMessageID; // id
        Registrant caller; // 注册者
        Message timeoutMessage; // 消息

        public Meta(int mointorMessageID, Registrant caller,
                    Message timeoutMessage) {
            super();
            this.mointorMessageID = mointorMessageID;
            this.caller = caller;
            this.timeoutMessage = timeoutMessage;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        Message caller = null;
        XviewLog.d(this.getClass().getName() + "   " + msg.what);
        switch (msg.what) {
            case REQUEST_TIME_OUT:
                Integer key = Integer.valueOf(msg.arg1);
                Meta meta = metaHolder.get(key);
                if (meta != null && meta.caller != null) {

                    JNIResponse jniRes = new JNIResponse(JNIResponse.Result.TIME_OUT);
                    jniRes.callerObject = meta.caller.getObject();

                    if (meta.caller.getHandler() != null) {
                        caller = Message.obtain(meta.caller.getHandler(), meta.caller.getWhat(), jniRes);
                    } else {
                        XviewLog.w(" message no target:" + meta.caller);
                    }
                } else {
                    XviewLog.w("Doesn't find time out message in the queue :" + msg.arg1);
                }
                // remove cache
                metaHolder.remove(key);
                break;
            // Handle normal message
            default:
                Registrant resgister = removeTimeoutMessage(msg.what);
                if (resgister == null) {
                    XviewLog.w(this.getClass().getName()
                            + " Igore message client don't expect callback :"
                            + msg.what);
                    return;
                }
                Object origObject = resgister.getObject();
                if (resgister.getHandler() != null) {
                    caller = Message.obtain(resgister.getHandler(),
                            resgister.getWhat());
                    JNIResponse jniRes = (JNIResponse) msg.obj;
                    jniRes.callerObject = origObject;
                    caller.obj = jniRes;
                } else {
                    XviewLog.w("Doesn't find  message in the queue :" + msg.arg1);
                }
                break;
        }

        if (caller == null) {
            XviewLog.w(" can not send message:" + msg.what
                    + " to target caller is null");
        } else {
            if (caller.getTarget() == null) {
                XviewLog.w(" can not send message:" + msg.what
                        + " to target caller target(" + caller.what
                        + ") is null");
            }
            caller.sendToTarget();
        }
    }

}
