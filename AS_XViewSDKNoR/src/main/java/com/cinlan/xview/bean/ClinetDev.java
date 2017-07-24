package com.cinlan.xview.bean;

import java.util.List;

/**
 * Created by Sivin on 2017/6/1.
 */

public class ClinetDev {

    private User mUser;

    private List<VideoDevice> mVideoDevLists;

    private boolean isOpened = false;





    /**
     * 这个客户端是否有可用的设备
     * @return true:有, false:没有
     */
    public boolean isCanOpen(){
        for(int i = 0 ; i < mVideoDevLists.size() ; i++){
            if(mVideoDevLists.get(i).getDisable() == 0){
                return true;
            }
        }
        return false;
    }


    /**
     * 获取第一个可用的设备
     * @return videoDevice
     */
    public VideoDevice getFirstEnableDev(){

        for(int i = 0 ; i < mVideoDevLists.size() ; i++){
            if(mVideoDevLists.get(i).getDisable() == 0){
                return mVideoDevLists.get(i);
            }
        }
        return null;
    }


    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public List<VideoDevice> getVideoDevLists() {
        return mVideoDevLists;
    }

    public void setVideoDevLists(List<VideoDevice> videoDevLists) {
        mVideoDevLists = videoDevLists;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }



}
