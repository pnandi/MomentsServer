package com.moments.webservices.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.moments.webservices.dao.ImagesDAO;

public class ImagesDAOImpl implements ImagesDAO{

	@Override
	public ByteArrayOutputStream getObjectFromS3(String bucketName, String key) {

		BasicAWSCredentials awsCredentials = new BasicAWSCredentials("ACCESS_KEY", "SECRET_KEY");

		//AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-east-1").
				withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			System.out.println("Downloading an object");

			//Region region = Region.getRegion(Regions.US_EAST_1);
			//s3Client.setRegion(region);

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
	public boolean setObjectToS3(String bucketName, String key, ByteArrayOutputStream baos) {

		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			//File file = new File(inputStream);
			BasicAWSCredentials awsCredentials = new BasicAWSCredentials("ACCESS_KEY", "SECRET_KEY");

			AmazonS3 s3Client = AmazonS3Client.builder().withRegion("us-east-1").
					withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
			System.out.println("Uploading a new object to S3 from a fileof size--->"+baos.toByteArray().length);
			ObjectMetadata objMetaData = new ObjectMetadata();
			objMetaData.setContentLength(baos.toByteArray().length);
			InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

			s3Client.putObject(new PutObjectRequest(bucketName, key, inputStream, objMetaData));

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
		return true;
	}

}
