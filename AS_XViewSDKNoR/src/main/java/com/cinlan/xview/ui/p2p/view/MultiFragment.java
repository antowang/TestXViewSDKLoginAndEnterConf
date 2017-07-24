package com.cinlan.xview.ui.p2p.view;


import com.cinlan.xview.bean.ClientDev;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.ui.callback.VdOperatorResultCallback;
import com.cinlan.xview.ui.p2p.base.BaseMultiFragment;

import static com.cinlan.xview.ui.callback.VdOperatorResultCallback.NO_PLACE;

/**
 * 多人聊天界面fragment
 * Created by sivin on 2017/5/22.
 */

public class MultiFragment extends BaseMultiFragment {


    public void setOpenCallBack(VdOperatorResultCallback callBack) {
        mOperatorCallBack = callBack;
    }


    @Override
    public void openVideo(long userId, ClientDev dev, int position, boolean isInit) {

        int screenSize = calcScreenNum(isInit);
        if (screenSize == 0) return;

        if (screenSize > 4) {
            if (mOperatorCallBack != null)
                mOperatorCallBack.operatorCallback(NO_PLACE);
            return;
        }

        if (screenSize <= 4) {
            super.openVideo(userId, dev, position, isInit);

            if (screenSize == 3) {
                if (isInit) {
                    //直接三分屏显示
                    pipParentLayoutGone();
                    threeLayout(mWantToShowSf.getVideoId(), mWantToShowSf.getSuf(), mOpenedSurfaceList.size());
                } else {
                    //画中画转三分屏
                    pipToThreeLayout(mWantToShowSf.getVideoId(), mWantToShowSf.getSuf());
                }

            } else if (screenSize >= 4) {  //四个人四分屏显示
                if (isInit) {
                    //直接四分屏
                    pipParentLayoutGone();
                    quarterLayout(mWantToShowSf.getVideoId(), mWantToShowSf.getSuf(), mOpenedSurfaceList.size());
                } else {
                    //三分屏转四分屏
                    threeToQuarterLayout(mWantToShowSf.getVideoId(), mWantToShowSf.getSuf());
                }
            }
        }
    }


    @Override
    public void closeVideo(long userId, VideoDevice vd , boolean isEixt) {
        super.closeVideo(userId, vd, isEixt);
        if (mOpenedSurfaceList.size() >= 3)
            rmSfFromLayout(vd.getId(), mWantToRmSf, mOpenedSurfaceList.size());
    }
}
