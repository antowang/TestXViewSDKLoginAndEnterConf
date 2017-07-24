package com.cinlan.xview.ui.p2p.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cinlan.xview.bean.ClientDev;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlankeji.khb.iphone.R;

import java.util.List;

class ExpAdapter extends BaseExpandableListAdapter {

    private Context mContext;

    private List<ClientDev> mClientList = null;

    private PlantControlListener mControlListener;


    void setControlListener(PlantControlListener controlListener) {
        this.mControlListener = controlListener;
    }

    ExpAdapter(Context context, List<ClientDev> list) {
        mContext = context;
        mClientList = list;
    }

    @Override
    public VideoDevice getChild(int groupPosition, int childPosition) {
        return mClientList.get(groupPosition).getVideoDevLists().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        ChildHolder childholder;

        if (convertView != null) {
            view = convertView;
            childholder = (ChildHolder) view.getTag();

        } else {
            view = View.inflate(mContext, R.layout.child_view, null);
            childholder = new ChildHolder();
            childholder.mImage = (ImageView) view.findViewById(R.id.image);
            view.setTag(childholder);
        }
        final VideoDevice child = getChild(groupPosition, childPosition);
        if (child.getDisable() == 1) {
            childholder.mImage.setBackgroundResource(R.drawable.video_userlist_no_xviewsdk);
        } else

        {
            if (child.isOpen()) {
                childholder.mImage.setBackgroundResource(R.drawable.video_userlist_see_xviewsdk);
            } else {
                childholder.mImage.setBackgroundResource(R.drawable.video_userlist_normal_xviewsdk);
            }
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(child.getDisable() == 1){
                    Toast.makeText(mContext,"对方设置禁用",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (child.isOpen()) {
                    mControlListener.onCloseVideo(mClientList.get(groupPosition).getUser().getmUserId(),childPosition);
                } else {
                    mControlListener.onOpenVideo(mClientList.get(groupPosition).getUser().getmUserId(),childPosition);
                }
            }
        });
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mClientList.get(groupPosition).getVideoDevLists().size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mClientList.get(groupPosition).getUser().getNickName();
    }

    @Override
    public int getGroupCount() {
        return mClientList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        View view;
        GroupHolder groupholder;
        if (convertView != null) {
            view = convertView;
            groupholder = (GroupHolder) view.getTag();
        } else {
            view = View.inflate(mContext, R.layout.group_text_view, null);
            groupholder = new GroupHolder();
            groupholder.title = (TextView) view.findViewById(R.id.group);
            groupholder.indicator = (ImageView) view.findViewById(R.id.id_indicator);
            view.setTag(groupholder);
        }
        groupholder.title.setText(getGroup(groupPosition));
        if (!isExpanded) {
            groupholder.indicator.setImageResource(R.drawable.arr_right);
        } else {
            groupholder.indicator.setImageResource(R.drawable.arr_down);
        }

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    private class GroupHolder {
        TextView title;
        ImageView indicator;
    }

    private class ChildHolder {
        ImageView mImage;
    }


    interface PlantControlListener{

        void onCloseVideo(Long userId,int pos);

        void onOpenVideo(Long userId ,int pos);

    }
}


   
