package com.cinlan.xview.msg;

/**
 * eventBus 事件类型常亮类
 * Created by sivin on 2017/5/12.
 */

public class EventMsgType {

    /**
     * 新用户进入会议,EventBus回调
     */
    public static final int ON_NEW_USER_ENTER = 0x00001;


    /**
     * 用户登出消息
     */
    public static final int ON_USER_LOGOUT = 0x00002;


    /**
     * 会议成员退出
     */
    public static final int ON_MEMBER_EXIT = 0x00003;

    /**
     * 设备状态更新,例如摄像头禁用开启等
     */
    public static final int ON_VIDEO_COME = 0x00004;


}
