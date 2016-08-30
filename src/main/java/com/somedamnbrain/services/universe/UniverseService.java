package com.somedamnbrain.services.universe;

import com.google.inject.Singleton;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.entities.Entities.DiagnosticResult;
import com.somedamnbrain.exceptions.SystemNotAvailableException;

@Singleton
public class UniverseService {

	private boolean configured;

	public void configureUniverse() {
		// TODO
		this.configured = true;
	}

	public void storeDiagnosticResult(Diagnostic diagnostic, DiagnosticResult result)
			throws SystemNotAvailableException {
		if (!this.configured) {
			throw new SystemNotAvailableException();
		}
		throw new RuntimeException("not implemented");
	}

	public void computeStability(Diagnostic diagnostic, DiagnosticResult.Builder diagnosticResultBuilder) {
		if (!this.configured) {
			// If universe is not configured, then everything is new !
			diagnosticResultBuilder.setStability(0);
		} else {
			throw new RuntimeException("not implemented");
		}

	}

	public boolean isConfigured() {
		return configured;
	}

}
