package com.cinlan.xview.bean.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.cinlan.xview.widget.DOCImageView;

public class Page implements Serializable {

	private int index; // �ڶ���ҳ
	private String paths; // ͼƬ·��
	private DOCImageView mDOCImageView;
	private String wbid;

	public String getWbid() {
		return wbid;
	}

	public void setWbid(String wbid) {
		this.wbid = wbid;
	}

	private Map<DOCImageView, Bitmap> map_bit = new HashMap<DOCImageView, Bitmap>();

	public Map<DOCImageView, Bitmap> getMap_bit() {
		return map_bit;
	}

	public void setMap_bit(Map<DOCImageView, Bitmap> map_bit) {
		this.map_bit = map_bit;
	}

	public DOCImageView getWbImageView() {
		return mDOCImageView;
	}

	public void setWbImageView(DOCImageView mDOCImageView) {
		this.mDOCImageView = mDOCImageView;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getPaths() {
		return paths;
	}

	public void setPaths(String paths) {
		this.paths = paths;
	}

}
