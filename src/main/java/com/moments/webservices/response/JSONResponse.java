package com.moments.webservices.response;

import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


public class JSONResponse {

	private String message;
	private String errorCode;
	private Map<?, ?> errorParams;
	private Map<?, ?> data;

	public String getMessage() {
		return message;
	}

	public Map<?, ?> getData() {
		return data;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Map<?, ?> getErrorParams() {
		return errorParams;
	}

	private JSONResponse(String message, String errorCode, Map<?, ?> errorParams) {
	    this.message = message;
	    this.errorCode = errorCode;
	    this.errorParams = errorParams;
    }

	@JsonCreator
	public JSONResponse(@JsonProperty("data") Map<?, ?> data) {
	    this.data = data;
    }

	/**
	 * Convenience method for creating a runtime exception for signaling a bad request.
	 * @param message The message to be sent back to the client.
	 * @return An exception that when uncaught will send a bad request response to the client.
	 */
	public static WebApplicationException badRequest(String message) {
		return new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(new JSONResponse(message, null, null)).build());
	}

	/**
	 * Convenience method for creating a runtime exception for signaling a 401 response.
	 * @param message The message to be sent back to the client.
	 * @return An exception that when uncaught will send a 401 response to the client.
	 */
	public static WebApplicationException badCredentials(String message) {
		return new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).entity(new JSONResponse(message, null, null)).build());
	}

	/**
	 * Convenience method for creating a runtime exception for signaling a 404 response.
	 * @param message The message to be sent back to the client.
	 * @return An exception that when uncaught will send a 404 response to the client.
	 */
	public static WebApplicationException notFound(String message) {
		return new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity(new JSONResponse(message, null, null)).build());
	}
	
	/**
	 * Convenience method for creating a runtime exception for signaling a error response.
	 * @param message The message to be sent back to the client.
	 * @return An exception that when uncaught will send a error response to the client.
	 */
	public static WebApplicationException error(Integer status, String message) {
		ResponseBuilder responseBuilder = (status == null ? Response.status(Response.Status.INTERNAL_SERVER_ERROR) : Response.status(status));
		return new WebApplicationException(responseBuilder.entity(new JSONResponse(message, null, null)).build());
	}
	
	/**
	 * Convenience method for creating a runtime exception for signaling an internal error and logging the cause.
	 * @param e The cause of the error.
	 * @return An exception that when uncaught will send an internal server error response to the client.
	 */
	/*public static Response internalError(Exception e) {
		String message = (e.getMessage() == null) ? "Unknown Server Error" : e.getMessage();
		String errorCode = OVCException.INTERNAL_ERROR;
		Map<String, ?> errorParams = null;
		if (e instanceof OVCException) {
			OVCException oe = (OVCException) e;
			errorCode = oe.getErrorCode();
			errorParams = oe.getErrorParams();
		} else
		if (e.getCause() instanceof OVCException) {
			OVCException oe = (OVCException) e.getCause();
			errorCode = oe.getErrorCode();
			errorParams = oe.getErrorParams();
		}

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new JSONResponse(message, errorCode, errorParams)).build();
	}*/
}
