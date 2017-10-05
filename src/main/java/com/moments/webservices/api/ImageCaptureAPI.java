package com.moments.webservices.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.moments.webservices.services.ImageServices;
import com.moments.webservices.services.impl.ImageServicesImpl;


@Path("image")
@RequestScoped
public class ImageCaptureAPI{

	@Path("getImage")
	@GET
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response getImages() {

		ImageServicesImpl imageServices = new ImageServicesImpl();
		ByteArrayOutputStream baos = imageServices.getObjectFromS3("moments-images", "IMG_3156.JPG");
        System.out.println("Server size: " + baos.size());
        return Response.ok(baos).build();
	}

	@Path("uploadImage")
	@POST
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.WILDCARD)
	public Response setImage(@FormDataParam(value = "file") InputStream uploadedInputStream,
	@FormDataParam("file") FormDataContentDisposition fileDetail) {
		System.out.println("in uploadImage -->"+fileDetail);
		
		ImageServicesImpl imageServices = new ImageServicesImpl();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len;
        byte[] buffer = new byte[4096];
        boolean result = false;
        try {
			while ((len = uploadedInputStream.read(buffer, 0, buffer.length)) != -1) {
			    baos.write(buffer, 0, len);
			}
			result = imageServices.setObjectToS3("moments-images", fileDetail.getFileName(),baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.ok(result).build();
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
