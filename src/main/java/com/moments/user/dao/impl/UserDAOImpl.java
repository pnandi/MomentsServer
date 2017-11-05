package com.moments.user.dao.impl;

import java.text.ParseException;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moments.db.obj.UserData;
import com.moments.db.utils.NoSQLDBUtils;
import com.moments.user.dao.UserDAO;
import com.moments.utils.DateTimeHelper;
import com.mongodb.client.model.Filters;

public class UserDAOImpl extends NoSQLDBUtils implements UserDAO {

	private static Logger LOGGER = LoggerFactory.getLogger(UserDAOImpl.class);
	
	@Override
	public UserData getUserDataByUsername(String username) {
		
		Document user = null;
		UserData userData = null;
	    Bson filterByUsername = Filters.eq("username", username);
	    user = getUsersCollection().find(filterByUsername).first();
	    try {
	    		userData = mapper.readValue(user.toJson(), new TypeReference<UserData>(){});
	    }catch(Exception e) {
	    		LOGGER.error("Error while fetching User Data", e);
	    }
	    
		return userData;
	}

	@Override
	public void createuser(UserData user) {
		
		Map userMap = mapper.convertValue(user, Map.class);
		
		getUsersCollection().insertOne(new Document(userMap));
	}
	
	

}
