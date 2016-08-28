package com.somedamnbrain.exceptions;

public class ExplainableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8614749530330112531L;

	public ExplainableException(Exception e) {
		super(e);
	}

	public ExplainableException() {
		super();
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

}