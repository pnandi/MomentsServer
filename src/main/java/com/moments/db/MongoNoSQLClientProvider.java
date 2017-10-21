package com.moments.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoNoSQLClientProvider {
	
	private static MongoClient noSQLClient = null;
	private static Logger LOGGER = LoggerFactory.getLogger(MongoNoSQLClientProvider.class);
	
	public static MongoClient getNoSQLClient() {
		
		if(noSQLClient == null) {
			synchronized (MongoClient.class) {
				if(noSQLClient == null) {
					initializeNOSQLDB();
				}
			}
		}
		return noSQLClient;
	}
	private static void initializeNOSQLDB() {
		String noSQLURI = System.getenv("NOSQL_URI");
		String noSQLDBName = System.getenv("NOSQL_DB");
		try {

			Builder builder = MongoClientOptions.builder();

			LOGGER.debug("\n Initiating connection using Mongo URI: ",noSQLURI);

			MongoClientURI mongoClientUri = new MongoClientURI(noSQLURI,builder);

			noSQLClient = new MongoClient(mongoClientUri);
			MongoDatabase db = noSQLClient.getDatabase(noSQLDBName);

			createCollectionDB(db, "images");

			LOGGER.debug("\nVerified connecting to Mongo!");
			
		}catch(Exception e) {
			
		}
	}
	
	private static void createCollectionDB(MongoDatabase db, String collectionName) {
		if (!collectionExists(db, collectionName)) {
			synchronized (MongoNoSQLClientProvider.class) {
				if (!collectionExists(db, collectionName)) {
					LOGGER.info("Creating the mongo collection for " + collectionName + "!");
						db.createCollection(collectionName);
				}
				else {
					LOGGER.debug(collectionName + " collection already exists!");
				}
			}
		}
	}

	private static boolean collectionExists(MongoDatabase db, final String collectionName) {
	    MongoIterable<String> listCollectionNames = db.listCollectionNames();
	    for (final String name : listCollectionNames) {
	    	LOGGER.debug("found collection : "+name);
	        if (name.equalsIgnoreCase(collectionName)) {
	            return true;
	        }
	    }
	    return false;
	}

}
