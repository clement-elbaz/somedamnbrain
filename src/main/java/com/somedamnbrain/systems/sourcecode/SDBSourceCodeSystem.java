package com.somedamnbrain.systems.sourcecode;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.git.GitSystem;
import com.somedamnbrain.systems.maven.MavenSystem;

public class SDBSourceCodeSystem extends AbstractSystem {

	private final GitSystem gitSystem;

	private final MavenSystem mavenSystem;

	private final UniverseService universeService;

	@Inject
	public SDBSourceCodeSystem(final GitSystem gitSystem, final MavenSystem mavenSystem,
			final UniverseService universeService) {
		this.gitSystem = gitSystem;
		this.mavenSystem = mavenSystem;
		this.universeService = universeService;
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
		return Diagnostic.notImplemented(this, universeService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
