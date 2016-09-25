package com.somedamnbrain.services.report;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.services.alert.AlertService;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.SDBSystem;

public class ReportService {

	private final UniverseService universeService;

	private final AlertService alertService;

	@Inject
	public ReportService(final UniverseService universeService, final AlertService alertService) {
		this.universeService = universeService;
		this.alertService = alertService;
	}

	public void reportDiagnosticResult(final SDBSystem rootSystem, final SDBSystem currentSystem,
			final Diagnostic diagnostic, final DiagnosticResult result) {

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

		try {
			this.universeService.storeDiagnosticResult(diagnostic, result);
		} catch (final SystemNotAvailableException e) {
			// We do not store diagnostic result if universe system is not
			// available
		}

		final Report report = this.computeReport(rootSystem, currentSystem, diagnostic, result, unstable,
				previousCorrectionAttempt);

		if (shouldAlert) {
			this.alertService.promoteReportToAlert(report);
		} else {
			this.displayReportOnConsole(report);
		}

	}

	private void displayReportOnConsole(final Report report) {
		System.out.println("REPORT - " + report.getSubject());
		System.out.println(report.getContent());

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
		content.append("Diagnostic " + diagnostic.getUniqueID() + " is ");
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

		subject.append("Somedamnbrain - ");
		subject.append(rootSystem.getUniqueID());
		subject.append(" - ");
		subject.append(currentSystem.getUniqueID());
		subject.append(" - ");

		return subject;
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

	public void reportCrash(final SDBSystem rootSystem, final SDBSystem currentSystem, final Exception e) {
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

	public void reportCrash(final Exception e) {
		// launch an alert for every crash
		final StringBuilder content = new StringBuilder();
		content.append("A crash occured but I do not know when : ");
		content.append("\r\n");
		content.append("\r\n");
		content.append(e.toString());

		final Report report = new Report("Somedamnbrain - A crash occured !", content.toString());

		this.alertService.promoteReportToAlert(report);
	}

}
