package com.moments.webservices.dao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import com.moments.db.obj.ImageData;

public interface ImagesDAO {

	ByteArrayOutputStream getObjectFromS3(String bucketName, String key);
	ArrayList<ImageData> getLatestImagesFromDB(String username, Date timestamp, boolean isHappy, boolean happyFilter);
	boolean setObjectToS3(String bucketName, String key, String folderName, ByteArrayOutputStream baos);
	void saveImageDataToDB(JSONObject json);
	
	
}
