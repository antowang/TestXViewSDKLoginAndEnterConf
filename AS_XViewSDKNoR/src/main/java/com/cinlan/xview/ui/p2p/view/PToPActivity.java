package com.cinlan.xview.ui.p2p.view;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cinlan.xview.ui.p2p.base.BaseConfActivity;
import com.cinlankeji.khb.iphone.R;

/**
 *
 * Created by Administrator on 2017/5/11.
 */

public class PToPActivity extends BaseConfActivity {

    /**
     * 显示视频画面的Fragment
     */
    private CommunicateFragment mCommunicateFragment;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptop_layout);
        addFragment();
    }

    private void addFragment() {
        mCommunicateFragment = new CommunicateFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.id_video_container, mCommunicateFragment);
        ft.commit();
    }

}
