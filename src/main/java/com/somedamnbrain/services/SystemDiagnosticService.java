package com.somedamnbrain.services;

import java.util.Iterator;

import com.somedamnbrain.diagnostic.Correction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.diagnostic.EnumDiagnosticAttempt;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.ExplainableException;
import com.somedamnbrain.systems.SDBSystem;

public class SystemDiagnosticService {

	private final AlertService alertService;
	private final SystemSelectorService selectorService;

	public SystemDiagnosticService(final AlertService alertService, final SystemSelectorService selectorService) {
		this.alertService = alertService;
		this.selectorService = selectorService;
	}

	/**
	 * Run a full diagnostic of a system.
	 * 
	 * @param system
	 *            system.
	 */
	public void diagnosticFullSystem(SDBSystem system) {
		boolean skipCorrections = false;

		Iterator<SDBSystem> iterator = selectorService.computeDependenciesResolution(system);

		while (iterator.hasNext()) {
			SDBSystem currentSystem = iterator.next();
			try {
				this.diagnosticSingleSystem(currentSystem, skipCorrections);
				selectorService.markSystemAsDiagnosticated(currentSystem);
			} catch (UnrecoverableDiagnosticFailureException e) {
				// If a system has a unrecoverable failure, we pursue diagnostic
				// but do not attempt any more correction.
				skipCorrections = true;
			}
		}
	}

	/**
	 * Diagnostic a single system.
	 * 
	 * @param system
	 *            system to run diagnostic on
	 * @param skipCorrections
	 *            true if not correction should be attempted
	 * @throws UnrecoverableDiagnosticFailureException
	 *             if the system encountered an unrecoverable failure
	 */
	private void diagnosticSingleSystem(SDBSystem system, boolean skipCorrections)
			throws UnrecoverableDiagnosticFailureException {
		for (Diagnostic diagnostic : system.getDiagnostics()) {
			DiagnosticResult result = diagnostic.attemptDiagnostic();
			if (!result.getSuccess()) {
				alertService.alertDiagnosticFailure(diagnostic, result, skipCorrections
						? EnumDiagnosticAttempt.SKIP_CORRECTIONS : EnumDiagnosticAttempt.CAN_ATTEMPT_CORRECTION);
				if (!skipCorrections) {
					Correction correction = diagnostic.getCorrection(result);
					correction.attemptCorrection();
					DiagnosticResult finalResult = diagnostic.attemptDiagnostic();
					alertService.alertDiagnosticFailure(diagnostic, result, EnumDiagnosticAttempt.CORRECTION_ATTEMPTED);
					if (!finalResult.getSuccess()) {
						throw new UnrecoverableDiagnosticFailureException();
					}
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
