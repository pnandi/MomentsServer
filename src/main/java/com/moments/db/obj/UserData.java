package com.moments.db.obj;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.moments.utils.JsonDateSerializer;

@JsonIgnoreProperties({ "_id"})

public class UserData{

	private String username;
	private String phoneNo;
	private Date lastLogin;
	private String passHash;
	private boolean hashLastModified;
	private String lastRegisteredDevice;
	private String imageFolder;
	private String messages;
	

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	
	@JsonSerialize(using = JsonDateSerializer.class)	
	public Date getLastLogin() {
		return lastLogin;
	}
	
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public String getPassHash() {
		return passHash;
	}
	public void setPassHash(String passHash) {
		this.passHash = passHash;
	}
	public boolean isHashLastModified() {
		return hashLastModified;
	}
	public void setHashLastModified(boolean hashLastModified) {
		this.hashLastModified = hashLastModified;
	}
	public String getLastRegisteredDevice() {
		return lastRegisteredDevice;
	}
	public void setLastRegisteredDevice(String lastRegisteredDevice) {
		this.lastRegisteredDevice = lastRegisteredDevice;
	}
	public String getImageFolder() {
		return imageFolder;
	}
	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	@Override
	public String toString() {
		return "UserData [username=" + username + ", phoneNo=" + phoneNo + ", lastLogin=" + lastLogin + ", passHash="
				+ passHash + ", hashLastModified=" + hashLastModified + ", lastRegisteredDevice=" + lastRegisteredDevice
				+ ", imageFolder=" + imageFolder + ", messages=" + messages + "]";
	}
}
