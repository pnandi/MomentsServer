package com.moments.webservices.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moments.db.utils.NoSQLDBUtils;
import com.moments.webservices.dao.ImagesDAO;
import com.moments.webservices.dao.impl.ImagesDAOImpl;
import com.moments.webservices.services.ImageServices;

public class ImageServicesImpl implements ImageServices{

	private static Logger LOGGER = LoggerFactory.getLogger(ImageServicesImpl.class);
	
	@Override
	public ByteArrayOutputStream getObjectFromS3(String bucketName, String key) {
		ImagesDAO imagesDAO = new ImagesDAOImpl();
		return imagesDAO.getObjectFromS3(bucketName, key);
	}

	private ByteArrayOutputStream compressImage(InputStream inputStream) {
		
		BufferedImage img;
		ByteArrayOutputStream smallBaos = new ByteArrayOutputStream();
		try {
		   img = ImageIO.read(inputStream);
		   BufferedImage scaledImg = Scalr.resize(img, 1024, 768);
		   ImageIO.write(scaledImg, "jpg", smallBaos); 
		   LOGGER.info("Small image size :" + smallBaos.size());
		} catch (IOException e) {
		   LOGGER.error("exception while resizing {}", e.getMessage());
		} // load image
	    
		return smallBaos;  
		
	}
	@Override
	public boolean setObjectToS3(String bucketName, String key, String folderName, ByteArrayOutputStream baos) {
		 ByteArrayOutputStream originalImage = baos;
		 LOGGER.info("Lagre image size :" + originalImage.size());
		 
		 ByteArrayOutputStream smallImage = compressImage(new ByteArrayInputStream(baos.toByteArray()));
		 
		 LOGGER.info("Small image size :" + originalImage.size());
		   
		 ImagesDAOImpl imagesDAO = new ImagesDAOImpl();
		 imagesDAO.setObjectToS3(bucketName, key,folderName+ "/" + "large", originalImage);
		 
		 
		 return imagesDAO.setObjectToS3(bucketName, key,folderName+ "/" + "small", smallImage);
	}


}
