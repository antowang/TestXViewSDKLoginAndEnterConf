package com.cinlan.xview.utils;


import java.util.ArrayList;
import java.util.List;

import com.cinlan.xview.service.JNIService;

import android.app.Activity;
import android.content.Intent;


public class ActivityHolder {

    @SuppressWarnings("unused")
	private final static String TAG = "ActivityHolder";  
    public  static List<Activity>  activityList = new ArrayList<Activity>();;
    private static ActivityHolder activityHolder;
    private static java.lang.String XTAG = ActivityHolder.class.getSimpleName();

    private ActivityHolder() {
    }

    public static synchronized ActivityHolder getInstance() {
        if (activityHolder == null) {
            activityHolder = new ActivityHolder();
        }
        return activityHolder;
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
        	if(checkActivityIsVasivle(activity)){
        	   removeActivity(activity);
        	   activityList.add(activityList.size(), activity);
        	}else{
        	  activityList.add(activity);
        	}
           
        }
    }

    public static void finishAllActivity() {
        XviewLog.i(XTAG, "finishAllActivity");
        int size = activityList.size();
        for (int i = size-1 ; i >= 0; i--) {
            Activity activity = activityList.get(i);
            if (activity != null) {
                XviewLog.i(XTAG, "activity.finish() = " + activity.toString());
                activity.finish();
            }
            activityList.remove(activity);
        }
    }
    
    public static void removeActivity(Activity activity) {
        try {
        	if(activityList != null){
        		 activityList.remove(activity);
        	}
        } catch (Exception e) {
        }
    }
    
    
    public boolean checkActivityIsVasivle(Activity activity) {
    	return activityList.contains(activity);
    }
}
