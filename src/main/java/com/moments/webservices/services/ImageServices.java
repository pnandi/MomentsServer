package com.moments.webservices.services;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface ImageServices {

	ByteArrayOutputStream getObjectFromS3(String bucketName, String key);
	boolean setObjectToS3(String bucketName, String key, ByteArrayOutputStream baos);
}
