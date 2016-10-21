package com.somedamnbrain.services.system;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.diagnostic.DiagnosticRun;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.SystemState;
import com.somedamnbrain.exceptions.ExplainableException;
import com.somedamnbrain.exceptions.NoResultException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.report.ReportService;
import com.somedamnbrain.services.universe.SystemStateService;
import com.somedamnbrain.systems.SDBSystem;

public class SystemDiagnosticService {

	private final static int MAX_NUMBER_ATTEMPT = 50;

	private final ReportService reportService;
	private final SystemSelectorService selectorService;
	private final SystemStateService systemStateService;

	@Inject
	public SystemDiagnosticService(final ReportService reportService, final SystemSelectorService selectorService,
			final SystemStateService systemStateService) {
		this.reportService = reportService;
		this.selectorService = selectorService;
		this.systemStateService = systemStateService;
	}

	/**
	 * Run a full diagnostic of a system.
	 * 
	 * @param rootSystem
	 *            system.
	 * @throws UnexplainableException
	 *             if an unexpected sdb failure occured
	 */
	public void diagnosticFullSystem(final SDBSystem rootSystem) throws UnexplainableException {
		boolean skipCorrections = false;

		final Iterator<SDBSystem> iterator = selectorService.computeDependenciesResolution(rootSystem);

		while (iterator.hasNext()) {
			final SDBSystem currentSystem = iterator.next();
			try {
				this.diagnosticSingleSystem(currentSystem, rootSystem, skipCorrections);
			} catch (final UnrecoverableDiagnosticFailureException e) {
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
	 * @param rootSystem
	 *            root system
	 * @param skipCorrections
	 *            true if not correction should be attempted
	 * @throws UnrecoverableDiagnosticFailureException
	 *             if the system encountered an unrecoverable failure
	 * @throws UnexplainableException
	 *             if an unexpected SDB failure occured
	 */
	private void diagnosticSingleSystem(final SDBSystem system, final SDBSystem rootSystem,
			final boolean skipCorrections) throws UnrecoverableDiagnosticFailureException, UnexplainableException {
		final List<DiagnosticRun> unrecoverableFailures = new ArrayList<DiagnosticRun>();

		for (final Diagnostic diagnostic : system.getDiagnostics()) {
			// TODO big question : should we catch Exception when calling
			// attemptDiagnostic and attemptCorrection ?
			final DiagnosticResult result = diagnostic.attemptDiagnostic();
			this.reportService.reportDiagnosticResult(rootSystem, system, diagnostic, result);
			if (!result.getSuccess()) {
				this.manageFailure(rootSystem, system, diagnostic, result, skipCorrections, unrecoverableFailures, 0);
			}
		}

		final SystemState state = this.systemStateService.computeAndStoreSystemState(system);

		if (state.getUp()) {
			system.executeIfOperational();
		}

		reportService.reportSystem(rootSystem, system, state);

		if (!unrecoverableFailures.isEmpty()) {
			throw new UnrecoverableDiagnosticFailureException();
		}
	}

	/**
	 * Manage a failure.
	 * 
	 * @param rootSystem
	 *            root system
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
	private void manageFailure(final SDBSystem rootSystem, final SDBSystem system, final Diagnostic diagnostic,
			final DiagnosticResult result, final boolean skipCorrections,
			final List<DiagnosticRun> unrecoverableFailures, final int nbAttempt) throws UnexplainableException {
		// TODO big question : should we catch Exception when calling
		// attemptDiagnostic and attemptCorrection ?
		try {
			final CorrectiveAction correction = diagnostic.getCorrection(result);
			if (!skipCorrections) {
				this.reportService.reportCorrectionAttempt(rootSystem, system, diagnostic, correction);
				correction.attemptCorrection();
				final DiagnosticResult resultAfterCorrection = diagnostic.attemptDiagnostic();
				this.reportService.reportDiagnosticResult(rootSystem, system, diagnostic, resultAfterCorrection);
				if (!resultAfterCorrection.getSuccess()) {
					if (this.isUnrecoverableFailure(result, resultAfterCorrection, nbAttempt)) {
						unrecoverableFailures.add(new DiagnosticRun(diagnostic, resultAfterCorrection));
					} else {
						this.manageFailure(rootSystem, system, diagnostic, resultAfterCorrection, skipCorrections,
								unrecoverableFailures, nbAttempt + 1);
					}
				}

			}
		} catch (final NoResultException e) {
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
	private boolean isUnrecoverableFailure(final DiagnosticResult resultBeforeCorrection,
			final DiagnosticResult resultAfterCorrection, final int nbAttempt) {
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
