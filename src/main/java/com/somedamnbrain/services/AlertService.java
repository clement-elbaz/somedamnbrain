package com.somedamnbrain.services;

import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.diagnostic.EnumDiagnosticAttempt;
import com.somedamnbrain.entities.Entities.DiagnosticResult;

public class AlertService {

	/**
	 * Alert the human minion about a diagnostic failure.
	 * 
	 * @param diagnostic
	 *            the diagnostic that failed
	 * @param diagnosticResult
	 *            the result of the diagnostic
	 * @param finalResult
	 *            true if this is a final result (eg correction has already been
	 *            attempted)
	 */
	public void alertDiagnosticFailure(final Diagnostic diagnostic, final DiagnosticResult diagnosticResult,
			final EnumDiagnosticAttempt attemptType) {
		throw new RuntimeException("not implemented");
	}
}
