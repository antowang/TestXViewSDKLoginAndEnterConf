package com.cinlan.xview.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.cinlankeji.khb.iphone.R;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.utils.GlobalHolder;


public class ConfUserListAdapter extends BaseAdapter {

	private Activity context;
	private List<User> users=new ArrayList<User>();
	
	
	public ConfUserListAdapter(Activity context,List<User> users){
		this.context=context;
		this.users=users;
		Collections.sort(users);
	}
	
	
	
	public void update(List<User> users){
		if(users==null)
			return;
		this.users=users;
		Collections.sort(users);
		notifyDataSetChanged();
	}
	
	
	@Override
	public int getCount() {
		if (users != null)
			return users.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (users != null)
			return users.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (users == null)
			return null;
		UserViewHolder holder=null;
		if(convertView==null){
			convertView=View.inflate(context, R.layout.item_conf_userlist_xviewsdk, null);
			holder=new UserViewHolder();
			holder.item_tv_username=(TextView)convertView.findViewById(R.id.item_tv_username_xviewsdk);
			holder.iv_has_audio=(ImageView)convertView.findViewById(R.id.iv_has_audio_xviewsdk);
			holder.iv_has_video=(ImageView)convertView.findViewById(R.id.iv_has_video_xviewsdk);
			convertView.setTag(holder);
		}else{
			holder=(UserViewHolder) convertView.getTag();
		}
		
		final User user=users.get(position);
		if(GlobalHolder.getInstance().getCurrentUser().getmUserId()==user.getmUserId()){
			holder.item_tv_username.setText(user.getNickName()+" (��)");
		}else{
			holder.item_tv_username.setText(user.getNickName());
		}
		
		holder.iv_has_audio.setVisibility(View.INVISIBLE);
		holder.iv_has_video.setVisibility(View.INVISIBLE);
		
		boolean contains = GlobalHolder.getInstance().mOpenUerDevList.contains(user);
		if(contains){
			holder.iv_has_video.setVisibility(View.VISIBLE);
		}
		Integer speak = GlobalHolder.getInstance().mSpeakUers.get(user.getmUserId());
		if(speak!=null&&speak==3){
			holder.iv_has_audio.setVisibility(View.VISIBLE);
		}
		return convertView;
	}
	

	 
	private  static class UserViewHolder{
		TextView item_tv_username;
		ImageView iv_has_audio;
		ImageView iv_has_video;
	}
}
