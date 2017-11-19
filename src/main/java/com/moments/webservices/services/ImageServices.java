package com.moments.webservices.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONObject;

public interface ImageServices {

	ByteArrayOutputStream getObjectFromS3(String bucketName, String key);
	JSONObject getMultipleObjectsFromS3(String username , String timestamp, boolean isHappy, boolean happyFilter);
	boolean setObjectToS3(String bucketName, String key, String folderName, ByteArrayOutputStream baos, JSONObject imageJson);
}
