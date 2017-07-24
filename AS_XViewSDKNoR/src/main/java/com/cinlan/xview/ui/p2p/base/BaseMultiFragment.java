package com.cinlan.xview.ui.p2p.base;

import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cinlan.xview.bean.SurfaceWrap;
import com.cinlan.xview.ui.p2p.view.CommunicateFragment;

/**
 * 多人会议BaseFragment
 * Created by sivin on 2017/5/23.
 */

public class BaseMultiFragment extends CommunicateFragment {


    /**
     * 画中画切三分屏
     *
     * @param three 第三个surfaceView
     */
    protected void pipToThreeLayout(String vdId,SurfaceView three) {

        if(isVdHaveOpened(vdId)) return;

        //从两路视频中取出原来的surfaceView
        SurfaceView full = removeFullSurfaceView();
        SurfaceView small = removeSmallSurfaceView();

        mRootView.removeAllViews();

        full.setZOrderOnTop(false);
        small.setZOrderOnTop(false);
        three.setZOrderOnTop(false);

        setQuarterLp(full);
        setQuarterLp(small);

        locationSurfaceView(full, 0, true);
        locationSurfaceView(small, 1, true);

        setQuarterLp(three);
        mRootView.addView(three);
        three.setTranslationY(mScreenSize.y / 2);
        three.setTranslationX(mScreenSize.x / 4);
        addOpenedSvf(vdId,three);
    }


    /**
     * 直接三分屏显示
     * @param sfv      sfv
     * @param position position
     */
    public void threeLayout(String vdId,SurfaceView sfv, int position) {
        if(isVdHaveOpened(vdId)) return;

        if(position <2){
            setQuarterLp(sfv);
            locationSurfaceView(sfv, position, true);

        }else{
            setQuarterLp(sfv);
            mRootView.addView(sfv);
            sfv.setTranslationY(mScreenSize.y / 2);
            sfv.setTranslationX(mScreenSize.x / 4);
        }

        addOpenedSvf(vdId,sfv);
    }


    /**
     * 从三分屏转四分屏布局
     * @param sfv 第四个surfaceView
     */
    public void threeToQuarterLayout(String vdId,SurfaceView sfv) {
        if(isVdHaveOpened(vdId)) return;
        SurfaceView three = mOpenedSurfaceList.get(2).getSuf();
        mRootView.removeView(three);
        setQuarterLp(three);
        locationSurfaceView(three, 2, true);
        setQuarterLp(sfv);
        locationSurfaceView(sfv, 3, true);
        addOpenedSvf(vdId,sfv);
    }

    /**
     * 直接四分屏布局
     * @param sfv      要布局的surfaceView
     * @param position 布局位置
     */
    public void quarterLayout(String vdId,SurfaceView sfv, int position) {
        if(isVdHaveOpened(vdId)) return;
        setQuarterLp(sfv);
        locationSurfaceView(sfv, position, true);
        addOpenedSvf(vdId,sfv);
    }


    /**
     * 设置surfaceView lp 是四分屏
     *
     * @param surfaceView surfaceView
     */
    private void setQuarterLp(SurfaceView surfaceView) {
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = mScreenSize.x / 2;
        params.height = mScreenSize.y / 2;
        surfaceView.setLayoutParams(params);
    }


    /**
     * 排布surfaceView
     *
     * @param surfaceView surfaceView
     * @param position    添加位置
     * @param add         是否添加到父容器中
     */
    private void locationSurfaceView(SurfaceView surfaceView, int position, boolean add) {
        int offsetX = mScreenSize.x / 2;
        int offsetY = mScreenSize.y / 2;
        if (add){
            mRootView.addView(surfaceView);
        }
        surfaceView.setTranslationX(offsetX * (position % 2));
        surfaceView.setTranslationY(offsetY * (position / 2));
    }


    @Override
    protected void rmSfFromLayout(String vdId, SurfaceWrap wrap, int num) {
        super.rmSfFromLayout(vdId,wrap,num);

        SurfaceView surfaceView = wrap.getSuf();
        if (num == 3) {
            //移除该移除的部分,同时剩下的切换成画中画模式
            mRootView.removeAllViews();
            removeOpenedVd(vdId,surfaceView);
            switchToPip(mOpenedSurfaceList.get(0).getSuf(), mOpenedSurfaceList.get(1).getSuf());

        } else if (num == 4) {
            mRootView.removeAllViews();
            removeOpenedVd(vdId,surfaceView);
            //将剩下的三个切换成倒品的模式
            setQuarterLp(mOpenedSurfaceList.get(0).getSuf());
            locationSurfaceView(mOpenedSurfaceList.get(0).getSuf(), 0, true);
            setQuarterLp(mOpenedSurfaceList.get(1).getSuf());
            locationSurfaceView(mOpenedSurfaceList.get(1).getSuf(), 1, true);

            setQuarterLp(mOpenedSurfaceList.get(2).getSuf());
            mRootView.addView(mOpenedSurfaceList.get(2).getSuf());
            mOpenedSurfaceList.get(2).getSuf().setTranslationY(mScreenSize.y / 2);
            mOpenedSurfaceList.get(2).getSuf().setTranslationX(mScreenSize.x/4);
        }
    }

}
