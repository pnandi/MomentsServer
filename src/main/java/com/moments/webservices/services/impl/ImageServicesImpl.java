package com.moments.webservices.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moments.db.obj.ImageData;
import com.moments.db.utils.GenericUtils;
import com.moments.db.utils.NoSQLDBUtils;
import com.moments.utils.DateTimeHelper;
import com.moments.webservices.dao.impl.ImagesDAOImpl;
import com.moments.webservices.services.ImageServices;

public class ImageServicesImpl implements ImageServices{

	private static Logger LOGGER = LoggerFactory.getLogger(ImageServicesImpl.class);

	ImagesDAOImpl imagesDAO = new ImagesDAOImpl();
	
	@Override
	public ByteArrayOutputStream getObjectFromS3(String bucketName, String key) {
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
	public boolean setObjectToS3(String bucketName, String key, String folderName, ByteArrayOutputStream baos, JSONObject imageJson) {
		
		 boolean isImageProcessed = false;
		ByteArrayOutputStream originalImage = baos;
		 LOGGER.info("Lagre image size :" + originalImage.size());
		 
		 ByteArrayOutputStream smallImage = compressImage(new ByteArrayInputStream(baos.toByteArray()));
		 
		 LOGGER.info("Small image size :" + originalImage.size());
		   
		 ImagesDAOImpl imagesDAO = new ImagesDAOImpl();
		 if(imagesDAO.setObjectToS3(bucketName, key,folderName+ "/" + "large", originalImage)) {
			 if(imagesDAO.setObjectToS3(bucketName, key,folderName+ "/" + "small", smallImage)) {
				 NoSQLDBUtils.saveImageMetaDataToDB(imageJson);
				 isImageProcessed = true;
			 }else {
				 isImageProcessed =false;
			 }
		 }else {
			 isImageProcessed =false;
		 }
		 
		 return isImageProcessed;

	}

	@Override
	public JSONObject getMultipleObjectsFromS3(String username, String timestamp) {
		
		JSONObject imagesJsonObj = new JSONObject();
		JSONArray imagesJsonArr = new JSONArray();
		Date dateTimestamp = null;
		try {
			
			if(StringUtils.isNotBlank(timestamp)) {
				dateTimestamp = DateTimeHelper.parseDateTimeTypeFormat(timestamp);
			}
			List<ImageData> imageDataList = imagesDAO.getLatestImagesFromDB(username, dateTimestamp);
	
			for (ImageData imageData : imageDataList) {
				//ByteArrayOutputStream baos = imageServices.getObjectFromS3("moments-images", key);
				String key = GenericUtils.getFolderName(imageData.getUsername(), imageData.getIsHappy())  + "/" + "small" + "/" + imageData.getImageId() ;
				ByteArrayOutputStream baos = imagesDAO.getObjectFromS3("moments-images", key);
				
				JSONObject jsonObj = new JSONObject();
				
				jsonObj.put("image", Base64.getEncoder().encode(baos.toByteArray()));
				jsonObj.put("timestamp",imageData.getTimestamp());
				jsonObj.put("comments", imageData.getMessages());
				
				imagesJsonArr.put(jsonObj);
			}
			
			imagesJsonObj.put("imageList", imagesJsonArr);
		}catch(Exception e) {
			LOGGER.error("exception while fetching multiple images {}",e.getMessage());
		}
		return imagesJsonObj;
	}
}
