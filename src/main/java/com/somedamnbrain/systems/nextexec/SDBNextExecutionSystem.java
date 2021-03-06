package com.somedamnbrain.systems.nextexec;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.services.universe.DiagnosticStateService;
import com.somedamnbrain.systems.AbstractSystem;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.sourcecode.SDBSourceCodeSystem;

public class SDBNextExecutionSystem extends AbstractSystem {

	private final SDBSourceCodeSystem sourceCodeSystem;

	private final DiagnosticStateService diagnosticStateService;

	@Inject
	public SDBNextExecutionSystem(final SDBSourceCodeSystem sourceCodeSystem,
			final DiagnosticStateService diagnosticStateService) {
		this.sourceCodeSystem = sourceCodeSystem;
		this.diagnosticStateService = diagnosticStateService;
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
		return Diagnostic.notImplemented(this, diagnosticStateService);
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
