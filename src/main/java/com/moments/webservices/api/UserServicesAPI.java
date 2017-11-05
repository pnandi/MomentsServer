package com.moments.webservices.api;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moments.db.obj.UserData;
import com.moments.user.services.UserService;
import com.moments.user.services.impl.UserServiceImpl;
import com.moments.utils.DateTimeHelper;

@Path("user")
@RequestScoped
public class UserServicesAPI {
	
	private static Logger LOGGER = LoggerFactory.getLogger(UserServicesAPI.class);
	
	//@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(String jsonString) {

		ObjectMapper mapper = new ObjectMapper();
		UserService userService = new UserServiceImpl();
		UserData user = new UserData();
		try {
			JSONObject json = new JSONObject(jsonString);
			
			json.put("lastLogin", DateTimeHelper.parseDateTimeTypeFormat(json.get("lastLogin").toString()).toString());
			LOGGER.info("user last login :{}", json.get("lastLogin").toString());
			user = (UserData) mapper.readValue(jsonString, UserData.class);
			userService.createUser(user);
		} catch (Exception e) {
			LOGGER.error("Error in reading json input", e);
    	   		return Response.status(500).type(MediaType.APPLICATION_JSON).
	    			   entity("{\"error\":\"Error in reading json input\"}").build();
		}
	       return Response.ok("{\"success\":\"User "+user.getUsername()+" created\"}", MediaType.APPLICATION_JSON).build(); 
	}
}
