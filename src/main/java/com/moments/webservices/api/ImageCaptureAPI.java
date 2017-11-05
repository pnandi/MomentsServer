package com.moments.webservices.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.moments.db.utils.NoSQLDBUtils;
import com.moments.security.service.MLAuthenticationService;
import com.moments.security.service.impl.MLAuthenticationServiceImpl;
import com.moments.webservices.services.ImageServices;
import com.moments.webservices.services.impl.ImageServicesImpl;

@Path("image")
@RequestScoped
public class ImageCaptureAPI{
	
	@Path("getImage")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
	public Response getImages(@QueryParam("key") String key, @HeaderParam("authorization") String authString) {
		
		MLAuthenticationService authService = new MLAuthenticationServiceImpl();
		
	       if(!authService.authenticate(authString)){
	    	   	return Response.status(403).type(MediaType.APPLICATION_JSON).
	    			   entity("{\"error\":\"User not authenticated\"}").build();
	        }
		ImageServices imageServices = new ImageServicesImpl();
		ByteArrayOutputStream baos = imageServices.getObjectFromS3("moments-images", key);
        System.out.println("Server size: " + baos.size());
        
        return Response.ok(baos.toByteArray(),MediaType.APPLICATION_OCTET_STREAM)
        		.header("content-disposition","attachment; filename = IMG_3156.JPG")
        		.build();
	}

	@Path("uploadImage")
	@POST
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setImage(@FormDataParam(value = "file") InputStream uploadedInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetail,
	    @FormDataParam("isHappy") boolean isHappy) {
	    System.out.println("in uploadImage -->"+fileDetail);
	    System.out.println("isHappy --> "+ isHappy);
	    ImageServicesImpl imageServices = new ImageServicesImpl();

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[4096];
        boolean result = false;
        NoSQLDBUtils dbUtils = new NoSQLDBUtils();
        // Need to get timestamp from device
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String folderName = dbUtils.getFolderName(518366499, isHappy);
        System.out.println("folderName Found: " + folderName + "\n");
        dbUtils.prepareDocumentImages(518366499,isHappy,"moments-images", folderName, fileDetail.getFileName(), timeStamp );
   
        try {
			while ((len = uploadedInputStream.read(buffer, 0, buffer.length)) != -1) {
			    baos.write(buffer, 0, len);
			}
			result = imageServices.setObjectToS3("moments-images", fileDetail.getFileName(), folderName , baos);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Response.status(403).entity(e).build();
	               
		}
        return Response.status(200).entity(result).build();

		//return Response.ok(result).build();
	}

	/*@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
	        ,
	        @FormDataParam("file") FormDataContentDisposition fileDetail,
	        @FormDataParam("path") String path) {


	    // Path format //10.217.14.97/Installables/uploaded/
	    System.out.println("path::"+path);
	    String uploadedFileLocation = path
	            + fileDetail.getFileName();

	    // save it
	    writeToFile(uploadedInputStream, uploadedFileLocation);

	    String output = "File uploaded to : " + uploadedFileLocation;

	    return Response.status(200).entity(output).build();

	}*/
	
	
}
