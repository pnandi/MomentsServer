package com.moments.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DateTimeHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeHelper.class);
	
	public static final String DATE_TYPE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_TYPE_FORMAT = "HH:mm:ss.SSS";
	public static final String DATETIME_TYPE_FORMAT = String.format("%s %s",DATE_TYPE_FORMAT, TIME_TYPE_FORMAT);
	public static final SimpleDateFormat datetimeTypeFormatter = new SimpleDateFormat(DATETIME_TYPE_FORMAT);
	
	public static String getUTCDateTime(java.util.Date date){
		try{
			if(date!=null){
				DateTime dateTime = new DateTime(date.getTime(), DateTimeZone.UTC);
				return dateTime.toString();
			}
		}catch(Exception e){
			LOGGER.error("Exception occurs in the UTC Date time", e);
		}
		return null;
	}
	
	public static final Timestamp parseDateTimeTypeFormat(String datetimeStr)
			throws ParseException {
		Timestamp ts = null;
		try {
			datetimeStr = datetimeStr.trim();
			while (datetimeStr.length() < 23) {
				datetimeStr = datetimeStr + "0";
			}

			datetimeStr = datetimeStr.substring(0, 10) + " "+ datetimeStr.substring(11, 23);

			ts = new Timestamp(datetimeTypeFormatter.parse(datetimeStr).getTime());
		} catch (Exception e) {
			LOGGER.error("Non iso datetime format: '{}'!!!", datetimeStr, e);
			throw e;
		}
		return ts;
	}
}
