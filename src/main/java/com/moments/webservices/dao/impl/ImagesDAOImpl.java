package com.moments.webservices.dao.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.type.TypeReference;
import com.moments.db.obj.ImageData;
import com.moments.db.obj.UserData;
import com.moments.db.utils.BsonHelper;
import com.moments.db.utils.GenericUtils;
import com.moments.db.utils.NoSQLDBUtils;
import com.moments.webservices.dao.ImagesDAO;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;

import static com.mongodb.client.model.Filters.and;

public class ImagesDAOImpl extends NoSQLDBUtils implements ImagesDAO{
	private static final String SUFFIX = "/";
       

    
    private static final String ACCESS_KEY = "";
    private static final String SECRET_KEY = "";

	private String HAPPY = "happy";
	private String SAD = "sad";
        private BasicAWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
	private static Logger LOGGER = LoggerFactory.getLogger(ImagesDAOImpl.class);

	@Override
	public ByteArrayOutputStream getObjectFromS3(String bucketName, String key) {


		//AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-east-1").
				withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			System.out.println("Downloading an object");

			S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, key));
			System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
			S3ObjectInputStream s3ObjectInpStrem = s3object.getObjectContent();

	        int len;
	        byte[] buffer = new byte[4096];
	        while ((len = s3ObjectInpStrem.read(buffer, 0, buffer.length)) != -1) {
	            baos.write(buffer, 0, len);
	        }
		    s3ObjectInpStrem.close();

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which" + " means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means" + " the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}catch (FileNotFoundException fnfe) {
		    System.out.println(fnfe.getMessage());
		} catch (IOException ioe) {
		    System.out.println(ioe.getMessage());
		    System.exit(1);
		}catch(Exception e) {
			 System.out.println(e.getMessage());
		}
		return baos;
	}

	@Override
	public boolean setObjectToS3(String bucketName, String key, String folderName, ByteArrayOutputStream baos) {
		boolean imageProcessed = false;
		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			System.out.println("BucketName: " + bucketName + "\n");
			System.out.println("key: " + key + "\n");
			System.out.println("folderName: " + folderName + "\n");
			System.out.println(awsCredentials.getAWSAccessKeyId());
			System.out.println(awsCredentials.getAWSSecretKey());

			AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-east-1").
					withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
			System.out.println("Uploading a new object to S3 from a fileof size--->"+baos.toByteArray().length);
			ObjectMetadata objMetaData = new ObjectMetadata();
			objMetaData.setContentLength(baos.toByteArray().length);
			InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

			if (GenericUtils.lookupFolder(bucketName, folderName, s3Client) != true) {
				GenericUtils.createFolder(bucketName, folderName, s3Client);
			}
			key = folderName + SUFFIX + key;
			System.out.println("new key: " + key + "\n");
			s3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, objMetaData));
			imageProcessed = true;
			
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return imageProcessed;
	}

	@Override
	public boolean deleteObjectFromS3(String bucketName, String keyLarge, String keySmall, String folderName) {
		
		boolean imageProcessed = false;
		try {

		    LOGGER.info("*****************************************************");
		    LOGGER.info("Request: deleteObjectFromS3: BucketName  --> " + bucketName);
		    LOGGER.info("Request: deleteObjectFromS3: keySmall    --> " + keySmall);
		    LOGGER.info("Request: deleteObjectFromS3: keyLarge    --> " + keyLarge);
		    LOGGER.info("Request: deleteObjectFromS3: folderName  --> " + folderName);
		    LOGGER.info("*****************************************************");
		
			System.out.println(awsCredentials.getAWSAccessKeyId());
			System.out.println(awsCredentials.getAWSSecretKey());

			AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-east-1").
					withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
			
			

			if (GenericUtils.lookupFolder(bucketName, folderName, s3Client) != true) {
				imageProcessed = false;
				LOGGER.error("Request: deleteObjectFromS3 Missing: folderName  --> " + folderName);
			}
			s3Client.deleteObject(new DeleteObjectRequest(bucketName, keySmall));
			s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyLarge));
			
			imageProcessed = true;
			
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
		return imageProcessed;
	}

	@Override
	public ArrayList<ImageData> getLatestImagesFromDB(String username, String timestamp, String isHappy) {

		List<Bson> conditions = new ArrayList<>();

		ArrayList <ImageData> imageDataList = new ArrayList<ImageData>();

		BsonHelper bsonHelper = new BsonHelper(conditions);
		bsonHelper.addEqBson("username", username);
		//bsonHelper.addEqBson("isHappy", "true");

		if(timestamp !=null) {
			bsonHelper.addLteBson("timestamp", ""+timestamp);
		}
		
		if (isHappy != null) {			
			bsonHelper.addEqBson("isHappy", isHappy);
		}
		
		Bson query = and(conditions);
		
		//FindIterable<Document> iterable = getImgageCollection().find(query);
		BsonDocument bsonDocument = query.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());

		LOGGER.info("get image query {}", bsonDocument);
		
		FindIterable<Document> iterable = getImageCollection().find(query).sort(new BasicDBObject("timestamp", -1)).limit(4);
		//FindIterable<Document> iterable = getImageCollection().find(query).sort(_id, 1).limit(4);
		try {
			for(Document image : iterable) {
				ImageData imageData = new ImageData();
				imageData = mapper.readValue(image.toJson(), new TypeReference<ImageData>(){});
				imageDataList.add(imageData);
			}
	    }catch(Exception e) {
	    		LOGGER.error("Error while fetching User Data", e);
	    }
		return imageDataList;
	}
    
	@Override
	public ArrayList<ImageData> getSingleImageFromDB(String username, String imageId) {

		List<Bson> conditions = new ArrayList<>();

		ArrayList <ImageData> imageDataList = new ArrayList<ImageData>();

		BsonHelper bsonHelper = new BsonHelper(conditions);
		bsonHelper.addEqBson("username", username);

		
		if (imageId != null) {			
			bsonHelper.addEqBson("imageId", imageId);
		}
		
		Bson query = and(conditions);
		
		//FindIterable<Document> iterable = getImgageCollection().find(query);
		BsonDocument bsonDocument = query.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());

		LOGGER.info("get image query {}", bsonDocument);
		
		FindIterable<Document> iterable = getImageCollection().find(query);
	
		try {
			for(Document image : iterable) {
				ImageData imageData = new ImageData();
				imageData = mapper.readValue(image.toJson(), new TypeReference<ImageData>(){});
				imageDataList.add(imageData);
			}
	    }catch(Exception e) {
	    		LOGGER.error("Error while fetching User Data", e);
	    }
		return imageDataList;
	}
	@Override
	public void deleteSingleImageFromDB(String username, String imageId) {

		List<Bson> conditions = new ArrayList<>();

		BsonHelper bsonHelper = new BsonHelper(conditions);
		bsonHelper.addEqBson("username", username);

		
		if (imageId != null) {			
			bsonHelper.addEqBson("imageId", imageId);
		}
		
		Bson query = and(conditions);
		
		BsonDocument bsonDocument = query.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());

		LOGGER.info("get image query {}", bsonDocument);
	
		try {
			getImageCollection().deleteOne(query);
			
	    }catch(Exception e) {
	    		LOGGER.error("Error while fetching User Data", e);
	    }
		
	}
	@Override
	public void saveImageDataToDB(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<ImageData> getLatestImagesFromDB(String username, Date timestamp, String isHappy) {
		// TODO Auto-generated method stub
		return null;
	}

}
