package com.somedamnbrain.exceptions;

public class UnexplainableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 526498663652578940L;

	public UnexplainableException() {
		super();
	}

	public UnexplainableException(final Exception e) {
		super(e);
	}

	public UnexplainableException(final String errorMessage) {
		super(errorMessage);
	}

}
