package com.cinlan.xview.msg;

import com.cinlan.xview.bean.ClientDev;

/**
 * Created by Administrator on 2017/6/5.
 */
public class ClientEnterMsg {

    private int msgType;

    private long mUserId;

    private ClientDev mDev;


    public ClientEnterMsg(int msgType, long mUserId, ClientDev mDev) {
        this.msgType = msgType;
        this.mUserId = mUserId;
        this.mDev = mDev;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public long getmUserId() {
        return mUserId;
    }

    public void setmUserId(long mUserId) {
        this.mUserId = mUserId;
    }

    public ClientDev getmDev() {
        return mDev;
    }

    public void setmDev(ClientDev mDev) {
        this.mDev = mDev;
    }
}
