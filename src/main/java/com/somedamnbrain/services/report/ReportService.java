package com.somedamnbrain.services.report;

import com.somedamnbrain.diagnostic.CorrectiveAction;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;

public class ReportService {

	public void reportDiagnosticResult(final Diagnostic diagnostic, final DiagnosticResult result,
			boolean willTryCorrection) {
		// launch an alert if stability is 0 <-- the system just changed state !
		// launch an alert if a correction will be tried
		// launch an alert if this result was preceded by a correction attempt

	}

	public void reportCorrectionAttempt(final Diagnostic diagnostic, final CorrectiveAction correctiveAction) {
		// launch an alert for every correction attempt
	}

	public void reportCrash(final Exception e) {
		// launch an alert for every crash
	}

}
