package com.somedamnbrain.diagnostic;

import com.somedamnbrain.exceptions.UnexplainableException;

/**
 * In somedamnbrain, a corrective action is an action SDB can attempt in order
 * to correct a diagnostic on a system. CorrectionAction are not idempotent and
 * attempts t o change the state of the system.
 * 
 * @author clement
 *
 */
public interface CorrectiveAction {

	/**
	 * Get a unique id (description) of the diagnostic. Please stay alphanumeric
	 * + space there. Ex : "Creating new universe file"
	 * 
	 * @return
	 */
	String getUniqueID();

	/**
	 * Attempt the correction. This may (and hopefully, will) alter the state of
	 * the system.
	 * 
	 * @throws UnexplainableException
	 *             if something unexpected occured during the correction
	 */
	void attemptCorrection() throws UnexplainableException;

}
