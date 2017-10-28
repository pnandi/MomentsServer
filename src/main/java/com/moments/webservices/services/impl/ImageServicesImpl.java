package com.moments.webservices.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.inject.Inject;

import com.moments.webservices.dao.ImagesDAO;
import com.moments.webservices.dao.impl.ImagesDAOImpl;
import com.moments.webservices.services.ImageServices;

public class ImageServicesImpl implements ImageServices{

	/*@Inject
	private ImagesDAO imagesDAO;*/

	@Override
	public ByteArrayOutputStream getObjectFromS3(String bucketName, String key) {
		ImagesDAOImpl imagesDAO = new ImagesDAOImpl();
		return imagesDAO.getObjectFromS3(bucketName, key);
	}

	@Override
	public boolean setObjectToS3(String bucketName, String key, String folderName, ByteArrayOutputStream baos) {

		ImagesDAOImpl imagesDAO = new ImagesDAOImpl();
		 return imagesDAO.setObjectToS3(bucketName, key,folderName, baos);
	}


}
