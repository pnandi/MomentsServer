package com.moments.db.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.json.JSONObject;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class GenericUtils {

	private static String SUFFIX = "/";
	private static String HAPPY = "happy";
	private static String SAD = "sad";
	
	public static String getFolderName(String userName, boolean isHappy) {
		String folder = userName + SUFFIX;
		if (isHappy) {
			folder += HAPPY;
		}else {
			folder += SAD;
		}
		return folder;
	}
	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		List<S3ObjectSummary> fileList =
				client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}

	public static boolean lookupFolder(String bucketName, String folderName, AmazonS3 client) {
		List<S3ObjectSummary> fileList =
				client.listObjects(bucketName, folderName).getObjectSummaries();
		boolean empty = false;
		if (fileList.isEmpty()) {
			empty = true;
		}
        return empty;
	}
	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {

		System.out.println("In create folder\n");
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}
	public static JSONObject prepareDocumentImages(String userName, boolean isHappy, String bucketName, String folderName, String imageName, String timeStamp, String messages) {
		
		String fileLoc = bucketName + SUFFIX + userName + SUFFIX;
		if (isHappy) {
			fileLoc += HAPPY + SUFFIX;
		}else {
			fileLoc += SAD + SUFFIX;
		}
		
		JSONObject imageMetaData = new JSONObject();
        imageMetaData.put("username", userName);
        imageMetaData.put("isHappy", isHappy);
        imageMetaData.put("imagePath", fileLoc);
        imageMetaData.put("imageId", imageName);
        imageMetaData.put("timestamp", timeStamp);
        imageMetaData.put("messages", messages);
        
       return imageMetaData;
	}
}
