package com.somedamnbrain.diagnostic;

import com.somedamnbrain.entities.Entities.DiagnosticResult;

/**
 * In somedamnbrain, a diagnostic is a monitoring action on a system. Diagnostic
 * should be idempotent and leave the situation unchanged.
 * 
 * @author clement
 *
 */
public interface Diagnostic {

	/**
	 * Get a description of the diagnostic.
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * Start the diagnostic and return a DiagnosticResult. Diagnostic should be
	 * idempotent and leave the situation unchanged.
	 * 
	 * @param system
	 * @return
	 */
	DiagnosticResult attemptDiagnostic();

	/**
	 * Return a correction for the diagnostic.
	 * 
	 * @param diagnosticResult
	 *            diagnostic result.
	 * @return a correction
	 */
	CorrectiveAction getCorrection(DiagnosticResult diagnosticResult);

}
