package com.somedamnbrain.services.ask;

import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;

public interface AskService {
	/**
	 * Try to initialize the service.
	 * 
	 * @return true if initialized properly.
	 * @throws UnexplainableException
	 *             if something unexpected happened.
	 */
	boolean initialize() throws UnexplainableException;

	/**
	 * Close the Ask Service.
	 * 
	 * @throws UnexplainableException
	 *             if something unexpected happened.
	 */
	void close() throws UnexplainableException;

	/**
	 * Ask a question to a human minion.
	 * 
	 * @param question
	 * @return answer
	 * @throws SystemNotAvailableException
	 *             if system is not properly initialized
	 * @throws UnexplainableException
	 *             if something unexpected happened.
	 */
	String askHumanMinion(String question) throws SystemNotAvailableException, UnexplainableException;

}
