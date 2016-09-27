package com.somedamnbrain.services.alert;

import org.apache.commons.lang3.StringUtils;

import com.somedamnbrain.services.report.Report;

public class AlertService {

	public void promoteReportToAlert(final Report report) {
		this.displayReport(report);

		// TODO send to mail system
	}

	private void displayReport(final Report report) {
		System.out.println("=======================================");
		System.out.println("ALERT - " + report.getSubject());
		System.out.println(report.getContent());
		System.out.println(StringUtils.EMPTY);
		System.out.println(StringUtils.EMPTY);

	}

}
