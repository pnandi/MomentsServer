package com.moments.security.service.impl;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moments.db.obj.UserData;
import com.moments.security.service.MLAuthenticationService;
import com.moments.security.utils.EncrptionHelper;
import com.moments.user.services.UserService;
import com.moments.user.services.impl.UserServiceImpl;

public class MLAuthenticationServiceImpl implements MLAuthenticationService {

	private static Logger LOGGER = LoggerFactory.getLogger(MLAuthenticationServiceImpl.class);
	@Override
	public String encryptUserPwd(String password) {
		
		
		return null;
	}
	
	@Override
	public boolean authenticate(String authCredentials) {

		UserService userService = new UserServiceImpl();
		
		if (null == authCredentials)
			return false;

		final String encodedUserPassword = authCredentials.replaceFirst("Basic" + " ", "");
		String usernameAndPassword = null;
		boolean authenticationStatus = false;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");

			final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();

			// encode user password
			String encrptedPwd = EncrptionHelper.getInstance().encrypt(password);
			
			// get User object by username
			UserData user = userService.getUser(username);
			

			// we have fixed the userid and password as admin
			// call some UserService/LDAP here
			authenticationStatus = encrptedPwd.equals(user.getPassHash());
			
			
		} catch (Exception e) {
			LOGGER.error("error while encrpting password");
		}


		return authenticationStatus;
	}

}
