package com.moments.db.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.moments.db.MongoNoSQLClientProvider;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class NoSQLDBUtils extends MongoNoSQLClientProvider{
	private String SUFFIX = "/";
	private String HAPPY = "happy";
	private String SAD = "sad";
	
	private static Logger LOGGER = LoggerFactory.getLogger(NoSQLDBUtils.class);
	
	protected MongoClient noSQLClient = MongoNoSQLClientProvider.getNoSQLClient();
	protected MongoDatabase db = noSQLClient.getDatabase(System.getenv("NOSQL_DB"));
	
	public void saveImageMetaDataToDB(JSONObject imageJson) {
		try {
			ObjectMapper om = new ObjectMapper();
			HashMap<String,Object> imagesMap = om.readValue(imageJson.toString(), new TypeReference<Map<String, String>>(){});
			//HashMap<String,Object> imagesMap = om.convertValue(imageJson, 
			//		om.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
			LOGGER.info("saving to mongo {}",imageJson);
			getImgageCollection().insertOne(new Document(imagesMap));
		}catch(Exception e) {
			LOGGER.error("error while saving to Mongo {}",e.getMessage());
		}
	}
	
	protected MongoCollection<Document> getImgageCollection(){
		return db.getCollection("images");
	}
	
	public void prepareDocumentImages(Integer phoneNo, boolean isHappy, String bucketName, String folderName, String imageName, String timeStamp) {
		
		System.out.println("in prepareDocumentImages start \n");
		String fileLoc = bucketName + SUFFIX + Integer.toString(phoneNo) + SUFFIX;
		if (isHappy) {
			fileLoc += HAPPY + SUFFIX;
		}else {
			fileLoc += SAD + SUFFIX;
		}
		JSONObject imageMetaData = new JSONObject();
        imageMetaData.put("phoneNo", phoneNo);
        imageMetaData.put("isHappy", isHappy);
        imageMetaData.put("fileLoc", fileLoc);
        imageMetaData.put("imageKey", imageName);
        imageMetaData.put("timeStamp", timeStamp);
        System.out.println("in prepareDocumentImages before Save \n");
        saveImageMetaDataToDB(imageMetaData);
        System.out.println("in prepareDocumentImages after Save \n");

        System.out.println("Iterating data");
        
        List<Document> documents = (List<Document>) getImgageCollection().find().into(
				new ArrayList<Document>());
        
        for(Document document : documents){
            System.out.println(document);
        }
       
	}
	public String getFolderName(Integer phoneNo, boolean isHappy) {
		String folder = Integer.toString(phoneNo) + SUFFIX;
		if (isHappy) {
			folder += HAPPY;
		}else {
			folder += SAD;
		}
		return folder;
	}
}
