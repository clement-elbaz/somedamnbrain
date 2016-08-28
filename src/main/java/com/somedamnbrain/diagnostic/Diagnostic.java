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
	 * Start the diagnostic and return a DiagnosticResult. Diagnostic should be
	 * idempotent and leave the situation unchanged.
	 * 
	 * @param system
	 * @return
	 */
	DiagnosticResult attemptDiagnostic();

	Correction getCorrection(DiagnosticResult diagnosticResult);

}
