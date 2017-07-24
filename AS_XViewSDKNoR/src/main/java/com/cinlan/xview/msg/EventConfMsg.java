package com.cinlan.xview.msg;

/**
 * Created by sivin on 2017/5/12.
 */

public class EventConfMsg {

    private int msgType;

    private long extra;

    private boolean bExtra = false;

    private int extra2;

    public EventConfMsg(int msgType, long extra, int extra2, boolean bExtra) {
        this.msgType = msgType;
        this.extra = extra;
        this.bExtra = bExtra;
        this.extra2 = extra2;
    }

    public EventConfMsg(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgType() {
        return msgType;
    }

    public long getExtra() {
        return extra;
    }

    public void setExtra(long extra) {
        this.extra = extra;
    }

    public boolean isbExtra() {
        return bExtra;
    }

    public void setbExtra(boolean bExtra) {
        this.bExtra = bExtra;
    }

    public int getExtra2() {
        return extra2;
    }

    public void setExtra2(int extra2) {
        this.extra2 = extra2;
    }

    public EventConfMsg(int msgType, long extra, boolean bExtra) {
        this.msgType = msgType;
        this.extra = extra;
        this.bExtra = bExtra;
    }

    public EventConfMsg(int msgType, long extra) {
        this.msgType = msgType;
        this.extra = extra;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
