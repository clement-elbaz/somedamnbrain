package com.somedamnbrain.services.alert;

import java.util.List;

import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.diagnostic.DiagnosticRun;
import com.somedamnbrain.diagnostic.EnumDiagnosticAttempt;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.systems.SDBSystem;

public class AlertService {

	/**
	 * Alert the human minion about a diagnostic.
	 * 
	 * @param diagnostic
	 *            the diagnostic that failed
	 * @param diagnosticResult
	 *            the result of the diagnostic
	 * @param finalResult
	 *            true if this is a final result (eg correction has already been
	 *            attempted)
	 */
	public void alertDiagnostic(final SDBSystem system, final Diagnostic diagnostic,
			final DiagnosticResult diagnosticResult, final EnumDiagnosticAttempt attemptType) {
		if (diagnosticResult.getSuccess()) {
			this.alertDiagnosticSuccess(system, diagnostic, diagnosticResult);
		} else {
			switch (attemptType) {
			case CAN_ATTEMPT_CORRECTION:
				this.alertDiagnosticPartialFailure(system, diagnostic, diagnosticResult);
				break;
			case SKIP_CORRECTIONS:
				this.alertDiagnosticFailureWithNoCorrections(system, diagnostic, diagnosticResult);
				break;
			case CORRECTION_UNAVAILABLE:
				this.alertDiagnosticCorrectionUnavailable(system, diagnostic, diagnosticResult);
				break;
			case CORRECTION_ATTEMPTED:
			default:
				this.alertDiagnosticUnrecoverableFailure(system, diagnostic, diagnosticResult);
				break;

			}
		}
	}

	private void alertDiagnosticCorrectionUnavailable(SDBSystem system, Diagnostic diagnostic,
			DiagnosticResult diagnosticResult) {
		String subject = system.getUniqueID() + " - Unrecoverable failure detected";
		StringBuilder content = new StringBuilder();
		content.append("The following diagnostic regarding system " + system.getUniqueID()
				+ " is in an unrecoverable failure : ");
		content.append(System.lineSeparator());
		content.append(diagnostic.getUniqueID());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("The diagnostic contained the following remarks : ");
		content.append(System.lineSeparator());
		content.append(diagnosticResult.getHumanMessage());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("No correction is available for this problem. I can't do it on my own !");

		this.alert(new Alert(subject, content.toString()));

	}

	private void alertDiagnosticFailureWithNoCorrections(SDBSystem system, Diagnostic diagnostic,
			DiagnosticResult diagnosticResult) {
		String subject = system.getUniqueID() + " - Partial failure detected";
		StringBuilder content = new StringBuilder();
		content.append("The following diagnostic regarding system " + system.getUniqueID() + " is in an failure : ");
		content.append(System.lineSeparator());
		content.append(diagnostic.getUniqueID());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("The diagnostic contained the following remarks : ");
		content.append(System.lineSeparator());
		content.append(diagnosticResult.getHumanMessage());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append(
				"I did not attempt any correction because another part of the system is in an unrecoverable failure.");

		this.alert(new Alert(subject, content.toString()));

	}

	private void alertDiagnosticUnrecoverableFailure(SDBSystem system, Diagnostic diagnostic,
			DiagnosticResult diagnosticResult) {
		String subject = system.getUniqueID() + " - Unrecoverable failure detected";
		StringBuilder content = new StringBuilder();
		content.append("The following diagnostic regarding system " + system.getUniqueID()
				+ " is in an unrecoverable failure : ");
		content.append(System.lineSeparator());
		content.append(diagnostic.getUniqueID());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("The diagnostic (after the correction attempt) contained the following remarks : ");
		content.append(System.lineSeparator());
		content.append(diagnosticResult.getHumanMessage());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("No more correction will be attempted. I can't do it on my own !");

		this.alert(new Alert(subject, content.toString()));

	}

	private void alertDiagnosticPartialFailure(SDBSystem system, Diagnostic diagnostic,
			DiagnosticResult diagnosticResult) {
		String subject = system.getUniqueID() + " - Partial failure detected";
		StringBuilder content = new StringBuilder();
		content.append(
				"The following diagnostic regarding system " + system.getUniqueID() + " is in partial failure : ");
		content.append(System.lineSeparator());
		content.append(diagnostic.getUniqueID());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("The diagnostic contained the following remarks : ");
		content.append(System.lineSeparator());
		content.append(diagnosticResult.getHumanMessage());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("A correction WILL be attempted. I'll let you know about the result.");

		this.alert(new Alert(subject, content.toString()));

	}

	private void alertDiagnosticSuccess(final SDBSystem system, final Diagnostic diagnostic,
			final DiagnosticResult diagnosticResult) {
		String subject = system.getUniqueID() + " - Diagnostic OK";
		StringBuilder content = new StringBuilder();
		content.append("The following diagnostic regarding system " + system.getUniqueID() + " is back in success : ");
		content.append(System.lineSeparator());
		content.append(diagnostic.getUniqueID());

		content.append(System.lineSeparator());
		content.append(System.lineSeparator());

		content.append("The successful diagnostic contained the following remarks : ");
		content.append(System.lineSeparator());
		content.append(diagnosticResult.getHumanMessage());

		this.alert(new Alert(subject, content.toString()));

	}

	private void alert(Alert alert) {
		System.out.println("ALERT - " + alert.getSubject());
		System.out.println(alert.getContent());

	}

	public void alertSystem(SDBSystem system, List<DiagnosticRun> unrecoverableFailures) {
		if (!unrecoverableFailures.isEmpty()) {
			String subject = system.getUniqueID() + " is DOWN";
			StringBuilder content = new StringBuilder();

			content.append("The system " + system.getUniqueID()
					+ " is down due to the following diagnostics reporting unrecoverable failures.");

			content.append(System.lineSeparator());
			content.append(System.lineSeparator());

			for (DiagnosticRun failure : unrecoverableFailures) {
				content.append(failure.getDiagnostic().getUniqueID());
				content.append(System.lineSeparator());
			}

			content.append("I can't get the system back up on my own !");

			this.alert(new Alert(subject, content.toString()));
		} else {
			String subject = system.getUniqueID() + " is UP";
			StringBuilder content = new StringBuilder();

			content.append("The system " + system.getUniqueID() + " is up with no unrecoverable failures.");

			this.alert(new Alert(subject, content.toString()));
		}

	}
}
