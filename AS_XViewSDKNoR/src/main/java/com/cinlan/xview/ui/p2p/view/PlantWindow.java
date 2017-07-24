package com.cinlan.xview.ui.p2p.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.cinlan.xview.bean.ClientDev;
import com.cinlan.xview.ui.callback.VdOperatorResultCallback;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlankeji.khb.iphone.R;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * Created by Sivin on 2017/5/28.
 */

public class PlantWindow extends PopupWindow implements VdOperatorResultCallback {

    private Context mContext;

    private ExpandableListView listView;

    private ExpAdapter mAdapter;

    private List<ClientDev> list = new ArrayList<>();


    public PlantWindow(Context context, int width, ExpAdapter.PlantControlListener l) {
        mContext = context;
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setWidth(width);
        setAnimationStyle(R.style.AnimationLeftFade);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_popwindow, null);
        setContentView(view);
        setBackgroundDrawable(new ColorDrawable(0xffffffff));
        setFocusable(true);
        listView = (ExpandableListView) view.findViewById(R.id.ex_list_view);
        listView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        mAdapter = new ExpAdapter(context, list);
        mAdapter.setControlListener(l);
        listView.setAdapter(mAdapter);
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        refreshData();
    }

    @Override
    public void operatorCallback(int result) {

        if(!isShowing()) return;

        switch (result) {
            case OPERATOR_OK:
                refreshData();
                break;

            case NO_PLACE:
                Toast.makeText(mContext, "请先关闭一个设备", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    private void refreshData() {
        list.clear();
        for (ClientDev dev : GlobalHolder.mClientDevList) {
            list.add(dev);
        }
        mAdapter.notifyDataSetChanged();
    }


}
