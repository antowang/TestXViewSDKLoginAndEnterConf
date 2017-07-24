package com.XunLiu.jni;

import android.content.Context;

public class NativeInitializer {

	public native void initialize(Context context);
	
	private static NativeInitializer instance;
	
	  
	private NativeInitializer(Context context) {
		
	}
	public static NativeInitializer getIntance(Context context) {
		if (instance == null) {
			instance = new NativeInitializer(context);
		}
		return instance;
	}
}
