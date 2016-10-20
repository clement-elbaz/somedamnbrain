package com.somedamnbrain.services.report;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.entities.Entities.SystemState;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.alert.AlertService;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.universe.LocalUniverseSystem;

public class ReportService {

	private final UniverseService universeService;

	private final AlertService alertService;

	@Inject
	public ReportService(final UniverseService universeService, final AlertService alertService) {
		this.universeService = universeService;
		this.alertService = alertService;
	}

	public void reportDiagnosticResult(final SDBSystem rootSystem, final SDBSystem currentSystem,
			final Diagnostic diagnostic, final DiagnosticResult result) throws UnexplainableException {

		boolean shouldAlert = false;
		boolean unstable = false;
		boolean previousCorrectionAttempt = false;

		if (this.shouldAlertDiagnosticStability(diagnostic, result)) {
			shouldAlert = true;
			unstable = true;
		}

		// launch an alert if this result was preceded by a correction attempt
		if (this.universeService.diagnosticAlreadyRan(diagnostic)) {
			shouldAlert = true;
			previousCorrectionAttempt = true;
		}

		this.universeService.storeDiagnosticResult(diagnostic, result);

		final Report report = this.computeReport(rootSystem, currentSystem, diagnostic, result, unstable,
				previousCorrectionAttempt);

		if (shouldAlert) {
			this.alertService.promoteReportToAlert(report);
		} else {
			this.displayReportOnConsole(report);
		}

	}

	private void displayReportOnConsole(final Report report) {
		System.out.println("=======================================");
		System.out.println("REPORT - " + report.getSubject());
		System.out.println(report.getContent());
		System.out.println(StringUtils.EMPTY);
		System.out.println(StringUtils.EMPTY);

	}

	private Report computeReport(final SDBSystem rootSystem, final SDBSystem currentSystem, final Diagnostic diagnostic,
			final DiagnosticResult result, final boolean unstable, final boolean previousCorrectionAttempt) {
		final StringBuilder subject = this.initiateSubject(rootSystem, currentSystem);
		subject.append(diagnostic.getUniqueID());
		if (result.getSuccess()) {
			subject.append(" is OK");
		} else {
			subject.append(" is KO");
		}

		final StringBuilder content = this.initiateContent(rootSystem, currentSystem);
		content.append("Diagnostic \"" + diagnostic.getUniqueID() + "\" is ");
		if (result.getSuccess()) {
			content.append("OK");
		} else {
			content.append("KO");
		}
		content.append(" with message : " + result.getHumanMessage());

		return new Report(subject.toString(), content.toString());
	}

	private StringBuilder initiateContent(final SDBSystem rootSystem, final SDBSystem currentSystem) {
		final StringBuilder content = new StringBuilder();
		content.append("Root System : " + rootSystem.getUniqueID());
		content.append("\r\n");
		content.append("System : " + currentSystem.getUniqueID());
		content.append("\r\n");
		content.append("\r\n");

		return content;
	}

	private StringBuilder initiateSubject(final SDBSystem rootSystem, final SDBSystem currentSystem) {
		final StringBuilder subject = new StringBuilder();

		subject.append(rootSystem.getUniqueID());
		subject.append(" - ");
		subject.append(currentSystem.getUniqueID());
		subject.append(" - ");

		return subject;
	}

	private boolean shouldAlertDiagnosticStability(final Diagnostic diagnostic, final DiagnosticResult result) {
		boolean shouldAlert = false;

		// launch an alert if stability is 0 <-- the system just changed state !
		if (result.getStability() == 0) {
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
			final Diagnostic diagnostic, final CorrectiveAction correctiveAction) throws UnexplainableException {
		// launch an alert for every correction attempt
		final StringBuilder subject = this.initiateSubject(rootSystem, currentSystem);
		subject.append("A correction will be attempted");

		final StringBuilder content = this.initiateContent(rootSystem, currentSystem);
		content.append("The following correction will be attempted for diagnostic " + diagnostic.getUniqueID() + " : ");
		content.append("\r\n");
		content.append("\r\n");
		content.append(correctiveAction.getUniqueID());

		final Report report = new Report(subject.toString(), content.toString());

		this.alertService.promoteReportToAlert(report);

	}

	public void reportCrash(final SDBSystem rootSystem, final SDBSystem currentSystem, final Exception e)
			throws UnexplainableException {
		// launch an alert for every crash
		final StringBuilder subject = this.initiateSubject(rootSystem, currentSystem);
		subject.append("A crash occured !");

		final StringBuilder content = this.initiateContent(rootSystem, currentSystem);
		content.append("A crash occured during the system diagnostic : ");
		content.append("\r\n");
		content.append("\r\n");
		content.append(e.toString());

		final Report report = new Report(subject.toString(), content.toString());

		this.alertService.promoteReportToAlert(report);
	}

	public void reportCrash(final Exception e) throws UnexplainableException {
		// launch an alert for every crash
		final StringBuilder content = new StringBuilder();
		content.append("A crash occured but I do not know when : ");
		content.append("\r\n");
		content.append("\r\n");
		content.append(e.toString());

		final Report report = new Report("Somedamnbrain - A crash occured !", content.toString());

		this.alertService.promoteReportToAlert(report);
	}

	public void reportSystem(final SDBSystem rootSystem, final SDBSystem currentSystem, final SystemState givenState)
			throws UnexplainableException {
		// Special case when current system is LocalUniverseSystem :
		// because its diagnostics are executed before UniverseService is
		// available, all the stabilities are wrong. In that case we recompute
		// all stabilities for the diagnostics now, as the UniverseService is
		// now available.
		final SystemState state;
		if (currentSystem instanceof LocalUniverseSystem) {
			state = this.universeService.recomputeCompleteSystemStability(currentSystem);
		} else {
			state = givenState;
		}

		final StringBuilder subject = this.initiateSubject(rootSystem, currentSystem);
		subject.append(currentSystem.getUniqueID() + " is ");
		if (state.getUp()) {
			subject.append("UP");
		} else {
			subject.append("DOWN");
		}

		final StringBuilder content = this.initiateContent(rootSystem, currentSystem);

		if (state.getUp()) {
			content.append(currentSystem.getUniqueID() + " is fully up and running since " + state.getStability()
					+ " executions.");
		} else {
			content.append(currentSystem.getUniqueID() + " is down since " + state.getStability() + " executions.");

			content.append("\r\n");
			content.append("\r\n");

			final List<Diagnostic> failedDiagnostics = this.universeService.getFailedDiagnostics(currentSystem);
			if (failedDiagnostics.isEmpty()) {
				content.append("No failed diagnostics");
			} else {
				content.append("The following diagnostics are in failure : ");
				for (final Diagnostic failedDiagnostic : failedDiagnostics) {
					content.append(failedDiagnostic.getUniqueID());
					content.append("\r\n");
				}
			}

			content.append("\r\n");
			content.append("\r\n");

			final List<SDBSystem> failedDependencies = this.universeService.getFailedDependencies(currentSystem);
			if (failedDependencies.isEmpty()) {
				content.append("No failed dependencies");
			} else {
				content.append("The following dependencies are in failure : ");
				for (final SDBSystem dependency : failedDependencies) {
					content.append("\r\n");
					content.append(dependency.getUniqueID());
				}
			}

		}

		final Report report = new Report(subject.toString(), content.toString());

		if (state.getStability() == 0) {
			this.alertService.promoteReportToAlert(report);
		} else {
			this.displayReportOnConsole(report);
		}

	}

	public void reportUnsustainability() throws UnexplainableException {
		alertService.promoteReportToAlert(new Report("Somedamnbrain is not sustainable !",
				"Somedamnbrain is not sustainable and will not be able to self-execute after this execution !"));

	}

	public void reportStability() throws SystemNotAvailableException, UnexplainableException {
		final int globalStability = this.universeService.computeGlobalStability();
		final boolean stable = globalStability != 0;

		final StringBuilder subject = new StringBuilder();
		subject.append("Universe " + this.universeService.getUniverseName() + " is ");
		if (stable) {
			subject.append("stable");
		} else {
			subject.append("unstable");
		}

		final StringBuilder content = new StringBuilder();
		content.append("Universe " + this.universeService.getUniverseName() + " is stable since " + globalStability
				+ " executions");
		content.append("\r\n");
		content.append("\r\n");

		final List<DiagnosticResult> failedDiagnostics = this.universeService.getFailedDiagnostics();

		if (failedDiagnostics.isEmpty()) {
			content.append("No diagnostics failing.");
		} else {
			content.append("The following diagnostics are in failure : ");
			content.append("\r\n");
			for (final DiagnosticResult failedDiagnostic : failedDiagnostics) {
				content.append("\r\n");
				content.append(failedDiagnostic.getDiagnosticId());
			}
		}

		content.append("\r\n");
		content.append("\r\n");

		final List<SystemState> failedSystems = this.universeService.getFailedSystems();
		if (failedSystems.isEmpty()) {
			content.append("No systems failing.");
		} else {
			content.append("The following systems are in failure : ");
			content.append("\r\n");
			for (final SystemState failedSystem : failedSystems) {
				content.append("\r\n");
				content.append(failedSystem.getUniqueId());
			}
		}

		// We want to alert a human about stability at every power of two in
		// order to
		// decrease human notification when situation is stable.
		// @see stackoverflow.com/a/19383296 If this one actually works this is
		// f**king brillant
		final boolean shouldAlert = globalStability == 0 || (globalStability & (globalStability - 1)) == 0;

		if (shouldAlert) {
			content.append("\r\n");
			content.append("\r\n");
			content.append("I will notify you again in " + globalStability + " executions, approximately "
					+ globalStability * 5 + " minutes");
		}

		final Report report = new Report(subject.toString(), content.toString());

		if (shouldAlert) {
			this.alertService.promoteReportToAlert(report);
		} else {
			this.displayReportOnConsole(report);
		}

	}

}
