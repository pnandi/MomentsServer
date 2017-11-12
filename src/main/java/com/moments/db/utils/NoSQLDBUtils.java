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
import com.moments.db.obj.UserData;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class NoSQLDBUtils extends MongoNoSQLClientProvider{
	
	private static String SUFFIX = "/";
	private static String HAPPY = "happy";
	private static String SAD = "sad";
	
	private static Logger LOGGER = LoggerFactory.getLogger(NoSQLDBUtils.class);
	
	protected static ObjectMapper mapper = new ObjectMapper();
	
	protected static MongoClient noSQLClient = MongoNoSQLClientProvider.getNoSQLClient();
	protected static MongoDatabase db = noSQLClient.getDatabase(System.getenv("NOSQL_DB"));
	
	public static void saveImageMetaDataToDB(JSONObject imageJson) {
		try {

			HashMap<String,Object> imagesMap = mapper.readValue(imageJson.toString(), new TypeReference<Map<String, String>>(){});
			//HashMap<String,Object> imagesMap = om.convertValue(imageJson, 
			//		om.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
			LOGGER.info("saving to mongo {}",imageJson);
			getImgageCollection().insertOne(new Document(imagesMap));
		}catch(Exception e) {
			LOGGER.error("error while saving to Mongo {}",e.getMessage());
		}
	}
	
	
	protected static MongoCollection<Document> getImgageCollection(){
		return db.getCollection("images");
	}
	
	protected static MongoCollection<Document> getUsersCollection(){
		return db.getCollection("users");
	}
	
	public static void prepareDocumentImages(String userName, boolean isHappy, String bucketName, String folderName, String imageName, String timeStamp) {
		
		System.out.println("in prepareDocumentImages start \n");
		String fileLoc = bucketName + SUFFIX + userName + SUFFIX;
		if (isHappy) {
			fileLoc += HAPPY + SUFFIX;
		}else {
			fileLoc += SAD + SUFFIX;
		}
		JSONObject imageMetaData = new JSONObject();
        imageMetaData.put("userName", userName);
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
	public static String getFolderName(String userName, boolean isHappy) {
		String folder = userName + SUFFIX;
		if (isHappy) {
			folder += HAPPY;
		}else {
			folder += SAD;
		}
		return folder;
	}
}
