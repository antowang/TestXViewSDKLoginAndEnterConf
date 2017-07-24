package com.cinlan.xview.bean;

import android.view.SurfaceView;

/**
 * Created by sivin on 2017/6/6.
 */

public class SurfaceWrap {

    private SurfaceView suf;

    private String videoId;

    public SurfaceWrap(SurfaceView suf, String videoId) {
        this.suf = suf;
        this.videoId = videoId;
    }

    public SurfaceView getSuf() {
        return suf;
    }

    public void setSuf(SurfaceView suf) {
        this.suf = suf;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
