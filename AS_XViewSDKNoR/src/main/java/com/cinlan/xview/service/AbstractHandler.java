package com.cinlan.xview.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.cinlan.jni.ImRequest;
import com.cinlan.xview.utils.XviewLog;


public abstract class AbstractHandler extends Handler {

    private static final int LOGIN_REQUEST_TIME_OUT = 0;
    private static final int ENTERCONF_REQUEST_TIME_OUT = 1;

    private SparseArray<Meta> metaHolder = new SparseArray<Meta>();

    public static final int DEFAULT_TIME_OUT_SECS = 3;
    public static final int JNI_REQUEST_LOG_IN = 2;
    public static final int JNI_REQUEST_ENTER_CONF = 3;

    public AbstractHandler() {
        super(Looper.getMainLooper());
    }

    public Message initTimeoutMessage(int mointorMessageID, long timeOutSec,
                                         Registrant caller) {
        // Create unique message object
        Message msg;
        if (mointorMessageID == JNI_REQUEST_LOG_IN) {
            msg = Message.obtain(this, LOGIN_REQUEST_TIME_OUT, mointorMessageID,
                    0, new Object());
        } else {
            msg = Message.obtain(this, ENTERCONF_REQUEST_TIME_OUT, mointorMessageID,
                    0, new Object());
        }
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
    public Registrant removeTimeoutMessage(int mointorMessageID) {
        Meta meta = metaHolder.get(Integer.valueOf(mointorMessageID));
        metaHolder.remove(Integer.valueOf(mointorMessageID));
        if (meta != null && mointorMessageID == JNI_REQUEST_LOG_IN) {
            this.removeMessages(LOGIN_REQUEST_TIME_OUT, meta.timeoutMessage.obj);
            return meta.caller;
        } else if (meta != null && mointorMessageID == JNI_REQUEST_ENTER_CONF) {
            this.removeMessages(ENTERCONF_REQUEST_TIME_OUT, meta.timeoutMessage.obj);
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
            case LOGIN_REQUEST_TIME_OUT: {
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
            }
            case ENTERCONF_REQUEST_TIME_OUT: {
                Integer key = Integer.valueOf(msg.arg1);
                Meta meta = metaHolder.get(key);
                if (meta != null && meta.caller != null) {

                    JNIResponse jniRes = new JNIResponse(JNIResponse.Result.ENTER_CONF_TIME_OUT);
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
            }
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
