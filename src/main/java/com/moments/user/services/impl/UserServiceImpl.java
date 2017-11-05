package com.moments.user.services.impl;

import com.moments.db.obj.UserData;
import com.moments.user.dao.UserDAO;
import com.moments.user.dao.impl.UserDAOImpl;
import com.moments.user.services.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public UserData getUser(String username) {
		UserDAO userDAO = new UserDAOImpl();
		
		return userDAO.getUserDataByUsername(username);

	}

	@Override
	public void createUser(UserData user) {
		UserDAO userDAO = new UserDAOImpl();
		userDAO.createuser(user);
		
	}

}
