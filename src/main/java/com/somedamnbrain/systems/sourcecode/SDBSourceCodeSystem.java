package com.somedamnbrain.systems.sourcecode;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.DiagnosticStateService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.git.GitSystem;
import com.somedamnbrain.systems.maven.MavenSystem;

public class SDBSourceCodeSystem extends AbstractSystem {

	private final GitSystem gitSystem;

	private final MavenSystem mavenSystem;

	private final DiagnosticStateService diagnosticStateService;

	@Inject
	public SDBSourceCodeSystem(final GitSystem gitSystem, final MavenSystem mavenSystem,
			final DiagnosticStateService diagnosticStateService) {
		this.gitSystem = gitSystem;
		this.mavenSystem = mavenSystem;
		this.diagnosticStateService = diagnosticStateService;
	}

	@Override
	public String getUniqueID() {
		return "SDBSourceCode";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(gitSystem, mavenSystem);
	}

	@Override
	public List<Diagnostic> getDiagnostics() {
		return Diagnostic.notImplemented(this, diagnosticStateService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
