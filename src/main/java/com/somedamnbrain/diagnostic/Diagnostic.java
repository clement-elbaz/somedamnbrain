package com.somedamnbrain.diagnostic;

import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;

/**
 * In somedamnbrain, a diagnostic is a monitoring action on a system. Diagnostic
 * should be idempotent and leave the situation unchanged.
 * 
 * @author clement
 *
 */
public interface Diagnostic {

	/**
	 * Get a unique id (description) of the diagnostic. Please stay alphanumeric
	 * + space there. Ex : "Checking universe file existence on the system"
	 * 
	 * @return
	 */
	String getUniqueID();

	/**
	 * Start the diagnostic and return a DiagnosticResult. Diagnostic should be
	 * idempotent and leave the situation unchanged.
	 * 
	 * @throws UnexplainableException
	 *             if something unexpected occured during the correction
	 */
	DiagnosticResult attemptDiagnostic() throws UnexplainableException;

	/**
	 * Return a correction for the diagnostic.
	 * 
	 * @param diagnosticResult
	 *            diagnostic result.
	 * @return a correction
	 */
	CorrectiveAction getCorrection(DiagnosticResult diagnosticResult) throws NoResultException;

}
