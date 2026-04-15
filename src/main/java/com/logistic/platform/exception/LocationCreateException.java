package com.logistic.platform.exception;

import com.logistic.common.exception.LogisticsException;

public class LocationCreateException extends LogisticsException {

	private static final long serialVersionUID = 1L;

	public LocationCreateException(int errorCode, String message) {
		super(errorCode, message);
	}

	public LocationCreateException(int errorCode, String message, Exception ex) {
		super(errorCode, message, ex);
	}
}
