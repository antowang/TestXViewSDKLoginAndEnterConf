package com.cinlan.xview.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cinlan.xview.bean.User;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.msg.MediaEntity;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.SPUtil;
import com.cinlankeji.khb.iphone.R;

public class ConfDeviceAdapter extends BaseAdapter {

	private Activity context;
	private List<UserDevice> devices = new ArrayList<UserDevice>();
	private List<MediaEntity> medias = new ArrayList<MediaEntity>();

	public ConfDeviceAdapter(Activity context, List<UserDevice> devices,
			List<MediaEntity> entitys) {
		this.context = context;
		this.devices = devices;
		this.medias = entitys;
		// Collections.sort(devices);
	}

	public void update(List<UserDevice> devices, List<MediaEntity> medias) {
		if (devices == null)
			return;
		this.devices = devices;
		this.medias = medias;
		// Collections.sort(devices);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return devices.size() + medias.size();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (position < devices.size()) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public Object getItem(int position) {
		if (position < devices.size()) {
			return devices.get(position);
		} else if (position >= devices.size()) {
			return medias.get(position - devices.size() + 1);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		MediaHolder holder1 = null;
		UserViewHolder holder = null;
		switch (type) {
		case 0:
			if (devices == null)
				return null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_conf_userlist_xviewsdk, null);
				holder = new UserViewHolder();
				holder.item_tv_username = (TextView) convertView
						.findViewById(R.id.item_tv_username_xviewsdk);
				holder.iv_has_audio = (ImageView) convertView
						.findViewById(R.id.iv_has_audio_xviewsdk);
				holder.iv_has_video = (ImageView) convertView
						.findViewById(R.id.iv_has_video_xviewsdk);
				holder.ivUserListIcon = (ImageView) convertView
						.findViewById(R.id.ivUserListIcon_xviewsdk);
				convertView.setTag(holder);
			} else {
				holder = (UserViewHolder) convertView.getTag();
			}

			UserDevice userDevice = devices.get(position);
			User user = userDevice.getUser();
			VideoDevice device = userDevice.getDevice();

			// No Duvice.
			if (device == null) {
				holder.iv_has_video.setVisibility(View.INVISIBLE);
			}

			// Random set picture.
			int number = getRandomNumber(1, 4);
			int[] imageIcons = new int[] { R.drawable.userlist_icon1_xviewsdk,
					R.drawable.userlist_icon2_xviewsdk, R.drawable.userlist_icon3_xviewsdk,
					R.drawable.userlist_icon4_xviewsdk };
			holder.ivUserListIcon.setImageResource(imageIcons[number - 1]);

			if (user != null) {
				// Set Name.
				if (GlobalHolder.getInstance().getCurrentUser().getmUserId() == user
						.getmUserId()) {
					holder.item_tv_username.setText(user.getNickName()
							+ context.getResources().getString(R.string.local_xviewsdk));
				} else {
					holder.item_tv_username.setText(user.getNickName());
				}

				holder.iv_has_audio.setVisibility(View.INVISIBLE);
				holder.iv_has_video.setVisibility(View.INVISIBLE);

				if (user.getmUserId() == GlobalHolder.getInstance()
						.getLocalUserId()) {
					/**
					 * Self.
					 */
					int configIntValue = SPUtil.getConfigIntValue(context,
							"local", 0);
					/**
					 * 1 - HaveCamera, opened.<br>
					 * 2 - NoCamera.<br>
					 * 0 - HaveCamera, not opened.<br>
					 * 4 - HaveCamera, disable.
					 */
					if (configIntValue == 1) {
						holder.iv_has_video.setVisibility(View.VISIBLE);
						holder.iv_has_video
								.setImageResource(R.drawable.video_userlist_see_xviewsdk);
					} else if (configIntValue == 0) {
						holder.iv_has_video.setVisibility(View.VISIBLE);
						holder.iv_has_video
								.setImageResource(R.drawable.video_userlist_normal_xviewsdk);
					} else if (configIntValue == 2) {
						holder.iv_has_video.setVisibility(View.INVISIBLE);
					} else if (configIntValue == 4) {
						holder.iv_has_video.setVisibility(View.VISIBLE);
						holder.iv_has_video
								.setImageResource(R.drawable.video_userlist_no_xviewsdk);
					}
				} else {
					/**
					 * Other User.
					 */
					// Whether it has been viewed.
					boolean contains = GlobalHolder.getInstance().mOpenUerDevList
							.contains(userDevice);

					if (contains) {
						holder.iv_has_video.setVisibility(View.VISIBLE);
						holder.iv_has_video
								.setImageResource(R.drawable.video_userlist_see_xviewsdk);
					} else {
						if (device != null) {
							holder.iv_has_video.setVisibility(View.VISIBLE);
							holder.iv_has_video
									.setImageResource(R.drawable.video_userlist_normal_xviewsdk);
						}
					}

					if (device != null && device.getDisable() == 1) {
						holder.iv_has_video.setVisibility(View.VISIBLE);
						holder.iv_has_video
								.setImageResource(R.drawable.video_userlist_no_xviewsdk);
					}
				}

				Integer speak = GlobalHolder.getInstance().mSpeakUers.get(user
						.getmUserId());

				if (speak != null && speak == 3) {
					holder.iv_has_audio.setVisibility(View.VISIBLE);
				}

				List<VideoDevice> list = GlobalHolder.getInstance().videodevices
						.get(user.getmUserId());
				// Redundant camera.
				if (list != null && list.size() > 1) {
					for (int i = 0; i < list.size() && device != null; i++) {
						VideoDevice device2 = list.get(i);
						if (device2.getId().equals(device.getId())) {
							if ("file".equals(device2.getVideotype())) {
								holder.item_tv_username.setText(user
										.getNickName()
										+ context.getResources().getString(
												R.string.mediashare_xviewsdk));
							} else {
								holder.item_tv_username.setText(user
										.getNickName() + "_" + (i + 1));
							}
						}
					}
				}
			}

			break;

		case 1:
			if (medias == null)
				return null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_conf_medialist_xviewsdk, null);
				holder1 = new MediaHolder();
				holder1.item_tv_mediaName = (TextView) convertView
						.findViewById(R.id.item_tv_medianame2_xviewsdk);
				holder1.iv_has_video = (ImageView) convertView
						.findViewById(R.id.iv_has_video2_xviewsdk);
				holder1.ivUserListIcon2 = (ImageView) convertView
						.findViewById(R.id.ivUserListIcon2_xviewsdk);
				convertView.setTag(holder1);
			} else {
				holder1 = (MediaHolder) convertView.getTag();
			}

			// Random set picture.
			int number1 = getRandomNumber(1, 4);
			int[] imageIcons1 = new int[] { R.drawable.userlist_icon1_xviewsdk,
					R.drawable.userlist_icon2_xviewsdk, R.drawable.userlist_icon3_xviewsdk,
					R.drawable.userlist_icon4_xviewsdk };
			holder1.ivUserListIcon2.setImageResource(imageIcons1[number1 - 1]);

			MediaEntity entity = medias.get(position - devices.size());
			holder1.item_tv_mediaName.setText(entity.getName());
			holder1.iv_has_video.setVisibility(View.INVISIBLE);
			boolean contains = GlobalHolder.getInstance().mOpenMedia.contains(entity);

			if (contains) {
				holder1.iv_has_video.setVisibility(View.VISIBLE);
				holder1.iv_has_video.setImageResource(R.drawable.video_userlist_see_xviewsdk);
			} else {
				holder1.iv_has_video.setVisibility(View.VISIBLE);
				holder1.iv_has_video.setImageResource(R.drawable.video_userlist_normal_xviewsdk);
			}
			break;
		}
		return convertView;
	}

	private static class UserViewHolder {
		TextView item_tv_username;
		ImageView iv_has_audio;
		ImageView iv_has_video;
		ImageView ivUserListIcon;
	}

	private static class MediaHolder {
		TextView item_tv_mediaName;
		ImageView iv_has_video;
		ImageView ivUserListIcon2;
	}

	private int getRandomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max) % (max - min + 1) + min;
	}
}
