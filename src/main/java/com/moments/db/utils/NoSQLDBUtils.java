package com.moments.db.utils;

import java.util.HashMap;

import org.bson.Document;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moments.db.MongoNoSQLClientProvider;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class NoSQLDBUtils extends MongoNoSQLClientProvider{
	
	protected MongoClient noSQLClient = MongoNoSQLClientProvider.getNoSQLClient();
	protected MongoDatabase db = noSQLClient.getDatabase(System.getenv("NOSQL_DB"));
	
	public void saveImageMetaDataToDB(JSONObject imageJson) {
		ObjectMapper om = new ObjectMapper();
		HashMap<String,Object> imagesMap = om.convertValue(imageJson, 
				om.getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
		getImgageCollection().insertOne(new Document(imagesMap));
	}
	
	protected MongoCollection<Document> getImgageCollection(){
		return db.getCollection("images");
	}

}
