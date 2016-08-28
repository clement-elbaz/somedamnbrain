package com.somedamnbrain.diagnostic;

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
	 * Attempt the correction. This may (and hopefully, will) alter the state of
	 * the system.
	 */
	void attemptCorrection();

}
