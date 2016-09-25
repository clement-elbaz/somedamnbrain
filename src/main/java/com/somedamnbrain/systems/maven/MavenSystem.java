package com.somedamnbrain.systems.maven;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.java.JavaSystem;

public class MavenSystem extends AbstractSystem {

	private final JavaSystem javaSystem;

	private final UniverseService universeService;

	@Inject
	public MavenSystem(final JavaSystem javaSystem, final UniverseService universeService) {
		this.javaSystem = javaSystem;
		this.universeService = universeService;
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
		return Diagnostic.notImplemented(this, universeService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO Auto-generated method stub

	}

}
