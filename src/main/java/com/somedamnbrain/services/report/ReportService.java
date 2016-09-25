package com.somedamnbrain.services.report;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.SDBSystem;

public class ReportService {

	private final UniverseService universeService;

	@Inject
	public ReportService(final UniverseService universeService) {
		this.universeService = universeService;
	}

	public void reportDiagnosticResult(final SDBSystem rootSystem, final SDBSystem currentSystem,
			final Diagnostic diagnostic, final DiagnosticResult result) {

		boolean shouldAlert = false;
		boolean shouldAlertStability = false;
		boolean shouldAlertPreviousCorrectionAttempt = false;

		if (this.shouldAlertDiagnosticStability(diagnostic, result)) {
			shouldAlert = true;
			shouldAlertStability = true;
		}

		// launch an alert if this result was preceded by a correction attempt
		if (this.universeService.diagnosticAlreadyRan(diagnostic)) {
			shouldAlert = true;
			shouldAlertPreviousCorrectionAttempt = true;
		}

		try {
			this.universeService.storeDiagnosticResult(diagnostic, result);
		} catch (final SystemNotAvailableException e) {
			// We do not store diagnostic result if universe system is not
			// available
		}

		// TODO create report diagnostic

		if (shouldAlert) {
			// TODO promote report to alert
		}

	}

	private boolean shouldAlertDiagnosticStability(final Diagnostic diagnostic, final DiagnosticResult result) {
		boolean shouldAlert = false;

		// launch an alert if stability is 0 <-- the system just changed state !
		final int diagnosticStability = universeService.computeStability(diagnostic, result.getMachineMessage());
		if (diagnosticStability == 0) {
			// => With one exception, if this is the first execution of the
			// universe, we only alert for failed diagnostic
			if (!result.getSuccess()) {
				shouldAlert = true;
			} else {
				try {
					if (this.universeService.getCurrentExecutionNumber() > 1) {
						shouldAlert = true;
					}
				} catch (final SystemNotAvailableException e) {
					// No need to alert if universe system is not available :
					// mail won't be either
					shouldAlert = false;
				}
			}

		}

		return shouldAlert;
	}

	public void reportCorrectionAttempt(final SDBSystem rootSystem, final SDBSystem currentSystem,
			final Diagnostic diagnostic, final CorrectiveAction correctiveAction) {
		// launch an alert for every correction attempt
	}

	public void reportCrash(final SDBSystem rootSystem, final SDBSystem currentSystem, final Exception e) {
		// launch an alert for every crash
	}

	public void reportCrash(final Exception e) {
		// launch an alert for every crash
	}

}
