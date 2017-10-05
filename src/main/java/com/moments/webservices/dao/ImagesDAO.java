package com.moments.webservices.dao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface ImagesDAO {

	ByteArrayOutputStream getObjectFromS3(String bucketName, String key);
	boolean setObjectToS3(String bucketName, String key, ByteArrayOutputStream baos);
	
}
