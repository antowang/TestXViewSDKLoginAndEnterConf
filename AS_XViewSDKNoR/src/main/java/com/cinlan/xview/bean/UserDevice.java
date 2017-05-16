package com.cinlan.xview.bean;

import java.io.Serializable;

import com.cinlan.xview.utils.GlobalHolder;

/**
 * User和VideoDevice的集合.
 * 
 * @author ChongXue
 * @date 2017-3-14
 */
public class UserDevice implements Serializable, Comparable<UserDevice> {

	private static final long serialVersionUID = 8411883904738942373L;
	private User user;
	private VideoDevice device;
	private boolean open;

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public VideoDevice getDevice() {
		return device;
	}

	public void setDevice(VideoDevice device) {
		this.device = device;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDevice other = (UserDevice) obj;
		/**********测试**********/
		if(device == null)
			return false;
		if(device.getId() == null)
			return false;
		if(other.device.getId() == null)
			return false;
		/********************/
		if (user.getmUserId() == other.user.getmUserId()
				&& device.getId() == other.device.getId()) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (user.getmUserId() ^ (user.getmUserId() >>> 32));
		return result;
	}

	@Override
	public int compareTo(UserDevice another) {
		// make sure current user align first position
		if (another == null)
			return 1;
		if (this.user.getmUserId() == GlobalHolder.getInstance()
				.getLocalUserId()) {
			return -1;
		}
		if (another.user.getmUserId() == GlobalHolder.getInstance()
				.getLocalUserId()) {
			return 1;
		}
		return 0;
	}
}
