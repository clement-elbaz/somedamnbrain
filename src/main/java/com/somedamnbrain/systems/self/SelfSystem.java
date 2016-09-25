package com.somedamnbrain.systems.self;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.nextexec.SDBNextExecutionSystem;

public class SelfSystem implements SDBSystem {

	private final SDBNextExecutionSystem nextExecutionSystem;

	@Inject
	public SelfSystem(final SDBNextExecutionSystem nextExecutionSystem) {
		this.nextExecutionSystem = nextExecutionSystem;
	}

	@Override
	public String getUniqueID() {
		return "Self";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(nextExecutionSystem);
	}

	@Override
	public List<Diagnostic> getDiagnostics() {
		return Collections.emptyList();
	}

	@Override
	public void executeIfOperational() throws UnexplainableException {
		// TODO

	}

}
