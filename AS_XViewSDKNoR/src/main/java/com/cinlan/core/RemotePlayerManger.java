package com.cinlan.core;

import android.app.Activity;
import android.view.SurfaceView;
import android.view.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/12/21 0021.
 */

public class RemotePlayerManger {
    private Activity mActivity = null;
    private static RemotePlayerManger remotePlayerManger=null;
    Map<String, RemoteSurfaceView> mapView =new HashMap<String, RemoteSurfaceView>();

    private RemotePlayerManger() {
    }

    public SurfaceView getRemoteSurfaceView(Activity activity, String devID, int mode)  {
        mActivity=activity;
        RemoteSurfaceView view = mapView.get(devID);
        if(view==null){
            view = new RemoteSurfaceView(mActivity, mode);
            mapView.put(devID,view);
        }
        return view;
    }

    public RemoteSurfaceView getRemoteSurfaceView(String devID)  {
        return mapView.get(devID);
    }

    public void removeRemoteSurfaceView(final String devID)  {
        if(mActivity==null)
            return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RemoteSurfaceView view= mapView.remove(devID);
                if(view!=null) {
                    //view.FreeDecoder();
                    //view.setVisibility(View.GONE);
                    view=null;
            }
        }});

    }

    public void removeAllRemoteSurfaceView()  {
        if(mActivity==null)
            return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Set<Map.Entry<String, RemoteSurfaceView>> map = mapView.entrySet();
                for (Iterator <Map.Entry<String, RemoteSurfaceView>> iterator = map.iterator(); iterator.hasNext();) {
                    Map.Entry<String, RemoteSurfaceView> entry = iterator.next();
                    RemoteSurfaceView view = entry.getValue();
                    //view.FreeDecoder();
                    //view.setVisibility(View.GONE);
                    view=null;
                }
            }
        });

    }

    public static synchronized RemotePlayerManger getInstance() {
        if (remotePlayerManger == null) {
            synchronized (RemotePlayerManger.class) {
                if (remotePlayerManger == null) {
                    remotePlayerManger = new RemotePlayerManger();
                }
            }
        };
        return remotePlayerManger;
    }


}
