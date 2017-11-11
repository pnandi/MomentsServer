package com.moments.security.service;

public interface MLAuthenticationService {

	boolean authenticate(String credentials);
	String encryptUserPwd(String password);
}
