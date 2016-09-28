package com.somedamnbrain.services.universe;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.somedamnbrain.exceptions.SystemNotAvailableException;
import com.somedamnbrain.services.report.ReportService;

@Singleton
public class SelfService {
	private final ReportService reportService;

	private boolean sustainable;

	@Inject
	public SelfService(final ReportService reportService) {
		this.sustainable = false;
		this.reportService = reportService;
	}

	public void assertSelfSustainability() {
		this.sustainable = true;
	}

	public void reportSelf() {
		try {
			reportService.reportStability();
		} catch (final SystemNotAvailableException e) {
			// this one is thrown from UniverseService. If Universe is not
			// properly loaded, we are not in a position to care about stability
			// so we let that one go.
		}

		if (!this.sustainable) {
			reportService.reportUnsustainability();
		}

	}

}
