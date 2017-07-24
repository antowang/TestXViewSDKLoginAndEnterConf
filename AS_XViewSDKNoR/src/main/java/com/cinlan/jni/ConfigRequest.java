package com.cinlan.jni;

public class ConfigRequest
{
	public native boolean initialize(ConfigRequest request);
	public native void unInitialize();

	public native void setConfigProp(String szItemPath, String szConfigAttr, String szValue);
	public native void getConfigProp(String szItemPath,String szConfigAttr, byte[] pValueBuf, int nBufLen);

	public native void getConfigPropCount(String szItemPath);

	public native void removeConfigProp(String szItemPath,String szConfigAttr);

	public native void setServerAddress(String szServerIP, int nPort);

	public native void setExtStoragePath(String szPath);
}
