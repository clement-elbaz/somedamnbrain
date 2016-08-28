package com.somedamnbrain.diagnostic;

import com.somedamnbrain.entities.Entities.DiagnosticResult;

public class DiagnosticRun {

	private final Diagnostic diagnostic;

	private final DiagnosticResult result;

	public DiagnosticRun(final Diagnostic diagnostic, final DiagnosticResult result) {
		this.diagnostic = diagnostic;
		this.result = result;
	}

	public Diagnostic getDiagnostic() {
		return diagnostic;
	}

	public DiagnosticResult getResult() {
		return result;
	}

}
