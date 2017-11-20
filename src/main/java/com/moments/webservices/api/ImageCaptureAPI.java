package com.moments.webservices.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moments.db.utils.GenericUtils;
import com.moments.security.service.MLAuthenticationService;
import com.moments.security.service.impl.MLAuthenticationServiceImpl;
import com.moments.webservices.request.obj.ImageSearchObj;
import com.moments.webservices.services.ImageServices;
import com.moments.webservices.services.impl.ImageServicesImpl;

@Path("image")
@RequestScoped
public class ImageCaptureAPI{
	private static Logger LOGGER = LoggerFactory.getLogger(ImageCaptureAPI.class);
	
	@Path("getImage")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
	public Response getImages(@QueryParam("key") String key, @HeaderParam("authorization") String authString,
	     @QueryParam("isHappy") String isHappyInString,
	     @QueryParam("userName") String userName){
		
		String fileName = key;
		LOGGER.info("*****************************************************");
		LOGGER.info("Request: getImages: isHappy  --> " + isHappyInString);
		LOGGER.info("Request: getImages: userName --> " + userName); 
		LOGGER.info("Request: getImages: fileKey  --> " + key); 
		LOGGER.info("*****************************************************");
		    
		MLAuthenticationService authService = new MLAuthenticationServiceImpl();
	    if(!authService.authenticate(authString)){
	    	   	return Response.status(403).type(MediaType.APPLICATION_JSON).
	    			   entity("{\"error\":\"User not authenticated\"}").build();
	    }
	    
	    boolean isHappy = true;
	    if (isHappyInString.equals("false") || isHappyInString.equals("0")){
		    isHappy = false;
	    }
	    
	    // getting full path
	    String folderName = GenericUtils.getFolderName(userName, isHappy);
       
        // forming key to find small image
        key = folderName  + "/" + "small" + "/" + key ;
        LOGGER.info("Request: getImages: Final fileKey  --> " + key); 
      
		ImageServices imageServices = new ImageServicesImpl();
		ByteArrayOutputStream baos = imageServices.getObjectFromS3("moments-images", key);
		LOGGER.info("Request: getImages: Size of file   --> " + baos.size()); 
     
        
        String headerContent = "attachment; " + "filename = " + fileName;
        
        return Response.ok(baos.toByteArray(),MediaType.APPLICATION_OCTET_STREAM)
        		.header("content-disposition",headerContent)
        		.build();
	}

	@Path("uploadImage")
	@POST
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setImage(@FormDataParam(value = "file") InputStream uploadedInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetail,
	    @FormDataParam("isHappy") String isHappyInString,
		@FormDataParam("userName") String userName,
		@FormDataParam("comment") String comment) {
		
		
		LOGGER.info("*****************************************************");
		LOGGER.info("Request: setImage: isHappy  --> " + isHappyInString);
		LOGGER.info("Request: setImage: userName --> " + userName); 
		LOGGER.info("Request: setImage: comment  --> " + comment); 
		LOGGER.info("Request: setImage: F Detail --> " + fileDetail); 
		LOGGER.info("*****************************************************");
	  
	    boolean isHappy = true;
	    
	    if (isHappyInString.equals("false") || isHappyInString.equals("0")){
		    isHappy = false;
	    }
	    ImageServicesImpl imageServices = new ImageServicesImpl();
          
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[4096];
        boolean result = false;
        
        // Need to get timestamp from device
        //String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        
        String folderName = GenericUtils.getFolderName(userName, isHappy);
        LOGGER.info("Request: setImage: folderName  --> " + folderName);
        
        UUID uuid = UUID.randomUUID();
        String fileNameKey = uuid.toString();
        System.out.println("UUID=" + fileNameKey );
        
        Instant instant = Instant.now();
 
        JSONObject imageJson = GenericUtils.prepareDocumentImages(userName,isHappy,"moments-images", folderName, fileNameKey, ""+ instant, comment );
   
        try {
			while ((len = uploadedInputStream.read(buffer, 0, buffer.length)) != -1) {
			    baos.write(buffer, 0, len);
			}
			result = imageServices.setObjectToS3("moments-images", fileNameKey, folderName , baos, imageJson);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(403).entity(e).build();
	               
		}
        return Response.status(200).entity(result).build();

		//return Response.ok(result).build();
	}
	@Path("getTopImages")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLatestImages(ImageSearchObj searchObj,
			@HeaderParam("authorization") String authString) {
		
		MLAuthenticationService authService = new MLAuthenticationServiceImpl();
		
	       if(!authService.authenticate(authString)){
	    	   	return Response.status(403).type(MediaType.APPLICATION_JSON).
	    			   entity("{\"error\":\"User not authenticated\"}").build();
	        }
	    
	    String username = searchObj.getUsername();
	    String timestamp = searchObj.getTimestamp();
	    String isHappy = searchObj.getIsHappy();
	    
	    LOGGER.info("*****************************************************");
	    LOGGER.info("Request: getLatestImages: username  --> " + username);
	    LOGGER.info("Request: getLatestImages: timestamp --> " + timestamp);
	    LOGGER.info("Request: getLatestImages: isHappy   --> " + isHappy);
	    LOGGER.info("*****************************************************");
	    
	    
	   isHappy = StringUtils.isNotEmpty(isHappy) ? isHappy : null;
	   timestamp = StringUtils.isNotEmpty(timestamp) ? timestamp : null;
	    
		ImageServices imageServices = new ImageServicesImpl();
	
        return Response.status(200).type(MediaType.APPLICATION_JSON)
        		.entity(imageServices.getMultipleObjectsFromS3(username, timestamp, isHappy ).toString()).build();
	}
	
	
}
