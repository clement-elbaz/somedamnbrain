package com.somedamnbrain.services.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.diagnostic.DiagnosticRun;
import com.somedamnbrain.diagnostic.EnumDiagnosticAttempt;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.ExplainableException;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.alert.AlertService;
import com.somedamnbrain.systems.SDBSystem;

public class SystemDiagnosticService {

	private final static int MAX_NUMBER_ATTEMPT = 50;

	private final AlertService alertService;
	private final SystemSelectorService selectorService;

	@Inject
	public SystemDiagnosticService(final AlertService alertService, final SystemSelectorService selectorService) {
		this.alertService = alertService;
		this.selectorService = selectorService;
	}

	/**
	 * Run a full diagnostic of a system.
	 * 
	 * @param system
	 *            system.
	 * @throws UnexplainableException
	 *             if an unexpected sdb failure occured
	 */
	public void diagnosticFullSystem(SDBSystem system) throws UnexplainableException {
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
	 * @throws UnexplainableException
	 *             if an unexpected SDB failure occured
	 */
	private void diagnosticSingleSystem(SDBSystem system, boolean skipCorrections)
			throws UnrecoverableDiagnosticFailureException, UnexplainableException {
		List<DiagnosticRun> unrecoverableFailures = new ArrayList<DiagnosticRun>();

		for (Diagnostic diagnostic : system.getDiagnostics()) {
			// TODO big question : should we catch Exception when calling
			// attemptDiagnostic and attemptCorrection ?
			DiagnosticResult result = diagnostic.attemptDiagnostic();
			if (!result.getSuccess()) {
				this.manageFailure(system, diagnostic, result, skipCorrections, unrecoverableFailures, 0);
			}
		}

		if (!unrecoverableFailures.isEmpty()) {
			alertService.alertSystem(system, unrecoverableFailures);
			throw new UnrecoverableDiagnosticFailureException();
		} else {
			system.executeIfOperational();
		}
	}

	/**
	 * Manage a failure.
	 * 
	 * @param system
	 *            system
	 * @param diagnostic
	 *            diagnostic
	 * @param result
	 *            diagnostic result
	 * @param skipCorrections
	 *            true if corrections must be skipped
	 * @param unrecoverableFailures
	 *            list of unrecoverable failures for this system
	 * @param nbAttempt
	 *            number of failure management attempt
	 * @throws UnexplainableException
	 *             if something unexpected happens
	 */
	private void manageFailure(final SDBSystem system, final Diagnostic diagnostic, final DiagnosticResult result,
			final boolean skipCorrections, final List<DiagnosticRun> unrecoverableFailures, final int nbAttempt)
			throws UnexplainableException {
		// TODO big question : should we catch Exception when calling
		// attemptDiagnostic and attemptCorrection ?
		try {
			CorrectiveAction correction = diagnostic.getCorrection(result);
			alertService.alertDiagnostic(system, diagnostic, result, skipCorrections
					? EnumDiagnosticAttempt.SKIP_CORRECTIONS : EnumDiagnosticAttempt.CAN_ATTEMPT_CORRECTION);
			if (!skipCorrections) {
				correction.attemptCorrection();
				DiagnosticResult resultAfterCorrection = diagnostic.attemptDiagnostic();
				alertService.alertDiagnostic(system, diagnostic, resultAfterCorrection,
						EnumDiagnosticAttempt.CORRECTION_ATTEMPTED);
				if (!resultAfterCorrection.getSuccess()) {
					if (this.isUnrecoverableFailure(result, resultAfterCorrection, nbAttempt)) {
						unrecoverableFailures.add(new DiagnosticRun(diagnostic, resultAfterCorrection));
					} else {
						this.manageFailure(system, diagnostic, resultAfterCorrection, skipCorrections,
								unrecoverableFailures, nbAttempt + 1);
					}
				}

			}
		} catch (NoResultException e) {
			alertService.alertDiagnostic(system, diagnostic, result, EnumDiagnosticAttempt.CORRECTION_UNAVAILABLE);
			unrecoverableFailures.add(new DiagnosticRun(diagnostic, result));
		}
	}

	/**
	 * Is the result an unrecoverable failure after the correction ?
	 * 
	 * @param resultBeforeCorrection
	 *            result before correction
	 * @param resultAfterCorrection
	 *            result after correction
	 * @param nbAttempt
	 *            current attempt number
	 * @return true if this is an unrecoverable failure
	 */
	private boolean isUnrecoverableFailure(DiagnosticResult resultBeforeCorrection,
			DiagnosticResult resultAfterCorrection, int nbAttempt) {
		// We bound the number of corrective action for a given diagnostic. This
		// is useful to break infinite loops between two type of failures.
		if (nbAttempt >= MAX_NUMBER_ATTEMPT) {
			return true;
		}

		// If the diagnostic result did not change after the correction, there
		// is no point in trying anymore
		if (StringUtils.equals(resultBeforeCorrection.getMachineMessage(), resultAfterCorrection.getMachineMessage())) {
			return true;
		}

		return false;
	}

	static class UnrecoverableDiagnosticFailureException extends ExplainableException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4280080209017599707L;

	}

}
