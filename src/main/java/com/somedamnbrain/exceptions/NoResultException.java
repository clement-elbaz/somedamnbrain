package com.somedamnbrain.exceptions;

import java.io.FileNotFoundException;

public class NoResultException extends ExplainableException {

	public NoResultException() {
		super();
	}

	public NoResultException(final FileNotFoundException e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4549176456513694804L;

}
