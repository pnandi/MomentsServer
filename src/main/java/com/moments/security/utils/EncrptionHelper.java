package com.moments.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;

public class EncrptionHelper {

		/** Logger instance for this class */
		private static final Logger LOGGER = LoggerFactory.getLogger(EncrptionHelper.class);

		private static String secretKey = "sEcREtSAuce=";
		private static String ivKey = "9999999999999999";

		private static EncrptionHelper instance = null;

		private static Cipher ecipher;
		private static Cipher dcipher;
		private static SecretKey key;

		private void init() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {

			byte[] keyBytes = (secretKey).getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			keyBytes = sha.digest(keyBytes);
			keyBytes = Arrays.copyOf(keyBytes, 16); // use only the  first 128 bits

			key = new SecretKeySpec(keyBytes, "AES");

			byte[] IV = ivKey.getBytes("UTF-8");
			//IV = sha.digest(IV);
			IV = Arrays.copyOf(IV, 16); // // use only the  first 128 bits
			AlgorithmParameterSpec IVP = new IvParameterSpec(IV);

			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, key,IVP);
			dcipher.init(Cipher.DECRYPT_MODE, key,IVP);
		}

		public static EncrptionHelper getInstance() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
			if (instance == null) {
				 synchronized(EncrptionHelper.class) {
					 if (instance == null) {
						 instance = new EncrptionHelper();
							instance.init();
					 }
				 }

			}
			return instance;
		}

		public String encrypt(String str) {
			try {
				byte[] utf8 = str.getBytes("UTF8");
				byte[] enc = ecipher.doFinal(utf8);

				return Base64.getEncoder().encodeToString(enc);
			} catch (Exception e) {
				LOGGER.error("Error in encrypt.", e);
			}
			return null;
		}

		public String decrypt(String str) {
			try {
				byte[] dec = Base64.getDecoder().decode(str);
				byte[] utf8 = dcipher.doFinal(dec);

				return new String(utf8, "UTF8");
			} catch (Exception e) {
				LOGGER.error("Error in decrypt.", e);
			}

			return null;
		}


		public static void main(String[] args) {
			try {
				System.out.println(EncrptionHelper.getInstance().encrypt("MyNameIsKhan"));
				System.out.println(EncrptionHelper.getInstance().decrypt("ql8f+WsQT82JEyknSGsmMw=="));
			} catch (NoSuchAlgorithmException e) {
				LOGGER.error("No Such Algorithm:", e);
				return;
			} catch (NoSuchPaddingException e) {
				LOGGER.error("No Such Padding:", e);
				return;
			} catch (InvalidKeyException e) {
				LOGGER.error("Invalid Key:", e);
				return;
			} catch (IOException e) {
				LOGGER.error("Error in decrypt.", e);
				return;
			} catch (InvalidAlgorithmParameterException e){
				LOGGER.error("Error in initialization vector.", e);
			}
		}
}
