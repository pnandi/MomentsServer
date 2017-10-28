package com.moments.webservices.services.impl;

import java.io.ByteArrayOutputStream;

import com.moments.webservices.dao.ImagesDAO;
import com.moments.webservices.dao.impl.ImagesDAOImpl;
import com.moments.webservices.services.ImageServices;

public class ImageServicesImpl implements ImageServices{


	@Override
	public ByteArrayOutputStream getObjectFromS3(String bucketName, String key) {
		ImagesDAO imagesDAO = new ImagesDAOImpl();
		return imagesDAO.getObjectFromS3(bucketName, key);
	}

	@Override
	public boolean setObjectToS3(String bucketName, String key, ByteArrayOutputStream baos) {

		ImagesDAO imagesDAO = new ImagesDAOImpl();
		 return imagesDAO.setObjectToS3(bucketName, key,baos);
	}

}
