package com.cinlan.xview.bean;

public class PersonalInfo {

	private String address;
	private String birthday;
	private String email;
	private String homepage;
	private String job;
	private String memo;
	private String mobile;
	private String postcode;
	private String realname;
	private String sex;
	private String telephone;

	public PersonalInfo() {
		super();
	}

	public PersonalInfo(String address, String birthday, String email,
			String homepage, String job, String memo, String mobile,
			String postcode, String realname, String sex, String telephone) {
		super();
		this.address = address;
		this.birthday = birthday;
		this.email = email;
		this.homepage = homepage;
		this.job = job;
		this.memo = memo;
		this.mobile = mobile;
		this.postcode = postcode;
		this.realname = realname;
		this.sex = sex;
		this.telephone = telephone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

}
