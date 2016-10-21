package com.somedamnbrain.systems.maven;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.DiagnosticStateService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.java.JavaSystem;

public class MavenSystem extends AbstractSystem {

	private final JavaSystem javaSystem;

	private final DiagnosticStateService diagnosticStateService;

	@Inject
	public MavenSystem(final JavaSystem javaSystem, final DiagnosticStateService diagnosticStateService) {
		this.javaSystem = javaSystem;
		this.diagnosticStateService = diagnosticStateService;
	}

	@Override
	public String getUniqueID() {
		return "Maven";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(javaSystem);
	}

	@Override
	public List<Diagnostic> getDiagnostics() {
		return Diagnostic.notImplemented(this, diagnosticStateService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO Auto-generated method stub

	}

}
