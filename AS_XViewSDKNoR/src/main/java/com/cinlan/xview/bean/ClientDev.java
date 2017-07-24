package com.cinlan.xview.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sivin on 2017/6/1.
 */

public class ClientDev {
    private User mUser;

    private List<VideoDevice> mVideoDevLists;

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
        if(mVideoDevLists == null){
            mVideoDevLists = new ArrayList<>();
        }
        return mVideoDevLists;
    }

    public void setVideoDevLists(List<VideoDevice> videoDevLists) {
        mVideoDevLists = videoDevLists;
    }

    /**
     * 判断客户端是否有设备打开
     * @return true:当前客户端下有设备打开  , false:当前客户端没有处于正在打开的设备
     */
    public boolean isOpened() {
        for(VideoDevice vd : mVideoDevLists){
            if(vd.isOpen()){
                return true;
            }
        }
        return false;
    }


    /**
     * 获取该客户端下所有处于当前正在打开的设备
     * @return 该Client下当前正在打开的视频设备集
     */
    public List<VideoDevice> getAllOpenVdList(){
        List<VideoDevice> vdList = new ArrayList<>();
        for(VideoDevice vd : mVideoDevLists){
            if(vd.isOpen()){
                vdList.add(vd);
            }
        }
        if(vdList.size() == 0){
            vdList = null;
        }
        return vdList;
    }

}
