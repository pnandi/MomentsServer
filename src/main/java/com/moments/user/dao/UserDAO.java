package com.moments.user.dao;

import com.moments.db.obj.UserData;

public interface UserDAO {

	UserData getUserDataByUsername(String username);
	void createuser(UserData user);
}
