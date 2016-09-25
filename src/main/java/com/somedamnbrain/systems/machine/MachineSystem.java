package com.somedamnbrain.systems.machine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.inject.Inject;
import com.somedamnbrain.diagnostic.Diagnostic;
import com.somedamnbrain.exceptions.UnexplainableException;
import com.somedamnbrain.systems.SDBSystem;
import com.somedamnbrain.systems.universe.MergeUniverseSystem;

public class MachineSystem implements SDBSystem {

	private final MergeUniverseSystem mergeUniverseSystem;

	@Inject
	public MachineSystem(final MergeUniverseSystem mergeUniverseSystem) {
		this.mergeUniverseSystem = mergeUniverseSystem;
	}

	@Override
	public String getUniqueID() {
		return "Machine";
	}

	@Override
	public List<SDBSystem> getDependencies() {
		return Arrays.asList(mergeUniverseSystem);
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
