package com.somedamnbrain.services;

import com.somedamnbrain.diagnostic.Correction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.ExplainableException;
import com.somedamnbrain.systems.SDBSystem;

public class SystemDiagnosticService {

	private final AlertService alertService;

	public SystemDiagnosticService(final AlertService alertService) {
		this.alertService = alertService;
	}

	public void diagnosticSystem(SDBSystem system, boolean skipCorrections)
			throws UnrecoverableDiagnosticFailureException {
		for (Diagnostic diagnostic : system.getDiagnostics()) {
			DiagnosticResult result = diagnostic.attemptDiagnostic();
			if (!result.getSuccess() && skipCorrections) {
				alertService.alertDiagnosticFailure(diagnostic, result, false);
				Correction correction = diagnostic.getCorrection(result);
				correction.attemptCorrection();
				DiagnosticResult finalResult = diagnostic.attemptDiagnostic();
				if (!finalResult.getSuccess()) {
					alertService.alertDiagnosticFailure(diagnostic, result, true);
					throw new UnrecoverableDiagnosticFailureException();
				}
			}
		}
	}

	static class UnrecoverableDiagnosticFailureException extends ExplainableException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4280080209017599707L;

	}

}
