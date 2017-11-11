package com.moments.user.services;

import com.moments.db.obj.UserData;

public interface UserService {

	UserData getUser(String username);
	void createUser(UserData user);
	
}
