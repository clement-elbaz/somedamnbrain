package com.somedamnbrain.systems.nextexec;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.UniverseService;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.sourcecode.SDBSourceCodeSystem;

public class SDBNextExecutionSystem implements SDBSystem {

	private final SDBSourceCodeSystem sourceCodeSystem;

	private final UniverseService universeService;

	@Inject
	public SDBNextExecutionSystem(final SDBSourceCodeSystem sourceCodeSystem, final UniverseService universeService) {
		this.sourceCodeSystem = sourceCodeSystem;
		this.universeService = universeService;
	}

	@Override
	public String getUniqueID() {
		return "SDBNextExecution";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(sourceCodeSystem);
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
