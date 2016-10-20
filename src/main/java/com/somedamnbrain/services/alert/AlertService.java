package com.somedamnbrain.services.alert;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.MailService;
import com.somedamnbrain.services.report.Report;
import com.somedamnbrain.services.universe.UniverseService;

public class AlertService {

	private final UniverseService universeService;
	private final MailService mailService;

	@Inject
	public AlertService(final UniverseService universeService, final MailService mailService) {
		this.universeService = universeService;
		this.mailService = mailService;
	}

	public void promoteReportToAlert(final Report report) throws UnexplainableException {
		this.displayReport(report);

		try {
			this.sendReportByEmail(report);
		} catch (final SystemNotAvailableException e) {
			// If email is not available, then we already did our job :
			// displaying the alert on the console.
		}
	}

	private void sendReportByEmail(final Report report) throws SystemNotAvailableException, UnexplainableException {
		final String subject = "[" + this.universeService.getUniverseName() + "] " + report.getSubject();
		final String content = report.getContent() + "\n\nSomedamnbrain";

		mailService.sendMail(subject, content);

	}

	private void displayReport(final Report report) {
		System.err.println("=======================================");
		System.err.println("ALERT - " + report.getSubject());
		System.err.println(report.getContent());
		System.err.println(StringUtils.EMPTY);
		System.err.println(StringUtils.EMPTY);

	}

}
