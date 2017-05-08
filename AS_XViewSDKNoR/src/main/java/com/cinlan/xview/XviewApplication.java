package com.cinlan.xview;

import java.lang.ref.WeakReference;
import java.util.Vector;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.cinlan.xview.utils.CrashHandler;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;

public class XviewApplication extends Application {

	private static XviewApplication instance;

	public static XviewApplication getInstance() {
		return instance;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		GlobalHolder.GlobalContext = getApplicationContext();

		instance = this;
//		CrashHandler catchHandler = CrashHandler.getInstance();
//		catchHandler.init(getApplicationContext());
		/**
		 * default VideoFlow.
		 */
		SPUtil.putConfigIntValue(getApplicationContext(), "videoFlow", 512);

		registerActivityLifecycleCallbacks(new LocalActivityLifecycleCallBack());
	}

	private Vector<WeakReference<Activity>> list = new Vector<WeakReference<Activity>>();
	class LocalActivityLifecycleCallBack implements ActivityLifecycleCallbacks {

		private Object mLock = new Object();
		private int refCount = 0;

		@Override
		public void onActivityCreated(Activity activity,
				Bundle savedInstanceState) {
			if (!list.contains(activity)) {
				list.add(new WeakReference<Activity>(activity));
			}
		}

		@Override
		public void onActivityDestroyed(Activity activity) {
			for (int i = 0; i < list.size(); i++) {
				WeakReference<Activity> w = list.get(i);
				Object obj = w.get();
				if (obj != null && ((Activity) obj) == activity) {
					list.remove(i--);
				} else {
					list.remove(i--);
				}
			}
		}

		@Override
		public void onActivityPaused(Activity activity) {

		}

		@Override
		public void onActivityResumed(Activity activity) {
		}

		@Override
		public void onActivitySaveInstanceState(Activity activity,
				Bundle outState) {

		}

		@Override
		public void onActivityStarted(Activity activity) {
			synchronized (mLock) {
				refCount++;
				if (refCount == 1) {
				}
			}
		}

		@Override
		public void onActivityStopped(Activity activity) {
			synchronized (mLock) {
				refCount--;
				if (refCount == 0) {
					if (mOnsetHomeListener != null)
						mOnsetHomeListener.backHome();
				}
			}
		}

	}

	private OnsetHomeListener mOnsetHomeListener;

	public interface OnsetHomeListener {
		void backHome();
	}

	public OnsetHomeListener getmOnsetHomeListener() {
		return mOnsetHomeListener;
	}

	public void setmOnsetHomeListener(OnsetHomeListener mOnsetHomeListener) {
		this.mOnsetHomeListener = mOnsetHomeListener;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		GlobalHolder.getInstance().mOpenUers.clear();
		GlobalHolder.getInstance().mOpenMedia.clear();
		GlobalHolder.getInstance().list.clear();
		GlobalHolder.getInstance().mSpeakUers.clear();
		GlobalHolder.getInstance().mUers.clear();
		GlobalHolder.getInstance().pages.clear();
		GlobalHolder.getInstance().userdevices.clear();
		GlobalHolder.getInstance().videodevices.clear();
		GlobalHolder.getInstance().mDocShares.clear();
	}

}
