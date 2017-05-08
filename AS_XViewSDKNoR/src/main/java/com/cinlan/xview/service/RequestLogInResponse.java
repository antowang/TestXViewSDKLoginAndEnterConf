package com.cinlan.xview.service;

import com.cinlan.xview.bean.User;

public class RequestLogInResponse extends JNIResponse {

	User u;

	public RequestLogInResponse(User u, Result res) {
		super(res);
		this.u = u;
		this.res = res;
	}

	public RequestLogInResponse(User u, Result res, Object originObject) {
		super(res);
		this.u = u;
		this.res = res;
		this.callerObject = originObject;
	}

	public User getUser() {
		return u;
	}

}
