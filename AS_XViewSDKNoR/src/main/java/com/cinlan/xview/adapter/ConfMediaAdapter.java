package com.cinlan.xview.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cinlan.xview.msg.MediaEntity;

public class ConfMediaAdapter extends BaseAdapter {

	private List<MediaEntity> medias = new ArrayList<MediaEntity>();
	private Activity context;
	
	public ConfMediaAdapter(Activity context,List<MediaEntity> medias){
		this.context = context;
		this.medias = medias;
	}
	
	public void update(List<MediaEntity> medias){
		if(medias==null){
			return ;
		}
		this.medias = medias;
		notifyDataSetChanged();
	}
	
	
	
	@Override
	public int getCount() {
		if(medias != null)
			return medias.size();
		
		return 0;
		
	}


	@Override
	public Object getItem(int position) {
		if(medias != null)
		return medias.get(position);
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		

		
		return convertView;
	}
	
}
