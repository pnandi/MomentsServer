package com.moments.db.utils;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.moments.db.MongoNoSQLClientProvider;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class NoSQLDBUtils extends MongoNoSQLClientProvider{
	
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

}
