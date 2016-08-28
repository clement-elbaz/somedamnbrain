package com.somedamnbrain.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.diagnostic.DiagnosticRun;
import com.somedamnbrain.diagnostic.EnumDiagnosticAttempt;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.ExplainableException;
import com.somedamnbrain.services.alert.AlertService;
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
			} catch (UnrecoverableDiagnosticFailureException e) {
				// If a system has a unrecoverable failure, we pursue diagnostic
				// but do not attempt any more correction on other systems.
				skipCorrections = true;
			}
			selectorService.markSystemAsDiagnosticated(currentSystem);
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
		List<DiagnosticRun> unrecoverableFailures = new ArrayList<DiagnosticRun>();

		for (Diagnostic diagnostic : system.getDiagnostics()) {
			DiagnosticResult result = diagnostic.attemptDiagnostic();
			if (!result.getSuccess()) {
				alertService.alertDiagnostic(system, diagnostic, result, skipCorrections
						? EnumDiagnosticAttempt.SKIP_CORRECTIONS : EnumDiagnosticAttempt.CAN_ATTEMPT_CORRECTION);
				if (!skipCorrections) {
					CorrectiveAction correction = diagnostic.getCorrection(result);
					correction.attemptCorrection();
					DiagnosticResult finalResult = diagnostic.attemptDiagnostic();
					alertService.alertDiagnostic(system, diagnostic, result,
							EnumDiagnosticAttempt.CORRECTION_ATTEMPTED);
					if (!finalResult.getSuccess()) {
						unrecoverableFailures.add(new DiagnosticRun(diagnostic, finalResult));
					}
				}

			}
		}

		if (!unrecoverableFailures.isEmpty()) {
			alertService.alertSystem(system, unrecoverableFailures);
			throw new UnrecoverableDiagnosticFailureException();
		}
	}

	static class UnrecoverableDiagnosticFailureException extends ExplainableException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4280080209017599707L;

	}

}
